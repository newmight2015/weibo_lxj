package edu.njust.sem.wa.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import edu.njust.sem.wa.SearchPages;
import edu.njust.sem.wa.util.TimeUtil;

public class PagesDao {
	private JdbcTemplate jdbcTemplate = DBUtil.getJdbcTemplate();

	private String insert = "insert into weibo_pages_log(task_id,keyword,end_time,curr_page,total_page) "
			+ "values(?,?,?,?,?)";
	private String updateTime = "update weibo_pages_log set start_time = ?"
			+ " where task_id = ? and keyword=? and end_time = ?";
	private String updateCurrPageNum = "update weibo_pages_log set curr_page = ? "
			+ "where keyword=? and end_time = ?";
	private String updateTotalPageNum = "update weibo_pages_log set total_page = ? "
			+ " where task_id = ? and keyword=? and end_time = ?";
	private String querySearchPages = "SELECT task_id,keyword ,end_time,curr_page FROM weibo_pages_log "
			+ "where curr_page <= total_page";

	public void insertSearchPages(SearchPages pages) {
		String endTime = pages.getEndTime();
		if (endTime == null || endTime.length() == 0) {
			endTime = TimeUtil.getCurrDate();
		}
		jdbcTemplate.update(insert, pages.getTask().getTaskId(),
				pages.getKeyword(), endTime, pages.getCurrPageNum(),
				pages.getTotalPageNum());
	}

	public void updateStartTime(SearchPages pages) {
		jdbcTemplate.update(updateTime, pages.getStartTime(),
				pages.getTaskId(), pages.getKeyword(), pages.getEndTime());
	}

	public void updateTotalPageNum(SearchPages pages) {
		jdbcTemplate
				.update(updateTotalPageNum, pages.getTaskId(),
						pages.getTotalPageNum(), pages.getKeyword(),
						pages.getEndTime());
	}

	public void updateCurrPageNum(SearchPages pages) {
		jdbcTemplate.update(updateCurrPageNum, pages.getCurrPageNum(),
				pages.getKeyword(), pages.getEndTime());
	}

	public SearchPages getUncomplatedSearchPages() {
		final SearchPages pages = SearchPages.getInstance();
		jdbcTemplate.query(querySearchPages, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				pages.setTaskId(rs.getInt("task_id"));
				pages.setKeyword(rs.getString("keyword"));
				pages.setEndTime(rs.getString("end_time"));
				pages.setCurrPageNum(rs.getInt("curr_page"));
			}
		});
		if (pages.getKeyword() != null && pages.getKeyword().length() > 0) {
			pages.setTask(TaskDao.getTaskHavingSearchPages(pages));
			pages.init();
			return pages;
		}
		return null;
	}
}
