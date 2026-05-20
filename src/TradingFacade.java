public class TradingFacade {
    private MarketStream marketStream;
    private Broker broker;
    private TradeManager tradeManager;
    private DataReaderFactory factory;
    private MarketDataReader reader;
    private TradingStrategy strategy;

    public TradingFacade() {
        SystemConfiguration config = SystemConfiguration.getInstance();

        marketStream = new MarketStream();
        marketStream.attach(new ChartDashboard());
        marketStream.attach(new ProfitLossCalculator(config.getEntryPrice(), config.getRiskLimit() / 100.0));

        broker = new Broker();
        tradeManager = new TradeManager();

        if (config.getDataFilePath().endsWith(".json")) {
            factory = new JsonReaderFactory();
        } else {
            factory = new TabularReaderFactory();
        }
        reader = factory.createReader();

        if(config.getActiveStrategy().equals("SHORT_TERM")) {
            strategy = new ShortTermStrategy("AAPL", broker, tradeManager);
        } else {
            strategy = new LongTermStrategy("AAPL", broker, tradeManager);
        }

        strategy.setMarketStream(marketStream);
    }

    public void startTrading() {
        System.out.println(" STOCK TRADING SYSTEM STARTING ");

        MarketDataCollection dataCollection = reader.readData();

        strategy.check(dataCollection);

        System.out.println(" END OF DAY TAX REPORT ");
        broker.printTaxReport();

        System.out.println(" UNDO LAST TRANSACTION ");
        tradeManager.undo(1);
    }
}