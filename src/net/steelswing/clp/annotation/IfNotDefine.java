/*
 * Ну вы же понимаете, что код здесь только мой?
 * Well, you do understand that the code here is only mine?
 */

package net.steelswing.clp.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * File: Define.java
 * Created on 28.12.2021, 10:36:28
 *
 * @author LWJGL2
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
@Inherited
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface IfNotDefine {

    public String[] value();
    public boolean throwsException() default true;

}
