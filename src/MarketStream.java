import java.util.ArrayList;
import java.util.List;

public class MarketStream {
    private List<PriceObserver> observers = new ArrayList<>();
    private MarketData currentData;

    public void attach(PriceObserver observer) { observers.add(observer); }
    public void detach(PriceObserver observer) { observers.remove(observer); }

    public void notifyObservers() {
        for (PriceObserver observer : observers) {
            observer.update(currentData);
        }
    }

    public void pushNewPrice(MarketData data) {
        this.currentData = data;
        System.out.println("\n[MarketStream] New price data pushed to the system: " + data.getSymbol() + " - $" + data.getClose());
        notifyObservers();
    }
}

interface PriceObserver {
    void update(MarketData data);
}

class ChartDashboard implements PriceObserver {
    @Override
    public void update(MarketData data) {
        System.out.println("[Chart Dashboard] Chart Updated -> " + data.getSymbol() + " New Price: $" + data.getClose() + " (Volume: " + data.getVolume() + ")");
    }
}

class ProfitLossCalculator implements PriceObserver {
    private double entryPrice;
    private double maxLossThreshold;

    public ProfitLossCalculator(double entryPrice, double maxLossThreshold) {
        this.entryPrice = entryPrice;
        this.maxLossThreshold = maxLossThreshold;
    }

    @Override
    public void update(MarketData data) {
        double currentPrice = data.getClose();
        double pnlPercentage = ((currentPrice - entryPrice) / entryPrice);

        System.out.println("[Profit/Loss Calculator] Current P&L: %" + String.format("%.2f", pnlPercentage * 100));

        if (pnlPercentage <= -maxLossThreshold) {
            System.out.println("!!! RISK WARNING: Maximum loss limit (%" + (maxLossThreshold * 100) + ") exceeded! (Level: HIGH). Please close the position with a SELL order. !!!");
        }
    }
}