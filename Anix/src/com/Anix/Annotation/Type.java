package com.Anix.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
/**
 * This annotation will be removed.<br>
 * Don't use.
 */
public @interface Type {
	public String[] values();
}
