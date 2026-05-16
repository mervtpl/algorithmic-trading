public class StockAsset implements PortfolioAsset {
    private String symbol;
    private int quantity;
    private double currentPrice;

    public StockAsset(String symbol, int quantity, double currentPrice) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.currentPrice = currentPrice;
    }

    public double getValue() { return quantity * currentPrice; }
    public String getSymbol() { return symbol; }

    @Override
    public void accept(PortfolioVisitor visitor) {
        visitor.visit(this);
    }
}