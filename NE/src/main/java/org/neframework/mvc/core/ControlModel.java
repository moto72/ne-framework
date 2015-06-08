package org.neframework.mvc.core;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * action 模型
 * 
 * @author uninf
 * 
 */
public class ControlModel implements Serializable {
	private static final long serialVersionUID = 1L;

	private Class<?> action;
	private Method method;
	private String restRegUrl;
	private Pattern restPattern;

	// ========= get / set ()==============
	public Class<?> getAction() {
		return action;
	}

	public void setAction(Class<?> action) {
		this.action = action;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Pattern getRestPattern() {
		return restPattern;
	}

	public String getRestRegUrl() {
		return restRegUrl;
	}

	public void setRestRegUrl(String restRegUrl) {
		this.restRegUrl = restRegUrl;
		// 预编译 正则
		if (restRegUrl != null) {
			restPattern = Pattern.compile(this.restRegUrl);
		}
	}

}
