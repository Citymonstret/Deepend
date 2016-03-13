#pragma once

#include "JniHelper.h"

enum NativeType {
	NTYPE_BYTE		= 0,
	NTYPE_INT		= 1,
	NTYPE_STRING	= 2
};

class NativeObj
{
private:
	jint *type;
public:
	NativeObj(jint *type, jint *i, jbyte *b, jstring *s, JNIEnv* env);
	~NativeObj();
	jint *getType();
	jobject * toJObject(JNIEnv* env);
	jint *i;
	jbyte *b;
	jstring *s;
	jint *getI()
	{
		return this->i;
	};
	jbyte *getB()
	{
		return this->b;
	};
	jstring *getS()
	{
		return this->s;
	};
};

