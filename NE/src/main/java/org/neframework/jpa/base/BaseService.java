package org.neframework.jpa.base;

import org.neframework.jpa.sql.JdbcComponent;
import org.neframework.jpa.sql.JdbcNewComponent;
import org.neframework.jpa.sql.OrmComponent;

public class BaseService {
	// 对象操作组件
	public final OrmComponent oc = new OrmComponent();
	// sql操作组件
	public final JdbcComponent jc = new JdbcComponent();

	public final JdbcNewComponent jcn = new JdbcNewComponent();
	// // mongo 操作
	// public static DB db = null;
	//
	// static {
	// try {
	// MongoClient mc = new MongoClient(Config.MONGO_HOST, Config.MONGO_PORT);
	// db = mc.getDB(Config.MONGO_DB);
	// } catch (UnknownHostException e) {
	// e.printStackTrace();
	// }
	// }

}
