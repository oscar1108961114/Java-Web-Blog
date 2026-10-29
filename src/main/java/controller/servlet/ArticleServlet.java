package controller.servlet;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.dao.ArticleDao;
import model.dao.ArticleDaoImpl;
import model.entity.Article;
import com.google.gson.Gson;
@WebServlet("/articles")
public class ArticleServlet extends HttpServlet
{
	private final ArticleDao articleDao = new ArticleDaoImpl();
    private Gson gson = new Gson();
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    //1.讀取(Read)所有文章
    {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-store");//no-store:將舊數據完全不保存
        try
        {
            List<Article> articles = articleDao.findAll();
            resp.getWriter().write(gson.toJson(articles));
        }
        catch (RuntimeException e)
        {
            resp.setStatus(500);
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "查詢失敗：" + e.getMessage());
            resp.getWriter().write(gson.toJson(err));
        }
    }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    //2.發表(Create)新文章
    {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");	
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        String author = req.getParameter("author");
        if(author == null || author.trim().isEmpty())
        {
        	author = "匿名作者";
        }
        if (title != null && !title.trim().isEmpty() && content != null && !content.trim().isEmpty())
        {
        	 try
        	 {
    	        Article article = new Article(0, title.trim(), content.trim(), author);
    	        Article saved = articleDao.insert(article);
    	        resp.setStatus(201);
    	        Map<String, Object> success_result = new HashMap<>();
    	        success_result.put("success", true);
    	        success_result.put("message", "文章發表成功:");
    	        success_result.put("data", saved);
    	        resp.getWriter().write(gson.toJson(success_result));
        	 }
        	 catch (RuntimeException e)
        	 {
    	        resp.setStatus(500);
    	        Map<String, Object> err = new HashMap<>();
    	        err.put("success", false);
    	        err.put("message", "發表失敗：" + e.getMessage());
    	        resp.getWriter().write(gson.toJson(err));
    	     }
        }
        else
        {
            resp.setStatus(400);
            Map<String, Object> failed_result = new HashMap<String, Object>();
            failed_result.put("success" , false);
            failed_result.put("message" , ":標題或內容不能為空！");
            resp.getWriter().write(gson.toJson(failed_result));
        }
    }
    @Override protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    //編輯/更新/修改(Update)
    {
    	req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Map<String, Object> result = new HashMap<>();
        String idParam = req.getParameter("id");
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        if (idParam == null || idParam.trim().isEmpty())//驗證id是否為空
        {
            resp.setStatus(400);
            result.put("success", false);
            result.put("message", "缺少文章編號");
            resp.getWriter().write(gson.toJson(result));
            return;
        }
        if (title == null || title.trim().isEmpty() //驗證文章標題和內容是否為空
            || content == null || content.trim().isEmpty())
        {
            resp.setStatus(400);
            result.put("success", false);
            result.put("message", "標題和內容不能為空");
            resp.getWriter().write(gson.toJson(result));
            return;
        }
        try //更新
        {
            int id = Integer.parseInt(idParam.trim());
            Article article = new Article();
            article.setId(id);
            article.setTitle(title.trim());
            article.setContent(content.trim());
            boolean ok = articleDao.update(article);
            if (ok)
            {
                result.put("success", true);
                result.put("message", "文章更新成功");
            }
            else
            {
                resp.setStatus(404);
                result.put("success", false);
                result.put("message", "找不到該文章");
            }
        }
        catch (NumberFormatException e)
        {
            resp.setStatus(400);
            result.put("success", false);
            result.put("message", "文章編號格式錯誤");
        }
        catch (RuntimeException e)
        {
            resp.setStatus(500);
            result.put("success", false);
            result.put("message", "更新失敗：" + e.getMessage());
        }
        resp.getWriter().write(gson.toJson(result));
    }
    @Override protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    //4.刪除(Delete)文章
    {
    	 req.setCharacterEncoding("UTF-8");
         resp.setContentType("application/json");
         resp.setCharacterEncoding("UTF-8");
         Map<String, Object> result = new HashMap<>();
         String idParam = req.getParameter("id");
         if (idParam == null || idParam.trim().isEmpty())
         {
             resp.setStatus(400);
             result.put("success", false);
             result.put("message", "缺少文章編號");
             resp.getWriter().write(gson.toJson(result));
             return;
         }
         try
         {
             int id = Integer.parseInt(idParam);
             boolean ok = articleDao.deleteById(id);
             if (ok)
             {
                 result.put("success", true);
                 result.put("message", "文章刪除成功");
             }
             else
             {
                 resp.setStatus(404);
                 result.put("success", false);
                 result.put("message", "找不到該文章");
             }
         }
         catch (NumberFormatException e)
         {
             resp.setStatus(400);
             result.put("success", false);
             result.put("message", "文章編號格式錯誤");
         }
         catch (RuntimeException e)
         {
             resp.setStatus(500);
             result.put("success", false);
             result.put("message", "刪除失敗：" + e.getMessage());
         }
         resp.getWriter().write(gson.toJson(result));
    }
}
