package org.neframework.jpa.sql.param;

import java.util.List;

public class NeParamTools {

	/**
	 * 处理sql语言
	 * 
	 * @param sqlOld
	 * @param params
	 * @return
	 */
	public static String handleSql(String sqlOld, NeParamList params) {
		sqlOld = sqlOld.replaceAll("\\(\\s*[?]\\s*\\)", "[?]");
		StringBuilder sb = new StringBuilder(sqlOld);
		List<NeParam> list = params.getParamList();

		int indexOf = 0;
		for (int index = 0; index < list.size(); index++) {
			NeParam p = list.get(index);
			indexOf = sb.indexOf("?", indexOf);
			if (!p.getIsNull()) {
				indexOf += 1;
				continue;
			}
			int indexOfL = sb.lastIndexOf("(", indexOf);
			int indexOfR = sb.indexOf(")", indexOf);
			sb.insert(indexOfL, "{");
			sb.insert(indexOfR + 2, "}");
			indexOf += 3;
		}// #for

		// System.err.println("--->" + sb);

		// 去除大括号
		String dkhReg = "(\\{[^\\}]*\\})";
		String made = sb.toString().replaceAll(dkhReg + "\\s+(and)", "").replaceAll("(and)\\s+" + dkhReg, "").replaceAll("(where)\\s+" + dkhReg, "").replace("[?]", "(?)");

		return made;
	}

	// 测试函数
	public static void main(String[] args) {
		String sql1 = "select * from t_user t where t.status=0 and (t.uname like ?) and (t.name like ?) and t.user_flag='已认证'";
		NeParamList params1 = new NeParamList();
		params1.add("");
		params1.add("");
		System.err.println(NeParamTools.handleSql(sql1, params1));

		String sql2 = "select * from t_user t where (t.uname like ?) and (t.name = ?) and t.user_flag='已认证'";
		NeParamList params2 = new NeParamList();
		params2.add("");
		params2.add("");
		System.err.println(NeParamTools.handleSql(sql2, params2));

		String sql3 = "select * from t_user t where (t.uname like ?) and (t.name = ?) order by t.created";
		NeParamList params3 = new NeParamList();
		params3.add("");
		params3.add("");
		System.err.println(NeParamTools.handleSql(sql3, params3));

		String sql4 = "select * from t_user t where (t.uname like ?) and t.status=0 and (t.name = ?) order by t.created";
		NeParamList params4 = new NeParamList();
		params4.add("");
		params4.add("");
		System.err.println(NeParamTools.handleSql(sql4, params4));

		String sql5 = "select * from t_user t where t.status=0 and (t.created=to_days( ?)) and (t.name = ?) order by t.created";
		NeParamList params5 = new NeParamList();
		params5.add("1");
		params5.add("");
		System.err.println(NeParamTools.handleSql(sql5, params5));

		String sql6 = "select * from t_user t where t.status=0 and (t.id in (?)) and (t.name = ?)";
		NeParamList params6 = new NeParamList();
		params6.add("'1','2','3'");
		params6.add("1");
		System.err.println(NeParamTools.handleSql(sql6, params6));

		String sql = "select * from t_user t where t.status=0 and (t.uname like ?) and (t.name = ?)";
		NeParamList params = new NeParamList();
		params.add("");
		params.add("1");
		System.err.println(NeParamTools.handleSql(sql, params));
	}
}
