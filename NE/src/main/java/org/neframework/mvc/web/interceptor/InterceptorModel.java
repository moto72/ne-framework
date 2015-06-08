package org.neframework.mvc.web.interceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * 拦截模型
 * 
 * @author 冯晓东
 * 
 */
public class InterceptorModel {
	// 要拦截的url路径
	private String url;
	// 拦截器list
	private List<Interceptor> list = new ArrayList<Interceptor>();

	// ============get/set()====================
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Interceptor> getList() {
		return list;
	}

	public void setList(List<Interceptor> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return url + "--->" + list;
	}
}
