package com.example.dream.fareslicer.RetrofitInputOutputClasses.ExpenseCreation;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExpenseCreationInput {

    @SerializedName("group_id")
    @Expose
    private String groupId;
    @SerializedName("expense_name")
    @Expose
    private String expenseName;
    @SerializedName("member_id")
    @Expose
    private String memberId;
    @SerializedName("expense_amount")
    @Expose
    private String expenseAmount;
    @SerializedName("payment_id")
    @Expose
    private String paymentId;
    @SerializedName("member_ids")
    @Expose
    private List<String> memberIds = null;
    @SerializedName("share_amount")
    @Expose
    private List<String> shareAmount = null;

//    public ShowAmountInput(String groupId, String expenseName, String memberId, String expenseAmount, String paymentId, List<String> memberIds, List<String> shareAmount) {
//        this.groupId = groupId;
//        this.expenseName = expenseName;
//        this.memberId = memberId;
//        this.expenseAmount = expenseAmount;
//        this.paymentId = paymentId;
//        this.memberIds = memberIds;
//        this.shareAmount = shareAmount;
//    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public void setExpenseAmount(String expenseAmount) {
        this.expenseAmount = expenseAmount;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }

    public void setShareAmount(List<String> shareAmount) {
        this.shareAmount = shareAmount;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getExpenseAmount() {
        return expenseAmount;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public List<String> getShareAmount() {
        return shareAmount;
    }
}
