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
