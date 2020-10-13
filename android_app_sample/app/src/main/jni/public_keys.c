#include <jni.h>

JNIEXPORT jstring JNICALL
 Java_com_gaa_iap_sample_billing_AppSecurity_getPublicKey(JNIEnv *env, jobject instance)
 {
// return (*env)->NewStringUTF(env, "BASE_64_PUBLIC_KEY");
 return (*env)->NewStringUTF(env, "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCc1xrcsPObrUvQDi39zrOFm3fcgPqmwHdRnd12EkSfdxt5oHxA09yNlRakxbR32iLzE7ilgZ0c0t0tb5eK06DLwA5OX/DjgXsmB+qKh2EaB1QWPO9TTVAqM7hzEI+/YIq8tV4ShcHATWO6o5E99del/hBvj3BOdgpvmaTvwO5rmQIDAQAB");
}

