package org.neframework.mvc.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FormTools {

	public static List<Object> getFormBeanRest(Method method, String webUrl, Pattern restPattern, HttpServletRequest req, HttpServletResponse resp) {
		List<Object> params = new ArrayList<Object>();

		Matcher matcher = restPattern.matcher(webUrl);
		int match_cnt = matcher.groupCount();
		matcher.find();

		// 取得方法的参数类型
		Class<?>[] paramTypes = method.getParameterTypes();
		// 取得方法的参数名称
		List<String> paramNames = MethodParametersTools.getParamNames(method);

		for (int i = 0; i < paramTypes.length; i++) {
			Class<?> paramType = paramTypes[i];
			String paramName = paramNames.get(i);

			Object param = null;
			Object matchVal = null;
			if (i < match_cnt) {
				matchVal = matcher.group(i + 1);
			}

			try {
				if (ChkTools.isBasicType(paramType)) {
					// 如果参数为基本数据类型
					String reqParamValue = req.getParameter(paramName);
					// 从正则中提取
					if (reqParamValue == null) {
						reqParamValue = (String) matchVal;
					}
					param = ChkTools.getBasicVal(reqParamValue, paramType);
				} else if (paramType == HttpServletRequest.class) {
					// 如果参数为Request,则将其初始化
					param = req;
				} else if (paramType == HttpServletResponse.class) {
					// 如果参数为Response,则将其初始化
					param = resp;
				} else if (paramType == String.class) {
					// 如果为String,则直接赋值
					String reqParamValue = req.getParameter(paramName);
					// 从正则中提取
					if (reqParamValue == null) {
						reqParamValue = (String) matchVal;
					}
					param = reqParamValue;
				} else if (paramType.isArray()) {
					// 如果为数组
					param = checkInitArray(req, paramName, paramType, method);
				} else {
					// 如果是BaseModel实例,则初始化对象并封装
					// if (packName.equals(Config.FORM_BEAN_PACK)) {
					param = checkInitObject(req, paramName, paramType);
					// }
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				params.add(param);
			}
		}
		return params;
	}

	public static List<Object> getFormBean(Method method, HttpServletRequest req, HttpServletResponse resp) {
		List<Object> params = new ArrayList<Object>();

		// 取得方法的参数类型
		Class<?>[] paramTypes = method.getParameterTypes();
		// 取得方法的参数名称
		List<String> paramNames = MethodParametersTools.getParamNames(method);

		for (int i = 0; i < paramTypes.length; i++) {
			Class<?> paramType = paramTypes[i];
			String paramName = paramNames.get(i);

			Object param = null;
			try {
				if (ChkTools.isBasicType(paramType)) {
					// 如果参数为基本数据类型
					String reqParamValue = req.getParameter(paramName);
					param = ChkTools.getBasicVal(reqParamValue, paramType);
				} else if (paramType == HttpServletRequest.class) {
					// 如果参数为Request,则将其初始化
					param = req;
				} else if (paramType == HttpServletResponse.class) {
					// 如果参数为Response,则将其初始化
					param = resp;
				} else if (paramType == String.class) {
					// 如果为String,则直接赋值
					String reqParamValue = req.getParameter(paramName);
					param = reqParamValue;
				} else if (paramType.isArray()) {
					// 如果为数组
					param = checkInitArray(req, paramName, paramType, method);
				} else {
					// 如果是BaseModel实例,则初始化对象并封装
					String packName = paramType.getPackage().getName();
					// if (packName.equals(Config.FORM_BEAN_PACK)) {
					param = checkInitObject(req, paramName, paramType);
					// }
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				params.add(param);
			}
		}
		return params;
	}

	/**
	 * 则初始化对象并封装
	 * 
	 * @param req
	 * @param paramName
	 * @param paramType
	 * @param method
	 * @return
	 */
	private static Object checkInitArray(HttpServletRequest req, String paramName, Class<?> paramType, Method method) {
		Object param = null;
		String[] reqParamValue = req.getParameterValues(paramName);
		Class<?> componentType = paramType.getComponentType();
		if (componentType == String.class) {
			param = reqParamValue;
		} else {
			String text = "方法methodName参数[paramName]定义错误.FormBean映射Array类型只能为String,不支持componentType";
			text = text.replace("methodName", method.getName());
			text = text.replace("paramName", paramName);
			text = text.replace("componentType", componentType.toString());
			throw new RuntimeException(text);
		}
		return param;
	}

	/**
	 * 
	 * @param req
	 * @param paramName
	 * @param paramType
	 * @return
	 * @throws Exception
	 */
	private static Object checkInitObject(HttpServletRequest req, String paramName, Class<?> paramType) throws Exception {
		Object param = paramType.newInstance();
		Enumeration<String> reqParams = req.getParameterNames();
		while (reqParams.hasMoreElements()) {
			String reqParam = reqParams.nextElement();
			if (reqParam.startsWith(paramName + ".")) {
				// 取req的值
				Object val = null;
				String reqVal = req.getParameter(reqParam);
				String fieldName = reqParam.substring(reqParam.indexOf(".") + 1);
				Field field = paramType.getDeclaredField(fieldName);

				Class<?> fieldType = field.getType();
				if (ChkTools.isBasicType(fieldType)) {
					val = ChkTools.getBasicVal(reqVal, fieldType);
				} else if (fieldType == String.class) {
					val = reqVal;
				} else {
					val = null;
				}
				ClassTools.setClassVal(field, param, val);
			}
		}
		return param;
	}// #checkInitObject

}
