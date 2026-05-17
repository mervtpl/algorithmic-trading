// Creator sınıfı
public abstract class DataReaderFactory {
    // Factory Method
    public abstract MarketDataReader createReader();
}

// Concrete Creator 1
class JsonReaderFactory extends DataReaderFactory {
    @Override
    public MarketDataReader createReader() {
        // Dosya yolu Singleton'dan gelebilir, şimdilik statik veriyoruz
        return new JsonDataAdapter(new Json("src/market_data.json"));
    }
}

// Concrete Creator 2
class DatabaseReaderFactory extends DataReaderFactory {
    @Override
    public MarketDataReader createReader() {
        // Tabular veride hisse sembolü varsayılan olarak AAPL kabul ediliyor
        return new TabularDataAdapter(new Tabular("src/market_data.csv"), "AAPL");
    }
}
