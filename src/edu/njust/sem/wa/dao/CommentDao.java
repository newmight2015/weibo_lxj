package edu.njust.sem.wa.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.jdbc.core.JdbcTemplate;

import edu.njust.sem.wa.domain.Comment;

public class CommentDao {
	private Connection conn = DBUtil.getConnection();
	private JdbcTemplate jdbcTemplate = DBUtil.getJdbcTemplate();

	private String insert = "insert into  weibo_comment(comment_id,reviewer_id,weibo_id,content,comment_time,approve_num)"
			+ " values(?,?,?,?,?,?)";
	private String query = "SELECT count(*) FROM weibo_comment where comment_id=?";

	private static CommentDao commentDao = null;

	private CommentDao() {
	};

	public static CommentDao getInstance() {
		if (commentDao == null) {
			commentDao = new CommentDao();
		}
		return commentDao;
	}

	public void insertComment(Comment comment) {
		int count = jdbcTemplate.queryForObject(query, Integer.class,
				comment.getCommentId());
		if (count == 0) {
			jdbcTemplate.update(insert, comment.getCommentId(),
					comment.getReviewerId(), comment.getWeiboId(),
					comment.getContent(), comment.getCommentTime(),
					comment.getApproveNum());
		}
	}

	public void extartExchangeInfo() throws SQLException {
		String sql = "SELECT comment_id,content,exchange_info FROM weibo_comment";
		Statement stmt = conn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = stmt.executeQuery(sql);
		String info = null;
		while (rs.next()) {
			info = rs.getString("content").trim();
			info = info.replace('：', ':');
			info = info.substring(info.indexOf(":")+1);
			if (info == null || info.length() == 0) {
				continue;
			}
			int index = info.indexOf("//@");
			if (index != -1) {
				info = info.substring(0, index);
			}
			rs.updateString("exchange_info", info);
			rs.updateRow();
		}
	}

}
