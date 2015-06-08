package org.neframework.jpa.core;

import java.util.List;
import java.util.Map;

import org.neframework.jpa.page.Page;

public interface JdbcPersistence {

	public boolean execute(String sql);

	// 批量修改对象,并且执行一组sql
	public boolean execute(List<String> sqls, List<List<Object>> vals);

	public List<Map<String, Object>> queryForList(String sql);

	public List<Map<String, Object>> queryForList(String sql, Page page);

	public List<Map<String, Object>> queryForList(String sql, Object[] args);

	// 万能分页查询
	public List<Map<String, Object>> queryForListUniversal(String sql, Object[] args, Page page);

	public List<Map<String, Object>> queryForList(String sql, Object[] args, Page page);

	public List<Map<String, Object>> queryForList(String sql, String sql_count, Object[] args, Map<String, String> sort_params, Page page);

	public Map<String, Object> queryForMap(String sql);

	public Map<String, Object> queryForMap(String sql, Object[] args);

	public int update(String sql);

	public int update(String sql, Object[] args);
}
