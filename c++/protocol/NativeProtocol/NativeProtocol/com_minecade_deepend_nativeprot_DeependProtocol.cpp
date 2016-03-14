#include "stdafx.h"
#include "com_minecade_deepend_nativeprot_DeependProtocol.h"

using std::cout;
using std::endl;

static jclass c_nativeClass;
static jmethodID c_constructorB;
static jmethodID c_constructorI;
static jmethodID c_constructorS;
static jstring emptyString;

jchar* bytesToChars(const jbyte *bytes, const jint len, _int32* offset)
{
	std::vector<jchar> vector(len);
	for (int i = 0; i < len; i++) {
		jbyte b = bytes[*offset + i];
		vector[i] = jchar(b);
	}
	*offset += len;
	return vector.data();
}

jbyte bytesToByte(const jbyte *bytes, _int32* offset)
{
	jbyte result;
	result = bytes[*offset];
	*offset += sizeof(jbyte);
	return result;
}

jint bytesToInt(jbyte *bytes, _int32* offset)
{
	jint result = 0;

	for (int i = 0; i < sizeof(jint); i++)
	{
		int total(i + *offset);
		result = (result << CHAR_BIT) + bytes[total];
	}

	*offset += sizeof(jint);
	return result;
}

JNIEXPORT void JNICALL Java_com_minecade_deepend_nativeprot_DeependProtocol_initialize(JNIEnv *env, jclass)
{
	cout << "[NativeProtocol] Loading com.minecade.deepend.nativeprot.NativeObj" << endl;
	jclass tmpClass = env->FindClass("com/minecade/deepend/nativeprot/NativeObj");
	c_nativeClass = (jclass)env->NewGlobalRef(tmpClass);

	cout << "[NativeProtocol] Loading byte constructor" << endl;
	c_constructorB = env->GetMethodID(c_nativeClass, "<init>", "(B)V");
	cout << "[NativeProtocol] Loading int constructor" << endl;
	c_constructorI = env->GetMethodID(c_nativeClass, "<init>", "(I)V");
	cout << "[NativeProtocol] Loading String constructor" << endl;
	c_constructorS = env->GetMethodID(c_nativeClass, "<init>", "(Ljava/lang/String;)V");
	env->DeleteLocalRef(tmpClass);
	cout << "[NativeProtocol] Loading empty string" << endl;
	emptyString = env->NewString(new jchar, 0);
}

JNIEXPORT void JNICALL Java_com_minecade_deepend_nativeprot_DeependProtocol_destroy(JNIEnv *env, jclass)
{
	env->DeleteGlobalRef(c_nativeClass);
}

JNIEXPORT jobjectArray JNICALL Java_com_minecade_deepend_nativeprot_DeependProtocol_loadNativeBuf
(JNIEnv * env, jclass, jint jArraySize, jbyteArray jBytes)
{
	jbyte* bytes = env->GetByteArrayElements(jBytes, JNI_FALSE);
	int offset = 0;
	jint objSize = bytesToInt(bytes, &offset);
	jobjectArray arr = env->NewObjectArray(objSize, c_nativeClass, nullptr);

	for (int object = 0; object < objSize; object++)
	{
		int requiredBytes = offset + (sizeof(jint) * 2);
		if (jArraySize < requiredBytes) {
			std::cout << "Size was less than expected" << std::endl;
			break;
		}
		jint type = bytesToInt(bytes, &offset);
		jint osize = bytesToInt(bytes, &offset);
		if (type == NativeType::NTYPE_BYTE)
		{
			jbyte byte = bytesToByte(bytes, &offset);
			jvalue values[1];
			values[0].b = byte;
			jobject cObject = env->NewObjectA(c_nativeClass, c_constructorB, values);
			env->SetObjectArrayElement(arr, jsize(object), cObject);
		}
		else if (type == NativeType::NTYPE_INT)
		{
			jint readInt = bytesToInt(bytes, &offset);
			jvalue values[1];
			values[0].i = readInt;
			jobject cObject = env->NewObjectA(c_nativeClass, c_constructorI, values);
			env->SetObjectArrayElement(arr, jsize(object), cObject);
		}
		else if (type == NativeType::NTYPE_STRING)
		{
			jchar* chars = bytesToChars(bytes, osize, &offset);
			jstring str = env->NewString(chars, osize);
			jvalue values[1];
			values[0].l = str;
			jobject cObject = env->NewObjectA(c_nativeClass, c_constructorS, values);
			env->SetObjectArrayElement(arr, jsize(object), cObject);
		}
	}
	env->ReleaseByteArrayElements(jBytes, bytes, 0);
	return (jobjectArray) arr;
}