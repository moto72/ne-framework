package org.neframework.jpa.core;

import java.util.List;

import org.neframework.jpa.model.DbModel;

public interface OrmPersistence {

	public void saveObj(DbModel obj);

	public void updateObj(DbModel obj);

	public void delObj(DbModel obj);

	public void delObjById(Object id, Class<?> clazz);

	public void delRealObj(DbModel obj);

	public void delRealObjById(Object id, Class<?> clazz);

	public DbModel findObjById(String _id, Class<?> clazz);

	public DbModel findObj(String sql, Class<?> clazz);

	public DbModel findObj(String sql, Object[] args, Class<?> clazz);

	// 批量修改对象
	public boolean saveOrUpdateObjs(List<DbModel> objs);

}
