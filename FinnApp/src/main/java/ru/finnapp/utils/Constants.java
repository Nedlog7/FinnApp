/**
 * Copyright 2004-2013 Crypto-Pro. All rights reserved.
 * Программный код, содержащийся в этом файле, предназначен
 * для целей обучения. Может быть скопирован или модифицирован
 * при условии сохранения абзацев с указанием авторства и прав.
 *
 * Данный код не может быть непосредственно использован
 * для защиты информации. Компания Крипто-Про не несет никакой
 * ответственности за функционирование этого кода.
 */
package ru.finnapp.utils;

/**
 * Служебный класс Constants с перечислением глобальных
 * констант клиентского приложения.
 *
 */
public interface Constants {

    String TAG = "FinnApp";

    String token = "c1cshrv48v6p6471m0jg";

    String finnhubSocketUrl = "wss://ws.finnhub.io?token=" + token;
    String finnhubUrl = "https://finnhub.io/";

    String[] tickerList = new String[]{
            "BA", "AAPL", "GOOGL", "AMZN", "TWTR", "MSFT", "TSLA", "FB", "INTC", "ATVI", "MU",
            "NFLX", "NVDA", "PFE", "T", "V", "ABBV", "BABA", "AAL", "BIDU", "BIIB", "BMY", "AVGO",
            "EA", "FDX", "F", "GE", "GM", "NEM", "PYPL", "QCOM", "WMT", "JNJ", "IBM", "PG", "LOW",
            "NEE", "TGT", "KO", "CVX", "ABT", "UBER", "CRM", "VTRS", "UNH", "HD", "GILD", "VZ",
            "BKNG", "MRK", "ORCL", "HON", "LMT", "MRNA", "TTWO", "FCX", "HAL", "BKR", "TXN", "AMGN",
            "DD", "M", "CMCSA", "EBAY", "CL", "AMAT"};

    int cacheSize = 10 * 1024 * 1024; // this is 10MB

}
