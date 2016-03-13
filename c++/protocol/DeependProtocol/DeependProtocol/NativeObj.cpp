#include "NativeObj.h"

NativeObj::NativeObj(jint * type, jint *i, jbyte *b, jstring *s, JNIEnv* env)
{
	this->type = type;

	if (i == nullptr) 
	{
		i = new jint(0);
	}
	if (b == nullptr)
	{
		b = new jbyte(0);
	}
	if (s == nullptr)
	{
		s = new jstring(env->NewString(new jchar[0], 0));
	}

	this->i = i;
	this->b = b;
	this->s = s;
}

NativeObj::~NativeObj()
{
	delete this->type;
	delete this->i;
	delete this->b;
	delete this->s;
}

jint * NativeObj::getType()
{
	return this->type;
}

jobject * NativeObj::toJObject(JNIEnv* env)
{
	// Find the class
	jclass* clazz = new jclass(env->FindClass("com/minecade/deepend/nativeprot/NativeObj"));

	// Find the constructor
	jmethodID* method = new jmethodID(env->GetMethodID(*clazz, "<init>", "(IIBLjava/lang/String;)V"));

	// Create constructor parameters
	jvalue args[4] = {};
	args[0].i = *getType();
	args[1].i = *i;
	args[2].b = *b;
	args[3].l = *s;
	// Return a new instance of the object
	
	return new jobject(env->NewObjectA(*clazz, *method, args));
}