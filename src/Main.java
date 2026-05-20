public class Main {

    public static void main(String[] args) {

        SystemConfiguration config =SystemConfiguration.getInstance();

        config.setRiskLimit(4.0);
        
        config.setActiveStrategy("LONG_TERM");
        TradingFacade facade = new TradingFacade();
        facade.startTrading();
    }
}
