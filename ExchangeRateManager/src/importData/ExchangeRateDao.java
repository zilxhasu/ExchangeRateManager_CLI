package importData;

import com.google.gson.Gson;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import util.JDBCutil;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class ExchangeRateDao {

    // 新增靜態變數來檢查是否已顯示過連線狀態
    private static boolean isConnectionChecked = false;

    // 檢查是否已經存在相同的日期  方法外部進行資料庫連接
    public boolean isDateExists(Connection connection, String date) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ExchangeRate WHERE date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, date);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    // 儲存匯率資料（插入或更新）
    public void saveExchangeRate(ExchangeRate exchangeRate) {
        try (Connection connection = JDBCutil.getConnection()) {
            if (isDateExists(connection, exchangeRate.getDate())) {
                updateExchangeRate(connection, exchangeRate); // 更新匯率
            } else {
                insertExchangeRate(connection, exchangeRate); // 插入匯率
            }
        } catch (SQLException e) {
            System.err.println("資料庫操作錯誤: " + e.getMessage());
            e.printStackTrace();
        }
    }



    // 新增匯率資料
    public void insertExchangeRate(Connection connection, ExchangeRate exchangeRate) throws SQLException {
        String sql = "INSERT INTO ExchangeRate(date, rateNTD, rateCNH, rateJPY, rateKRW, rateSGD, rateEUR, rateGBP, rateAUD) "
                + "VALUES(?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, exchangeRate.getDate());
            preparedStatement.setDouble(2, exchangeRate.getRateNTD());
            preparedStatement.setDouble(3, exchangeRate.getRateCNH());
            preparedStatement.setDouble(4, exchangeRate.getRateJPY());
            preparedStatement.setDouble(5, exchangeRate.getRateKRW());
            preparedStatement.setDouble(6, exchangeRate.getRateSGD());
            preparedStatement.setDouble(7, exchangeRate.getRateEUR());
            preparedStatement.setDouble(8, exchangeRate.getRateGBP());
            preparedStatement.setDouble(9, exchangeRate.getRateAUD());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                // 插入後不顯示即時訊息，僅顯示總結
            } else {
                System.err.println("沒有插入任何資料");
            }
        }
    }


    //刪除匯率資料
    //依照日期
    public void deleteExchangeRate(String date) {
        String sql = "DELETE FROM ExchangeRate WHERE date = ?";
        Connection connection = JDBCutil.getConnection(); // 確保連線

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, date);
            int rowsAffected = preparedStatement.executeUpdate(); // 使用 executeUpdate() 执行删除操作
            if (rowsAffected > 0) {
                System.out.println("刪除匯率資料，日期為：" + date);
            } else {
                System.out.println("沒有找到指定日期的匯率資料進行刪除");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCutil.closeResource(connection, preparedStatement, null);
        }
    }


    // 修改匯率資料
    public void updateExchangeRate(Connection connection, ExchangeRate exchangeRate) throws SQLException {
        String sql = "UPDATE ExchangeRate SET rateNTD = ?, rateCNH = ?, rateJPY = ?, rateKRW = ?, rateSGD = ?, rateEUR = ?, rateGBP = ?, rateAUD = ? "
                + "WHERE date = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDouble(1, exchangeRate.getRateNTD());
            preparedStatement.setDouble(2, exchangeRate.getRateCNH());
            preparedStatement.setDouble(3, exchangeRate.getRateJPY());
            preparedStatement.setDouble(4, exchangeRate.getRateKRW());
            preparedStatement.setDouble(5, exchangeRate.getRateSGD());
            preparedStatement.setDouble(6, exchangeRate.getRateEUR());
            preparedStatement.setDouble(7, exchangeRate.getRateGBP());
            preparedStatement.setDouble(8, exchangeRate.getRateAUD());
            preparedStatement.setString(9, exchangeRate.getDate());

            int rowsAffected = preparedStatement.executeUpdate();
            // 不在此顯示訊息，統一在 DownloadData 顯示更新總結
        }
    }

    // 查詢所有匯率資料
    public List<ExchangeRate> findAllExchangeRate(Connection connection) throws SQLException {
        List<ExchangeRate> exchangeRates = new ArrayList<>();

        // 確保查詢語句不為 null
        String sql = "SELECT * FROM ExchangeRate";  // 確保這裡的查詢語句正確
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    ExchangeRate exchangeRate = new ExchangeRate(
                            resultSet.getString("date"),
                            resultSet.getFloat("rateNTD"),
                            resultSet.getFloat("rateCNH"),
                            resultSet.getFloat("rateJPY"),
                            resultSet.getFloat("rateKRW"),
                            resultSet.getFloat("rateSGD"),
                            resultSet.getFloat("rateEUR"),
                            resultSet.getFloat("rateGBP"),
                            resultSet.getFloat("rateAUD")
                    );
                    exchangeRates.add(exchangeRate);
                }
            }
        }
        return exchangeRates;
    }

    // 查詢指定日期範圍的匯率資料
    public List<ExchangeRate> findExchangeRateByDateRange(Connection connection, String startDate, String endDate) throws SQLException {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        String sql = "SELECT * FROM ExchangeRate WHERE date BETWEEN ? AND ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, startDate);
            preparedStatement.setString(2, endDate);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    ExchangeRate exchangeRate = new ExchangeRate(
                            resultSet.getString("date"),
                            resultSet.getFloat("rateNTD"),
                            resultSet.getFloat("rateCNH"),
                            resultSet.getFloat("rateJPY"),
                            resultSet.getFloat("rateKRW"),
                            resultSet.getFloat("rateSGD"),
                            resultSet.getFloat("rateEUR"),
                            resultSet.getFloat("rateGBP"),
                            resultSet.getFloat("rateAUD")
                    );
                    exchangeRates.add(exchangeRate);
                }
            }
        }
        return exchangeRates;
    }


    //輸出csv
    public void writeToCsv(String date, Double rateNTD, Double rateCNH, Double rateJPY, Double rateKRW, Double rateSGD, Double rateEUR, Double rateGBP, Double rateAUD) {
        FileWriter fileWriter = null;
        try {
            //指定csv路徑（true表示追加模式）
            fileWriter = new FileWriter("/Users/zilxhasu/Documents/exchangeRate.csv", true);
            //建構csv每行資料
            StringBuilder sb = new StringBuilder();
            sb.append(date).append(",");
            sb.append(rateNTD).append(",");
            sb.append(rateCNH).append(",");
            sb.append(rateJPY).append(",");
            sb.append(rateKRW).append(",");
            sb.append(rateSGD).append(",");
            sb.append(rateEUR).append(",");
            sb.append(rateGBP).append(",");
            sb.append(rateAUD).append("\n");

            //寫入資料到檔案
            fileWriter.write(sb.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 匯出 JSON
    public void writeToJson(List<ExchangeRate> exchangeRates, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            Gson gson = new Gson();
            gson.toJson(exchangeRates, fileWriter); //將 exchangeRates 轉換為 JSON 並寫入到 fileWriter。
            System.out.println("匯率資料已成功匯出為 JSON 檔案！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 匯出 XML
    public void writeToXml(List<ExchangeRate> exchangeRates, String filePath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            //創建 XML 的根元素 <ExchangeRates>，加到 Document 物件中
            Element rootElement = doc.createElement("ExchangeRates");
            doc.appendChild(rootElement);

            for (ExchangeRate exchangeRate : exchangeRates) {
                Element rateElement = doc.createElement("ExchangeRate");
                rootElement.appendChild(rateElement);

                Element dateElement = doc.createElement("Date");
                dateElement.appendChild(doc.createTextNode(exchangeRate.getDate()));
                rateElement.appendChild(dateElement);

                Element ntdElement = doc.createElement("RateNTD");
                ntdElement.appendChild(doc.createTextNode(String.valueOf(exchangeRate.getRateNTD())));
                rateElement.appendChild(ntdElement);

                Element cnhElement = doc.createElement("RateCNH");
                cnhElement.appendChild(doc.createTextNode(String.valueOf(exchangeRate.getRateCNH())));
                rateElement.appendChild(cnhElement);

                Element jpyElement = doc.createElement("RateJPY");
                jpyElement.appendChild(doc.createTextNode(String.valueOf(exchangeRate.getRateJPY())));
                rateElement.appendChild(jpyElement);

                Element krwElement = doc.createElement("RateKRW");
                krwElement.appendChild(doc.createTextNode(String.valueOf(exchangeRate.getRateKRW())));
                rateElement.appendChild(krwElement);

                Element sgdElement = doc.createElement("RateSGD");
                sgdElement.appendChild(doc.createTextNode(String.valueOf(exchangeRate.getRateSGD())));
                rateElement.appendChild(sgdElement);

                Element eurElement = doc.createElement("RateEUR");
                eurElement.appendChild(doc.createTextNode(String.valueOf(exchangeRate.getRateEUR())));
                rateElement.appendChild(eurElement);

                Element gbpElement = doc.createElement("RateGBP");
                gbpElement.appendChild(doc.createTextNode(String.valueOf(exchangeRate.getRateGBP())));
                rateElement.appendChild(gbpElement);

                Element audElement = doc.createElement("RateAUD");
                audElement.appendChild(doc.createTextNode(String.valueOf(exchangeRate.getRateAUD())));
                rateElement.appendChild(audElement);
            }

            // 寫入到文件
            try (FileWriter fileWriter = new FileWriter(filePath)) {
                fileWriter.write(convertDocumentToString(doc));
            }

            System.out.println("匯率資料已成功匯出為 XML 檔案！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //將 Document 物件轉換為 XML 格式的字符串
    private String convertDocumentToString(Document doc) throws Exception {
        javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        //DOMSource 和 StreamResult 分別是將 Document 物件和轉換結果（字符串）與轉換器連接的方式
        javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(doc);
        java.io.StringWriter writer = new java.io.StringWriter();
        javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(writer);
        transformer.transform(source, result);
        return writer.toString();
    }
    // 方法: 驗證日期格式是否正確
    public static boolean isValidDateFormat(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        dateFormat.setLenient(false);  // 不允許寬鬆的解析
        try {
            dateFormat.parse(date);
            return true;  // 日期格式正確
        } catch (ParseException e) {
            return false;  // 日期格式錯誤
        }

    }
    public static String getFileExtension(String filePath) {
            // Check if the file path is not empty and contains a dot (.)
            if (filePath != null && filePath.lastIndexOf('.') > 0) {
                return filePath.substring(filePath.lastIndexOf('.') + 1).toLowerCase();
            }
            return "";  // Return an empty string if the file path doesn't have an extension
    }


}
