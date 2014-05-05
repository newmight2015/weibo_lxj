package edu.njust.sem.wa.rel;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import edu.njust.sem.wa.dao.DBUtil;
import edu.njust.sem.wa.dao.RelDao;

public class AllRel {
	private static RelDao relDao = new RelDao();
	private static HashMap<String, Integer> map = new HashMap<>();

	public static void main(String[] args) {
		List<RelEntry> entries = relDao.getForwardRels();
		entries.addAll(relDao.getCommentRels());
		entries.addAll(relDao.getAtRels());
		try {
			for (RelEntry entry : entries) {
				String nameO = entry.getOriginBloggerId();
				String nameF = entry.getForwardBloggerId();
				if (nameO != null) {
					try {
						insertRel(nameF, nameO);
						putInMap(nameF);
						putInMap(nameO);
					} catch (SQLException e) {
						e.printStackTrace();
						continue;
					}
				}
			}
			String sql = "insert into weibo_all_rel_num values(?,?)";
			relDao.insertNameNum(map, sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeConnection();
		}
	}

	private static void insertRel(String nameF, String nameO)
			throws SQLException {
		relDao.insertAllRel(nameF, nameO);
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
