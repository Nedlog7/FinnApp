package ru.finnapp.models;

import ru.finnapp.FinnApp;

public class BaseModel {

    public static <T extends BaseModel>T fromJson(String json, Class<T> typeOf) {
        return FinnApp.getGson().fromJson(json, typeOf);
    }

}
