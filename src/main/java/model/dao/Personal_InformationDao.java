package model.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.entity.Personal_Information;
import util.DButil;
public class Personal_InformationDao
{
	public Personal_Information getInfo() //查詢資料庫所有Personal_Information物件(給前端渲染用)
	{
		String sql = """
						 SELECT id ,user_name ,user_dream ,introduction FROM personal_information WHERE id = 1					  
					 """;
		 try (Connection conn = DButil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql);
	             ResultSet rs = ps.executeQuery())
		 {
			 while(rs.next())
			 {
				 Personal_Information p = new  Personal_Information();
				 p.setId(rs.getInt("id"));
				 p.setUser_name(rs.getString("user_name"));
				 p.setUser_dream(rs.getString("user_dream"));
				 p.setIntroduction(rs.getString("introduction"));
				 return p;
			 }
		 }
		 catch (SQLException e)
		 {
			e.printStackTrace();
			throw new RuntimeException("查詢個人資料失敗", e);
		 }
		 return null;//找不到資料回傳 null
	 }
	 public boolean update(Personal_Information personal_information) //更新/編輯/修改個人資料
	 {
		 String sql = "UPDATE personal_information SET user_name = ? , user_dream = ? , introduction = ? WHERE id = ?";
		 try (Connection conn = DButil.getConnection();
		         PreparedStatement ps = conn.prepareStatement(sql))
		 {
			ps.setString(1, personal_information.getUser_name());
	        ps.setString(2, personal_information.getUser_dream());
	        ps.setString(3, personal_information.getIntroduction());
	        ps.setInt(4, personal_information.getId());
	        return ps.executeUpdate() > 0;
		 }
		 catch (SQLException e)
		 {
			e.printStackTrace();
	        throw new RuntimeException("更新個人資料失敗", e);
		 }
	 }
}
