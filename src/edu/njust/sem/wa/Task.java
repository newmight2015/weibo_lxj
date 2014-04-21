package edu.njust.sem.wa;

import edu.njust.sem.wa.dao.PagesDao;
import edu.njust.sem.wa.dao.TaskDao;

public class Task {
	private int taskId;
	private String keyword;
	private String startTime;
	private String endTime;
	private String currTime;
	private boolean isComplated;
	private static TaskDao taskDao = new TaskDao();
	private static PagesDao pagesDao = new PagesDao();
	private static Task task;
	private static SearchPages pages;

	private Task() {

	}

	public static Task getInstance() {
		if (task == null) {
			task = new Task();
		}
		return task;
	}

	/**
	 * 获取未完成任务队列里面的第一个任务
	 * 
	 * @return 返回一个未完成的任务，如果没有未完成任务，返回null
	 */
	public static Task getTask() {
		if (task == null) {
			task = taskDao.getUnFinishedTask(new Task());
		}
		return task;
	}

	/**
	 * 结束一个任务
	 */
	public void finishTask() {
		taskDao.finishTask(task);
		task = null;
	}

	/**
	 * 每当一个搜索页面抓取完成，就要更新任务当前时间
	 */
	public void updateTaskCurrTime() {
		taskDao.updateTaskCurrTime(task);
	}

	/**
	 * 返回新的搜索页面SearchPages实例
	 * 
	 * @return
	 */
	public SearchPages getNextSearchPages() {
		updateTaskCurrTime();
		String keyword = task.getKeyword();
		if (keyword == null) {
			return null;
		}
		pages = SearchPages.getInstance();
		pages.setKeyword(keyword);
		pages.setEndTime(task.getCurrTime());
		pages.setDeadline(task.getStartTime());
		pages.setTask(task);
		if (pages.init()) {
			pagesDao.insertSearchPages(pages);
			return pages;
		} else {
			task.finishTask();
		}
		return null;
	}

	public SearchPages getCurrSearchPages() {
		return pages;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getCurrTime() {
		return currTime;
	}

	public void setCurrTime(String currTime) {
		this.currTime = currTime;
	}

	public boolean isComplated() {
		return isComplated;
	}

	public void setComplated(boolean isComplated) {
		this.isComplated = isComplated;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

}
