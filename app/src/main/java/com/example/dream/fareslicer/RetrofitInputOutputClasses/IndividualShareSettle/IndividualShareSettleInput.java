package com.example.dream.fareslicer.RetrofitInputOutputClasses.IndividualShareSettle;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IndividualShareSettleInput {

    @SerializedName("curuser_id")
    @Expose
    private String curuserId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("amount")
    @Expose
    private String amount;

    public String getCuruserId() {
        return curuserId;
    }

    public void setCuruserId(String curuserId) {
        this.curuserId = curuserId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

}