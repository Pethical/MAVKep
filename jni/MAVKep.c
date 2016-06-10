#include <jni.h>

char __aeabi_unwind_cpp_pr0[0];

#define method(ret,name) ret name(JNIEnv * env); JNIEXPORT ret JNICALL Java_hu_pethical_mavkep_global_Jni_##name(JNIEnv * env, jobject jObj){ return name(env); }
#define javaString(name) method(name, jstring)
#define retString(val) return (*env)->NewStringUTF(env,val);
#define javaconst(name,val) JNIEXPORT jstring JNICALL Java_hu_pethical_mavkep_global_Jni_Get##name(JNIEnv * env, jobject jObj){ retString(val); }

javaconst(MapUrl, "http://pethical.hu:3333/map.php");
javaconst(BaseUrl, "http://train.pethical.hu:3333/index.php?from=" );
javaconst(LocalTransportUrl, "http://train.pethical.hu:3333/local.php?origin=%s&destination=%s&when=%s");
javaconst(Stations,"http://train.pethical.hu:3333/stations.sql");
