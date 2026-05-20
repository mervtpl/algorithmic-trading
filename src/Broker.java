import java.util.ArrayList;
import java.util.List;

public class Broker {
    private CashAsset cashAsset;
    private List<StockAsset> stockAssets;

    public Broker(){
        cashAsset = new CashAsset(10000.0);
        stockAssets = new ArrayList<>();
    }

    public void Action(String type, String symbol, double price){
        switch(type){
            case "BUY" : 
                cashAsset.setAmount(cashAsset.getAmount() - price);
                stockAssets.add(new StockAsset(symbol, 1, price));
                break;
            case "SELL" : 
                cashAsset.setAmount(cashAsset.getAmount() + price);
                for (int i = 0; i < stockAssets.size(); i++) {
                    if (stockAssets.get(i).getSymbol().equals(symbol)) {
                        stockAssets.remove(i);
                        break;
                    }
                }
                break;
        }
        System.out.println("Action executed: "+type+" "+symbol+" at "+price);
        System.out.println("Current balance: "+cashAsset.getAmount());
    }

    public double calculateTotalValueAndPrintReport() {
        double total = cashAsset.getAmount();
        System.out.println("[Risk Report] Cash Asset Added: $" + cashAsset.getAmount());
        for (StockAsset stock : stockAssets) {
            double value = stock.getValue();
            total += value;
            System.out.println("[Risk Report] Stock Asset Added: " + stock.getSymbol() + " | Total Value: $" + value);
        }
        return total;
    }

    public void printTaxReport() {
        System.out.println("[Tax Calculator] Cash tax deduction: $0");
        for (StockAsset stock : stockAssets) {
            double tax = stock.getValue() * 0.02;
            System.out.println("[Tax Calculator] Tax calculated for " + stock.getSymbol() + ": $" + tax);
        }
    }
}

class StockAsset {
    private String symbol;
    private int quantity;
    private double currentPrice;

    public StockAsset(String symbol, int quantity, double currentPrice) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.currentPrice = currentPrice;
    }

    public double getValue() { return quantity * currentPrice; }
    public String getSymbol() { return symbol; }
}

class CashAsset {
    private double amount;

    public CashAsset(double amount) {
        this.amount = amount;
    }

    public double getAmount() { return amount; }
    
    public void setAmount(double amount) { this.amount = amount; }
}
