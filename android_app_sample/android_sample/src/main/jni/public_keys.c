#include <jni.h>

JNIEXPORT jstring JNICALL
 Java_com_gaa_iap_sample_billing_AppSecurity_getPublicKey(JNIEnv *env, jobject instance)
 {
   // This is license Key
   return (*env)->NewStringUTF(env, "BASE_64_PUBLIC_KEY");
 }

