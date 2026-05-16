public interface PortfolioAsset {
    void accept(PortfolioVisitor visitor);
}