package ru.finnapp.models;

import java.util.List;

public class CurrentPrice extends BaseModel {

    public final List<Data> data;
    public final String type;

    public CurrentPrice(List<Data> data, String type) {
        super();
        this.data = data;
        this.type = type;
    }

}