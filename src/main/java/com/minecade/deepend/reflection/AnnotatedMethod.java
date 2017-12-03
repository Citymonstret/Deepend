package com.minecade.deepend.reflection;

import lombok.Value;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created 3/5/2016 for Deepend
 *
 * @author Citymonstret
 */
@Value
public class AnnotatedMethod<T extends Annotation>
{

    T annotation;

    Method method;

}
