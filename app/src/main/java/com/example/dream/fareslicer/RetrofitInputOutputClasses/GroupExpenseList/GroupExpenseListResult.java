package com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupExpenseList;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupExpenseListResult {

    @SerializedName("output")
    @Expose
    private List<GroupExpenseListOutput> output = null;

    public List<GroupExpenseListOutput> getOutput() {
        return output;
    }

    public void setOutput(List<GroupExpenseListOutput> output) {
        this.output = output;
    }

}