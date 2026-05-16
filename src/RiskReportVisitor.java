public class RiskReportVisitor implements PortfolioVisitor {
    private double totalPortfolioValue = 0;

    @Override
    public void visit(StockAsset stock) {
        double value = stock.getValue();
        totalPortfolioValue += value;
        System.out.println("[Risk Report] Hisse Varligi Eklendi: " + stock.getSymbol() + " | Toplam Deger: $" + value);
    }

    @Override
    public void visit(CashAsset cash) {
        totalPortfolioValue += cash.getAmount();
        System.out.println("[Risk Report] Nakit Varligi Eklendi: $" + cash.getAmount());
    }

    public double getTotalValue() {
        return totalPortfolioValue;
    }
}