package controller.filter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.gson.Gson;
@WebFilter(urlPatterns = {"/articles", "/personal_information"})
public class BlogFilter extends HttpFilter
{
	private final Gson gson = new Gson();
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String method = req.getMethod();
		if ("GET".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method)) //如果只是看文章 (GET)，或是 OPTIONS (預檢請求)，直接放行
		{
            chain.doFilter(req, resp);
            return;
        }
		HttpSession session = req.getSession(false); //如果是修改操作 (POST/PUT/DELETE)，檢查 Session
		 if (session == null || session.getAttribute("loggedInUser") == null)
		 {
            
            resp.setStatus(401);// 沒登入，回傳 401 Unauthorized
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "權限不足，請先登入！");
            resp.getWriter().write(gson.toJson(err));
            return; // 攔截，不繼續往下走
         }
		 chain.doFilter(req, resp);//有登入，放行
	}
}
