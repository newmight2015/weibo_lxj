package edu.njust.sem.wa.rel;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import edu.njust.sem.wa.dao.DBUtil;
import edu.njust.sem.wa.dao.RelDao;

public class CommentRel {
	private static RelDao relDao = new RelDao();
	private static HashMap<String, Integer> map = new HashMap<>();

	public static void main(String[] args) {
		List<RelEntry> entries = relDao.getCommentRels();
		try {
			for (RelEntry entry : entries) {
				String nameR = entry.getForwardBloggerId();
				String nameO = entry.getOriginBloggerId();
				try {
					insertRel(nameR, nameO);
					putInMap(nameR);
					putInMap(nameO);
				} catch (SQLException e) {
					e.printStackTrace();
					continue;
				}
			}
			String sql = "insert into weibo_comment_rel_num values(?,?)";
			relDao.insertNameNum(map, sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeConnection();
		}
	}

	private static void insertRel(String nameR, String nameO)
			throws SQLException {
		relDao.insertCommentRel(nameR, nameO);
	}

	public static void putInMap(String word) {
		if (map.containsKey(word)) {
			int num = map.get(word) + 1;
			map.put(word, num);
		} else {
			map.put(word, 1);
		}
	}
}
