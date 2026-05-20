// Creator sınıfı
public abstract class DataReaderFactory {
    // Factory Method
    public abstract MarketDataReader createReader();
}

// Concrete Creator 1
class JsonReaderFactory extends DataReaderFactory {
    @Override
    public MarketDataReader createReader() {
        // Dosya yolu Singleton'dan geliyor
        return new JsonDataAdapter(new Json(SystemConfiguration.getInstance().getDataFilePath()));
    }
}

// Concrete Creator 2
class TabularReaderFactory extends DataReaderFactory {
    @Override
    public MarketDataReader createReader() {
        // Dosya yolu Singleton'dan geliyor, Tabular veride hisse sembolü varsayılan olarak AAPL kabul ediliyor
        return new TabularDataAdapter(new Tabular(SystemConfiguration.getInstance().getDataFilePath()), "AAPL");
    }
}
