package importData;

import util.JDBCutil;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static importData.ExchangeRateDao.getFileExtension;
import static importData.ExchangeRateDao.isValidDateFormat;

public class DemoExchangeRate {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);  // 用來讀取用戶輸入
        ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
        ExchangeRateService exchangeRateService = new ExchangeRateService();
        Connection connection = null;

        // 在這裡定義 filePath 變數
        String filePath = "";

        try {
            // 連接資料庫
            connection = JDBCutil.getConnection();
            //程序會不斷循環，直到用戶選擇退出
            while (true) {

                // 顯示操作選單
                System.out.println("請選擇操作:");
                System.out.println("1. 新增匯率資料");
                System.out.println("2. 刪除匯率資料");
                System.out.println("3. 修改匯率資料");
                System.out.println("4. 查詢匯率資料");
                System.out.println("5. 匯出匯率資料");
                System.out.println("6. 離開");
                System.out.print("請輸入選項（1-6）: ");

                String input = scanner.nextLine();  // 讀取使用者輸入

                // 嘗試將輸入轉換為整數
                int choice;
                try {
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.err.println("無效選項，請重新輸入！");
                    continue;  // 無效輸入則跳過此次迴圈，重新顯示選單
                }
                scanner.nextLine();  // **清除緩衝區**

                // 根據選擇執行對應的操作
                switch (choice) {
                    case 1: // 新增匯率資料
                        System.out.println("請輸入匯率資料的檔案路徑:");
                        System.out.println("1. 從檔案匯入匯率資料");
                        System.out.println("2. 新增單筆匯率資料");
                        System.out.print("請輸入選項（1-2）: ");
                        String addChoiceInput = scanner.nextLine();
                        int addChoice = -1;
                        try {
                            addChoice = Integer.parseInt(addChoiceInput);  // 讀取並處理選項
                        } catch (NumberFormatException e) {
                            System.err.println("無效選項，請重新選擇！");
                            continue;  // 無效選擇，跳過本次迴圈
                        }

                        if (addChoice == 1) {
                            // 從檔案匯入匯率資料的處理流程（原來的程式碼）
                            System.out.println("請輸入檔案路徑...");
                            filePath = scanner.nextLine();
                            File file = new File(filePath);

                            // 檢查檔案是否存在
                            if (!file.exists()) {
                                System.out.println("\u001B[31m檔案路徑不存在！請重新選擇操作。\u001B[0m");
                                continue; // 這會跳過當前迴圈並重新顯示操作選單
                            }

                            // 確認格式並讀取資料
                            System.out.println("請選擇檔案格式（json/csv）:");
                            String format = scanner.nextLine().toLowerCase(); // 讀取並轉換為小寫

                            // 檢查檔案的副檔名與選擇的格式是否一致
                            String fileExtension = getFileExtension(filePath);  // 獲取檔案副檔名
                            if (!format.equals(fileExtension)) {
                                System.out.println("\u001B[31m檔案格式與選擇的格式不匹配！請確保檔案格式與輸入的格式一致。\u001B[0m");
                                continue; // 跳過本次循環，回到選單
                            }

                            // 驗證格式是否正確
                            if (!format.equals("json") && !format.equals("csv") ) {
                                System.out.println("\u001B[31m無效的檔案格式，請輸入正確的格式（json/csv/xml）！\u001B[0m");
                                continue; // 跳過本次循環，回到選單
                            }

                            List<ExchangeRate> exchangeRatesData = exchangeRateService.getExchangeRateData(filePath, format);

                            // 寫入資料庫
                            int insertCount = 0;
                            for (ExchangeRate exchangeRate : exchangeRatesData) {
                                try {
                                    if (!exchangeRateDao.isDateExists(connection, exchangeRate.getDate())) {
                                        exchangeRateDao.insertExchangeRate(connection, exchangeRate);
                                        insertCount++;
                                    }
                                } catch (Exception e) {
                                    System.err.println("Error inserting exchange rate: " + exchangeRate);
                                    e.printStackTrace();
                                }
                            }
                            System.out.println("匯率資料新增筆數: " + insertCount);
                        } else if (addChoice == 2) {
                            // 新增單筆匯率資料

                            // 輸入日期
                            System.out.print("請輸入日期（格式: yyyy-MM，例如 2023-11）: ");
                            scanner.nextLine();  // 清除緩衝區中的換行符
                            String date = scanner.nextLine();  // 使用 nextLine() 確保正確讀取日期

                            // 驗證日期格式
                            if (!isValidDateFormat(date)) {
                                System.out.println("\u001B[31m日期格式錯誤，請重新輸入！\u001B[0m");
                                continue;  // 返回主菜單
                            }

                            // 檢查資料是否已存在
                            if (exchangeRateDao.isDateExists(connection, date)) {
                                // 如果資料已經存在，跳轉至更新功能（選項 3）
                                System.out.println("\u001B[33m該日期的匯率資料已存在，將跳轉至更新功能...\u001B[0m");

                                // 提示輸入新的匯率資料
                                System.out.print("請輸入新的匯率資料（以空格分隔 NTD, CNH, JPY, KRW, SGD, EUR, GBP, AUD）: ");
                                String[] newRates = scanner.nextLine().split(" ");

                                // 確保資料格式正確
                                if (newRates.length != 8) {
                                    System.out.println("\u001B[31m資料格式錯誤，請再次輸入！\u001B[0m");
                                    break;
                                }

                                // 更新匯率資料
                                try {
                                    ExchangeRate updatedRate = new ExchangeRate(
                                            date,
                                            Double.parseDouble(newRates[0]),
                                            Double.parseDouble(newRates[1]),
                                            Double.parseDouble(newRates[2]),
                                            Double.parseDouble(newRates[3]),
                                            Double.parseDouble(newRates[4]),
                                            Double.parseDouble(newRates[5]),
                                            Double.parseDouble(newRates[6]),
                                            Double.parseDouble(newRates[7])
                                    );

                                    // 呼叫更新方法
                                    exchangeRateDao.updateExchangeRate(connection, updatedRate);
                                    System.out.println("匯率資料已更新: " + updatedRate);
                                } catch (NumberFormatException e) {
                                    System.err.println("匯率資料格式錯誤，請確認數值輸入正確！");
                                }
                            } else {
                                // 如果資料不存在，則新增
                                System.out.print("請輸入匯率資料（以空格分隔 NTD, CNH, JPY, KRW, SGD, EUR, GBP, AUD）: ");
                                String[] rates = scanner.nextLine().split(" ");  // 使用 nextLine() 確保正確讀取匯率資料

                                // 確保資料格式正確
                                if (rates.length != 8) {
                                    System.err.println("資料格式錯誤，請再次輸入！");
                                    break;
                                }

                                // 新增匯率資料
                                try {
                                    ExchangeRate newRate = new ExchangeRate(
                                            date,
                                            Double.parseDouble(rates[0]),
                                            Double.parseDouble(rates[1]),
                                            Double.parseDouble(rates[2]),
                                            Double.parseDouble(rates[3]),
                                            Double.parseDouble(rates[4]),
                                            Double.parseDouble(rates[5]),
                                            Double.parseDouble(rates[6]),
                                            Double.parseDouble(rates[7])
                                    );
                                    exchangeRateDao.saveExchangeRate(newRate);  // 使用 save 方法新增資料
                                    System.out.println("新增匯率資料成功: " + newRate);
                                } catch (NumberFormatException e) {
                                    System.err.println("匯率資料格式錯誤，請確認數值輸入正確！");
                                }
                            }
                        }
                        break;

                    case 2: // 刪除匯率資料
                        System.out.print("請輸入要刪除的日期（例如 2023-11）: ");
                        String deleteDate = scanner.nextLine();
                        // 檢查日期格式
                        if (!isValidDateFormat(deleteDate)) {
                            System.out.println("\u001B[31m日期格式錯誤，請重新輸入！\u001B[0m");
                            break;  // 停止繼續執行，讓使用者重新輸入
                        }
                        exchangeRateDao.deleteExchangeRate(deleteDate);
                        System.out.println("匯率資料已刪除: " + deleteDate);
                        break;

                    case 3: // 更新匯率資料
                        System.out.print("請輸入要[更新/修改]的日期（例如 2023-11）: ");
                        String updateDate = scanner.nextLine();

                        // 檢查日期格式
                        if (!isValidDateFormat(updateDate)) {
                            System.out.println("\u001B[31m日期格式錯誤，請重新輸入（格式應為 yyyy-MM，例如 2023-11）！\u001B[0m");
                            break;  // 停止繼續執行，讓使用者重新輸入
                        }
                        // 檢查該日期資料是否存在
                        if (!exchangeRateDao.isDateExists(connection, updateDate)) {
                            System.out.println("\u001B[31m該日期的匯率資料不存在，無法更新！\u001B[0m");
                            break;  // 停止執行，讓使用者重新輸入
                        }

                        // 輸入匯率資料
                        System.out.print("請輸入新匯率資料（以空格分隔 NTD, CNH, JPY, KRW, SGD, EUR, GBP, AUD）: ");
                        String[] newRates = scanner.nextLine().split(" ");
                        if (newRates.length != 8) {
                            System.err.println("資料格式錯誤，請再次輸入！");
                            break;
                        }

                        // 更新匯率資料
                        ExchangeRate updatedRate = new ExchangeRate(
                                updateDate,
                                Double.parseDouble(newRates[0]),
                                Double.parseDouble(newRates[1]),
                                Double.parseDouble(newRates[2]),
                                Double.parseDouble(newRates[3]),
                                Double.parseDouble(newRates[4]),
                                Double.parseDouble(newRates[5]),
                                Double.parseDouble(newRates[6]),
                                Double.parseDouble(newRates[7])
                        );

                        exchangeRateDao.updateExchangeRate(connection, updatedRate);
                        System.out.println("匯率資料已更新: " + updatedRate);

                        break;

                    case 4: // 查詢匯率資料
                        System.out.println("請選擇查詢方式:");
                        System.out.println("1. 查詢所有匯率資料");
                        System.out.println("2. 查詢指定日期區間資料");
                        System.out.print("請輸入選項（1-2）: ");
                        String queryChoiceInput = scanner.nextLine();
                        int queryChoice = -1;
                        try {
                            queryChoice = Integer.parseInt(queryChoiceInput);  // 讀取並處理選項
                        } catch (NumberFormatException e) {
                            System.err.println("無效選項，請重新選擇！");
                            continue;  // 無效選擇，跳過本次迴圈
                        }

                        if (queryChoice == 1) {
                            // 查詢所有匯率資料
                            List<ExchangeRate> exchangeRateList = exchangeRateDao.findAllExchangeRate(connection);
                            if (exchangeRateList.isEmpty()) {
                                System.out.println("目前沒有匯率資料。");
                            } else {
                                System.out.println("所有匯率資料:");
                                for (ExchangeRate exchangeRate : exchangeRateList) {
                                    System.out.println(exchangeRate);
                                }
                            }
                        } else if (queryChoice == 2) {
                            // 查詢指定日期區間的匯率資料
                            // 查詢指定日期區間的匯率資料
                            String startDate = null;
                            String endDate = null;

                            // 檢查起始日期
                            while (true) {
                                System.out.print("請輸入查詢起始日期（格式: yyyy-MM，如 2023-11）: ");
                                startDate = scanner.nextLine();
                                if (isValidDateFormat(startDate)) {
                                    break;
                                } else {
                                    System.out.println("\u001B[31m日期格式錯誤，請使用 yyyy-MM 格式！\u001B[0m");
                                }
                            }

                            // 檢查結束日期
                            while (true) {
                                System.out.print("請輸入查詢結束日期（格式: yyyy-MM，如 2024-11）: ");
                                endDate = scanner.nextLine();
                                if (isValidDateFormat(endDate)) {
                                    break;
                                } else {
                                    System.out.println("\u001B[31m日期格式錯誤，請使用 yyyy-MM 格式！\u001B[0m");
                                }
                            }

                            // 檢查日期範圍是否有效
                            if (startDate.compareTo(endDate) > 0) {
                                System.out.println("\u001B[31m錯誤：起始日期不能晚於結束日期！\u001B[0m");
                                break;  // 讓使用者重新輸入
                            }

                            // 查詢指定日期範圍的匯率資料
                            List<ExchangeRate> exchangeRatesInRange = exchangeRateDao.findExchangeRateByDateRange(connection, startDate, endDate);
                            if (exchangeRatesInRange.isEmpty()) {
                                System.out.println("在此日期範圍內沒有匯率資料！");
                            } else {
                                System.out.println("查詢結果:");
                                for (ExchangeRate exchangeRate : exchangeRatesInRange) {
                                    System.out.println(exchangeRate);
                                }
                            }
                        } else {
                            System.out.println("無效選項，請重新選擇！");
                        }
                        break;

                    case 5: // 匯出匯率資料至指定格式

                        System.out.println("您可以選擇匯出資料的格式，請輸入選項:");
                        System.out.println("1. 匯出所有資料");
                        System.out.println("2. 匯出指定日期範圍資料");
                        System.out.print("請輸入選項（1-2）: ");
                        String exportChoiceInput = scanner.nextLine();
                        int exportChoice = -1;
                        try {
                            exportChoice = Integer.parseInt(exportChoiceInput);  // 讀取並處理選項
                        } catch (NumberFormatException e) {
                            System.err.println("無效選項，請重新選擇！");
                            break;
                        }



                        // 選擇匯出資料的範圍
                        List<ExchangeRate> exchangeRatesToExport = new ArrayList<>();
                        if (exportChoice == 1) {
                            // 匯出所有資料
                            exchangeRatesToExport = exchangeRateDao.findAllExchangeRate(connection);
                            if (exchangeRatesToExport.isEmpty()) {
                                System.out.println("沒有匯率資料可以匯出！");
                                break;
                            }
                        } else if (exportChoice == 2) {
                            // 匯出指定日期範圍資料
                            System.out.print("請輸入查詢起始日期（例如： 2023-11）: ");
                            String startDate = scanner.nextLine();
                            System.out.print("請輸入查詢結束日期（例如： 2024-11）: ");
                            String endDate = scanner.nextLine();

                            // 查詢指定日期範圍的匯率資料
                            exchangeRatesToExport = exchangeRateDao.findExchangeRateByDateRange(connection, startDate, endDate);
                            if (exchangeRatesToExport.isEmpty()) {
                                System.out.println("在此日期範圍內沒有匯率資料可以匯出！");
                                break;
                            }
                        } else {
                            System.out.println("無效選項，請重新選擇！");
                            break;
                        }

                        // 顯示匯出格式選擇
                        System.out.println("請選擇匯出格式:");
                        System.out.println("1. CSV 格式");
                        System.out.println("2. JSON 格式");
                        System.out.println("3. XML 格式");
                        System.out.print("請輸入選項（1-3）: ");
                        String formatChoiceInput = scanner.nextLine();
                        int formatChoice = -1;
                        int attemptCount = 0;
                        while (attemptCount < 3) {
                            try {
                                // 顯示選項
                                System.out.println("請選擇匯出格式:");
                                System.out.println("1. CSV 格式");
                                System.out.println("2. JSON 格式");
                                System.out.println("3. XML 格式");

                                // 讀取用戶輸入
                                formatChoice = Integer.parseInt(formatChoiceInput);

                                if (formatChoice >= 1 && formatChoice <= 3) {
                                    break; // 正確選項，退出迴圈
                                } else {
                                    System.out.println("無效選項，請重新選擇！");
                                    attemptCount++;
                                    if (attemptCount >= 3) {
                                        System.err.println("錯誤次數過多，將返回主菜單...");
                                        return; // 跳回主菜單
                                    }
                                    // 提示用戶再次輸入正確的選項
                                    formatChoiceInput = scanner.nextLine(); // 再次輸入
                                }
                            } catch (NumberFormatException e) {
                                // 顯示錯誤信息並重新提示選項
                                System.out.println("無效的輸入，請輸入數字選項！");
                                attemptCount++;
                                if (attemptCount >= 3) {
                                    System.err.println("錯誤次數過多，將返回主菜單...");
                                    break; // 跳回主菜單
                                }
                                // 提示用戶再次輸入正確的選項
                                formatChoiceInput = scanner.nextLine(); // 再次輸入
                            }
                        }


                        // 根據格式選擇匯出資料
                        switch (formatChoice) {
                            case 1: // CSV 格式
                                filePath = "/Users/zilxhasu/Documents/exchangeRate.csv";
                                for (ExchangeRate exchangeRate : exchangeRatesToExport) {
                                    exchangeRateDao.writeToCsv(
                                            exchangeRate.getDate(),
                                            exchangeRate.getRate("NTD"),
                                            exchangeRate.getRate("CNH"),
                                            exchangeRate.getRate("JPY"),
                                            exchangeRate.getRate("KRW"),
                                            exchangeRate.getRate("SGD"),
                                            exchangeRate.getRate("EUR"),
                                            exchangeRate.getRate("GBP"),
                                            exchangeRate.getRate("AUD")
                                    );
                                }
                                System.out.println("匯率資料已成功匯出到 CSV 檔案！");
                                break;

                            case 2: // JSON 格式
                                filePath = "/Users/zilxhasu/Documents/exchangeRate.json";
                                exchangeRateDao.writeToJson(exchangeRatesToExport, filePath);
                                break;

                            case 3: // XML 格式
                                filePath = "/Users/zilxhasu/Documents/exchangeRate.xml";
                                exchangeRateDao.writeToXml(exchangeRatesToExport, filePath);
                                break;

                            default:
                                System.out.println("無效選項，請重新選擇！");
                        }
                        break;


                    case 6: // 離開
                        System.out.println("感謝使用，程序結束！");
                        scanner.close();
                        return;  // 結束程式

                    default:
                        System.err.println("無效選項，請重新輸入。");
                }
            }
        } catch (Exception e) {
            System.err.println("資料庫操作錯誤: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 確保連線關閉
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (scanner != null) {
                scanner.close();
            }
        }

    }
}