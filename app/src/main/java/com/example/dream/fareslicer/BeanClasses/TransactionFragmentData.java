package com.example.dream.fareslicer.BeanClasses;

public class TransactionFragmentData {

    private String userId;
    private String userName;
    private String amount;

    public TransactionFragmentData(String userId, String userName, String amount) {
        this.userId = userId;
        this.userName = userName;
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getAmount() {
        return amount;
    }
}
