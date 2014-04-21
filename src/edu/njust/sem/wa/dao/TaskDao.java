package edu.njust.sem.wa.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import edu.njust.sem.wa.SearchPages;
import edu.njust.sem.wa.Task;

public class TaskDao {
	private static JdbcTemplate jdbcTemplate = DBUtil.getJdbcTemplate();
	private String queryTask = "SELECT task_id,keyword,start_time,end_time,curr_time "
			+ "FROM weibo_task where is_finished = false limit 1";
	private String updateTaskToFinish = "update  weibo_task set is_finished = true "
			+ "where task_id = ?";
	private String updateTaskCurrTime = "update  weibo_task set curr_time = ?"
			+ "where task_id = ?";
	private static String queryTaskHavingSearchPages = "SELECT task_id,keyword,is_finished,start_time,end_time,curr_time "
			+ "FROM weibo_task where task_id = ?";

	/**
	 * 获取一个未完成的任务
	 * 
	 * @param task
	 * @return
	 */
	public Task getUnFinishedTask(final Task task) {
		jdbcTemplate.query(queryTask, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				task.setTaskId(rs.getInt("task_id"));
				task.setKeyword(rs.getString("keyword"));
				task.setCurrTime(rs.getString("curr_time"));
				task.setStartTime(rs.getString("start_time"));
				task.setEndTime(rs.getString("end_time"));
			}
		});
		if (task != null && task.getKeyword() != null) {
			return task;
		}
		return null;
	}

	/**
	 * 
	 * @param pages
	 * @return
	 */
	public static Task getTaskHavingSearchPages(final SearchPages pages) {
		final Task task = Task.getInstance();
		jdbcTemplate.query(queryTaskHavingSearchPages,
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						task.setTaskId(rs.getInt("task_id"));
						task.setKeyword(rs.getString("keyword"));
						task.setCurrTime(rs.getString("curr_time"));
						task.setStartTime(rs.getString("start_time"));
						task.setEndTime(rs.getString("end_time"));
						
					}
				}, pages.getTaskId());
		return task;
	}

	public void updateTaskCurrTime(Task task) {
		jdbcTemplate.update(updateTaskCurrTime, task.getCurrTime(),
				task.getTaskId());
	}

	public void finishTask(Task task) {
		jdbcTemplate.update(updateTaskToFinish, task.getTaskId());
	}
}
