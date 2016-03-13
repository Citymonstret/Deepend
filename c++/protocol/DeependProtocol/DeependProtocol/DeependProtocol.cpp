#include <iostream>
#include "DeependProtocol.h"
#include "JniHelper.h"
#include "NativeObj.h"
#include "NativeBuf.h"

using std::cout;
using std::endl;

JNIEXPORT jobject JNICALL Java_com_minecade_deepend_nativeprot_DeependProtocol_readNativeBuf
(JNIEnv *env, jclass, jint jArraySize, jbyteArray jBytes) {
	std::cout << "Inside of Java_com_minecade_deepend_nativeprot_DeependProtocol_readNativeBuf!" << std::endl;

	jclass cls;
	jmethodID constructor;
	jvalue args[3];
	jobject object;

	NativeBuf *buf = new NativeBuf(env, &jArraySize, &jBytes); // Create a new nativebuf

	// This following part will create a new NativeBuf from the values that
	// we have in our implementation, much good ~ yes
	cls = env->FindClass("com/minecade/deepend/nativeprot/NativeBuf");

	cout << "Found class" << endl;

	constructor = env->GetMethodID(cls, "<init>", "(I;[B;[Lcom/minecade/deepend/nativeprot/NativeObj;)V");

	cout << "Found constructor" << endl;

	args[0].i = *(buf)->getSize(); // int
	cout << "set 1" << endl;

	args[1].l = *buf->getBytes(); // byte[] 
	cout << "set 2" << endl;

	args[2].l = *buf->toJObjectArray(env); // NativeObj[]

	cout << "Found values" << endl;

	// We want to be 100% sure that all objects are de-referenced
	delete buf;

	// Let's create the object
	return env->NewObjectA(cls, constructor, args);
}

JNIEXPORT jobject JNICALL Java_com_minecade_deepend_nativeprot_DeependProtocol_writeNativeBuf
(JNIEnv *, jclass, jint, jobject) {
	return nullptr;
}