public class TradingFacade {
    // Observer sistemi
    private MarketStream marketStream;
    // Trade işlemleri
    private Broker broker;
    // Command manager
    private TradeManager tradeManager;
    // Factory pattern
    private DataReaderFactory factory;
    // Adapter pattern reader
    private MarketDataReader reader;
    // Strategy pattern
    private TradingStrategy strategy;
  
    public TradingFacade() {
        // Observerlar ekleniyor
        marketStream = new MarketStream();
        marketStream.attach(new ChartDashboard());
        marketStream.attach(new ProfitLossCalculator(180.0,0.05));

        // Sistemler oluşturuluyor
        broker = new Broker();
        tradeManager = new TradeManager();

        // Factory ile reader oluşturuluyor
        factory = new TabularReaderFactory();
        reader = factory.createReader();

        // Strategy oluşturuluyor
        strategy = new ShortTermStrategy("AAPL", broker, tradeManager);

        // Observer bağlantısı kuruluyor
        strategy.setMarketStream(marketStream);
    }
    // Facade method
    public void startTrading() {
        System.out.println(" STOCK TRADING SYSTEM STARTING ");

        // Veri okunuyor
        MarketDataCollection dataCollection = reader.readData();

        // Strategy çalıştırılıyor
        strategy.check(dataCollection);
        System.out.println( " END OF DAY TAX REPORT ");

        // Visitor pattern
        TaxCalculatorVisitor taxVisitor = new TaxCalculatorVisitor();
        broker.acceptVisitor(taxVisitor);

        System.out.println(" UNDO LAST TRANSACTION ");
        // Command undo
        tradeManager.Undo(1);
    }
}
