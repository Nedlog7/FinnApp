package ru.finnapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CompanyProfile2 {

    /* Country of company's headquarter. */
    @SerializedName("country")
    @Expose
    String country = null;
    /* Currency used in company filings. */
    @SerializedName("currency")
    @Expose
    private String currency;
    /* Listed exchange. */
    @SerializedName("exchange")
    @Expose
    private String exchange;
    /* Company name. */
    @SerializedName("name")
    @Expose
    private String name;
    /* Company symbol/ticker as used on the listed exchange. */
    @SerializedName("ticker")
    @Expose
    private String ticker;
    /* IPO date. */
    @SerializedName("ipo")
    @Expose
    private String ipo;
    /* Market Capitalization. */
    @SerializedName("marketCapitalization")
    @Expose
    private Float marketCapitalization;
    /* Number of oustanding shares. */
    @SerializedName("shareOutstanding")
    @Expose
    private Float shareOutstanding;
    /* Logo image. */
    @SerializedName("logo")
    @Expose
    private String logo;
    /* Company phone number. */
    @SerializedName("phone")
    @Expose
    private String phone;
    /* Company website. */
    @SerializedName("weburl")
    @Expose
    private String weburl;
    /* Finnhub industry classification. */
    @SerializedName("finnhubIndustry")
    @Expose
    private String finnhubIndustry;

    public String getName() {
        return name;
    }

    public String getTicker() {
        return ticker;
    }

    public String getLogo() {
        return logo;
    }

}
