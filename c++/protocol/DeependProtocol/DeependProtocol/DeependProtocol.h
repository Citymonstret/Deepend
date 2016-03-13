#ifndef _Included_com_minecade_deepend_nativeprot_DeependProtocol
#define _Included_com_minecade_deepend_nativeprot_DeependProtocol

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif // _cplusplus

	/*
	* Class:     com_minecade_deepend_nativeprot_DeependProtocol
	* Method:    readNativeBuf
	* Signature: (I[B)Lcom/minecade/deepend/nativeprot/NativeBuf;
	*/
	JNIEXPORT jobject JNICALL Java_com_minecade_deepend_nativeprot_DeependProtocol_readNativeBuf
		(JNIEnv *, jclass, jint, jbyteArray);

	/*
	* Class:     com_minecade_deepend_nativeprot_DeependProtocol
	* Method:    writeNativeBuf
	* Signature: (ILcom/minecade/deepend/nativeprot/NativeObj;)Lcom/minecade/deepend/nativeprot/NativeBuf;
	*/
	JNIEXPORT jobject JNICALL Java_com_minecade_deepend_nativeprot_DeependProtocol_writeNativeBuf
		(JNIEnv *, jclass, jint, jobject);

#ifdef __cplusplus
}
#endif // _cplusplus
#endif //  _Included_com_minecade_deepend_nativeprot_DeependProtocol