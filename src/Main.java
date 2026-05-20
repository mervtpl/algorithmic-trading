public class Main {
    public static void main(String[] args) {
        // Facade oluşturuluyor
        TradingFacade facade = new TradingFacade();

        // Tüm sistem facade üzerinden çalışıyor
        facade.startTrading();
    }
}
