public interface PortfolioVisitor {
    void visit(StockAsset stock);
    void visit(CashAsset cash);
}

class RiskReportVisitor implements PortfolioVisitor {
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

class TaxCalculatorVisitor implements PortfolioVisitor {
    private double totalTax = 0;

    @Override
    public void visit(StockAsset stock) {
        double tax = stock.getValue() * 0.02;
        totalTax += tax;
        System.out.println("[Tax Calculator] " + stock.getSymbol() + " varligi icin hesaplanan kesinti/vergi: $" + tax);
    }

    @Override
    public void visit(CashAsset cash) {
        System.out.println("[Tax Calculator] Nakit icin kesinti: $0");
    }
}