package com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CallResult {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("output")
    @Expose
    private List<CallOutput> output = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CallOutput> getOutput() {
        return output;
    }

    public void setOutput(List<CallOutput> output) {
        this.output = output;
    }

}
