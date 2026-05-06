public class MarketData {
    private String symbol;
    private double open;
    private double high;
    private double low;
    private double close;
    private int volume;
    private String timestamp;

    public MarketData(String symbol, double open, double high, double low, double close, int volume, String timestamp) {
        this.symbol = symbol;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.timestamp = timestamp;
    }

    public String getSymbol() { return symbol; }
    public double getOpen() { return open; }
    public double getHigh() { return high; }
    public double getLow() { return low; }
    public double getClose() { return close; }
    public int getVolume() { return volume; }
    public String getTimestamp() { return timestamp; }
    
    
    public double getPrice() { return close; } 

    @Override
    public String toString() {
        return "MarketData{" +
                "symbol='" + symbol + '\'' +
                ", close=" + close +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
