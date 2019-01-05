package com.example.dream.fareslicer.RetrofitInputOutputClasses.ExpenseDeletion;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExpenseDeletionInput {

    @SerializedName("expense_id")
    @Expose
    private String expenseId;
    @SerializedName("user_id")
    @Expose
    private String userId;

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}