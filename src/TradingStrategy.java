import java.util.ArrayList;
import java.util.List;


interface Indicator {
    double calculate(List<Double> priceHistory, int period);
}

class SMAIndicator implements Indicator {
    @Override
    public double calculate(List<Double> priceHistory, int period) {
        if (priceHistory.isEmpty()) return 0.0;
        int limit = Math.min(period, priceHistory.size());
        double sum = 0.0;
        for (int i = priceHistory.size() - limit; i < priceHistory.size(); i++) {
            sum += priceHistory.get(i);
        }
        return sum / limit;
    }
}

public abstract class TradingStrategy {

    protected String symbol;
    protected double currentPrice;
    protected Broker broker;
    protected TradeManager tradeManager;
    protected List<Double> priceHistory = new ArrayList<>();
    protected MarketStream marketStream;
    protected Indicator indicator;

    public void setMarketStream(MarketStream stream){
        this.marketStream=stream;
    }

    public String getSymbol(){
        return symbol;
    }


    public void check(MarketDataCollection dataCollection){
        fetchData(dataCollection);
        String decision = analyzeIndicators();
        decision = calculateRisk(decision);
        executeTrade(decision);
    }

    protected void fetchData(MarketDataCollection dataCollection){
        System.out.println("Fetching market data for "+symbol+"...");
        priceHistory.clear();
        MarketDataIterator iterator = dataCollection.createIterator();
        while (iterator.hasNext()) {
            MarketData data = iterator.current();
            iterator.next();
            currentPrice = data.getClose();
            priceHistory.add(currentPrice);
            if (marketStream != null) {
                marketStream.pushNewPrice(data);
            }
        }
    }

    protected void executeTrade(String decision) {
        if(decision.equals("HOLD")) {
            System.out.println("Trade conditions not met. Holding position.");
            return;
        }
        System.out.println("Trade conditions evaluated. Executing " + decision + " Order...");
        Command command = new TradeCommand(broker, decision, symbol, currentPrice);
        tradeManager.compute(command);
    }

    protected abstract String analyzeIndicators();
    protected abstract String calculateRisk(String currentDecision);
}

class ShortTermStrategy extends TradingStrategy {

    public ShortTermStrategy(String symbol, Broker broker, TradeManager manager) {
        this.symbol = symbol;
        this.broker = broker;
        this.tradeManager = manager;
        this.indicator = new SMAIndicator();
    }

    @Override
    protected String analyzeIndicators() {
        int period = 200;
        double sma = indicator.calculate(priceHistory, period);
        double threshold = SystemConfiguration.getInstance().getThresholdValue();

        System.out.println("Short-Term SMA (" + period + " mins): " + String.format("%.2f", sma) + " | Threshold: " + threshold + " | Current: " + currentPrice);

        if (currentPrice > (sma + threshold)) return "BUY";
        else return "SELL";
    }

    @Override
    protected String calculateRisk(String currentDecision) {
        double maxLossPercent = SystemConfiguration.getInstance().getRiskLimit() / 100.0;
        double entryPrice = SystemConfiguration.getInstance().getEntryPrice();
        System.out.println("Checking short-term risk limit (Max Loss: %" + SystemConfiguration.getInstance().getRiskLimit() + ")...");

        double lossPercent = (entryPrice - currentPrice) / entryPrice;
        if (lossPercent >= maxLossPercent) {
            System.out.println("!!! RISK NOTIFICATION (Level: HIGH): Position loss %" +
                    String.format("%.2f", lossPercent * 100) +
                    " exceeds max limit %" + SystemConfiguration.getInstance().getRiskLimit() +
                    ". Forcing SELL order. !!!");
            return "SELL";
        }
        return currentDecision;
    }
}

class LongTermStrategy extends TradingStrategy {

    public LongTermStrategy(String symbol, Broker broker, TradeManager manager) {
        this.symbol = symbol;
        this.broker = broker;
        this.tradeManager = manager;
        this.indicator = new SMAIndicator();
    }

    @Override
    protected String analyzeIndicators() {
        int period = 200;
        double sma = indicator.calculate(priceHistory, period);
        double threshold = SystemConfiguration.getInstance().getThresholdValue();

        System.out.println("Long-Term SMA (" + period + " days): " + String.format("%.2f", sma) + " | Threshold: " + threshold + " | Current: " + currentPrice);

        if (currentPrice > (sma + threshold)) return "BUY";
        else return "SELL";
    }

    @Override
    protected String calculateRisk(String currentDecision) {
        double maxLossPercent = SystemConfiguration.getInstance().getRiskLimit() / 100.0;
        double entryPrice = SystemConfiguration.getInstance().getEntryPrice();
        System.out.println("Checking long-term risk limit (Max Loss: %" + SystemConfiguration.getInstance().getRiskLimit() + ")...");

        double lossPercent = (entryPrice - currentPrice) / entryPrice;
        if (lossPercent >= maxLossPercent) {
            System.out.println("!!! RISK NOTIFICATION (Level: HIGH): Position loss %" +
                    String.format("%.2f", lossPercent * 100) +
                    " exceeds max limit %" + SystemConfiguration.getInstance().getRiskLimit() +
                    ". Forcing SELL order. !!!");
            return "SELL";
        }
        return currentDecision;
    }
}