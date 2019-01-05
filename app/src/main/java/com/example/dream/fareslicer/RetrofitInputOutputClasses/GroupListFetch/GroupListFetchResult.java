package com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupListFetch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class GroupListFetchResult {

    @SerializedName("output")
    @Expose
    private List<GroupListFetchOutput> output = null;

    public List<GroupListFetchOutput> getOutput() {
        return output;
    }

    public void setOutput(List<GroupListFetchOutput> output) {
        this.output = output;
    }

}

