package org.neframework.jpa.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neframework.jpa.annotation.Id;
import org.neframework.jpa.annotation.NotMap;
import org.neframework.jpa.annotation.Table;
import org.neframework.jpa.model.DbModel;

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
			if (field.isAnnotationPresent(NotMap.class)) {
				continue;
			}
			list.add(field.getName());
		}

		return list;
	}

	public static List<Field> getClassFields(Class<?> clazz) {
		List<Field> clazzField = new ArrayList<Field>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(NotMap.class)) {
				continue;
			}
			clazzField.add(field);
		}// #for
		return clazzField;
	}

	/**
	 * 获取 id 字段
	 * 
	 * @param clazz
	 * @return
	 */
	public static Field getIdField(Class<?> clazz) {
		Field id = null;

		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Id.class)) {
				id = field;
				break;
			}
		}// #for

		if (id == null) {// 如果没有主键映射，则默认返回第一个非NotMap的字段
			for (Field field : fields) {
				if (!field.isAnnotationPresent(NotMap.class)) {
					id = field;
					break;
				}
			}// #for
		}

		return id;
	}// #getIdField

	/**
	 * 获取 created 字段
	 * 
	 * @param clazz
	 * @return
	 */
	public static Field getCreatedField(Class<?> clazz) {
		Field createdField = null;

		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.getName().equals("created")) {
				createdField = field;
				break;
			}
		}// #for

		return createdField;
	}// #getIdField

	public static DbModel trunMapToObj(Map<String, Object> map, Class<?> clazz) {
		DbModel obj = null;
		if (map == null || map.isEmpty()) {
			return obj;
		}
		try {
			obj = (DbModel) clazz.newInstance();
			List<Field> fields = ClassTools.getClassFields(clazz);
			for (Field field : fields) {
				Object val = map.get(field.getName());
				ClassTools.setClassVal(field, obj, val);
			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return obj;
	}

	/**
	 * 获取Class对应的table名称，如果和表不匹配可是使用@Table注解来设置
	 * 
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getTableName(Class clazz) {
		if (!clazz.isAnnotationPresent(Table.class)) {
			return clazz.getSimpleName().toString();
		} else if ("".equals(((Table) clazz.getAnnotation(Table.class))
				.TableName().toString())) {
			return clazz.getSimpleName().toString();
		} else
			return ((Table) clazz.getAnnotation(Table.class)).TableName()
					.toString();
	}

}
