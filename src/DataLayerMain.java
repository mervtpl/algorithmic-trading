// Sadece 1. Kismin (Veri Katmani) test edilecegi Main sinifi

public class DataLayerMain {
    public static void main(String[] args) {
        System.out.println("=== 1. KISIM: VERI KATMANI VE ADAPTASYON (DATA ARCHITECT) ===\n");

        // 1. Durum: Sistem JSON formatından veri cekecek
        System.out.println("--- 1. JSON Kaynagindan Veri Okuma ---");
        DataReaderFactory jsonFactory = new JsonReaderFactory();
        MarketDataReader jsonReader = jsonFactory.createReader(); // Factory Method
        
        MarketDataCollection jsonCollection = jsonReader.readData(); // Adapter converts
        printData(jsonCollection); // Iterator traverses


        System.out.println("\n--- 2. Veritabanindan Veri Okuma ---");
        // 2. Durum: Sistem Legacy Veritabanindan (Tabular) veri cekecek
        DataReaderFactory dbFactory = new DatabaseReaderFactory();
        MarketDataReader dbReader = dbFactory.createReader(); // Factory Method
        
        MarketDataCollection dbCollection = dbReader.readData(); // Adapter converts
        printData(dbCollection); // Iterator traverses
        
        System.out.println("\n--- 3. Composite (Bilesik) Kaynaktan Veri Okuma ---");
        // Hem JSON hem DB okuyucularini birlestiriyoruz
        CompositeMarketDataReader compositeReader = new CompositeMarketDataReader();
        compositeReader.addReader(jsonFactory.createReader());
        compositeReader.addReader(dbFactory.createReader());

        MarketDataCollection compositeCollection = compositeReader.readData();
        printData(compositeCollection);
    }

    private static void printData(MarketDataCollection collection) {
        System.out.println("Koleksiyon uzerinde Iterator ile donuluyor:");
        MarketDataIterator iterator = collection.createIterator();
        while (iterator.hasNext()) {
            MarketData data = iterator.next();
            System.out.println(" -> " + data);
        }
    }
}
