package com.example.dream.fareslicer.RetrofitInputOutputClasses.TransactionMembersList;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionMembersListResult {

    @SerializedName("output")
    @Expose
    private List<TransactionMembersListOutput> output = null;

    public List<TransactionMembersListOutput> getOutput() {
        return output;
    }

    public void setOutput(List<TransactionMembersListOutput> output) {
        this.output = output;
    }

}