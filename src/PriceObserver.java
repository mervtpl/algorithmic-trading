public interface PriceObserver {
    void update(MarketData data);
}

class ChartDashboard implements PriceObserver {
    @Override
    public void update(MarketData data) {
        System.out.println("[Chart Dashboard] Grafik Guncellendi -> " + data.getSymbol() + " Yeni Fiyat: $" + data.getClose() + " (Hacim: " + data.getVolume() + ")");
    }
}

class ProfitLossCalculator implements PriceObserver {
    private double entryPrice; 
    private double maxLossThreshold; 

    public ProfitLossCalculator(double entryPrice, double maxLossThreshold) {
        this.entryPrice = entryPrice;
        this.maxLossThreshold = maxLossThreshold; 
    }

    @Override
    public void update(MarketData data) {
        double currentPrice = data.getClose();
        double pnlPercentage = ((currentPrice - entryPrice) / entryPrice);
        
        System.out.println("[Profit/Loss Calculator] Anlik Kar/Zarar: %" + String.format("%.2f", pnlPercentage * 100));

        if (pnlPercentage <= -maxLossThreshold) {
            System.out.println("!!! RISK UYARISI: Maksimum zarar limiti (%" + (maxLossThreshold * 100) + ") asildi! (Seviye: YUKSEK). Lütfen SELL emri ile pozisyonu kapatin. !!!");
        }
    }
}