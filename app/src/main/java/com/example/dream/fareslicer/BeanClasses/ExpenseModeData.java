package com.example.dream.fareslicer.BeanClasses;

public class ExpenseModeData {

    String payment_id="";
    String payment_mode="";

    public ExpenseModeData(String payment_id, String payment_mode) {
        this.payment_id = payment_id;
        this.payment_mode = payment_mode;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public String getPayment_mode() {
        return payment_mode;
    }
}
