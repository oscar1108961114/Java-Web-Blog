package controller.servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import com.google.gson.Gson;
import model.dao.MemberDao;
import model.entity.Member;
@WebServlet("/blog_member")
public class MemberServlet extends HttpServlet
{
	private final MemberDao Dao = new MemberDao();
    private Gson gson = new Gson();
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	//檢查是否已登入，用前端來判斷要不要顯示編輯/刪除按鈕
	{
		resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-store");//no-store:將舊數據完全不保存
        HttpSession session = req.getSession(false);
        Map<String, Object> result = new HashMap<>();
        if (session != null && session.getAttribute("loggedInUser") != null)
        {
            result.put("success", true);
            result.put("message", "已登入");
        }
        else
        {
            result.put("success", false);
            result.put("message", "未登入");
        }
        resp.getWriter().write(gson.toJson(result));
	}
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String account = req.getParameter("account");
        String password = req.getParameter("password");
        Map<String, Object> result = new HashMap<>();
        if (account == null || password == null || account.trim().isEmpty() || password.trim().isEmpty())
        {
            resp.setStatus(400);
            result.put("success", false);
            result.put("message", "帳號或密碼不能為空");
            resp.getWriter().write(gson.toJson(result));
            return;
        }
        Member member = Dao.getmember();
        if (member != null && BCrypt.checkpw(password, member.getPassword()))
        {
        	HttpSession session = req.getSession(true); // 登入成功，建立 Session
        	session.setAttribute("loggedInUser", member);
            session.setMaxInactiveInterval(30 * 60); //30分鐘後自動登出
            result.put("success", true);
            result.put("message", "登入成功");
            resp.getWriter().write(gson.toJson(result));
        }
        else
        {
            resp.setStatus(401);
            result.put("success", false);
            result.put("message", "帳號或密碼錯誤");
            resp.getWriter().write(gson.toJson(result));
        }
	  }
	@Override protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
	    resp.setContentType("application/json");
	    resp.setCharacterEncoding("UTF-8");
	    HttpSession session = req.getSession(false);
	    if (session != null)
	    {
	        session.invalidate();
	    }	    
	    Map<String, Object> result = new HashMap<>();
	    result.put("success", true);
	    result.put("message", "已登出");
	    resp.getWriter().write(gson.toJson(result));
	 }
}
