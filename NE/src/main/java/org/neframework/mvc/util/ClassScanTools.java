package org.neframework.mvc.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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
public class ClassScanTools {

	/**
	 * 
	 * 从包package中获取所有的Class
	 * 
	 * @param pack
	 * 
	 * @return
	 */

	public static List<Class<?>> getClasses(String pack) {

		// 定义一个class类的集合
		List<Class<?>> classes = new ArrayList<Class<?>>();
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

	public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, List<Class<?>> classes) {
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
					classes.add(loadClass);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		// 加载注解下的所有类
		System.err.println(getClasses(""));
	}
}
