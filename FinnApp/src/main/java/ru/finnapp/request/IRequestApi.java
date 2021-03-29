package ru.finnapp.request;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import ru.finnapp.models.CompanyProfile2;
import ru.finnapp.models.Quote;
import ru.finnapp.utils.Constants;

/**
 * Интерфейс IRequestApi содержит запросы для сервера finnhub.
 */
public interface IRequestApi {

    @GET("api/v1/stock/profile2")
    @Headers({"Content-Type:application/json", "X-Finnhub-Token:" + Constants.token})
    Observable<CompanyProfile2> stockProfile(@Query("symbol") String symbol);

    @GET("api/v1/quote")
    @Headers({"Content-Type:application/json", "X-Finnhub-Token:" + Constants.token})
    Observable<Quote> quote(@Query("symbol") String symbol);

}
