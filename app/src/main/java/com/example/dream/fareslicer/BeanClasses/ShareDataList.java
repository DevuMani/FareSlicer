package com.example.dream.fareslicer.BeanClasses;

public class ShareDataList {

    String trans_id="";
    String user_name="";
    String share_amount="";
    String date="";



    public ShareDataList(String trans_id, String user_name, String share_amount, String date) {

        this.trans_id = trans_id;
        this.user_name = user_name;
        this.share_amount = share_amount;
        this.date=date;

    }

    public String getTrans_id() {
        return trans_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getShare_amount() {
        return share_amount;
    }

    public String getDate() {
        return date;
    }
}
