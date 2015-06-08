package org.neframework.jpa.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

public final class DataSourceTools {
	private static Properties p = null;
	private static DruidDataSource dataSource = null;

	private DataSourceTools() {

	}

	static {
		init();
	}

	private static void init() {

		p = new Properties();
		InputStream in = DataSourceTools.class.getClassLoader().getResourceAsStream("druid.properties");
		try {
			p.load(in);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(p);
			dataSource.init();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	/**
	 * 
	 * @Description:
	 * @return
	 * @throws
	 */
	public static Connection getConn() {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return conn;
	}

	/**
	 * 关闭数据库连接
	 * 
	 * @param connect
	 */
	public static void close(ResultSet rs, Statement stmt, Connection conn) {
		try {
			if (rs != null) {
				if (!rs.isClosed()) {
					rs.close();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			if (stmt != null) {
				if (!stmt.isClosed()) {
					stmt.close();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			if (conn != null) {
				if (!conn.isClosed()) {
					conn.close();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}// #close

	public static void main(String[] args) {
		System.err.println(getConn());

	}

}
