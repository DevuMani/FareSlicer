package com.example.dream.fareslicer.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dream.fareslicer.AdapterClasses.ShareDataDisplayAdapter;
import com.example.dream.fareslicer.BeanClasses.ShareDataList;
import com.example.dream.fareslicer.R;
import com.example.dream.fareslicer.RetrofitClientAndInterface.RetrofitClient;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.QueryValue;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.ExpenseDeletion.ExpenseDeletionInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.ExpenseDeletion.ExpenseDeletionOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.MemberDeletion.MemberDeletionOutput;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExpenseActivity extends AppCompatActivity {

    private static final String TAG = "ExpenseActivity";
    String expense_id="";
    String expense_name="";

    private String g_id="";      // variable for intent,
    private String g_name="";    // variable for intent,

    private String currency="";


    TextView text_name,delete,text_pay_name,text_user_name,text_amount;
    RecyclerView expense_share;
    private LinearLayoutManager linearLayoutManager;
    private ShareDataDisplayAdapter shareDataSetAdapter;
    ArrayList<ShareDataList> share_list=new ArrayList<>();
    private ShareDataList shareDataList;
    private String expense_amount="";
    private static Double sum=0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        sum=0.0;
        initView();
        expense_id=getIntent().getStringExtra("expense_id");
        expense_name=getIntent().getStringExtra("expense_name");

        g_id=getIntent().getStringExtra("group_id");
        g_name=getIntent().getStringExtra("group_name");

        SharedPreferences sp=getSharedPreferences("User",MODE_PRIVATE);
//        currency=getResources().getString(R.string.currency); remove currency
        currency=sp.getString("user_currency","");

        getSupportActionBar().setTitle(expense_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        expenseDetailsCall();


        initDataRecyclerView();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteExpense();
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent=new Intent(this,ExpenseListView.class);
        intent.putExtra("g_id",g_id);
        intent.putExtra("g_name",g_name);
        startActivity(intent);
        finish();

    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        return true;

    }

    private void initView() {

        text_name=findViewById(R.id.expense_name);
        delete=findViewById(R.id.delete_expense);
        text_pay_name=findViewById(R.id.expense_mode_name);
        text_user_name=findViewById(R.id.expense_added_user);
        text_amount=findViewById(R.id.expense_amount);
        expense_share=findViewById(R.id.expense_list_share);


    }

    private void initDataRecyclerView() {
        linearLayoutManager=new LinearLayoutManager(ExpenseActivity.this);
        shareDataSetAdapter = new ShareDataDisplayAdapter(ExpenseActivity.this, share_list);
        expense_share.setLayoutManager(linearLayoutManager);
        expense_share.setAdapter(shareDataSetAdapter);
    }

    private void expenseDetailsCall() {

        String query="select e.expense_name,p.payment_name,e.expense_amount,u.user_name from tb_expense e,tb_member m ,tb_user u,tb_payment_mode p where m.member_id=e.member_id and e.payment_id=p.payment_id and m.user_id= u.user_id and e.expense_id=?";
        ArrayList<String> value=new ArrayList();
        value.add(expense_id);


        QueryValue queryValue=new QueryValue();
        queryValue.setQuery(query);
        queryValue.setValue(value);

        Call<CallResult> call=RetrofitClient.getInstance().getApi().select(queryValue);

        call.enqueue(new Callback<CallResult>() {
            @Override
            public void onResponse(Call<CallResult> call, Response<CallResult> response) {

                if(response.code()==200) {

                    CallResult selectResult = response.body();

                    String success = "";
                    if (selectResult != null) {
                        success = selectResult.getStatus();

                        if (success.equalsIgnoreCase("true")) {

                            List<CallOutput> outputList=selectResult.getOutput();
                            CallOutput callOutput=outputList.get(0);
                            List<String> value=callOutput.getValue();

                            String name=value.get(0);
                            String share_mode=value.get(1);
                            expense_amount=value.get(2);
                            String user_name=value.get(3);

                            text_name.setText(name);
//                            text_desc.setText(description);
                            text_pay_name.setText(share_mode);
                            text_amount.setText(currency+" "+expense_amount);
                            text_user_name.setText(user_name);

                            expenseMembersFetchCall();
                        }
                        else
                        {
                            Log.e(TAG,success);
//                            Snackbar.make(linearLayout,"Selection Failed",Snackbar.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Log.e(TAG,selectResult.toString());
//                        Snackbar.make(linearLayout,"Selection Failed",Snackbar.LENGTH_LONG).show();

                    }
                }
                else
                {

                    String s="";
                    try {
                        if (response.errorBody() != null) {
                            s=response.errorBody().string();
                            Log.e(TAG,"Error body is "+s);
                        }
                        else
                        {
                            Log.e(TAG,"Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e(TAG,e.getMessage());

                    }
                }


            }

            @Override
            public void onFailure(Call<CallResult> call, Throwable t) {
//                Toast.makeText(NewGroup.this, "Insertion Call failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG, t.getMessage());
            }
        });


    }

    private void expenseMembersFetchCall() {

//        String query="select s.share_id,u.user_name,s.share_amount from tb_share s, tb_member m, tb_user u where s.member_id=m.member_id and m.user_id=u.user_id and expense_id=?";
//        final ArrayList<String> value=new ArrayList();
//        value.add(expense_id);

        String query="select t.trans_id,t.expense_id, m.user_id,u.user_name,t.amount,t.date from tb_transaction t,tb_expense e,tb_member m, tb_user u where e.expense_id=t.expense_id and t.member_id=m.member_id and m.user_id=u.user_id and t.expense_id =? order by t.date" ;
        ArrayList<String> value=new ArrayList();
        value.add(expense_id);

        QueryValue queryValue=new QueryValue();
        queryValue.setQuery(query);
        queryValue.setValue(value);

        Call<CallResult> call=RetrofitClient.getInstance().getApi().select(queryValue);

        call.enqueue(new Callback<CallResult>() {
            @Override
            public void onResponse(Call<CallResult> call, Response<CallResult> response) {

                if(response.code()==200) {

                    CallResult selectResult = response.body();

                    String success = "";
                    if (selectResult != null) {
                        success = selectResult.getStatus();

                        if (success.equalsIgnoreCase("true")) {

                            List<CallOutput> outputList=selectResult.getOutput();
                            for (int i = 0; i <outputList.size() ; i++) {
                                CallOutput callOutput=outputList.get(i);
                                List<String> values=callOutput.getValue();


                                String trans_id=values.get(0);
                                String user_id=values.get(2);
                                String user_name=values.get(3);
                                String share_amount=values.get(4);
                                String date=values.get(5);
                                SharedPreferences sp=getSharedPreferences("User",MODE_PRIVATE);

                                if(user_id.equalsIgnoreCase(sp.getString("user_id","")))
                                {
                                    user_name="You";
                                }
                                sum=sum+Double.parseDouble(share_amount);

                                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
                                Date old_date_format=null;

                                try {
                                    old_date_format=simpleDateFormat.parse(date);
                                    date=sdf.format(old_date_format);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                shareDataList=new ShareDataList(trans_id,user_name,share_amount,date);
                                share_list.add(shareDataList);

                            }
//remove
                            //To show your share in this expense

//                            if((Double.parseDouble(expense_amount)!=sum )&& (Double.parseDouble(expense_amount)>sum)) {
//
//                                String your_share = "" + (Double.parseDouble(expense_amount) - sum);
//                                shareDataList = new ShareDataList("", "you", your_share);
//                                share_list.add(shareDataList);
//                            }
                            shareDataSetAdapter.notifyDataSetChanged();
                         }
                        else
                        {
                            Log.e(TAG,success);
                        }
                    }
                    else
                    {
                        Log.e(TAG,selectResult.toString());
                    }
                }
                else
                {

                    String s="";
                    try {
                        if (response.errorBody() != null) {
                            s=response.errorBody().string();
                            Log.e(TAG,"Error body is "+s);
                        }
                        else
                        {
                            Log.e(TAG,"Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e(TAG,e.getMessage());

                    }
                }


            }

            @Override
            public void onFailure(Call<CallResult> call, Throwable t) {
//                Toast.makeText(NewGroup.this, "Insertion Call failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG, t.getMessage());
            }
        });

    }

    public void deleteExpense() {

        SharedPreferences sp=getSharedPreferences("User",MODE_PRIVATE);
        String user_id=sp.getString("user_id","");

        ExpenseDeletionInput input=new ExpenseDeletionInput();
        input.setExpenseId(expense_id);
        input.setUserId(user_id);

        Call<ExpenseDeletionOutput> call=RetrofitClient.getInstance().getApi().expenseDelete(input);

        call.enqueue(new Callback<ExpenseDeletionOutput>() {
            @Override
            public void onResponse(Call<ExpenseDeletionOutput> call, Response<ExpenseDeletionOutput> response) {
                if(response.code()==200) {

                    ExpenseDeletionOutput selectResult = response.body();

                    String success = "";
                    if (selectResult != null) {
                        success = selectResult.getStatus();

                        if (success.equalsIgnoreCase("true")) {

                            Toast.makeText(ExpenseActivity.this, "Deleted expense", Toast.LENGTH_SHORT).show();


                        }
                        else if(success.equalsIgnoreCase("false"))
                        {
                            String error_msg=selectResult.getError();

                            if(error_msg.equalsIgnoreCase("Not Owner of Expense"))
                            Toast.makeText(ExpenseActivity.this, "Only owner can delete expense ", Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            Log.e(TAG,success);
                        }
                    }
                    else
                    {
                        Log.e(TAG,selectResult.toString());
//                        Snackbar.make(linearLayout,"Selection Failed",Snackbar.LENGTH_LONG).show();

                    }
                }
                else
                {

                    String s="";
                    try {
                        if (response.errorBody() != null) {
                            s=response.errorBody().string();
                            Log.e(TAG,"Error body is "+s);
                        }
                        else
                        {
                            Log.e(TAG,"Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e(TAG,e.getMessage());

                    }
                }

            }

            @Override
            public void onFailure(Call<ExpenseDeletionOutput> call, Throwable t) {

            }
        });

    }
}
