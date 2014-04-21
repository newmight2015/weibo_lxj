package edu.njust.sem.wa.leader;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import edu.njust.sem.wa.dao.LeaderDao;
import edu.njust.sem.wa.util.ParseUtil;

public class LeaderService {
	private LeaderDao leaderDao = new LeaderDao();
	private Map<String, Leader> leaders = leaderDao.getImportantBlogger();

	/**
	 * 统计所有意见领袖候选人被@的次数
	 * 
	 * @throws SQLException
	 */
	public void countAllAtNums() throws SQLException {
		List<String> contents = leaderDao.getExchangeInfo();
		for (String content : contents) {
			List<String> names = ParseUtil.parseBloggerNameFromContent(content);
			for (String name : names) {
				if (leaders.containsKey(name)) {
					leaders.get(name).addAtNum(1);
				}
			}
		}
		for(String key : leaders.keySet()){
			Leader leader = leaders.get(key);
			leader.init();
			System.out.println(leader);
			leaderDao.insertLeader(leader);
		}
	}
}
