package org.neframework.jpa.sql;

import java.util.List;
import java.util.Map;

import org.neframework.jpa.page.Page;
import org.neframework.jpa.sql.param.NeParamList;
import org.neframework.jpa.sql.param.NeParamTools;

public class JdbcNewComponent {

	protected JdbcComponent jc = new JdbcComponent();

	public List<Map<String, Object>> queryForList(String sqlOld, NeParamList params, Map<String, String> sort_params, Page page) {

		String endSql = NeParamTools.handleSql(sqlOld, params);

		// System.err.println(endSql + "-->" + params);
		return jc.queryForList(endSql, params.getParamValues(), sort_params, page);
	}

	public static void main(String[] args) {

	}

}
