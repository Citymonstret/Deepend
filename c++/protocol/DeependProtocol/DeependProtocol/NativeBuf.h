#pragma once

#include "JniHelper.h"
#include "NativeObj.h"

class NativeBuf
{
private:
	jint *byteSize;
	jint* objectSize;
	jbyteArray* bytes;
	NativeObj* objects;
	JNIEnv* env;
public:
	NativeBuf(JNIEnv* env, jint* size, jbyteArray* bytes);
	~NativeBuf();
	NativeObj* *getObjects();
	jint* getByteSize();
	jint* getSize();
	jbyteArray* getBytes();
	jobjectArray* toJObjectArray(JNIEnv* env);
	static jbyte bytesToByte(const jbyte *bytes, _int32* offset = 0);
	static jint bytesToInt(jbyte *bytes, _int32* offset = 0);
	static jchar* bytesToChars(const jbyte *bytes, jint len, _int32* offset = 0);
};