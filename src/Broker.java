public class Broker {
    private double current_balance;
    public Broker(){
        current_balance = 10000.0;
    }

    public void Action(String type, String symbol,double price){
        switch(type){
            case "BUY" : current_balance -= price;
            break;
            case "SELL" : current_balance +=price;
            break;
        }
        System.out.println("Action executed: "+type+" "+symbol+" at "+price);
        System.out.println("Current balance: "+current_balance);
    }
}
