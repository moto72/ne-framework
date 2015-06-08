package org.neframework.jpa.util;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neframework.jpa.annotation.Id;
import org.neframework.jpa.annotation.NotMap;
import org.neframework.jpa.model.DbModel;

public class MysqlOrmTools {

	/**
	 * 获取传入变量的属性值列表
	 * 
	 * @param obj
	 * @return
	 */
	public static List<Object> getValuesPar(Object obj) {
		List<Object> list = new ArrayList<Object>();
		List<Field> fields = ClassTools.getClassFields(obj.getClass());
		for (Field field : fields) {
			if (field.isAnnotationPresent(NotMap.class)) {
				continue;
			}

			boolean acc = field.isAccessible();
			field.setAccessible(true);
			try {
				Object value = field.get(obj);
				list.add(value);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				field.setAccessible(acc);
			}
		}// for end
		return list;
	}

	/**
	 * 根据BEAN生成INSERT SQL 语句
	 * 
	 * @param clazz
	 * @return
	 */
	public static String getInsSQL(final Class<?> clazz) {
		StringBuilder sb = new StringBuilder("insert into ");
		sb.append(ClassTools.getTableName(clazz)).append("(");
		List<String> names = ClassTools.getClassProperties(clazz);
		int i = 0;
		for (i = 0; i < names.size() - 1; i++) {
			sb.append('`').append(names.get(i)).append("`,");
		}
		sb.append('`').append(names.get(names.size() - 1)).append('`');
		sb.append(") values (");
		for (i = 0; i < names.size() - 1; i++) {
			sb.append("? , ");
		}
		sb.append("?)");
		return sb.toString();
	}

	/**
	 * 根据BEAN生成UPDATE SQL 语句
	 * 
	 * @param clazz
	 * @return
	 */
	public static String getUpdateSQL(final Class<?> clazz) {
		StringBuilder sb = new StringBuilder("update ");
		sb.append(ClassTools.getTableName(clazz)).append(" set ");
		List<Field> names = ClassTools.getClassFields(clazz);

		Field id = null;
		for (Field name : names) {
			if (name.isAnnotationPresent(Id.class)) {
				id = name;
				continue;
			}
			sb.append('`').append(name.getName() + "`= ?,");
		}
		// 去掉 最后的逗号
		sb.deleteCharAt(sb.length() - 1);
		sb.append(" where ");
		if (id != null) {// 如果类中有主键字段则设置主键为条件
			sb.append(id.getName()).append(" = ?");
		} else {// 否则默认采用第一个字段为主键
			sb.append(ClassTools.getIdField(clazz)).append(" = ?");
		}
		return sb.toString();
	}

	/**
	 * 根据BEAN生成UPDATE SQL 语句
	 * 
	 * @param clazz
	 * @return
	 */
	public static String getDelSQL(final Class<?> clazz) {
		StringBuilder sb = new StringBuilder("update ");
		sb.append(ClassTools.getTableName(clazz)).append(" set `status` = -100 where ");
		Field id = ClassTools.getIdField(clazz);
		sb.append(id.getName()).append(" = ?");
		return sb.toString();
	}

	/**
	 * 根据BEAN生成DEL SQL 语句
	 * 
	 * @param clazz
	 * @return
	 */
	public static String getDelRealSQL(final Class<?> clazz) {
		StringBuilder sb = new StringBuilder("delete from  ");
		sb.append(ClassTools.getTableName(clazz)).append(" where ");
		Field id = ClassTools.getIdField(clazz);
		sb.append(id.getName()).append(" = ?");
		return sb.toString();
	}

	/**
	 * 生成查询语句
	 * 
	 * @param clazz
	 * @return
	 */
	public static String getSelectSQL(final Class<?> clazz, boolean status) {
		StringBuilder sb = new StringBuilder("select * from  ");
		sb.append(ClassTools.getTableName(clazz)).append(" where ");
		Field id = ClassTools.getIdField(clazz);
		sb.append(id.getName()).append(" = ?");
		if (status) {
			sb.append(" and status = 0");
		}
		return sb.toString();
	}

	/*
	 * 获取建表语句
	 */
	public static String getCreateSql(final Class<?> clazz) {
		List<String> cols = ClassTools.getClassProperties(clazz);
		String sql = "create table " + ClassTools.getTableName(clazz).toLowerCase() + "(\n";
		for (String c : cols) {
			sql += "\t" + c + " varchar(20),\n";
		}
		sql = sql.substring(0, sql.length() - 2);
		sql += "\n)";
		return sql;
	}

	/**
	 * 封装Bean
	 * 
	 * @param map
	 * @param obj
	 */
	public static void fillEntity(Map<String, Object> map, Object obj) {
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.getName().equals("serialVersionUID")) {
				continue;
			}
			String name = field.getName();
			if (map.get(name) == null) {
				continue;
			} else {
				boolean acc = field.isAccessible();
				field.setAccessible(true);
				try {
					field.set(obj, map.get(name));
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					field.setAccessible(acc);
				}
			}
		}
	}

	/**
	 * 将bean封装成List
	 * 
	 * @param list
	 * @param clazz
	 * @return
	 */
	public static List<Object> InvokeList(List<Map<String, Object>> list, final Class<?> clazz) {
		List<Object> invokeList = new ArrayList<Object>();
		try {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				Object ob = clazz.getConstructors()[0].newInstance();
				fillEntity(map, ob);
				invokeList.add(ob);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return invokeList;
	}

	/**
	 * 将结果集的数据封装成BEAN,将bean封装成List集合是
	 * 
	 * @return
	 */
	public static List<?> getResultSetList(ResultSet rs, final Class<?> clazz) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			ResultSetMetaData rsmd;
			rsmd = rs.getMetaData();
			// 获取列信息
			int ColumnNumber = rsmd.getColumnCount();
			while (rs.next()) {
				Map<String, Object> hm = new HashMap<String, Object>();
				for (int i = 1; i <= ColumnNumber; i++) {
					hm.put(rsmd.getColumnName(i), rs.getObject(i));
				}
				list.add(hm);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 生成查询总记录数语句
	 * 
	 * @param sql
	 * @return
	 */
	public static String getCntSql(String sql) {
		StringBuilder sizeSql = new StringBuilder("SELECT COUNT(1) ");
		if (sql == null) {
			return null;
		}
		sizeSql.append(sql.substring(sql.toUpperCase().indexOf("FROM")));
		return sizeSql.toString();
	}

	/**
	 * 
	 * @Description:
	 * @param rs
	 * @return
	 * @throws
	 */
	public static List<String> getColumnLables(ResultSet rs) {
		List<String> columnLables = new ArrayList<String>();
		ResultSetMetaData metaData = null;
		try {
			metaData = rs.getMetaData();
			int count = metaData.getColumnCount();

			for (int i = 0; i < count; i++) {
				columnLables.add(metaData.getColumnLabel(i + 1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return columnLables;

	}

	// 初始化 DbModel 数据
	public static void initDbModel(DbModel obj) {
		// 设置id
		Field id = ClassTools.getIdField(obj.getClass());
		Object idVal = ClassTools.getClassVal(id, obj);
		if (ChkTools.isNull(idVal)) {
			// idVal = GenerateTools.getUUID();
			// 用压缩版的 id
			idVal = GenerateTools.getBase58ID();
			ClassTools.setClassVal(id, obj, idVal);
		}
		// 程序 设置时间戳
		Field createdField = ClassTools.getCreatedField(obj.getClass());
		if (null != createdField) {
			Timestamp created = new Timestamp(System.currentTimeMillis());
			ClassTools.setClassVal(createdField, obj, created);
		}
	}

	public static List<Object> getUpdateVals(DbModel obj) {
		List<Object> vals = new ArrayList<Object>();
		List<Field> fields = ClassTools.getClassFields(obj.getClass());
		Field id = null;
		for (Field field : fields) {
			if (field.isAnnotationPresent(Id.class)) {
				id = field;
				continue;
			}
			vals.add(ClassTools.getClassVal(field, obj));
		}
		vals.add(ClassTools.getClassVal(id, obj));
		return vals;
	}
}
