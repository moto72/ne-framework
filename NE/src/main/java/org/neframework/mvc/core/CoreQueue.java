package org.neframework.mvc.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neframework.mvc.annotation.Controller;
import org.neframework.mvc.annotation.RequestMapping;
import org.neframework.mvc.annotation.RequestMappingRest;
import org.neframework.mvc.util.AnnotationTools;
import org.neframework.mvc.util.ClassScanTools;
import org.neframework.mvc.web.interceptor.Interceptor;
import org.neframework.mvc.web.interceptor.InterceptorModel;

/**
 * 核心初始化数据缓存队列
 * 
 * @author 冯晓东
 *
 */
public class CoreQueue {

	// 程序的所有类
	public static List<Class<?>> allClasses = new ArrayList<Class<?>>();
	// 拦截器
	public static List<Class<Interceptor>> interceptorList = new ArrayList<Class<Interceptor>>();
	public static List<InterceptorModel> interceptors = new ArrayList<InterceptorModel>();
	// 存放URL 对应的 控制器
	public static Map<String, ControlModel> controlMap = AnnotationTools.getUrls();
	public static Map<String, ControlModel> restControlMap = AnnotationTools.getRestUrls();

	static {
		init();
	}

	private static void init() {
		// TODO 初始化所有程序
		allClasses = ClassScanTools.getClasses("");
		// 加载控制器
		initControlMap();
		// 加载拦截器
		initAllInterceptors();

	}

	/**
	 * 初始化 所有的拦截器
	 */
	public static void initAllInterceptors() {
		for (Class<?> clazz : interceptorList) {
			try {

				boolean b = clazz.isAnnotationPresent(RequestMapping.class);
				if (b) {

					RequestMapping rm = clazz.getAnnotation(RequestMapping.class);
					String interceptor_urls = rm.value();

					String[] interceptor_url_arr = interceptor_urls.split(",");
					for (String interceptor_url : interceptor_url_arr) {
						InterceptorModel model = null;
						for (InterceptorModel im : interceptors) {
							if (im.getUrl().equals(interceptor_url)) {
								model = im;
								break;
							}
						}
						if (model == null) {
							model = new InterceptorModel();
							model.setUrl(interceptor_url);
							interceptors.add(model);
						}

						Interceptor interceptor = (Interceptor) clazz.newInstance();
						model.getList().add(interceptor);
					}
				} else {
					String error = "拦截器::inteceptor应该加上@RequestMapping指定拦截范畴，例如 @RequestMapping(\"/cp\")";
					error = error.replace("::inteceptor", "" + clazz);
					throw new RuntimeException(error);
				}

			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

		}// #for

	}// #initAllInterceptors

	/**
	 * 
	 * 分析action的class类的map集合,封装访问地址,方法和类
	 * 
	 * @param classes
	 *            class类的map集合
	 */
	public static void initControlMap() {
		// 循环class类的集合
		for (Class<?> action : allClasses) {
			// 判断类有无Controller标记,则封装map对象
			if (action.isAnnotationPresent(Controller.class)) {
				// 取得action类的全局RequestMapping标记
				RequestMapping rm1 = action.getAnnotation(RequestMapping.class);
				String classUrl = "";
				if (rm1 != null) {
					classUrl = rm1.value();
				}
				Method[] methods = action.getDeclaredMethods();
				for (Method method : methods) {
					// 取得method的RequestMapping标记
					RequestMapping rm2 = method.getAnnotation(RequestMapping.class);

					if (rm2 != null) {
						String methodUrl = rm2.value();

						String url = "/" + classUrl + "/" + methodUrl;
						url = url.replaceAll("//", "/");
						url = url.replaceAll("//", "/");

						ControlModel model = new ControlModel();
						model.setAction(action);
						model.setMethod(method);
						model.setRestRegUrl(null);

						controlMap.put(url, model);
					}// #if

					RequestMappingRest rm3 = method.getAnnotation(RequestMappingRest.class);

					if (rm3 != null) {
						String methodUrl = rm3.value();
						String url = "/" + classUrl + "/" + methodUrl;
						url = url.replaceAll("//", "/");
						url = url.replaceAll("//", "/");

						ControlModel model = new ControlModel();
						model.setAction(action);
						model.setMethod(method);
						model.setRestRegUrl(methodUrl);

						restControlMap.put(url, model);
					}

				}
			} else if (Interceptor.class.isAssignableFrom(action)) {
				if (action != Interceptor.class) {
					interceptorList.add((Class<Interceptor>) action);
				}
			} else {

			}
		}// #while
	}

	public static void main(String[] args) {
		System.err.println("程序所有类-->" + allClasses);
		System.err.println("所有拦截器类-->" + interceptorList);
		System.err.println("所有拦截器-->" + interceptors);
		System.err.println("所有controlMap-->" + controlMap);
		System.err.println("所有restControlMap-->" + restControlMap);
	}
}
