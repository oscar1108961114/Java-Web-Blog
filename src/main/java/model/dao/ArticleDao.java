package model.dao;
import java.util.List;
import model.entity.Article;
public interface ArticleDao
{
	List<Article> findAll();
    Article insert(Article article);
    boolean deleteById(int id);
    boolean update(Article article);
}
