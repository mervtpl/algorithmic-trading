import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

// Hedef arayüz (Target Interface)
public interface MarketDataReader {
    MarketDataCollection readData();
}

// --- Adaptee 1: External JSON API  ---
class Json {
    private String filePath;

    public Json(String filePath) {
        this.filePath = filePath;
    }

    public String fetchRawJson() {
        try {
            java.io.File file = new java.io.File(filePath);
            if (!file.exists()) {
                // Eğer src/ ile başlamıyorsa src/ ekleyip dene, src/ ile başlıyorsa src/ kaldırıp dene
                String alternativePath = filePath.startsWith("src/") ? filePath.substring(4) : "src/" + filePath;
                file = new java.io.File(alternativePath);
            }
            
            System.out.println("[API] Reading data from: " + file.getAbsolutePath());
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            System.err.println("Error reading JSON file at " + filePath + ": " + e.getMessage());
            // Fallback to hardcoded sample if file fails
            return "[{\"symbol\": \"AAPL\", \"open\": 180.50, \"high\": 183.50, \"low\": 179.20, \"close\": 182.50, \"volume\": 12500, \"timestamp\": \"2025-04-23T00:00:00Z\"}]";
        }
    }
}

// --- Adaptee 2: CSV (Tabular) ---
class Tabular {
    private String filePath;

    public Tabular(String filePath) {
        this.filePath = filePath;
    }

    public Object[][] fetchTabularData() {
        try {
            java.io.File file = new java.io.File(filePath);
            if (!file.exists()) {
                String alternativePath = filePath.startsWith("src/") ? filePath.substring(4) : "src/" + filePath;
                file = new java.io.File(alternativePath);
            }
            
            System.out.println("[Tabular] Reading data from: " + file.getAbsolutePath());
            List<String> lines = Files.readAllLines(file.toPath());
            
            // İlk satır başlık (header) olduğu için atlıyoruz, o yüzden size - 1
            Object[][] data = new Object[lines.size() - 1][6];
            
            for (int i = 1; i < lines.size(); i++) {
                String[] columns = lines.get(i).split(",");
                data[i-1][0] = columns[0]; // Timestamp
                data[i-1][1] = Double.parseDouble(columns[1]); // Open
                data[i-1][2] = Double.parseDouble(columns[2]); // High
                data[i-1][3] = Double.parseDouble(columns[3]); // Low
                data[i-1][4] = Double.parseDouble(columns[4]); // Close
                data[i-1][5] = Integer.parseInt(columns[5]); // Volume
            }
            return data;
            
        } catch (IOException e) {
            System.err.println("Error reading CSV file at " + filePath + ": " + e.getMessage());
            // Fallback to hardcoded sample if file fails
            return new Object[][] {
                {"2025-04-23T00:00:00Z", 180.00, 183.50, 179.20, 182.50, 12500},
                {"2025-04-24T00:00:00Z", 183.00, 185.80, 181.40, 184.90, 9800}
            };
        }
    }
}

// --- Adapter 1: JSON Adapter ---
class JsonDataAdapter implements MarketDataReader {
    private Json json;

    public JsonDataAdapter(Json json) {
        this.json = json;
    }

    @Override
    public MarketDataCollection readData() {
        String rawJson = json.fetchRawJson();
        System.out.println("[Adapter] Converting JSON string to MarketData format...");
        
        List<MarketData> list = new ArrayList<>();
        try {
            // Normalleştirme: Süslü parantezleri ve virgülleri satır satır bölünebilecek şekilde düzenleyelim
            String normalized = rawJson.replace("{", "\n{\n")
                                       .replace("}", "\n}\n")
                                       .replace(",", ",\n");
            String[] lines = normalized.split("\n");
            
            String symbol = "";
            double open = 0, high = 0, low = 0, close = 0;
            int volume = 0;
            String timestamp = "";
            
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("\"symbol\"")) {
                    symbol = extractValue(line);
                } else if (line.startsWith("\"open\"")) {
                    open = Double.parseDouble(extractValue(line));
                } else if (line.startsWith("\"high\"")) {
                    high = Double.parseDouble(extractValue(line));
                } else if (line.startsWith("\"low\"")) {
                    low = Double.parseDouble(extractValue(line));
                } else if (line.startsWith("\"close\"")) {
                    close = Double.parseDouble(extractValue(line));
                } else if (line.startsWith("\"volume\"")) {
                    volume = Integer.parseInt(extractValue(line));
                } else if (line.startsWith("\"timestamp\"")) {
                    timestamp = extractValue(line);
                } else if (line.startsWith("}")) {
                    if (!symbol.isEmpty()) {
                        list.add(new MarketData(symbol, open, high, low, close, volume, timestamp));
                        // Reset fields for the next object
                        symbol = ""; open = 0.0; high = 0.0; low = 0.0; close = 0.0; volume = 0; timestamp = "";
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("JSON line-by-line parsing error: " + e.getMessage());
            // Fallback in case of failure
            list.add(new MarketData("AAPL", 180.50, 183.50, 179.20, 182.50, 12500, "2025-04-23T00:00:00Z"));
        }
        
        return new StandardMarketDataCollection(list);
    }

    private String extractValue(String line) {
        int colonIndex = line.indexOf(":");
        if (colonIndex == -1) return "";
        String val = line.substring(colonIndex + 1).trim();
        if (val.endsWith(",")) {
            val = val.substring(0, val.length() - 1).trim();
        }
        if (val.startsWith("\"") && val.endsWith("\"")) {
            val = val.substring(1, val.length() - 1);
        }
        return val;
    }
}

// --- Adapter 2: Table Adapter ---
class TabularDataAdapter implements MarketDataReader {
    private Tabular table;
    private String defaultSymbol; // Tabular datada sembol olmadigi icin bir varsayilan deger tutulabilir

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
            String timestamp = (String) row[0];
            double open = (Double) row[1];
            double high = (Double) row[2];
            double low = (Double) row[3];
            double close = (Double) row[4];
            int volume = (Integer) row[5];
            
            list.add(new MarketData(defaultSymbol, open, high, low, close, volume, timestamp));
        }
        
        return new StandardMarketDataCollection(list);
    }
}

