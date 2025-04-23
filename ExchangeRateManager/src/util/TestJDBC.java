package util;

import java.sql.Connection;

public class TestJDBC {
    public static void main(String[] args) {
        // 嘗試連接資料庫
        Connection connection = null;

        try {
            connection = JDBCutil.getConnection(); // 獲取連線

            if (connection != null) {
                System.out.println("資料庫連線成功！");
            } else {
                System.out.println("資料庫連線失敗！");
            }
        } catch (Exception e) {
            System.err.println("發生異常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            // 確保連線資源得到關閉
            JDBCutil.closeResource(connection); // 只關閉連線
        }
    }

}
