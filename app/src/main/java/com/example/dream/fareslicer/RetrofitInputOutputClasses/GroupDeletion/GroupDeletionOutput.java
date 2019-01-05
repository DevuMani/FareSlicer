package com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupDeletion;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupDeletionOutput {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("error")
    @Expose
    private Object error;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

}