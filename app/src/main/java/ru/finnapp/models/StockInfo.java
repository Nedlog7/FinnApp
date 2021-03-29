package ru.finnapp.models;

public class StockInfo {

    private String symbol;
    private String name;
    private String logoUrl;
    private float closePrice = 0;
    private float currentPrice = 0;
    private boolean isFavorite = false;

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void setClosePrice(float closePrice) {
        this.closePrice = closePrice;
    }

    public void setCurrentPrice(float currentPrice) {
        this.currentPrice = currentPrice;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public float getClosePrice() {
        return closePrice;
    }

    public float getCurrentPrice() {
        return currentPrice;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

}
