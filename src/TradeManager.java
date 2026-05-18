import java.util.ArrayList;

public class TradeManager {
    private int current;
    private ArrayList<Command> commands = new ArrayList<Command>();
    public TradeManager(){
        current = 0;
    }

    public void Redo(int levels){
        System.out.println("Redo "+levels+" levels ");
        for(int i =0; i<levels; i++){
            if(current < commands.size()){
                Command command = commands.get(current++);
                command.execute();
            }
        }
    }
    public void Undo(int levels){
        System.out.println("Undo "+levels+" levels ");
        for(int i =0; i< levels; i++){
            if(current > 0){
                Command command = commands.get(--current);
                command.unExecute();
            }
        }
    }
    public void Compute(Command command){
        command.execute();
        commands.add(command);
        current++;
    }
}
 interface Command {
    public void execute();
    public void unExecute();
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
