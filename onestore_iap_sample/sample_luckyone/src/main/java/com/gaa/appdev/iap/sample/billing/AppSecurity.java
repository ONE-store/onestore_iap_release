package com.gaa.appdev.iap.sample.billing;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

/**
 * Created by 1000147 on 2017. 9. 4..
 *
 * SDK 내부에도 verifyPurchase 부분이 아래의 로직 처럼 동일하게 구현되어 있다 앱에서 사용을 위해서 참고용이다.
 * 앱(클라이언트)에서는 어떠한 보안 로직도 안전할수가 없다. 앱의 모든 부분의 보안이 노출되더라도 로직은 서버를 통해서 구현되어야 안전하며 실제로도 그렇게 구현해야 한다.
 *
 * 서버로 영수증 검증해야 안전하다.
 * Developer Payoad를 발급하고 검증하는 것은 개발자의 선택이고 강제사항은 아니다
 * 1. App에서 App server에 payload발급 요청을 한다 . 그걸 받아서 결제서버 (구글 , 원스토어) 에 인앱 결제 요청을 보낸다
 * 2. 결제가 완료되면 결제 영수증을 포함한 결제 정보가 반환된다  이때 결제 영수증에서 Payload를 추출해 App 서버에 검증을 요청하고 검증 결과를 리턴받는다
 * 3. payload 검증 결과가 올바르면 app 서버에서 결제 서버로 결제 영수증 검증 요청한다.(서버 To 서버)
 * 4. 검증 결과가 올바르면 사용자 계정에 결제 사항에 따른 상품을 지급한다
 *
 * 사용자가 Play Store에서 인앱 결제를 신청한다.
 * 구글은 구매 정보를 DB에다 박아두고 결제 정보와 검증 문자열을 사용자의 디바이스에 돌려준다.
 * 앱에서 구글에서 받은 검증 문자열을 다시 개발사측의 API 서버에 전송한다.
 * API 서버에서 사용자로부터 받은 검증 문자열을 Play Store API에 던진다.
 * 구글은 API 서버로 부터 받은 검증 문자열을 가지고 DB에 박아둔 구매정보의 일부를 개발사 API 서버로 반환한다.
 * 구글로 부터 받은 구매 정보가 정상적이라면 인앱 결제로 구매한 아이템을 개발사측 DB에 적어준다. 그리고 사용자에게 정상적인 결제가 이루어졌다는 응답을 보낸다.
 * 가장 중요한 포인트를 짚자면, 검증을 하는 주체는 사용자의 디바이스가 아니고 개발사측 서버 라는 것이다. 절대 클라이언트를 믿어서는 안된다.
 *
 * - API Key를 어플리케이션 코드 내에 바로 삽입 하지 않고, 복호화가 가능한 암호화 (가령 XOR 연산을 예를 들면) 를 이용 하는데, 어플리케이션 실행 시 암호화 하도록 하고, 암호화에 사용하는 키를 서버 로 부터 획득 하는 구조로 구현 하도록 한다.
 * : 가장 안전한 방법
 * 1 대칭키를 사용해서 키를 암호화한다.
 * 2 안드로이드 리소스 파일에 암호화된 키를 저장한다.
 * 3 암호화된 키를 사용하기 전에 서버에서 대칭키를 받아서 복호화한다.
 * : 비교적 안전한 방법
 * 1. 네이티브 코드에 키를 리턴하는 메서드를 구현한다
 * 2. 네이티브 메서드를 호출해서 키를 받는다.
 * : 안전하지 않은 방법
 * 1. 키를 소스나 리소스 파일에 저장해서 사용
 *
 * 앱을 완벽하게 막기는 불가능하며 서버가 없을 경우에는 Proguard로 난독화 처리와 앱에서 사용하는 키를 비교적 안전하게 관리해서 크래킹을 어렵게 만드는것이 현실적인 방법.
 * 실제로 앱스토어에 올라오는 모든 앱들이 다 크래킹이 가능함. 따라서 서버에 중요 로직을 두는것이 안전함.
 *
 * Developer Payload 는 상품의 구매 요청 시에 개발자가 임의로 지정 할 수 있는 문자열 입니다. 이 Payload 값은 결제 완료 이후에 다시 전달 받게 되며 결제 요청 시의 값과 차이가 있다면 구매 요청에 변조가 있다고 판단 하면 됩니다.
 * Payload 검증을 통해 Freedom 과 같은 변조 된 요청을 차단 할 수 있으며, Payload 의 발급 및 검증 프로세스를  자체 서버를 통해 이루어 지도록 합니다.
 */

public class AppSecurity {
    private static final String TAG = AppSecurity.class.getSimpleName();
    private static final String KEY_FACTORY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA512withRSA";
    private static final String PUBLIC_KEY;

    /*
    앱을 등록시에 서버에 private, public key로 한쌍이 생성되며 앱에서는 퍼블릭키를 통해서 해당 상품의 위변조 여부를 조회하는 전자 서명을 조회를 위한 용도로 사용한다.
    퍼블릭 키를 안전하게 저장하는것이 중요하며 서버에 저장하는것이 좋으며 앱에서는 샘플용으로 하드코딩보다는 native를 통해서 제공하였다.
     */
    static {
        System.loadLibrary("public_keys");
        PUBLIC_KEY = getPublicKey();
    }

    public static native String getPublicKey();

    public static boolean verifyPurchase(String signedData, String signature) {
        Log.d(TAG, "\n========== Security verifyPurchase ==========");
        Log.d(TAG, "BASE64 PUBLICKEY :: " + PUBLIC_KEY);
        Log.d(TAG, "SIGNED DATA :: " + signedData);
        Log.d(TAG, "SIGNATURE :: " + signature);
        Log.d(TAG, "=============================================\n");

        if (TextUtils.isEmpty(signedData) || TextUtils.isEmpty(signature)) {
            return false;
        }
        PublicKey key = generatePublicKey(PUBLIC_KEY);
        return verify(key, signedData, signature);

    }

    public static PublicKey generatePublicKey(String encodedPublicKey) {
        try {
            byte[] decodedKey = Base64.decode(encodedPublicKey, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
            return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException("RSA not available", e);
        } catch (InvalidKeySpecException e) {
            Log.e(TAG, "Invalid key specification.");
            throw new IllegalArgumentException(e);
        }
    }

    public static boolean verify(PublicKey publicKey, String signedData, String signature) {
        try {
            Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
            sig.initVerify(publicKey);
            sig.update(signedData.getBytes());
            if (!sig.verify(Base64.decode(signature, Base64.DEFAULT))) {
                Log.e(TAG, "Signature verification failed.");
                return false;
            }
            return true;
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "NoSuchAlgorithmException.");
        } catch (InvalidKeyException e) {
            Log.e(TAG, "Invalid key specification.");
        } catch (SignatureException e) {
            Log.e(TAG, "SignatureTest exception.");
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Base64 decoding failed.");
        }
        return false;
    }

    public static String generatePayload() {
        char[] payload;
        final char[] specials = {'~', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '+', '-', '{', '}', '|', '\\', '/', '.',
                '.', '=', '[', ']', '?', '<', '>'};
        StringBuilder buffer = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ++ch) {
            buffer.append(ch);
        }
        for (char ch = 'a'; ch <= 'z'; ++ch) {
            buffer.append(ch);
        }
        for (char ch = 'A'; ch <= 'Z'; ++ch) {
            buffer.append(ch);
        }

        for (char ch : specials) {
            buffer.append(ch);
        }

        payload = buffer.toString().toCharArray();

        StringBuilder randomString = new StringBuilder();
        Random random = new Random();

        //length : 20자
        for (int i = 0; i < 20; i++) {
            randomString.append(payload[random.nextInt(payload.length)]);
        }

        return randomString.toString();
    }
}
