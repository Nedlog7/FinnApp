package ru.finnapp.models;

import java.util.List;

public class StockInfoFavorite {

    private final List<StockInfo> stockInfoList;
    private final List<String> symbolList;

    public StockInfoFavorite(List<StockInfo> stockInfoList, List<String> symbolList) {
        this.stockInfoList = stockInfoList;
        this.symbolList = symbolList;
    }

    public List<StockInfo> getStockInfoList() {
        return stockInfoList;
    }

    public List<String> getSymbolList() {
        return symbolList;
    }

}
