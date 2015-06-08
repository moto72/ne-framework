package org.neframework.mvc.core;

import java.util.Set;

public class ActionInvoke {
	/**
	 * 根据 请求 url，获取控制模型
	 * 
	 * @param webUrl
	 * @return
	 */
	public static ControlModel getControllModel(String webUrl) {
		ControlModel cm = null;

		// 搜索普通url
		cm = CoreQueue.controlMap.get(webUrl);

		// 搜索rest
		if (cm == null) {
			Set<String> restUrls = CoreQueue.restControlMap.keySet();
			for (String restUrl : restUrls) {
				if (webUrl.matches(restUrl)) {
					cm = CoreQueue.restControlMap.get(restUrl);
					break;
				}
			}

		}
		return cm;
	}
}
