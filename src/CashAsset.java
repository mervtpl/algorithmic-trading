public class CashAsset implements PortfolioAsset {
    private double amount;

    public CashAsset(double amount) {
        this.amount = amount;
    }

    public double getAmount() { return amount; }

    @Override
    public void accept(PortfolioVisitor visitor) {
        visitor.visit(this);
    }
}