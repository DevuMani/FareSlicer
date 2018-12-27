package com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CallOutput {


    @SerializedName("row")
    @Expose
    private Integer row;
    @SerializedName("field")
    @Expose
    private List<String> field = null;
    @SerializedName("value")
    @Expose
    private List<String> value = null;

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public List<String> getField() {
        return field;
    }

    public void setField(List<String> field) {
        this.field = field;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }
}
