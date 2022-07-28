#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_gaa_appdev_iap_sample_billing_AppSecurity_getPublicKey(JNIEnv *env,
                                                                         jclass clazz) {
    return (*env)->NewStringUTF(env,
                                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCM3xMHKCxyhA57mb3j7jrkqNYyRU4z7iPwovmFZ+s2mxFX2y8IBz6qyuDAM8e7UO6m4wG0GpRQL6mQhSDgPnACMW+rzWrQBwNbX2PSndqc6lGWZHKJJc+OB6Izxr5FrsW49SfwpOLEoivipeOnatNXi6pyxXoim9WJlum7L2R7FwIDAQAB");
}

