#include "NativeBuf.h"
#include <limits.h>

#ifndef _IOSTREAM_
#include <iostream>
#endif // !_IOSTREAM_

#ifndef _VECTOR_
#include <vector>
#endif // !_VECTOR_


NativeBuf::NativeBuf(JNIEnv* env, jint * size, jbyteArray * bytes)
{
	this->env = env;
	this->byteSize = size;
	this->bytes = bytes;

	jbyte* b = env->GetByteArrayElements(*bytes, JNI_FALSE);
	
	int offset;
	offset = 0;

	std::cout << "Size of array: " << *size << std::endl;

	jint* objSize = new jint(bytesToInt(b, &offset)); // Number of objects

	std::cout << "ObjSize: " << objSize << std::endl;

	NativeObj *objs[256]{};
		
	for (int o = 0; o < *objSize; o++) // Read all objects
	{
		std::cout << std::endl << ">>> INFO " << std::endl << std::endl;
		std::cout << "> Offset: " << offset << std::endl;
		std::cout << std::endl << ">>> OBJECT " << o << std::endl << std::endl;

		int required = offset + (sizeof(jint) * 2);
		if (*size < required) {
			std::cout << "Size was less than expected (" << *size << " < " << required << ")" << std::endl;
			break;
		}
		
		jint* type = new jint(bytesToInt(b, &offset));	// Read the type	
		
		std::cout << "Type: " << *type << std::endl;
		
		jint osize = bytesToInt(b, &offset); // Read the object size

		std::cout << "Size: " << osize << std::endl;

		if (*type == NativeType::NTYPE_INT)
		{
			jint* i = new jint(bytesToInt(b, &offset));
			std::cout << "Int: " << *i << std::endl;
			NativeObj *ooo = new NativeObj(type, i, nullptr, nullptr, env);
			objs[o] = ooo;
			std::cout << "Set " << o << " to type " << *(ooo->getType()) << std::endl;
		}
		else if (*type == NativeType::NTYPE_BYTE)
		{
			jbyte* byte = new jbyte(bytesToByte(b, &offset));
			std::cout << "Byte: " << int(*byte) << std::endl;
			NativeObj *ooo = new NativeObj(type, nullptr, byte, nullptr, env);
			objs[o] = ooo;
			std::cout << "Set " << o << " to type " << *(ooo->getType()) << std::endl;
		}
		else if (*type == NativeType::NTYPE_STRING)
		{
			std::cout << "attempting to read data " << std::endl;
			jchar* chars = bytesToChars(b, osize, &offset);
			std::cout << " data read " << std::endl;
			jstring* string = new jstring(env->NewString(chars, osize));
			NativeObj *ooo = new NativeObj(type, nullptr, nullptr, string, env);
			objs[o] = ooo;
		}
	}

	std::cout << std::endl << ">>> DEBUG " << std::endl << std::endl;

	for (int i = 0; i < *objSize; i++)
	{
		NativeObj obj = *objs[i];
		jint type = *obj.getType();

		std::cout << "Found object of type " << type << std::endl;
		switch (type)
		{
		case NativeType::NTYPE_BYTE:
		{
			std::cout << "Value: " << int(*obj.getB()) << std::endl;
		} break;
		case NativeType::NTYPE_STRING:
		{
			std::cout << "Value: " << *obj.getS() << std::endl;
		} break;
		case NativeType::NTYPE_INT:
		{
			std::cout << "Value: " << *obj.getI() << std::endl;
		} break;
		default:
		{
			std::cout << "Unknown type :((" << std::endl;
		}
		}
	}

	this->objects = *objs;
	this->objectSize = objSize;
}

NativeBuf::~NativeBuf()
{
	delete this->byteSize;
	delete this->objectSize;
	delete[] this->bytes;
	delete[] this->objects;
}

NativeObj ** NativeBuf::getObjects()
{
	return &this->objects;
}

jint * NativeBuf::getByteSize()
{
	return this->byteSize;
}

jint * NativeBuf::getSize()
{
	return this->objectSize;
}

jbyteArray * NativeBuf::getBytes()
{
	return this->bytes;
}

using std::cout;
using std::endl;

jobjectArray* NativeBuf::toJObjectArray(JNIEnv * env)
{
	jint* size = getSize();
	cout << "Found size: " << *size << endl;
	
	jclass clazz = env->FindClass("com/minecade/deepend/nativeprot/NativeObj"); // Find the class

	cout << "Found class" << endl;

	NativeObj d = this->objects[0];
	cout << "Found d" << endl;

	jobject* dod = d.toJObject(env);
	cout << "Found dod" << endl;

	jobjectArray* arr = new jobjectArray(env->NewObjectArray(*size, clazz, *d.toJObject(env))); // Create new array
	cout << "Created array" << endl;

	for (int i = 1; i < *size; i++)
	{
		NativeObj zz = this->objects[i];
		cout << "zz" << endl;
		jobject* zzo = zz.toJObject(env);
		cout << "zzo" << endl;

		cout << i << "/" << *size << endl;

		env->SetObjectArrayElement(*arr, i, *zzo); // Populate the array
	}

	cout << "Done" << endl;

	return arr;
}

jchar* NativeBuf::bytesToChars(const jbyte *bytes, const jint len, _int32* offset)
{
	std::vector<jchar> vector(len);
	for (int i = 0; i < len; i++) {
		jbyte b = bytes[*offset + i];
		std::cout << b << std::endl;
		vector[i] = jchar(b);
	}
	*offset += len;
	return vector.data();
}

jbyte NativeBuf::bytesToByte(const jbyte *bytes, _int32* offset)
{
	jbyte result;
	result = bytes[*offset];
	*offset += sizeof(jbyte);
	return result;
}

jint NativeBuf::bytesToInt(jbyte *bytes, _int32* offset)
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