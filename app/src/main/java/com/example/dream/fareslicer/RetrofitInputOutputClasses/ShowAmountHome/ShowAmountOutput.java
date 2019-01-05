package com.example.dream.fareslicer.RetrofitInputOutputClasses.ShowAmountHome;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShowAmountOutput {

    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("owe")
    @Expose
    private String owe;
    @SerializedName("own")
    @Expose
    private String own;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOwe() {
        return owe;
    }

    public void setOwe(String owe) {
        this.owe = owe;
    }

    public String getOwn() {
        return own;
    }

    public void setOwn(String own) {
        this.own = own;
    }

}