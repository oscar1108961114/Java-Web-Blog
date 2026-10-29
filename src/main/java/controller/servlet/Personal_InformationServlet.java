package controller.servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import model.dao.Personal_InformationDao;
import model.entity.Personal_Information;
@WebServlet("/personal_information")
public class Personal_InformationServlet extends HttpServlet
{
	private final Personal_InformationDao Dao = new Personal_InformationDao();
    private Gson gson = new Gson();
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	//1.讀取(Read)所有個人資訊
	{
		resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-store");//no-store:將舊數據完全不保存
        try
        {
        	Personal_Information info = Dao.getInfo();
            resp.getWriter().write(gson.toJson(info != null ? info : new Personal_Information()));//如果有資料，直接回傳 JSON 物件；如果沒有，回傳一個空物件
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
	@Override protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Map<String, Object> result = new HashMap<>();
        String idParam = req.getParameter("id");
        String user_name = req.getParameter("user_name");
        String user_dream = req.getParameter("user_dream");
        String introduction = req.getParameter("introduction");
        if (idParam == null || idParam.trim().isEmpty())//驗證id是否為空
        {
            resp.setStatus(400);
            result.put("success", false);
            result.put("message", "缺少個人資訊編號");
            resp.getWriter().write(gson.toJson(result));
            return;
        }
        if (user_name == null) user_name = "";
        if (user_dream == null) user_dream = "";
        if (introduction == null) introduction = "";
        try //更新
        {
            int id = Integer.parseInt(idParam.trim());
            Personal_Information info = new Personal_Information();
            info.setId(id);
            info.setUser_name(user_name.trim());
            info.setUser_dream(user_dream.trim());
            info.setIntroduction(introduction.trim());
            boolean ok =Dao.update(info);
            if (ok)
            {
                result.put("success", true);
                result.put("message", "個人資料更新成功");
            }
            else
            {
                resp.setStatus(404);
                result.put("success", false);
                result.put("message", "找不到該個人資料");
            }
        }
        catch (NumberFormatException e)
        {
            resp.setStatus(400);
            result.put("success", false);
            result.put("message", "個人資料格式錯誤");
        }
        catch (RuntimeException e)
        {
            resp.setStatus(500);
            result.put("success", false);
            result.put("message", "更新失敗：" + e.getMessage());
        }
        resp.getWriter().write(gson.toJson(result));
	}
	
}
