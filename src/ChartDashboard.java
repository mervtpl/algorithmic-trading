public class ChartDashboard implements PriceObserver {
    @Override
    public void update(MarketData data) {
        System.out.println("[Chart Dashboard] Grafik Guncellendi -> " + data.getSymbol() + " Yeni Fiyat: $" + data.getClose() + " (Hacim: " + data.getVolume() + ")");
    }
}