package org.neframework.mvc.util;

import javax.servlet.http.HttpServletRequest;

public class WebTools {

	/**
	 * 处理 URL ,防止用户注水
	 * 
	 * @param webUrl
	 * @return
	 */
	private static String handleUrl(String webUrl) {
		webUrl = webUrl.replaceAll("/{1,}", "/");
		return webUrl;
	}

	/**
	 * 获取请求路径，不带 req.getContextPath() 的 uri
	 * 
	 * @param req
	 * @return
	 */
	public static String getUri(HttpServletRequest req) {
		String webUrl = req.getRequestURI();
		webUrl = handleUrl(webUrl);
		int len = req.getContextPath().length();
		if (len > 1) {
			webUrl = webUrl.substring(len);
		}

		if (webUrl.endsWith(".jsp")) {
			Object requestDispatcherPath = req.getAttribute("org.apache.catalina.core.DISPATCHER_REQUEST_PATH");
			if (null != requestDispatcherPath) {
				webUrl = requestDispatcherPath.toString();
			}
		}

		return webUrl;
	}

	public static boolean isForward(HttpServletRequest req) {
		String webUrl = req.getRequestURI();
		return webUrl.endsWith(".jsp") ? false : true;
	}

	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static void main(String[] args) {
		String[] url = new String[] { "//xx///a.htm", "///////xx///a.htm" };
		for (String u : url) {
			System.err.println(handleUrl(u));
		}

	}
}
