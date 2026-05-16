public class TaxCalculatorVisitor implements PortfolioVisitor {
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