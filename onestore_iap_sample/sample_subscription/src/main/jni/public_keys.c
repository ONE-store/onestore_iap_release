#include <jni.h>

JNIEXPORT jstring
Java_com_gaa_appdev_subscription_sample_billing_AppSecurity_getPublicKey(JNIEnv *env,jclass clazz) {
    return (*env)->NewStringUTF(env,
                                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC1nzm7QhCXBtTVRTBfX5JEi7XET+Sg27z4eyo8x+k0MLdP0L3it3RgmTNfYdhQB+lJZA+2/D7hDNys+LKRTq+LnOB1cfQZo0L0xqGpV4w+x5O1/z5VGbqlT0XQYtOdcHxlTaJoXHpo1bniFzbOv4XMvzfnPi42g94QAt7v/erSaQIDAQAB");
}

