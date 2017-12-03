package com.minecade.deepend.reflection;

import java.lang.reflect.Method;

/**
 * Created 3/5/2016 for Deepend
 *
 * @author Citymonstret
 */
public class ReflectionMethod<T>
{

    private final Method method;
    private final Object instance;
    private final Class<T> returnType;

    public ReflectionMethod(final Method method, final Object instance, final Class<T> returnType)
    {
        this.method = method;
        this.method.setAccessible( true );
        this.instance = instance;
        this.returnType = returnType;
    }

    public T handle(final Object... in)
    {
        try
        {
            return returnType.cast( this.method.invoke( instance, in ) );
        } catch ( final Exception e )
        {
            e.printStackTrace();
        }
        return null;
    }
}
