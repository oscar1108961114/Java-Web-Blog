package util;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;
public class MemberCSVutil
{
	public static void main(String[] args)
	{
        String csvPath = "src/main/resources/csv/administrator.csv";
        List<String[]> members = read(csvPath);
        int n = writeMembers(members);
        System.out.println("成功匯入 " + n + " 筆管理員資料！");
    }
	 public static List<String[]> read(String filePath)
	 {
	        List<String[]> list = new ArrayList<>();
	        try (BufferedReader br = new BufferedReader(
	                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8)))
	        {
	            String headerLine = br.readLine();
	            if (headerLine == null) return list;	                    
	            if (headerLine.startsWith("\uFEFF")) //處理 BOM
	            {
	                headerLine = headerLine.substring(1);
	            }
	            String[] headers = headerLine.split(",", -1);
	            int nameIdx = indexOf(headers, "name");
	            int accountIdx = indexOf(headers, "account");
	            int passwordIdx = indexOf(headers, "password");
	            if (accountIdx < 0 || passwordIdx < 0)
	            {
	                throw new RuntimeException("CSV 缺少必要欄位（account / password）");
	            }
	            String line;
	            while ((line = br.readLine()) != null)
	            {
	                if (line.trim().isEmpty()) continue;
	                String[] cols = line.split(",", -1);// 將 name, account, password 存進陣列 (因為 Member 實體類可能沒有 name 欄位，我們先這樣接)              	                
	                String name = nameIdx >= 0 && cols.length > nameIdx ? cols[nameIdx].trim() : "";
	                String account = cols.length > accountIdx ? cols[accountIdx].trim() : "";
	                String password = cols.length > passwordIdx ? cols[passwordIdx].trim() : "";	                
	                list.add(new String[]{name, account, password});
	            }
	        }
	        catch (Exception e)
	        {
	            throw new RuntimeException("讀取 CSV 失敗：" + e.getMessage(), e);
	        }
	        return list;
      }
	 public static int writeMembers(List<String[]> members)
	 {
        if (members == null || members.isEmpty()) return 0;	    	        
        String sql = "INSERT INTO member (name, account, password) VALUES (?, ?, ?)";
        int inserted = 0;
        try (Connection conn = DButil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            conn.setAutoCommit(false); // 開啟事務
            for (String[] m : members) {
                String name = m[0];
                String account = m[1];
                String rawPassword = m[2]; // 明文密碼	                
                String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
                ps.setString(1, name);
                ps.setString(2, account);
                ps.setString(3, hashedPassword); //存入加密後的密碼
                ps.addBatch();
            }
            int[] results = ps.executeBatch();
            inserted = results.length;
            conn.commit();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new RuntimeException("批量匯入管理員失敗：" + e.getMessage(), e);
        }
        return inserted;
      }
	 private static int indexOf(String[] arr, String target)// 工具：查找表頭索引
	 {
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i].trim().equalsIgnoreCase(target)) return i;
        }
        return -1;
     }
}
