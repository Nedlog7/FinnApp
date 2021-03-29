package ru.finnapp.ui.main.viewModels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import javax.net.ssl.SSLSocketFactory;

import ru.finnapp.FinnApp;
import ru.finnapp.models.CurrentPrice;
import ru.finnapp.utils.Constants;

public class SocketLiveData extends LiveData<CurrentPrice> implements Constants {

    private static final SocketLiveData instance = new SocketLiveData();

    public static SocketLiveData get() {
        return instance;
    }

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super CurrentPrice> observer) {
        super.observe(owner, observer);
        connect();
    }

    public synchronized void connect() {
        if (FinnApp.webSocketClient == null)
            initWebSocket();
    }

    private void initWebSocket() {

        FinnApp.webSocketClient = new WebSocketClient(URI.create(finnhubSocketUrl)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d(TAG, "onOpen");
                for (String s : tickerList)
                    send("{\"type\":\"subscribe\",\"symbol\":\"" + s + "\"}");
            }

            @Override
            public void onMessage(String message) {
                Log.d(TAG, "onMessage: " + message);
                handleEvent(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d(TAG, "onClose: " + reason);
                if (code == 1006) {
                    FinnApp.webSocketClient = null;
                    instance.connect();
                }
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "onError: " + ex.getMessage());
            }
        };

        SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        FinnApp.webSocketClient.setSocketFactory(socketFactory);
        FinnApp.webSocketClient.connect();

    }

    private synchronized void handleEvent(String message) {
        try {
            CurrentPrice price = CurrentPrice.fromJson(message, CurrentPrice.class);
            postValue(price);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
