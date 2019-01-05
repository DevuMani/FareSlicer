package com.example.dream.fareslicer.BeanClasses;

public class ExpenseMemberData {

    String member_id="";
    String user_id="";
    String member_name="";
    Double value=0.0;


    public ExpenseMemberData(String member_id,String user_id,String member_name) {
        this.member_id = member_id;
        this.member_name = member_name;
        this.user_id=user_id;
    }



    public String getMember_id() {
        return member_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getMember_name() {
        return member_name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
