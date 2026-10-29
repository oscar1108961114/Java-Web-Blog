package util;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import model.entity.Article;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class ArticleCsvutil
{
	public static void main(String[] args)
	{
        String csvPath = "src/main/resources/csv/Article.csv";
        List<Article> articles = ArticleCsvutil.readArticles(csvPath);
        int n = ArticleCsvutil.writeArticle(articles);
        System.out.println("成功匯入 " + n + " 篇");
    }
	 private static final String[] HEADERS = {"title", "content", "author"};
	 public static List<Article> readArticles(String filePath) //1. 讀取 CSV，回傳 Article 列表（不含 id 和 createdAt，交給資料庫自動生成）
	 {
		 List<Article> list = new ArrayList<>();
		 try (BufferedReader br = new BufferedReader(
	                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8)))
		 {
			 String headerLine = br.readLine();
			 if (headerLine == null) return list;
			 if (headerLine.startsWith("\uFEFF")) //處理 BOM（CSV 用 Excel 存檔時經常會有）
			 {
                headerLine = headerLine.substring(1);
             }
			 String headers[] = headerLine.split(",", -1);//以逗號切割並保留結尾的空字串
			 int titleIdx = indexOf(headers, "title");
             int contentIdx = indexOf(headers, "content");
             int authorIdx = indexOf(headers, "author");
             if (titleIdx < 0 || contentIdx < 0 || authorIdx < 0)
             {
                 throw new RuntimeException("CSV 缺少必要欄位（title / content / author）");
             }
             String line;
             int lineNo = 1;
             while ((line = br.readLine()) != null)
             {
                 lineNo++;
                 if (line.trim().isEmpty()) continue;
                 String[] cols = line.split(",", -1);
                 int maxIdx = Math.max(titleIdx, Math.max(contentIdx, authorIdx));
                 if (cols.length <= maxIdx)              
                 {
                     System.err.println("第 " + lineNo + " 行欄位數不足，跳過");
                     continue;
                 }
                 Article a = new Article();
                 a.setTitle(cols[titleIdx].trim());
                 a.setContent(cols[contentIdx].trim());
                 a.setAuthor(cols[authorIdx].trim());
                 list.add(a);
             }
		 }
		 catch (Exception e)
		 {
            throw new RuntimeException("讀取 CSV 失敗：" + e.getMessage(), e);
         }
		 return list;
	 }
	 public static int writeArticle(List<Article> articles)
	 {
		 if (articles == null || articles.isEmpty()) return 0;
		 String sql = "INSERT INTO article (title, content, author) VALUES (?, ?, ?)";
		 int inserted = 0;
		 try (Connection conn = DButil.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql))
		 {
			 conn.setAutoCommit(false); // 開事務，整批要嘛全成功，要嘛全失敗
			 for (Article a : articles)
			 {
                ps.setString(1, a.getTitle());
                ps.setString(2, a.getContent());
                ps.setString(3, a.getAuthor());
                ps.addBatch();
             }
			 int[] results = ps.executeBatch();
			 inserted = results.length;
			 conn.commit();
		 }
		 catch (SQLException e)
		 {
			e.printStackTrace();
            throw new RuntimeException("批量匯入失敗：" + e.getMessage(), e);
         }
		 return inserted;
	 }
	 private static int indexOf(String[] arr, String target) //工具：查找表頭索引（忽略大小寫和空白）
	 {
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i].trim().equalsIgnoreCase(target)) return i;
        }
        return -1;
     }
}
