package org.neframework.mvc.web.interceptor;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.neframework.mvc.core.BaseController;
import org.neframework.mvc.core.CoreQueue;
import org.neframework.mvc.util.WebTools;

public class InteceptorInvoke {

	public static boolean invokeBefore(BaseController action, Method method, HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		String webUrl = WebTools.getUri(req);

		for (InterceptorModel interceptor : CoreQueue.interceptors) {
			if (webUrl.startsWith(interceptor.getUrl())) {
				// 属于拦截器范畴才拦截
				List<Interceptor> list = interceptor.getList();
				for (Interceptor in : list) {
					boolean before = in.before(action, method, req, resp);
					if (before == false) {
						return false;
					}
				}

			}

		}// #for

		return true;
	}

	public static boolean invokeAfter(BaseController action, Method method, HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		String webUrl = WebTools.getUri(req);

		for (InterceptorModel interceptor : CoreQueue.interceptors) {
			if (webUrl.startsWith(interceptor.getUrl())) {
				// 属于拦截器范畴才拦截
				List<Interceptor> list = interceptor.getList();
				for (Interceptor in : list) {
					boolean after = in.after(action, method, req, resp);
					if (after == false) {
						return false;
					}
				}

			}

		}// #for

		return true;
	}

}
