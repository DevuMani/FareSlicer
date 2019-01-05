package com.example.dream.fareslicer.BeanClasses;

public class GroupMemberData {

    private String memberId;
    private String userId;
    private String userName;
    private String amount;

    public GroupMemberData(String memberId, String userId, String userName, String amount) {
        this.memberId = memberId;
        this.userId = userId;
        this.userName = userName;
        this.amount = amount;
    }

    public String getMemberId() {
        return memberId;
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
