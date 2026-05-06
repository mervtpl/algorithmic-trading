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
class ExternalJsonAPI {
    private String filePath;

    public ExternalJsonAPI(String filePath) {
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

// --- Adaptee 2: Legacy Database (Tabular) ---
class LegacyDatabase {
    private String filePath;

    public LegacyDatabase(String filePath) {
        this.filePath = filePath;
    }

    public Object[][] executeQuery(String query) {
        try {
            java.io.File file = new java.io.File(filePath);
            if (!file.exists()) {
                String alternativePath = filePath.startsWith("src/") ? filePath.substring(4) : "src/" + filePath;
                file = new java.io.File(alternativePath);
            }
            
            System.out.println("[Legacy DB] Executing query: " + query + " (Reading from: " + file.getAbsolutePath() + ")");
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
    private ExternalJsonAPI jsonApi;

    public JsonDataAdapter(ExternalJsonAPI jsonApi) {
        this.jsonApi = jsonApi;
    }

    @Override
    public MarketDataCollection readData() {
        String rawJson = jsonApi.fetchRawJson();
        System.out.println("[Adapter] Converting JSON string to MarketData format...");
        
        // JSON Parsing simulasyonu
        List<MarketData> list = new ArrayList<>();
        list.add(new MarketData("AAPL", 180.50, 183.50, 179.20, 182.50, 12500, "2025-04-23T00:00:00Z"));
        
        return new StandardMarketDataCollection(list);
    }
}

// --- Adapter 2: Database Adapter ---
class DatabaseDataAdapter implements MarketDataReader {
    private LegacyDatabase database;
    private String defaultSymbol; // Tabular datada sembol olmadigi icin bir varsayilan deger tutulabilir

    public DatabaseDataAdapter(LegacyDatabase database, String defaultSymbol) {
        this.database = database;
        this.defaultSymbol = defaultSymbol;
    }

    @Override
    public MarketDataCollection readData() {
        Object[][] rawData = database.executeQuery("SELECT * FROM market_history");
        System.out.println("[Adapter] Converting Tabular DB data to MarketData format...");
        
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

// --- Pattern 4: Composite Adapter  ---
class CompositeMarketDataReader implements MarketDataReader {
    private List<MarketDataReader> readers = new ArrayList<>();

    public void addReader(MarketDataReader reader) {
        readers.add(reader);
    }

    @Override
    public MarketDataCollection readData() {
        List<MarketData> allData = new ArrayList<>();
        System.out.println("[Composite] Birden fazla kaynaktan (JSON, DB vb.) veri toplaniyor...");
        
        for (MarketDataReader reader : readers) {
            MarketDataCollection collection = reader.readData();
            MarketDataIterator iterator = collection.createIterator();
            while (iterator.hasNext()) {
                allData.add(iterator.next());
            }
        }
        
        return new StandardMarketDataCollection(allData);
    }
}
