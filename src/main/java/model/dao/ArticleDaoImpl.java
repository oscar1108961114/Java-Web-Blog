package model.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.entity.Article;
import util.DButil;
public class ArticleDaoImpl implements ArticleDao
{
	@Override public List<Article> findAll() //查詢資料庫所有Article物件(給前端渲染用)
	{
		List<Article> list = new ArrayList<Article>();
		String sql = """
						 SELECT a.id ,a.title ,a.content ,a.author , a.created_at FROM article a					  
					 """;
		 try (Connection conn = DButil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql);
	             ResultSet rs = ps.executeQuery())
		 {
			 while(rs.next())
			 {
				 Article a = new Article();
				 a.setId(rs.getInt("id"));
				 a.setTitle(rs.getString("title"));
				 a.setContent(rs.getString("content"));
				 a.setAuthor(rs.getString("author"));
				 a.setCreated_at(rs.getTimestamp("created_at").getTime());
				 list.add(a);
			 }
		 }
		 catch (SQLException e)
		 {
			e.printStackTrace();
			throw new RuntimeException("查詢文章失敗", e);
		 }
		 return list;
	}
	@Override public Article insert(Article article) //從前端寫入文章，除了前端要渲染以外，資料庫也要寫入資料
	{
        String sql = "INSERT INTO article (title, content, author) "
                   + "VALUES (?, ?, ?)";
        try (Connection conn = DButil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            ps.setString(1, article.getTitle());
            ps.setString(2, article.getContent());
            ps.setString(3, article.getAuthor());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys())
            {
                if (keys.next())
                {
                    article.setId(keys.getInt(1));
                }
            }
            return article;
        }
        catch (SQLException e)
        {
            throw new RuntimeException("新增文章失敗", e);
        }
	 }
	 @Override public boolean update(Article article) //更新/編輯/修改文章
	 {
		 String sql = "UPDATE article SET title = ?, content = ? WHERE id = ?";
		 try (Connection conn = DButil.getConnection();
		         PreparedStatement ps = conn.prepareStatement(sql))
		 {
			ps.setString(1, article.getTitle());
	        ps.setString(2, article.getContent());
	        ps.setInt(3, article.getId());
	        return ps.executeUpdate() > 0;
		 }
		 catch (SQLException e)
		 {
			e.printStackTrace();
	        throw new RuntimeException("更新文章失敗", e);
		 }
	 }
	 @Override public boolean deleteById(int id) //前端刪除文章資料庫也要跟著刪除
	 {
        String sql = "DELETE FROM article WHERE id = ?";
        try (Connection conn = DButil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            throw new RuntimeException("刪除文章失敗", e);
        }
	 }
}
