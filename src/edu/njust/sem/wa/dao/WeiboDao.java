package edu.njust.sem.wa.dao;

import org.springframework.jdbc.core.JdbcTemplate;

import edu.njust.sem.wa.domain.Weibo;

public class WeiboDao {
	private JdbcTemplate jdbcTemplate = DBUtil.getJdbcTemplate();

	private String insertWeibo = "insert into weibo_info (weibo_id,mid,blogger_id,blogger_name,forward_num,"
			+ "comment_num,approve_num,collect_num,search_keyword,url,content,origin,publish_time)"
			+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private String queryNum = "SELECT count(*) FROM weibo_info where mid=?";
	

	private static WeiboDao weiboDao;

	private WeiboDao() {
	};

	public static WeiboDao getInstance() {
		if (weiboDao == null) {
			weiboDao = new WeiboDao();
		}
		return weiboDao;
	}

	public boolean insertWeibo(Weibo weibo) {
		int count = jdbcTemplate.queryForObject(queryNum, Integer.class,
				weibo.getMid());
		if (count == 0) {
			jdbcTemplate.update(insertWeibo, weibo.getWeiboId(),
					weibo.getMid(), weibo.getBloggerId(),
					weibo.getBloggerName(), weibo.getForwardNum(),
					weibo.getCommentNum(), weibo.getApproveNum(),
					weibo.getCollectNum(), weibo.getSearchKeyword(),
					weibo.getUrl(), weibo.getContent(), weibo.getOrigin(),
					weibo.getPublishTime());
			return true;
		}
		return false;
	}

}
