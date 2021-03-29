package ru.finnapp.request;

import android.content.Context;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.finnapp.models.CompanyProfile2;
import ru.finnapp.models.Quote;
import ru.finnapp.models.StockInfo;
import ru.finnapp.ui.main.StockRecyclerViewAdapter;
import ru.finnapp.utils.Constants;
import ru.finnapp.utils.Utilities;

public class RequestApi implements Constants {

    private final Context context;
    private final StockRecyclerViewAdapter viewAdapter;
    private Retrofit retrofit;

    public RequestApi(Context context, StockRecyclerViewAdapter viewAdapter) {
        this.context = context;
        this.viewAdapter = viewAdapter;
    }

    private OkHttpClient createOkHttpClient() throws Exception {

        InputStream caInput = context.getAssets().open("finnRoot.cer");

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate ca = cf.generateCertificate(caInput);
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .addInterceptor(provideOfflineInterceptor())
                .addNetworkInterceptor(provideOnlineInterceptor())
                .cache(provideCache())
                .followRedirects(false)
                .sslSocketFactory(sslContext.getSocketFactory(),
                (X509TrustManager) tmf.getTrustManagers()[0]);

        return client.build();

    }

    public void stockProfileRequest() {

        try {

            OkHttpClient httpClient = createOkHttpClient();

            RxJava2CallAdapterFactory rxAdapter = RxJava2CallAdapterFactory
                    .createWithScheduler(Schedulers.io());

            retrofit = new Retrofit.Builder()
                    .baseUrl(finnhubUrl)
                    .addCallAdapterFactory(rxAdapter)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            IRequestApi requestApi = retrofit.create(IRequestApi.class);

            for (String symbol : tickerList) {

                requestApi.stockProfile(symbol).observeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableObserver<CompanyProfile2>() {
                            @Override
                            public void onNext(@NotNull CompanyProfile2 value) {
                                Log.d(TAG, "onNext");

                                StockInfo stockInfo = new StockInfo();
                                stockInfo.setSymbol(value.getTicker());
                                stockInfo.setName(value.getName());
                                stockInfo.setLogoUrl(value.getLogo());

                                viewAdapter.setStock(stockInfo);
                            }

                            @Override
                            public void onError(@NotNull Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG, "onComplete");
                                quoteRequest(symbol);
                            }
                        });

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void quoteRequest(String symbol) {

        IRequestApi requestApi = retrofit.create(IRequestApi.class);
        requestApi.quote(symbol).observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<Quote>() {
                    @Override
                    public void onNext(@NonNull Quote quote) {
                        Log.d(TAG, "onNext");
                        viewAdapter.updateStock(symbol, quote.getPc(), quote.getC());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    Cache provideCache() {
        return new Cache(new File(context.getCacheDir(), "http-cache"), cacheSize);
    }

    private Interceptor provideOnlineInterceptor() {
        return chain -> {
            okhttp3.Response response = chain.proceed(chain.request());
            CacheControl cacheControl;
            if (Utilities.isNetworkAvailable(context)) {
                cacheControl = new CacheControl.Builder().maxAge(0, TimeUnit.SECONDS).build();
            } else {
                cacheControl = new CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build();
            }

            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", cacheControl.toString())
                    .build();

        };
    }

    private Interceptor provideOfflineInterceptor() {
        return chain -> {
            Request request = chain.request();

            if (!Utilities.isNetworkAvailable(context)) {
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build();

                request = request.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .cacheControl(cacheControl)
                        .build();
            }

            return chain.proceed(request);
        };
    }

}
