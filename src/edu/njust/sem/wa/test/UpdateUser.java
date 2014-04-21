package edu.njust.sem.wa.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.njust.sem.wa.dao.DBUtil;
import edu.njust.sem.wa.domain.Blogger;
import edu.njust.sem.wa.service.BloggerService;
import edu.njust.sem.wa.util.TimeUtil;

public class UpdateUser {

	public static void main(String[] args) throws SQLException {
		Connection conn = DBUtil.getConnection();
		PreparedStatement ps = conn.prepareStatement(
				"SELECT user_id,name,verify FROM user where verify=-1",
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			TimeUtil.sleep(1000);
			Blogger blogger = new Blogger();
			//blogger.setId(Long.parseLong(rs.getString("user_id")));
			blogger = BloggerService.getBlogger(blogger);
			if(blogger != null){
				rs.updateInt("verify", blogger.getVerify());
				rs.updateRow();
			}
		}
		DBUtil.closeConnection();
	}

}
