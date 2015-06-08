package org.neframework.mvc.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 控制器: 代表被注解的类 为 action
 * 
 * @author uninf
 * 
 */

@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Interceptor {

}
