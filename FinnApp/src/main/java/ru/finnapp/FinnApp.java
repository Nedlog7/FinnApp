package ru.finnapp;

import android.app.Application;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.java_websocket.client.WebSocketClient;

import ru.finnapp.ui.main.viewModels.SocketLiveData;
import ru.finnapp.utils.Constants;

public class FinnApp extends Application implements LifecycleObserver, Constants {

    public static boolean appInBackground;
    private static Gson gson;
    public static WebSocketClient webSocketClient;

    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        gson = new GsonBuilder().create();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onMoveToForeground() {
        // app moved to foreground
        appInBackground = false;

        if(webSocketClient != null && !webSocketClient.getConnection().isOpen()) {
            FinnApp.webSocketClient = null;
            SocketLiveData socketLiveData = SocketLiveData.get();
            socketLiveData.connect();
        }
            //SocketReconnectionScheduler.schedule();

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onMoveToBackground() {
        // app moved to background
        appInBackground = true;

        if(webSocketClient != null && webSocketClient.getConnection().isOpen())
            webSocketClient.close();
    }

    public static Gson getGson() {
        return gson;
    }

}
