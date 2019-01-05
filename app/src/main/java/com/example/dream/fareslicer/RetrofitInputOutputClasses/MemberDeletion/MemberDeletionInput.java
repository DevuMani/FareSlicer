package com.example.dream.fareslicer.RetrofitInputOutputClasses.MemberDeletion;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MemberDeletionInput {

    @SerializedName("group_id")
    @Expose
    private String groupId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("curuser_id")
    @Expose
    private String curuserId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCuruserId() {
        return curuserId;
    }

    public void setCuruserId(String curuserId) {
        this.curuserId = curuserId;
    }

}