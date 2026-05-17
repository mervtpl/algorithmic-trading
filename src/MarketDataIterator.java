import java.util.List;

public interface MarketDataIterator {
    boolean hasNext();
     void  next();
     MarketData Current();
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
    private int index = 0;

    public StandardMarketDataIterator(List<MarketData> dataList) {
        this.dataList = dataList;
    }

    @Override
    public boolean hasNext() {
        return index < dataList.size();
    }

    @Override
    public void next() {
        if(hasNext()){
            index++;
        }else{
            System.out.println("End of data");
        } 
    }

    @Override
    public MarketData Current() {
       return dataList.get(index);
    }


}
