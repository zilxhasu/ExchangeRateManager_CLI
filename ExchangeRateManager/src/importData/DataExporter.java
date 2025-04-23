package importData;

import com.google.gson.Gson;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DataExporter {

    // 匯出至 CSV
    public void exportToCsv(List<ExchangeRate> exchangeRates, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            for (ExchangeRate exchangeRate : exchangeRates) {
                fileWriter.append(exchangeRate.getDate())
                        .append(",")
                        .append(String.valueOf(exchangeRate.getRateNTD()))
                        .append(",")
                        .append(String.valueOf(exchangeRate.getRateCNH()))
                        .append(",")
                        .append(String.valueOf(exchangeRate.getRateJPY()))
                        .append(",")
                        .append(String.valueOf(exchangeRate.getRateKRW()))
                        .append(",")
                        .append(String.valueOf(exchangeRate.getRateSGD()))
                        .append(",")
                        .append(String.valueOf(exchangeRate.getRateEUR()))
                        .append(",")
                        .append(String.valueOf(exchangeRate.getRateGBP()))
                        .append(",")
                        .append(String.valueOf(exchangeRate.getRateAUD()))
                        .append("\n");
            }
            System.out.println("資料成功匯出為 CSV 格式");
        } catch (IOException e) {
            System.err.println("匯出至 CSV 時發生錯誤: " + e.getMessage());
        }
    }

    // 匯出至 JSON
    public void exportToJson(List<ExchangeRate> exchangeRates, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            Gson gson = new Gson();
            gson.toJson(exchangeRates, fileWriter);
            System.out.println("資料成功匯出為 JSON 格式");
        } catch (IOException e) {
            System.err.println("匯出至 JSON 時發生錯誤: " + e.getMessage());
        }
    }

    // 匯出至 XML
    public void exportToXml(List<ExchangeRate> exchangeRates, String filePath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element rootElement = doc.createElement("ExchangeRates");
            doc.appendChild(rootElement);

            for (ExchangeRate exchangeRate : exchangeRates) {
                Element rateElement = doc.createElement("ExchangeRate");
                rootElement.appendChild(rateElement);

                addElement(doc, rateElement, "Date", exchangeRate.getDate());
                addElement(doc, rateElement, "RateNTD", String.valueOf(exchangeRate.getRateNTD()));
                addElement(doc, rateElement, "RateCNH", String.valueOf(exchangeRate.getRateCNH()));
                addElement(doc, rateElement, "RateJPY", String.valueOf(exchangeRate.getRateJPY()));
                addElement(doc, rateElement, "RateKRW", String.valueOf(exchangeRate.getRateKRW()));
                addElement(doc, rateElement, "RateSGD", String.valueOf(exchangeRate.getRateSGD()));
                addElement(doc, rateElement, "RateEUR", String.valueOf(exchangeRate.getRateEUR()));
                addElement(doc, rateElement, "RateGBP", String.valueOf(exchangeRate.getRateGBP()));
                addElement(doc, rateElement, "RateAUD", String.valueOf(exchangeRate.getRateAUD()));
            }

            writeDocumentToFile(doc, filePath);
            System.out.println("資料成功匯出為 XML 格式");
        } catch (Exception e) {
            System.err.println("匯出至 XML 時發生錯誤: " + e.getMessage());
        }
    }

    // 幫助方法：將資料添加到 XML 中
    private void addElement(Document doc, Element parent, String tagName, String value) {
        Element element = doc.createElement(tagName);
        element.appendChild(doc.createTextNode(value));
        parent.appendChild(element);
    }

    // 幫助方法：將 XML 轉換並寫入檔案
    private void writeDocumentToFile(Document doc, String filePath) throws Exception {
        javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(doc);
        java.io.StringWriter writer = new java.io.StringWriter();
        javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(writer);
        transformer.transform(source, result);
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(writer.toString());
        }
    }
}
