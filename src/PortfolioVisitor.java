public interface PortfolioVisitor {
    void visit(StockAsset stock);
    void visit(CashAsset cash);
}