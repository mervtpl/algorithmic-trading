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

    public void acceptVisitor(PortfolioVisitor visitor) {
        cashAsset.accept(visitor);
        for (StockAsset stock : stockAssets) {
            stock.accept(visitor);
        }
    }
}
