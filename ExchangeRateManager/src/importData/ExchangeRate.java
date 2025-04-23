package importData;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

//可以被序列化，這對於保存到檔案或者傳遞過網路有用
public class ExchangeRate implements Serializable {

    private String date;

    // 使用 @JsonProperty 註解來映射 JSON 中的特殊欄位名稱
    @JsonProperty("新台幣（匯率）")
    private double rateNTD;

    @JsonProperty("人民幣（匯率）")
    private double rateCNH;

    @JsonProperty("日圓（匯率）")
    private double rateJPY;

    @JsonProperty("韓元（匯率）")
    private double rateKRW;

    @JsonProperty("新加坡元（匯率）")
    private double rateSGD;

    @JsonProperty("歐元（匯率）")
    private double rateEUR;

    @JsonProperty("英鎊（匯率）")
    private double rateGBP;

    @JsonProperty("澳幣（匯率）")
    private double rateAUD;

    // 無參構造函數 在使用 JSON ， Jackson 需要一個無參的構造函數來實例化物件
    public ExchangeRate() {
        super();
    }

    // 帶參構造函數（用於 CSV 解析或直接初始化對象時）
    public ExchangeRate(String date, double rateNTD, double rateCNH, double rateJPY, double rateKRW,
                        double rateSGD, double rateEUR, double rateGBP, double rateAUD) {
        this.date = date;
        this.rateNTD = rateNTD;
        this.rateCNH = rateCNH;
        this.rateJPY = rateJPY;
        this.rateKRW = rateKRW;
        this.rateSGD = rateSGD;
        this.rateEUR = rateEUR;
        this.rateGBP = rateGBP;
        this.rateAUD = rateAUD;
    }

    // 獲取日期
    @JsonProperty("月別")
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // 取得新台幣匯率
    @JsonProperty("新台幣（匯率）")
    public double getRateNTD() {
        return rateNTD;
    }

    public void setRateNTD(double rateNTD) {
        this.rateNTD = rateNTD;
    }

    // 取得人民幣匯率
    @JsonProperty("人民幣（匯率）")
    public double getRateCNH() {
        return rateCNH;
    }

    public void setRateCNH(double rateCNH) {
        this.rateCNH = rateCNH;
    }

    // 取得日圓匯率
    @JsonProperty("日圓（匯率）")
    public double getRateJPY() {
        return rateJPY;
    }

    public void setRateJPY(double rateJPY) {
        this.rateJPY = rateJPY;
    }

    // 取得韓元匯率
    @JsonProperty("韓元（匯率）")
    public double getRateKRW() {
        return rateKRW;
    }

    public void setRateKRW(double rateKRW) {
        this.rateKRW = rateKRW;
    }

    // 取得新加坡元匯率
    @JsonProperty("新加坡元（匯率）")
    public double getRateSGD() {
        return rateSGD;
    }

    public void setRateSGD(double rateSGD) {
        this.rateSGD = rateSGD;
    }

    // 取得歐元匯率
    @XmlElement(name = "歐元（匯率）")
    public double getRateEUR() {
        return rateEUR;
    }

    public void setRateEUR(double rateEUR) {
        this.rateEUR = rateEUR;
    }

    // 取得英鎊匯率
    @JsonProperty("英鎊（匯率）")
    public double getRateGBP() {
        return rateGBP;
    }

    public void setRateGBP(double rateGBP) {
        this.rateGBP = rateGBP;
    }

    // 取得澳幣匯率
    @JsonProperty("澳幣（匯率）")

    public double getRateAUD() {
        return rateAUD;
    }

    public void setRateAUD(double rateAUD) {
        this.rateAUD = rateAUD;
    }

    // 內部異常類別
    public static class InvalidRateFormatException extends Exception {
        public InvalidRateFormatException(String message) {
            super(message);
        }
    }

    // 處理數字轉換的輔助方法
    private double parseRate(Object rate) throws InvalidRateFormatException {
        if (rate instanceof String) {
            try {
                return Double.parseDouble((String) rate);  // 將字串轉為 Double
            } catch (NumberFormatException e) {
                throw new InvalidRateFormatException("無法解析匯率: " + rate);  // 如果無法解析，拋出異常
            }
        } else if (rate instanceof Double) {
            return (Double) rate;  // 如果已經是 Double，直接賦值
        } else {
            throw new InvalidRateFormatException("無效的匯率格式: " + rate);  // 如果是其他格式，拋出異常
        }


    }

    // 使用通用的 getRate 方法來獲取匯率
    public double getRate(String currencyType) throws InvalidRateFormatException {
        switch (currencyType) {
            case "NTD":
                return this.rateNTD;
            case "CNH":
                return this.rateCNH;
            case "JPY":
                return this.rateJPY;
            case "KRW":
                return this.rateKRW;
            case "SGD":
                return this.rateSGD;
            case "EUR":
                return this.rateEUR;
            case "GBP":
                return this.rateGBP;
            case "AUD":
                return this.rateAUD;
            default:
                throw new InvalidRateFormatException("無效的貨幣類型: " + currencyType);
        }
    }
    private static void addNewExchangeRate(Connection connection, String date, Scanner scanner) {
    }

    private static boolean isValidDate(String date) {
        return false;
    }

    public void updateExchangeRate(Connection connection, String date, Scanner scanner) {
    }
    public static void exportData(List<ExchangeRate> exchangeRatesToExport, int formatChoice) {
    }

    @Override
    public String toString() {
        return String.format("ExchangeRate{date='%s', rateNTD=%.2f, rateCNH=%.4f, rateJPY=%.2f, rateKRW=%.1f, rateSGD=%.4f, rateEUR=%.4f, rateGBP=%.4f, rateAUD=%.4f}",
                date, rateNTD, rateCNH, rateJPY, rateKRW, rateSGD, rateEUR, rateGBP, rateAUD);
    }
}

