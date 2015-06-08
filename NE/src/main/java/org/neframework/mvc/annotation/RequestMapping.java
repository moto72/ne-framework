package org.neframework.mvc.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * action的访问地址,类上面的和方法名上面的拼装和web访问的url
 * 
 * @author uninf
 * 
 */

@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RequestMapping {

	public String value();
}
