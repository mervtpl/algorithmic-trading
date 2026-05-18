import java.util.ArrayList;
import java.util.List;

public abstract class TradingStrategy {

    protected String symbol;
    protected double currentPrice;
    protected Broker broker;
    protected TradeManager tradeManager;
    protected List<Double> priceHistory = new ArrayList<>();

    public String getSymbol(){
        return symbol;
    }

    public void check(MarketDataCollection dataCollection){
        fetchData(dataCollection);
        String decision = analyzeIndicators();
        calculateRisk();
        executeTrade(decision);
    }

    protected void fetchData(MarketDataCollection dataCollection){
        System.out.println("Fetching market data for "+symbol+"...");
        priceHistory.clear();

        MarketStream marketStream = new MarketStream();
        marketStream.attach(new ChartDashboard());
        boolean first = true;

        MarketDataIterator iterator = dataCollection.createIterator();
        while (iterator.hasNext()) {
            MarketData data = iterator.Current();
            iterator.next();
            currentPrice = data.getClose();
            priceHistory.add(currentPrice);

            if (first) {
                marketStream.attach(new ProfitLossCalculator(currentPrice, 0.05));
                first = false;
            }

            marketStream.pushNewPrice(data);
        }
    }
    protected void executeTrade(String decision) {
        System.out.println("Trade conditions evaluated. Executing " + decision + " Order...");
        Command command = new TradeCommand(broker, decision, symbol, currentPrice);
        tradeManager.Compute(command);
    }
    protected abstract String analyzeIndicators();
    protected abstract void calculateRisk();

}