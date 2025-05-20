package com.quicktvui.sdk.core;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Create by weipeng on 2021/08/10 18:34
 */

@Target(METHOD)
@Retention(RUNTIME)
public @interface EsStartParam {
    String value() default "";
}