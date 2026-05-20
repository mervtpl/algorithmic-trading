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

    // Singleton configuration object
    SystemConfiguration config = SystemConfiguration.getInstance();

    // Observerlar ekleniyor
    marketStream = new MarketStream();
    marketStream.attach(new ChartDashboard());
    marketStream.attach(   new ProfitLossCalculator(180.0,config.getRiskLimit() / 100.0) );

    broker = new Broker();
    tradeManager = new TradeManager();
    factory = new TabularReaderFactory();
    reader = factory.createReader();

    // Strategy seçimi Singleton üzerinden yapılıyor
    if(config.getActiveStrategy().equals("SHORT_TERM")) {
        strategy = new ShortTermStrategy("AAPL",broker,tradeManager);
    } else {
        strategy = new LongTermStrategy("AAPL",broker,tradeManager);
    }

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
        TaxCalculatorVisitor taxVisitor = new TaxCalculatorVisitor();
        broker.acceptVisitor(taxVisitor);

        System.out.println(" UNDO LAST TRANSACTION ");
        tradeManager.Undo(1);
    }
}
