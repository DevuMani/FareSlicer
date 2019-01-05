package com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupExpenseList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupExpenseListOutput {

    @SerializedName("expense_id")
    @Expose
    private String expenseId;
    @SerializedName("expense_name")
    @Expose
    private String expenseName;
    @SerializedName("expense_date")
    @Expose
    private String expenseDate;
    @SerializedName("expense_amount")
    @Expose
    private String expenseAmount;
    @SerializedName("user_amount")
    @Expose
    private String userAmount;

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(String expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getExpenseAmount() {
        return expenseAmount;
    }

    public void setExpenseAmount(String expenseAmount) {
        this.expenseAmount = expenseAmount;
    }

    public String getUserAmount() {
        return userAmount;
    }

    public void setUserAmount(String userAmount) {
        this.userAmount = userAmount;
    }

}