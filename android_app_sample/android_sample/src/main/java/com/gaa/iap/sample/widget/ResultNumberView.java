package com.gaa.iap.sample.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gaa.iap.sample.R;

import java.util.ArrayList;
import java.util.List;

public class ResultNumberView extends ConstraintLayout {

    private RecyclerView mRecyclerView;

    public ResultNumberView(Context context) {
        super(context);
        init();
    }

    public ResultNumberView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        final Context context = getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.view_result, this, true);
        mRecyclerView = view.findViewById(R.id.recycler);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(context));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
//        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    public void setData(List<Integer> luckyNumbers, List<List<Integer>> myNumbers) {
        mRecyclerView.setAdapter(new BallAdapter(luckyNumbers, myNumbers));
    }

    public void clear() {
        if (mRecyclerView.getAdapter() != null) {
            int count = mRecyclerView.getAdapter().getItemCount();
            mRecyclerView.getAdapter().notifyItemRangeRemoved(0, count);
        }
    }

    private static class BallAdapter extends RecyclerView.Adapter<BallViewHolder> {

        private List<Integer> mLuckyNumbers;
        private List<List<Integer>> mMyNumbers;

        private BallAdapter(List<Integer> luckyNumbers, List<List<Integer>> myNumbers) {
            mLuckyNumbers = luckyNumbers;
            mMyNumbers = myNumbers;
        }

        @NonNull
        @Override
        public BallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new BallViewHolder(inflater.inflate(R.layout.view_result_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull BallViewHolder holder, int position) {
            holder.setNumber(mLuckyNumbers, mMyNumbers.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mMyNumbers.size();
        }
    }

    private static final class BallViewHolder extends RecyclerView.ViewHolder {
        private TextView mRankView;
        private ImageView mBonusView;
        private ArrayList<BallView> mBallViews = new ArrayList<>();

        private BallViewHolder(@NonNull View itemView) {
            super(itemView);
            mRankView = itemView.findViewById(R.id.rank);
            mBonusView = itemView.findViewById(R.id.bonus);
            ViewGroup group = (ViewGroup) itemView;
            for (int i = 1; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof BallView) {
                    mBallViews.add((BallView) child);
                }
            }
        }

        void setNumber(List<Integer> luckyNums, List<Integer> myNums, int position) {
            int luckyCount = 0;
            for (int i = 0; i < myNums.size(); i++) {
                boolean isLucy = false;
                int myNum = myNums.get(i);
                for (int j = 0; j < luckyNums.size(); j++) {
                    if (myNum == luckyNums.get(j)) {
                        luckyCount++;
                        isLucy = true;
                        break;
                    }
                }
                mBallViews.get(i).setNumber(myNum, isLucy);
            }

            mRankView.setText(getRankString(luckyCount));
            mBonusView.setVisibility(position >= 5 ? VISIBLE : GONE);
        }

        private String getRankString(int number) {
            switch (number) {
                case 3: return "4th";
                case 4: return "3rd";
                case 5: return "2nd";
                case 6: return "1st";
                default: return " - ";
            }
        }
    }


    private static class DividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable divider;

        private DividerItemDecoration(Context context) {
            final TypedArray styledAttributes = context.obtainStyledAttributes(new int[]{android.R.attr.listDivider});
            divider = styledAttributes.getDrawable(0);
            styledAttributes.recycle();
        }

        @Override
        public void onDraw(@NonNull Canvas canvas, RecyclerView parent, @NonNull RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + divider.getIntrinsicHeight();
                divider.setBounds(left, top, right, bottom);
                divider.draw(canvas);
            }
        }
    }
}
