package com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupMembersDetailsFetch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupMembersDetailsFetchOutput {

    @SerializedName("member_id")
    @Expose
    private String memberId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("amount")
    @Expose
    private String amount;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

}