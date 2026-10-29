package util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DButil
{
	private static final String URL = "jdbc:mysql://localhost:3306/blog?useSSL=false&serverTimezone=Asia/Taipei&characterEncoding=utf8&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "970522";
    static
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException("MySQL Driver 載入失敗！", e);
        }
    }
    public static Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
