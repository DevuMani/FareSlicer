package com.example.dream.fareslicer.BeanClasses;

public class GroupData {

    String group_id="";
    String group_name="";
    String group_currency="";

    public GroupData(String group_id, String group_name,String group_currency) {
        this.group_id = group_id;
        this.group_name = group_name;
        this.group_currency=group_currency;
    }

    public String getGroup_id() {
        return group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public String getGroup_currency() {
        return group_currency;
    }


    private String groupId="";
    private String groupName="";
    private String amount="";
    private String owe="";
    private String own="";


    public GroupData(String groupId, String groupName, String amount, String owe, String own) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.amount = amount;
        this.owe = owe;
        this.own = own;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getAmount() {
        return amount;
    }

    public String getOwe() {
        return owe;
    }

    public String getOwn() {
        return own;
    }
}
