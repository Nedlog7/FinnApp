package ru.finnapp.ui.main.viewModels;

import androidx.lifecycle.LiveData;

import ru.finnapp.models.StockInfo;

public class FavoriteLiveData extends LiveData<StockInfo> {

    private static final FavoriteLiveData instance = new FavoriteLiveData();

    public static FavoriteLiveData get() {
        return instance;
    }

    public synchronized void updateStock(StockInfo stockInfo){
        postValue(stockInfo);
    }

}
