package ru.finnapp.ui.main.viewModels;

import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private final SocketLiveData socketLiveData;

    public MainViewModel() {
        socketLiveData = SocketLiveData.get();
    }

    public SocketLiveData getSocketLiveData() {
        return socketLiveData;
    }

}
