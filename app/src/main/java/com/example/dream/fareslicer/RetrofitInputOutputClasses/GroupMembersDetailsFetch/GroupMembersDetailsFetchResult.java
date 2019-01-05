package com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupMembersDetailsFetch;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupMembersDetailsFetchResult {

    @SerializedName("output")
    @Expose
    private List<GroupMembersDetailsFetchOutput> output = null;

    public List<GroupMembersDetailsFetchOutput> getOutput() {
        return output;
    }

    public void setOutput(List<GroupMembersDetailsFetchOutput> output) {
        this.output = output;
    }

}