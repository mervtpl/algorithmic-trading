public class ShortTermStrategy extends TradingStrategy {

    public ShortTermStrategy(String symbol, Broker broker, TradeManager manager) {
        this.symbol = symbol;
        this.broker = broker;
        this.tradeManager = manager;
    }

   @Override
    protected String analyzeIndicators(){
        int period =200;
        double sma= calculateSMA(period);
       System.out.println("Short-Term SMA (" + period + "): " + String.format("%.2f", sma) + " | Current Price: " + currentPrice);

       if(currentPrice > sma){
           return "BUY";
       }else{
           return "SELL";
       }
   }

   @Override
    protected void calculateRisk(){
       System.out.println("Checking short-term tight risk limits...");
       RiskReportVisitor riskReport = new RiskReportVisitor();
       broker.acceptVisitor(riskReport);
       System.out.println("Risk Hesaplamasi Tamamlandi. Toplam Varlik: $" + riskReport.getTotalValue());
   }

    protected double calculateSMA(int period){
        if(priceHistory.isEmpty()) {
            return 0.0;
        }
        int limit = Math.min(period,priceHistory.size());
        double sum = 0.0;

        for(int i = priceHistory.size() - limit; i < priceHistory.size(); i++){
            sum += priceHistory.get(i);
        }
        return sum/limit;
    }
}
