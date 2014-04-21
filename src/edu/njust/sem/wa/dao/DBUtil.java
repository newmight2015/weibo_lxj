package edu.njust.sem.wa.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DBUtil {

	private static ComboPooledDataSource ds = new ComboPooledDataSource();
	private static JdbcTemplate jdbcTemplate;
	private static Connection conn = null;

	public static JdbcTemplate getJdbcTemplate() {
		if (jdbcTemplate == null) {
			return new JdbcTemplate(ds);
		}
		return jdbcTemplate;
	}

	public static Connection getConnection() {
		if (conn == null) {
			try {
				// The newInstance() call is a work around for some
				// broken Java implementations
				Class.forName("com.mysql.jdbc.Driver").newInstance();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				conn = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/weibo?rewriteBatchedStatements=true&useUnicode=true&characterEncoding=utf-8", "root", "890");
			} catch (SQLException ex) {
				// handle any errors
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
			}
		}
		return conn;
	}
	public static void closeConnection() {
		if (conn != null) {
			try {
				conn.close();
				conn = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
