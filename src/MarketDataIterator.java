import java.util.List;

public interface MarketDataIterator {
    boolean hasNext();
    MarketData next();
}

interface MarketDataCollection {
    MarketDataIterator createIterator();
}

class StandardMarketDataCollection implements MarketDataCollection {
    private List<MarketData> dataList;

    public StandardMarketDataCollection(List<MarketData> dataList) {
        this.dataList = dataList;
    }

    @Override
    public MarketDataIterator createIterator() {
        return new StandardMarketDataIterator(dataList);
    }
}

class StandardMarketDataIterator implements MarketDataIterator {
    private List<MarketData> dataList;
    private int position = 0;

    public StandardMarketDataIterator(List<MarketData> dataList) {
        this.dataList = dataList;
    }

    @Override
    public boolean hasNext() {
        return position < dataList.size();
    }

    @Override
    public MarketData next() {
        return dataList.get(position++);
    }
}
