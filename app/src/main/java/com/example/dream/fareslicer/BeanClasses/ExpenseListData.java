package com.example.dream.fareslicer.BeanClasses;

public class ExpenseListData {

    private String expenseId;
    private String expenseName;
    private String expenseDate;
    private String expenseAmount;
    private String userAmount;

    public ExpenseListData(String expenseId, String expenseName, String expenseDate, String expenseAmount, String userAmount) {
        this.expenseId = expenseId;
        this.expenseName = expenseName;
        this.expenseDate = expenseDate;
        this.expenseAmount = expenseAmount;
        this.userAmount = userAmount;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public String getExpenseAmount() {
        return expenseAmount;
    }

    public String getUserAmount() {
        return userAmount;
    }
}
