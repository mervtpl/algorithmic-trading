import java.util.ArrayList;
import java.util.List;

public class TradeManager {
    private int current;
    private ArrayList<Command> commands = new ArrayList<Command>();

    public TradeManager(){
        current = 0;
    }

    public void redo(int levels){
        System.out.println("Redo "+levels+" levels ");
        for(int i =0; i<levels; i++){
            if(current < commands.size()){
                Command command = commands.get(current++);
                command.execute();
            }
        }
    }

    public void undo(int levels){
        System.out.println("Undo "+levels+" levels ");
        for(int i =0; i< levels; i++){
            if(current > 0){
                Command command = commands.get(--current);
                command.unExecute();
            }
        }
    }

    public void compute(Command command){
        command.execute();
        commands.add(command);
        current++;
    }
}

interface Command {
    void execute();
    void unExecute();
}

class TradeCommand implements Command {
    private Broker broker;
    private String type;
    private String symbol;
    private double price;

    public TradeCommand(Broker broker, String type, String symbol, double price) {
        this.broker = broker;
        this.type = type;
        this.symbol = symbol;
        this.price = price;
    }

    public void execute() {
        broker.action(type, symbol, price);
    }

    public void unExecute() {
        broker.action(undoType(type), symbol, price);
    }

    private String undoType(String type) {
        switch (type) {
            case "BUY": return "SELL";
            case "SELL": return "BUY";
            default:  return "HOLD";
        }
    }
}

class Broker {
    private CashAsset cashAsset;
    private List<StockAsset> stockAssets;

    public Broker(){
        cashAsset = new CashAsset(10000.0);
        stockAssets = new ArrayList<>();
    }

    public void action(String type, String symbol, double price){
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
    public CashAsset(double amount) { this.amount = amount; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}