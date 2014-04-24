package edu.njust.sem.wa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;

import edu.njust.sem.wa.domain.HotWeibo;

public class HotWeiboDao {
	private static HotWeiboDao hotWeiboDao = new HotWeiboDao();
	private JdbcTemplate jdbcTemplate = DBUtil.getJdbcTemplate();
	private Connection conn = DBUtil.getConnection();
	private PreparedStatement ps;
	private String sqlQueryOneHotWeibo = "SELECT weibo_url,weibo_id,has_comment,has_forward ,forward_request_times,"
			+ "comment_request_times,forward_action_data,comment_action_data,curr_forward_page_num,total_forward_page_num,"
			+ "curr_comment_page_num,total_comment_page_num "
			+ " from weibo_hot_weibo where has_comment = 0 or has_forward = 0 limit 1";
	private String insertHotWeibo = "insert weibo_hot_weibo (weibo_url,weibo_id,forward_request_times,comment_request_times,has_comment,has_forward,"
			+ "forward_action_data,comment_action_data,curr_forward_page_num,total_forward_page_num,curr_comment_page_num,total_comment_page_num) "
			+ "values (?,?,?,?,?,?,?,?,?,?,?,?)";
	private String sqlQueryWeiboNum = "SELECT count(*) FROM weibo.weibo_hot_weibo where weibo_url = ?";
	private String sqlUpdateHotWeibo = "update weibo_hot_weibo set has_comment = ? ,has_forward = ?,"
			+ "comment_action_data = ?,forward_action_data = ?,total_forward_page_num = ?,"
			+ "curr_forward_page_num = ?, total_comment_page_num = ?,curr_comment_page_num = ?,"
			+ "forward_request_times = ?,comment_request_times= ? where weibo_url = ?";
	private String sqlDeleteHotWeibo = "delete from weibo_hot_weibo where weibo_url = ?";
	private String sqlDeleteFetchedHotWeibo = "delete FROM weibo_hot_weibo where has_comment = 1 "
			+ "and has_forward = 1 and total_forward_page_num < 10 and total_comment_page_num < 10";

	public static HotWeiboDao getInstance() {
		return hotWeiboDao;
	}

	public HotWeibo getOneHotWeibo() throws SQLException {
		if (ps == null) {
			ps = conn.prepareStatement(sqlQueryOneHotWeibo,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
		}
		ResultSet rs = ps.executeQuery();
		HotWeibo hotWeibo = new HotWeibo();
		if (rs.next()) {
			hotWeibo.setUrl(rs.getString("weibo_url"));
			hotWeibo.setWeiboId(rs.getString("weibo_id"));
			hotWeibo.setHasComment(rs.getBoolean("has_comment"));
			hotWeibo.setHasForward(rs.getBoolean("has_forward"));
			hotWeibo.setForwardRequestTimes(rs.getInt("forward_request_times"));
			hotWeibo.setCommentRequestTimes(rs.getInt("comment_request_times"));
			hotWeibo.setForwardActionData(rs.getString("forward_action_data"));
			hotWeibo.setCommentActionData(rs.getString("comment_action_data"));
			hotWeibo.setCurrCommentPageNum(rs.getInt("curr_comment_page_num"));
			hotWeibo.setCurrForwardPageNum(rs.getInt("curr_forward_page_num"));
			hotWeibo.setTotalCommentPageNum(rs.getInt("total_comment_page_num"));
			hotWeibo.setTotalForwardPageNum(rs.getInt("total_forward_page_num"));
			return hotWeibo;
		}
		return null;
	}

	public boolean hasHotWeibo(HotWeibo hotWeibo) {
		int num = jdbcTemplate.queryForObject(sqlQueryWeiboNum, Integer.class,
				hotWeibo.getUrl());
		if (num == 0) {
			return false;
		}
		return true;
	}

	public void insertHotWeibo(HotWeibo hotWeibo) {
		if (!hasHotWeibo(hotWeibo)) {
			jdbcTemplate.update(insertHotWeibo, hotWeibo.getUrl(),
					hotWeibo.getWeiboId(), hotWeibo.getForwardRequestTimes(),
					hotWeibo.getCommentRequestTimes(), hotWeibo.isHasComment(),
					hotWeibo.isHasForward(), hotWeibo.getForwardActionData(),
					hotWeibo.getCommentActionData(),
					hotWeibo.getCurrForwardPageNum(),
					hotWeibo.getTotalForwardPageNum(),
					hotWeibo.getCurrCommentPageNum(),
					hotWeibo.getTotalCommentPageNum());
		}
	}

	public void updateHotWeibo(HotWeibo hotWeibo) {
		if (hasHotWeibo(hotWeibo)) {
			jdbcTemplate.update(sqlUpdateHotWeibo, hotWeibo.isHasComment(),
					hotWeibo.isHasForward(), hotWeibo.getCommentActionData(),
					hotWeibo.getForwardActionData(),
					hotWeibo.getTotalForwardPageNum(),
					hotWeibo.getCurrForwardPageNum(),
					hotWeibo.getTotalCommentPageNum(),
					hotWeibo.getCurrCommentPageNum(),
					hotWeibo.getForwardRequestTimes(),
					hotWeibo.getCommentRequestTimes(), hotWeibo.getUrl());
			jdbcTemplate.update(sqlDeleteFetchedHotWeibo);
		} else {
			System.err.println("热门微博不存在于数据库中" + hotWeibo);
		}

	}

	public void deleteHotWeibo(HotWeibo hotWeibo) {
		if (hasHotWeibo(hotWeibo)) {
			jdbcTemplate.update(sqlDeleteHotWeibo, hotWeibo.getUrl());
		} else {
			System.err.println("热门微博不存在于数据库中" + hotWeibo);
		}
	}
}
