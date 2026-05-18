public class Main {
    public static void main(String[] args) {
        System.out.println("=== BORSA İŞLEM SİSTEMİ BAŞLATILIYOR ===\n");

        // 1. OBSERVER SİSTEMİNİ KUR (Gözlemci)
        MarketStream anaYayin = new MarketStream();
        anaYayin.attach(new ChartDashboard());
        anaYayin.attach(new ProfitLossCalculator(180.0, 0.05)); // Örnek giriş fiyatı 180, zarar limiti %5

        // 2. TEMEL SİSTEMLERİ KUR (Broker ve Manager)
        Broker broker = new Broker();
        TradeManager tradeManager = new TradeManager();

        // 3. VERİ KATMANINI KUR (Factory ve Adapter)
        // Burada Abstract Factory kullanılarak bir Tabular (CSV) okuyucu üretiliyor.
        DataReaderFactory factory = new TabularReaderFactory();
        MarketDataReader reader = factory.createReader();

        // Veriyi oku (Adapter deseni sayesinde CSV verisi MarketData nesnelerine dönüşecek)
        MarketDataCollection veriKoleksiyonu = reader.readData();

        // 4. STRATEJİYİ KUR VE KÖPRÜLERİ BİRLEŞTİR (Template Method + Observer)
        ShortTermStrategy stratejim = new ShortTermStrategy("AAPL", broker, tradeManager);
        stratejim.setMarketStream(anaYayin); // Köprüyü kurduk! Fiyat çekildikçe anaYayin'a gidecek.

        System.out.println("\n--- İŞ AKIŞI (TEMPLATE METHOD) BAŞLIYOR ---");
        // 5. SİSTEMİ ÇALIŞTIR
        // check metodu sırasıyla: fetchData() -> analyzeIndicators() -> calculateRisk() -> executeTrade() çalıştırır.
        stratejim.check(veriKoleksiyonu);

        // 6. GÜN SONU VERGİ RAPORU (Visitor Pattern Testi)
        System.out.println("\n--- GÜN SONU VERGİ RAPORU ---");
        TaxCalculatorVisitor taxVisitor = new TaxCalculatorVisitor();
        broker.acceptVisitor(taxVisitor); // Ziyaretçiyi içeri aldık

        // 7. KOMUT GERİ ALMA (Command Pattern Testi)
        System.out.println("\n--- SON İŞLEMİ GERİ ALMA (UNDO) ---");
        tradeManager.Undo(1); // 1 adım geri al (parametre eklendi)
    }
}