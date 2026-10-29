package util;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.entity.Personal_Information;
public class Personal_InformatonCSVutil
{
	public static void main(String[] args)
	{
        String csvPath = "src/main/resources/csv/Personal_Information.csv";
        Personal_Information info = Personal_InformatonCSVutil.read(csvPath);
        Personal_InformatonCSVutil.writer(info);
    }
	public static Personal_Information read(String filePath)
	{
		try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8)))
		{
            String headerLine = br.readLine();
            if (headerLine == null) return null;                        
            if (headerLine.startsWith("\uFEFF"))//Excel 存 CSV 常有的 \uFEFF
            {
                headerLine = headerLine.substring(1);
            }
            String dataLine = br.readLine();
            if (dataLine == null) return null;           
            List<String> cols = parseCsvLine(dataLine);// 解析 CSV 行（支援引號包覆與 "" 跳脫）           
            Personal_Information info = new Personal_Information();//依照表頭順序：user_name, user_dream, introduction                       
            info.setUser_name(cols.size() > 0 ? cols.get(0).trim() : "");// 1. user_name (如果是空字串就給空字串)           
            info.setUser_dream(cols.size() > 1 ? cols.get(1).trim() : "");// 2. user_dream(如果是空字串就給空字串)           
            
            String intro = cols.size() > 2 ? cols.get(2) : "";// 3. introduction (把 CSV 裡的字面 \n 轉成真實換行符，並處理雙引號)           
            if (intro.startsWith("\"") && intro.endsWith("\""))//處理前後可能殘留的引號（解析器通常會處理掉，但保險起見）
            {
                intro = intro.substring(1, intro.length() - 1);
            }            
            intro = intro.replace("\\n", "\n");// 將字面上的 \n 轉成真正的換行符 \n            
            info.setIntroduction(intro);
            info.setId(1); //固定 id=1
            return info;

        }
		catch (Exception e)
		{
            throw new RuntimeException("讀取 CSV 失敗：" + e.getMessage(), e);
        }
	  }
	 public static void writer(Personal_Information info)
	 {
		 if (info == null) return;
		 String sql = """
		 			  INSERT INTO personal_information (id, user_name, user_dream, introduction) VALUES (?, ?, ?, ?)
		 			  ON DUPLICATE KEY UPDATE user_name = VALUES(user_name), user_dream = VALUES(user_dream),
		 			  introduction = VALUES(introduction)		 			  
		 		      """;
		 try (Connection conn = DButil.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql))
		 {

	            ps.setInt(1, info.getId());
	            ps.setString(2, info.getUser_name());
	            ps.setString(3, info.getUser_dream());
	            ps.setString(4, info.getIntroduction());
	            
	            ps.executeUpdate();
	            System.out.println("個人資訊匯入成功！");

         }
		 catch (SQLException e)
		 {
            throw new RuntimeException("匯入資料庫失敗：" + e.getMessage(), e);
         }
	 }
	 private static List<String> parseCsvLine(String line) //處理引號內的逗號與跳脫引號
	 {
	        List<String> result = new ArrayList<>();
	        boolean inQuotes = false;
	        StringBuilder sb = new StringBuilder();
	        for (int i = 0; i < line.length(); i++)
	        {
	            char c = line.charAt(i);
	            if (c == '"')
	            {
	                if (inQuotes)
	                {	                    
	                    if (i + 1 < line.length() && line.charAt(i + 1) == '"')// 遇到 "" 代表一個真實的雙引號
	                    {
	                        sb.append('"');
	                        i++; //跳過下一個引號
	                    }
	                    else
	                    {
	                        inQuotes = false; // 結束引號區塊
	                    }
	                }
	                else
	                {
	                    inQuotes = true; // 開始引號區塊
	                }
	            }
	            else if (c == ',' && !inQuotes)//遇到逗號且不在引號內，代表欄位結束
	            {                
	                result.add(sb.toString());
	                sb.setLength(0);
	            }
	            else
	            {
	                sb.append(c);
	            }
	        }
	        result.add(sb.toString()); //加入最後一個欄位
	        return result;
	    }
}
