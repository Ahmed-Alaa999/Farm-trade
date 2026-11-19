#include <jni.h>
extern "C" JNIEXPORT jint JNICALL
Java_com_farmtrade_game_Native_status(JNIEnv*, jobject, jlong now, jlong planted,
                                       jlong watered, jlong grow, jlong wilt) {
    long long dry = now - watered;
    long long pct = (now - planted) * 100 / (grow > 0 ? grow : 1);
    if (pct > 100) pct = 100;
    if (pct < 0) pct = 0;
    int s;
    if (dry > wilt) s = 3;
    else if (dry > wilt / 2) s = 2;
    else if (pct >= 100) s = 1;
    else s = 0;
    return s * 1000 + (int)pct;
}