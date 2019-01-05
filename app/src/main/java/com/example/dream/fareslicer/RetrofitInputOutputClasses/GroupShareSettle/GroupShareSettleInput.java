package com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupShareSettle;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupShareSettleInput {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("group_id")
    @Expose
    private String groupId;
    @SerializedName("member_id")
    @Expose
    private String memberId;
    @SerializedName("amount")
    @Expose
    private String amount;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

}