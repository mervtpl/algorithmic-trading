public class TradingFacade {
    // Observer system
    private MarketStream marketStream;
    // Trade operations
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

    // Attaching observers
    marketStream = new MarketStream();
    marketStream.attach(new ChartDashboard());
    marketStream.attach(   new ProfitLossCalculator(180.0,config.getRiskLimit() / 100.0) );

    broker = new Broker();
    tradeManager = new TradeManager();
    if (config.getDataFilePath().endsWith(".json")) {
        factory = new JsonReaderFactory();
    } else {
        factory = new TabularReaderFactory();
    }
    reader = factory.createReader();

    // Strategy selection via Singleton
    if(config.getActiveStrategy().equals("SHORT_TERM")) {
        strategy = new ShortTermStrategy("AAPL",broker,tradeManager);
    } else {
        strategy = new LongTermStrategy("AAPL",broker,tradeManager);
    }

    // Connecting observer to strategy
    strategy.setMarketStream(marketStream);
}
    // Facade method
    public void startTrading() {
        System.out.println(" STOCK TRADING SYSTEM STARTING ");

        // Reading data
        MarketDataCollection dataCollection = reader.readData();

        // Running strategy
        strategy.check(dataCollection);
        System.out.println( " END OF DAY TAX REPORT ");
        broker.printTaxReport();

        System.out.println(" UNDO LAST TRANSACTION ");
        tradeManager.Undo(1);
    }
}
