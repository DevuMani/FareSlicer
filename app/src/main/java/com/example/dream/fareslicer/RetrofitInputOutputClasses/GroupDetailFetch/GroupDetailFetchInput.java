package com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupDetailFetch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupDetailFetchInput {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("group_id")
    @Expose
    private String groupId;

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

}