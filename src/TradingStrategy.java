import java.util.ArrayList;
import java.util.List;

public abstract class TradingStrategy {

    protected String symbol;
    protected double currentPrice;
    protected Broker broker;
    protected TradeManager tradeManager;
    protected List<Double> priceHistory = new ArrayList<>();
    protected MarketStream marketStream;

    public void setMarketStream(MarketStream stream){
        this.marketStream=stream;
    }

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

        MarketDataIterator iterator = dataCollection.createIterator();
        while (iterator.hasNext()) {
            MarketData data = iterator.Current();
            iterator.next();

            currentPrice = data.getClose();
            priceHistory.add(currentPrice);

            if (marketStream != null) {
                marketStream.pushNewPrice(data);
            }
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

 class ShortTermStrategy extends TradingStrategy {

     public ShortTermStrategy(String symbol, Broker broker, TradeManager manager) {
         this.symbol = symbol;
         this.broker = broker;
         this.tradeManager = manager;
     }

     @Override
     protected String analyzeIndicators() {
         int period = 200;
         double sma = calculateSMA(period);
         System.out.println("Short-Term SMA (" + period + "): " + String.format("%.2f", sma) + " | Current Price: " + currentPrice);

         if (currentPrice > sma) {
             return "BUY";
         } else {
             return "SELL";
         }
     }

     @Override
     protected void calculateRisk() {
         System.out.println("Checking short-term tight risk limits...");
         RiskReportVisitor riskReport = new RiskReportVisitor();
         broker.acceptVisitor(riskReport);
         System.out.println("Risk Hesaplamasi Tamamlandi. Toplam Varlik: $" + riskReport.getTotalValue());
     }

     protected double calculateSMA(int period) {
         if (priceHistory.isEmpty()) {
             return 0.0;
         }
         int limit = Math.min(period, priceHistory.size());
         double sum = 0.0;

         for (int i = priceHistory.size() - limit; i < priceHistory.size(); i++) {
             sum += priceHistory.get(i);
         }
         return sum / limit;
     }
 }



class LongTermStrategy extends TradingStrategy {

    public LongTermStrategy(String symbol, Broker broker, TradeManager manager) {
        this.symbol = symbol;
        this.broker = broker;
        this.tradeManager = manager;
    }

    @Override
    protected String analyzeIndicators() {

        int period = 200;
        double sma = calculateSMA(period);
        System.out.println("Long-Term SMA (" + period + "): " + String.format("%.2f", sma) + " | Current Price: " + currentPrice);

        if (currentPrice > sma) {
            return "BUY";
        } else {
            return "SELL";
        }
    }

    @Override
    protected void calculateRisk() {
        System.out.println("Checking long-term wide risk limits...");
        RiskReportVisitor riskReport = new RiskReportVisitor();
        broker.acceptVisitor(riskReport);
        System.out.println("Risk Hesaplamasi Tamamlandi. Toplam Varlik: $" + riskReport.getTotalValue());
    }

    private double calculateSMA(int period) {
        if (priceHistory.isEmpty()){
            return 0.0;
        }

        int limit = Math.min(period, priceHistory.size());
        double sum = 0.0;

        for (int i = priceHistory.size() - limit; i < priceHistory.size(); i++) {
            sum += priceHistory.get(i);
        }
        return sum / limit;
    }
}

