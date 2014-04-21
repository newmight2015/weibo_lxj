package edu.njust.sem.wa.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import edu.njust.sem.wa.domain.Blogger;

public class BloggerDao {
	private JdbcTemplate jdbcTemplate = DBUtil.getJdbcTemplate();
	private String sqlAdd = "insert into weibo_blogger (id,url,nike_name,gender,follow_num,fans_num,weibo_num,"
			+ "verify,is_daren,is_member,introduce,address,school,company) "
			+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private String sqlQuery = "SELECT count(*) FROM weibo_blogger where nike_name = ?";
	private String sqlQueryBloggerByName = "SELECT id,url,nike_name,gender,follow_num,fans_num,weibo_num,"
			+ "verify,is_daren,"
			+ "is_member,introduce,address,school,company FROM weibo_blogger where nike_name = ?";

	public void insertBlogger(Blogger blogger) {
		jdbcTemplate
				.update(sqlAdd, blogger.getId(), blogger.getUrl(), blogger
						.getNikeName(), blogger.getGender(), blogger
						.getFollowNum(), blogger.getFansNum(), blogger
						.getWeiboNum(), blogger.getVerify(), blogger.isDaren(),
						blogger.isMember(), blogger.getIntroduce().trim(),
						blogger.getAddress(), blogger.getSchool(), blogger
								.getCompany());
	}

	public boolean hasBlogger(String name) {
		int count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, name);
		if (count > 0) {
			return true;
		}
		return false;
	}

	public Blogger getBloggerByName(String name) {
		final Blogger blogger = new Blogger();
		jdbcTemplate.query(sqlQueryBloggerByName, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				blogger.setId(rs.getString("id"));
				blogger.setUrl(rs.getString("url"));
				blogger.setNikeName(rs.getString("nike_name"));
				blogger.setGender(rs.getString("gender"));
				blogger.setFollowNum(rs.getInt("follow_num"));
				blogger.setFansNum(rs.getInt("fans_num"));
				blogger.setWeiboNum(rs.getInt("weibo_num"));
				blogger.setVerify(rs.getInt("verify"));
				blogger.setDaren(rs.getBoolean("is_daren"));
				blogger.setMember(rs.getBoolean("is_member"));
				blogger.setIntroduce(rs.getString("introduce"));
				blogger.setAddress(rs.getString("address"));
				blogger.setSchool(rs.getString("school"));
				blogger.setCompany(rs.getString("company"));
			}
		}, name);

		return blogger;
	}
}
