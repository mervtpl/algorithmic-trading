import java.util.ArrayList;
import java.util.List;

public class MarketStream {
    private List<PriceObserver> observers = new ArrayList<>();
    private MarketData currentData;

    public void attach(PriceObserver observer) {
        observers.add(observer);
    }

    public void detach(PriceObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (PriceObserver observer : observers) {
            observer.update(currentData);
        }
    }

    public void pushNewPrice(MarketData data) {
        this.currentData = data;
        System.out.println("\n[MarketStream] Yeni fiyat verisi sisteme aktarildi: " + data.getSymbol() + " - $" + data.getClose());
        notifyObservers();
    }
}