package org.neframework.jpa.sql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neframework.jpa.core.OrmPersistence;
import org.neframework.jpa.model.DbModel;
import org.neframework.jpa.util.ChkTools;
import org.neframework.jpa.util.ClassTools;
import org.neframework.jpa.util.MysqlOrmTools;

public class OrmComponent implements OrmPersistence {

	private final JdbcComponent jc = new JdbcComponent();

	@Override
	public void saveObj(DbModel obj) {
		String sql = MysqlOrmTools.getInsSQL(obj.getClass());
		MysqlOrmTools.initDbModel(obj);
		List<Object> vals = MysqlOrmTools.getValuesPar(obj);
		jc.update(sql, vals.toArray());
	}

	@Override
	public void updateObj(DbModel obj) {
		if (obj == null) {
			return;
		}
		String sql = MysqlOrmTools.getUpdateSQL(obj.getClass());
		List<Object> vals = MysqlOrmTools.getUpdateVals(obj);
		jc.update(sql, vals.toArray());

	}

	@Override
	public void delObjById(Object id, Class<?> clazz) {
		String sql = MysqlOrmTools.getDelSQL(clazz);
		jc.update(sql, new Object[] { id });
	}

	@Override
	public void delObj(DbModel obj) {
		if (obj == null) {
			return;
		}
		Field id = ClassTools.getIdField(obj.getClass());
		Object val = ClassTools.getClassVal(id, obj);

		delObjById(val, obj.getClass());
	}

	@Override
	public void delRealObj(DbModel obj) {
		if (obj == null) {
			return;
		}
		Field id = ClassTools.getIdField(obj.getClass());
		Object val = ClassTools.getClassVal(id, obj);

		delRealObjById(val, obj.getClass());
	}

	@Override
	public void delRealObjById(Object id, Class<?> clazz) {
		if (ChkTools.isNull(id)) {
			return;
		}
		String sql = MysqlOrmTools.getDelRealSQL(clazz);
		jc.update(sql, new Object[] { id });
	}

	@Override
	public DbModel findObjById(String _id, Class<?> clazz) {
		if (ChkTools.isNull(_id)) {
			return null;
		}
		String sql = MysqlOrmTools.getSelectSQL(clazz, true);

		Map<String, Object> map = jc.queryForMap(sql, new String[] { _id });

		DbModel model = ClassTools.trunMapToObj(map, clazz);
		return model;
	}

	@Override
	public DbModel findObj(String sql, Class<?> clazz) {
		return findObj(sql, null, clazz);
	}

	@Override
	public DbModel findObj(String sql, Object[] args, Class<?> clazz) {
		Map<String, Object> map = jc.queryForMap(sql, args);

		DbModel model = ClassTools.trunMapToObj(map, clazz);
		return model;
	}

	@Override
	public boolean saveOrUpdateObjs(List<DbModel> objs) {
		// TODO 批量修改对象
		if (ChkTools.isNull(objs)) {
			return true;
		}
		List<String> sqls = new ArrayList<String>();
		List<List<Object>> valList = new ArrayList<List<Object>>();

		for (DbModel obj : objs) {
			String sql = null;
			List<Object> vals = null;

			Field id = ClassTools.getIdField(obj.getClass());
			Object idVal = ClassTools.getClassVal(id, obj);
			if (idVal == null) {
				// 添加
				sql = MysqlOrmTools.getInsSQL(obj.getClass());
				MysqlOrmTools.initDbModel(obj);
				vals = MysqlOrmTools.getValuesPar(obj);

			} else {
				// 修改
				sql = MysqlOrmTools.getUpdateSQL(obj.getClass());
				vals = MysqlOrmTools.getUpdateVals(obj);
			}

			sqls.add(sql);
			valList.add(vals);
		}

		boolean b = jc.execute(sqls, valList);
		return b;

	}
}
