package com.minecade.deepend.reflection;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created 3/5/2016 for Deepend
 *
 * @author Citymonstret
 */
@UtilityClass
public class AnnotationUtil
{

    public static <T extends Annotation> Collection<AnnotatedMethod<T>> getAnnotatedMethods(@NonNull final Class<T> a, @NonNull final Class<?> c)
    {
        final List<AnnotatedMethod<T>> annotatedMethods = new ArrayList<>();

        Class<?> clazz = c;
        while ( clazz != Object.class )
        {
            final List<Method> allMethods = new ArrayList<>( Arrays.asList( clazz.getDeclaredMethods() ) );
            allMethods.stream().filter( method -> method.isAnnotationPresent( a ) ).forEach( method -> {
                Annotation[] annotations = method.getAnnotations();
                for ( final Annotation aa : annotations )
                {
                    if ( a.isInstance( aa ) )
                    {
                        annotatedMethods.add( new AnnotatedMethod<>( a.cast( aa ), method ) );
                    }
                }
            } );
            clazz = c.getSuperclass();
        }

        return annotatedMethods;
    }

}
