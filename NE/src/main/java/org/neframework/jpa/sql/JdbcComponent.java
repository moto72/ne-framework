package org.neframework.jpa.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neframework.jpa.core.JdbcPersistence;
import org.neframework.jpa.exception.ServiceException;
import org.neframework.jpa.page.Page;
import org.neframework.jpa.util.ChkTools;
import org.neframework.jpa.util.DataSourceTools;
import org.neframework.jpa.util.MysqlOrmTools;

public class JdbcComponent implements JdbcPersistence {

	/**
	 * 如果第一个结果是 ResultSet 对象，则返回 true；如果第一个结果是更新计数或者没有结果，则返回 false
	 */
	@Override
	public boolean execute(String sql) {
		boolean execute = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DataSourceTools.getConn();
			pstmt = conn.prepareStatement(sql);

			execute = pstmt.execute(sql);

		} catch (SQLException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		} finally {
			DataSourceTools.close(null, pstmt, conn);
		}
		return execute;
	}// # execute

	@Override
	public boolean execute(List<String> sqls, List<List<Object>> valList) {
		// TODO 批量修改对象,并且执行一组sql
		boolean execute = false;

		if (sqls.size() != valList.size()) {
			throw new RuntimeException("sql的数量必须和参数的数量一致.");
		}

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DataSourceTools.getConn();
			conn.setAutoCommit(false);

			for (int i = 0; i < sqls.size(); i++) {
				String sql = sqls.get(i);
				List<Object> val = valList.get(i);

				pstmt = conn.prepareStatement(sql);
				if (ChkTools.isNotNull(val)) {
					int index = 1;
					for (Object param : val) {
						pstmt.setObject(index++, param);
					}
				}
				pstmt.execute();
			}

			execute = true;
		} catch (SQLException e) {
			execute = false;
			e.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} finally {
			DataSourceTools.close(null, pstmt, conn);
		}
		return execute;
	}

	@Override
	public List<Map<String, Object>> queryForList(String sql) {
		return this.queryForList(sql, new Object[] {});
	}

	@Override
	public List<Map<String, Object>> queryForList(String sql, Object[] args) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DataSourceTools.getConn();
			pstmt = conn.prepareStatement(sql);

			if (ChkTools.isNotNull(args)) {
				for (int i = 0; i < args.length; i++) {
					pstmt.setObject(i + 1, args[i]);
				}
			}
			rs = pstmt.executeQuery();
			List<String> labels = MysqlOrmTools.getColumnLables(rs);
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();

				Object val = null;
				for (String label : labels) {
					val = rs.getObject(label);
					map.put(label, val);
				}

				list.add(map);
			}// #while

		} catch (SQLException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		} finally {
			DataSourceTools.close(rs, pstmt, conn);
		}

		return list;
	}// #queryForList

	@Override
	public Map<String, Object> queryForMap(String sql) {
		return queryForMap(sql, null);
	}

	@Override
	public Map<String, Object> queryForMap(String sql, Object[] args) {
		Map<String, Object> map = new HashMap<String, Object>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DataSourceTools.getConn();
			pstmt = conn.prepareStatement(sql);

			if (ChkTools.isNotNull(args)) {
				for (int i = 0; i < args.length; i++) {
					pstmt.setObject(i + 1, args[i]);
				}
			}
			rs = pstmt.executeQuery();
			List<String> labels = MysqlOrmTools.getColumnLables(rs);
			if (rs.next()) {

				Object val = null;
				for (String label : labels) {
					val = rs.getObject(label);
					map.put(label, val);
				}

			}// #while

		} catch (SQLException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		} finally {
			DataSourceTools.close(rs, pstmt, conn);
		}

		return map;
	}

	@Override
	public int update(String sql) {
		return this.update(sql, null);
	}

	@Override
	public int update(String sql, Object[] args) {
		int update = 0;

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DataSourceTools.getConn();

			pstmt = conn.prepareStatement(sql);
			if (ChkTools.isNotNull(args)) {
				for (int i = 0; i < args.length; i++) {
					pstmt.setObject(i + 1, args[i]);
				}
			}

			update = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		} finally {
			DataSourceTools.close(null, pstmt, conn);
		}
		return update;
	}

	/**
	 * 分页查询
	 */
	@Override
	public List<Map<String, Object>> queryForList(String sql, Page page) {
		return queryForList(sql, null, page);
	}

	@Override
	public List<Map<String, Object>> queryForList(String sql, Object[] vals, Page page) {
		Integer position = sql.toLowerCase().indexOf("from ");
		String sql_count = "select count(1) cnt " + sql.substring(position);

		return this.queryForList(sql, sql_count, vals, null, page);
	}

	@Override
	public List<Map<String, Object>> queryForList(String sql, String sql_count, Object[] vals, Map<String, String> sort_params, Page page) {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DataSourceTools.getConn();

			long count = (Long) this.queryForMap(sql_count, vals).get("cnt");

			page.setRowTotal(count);
			page.setPageTotal((int) (page.getRowTotal() - 1) / page.getPageSize() + 1);
			if (page.getPageNum() > page.getPageTotal()) {
				page.setPageNum(page.getPageTotal());
			}
			if (page.getPageNum() <= 0) {
				page.setPageNum(1);
			}

			if (ChkTools.isNotNull(sort_params)) {
				StringBuilder sb = new StringBuilder();
				for (String key : sort_params.keySet()) {
					sb.append(" ").append(key).append(" ").append(sort_params.get(key)).append(",");
				}

				String ob = " order by";
				int orderIndex = sql.toLowerCase().indexOf(ob);
				if (orderIndex == -1) {
					sb.deleteCharAt(sb.length() - 1);
					sql += ob + sb.toString();
				} else {
					sql = new StringBuilder(sql).insert(orderIndex + ob.length(), sb).toString();
				}
			}
			// 分页
			sql += " limit :start,:pageSize";
			sql = sql.replace(":start", page.getPageSize() * (page.getPageNum() - 1) + "");
			sql = sql.replace(":pageSize", page.getPageSize() + "");

			pstmt = conn.prepareStatement(sql);
			if (ChkTools.isNotNull(vals)) {
				for (int i = 0; i < vals.length; i++) {
					pstmt.setObject(i + 1, vals[i]);
				}
			}
			rs = pstmt.executeQuery();

			List<String> lables = MysqlOrmTools.getColumnLables(rs);
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (String lable : lables) {
					map.put(lable, rs.getObject(lable));
				}
				ret.add(map);
			}

			return ret;

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e);
		} finally {
			DataSourceTools.close(rs, pstmt, conn);
		}

	}

	/**
	 * 万能分页查询
	 */
	@Override
	public List<Map<String, Object>> queryForListUniversal(String sql, Object[] vals, Page page) {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DataSourceTools.getConn();

			pstmt = conn.prepareStatement(sql);
			if (ChkTools.isNotNull(vals)) {
				for (int i = 0; i < vals.length; i++) {
					pstmt.setObject(i + 1, vals[i]);
				}
			}
			rs = pstmt.executeQuery();
			rs.last();
			long count = rs.getRow();

			page.setRowTotal(count);
			page.setPageTotal((int) (page.getRowTotal() - 1) / page.getPageSize() + 1);
			if (page.getPageNum() > page.getPageTotal()) {
				page.setPageNum(page.getPageTotal());
			}
			if (page.getPageNum() <= 0) {
				page.setPageNum(1);
			}

			// 分页
			sql += " limit :start,:pageSize";
			sql = sql.replace(":start", page.getPageSize() * (page.getPageNum() - 1) + "");
			sql = sql.replace(":pageSize", page.getPageSize() + "");

			pstmt = conn.prepareStatement(sql);
			if (ChkTools.isNotNull(vals)) {
				for (int i = 0; i < vals.length; i++) {
					pstmt.setObject(i + 1, vals[i]);
				}
			}
			rs = pstmt.executeQuery();

			List<String> lables = MysqlOrmTools.getColumnLables(rs);
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (String lable : lables) {
					map.put(lable, rs.getObject(lable));
				}
				ret.add(map);
			}

			return ret;

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e);
		} finally {
			DataSourceTools.close(rs, pstmt, conn);
		}

	}// #queryForListUniversal

	// 带排序的
	public List<Map<String, Object>> queryForList(String sql, Object[] vals, Map<String, String> sort_params, Page page) {
		// TODO Auto-generated method stub

		Integer position = sql.toLowerCase().indexOf("from ");
		String sql_count = "select count(1) cnt " + sql.substring(position);

		return this.queryForList(sql, sql_count, vals, sort_params, page);

	}

}
