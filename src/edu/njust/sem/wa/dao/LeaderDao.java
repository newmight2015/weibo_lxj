package edu.njust.sem.wa.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import edu.njust.sem.wa.domain.Blogger;
import edu.njust.sem.wa.leader.Leader;

public class LeaderDao {
	private String queryImportantBloggerFromWeiboInfo = "SELECT distinct blogger_id,blogger_name,forward_num + comment_num as he "
			+ "FROM weibo_info having he >= 100";
	private String queryImportantBloggerFromWebioForward = "SELECT distinct forwarder_id,weibo_blogger.nike_name "
			+ "FROM weibo_forward ,weibo_blogger where forward_num >= 50 and forwarder_id = weibo_blogger.id";
	private String queryForwardNumByName = "SELECT count(forward_id) "
			+ "FROM weibo_info,weibo_forward where weibo_info.blogger_name = ? "
			+ "and weibo_info.mid = weibo_forward.weibo_id";
	private String queryCommentNumByName = "SELECT count(weibo_comment.comment_id) FROM weibo_info,weibo_comment "
			+ "where weibo_info.blogger_name = ? and weibo_info.mid = weibo_comment.weibo_id";
	private String queryReplyNumByName = "SELECT count(reviewer_id) FROM weibo_blogger, "
			+ "weibo.weibo_comment where nike_name = ? and reviewer_id = weibo_blogger.id";
	private String queryVerifyForwardNumByName = "SELECT count(forward_id) FROM weibo_info,weibo_forward,weibo_blogger "
			+ "where weibo_info.blogger_name =? and weibo_info.mid = weibo_forward.weibo_id and "
			+ "forwarder_id =  weibo_blogger.id and verify > 0";
	private String queryVerifyCommentNumByName = "SELECT count(comment_id) FROM weibo_info,weibo_comment,weibo_blogger "
			+ "where weibo_info.blogger_name =? and "
			+ "weibo_info.mid = weibo_comment.weibo_id and reviewer_id =  weibo_blogger.id and verify > 0";
	private String insertLeader = "insert weibo_leader (blogger_name,fans_num,at_num,weibo_num,follow_num,forward_num,"
			+ "comment_num,reply_num,is_expert,has_verified,media_index,leader_index)values(?,?,?,?,?,?,?,?,?,?,?,?)";
	private JdbcTemplate jdbcTemplate = DBUtil.getJdbcTemplate();
	private BloggerDao bloggerDao = new BloggerDao();
	private Connection conn = DBUtil.getConnection();
	private static LeaderDao leaderDao;

	public static LeaderDao getInstance() {
		if (leaderDao == null) {
			leaderDao = new LeaderDao();
		}
		return leaderDao;
	}

	/**
	 * 通过微博用户昵称来查询该用户发布的微博被加V用户评论的数量
	 * 
	 * @param name
	 * @return
	 */
	public int gerVerifyCommentNumByName(String name) {
		return jdbcTemplate.queryForObject(queryVerifyCommentNumByName,
				Integer.class, name);
	}

	/**
	 * 通过微博用户昵称来查询该用户发布的微博被加V用户转发的数量
	 * 
	 * @param name
	 * @return
	 */
	public int getVerifyForwardNumByName(String name) {
		return jdbcTemplate.queryForObject(queryVerifyForwardNumByName,
				Integer.class, name);
	}

	/**
	 * 通过微博用户昵称来查询该用户参与评论的次数
	 * 
	 * @param name
	 * @return
	 */
	public int getReplayNumByName(String name) {
		return jdbcTemplate.queryForObject(queryReplyNumByName, Integer.class,
				name);
	}

	/**
	 * 通过微博用户昵称来查询该用户所发布微博被评论的次数
	 * 
	 * @param name
	 * @return
	 */
	public int getCommentNumByName(String name) {
		return jdbcTemplate.queryForObject(queryCommentNumByName,
				Integer.class, name);
	}

	/**
	 * 通过微博用户昵称来查询该用户所发布微博被转发的次数
	 * 
	 * @param name
	 * @return
	 */
	public int getForwardNumByName(String name) {
		return jdbcTemplate.queryForObject(queryForwardNumByName,
				Integer.class, name);
	}

	/**
	 * 获取意见领袖候选人（所发布某一条原创微博转发数和评论数加起来大于等于100）
	 * 
	 * @return
	 */
	public Map<String, Leader> getImportantBlogger() {
		final Map<String, Leader> leaders = new HashMap<>();
		jdbcTemplate.query(queryImportantBloggerFromWeiboInfo,
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						do {
							Blogger blogger = new Blogger();
							blogger = bloggerDao.getBloggerByName(rs
									.getString("blogger_name"));
							Leader leader = new Leader(blogger);
							leaders.put(blogger.getNikeName(), leader);
						} while (rs.next());
					}
				});
		// jdbcTemplate.query(queryImportantBloggerFromWebioForward,
		// new RowCallbackHandler() {
		//
		// @Override
		// public void processRow(ResultSet rs) throws SQLException {
		// do {
		// Blogger blogger = new Blogger();
		// blogger = bloggerDao.getBloggerByName(rs
		// .getString(2));
		// Leader leader = new Leader(blogger);
		// leaders.put(blogger.getNikeName(), leader);
		// } while (rs.next());
		// }
		//
		// });
		return leaders;
	}

	public List<String> getExchangeInfo() throws SQLException {
		String sql1 = "SELECT exchange_info FROM weibo_comment where exchange_info <> ''";
		String sql2 = "SELECT exchange_info FROM weibo_forward where exchange_info <> ''";
		String sql3 = "SELECT content FROM weibo_info where content <>''";
		Statement stmt = conn.createStatement();
		List<String> contents = new LinkedList<>();
		ResultSet rs = stmt.executeQuery(sql1);
		while (rs.next()) {
			contents.add(rs.getString("exchange_info"));
		}
		rs = stmt.executeQuery(sql2);
		while (rs.next()) {
			contents.add(rs.getString("exchange_info"));
		}
		rs = stmt.executeQuery(sql3);
		while (rs.next()) {
			contents.add(rs.getString("content"));
		}
		return contents;
	}

	public void insertLeader(Leader leader) {
		jdbcTemplate.update(insertLeader, leader.getBloggerName(),
				leader.getFansNum(), leader.getAtNum(), leader.getWeiboNum(),
				leader.getFollowNum(), leader.getForwardNum(),
				leader.getCommentNum(), leader.getReplyNum(),
				leader.isExpert(), leader.isHasVerified(),
				leader.getMediaIndex(), leader.getLeaderIndex());
	}
}
