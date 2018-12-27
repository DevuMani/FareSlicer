package com.example.dream.fareslicer.RetrofitClientAndInterface;


import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.QueryValue;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.ExpenseCreation.ExpenseCreationInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.ExpenseCreation.ExpenseCreationOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.ExpenseDeletion.ExpenseDeletionInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.ExpenseDeletion.ExpenseDeletionOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupDeletion.GroupDeletionInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupDeletion.GroupDeletionOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupDetailFetch.GroupDetailFetchInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupDetailFetch.GroupDetailFetchResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupExpenseList.GroupExpenseListInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupExpenseList.GroupExpenseListResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupListFetch.GroupListFetchInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupListFetch.GroupListFetchResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupMembersDetailsFetch.GroupMembersDetailsFetchInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupMembersDetailsFetch.GroupMembersDetailsFetchResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupShareSettle.GroupShareSettleInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupShareSettle.GroupShareSettleOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.IndividualShareSettle.IndividualShareSettleInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.IndividualShareSettle.IndividualShareSettleOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.MemberDeletion.MemberDeletionInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.MemberDeletion.MemberDeletionOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.ShowAmountHome.ShowAmountInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.ShowAmountHome.ShowAmountOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.TransactionMembersList.TransactionMembersListInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.TransactionMembersList.TransactionMembersListResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Api {


    @POST("insert")
    Call<CallResult> insert(@Body QueryValue queryValue);

    @POST("select")
    Call<CallResult> select(@Body QueryValue queryValue);

    @POST("expenseCreation")
    Call<ExpenseCreationOutput> expenseCreation(@Body ExpenseCreationInput input);

    @POST("showAmountHome")
    Call<ShowAmountOutput> showAmountHome(@Body ShowAmountInput input);

    @POST("groupListFetch")
    Call<GroupListFetchResult> groupListFetch(@Body GroupListFetchInput input);

    @POST("transactionMembersList")
    Call<TransactionMembersListResult> transactionMembersList(@Body TransactionMembersListInput input);

    @POST("groupMembersDetailsFetch")
    Call<GroupMembersDetailsFetchResult> groupMembersDetailsFetch(@Body GroupMembersDetailsFetchInput input);

    @POST("individualShareSettle")
    Call<IndividualShareSettleOutput> individualShareSettle(@Body IndividualShareSettleInput input);

    @POST("groupShareSettle")
    Call<GroupShareSettleOutput> groupShareSettle(@Body GroupShareSettleInput input);

    @POST("groupExpenseList")
    Call<GroupExpenseListResult> groupExpenseList(@Body GroupExpenseListInput input);

    @POST("groupDetailFetch")
    Call<GroupDetailFetchResult> groupDetailFetch(@Body GroupDetailFetchInput input);

    @POST("groupDeletion")
    Call<GroupDeletionOutput> groupDeletion(@Body GroupDeletionInput input);

    @POST("memberDeletion")
    Call<MemberDeletionOutput> memberDeletion(@Body MemberDeletionInput input);

    @POST("expenseDelete")
    Call<ExpenseDeletionOutput> expenseDelete(@Body ExpenseDeletionInput input);

}
