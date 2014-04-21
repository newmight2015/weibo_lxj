package edu.njust.sem.wa.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.jdbc.core.JdbcTemplate;

import edu.njust.sem.wa.domain.Forward;

public class ForwardDao {
	private static ForwardDao forwardDao;
	private Connection conn = DBUtil.getConnection();
	private String sqlInsertForward = "insert into weibo_forward "
			+ "(forward_id,forwarder_id,url,weibo_id,content,publish_time,forward_num,approve_num)"
			+ "values(?,?,?,?,?,?,?,?)";
	private String sqlQueryForwardById = "select count(*) from weibo_forward where forward_id = ?";
	private JdbcTemplate jdbcTemplate = DBUtil.getJdbcTemplate();

	private ForwardDao() {
	};

	public static ForwardDao getInstance() {
		if (forwardDao == null) {
			forwardDao = new ForwardDao();
		}
		return forwardDao;
	}

	public void insertForward(Forward forward) {
		if (!hasForward(forward.getForwardId())) {
			jdbcTemplate.update(sqlInsertForward, forward.getForwardId(),
					forward.getForwarderId(), forward.getUrl(),
					forward.getWeiboId(), forward.getContent(),
					forward.getPublishTime(), forward.getForwardNum(),
					forward.getApproveNum());
		}
	}

	public boolean hasForward(String forwardId) {
		int num = jdbcTemplate.queryForObject(sqlQueryForwardById,
				Integer.class, forwardId);
		if (num == 0) {
			return false;
		}
		return true;
	}

	public void extartExchangeInfo() throws SQLException {
		String sql = "SELECT forward_id,content,exchange_info FROM weibo_forward";
		Statement stmt = conn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = stmt.executeQuery(sql);
		String info = null;
		while (rs.next()) {
			info = rs.getString("content").trim();
			if (info == null || info.length() == 0) {
				continue;
			}
			int index = info.indexOf("//@");
			if (index != -1) {
				info = info.substring(0, index);
			}
			info = info.replace('：', ':');
			rs.updateString("exchange_info", info);
			rs.updateRow();
		}
	}
}
