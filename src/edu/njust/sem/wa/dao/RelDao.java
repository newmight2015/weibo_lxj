package edu.njust.sem.wa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import edu.njust.sem.wa.rel.RelEntry;
import edu.njust.sem.wa.util.ParseUtil;

public class RelDao {
	private String queryForwardRel = "SELECT forward_blogger,origin_blogger, times FROM "
			+ "weibo_forward_rel where forward_blogger = ? and origin_blogger = ?";
	private String queryCommentRel = "SELECT reviewer_blogger,origin_blogger,times FROM "
			+ "weibo_comment_rel where reviewer_blogger = ? and origin_blogger=?";
	private String queryAtRel = "SELECT at_blogger,origin_blogger ,times FROM "
			+ "weibo_at_rel where at_blogger = ? and origin_blogger = ?";
	private String queryAllRel = "SELECT action_blogger,origin_blogger ,times FROM "
			+ "weibo.weibo_all_rel where action_blogger = ? and origin_blogger = ?";

	private JdbcTemplate jdbcTemplate = DBUtil.getJdbcTemplate();
	private static BloggerDao bloggerDao = new BloggerDao();
	private Connection conn = DBUtil.getConnection();
	private PreparedStatement psForwardRel = null;
	private PreparedStatement psCommentRel = null;
	private PreparedStatement psAtRel = null;
	private PreparedStatement psAllRel = null;

	public void insertForwardRel(String nameF, String nameO)
			throws SQLException {
		if (psForwardRel == null) {
			psForwardRel = conn.prepareStatement(queryForwardRel,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
		}
		insertRel(nameF, nameO, psForwardRel);
	}

	public void insertCommentRel(String nameR, String nameO)
			throws SQLException {
		if (psCommentRel == null) {
			psCommentRel = conn.prepareStatement(queryCommentRel,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
		}
		insertRel(nameR, nameO, psCommentRel);
	}

	public void insertAtRel(String nameA, String nameO) throws SQLException {
		if (psAtRel == null) {
			psAtRel = conn.prepareStatement(queryAtRel,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
		}
		insertRel(nameA, nameO, psAtRel);
	}

	public void insertAllRel(String nameAction, String nameOrigin)
			throws SQLException {
		if (psAllRel == null) {
			psAllRel = conn.prepareStatement(queryAllRel,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
		}
		insertRel(nameAction, nameOrigin, psAllRel);
	}

	public void insertRel(String nameN, String nameO, PreparedStatement ps)
			throws SQLException {
		ps.setString(1, nameN);
		ps.setString(2, nameO);
		ResultSet rs = ps.executeQuery();
		int times = -1;
		if (rs.next()) {
			times = rs.getInt("times");
			rs.updateInt("times", ++times);
			rs.updateRow();
		} else {
			rs.moveToInsertRow();
			rs.updateString(1, nameN);
			rs.updateString(2, nameO);
			rs.updateInt(3, 1);
			rs.insertRow();
		}
	}

	public void insertNameNum(HashMap<String, Integer> map, String sql) {
		for (String name : map.keySet()) {
			int num = map.get(name);
			jdbcTemplate.update(sql, name, num);
		}
	}

	public List<RelEntry> getCommentRels() {
		String sql = "SELECT weibo_comment.content,weibo_info.user_name FROM weibo_comment,weibo_info "
				+ "where weibo_comment.weibo_id = weibo_info.weibo_id";
		final List<RelEntry> entries = new LinkedList<>();
		jdbcTemplate.query(sql, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				do {
					String originBlogger = rs.getString(2).trim();
					if (originBlogger.length() > 0) {
						String actionBlogger = getBloggerForComment(rs
								.getString(1));
						if (actionBlogger != null && actionBlogger.length() > 0) {
							RelEntry entry = new RelEntry(originBlogger,
									actionBlogger);
							entries.add(entry);
						}
					}
				} while (rs.next());
			}
		});
		return entries;
	}

	public List<RelEntry> getForwardRels() {
		String sql = "SELECT content,user_name FROM weibo_info where is_origin = 0";
		final List<RelEntry> entries = new LinkedList<>();
		jdbcTemplate.query(sql, new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				do {
					String originName = getBloggerForFoward(rs
							.getString("content"));
					if (originName != null && originName.length() > 0) {
						String name = rs.getString("user_name");
						RelEntry entry = new RelEntry(originName, name);
						entries.add(entry);
					}
				} while (rs.next());
			}

		});
		return entries;
	}

	private static String getBloggerForFoward(String content) {
		content = content.replace('：', ':');
		int index1 = content.indexOf("//@");
		String name = null;
		if (index1 != -1) {
			index1 += 3;
			int index2 = content.indexOf(":", index1);
			int index3 = content.indexOf(' ', index1);
			if (index3 > 0) {
				index2 = Math.min(index2, index3);
			}
			name = content.substring(index1, index2).trim();
			if (name.contains("@") || name.length() > 30 || name.length() == 0) {
				name = null;
			}
		}
		return name;
	}

	public List<RelEntry> getAtRels() {
		List<RelEntry> entries = new LinkedList<>();
		getCommentAtRels(entries);
		getWeiboAtRels(entries);
		return entries;
	}

	private List<RelEntry> getWeiboAtRels(final List<RelEntry> entries) {
		String queryWeibos = "SELECT content ,user_name FROM weibo_info";
		jdbcTemplate.query(queryWeibos, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				do {
					String originName = rs.getString("user_name").trim();
					if (originName.length() > 0) {
						String content = rs.getString("content");
						int index = content.indexOf("//@");
						if (index != -1) {
							content = content.substring(0, index);
						}
						content = content.replace('：', ':');
						List<String> atBloggers = ParseUtil
								.parseBloggerNameFromContent(content);
						if (atBloggers.size() > 0) {
							for (String blogger : atBloggers) {
								if (bloggerDao.hasBlogger(blogger)) {
									RelEntry entry = new RelEntry(originName,
											blogger);
									entries.add(entry);
								}
							}
						}
					}
				} while (rs.next());
			}
		});
		return entries;
	}

	private List<RelEntry> getCommentAtRels(final List<RelEntry> entries) {
		String queryComments = "SELECT content FROM weibo_comment";
		jdbcTemplate.query(queryComments, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				do {
					RelEntry entry = null;
					String content = rs.getString("content");
					String[] arr = content.split("：");
					String originName = arr[0].trim();
					if (originName != null && originName.length() > 0) {
						String info = arr[1];
						int index = info.indexOf("//@");
						if (index != -1) {
							info = info.substring(0, index);
						}
						info = info.replace('：', ':');
						List<String> atBloggers = ParseUtil
								.parseBloggerNameFromContent(info);
						if (atBloggers.size() > 0) {
							for (String at : atBloggers) {
								if (bloggerDao.hasBlogger(at)) {
									entry = new RelEntry(originName, at);
									entries.add(entry);
								}
							}
						}
					}
				} while (rs.next());
			}
		});
		return entries;
	}

	private static String getBloggerForComment(String content) {
		content = content.replace('：', ':');
		int index1 = content.indexOf(':');
		String name = null;
		if (index1 != -1) {
			name = content.substring(0, index1).trim();
			if (name.length() == 0 || name.contains("@") || name.length() > 30) {
				name = null;
			}
		}
		return name;
	}

}
