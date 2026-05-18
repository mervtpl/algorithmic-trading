public class TradeCommand implements Command {
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
        broker.Action(type, symbol, price);
    }

    public void unExecute() {
        broker.Action(Undo(type), symbol, price);
    }

    private String Undo(String type) {
        switch (type) {
            case "BUY": return "SELL";
            case "SELL": return "BUY";
            default:  return "HOLD";
        }
    }
}