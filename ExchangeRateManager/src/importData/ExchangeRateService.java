package importData;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateService {

    // 根據檔案格式來選擇不同的解析方法 //format:檔案格式csv/json
    public static List<ExchangeRate> getExchangeRateData(String filePath, String format) throws IOException {
        if (format.equalsIgnoreCase("json")) {
            return getExchangeRateDataFromJson(filePath);
        } else if (format.equalsIgnoreCase("csv")) {
            return getExchangeRateDataFromCsv(filePath);
        } else {
            throw new IllegalArgumentException("不支持的格式：" + format);
        }
    }

    // JSON 格式解析
    public static List<ExchangeRate> getExchangeRateDataFromJson(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
// 輸出檔案路徑以檢查
        System.out.println("正在讀取的檔案路徑：" + filePath);

        // 檢查檔案是否存在
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("檔案不存在: " + filePath);
        }
        // 需要處理 JSON 中的特殊字段名稱（如 "新台幣（匯率）"）
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<ExchangeRate> exchangeRates = objectMapper.readValue(
                new File(filePath),
                objectMapper.getTypeFactory().constructCollectionType(List.class, ExchangeRate.class)
        );
        return exchangeRates;
    }

    // CSV 格式解析（假設 CSV 格式是用逗號分隔）
    // 解析 CSV 格式並將資料儲存到 List<ExchangeRate>
    public static List<ExchangeRate> getExchangeRateDataFromCsv(String filePath) throws IOException {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
// 輸出檔案路徑以檢查
        System.out.println("正在讀取的檔案路徑：" + filePath);

        // 檢查檔案是否存在
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("檔案不存在: " + filePath);
        }
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;

        // 跳過標題行
        br.readLine();

        while ((line = br.readLine()) != null) {
            String[] fields = line.split(",");

            // 假設每一行資料對應到 ExchangeRate 欄位
            if (fields.length == 9) {
                try {
                    // 去除每個欄位的單位描述並解析數字
                    String date = fields[0].trim();  // 日期（去除空格）
                    double rateNTD = parseRate(fields[1]);
                    double rateCNH = parseRate(fields[2]);
                    double rateJPY = parseRate(fields[3]);
                    double rateKRW = parseRate(fields[4]);
                    double rateSGD = parseRate(fields[5]);
                    double rateEUR = parseRate(fields[6]);
                    double rateGBP = parseRate(fields[7]);
                    double rateAUD = parseRate(fields[8]);

                    // 構建 ExchangeRate 物件
                    ExchangeRate exchangeRate = new ExchangeRate(date, rateNTD, rateCNH, rateJPY, rateKRW, rateSGD, rateEUR, rateGBP, rateAUD);

                    // 添加到列表
                    exchangeRates.add(exchangeRate);
                } catch (NumberFormatException e) {
                    System.err.println("無法解析數據行: " + line);
                    e.printStackTrace();
                }
            }
        }
        br.close();
        return exchangeRates;
    }

    // 解析數字方法，處理無效數字的情況
    private static double parseRate(String rate) {
        try {
            return Double.parseDouble(rate.trim());  // 去除可能的空格並解析數字
        } catch (NumberFormatException e) {
            return 0.0;  // 如果解析失敗，返回 0.0 或者根據需求設置為其他值
        }
    }


}
