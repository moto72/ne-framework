package org.neframework.mvc.web.interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.neframework.mvc.core.BaseController;

public interface Interceptor {

	public boolean before(BaseController action, Method method, HttpServletRequest request, HttpServletResponse response);

	public boolean after(BaseController action, Method method, HttpServletRequest request, HttpServletResponse response);

}
