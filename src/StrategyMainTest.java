public class StrategyMainTest {
    public static void main(String[] args) {

        System.out.println("###################################################");
        System.out.println("#      ALGORITMIK TICARET SIMULASYONU BASLADI     #");
        System.out.println("###################################################\n");

        // ==========================================================
        // 1. KISIM: VERİ KATMANI (Data Architect - Arkadaşının Kısmı)
        // ==========================================================
        System.out.println("=== 1. KISIM: Veriler Sisteme Cekiliyor ===");

        // JSON formatından veri çekmek için Factory ve Adapter kullanılıyor
        DataReaderFactory jsonFactory = new JsonReaderFactory();
        MarketDataReader jsonReader = jsonFactory.createReader();

        System.out.println("JSON kaynagindan veriler okutuluyor...");
        MarketDataCollection myDataCollection = jsonReader.readData();
        System.out.println("Veriler basariyla MarketDataCollection formatina donusturuldu.\n");


        // ==========================================================
        // 2. KISIM: STRATEJİ VE ANALİZ (Backend Developer - Senin Kısmın)
        // ==========================================================
        System.out.println("=== 2. KISIM: Analiz ve Strateji Motoru Calistiriliyor ===");

        // Ortak nesneleri olusturuyoruz (Command Pattern: Receiver ve Invoker)
        Broker broker = new Broker();
        TradeManager tradeManager = new TradeManager();

        // --- TEST A: KISA VADELI STRATEJİ ---
        TradingStrategy shortTermStrategy = new ShortTermStrategy("AAPL", broker, tradeManager);
        System.out.println("\n[TEST] " + shortTermStrategy.getSymbol() + " icin Kisa Vadeli (Short-Term) strateji uygulaniyor...");
        shortTermStrategy.check(myDataCollection); // Template Method cagiriliyor (Fetch Data -> Analyze Indicators -> Calculate Risk -> Execute Trade)

        // --- TEST B: UZUN VADELI STRATEJİ ---
        TradingStrategy longTermStrategy = new LongTermStrategy("AAPL", broker, tradeManager);
        System.out.println("\n[TEST] " + longTermStrategy.getSymbol() + " icin Uzun Vadeli (Long-Term) strateji uygulaniyor...");
        longTermStrategy.check(myDataCollection); // Ayni iskelet, farkli hesaplama adimlari

        // ==========================================================
        // 3. KISIM: İŞLEM GEÇMİŞİ VE GERİ ALMA (Command Pattern Undo)
        // ==========================================================
        System.out.println("\n=== 3. KISIM: Islem Gecmisi ve Geri Alma (Undo) ===");
        // Son yapilan islemi geri aliyoruz
        tradeManager.Undo(1);

        System.out.println("\n###################################################");
        System.out.println("#              SISTEM BASARIYLA KAPANDI           #");
        System.out.println("###################################################");
    }
}