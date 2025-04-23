package util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class JDBCutil {


    // 靜態方法，用來獲取資料庫連線
    public static Connection getConnection() {
        Connection connection = null; // 用來儲存資料庫連線物件
        try {
            // 使用 getResourceAsStream 加載配置文件
            // 注意：請確保你的配置文件在 classpath 中
            InputStream inputStream = JDBCutil.class.getClassLoader().getResourceAsStream("jdbc.properties");

            // 如果文件未找到，則拋出異常
            if (inputStream == null) {
                throw new IOException("jdbc.properties file not found in classpath.");
            }

            // 加載屬性文件
            Properties properties = new Properties();
            properties.load(inputStream);

            // 從 properties 物件中讀取資料庫的設定
            String user = properties.getProperty("user"); // 取得資料庫使用者名稱
            String password = properties.getProperty("password"); // 取得資料庫密碼
            String url = properties.getProperty("url"); // 取得資料庫 URL

            // 使用 DriverManager 取得資料庫連線
            connection = DriverManager.getConnection(url, user, password);
            boolean status = !connection.isClosed();
            System.out.println("連線狀態:"+status);
        } catch (IOException e) {
            System.err.println("讀取配置檔案錯誤: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("資料庫連接錯誤: " + e.getMessage());
            e.printStackTrace();
        }

        return connection;
    }

    // 關閉資料庫資源（Connection, Statement, ResultSet）
    public static void closeResource(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) resultSet.close();  // 關閉 ResultSet
            if (statement != null) statement.close();  // 關閉 Statement
            if (connection != null) connection.close(); // 關閉 Connection
        } catch (SQLException e) {
            System.err.println("關閉資源時發生錯誤：" + e.getMessage());
            e.printStackTrace(); // 打印錯誤信息
        }
    }
    //關閉資料庫資料（Connection, Statement）
    public static void closeResource(Connection connection,Statement statement) {
        try {
            if(connection !=null) {
                connection.close();
            }
            if(statement !=null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 單獨關閉資料庫連線
    public static void closeResource(Connection connection) {
        try {
            if (connection != null) {
                connection.close(); // 關閉資料庫連線
                System.out.println("資料庫連線已關閉");
            }
        } catch (SQLException e) {
            System.err.println("關閉連線時發生錯誤：" + e.getMessage());
            e.printStackTrace(); // 打印錯誤信息
        }
    }

}
