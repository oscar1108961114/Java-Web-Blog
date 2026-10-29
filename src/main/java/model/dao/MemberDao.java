package model.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.entity.Member;
import util.DButil;
public class MemberDao
{
	public Member getmember()
	{
		String sql = """
				 	 SELECT id , name , account , password FROM member WHERE id = 1					  
			         """;
		try (Connection conn = DButil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql);
	             ResultSet rs = ps.executeQuery())
		 {
			 while(rs.next())
			 {
				 Member m = new  Member();
				 m.setId(rs.getInt("id"));
				 m.setName(rs.getString("name"));
				 m.setAccount(rs.getString("account"));
				 m.setPassword(rs.getString("password"));
				 return m;
			 }
		 }
		 catch (SQLException e)
		 {
			e.printStackTrace();
			throw new RuntimeException("查詢帳號失敗", e);
		 }
		 return null;//找不到資料回傳 null
	}
}
