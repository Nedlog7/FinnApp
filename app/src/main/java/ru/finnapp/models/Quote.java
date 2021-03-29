package ru.finnapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Quote {

    /* Open price of the day */
    @SerializedName("o")
    @Expose
    private Float o;
    /* High price of the day */
    @SerializedName("h")
    @Expose
    private Float h;
    /* Low price of the day */
    @SerializedName("l")
    @Expose
    private Float l;
    /* Current price */
    @SerializedName("c")
    @Expose
    private Float c;
    /* Previous close price */
    @SerializedName("pc")
    @Expose
    private Float pc;

    public Float getC() {
        return c;
    }

    public Float getPc() {
        return pc;
    }
}
