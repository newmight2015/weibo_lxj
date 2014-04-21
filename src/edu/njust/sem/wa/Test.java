package edu.njust.sem.wa;

import java.sql.SQLException;

import edu.njust.sem.wa.dao.DBUtil;
import edu.njust.sem.wa.leader.LeaderService;


public class Test {
	public static void main(String[] args) {
		//ForwardService forwardService = ForwardService.getInstance();
		//forwardService.extartExchangeInfo();
		//CommentService.getInstance().extartExchangeInfo();
		try {
			new LeaderService().countAllAtNums();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBUtil.closeConnection();
		}
	}
}
