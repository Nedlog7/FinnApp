package ru.finnapp.ui.main.viewModels;

import androidx.lifecycle.ViewModel;

public class FavoriteViewModel extends ViewModel {

    private final FavoriteLiveData favoriteLiveData;

    public FavoriteViewModel() {
        favoriteLiveData = FavoriteLiveData.get();
    }

    public FavoriteLiveData getFavoriteLiveData() {
        return favoriteLiveData;
    }

}
