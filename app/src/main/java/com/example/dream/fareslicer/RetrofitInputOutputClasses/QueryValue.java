package com.example.dream.fareslicer.RetrofitInputOutputClasses;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QueryValue {

    @SerializedName("query")
    @Expose
    private String query;
    @SerializedName("value")
    @Expose
    private List<String> value = null;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }
}

