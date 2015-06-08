package org.neframework.mvc.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ClassTools {
	/**
	 * 设置BEAN属性值
	 * 
	 * @param field
	 * @param obj
	 * @param val
	 */
	public static void setClassVal(Field field, Object obj, Object val) {
		boolean acc = field.isAccessible();
		field.setAccessible(true);
		try {
			field.set(obj, val);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			field.setAccessible(acc);
		}
	}

	/**
	 * 获取属性值
	 * 
	 * @param field
	 * @param obj
	 * @return
	 */
	public static Object getClassVal(Field field, Object obj) {
		boolean acc = field.isAccessible();
		field.setAccessible(true);
		try {
			return field.get(obj);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			field.setAccessible(acc);
		}

	}

	/**
	 * 获取Class的属性名称
	 * 
	 * @param clazz
	 * @return
	 */
	public static List<String> getClassProperties(Class<?> clazz) {
		List<String> list = new ArrayList<String>();
		List<Field> fields = getClassFields(clazz);
		for (Field field : fields) {
			list.add(field.getName());
		}

		return list;
	}

	public static List<Field> getClassFields(Class<?> clazz) {
		List<Field> clazzField = new ArrayList<Field>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			clazzField.add(field);
		}// #for
		return clazzField;
	}


}
