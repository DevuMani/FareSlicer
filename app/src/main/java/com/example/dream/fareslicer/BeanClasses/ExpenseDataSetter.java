package com.example.dream.fareslicer.BeanClasses;


public class ExpenseDataSetter {

    String amount="";
    String member_id="";

    public ExpenseDataSetter(String amount, String member_id) {
        this.amount = amount;
        this.member_id = member_id;
    }


    public String getAmount() {
        return amount;
    }

    public String getMember_id() {
        return member_id;
    }
}
