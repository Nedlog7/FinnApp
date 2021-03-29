package ru.finnapp.ui.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ru.finnapp.models.StockInfo;
import ru.finnapp.models.StockInfoFavorite;
import ru.finnapp.ui.main.viewModels.FavoriteLiveData;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FavoriteStocks";
    public static final String FAVORITE_STOCKS_TABLE = "FavoriteStocksTable";
    private static final int DATABASE_VERSION = 1;
    private static DatabaseHelper dbHelper;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(context);
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_FAVORITE_STOCKS_TABLE =
                " CREATE TABLE " + FAVORITE_STOCKS_TABLE +
                        " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        " symbol TEXT, " +
                        " name TEXT, " +
                        " logoUrl TEXT, " +
                        " closePrice REAL, " +
                        " isFavorite INTEGER);";
        db.execSQL(SQL_CREATE_FAVORITE_STOCKS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + FAVORITE_STOCKS_TABLE);
            onCreate(db);
        }
    }

    public synchronized long putStock(StockInfo stockInfo) {

        ContentValues values = new ContentValues();
        values.put("symbol", stockInfo.getSymbol());
        values.put("name", stockInfo.getName());
        values.put("logoUrl", stockInfo.getLogoUrl());
        values.put("closePrice", stockInfo.getClosePrice());
        values.put("isFavorite", stockInfo.isFavorite() ? 1 : 0);


        return insert(values);

    }

    private long insert(ContentValues values) {

        SQLiteDatabase db = getWritableDatabase();
        return db.insertOrThrow(DatabaseHelper.FAVORITE_STOCKS_TABLE, null, values);

    }

    public synchronized void setClosePrice(String symbol, float closePrice) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("closePrice", closePrice);

        db.update(FAVORITE_STOCKS_TABLE, values,  "symbol = ?", new String[] {symbol});
    }

    public synchronized void setFavorite(StockInfo stockInfo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isFavorite", stockInfo.isFavorite() ? 1 : 0);

        db.update(FAVORITE_STOCKS_TABLE, values,  "symbol = ?",
                new String[] {stockInfo.getSymbol()});

        FavoriteLiveData.get().updateStock(stockInfo);
    }

    public StockInfo getStockInfo(String symbol) {

        StockInfo stockInfo = null;
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                "isFavorite"
        };

        Cursor cursor = db.query(FAVORITE_STOCKS_TABLE, projection,
                " symbol = ?", new String[] {symbol}, null, null, null);

        if(cursor != null && cursor.moveToFirst()) {

            stockInfo = new StockInfo();
            stockInfo.setFavorite(cursor.getInt(0) == 1);

            cursor.close();
        }

        return stockInfo;

    }

    public StockInfoFavorite getStockFavoriteList() {
        List<StockInfo> stockInfoList = new ArrayList<>();
        List<String> symbolList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                "_id",
                "symbol",
                "name",
                "logoUrl",
                "closePrice",
                "isFavorite"
        };

        Cursor cursor = db.query(FAVORITE_STOCKS_TABLE, projection,
                null, null, null, null, null);

        if(cursor != null) {
            while(cursor.moveToNext()) {

                boolean isFavorite = cursor.getInt(5) == 1;

                if (isFavorite) {
                    StockInfo stockInfo = new StockInfo();
                    stockInfo.setSymbol(cursor.getString(1));
                    stockInfo.setName(cursor.getString(2));
                    stockInfo.setLogoUrl(cursor.getString(3));
                    stockInfo.setClosePrice(cursor.getFloat(4));
                    stockInfo.setFavorite(true);

                    stockInfoList.add(stockInfo);
                    symbolList.add(cursor.getString(1));
                }

            }
            cursor.close();
        }

        return new StockInfoFavorite(stockInfoList, symbolList);

    }

}
