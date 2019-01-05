package com.example.dream.fareslicer.RetrofitInputOutputClasses.TransactionMembersList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionMembersListInput {

    @SerializedName("user_id")
    @Expose
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}