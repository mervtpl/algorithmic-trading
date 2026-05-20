import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.io.IOException;

public interface MarketDataReader {
    MarketDataCollection readData();
}

class MarketData {
    private String symbol;
    private double open;
    private double high;
    private double low;
    private double close;
    private int volume;
    private String timestamp;

    public MarketData(String symbol, double open, double high, double low, double close, int volume, String timestamp) {
        this.symbol = symbol; this.open = open; this.high = high; this.low = low; this.close = close; this.volume = volume; this.timestamp = timestamp;
    }

    public String getSymbol() { return symbol; }
    public double getOpen() { return open; }
    public double getHigh() { return high; }
    public double getLow() { return low; }
    public double getClose() { return close; }
    public int getVolume() { return volume; }
    public String getTimestamp() { return timestamp; }
    public double getPrice() { return close; }

    @Override
    public String toString() {
        return "MarketData{symbol='" + symbol + "', close=" + close + ", timestamp='" + timestamp + "'}";
    }
}

interface MarketDataIterator {
    boolean hasNext();
    void next();
    MarketData current();
}

interface MarketDataCollection {
    MarketDataIterator createIterator();
}

class StandardMarketDataCollection implements MarketDataCollection {
    private List<MarketData> dataList;
    public StandardMarketDataCollection(List<MarketData> dataList) { this.dataList = dataList; }
    @Override
    public MarketDataIterator createIterator() { return new StandardMarketDataIterator(dataList); }
}

class StandardMarketDataIterator implements MarketDataIterator {
    private List<MarketData> dataList;
    private int index = 0;
    public StandardMarketDataIterator(List<MarketData> dataList) { this.dataList = dataList; }
    @Override
    public boolean hasNext() { return index < dataList.size(); }
    @Override
    public void next() {
        if(hasNext()) index++;
        else System.out.println("End of data");
    }
    @Override
    public MarketData current() { return dataList.get(index); }
}

class Json {
    private String filePath;
    public Json(String filePath) { this.filePath = filePath; }

    public String fetchRawJson() {
        try {
            java.io.File file = new java.io.File(filePath);
            if (!file.exists()) {
                String alternativePath = filePath.startsWith("src/") ? filePath.substring(4) : "src/" + filePath;
                file = new java.io.File(alternativePath);
            }
            System.out.println("[API] Reading data from: " + file.getAbsolutePath());
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            System.err.println("Error reading JSON file at " + filePath + ": " + e.getMessage());
            return "[{\"symbol\": \"AAPL\", \"open\": 180.50, \"high\": 183.50, \"low\": 179.20, \"close\": 182.50, \"volume\": 12500, \"timestamp\": \"2025-04-23T00:00:00Z\"}]";
        }
    }
}

class Tabular {
    private String filePath;
    public Tabular(String filePath) { this.filePath = filePath; }

    public Object[][] fetchTabularData() {
        try {
            java.io.File file = new java.io.File(filePath);
            if (!file.exists()) {
                String alternativePath = filePath.startsWith("src/") ? filePath.substring(4) : "src/" + filePath;
                file = new java.io.File(alternativePath);
            }
            System.out.println("[Tabular] Reading data from: " + file.getAbsolutePath());
            List<String> lines = Files.readAllLines(file.toPath());
            Object[][] data = new Object[lines.size() - 1][6];
            for (int i = 1; i < lines.size(); i++) {
                String[] columns = lines.get(i).split(",");
                data[i-1][0] = columns[0];
                data[i-1][1] = Double.parseDouble(columns[1]);
                data[i-1][2] = Double.parseDouble(columns[2]);
                data[i-1][3] = Double.parseDouble(columns[3]);
                data[i-1][4] = Double.parseDouble(columns[4]);
                data[i-1][5] = Integer.parseInt(columns[5]);
            }
            return data;
        } catch (IOException e) {
            System.err.println("Error reading CSV file at " + filePath + ": " + e.getMessage());
            return new Object[][] { {"2025-04-23T00:00:00Z", 180.00, 183.50, 179.20, 182.50, 12500} };
        }
    }
}

class JsonDataAdapter implements MarketDataReader {
    private Json json;
    public JsonDataAdapter(Json json) { this.json = json; }

    @Override
    public MarketDataCollection readData() {
        String rawJson = json.fetchRawJson();
        System.out.println("[Adapter] Converting JSON string to MarketData format...");
        List<MarketData> list = new ArrayList<>();
        try {
            String normalized = rawJson.replace("{", "\n{\n").replace("}", "\n}\n").replace(",", ",\n");
            String[] lines = normalized.split("\n");
            String symbol = "";
            double open = 0, high = 0, low = 0, close = 0;
            int volume = 0; String timestamp = "";

            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("\"symbol\"")) symbol = extractValue(line);
                else if (line.startsWith("\"open\"")) open = Double.parseDouble(extractValue(line));
                else if (line.startsWith("\"high\"")) high = Double.parseDouble(extractValue(line));
                else if (line.startsWith("\"low\"")) low = Double.parseDouble(extractValue(line));
                else if (line.startsWith("\"close\"")) close = Double.parseDouble(extractValue(line));
                else if (line.startsWith("\"volume\"")) volume = Integer.parseInt(extractValue(line));
                else if (line.startsWith("\"timestamp\"")) timestamp = extractValue(line);
                else if (line.startsWith("}")) {
                    if (!symbol.isEmpty()) {
                        list.add(new MarketData(symbol, open, high, low, close, volume, timestamp));
                        symbol = ""; open = 0; high = 0; low = 0; close = 0; volume = 0; timestamp = "";
                    }
                }
            }
        } catch (Exception e) {
            list.add(new MarketData("AAPL", 180.50, 183.50, 179.20, 182.50, 12500, "2025-04-23T00:00:00Z"));
        }
        return new StandardMarketDataCollection(list);
    }

    private String extractValue(String line) {
        int colonIndex = line.indexOf(":");
        if (colonIndex == -1) return "";
        String val = line.substring(colonIndex + 1).trim();
        if (val.endsWith(",")) val = val.substring(0, val.length() - 1).trim();
        if (val.startsWith("\"") && val.endsWith("\"")) val = val.substring(1, val.length() - 1);
        return val;
    }
}

class TabularDataAdapter implements MarketDataReader {
    private Tabular table;
    private String defaultSymbol;

    public TabularDataAdapter(Tabular table, String defaultSymbol) {
        this.table = table;
        this.defaultSymbol = defaultSymbol;
    }

    @Override
    public MarketDataCollection readData() {
        Object[][] rawData = table.fetchTabularData();
        System.out.println("Converting Tabular DB data to MarketData format...");
        List<MarketData> list = new ArrayList<>();
        for (Object[] row : rawData) {
            list.add(new MarketData(defaultSymbol, (Double) row[1], (Double) row[2], (Double) row[3], (Double) row[4], (Integer) row[5], (String) row[0]));
        }
        return new StandardMarketDataCollection(list);
    }
}