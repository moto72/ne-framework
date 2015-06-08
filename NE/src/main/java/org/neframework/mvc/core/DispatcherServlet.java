package org.neframework.mvc.core;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.neframework.mvc.annotation.ResponseBody;
import org.neframework.mvc.util.FormTools;
import org.neframework.mvc.util.WebTools;
import org.neframework.mvc.util.json.JsonTools;
import org.neframework.mvc.web.interceptor.InteceptorInvoke;

public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Logger log = Logger.getLogger("ne framework DispatcherServlet");

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 获取web访问的地址url
		String webUrl = WebTools.getUri(req);
		log.info(webUrl);

		// 封装Controller(action)映射到map
		ControlModel cm = ActionInvoke.getControllModel(webUrl);
		if (cm == null) {
			log.info("end --->" + webUrl);
			resp.sendError(404);
			return;
		}

		Class<?> actionClass = cm.getAction();
		Method method = cm.getMethod();
		Pattern restPattern = cm.getRestPattern();

		try {
			// 初始化action实例
			BaseController action = (BaseController) actionClass.newInstance();
			action.setReq(req);
			action.setResp(resp);

			// 执行前置拦截器
			boolean before = InteceptorInvoke.invokeBefore(action, method, req, resp);
			if (before == false) {
				return;
			}

			// 封装 form bean
			List<Object> params = null;

			if (restPattern != null) {
				params = FormTools.getFormBeanRest(method, webUrl, restPattern, req, resp);
			} else {
				params = FormTools.getFormBean(method, req, resp);
			}

			Object invoke = method.invoke(action, params.toArray());
			if (invoke != null && method.getReturnType() == String.class) {
				action.return_url = invoke.toString();
			}

			// 执行后置拦截器
			boolean after = InteceptorInvoke.invokeAfter(action, method, req, resp);
			if (after == false) {
				return;
			}

			/**
			 * 判断返回值,确定重定向还是转发
			 */
			// ajax 方式
			if (method.isAnnotationPresent(ResponseBody.class)) {
				if (method.getReturnType() == String.class) {
					resp.getWriter().print(action.return_url);
				} else {
					resp.getWriter().print(JsonTools.toJson(action.result));
				}
				resp.getWriter().flush();

				return;
			} else if (action.return_url != null) {

				// URL 方式
				String return_url = action.return_url;

				if (return_url.startsWith("redirect:")) {
					return_url = return_url.substring(return_url.indexOf(":") + 1);

					String end_url = null;
					if (return_url.startsWith("http://")) {
						end_url = return_url;
					} else if (return_url.startsWith("/")) {
						end_url = req.getContextPath() + return_url;
					} else {
						int lastIndexOf = webUrl.lastIndexOf("/");
						end_url = req.getContextPath() + webUrl.substring(0, lastIndexOf) + "/" + return_url;
					}
					resp.sendRedirect(end_url);// 重定向

				} else {
					if (WebTools.isForward(req)) {
						req.getRequestDispatcher(return_url).forward(req, resp);// 转发
					} else {
						// jsp:include 的情况
						req.getRequestDispatcher(return_url).include(req, resp);
					}
				}
			} else {
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}// #doPost
}
