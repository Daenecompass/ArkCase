package com.armedia.acm.services.transcribe.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/01/2018
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TranscribeConfigurationProperty
{
    public String key();
    public boolean read() default true;
    public boolean write() default true;
}