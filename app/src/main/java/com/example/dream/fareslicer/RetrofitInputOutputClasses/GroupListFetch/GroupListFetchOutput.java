package com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupListFetch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupListFetchOutput {

    @SerializedName("group_id")
    @Expose
    private String groupId;
    @SerializedName("group_name")
    @Expose
    private String groupName;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("owe")
    @Expose
    private String owe;
    @SerializedName("own")
    @Expose
    private String own;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

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
