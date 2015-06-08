package org.neframework.mvc.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.neframework.mvc.annotation.Controller;
import org.neframework.mvc.annotation.RequestMapping;
import org.neframework.mvc.annotation.RequestMappingRest;
import org.neframework.mvc.core.ControlModel;

/**
 * 
 * @ClassName: AnnotationTools
 * @Description: 
 *               读取特定包下面的类,将有Controller标记的action封装,读取action和method上有RequestMapping标记的
 *               封装结构为Map<String, Map<String,
 *               Object>>,key存放客户端访问的webUrl例如:/cp/user/list.htm String:webUrl
 *               Map<String,
 *               Object>:key存放"action"和"method",Object存放对应的action和method对象
 * @author 冯晓东 于 2013-8-14 上午11:51:33
 */
public class AnnotationTools {

	/**
	 * 
	 * 从包package中获取所有的Class
	 * 
	 * @param pack
	 * 
	 * @return
	 */

	public static Map<String, Class<?>> getClasses(String pack) {

		// 定义一个class类的集合
		Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
		// 是否循环迭代
		boolean recursive = true;
		// 获取包的名字 并进行替换
		String packageName = pack;
		String packageDirName = packageName.replace('.', '/');
		// 定义一个枚举的集合 并进行循环来处理这个目录下的things
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			// 循环迭代下去
			while (dirs.hasMoreElements()) {
				// 获取下一个元素
				URL url = dirs.nextElement();
				// 得到协议的名称
				String protocol = url.getProtocol();
				// 如果是以文件的形式保存在服务器上
				if ("file".equals(protocol)) {
					// 获取包的物理路径
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					// 以文件的方式扫描整个包下的文件 并添加到集合中
					findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return classes;
	}

	/**
	 * 
	 * 以文件的形式来获取包下的所有Class
	 * 
	 * @param packageName
	 * 
	 * @param packagePath
	 * 
	 * @param recursive
	 * 
	 * @param classes
	 */

	public static void findAndAddClassesInPackageByFile(String packageName,

	String packagePath, final boolean recursive, Map<String, Class<?>> classes) {
		// 获取此包的目录 建立一个File
		File dir = new File(packagePath);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles(new FileFilter() {
			// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});

		// 循环所有文件
		for (File file : dirfiles) {
			// 如果是目录 则继续扫描
			if (file.isDirectory()) {
				String fileStr = "";
				if (ChkTools.isNotNull(packageName)) {
					fileStr = packageName + "." + file.getName();
				} else {
					fileStr = file.getName();
				}
				findAndAddClassesInPackageByFile(fileStr, file.getAbsolutePath(), recursive, classes);
			} else {
				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					String calzzStr = "";
					if (ChkTools.isNotNull(packageName)) {
						calzzStr = packageName + '.' + className;
					} else {
						calzzStr = className;
					}
					Class<?> loadClass = Thread.currentThread().getContextClassLoader().loadClass(calzzStr);
					classes.put(loadClass.toString(), loadClass);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 
	 * 分析action的class类的map集合,封装访问地址,方法和类
	 * 
	 * @param classes
	 *            class类的map集合
	 */
	public static Map<String, ControlModel> getUrls() {
		Map<String, Class<?>> classes = getClasses("");

		// 定义一个url,class,method的集合
		Map<String, ControlModel> urlMap = new HashMap<String, ControlModel>();
		// 循环class类的集合
		Iterator<?> iterator = classes.keySet().iterator();
		while (iterator.hasNext()) {
			// 取得class
			Class<?> action = classes.get(iterator.next());
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
						// model.setRestUrl(url);

						urlMap.put(url, model);
					}

				}
			}
		}// #while
		return urlMap;
	}

	/**
	 * 
	 * 分析action的class类的map集合,封装访问地址,方法和类
	 * 
	 * @param classes
	 *            class类的map集合
	 */
	public static Map<String, ControlModel> getRestUrls() {
		Map<String, Class<?>> classes = getClasses("");

		// 定义一个url,class,method的集合
		Map<String, ControlModel> urlMap = new HashMap<String, ControlModel>();
		// 循环class类的集合
		Iterator<?> iterator = classes.keySet().iterator();
		while (iterator.hasNext()) {
			// 取得class
			Class<?> action = classes.get(iterator.next());
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
					RequestMappingRest rm2 = method.getAnnotation(RequestMappingRest.class);

					if (rm2 != null) {
						String methodUrl = rm2.value();
						String url = "/" + classUrl + "/" + methodUrl;
						url = url.replaceAll("//", "/");
						url = url.replaceAll("//", "/");

						ControlModel model = new ControlModel();
						model.setAction(action);
						model.setMethod(method);
						model.setRestRegUrl(methodUrl);

						urlMap.put(url, model);
					}

				}
			}
		}// #while
		return urlMap;
	}

	public static void main(String[] args) {
		Map<String, ControlModel> urls = getUrls();

		for (String url : urls.keySet()) {
			ControlModel map = urls.get(url);
			System.err.println(url);
			System.err.println(map.getAction());
		}

	}
}
