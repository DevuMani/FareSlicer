package com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupDeletion;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupDeletionInput {

    @SerializedName("group_id")
    @Expose
    private String groupId;
    @SerializedName("user_id")
    @Expose
    private String userId;

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

}