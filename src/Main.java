//MERVE TOPAL
//DAMLA İNCEBIYIK
//İPEK GÜZEL
//NURİYE RÜMEYSA GÜL
//Algorithmic Trading System

public class Main {
    public static void main(String[] args) {
        SystemConfiguration config = SystemConfiguration.getInstance();
        config.setRiskLimit(4.0);
        config.setActiveStrategy("LONG_TERM");
        TradingFacade tradingSystem = new TradingFacade();
        tradingSystem.startTrading();
    }
}

