package edu.njust.sem.wa.rel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import edu.njust.sem.wa.dao.DBUtil;

public class Test {
	private static HashMap<String, Integer> map = new HashMap<>();

	private static JdbcTemplate jt = DBUtil.getJdbcTemplate();

	public static void main(String[] args) {
//		while(hasOneDegreeNode()){
		delete();
//		}
	}
	private static boolean hasOneDegreeNode(){
		int num = jt.queryForObject("SELECT count(*) FROM weibo.weibo_all_rel_num where num = 1", Integer.class);
		if(num > 0){
			return true;
		}
		return false;
	}
	private static void delete() { 
		jt.update("delete FROM weibo_all_rel where action_blogger in (SELECT blogger_name FROM weibo_all_rel_num where num = 1)");
		jt.update("delete FROM weibo_all_rel where origin_blogger in (SELECT blogger_name FROM weibo_all_rel_num where num = 1)");
		jt.update("delete FROM weibo_all_rel_num");
		String sql1 = "SELECT sum(times),action_blogger FROM weibo_all_rel group by action_blogger";
		String sql2 = "SELECT sum(times),origin_blogger FROM weibo_all_rel group by origin_blogger";
		jt.query(sql1, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				do {
					String name = rs.getString(2);
					int times = rs.getInt(1);
					putInMap(name, times);
				} while (rs.next());
			}

		});
		jt.query(sql2, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				do {
					String name = rs.getString(2);
					int times = rs.getInt(1);
					putInMap(name, times);
				} while (rs.next());
			}

		});
		insertNameNum(map);
	}

	public static void putInMap(String name, int times) {
		if (map.containsKey(name)) {
			int num = map.get(name) + times;
			map.put(name, num);
		} else {
			map.put(name, times);
		}
	}

	public static void insertNameNum(HashMap<String, Integer> map) {
		String sql = "insert into weibo_all_rel_num values(?,?)";
		for (String name : map.keySet()) {
			int num = map.get(name);
			jt.update(sql,name, num);
		}
	}
}
