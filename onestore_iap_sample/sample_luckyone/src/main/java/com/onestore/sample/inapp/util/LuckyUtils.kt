package com.onestore.sample.inapp.util

import android.util.Log
import java.util.*

object LuckyUtils {
    private val TAG = LuckyUtils::class.java.simpleName

    fun getSuggestNumbers(maxNum: Int, luckyBall: List<Int>): MutableList<List<Int>> {

        val suggestedNumbersList: MutableList<List<Int>> = ArrayList(maxNum)
        val lineNum = if (luckyBall.isNotEmpty()) Random().nextInt(maxNum) else -1

        for (i in 0 until maxNum) {
            val myBalls = if (lineNum == i) {
                decidedlyRandomNumber(luckyBall)
            } else {
                createRandomNumberList()
            }

            suggestedNumbersList.add(myBalls)
        }
        return suggestedNumbersList
    }

    val luckyNumbers: List<Int>
        get() = createRandomNumberList()

    private fun createRandomNumberList(): List<Int> {
        val numberList: MutableList<Int> = ArrayList()
        val rand = Random()

        while (numberList.size < 6) {
            val number = rand.nextInt(45) + 1

            //중복 발생
            if (numberList.contains(number)) {
                continue
            }

            numberList.add(number)
        }

        numberList.sortWith(Comparator { obj: Int, anotherInteger: Int? -> obj.compareTo(anotherInteger!!) })

        Log.d(TAG, "createRandomNumberList numberList $numberList")

        return numberList
    }

    private fun decidedlyRandomNumber(luckyBall: List<Int>): List<Int> {
        val numberList: MutableList<Int> = ArrayList()
        val size = Random().nextInt(4)

        if (size != 0) {
            val removes = arrayOfNulls<Int>(size)

            Arrays.fill(removes, -1)

            for (i in removes.indices) {
                val index = Random().nextInt(6)
                if (!removes.contains(index)) {
                    removes[i] = index
                }
            }

            for (i in luckyBall.indices) {
                if (!removes.contains(i)) {
                    numberList.add(luckyBall[i])
                }
            }

            while (numberList.size < 6) {
                val number = Random().nextInt(45) + 1
                //중복 발생
                if (numberList.contains(number)) continue
                numberList.add(number)
            }

            numberList.sortWith(Comparator { obj: Int, anotherInteger: Int ->
                obj.compareTo(anotherInteger)
            })
            return numberList
        }
        return luckyBall
    }

    fun getWonCoin(luckyBall: List<Int>, myBallList: List<List<Int>>): Int {
        var totalCoin = 0
        for (myBalls in myBallList) {
            var lineCount = 0
            for (lucky in luckyBall) {
                for (my in myBalls) {
                    if (lucky == my) {
                        lineCount++
                    }
                }
            }
            totalCoin += getLuckyCoin(lineCount)
        }
        return totalCoin
    }

    private fun getLuckyCoin(number: Int): Int {
        return when (number) {
            3 -> 5
            4 -> 30
            5 -> 100
            6 -> 300
            else -> 0
        }
    }
}