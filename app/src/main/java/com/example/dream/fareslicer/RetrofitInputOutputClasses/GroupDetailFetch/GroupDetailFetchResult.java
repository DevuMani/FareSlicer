package com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupDetailFetch;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupDetailFetchResult {

    @SerializedName("output")
    @Expose
    private List<GroupDetailFetchOutput> groupDetailFetchOutput = null;

    public List<GroupDetailFetchOutput> getGroupDetailFetchOutput() {
        return groupDetailFetchOutput;
    }

    public void setGroupDetailFetchOutput(List<GroupDetailFetchOutput> groupDetailFetchOutput) {
        this.groupDetailFetchOutput = groupDetailFetchOutput;
    }

}