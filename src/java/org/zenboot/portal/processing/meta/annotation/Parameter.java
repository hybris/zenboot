package org.zenboot.portal.processing.meta.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation parameter for Scriptlets
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD })
public @interface Parameter
{
	String description() default "";

	String name();

	String defaultValue() default "";

	ParameterType type() default ParameterType.CONSUME;

	boolean visible() default true;
}
