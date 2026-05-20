// MERVE TOPAL
// RÜMEYSA GÜL
// DAMLA İNCEBIYIK
// Algorithmic Trading System

public class Main {

    public static void main(String[] args) {

        SystemConfiguration config =SystemConfiguration.getInstance();

        config.setRiskLimit(4.0);
        
        config.setActiveStrategy("LONG_TERM");
        config.setDataFilePath("src/market_data.json");
        TradingFacade facade = new TradingFacade();
        facade.startTrading();
    }
}
