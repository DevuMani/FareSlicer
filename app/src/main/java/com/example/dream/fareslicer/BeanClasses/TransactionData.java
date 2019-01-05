package com.example.dream.fareslicer.BeanClasses;

public class TransactionData {

    String user_id="";
    String user_name="";

    public TransactionData(String user_id, String user_name) {
        this.user_id = user_id;
        this.user_name = user_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }
}
