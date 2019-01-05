package com.example.dream.fareslicer.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.dream.fareslicer.AdapterClasses.ExpenseListAdapter;
import com.example.dream.fareslicer.BeanClasses.ExpenseListData;
import com.example.dream.fareslicer.R;
import com.example.dream.fareslicer.RetrofitClientAndInterface.RetrofitClient;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupExpenseList.GroupExpenseListInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupExpenseList.GroupExpenseListOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupExpenseList.GroupExpenseListResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExpenseListView extends AppCompatActivity {

    private static final String TAG = "Expense List View";
    private String g_id="";      // variable for intent, to store group id
    private String g_name="";    // variable for intent, to store group name

    private String currency="";

    private RecyclerView expense_list_view;
    private SwipeRefreshLayout refreshLayout;
    private ExpenseListAdapter expenseListAdapter;
    LinearLayoutManager lm_expenseList;
    ArrayList<ExpenseListData> expense_list;

    ProgressBar expenseList_progressBar;
    LinearLayout no_data_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list_view);

        g_id=getIntent().getStringExtra("g_id");
        g_name=getIntent().getStringExtra("g_name");

        SharedPreferences sp=getSharedPreferences("User",MODE_PRIVATE);
//        currency=getResources().getString(R.string.currency); remove currency
        currency=sp.getString("user_currency","");

        getSupportActionBar().setTitle(g_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initView();
        setExpenseListData();
        initRecyclerView();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                myUpdateOperation();

//                Toast.makeText(ExpenseListView.this, "Called Swipe", Toast.LENGTH_SHORT).show();

            }
        });
    }



    private void myUpdateOperation() {

        setExpenseListData();
        initRecyclerView();

    }

    private void initView() {

        expense_list_view=findViewById(R.id.expense_list_recyclerView);
        refreshLayout=findViewById(R.id.expense_list_refresh);
        expenseList_progressBar =findViewById(R.id.expense_progress_bar);
        expenseList_progressBar.setVisibility(View.VISIBLE);

        no_data_layout=findViewById(R.id.expense_no_transaction_layout);
        no_data_layout.setVisibility(View.GONE);

    }

    private void setExpenseListData() {

        SharedPreferences sp=getSharedPreferences("User",MODE_PRIVATE);
        String user_id=sp.getString("user_id","");

        GroupExpenseListInput input=new GroupExpenseListInput();
        input.setGroupId(g_id);
        input.setUserId(user_id);

        expense_list=new ArrayList<>();
        expense_list.clear();

        Call<GroupExpenseListResult> call=RetrofitClient.getInstance().getApi().groupExpenseList(input);

        call.enqueue(new Callback<GroupExpenseListResult>() {
            @Override
            public void onResponse(Call<GroupExpenseListResult> call, Response<GroupExpenseListResult> response) {

                if(response.code()==200)
                {

                    GroupExpenseListResult selectResult = response.body();

                    if (selectResult != null) {

                            List<GroupExpenseListOutput> outputList=selectResult.getOutput();

                            for (int i = 0; i <outputList.size() ; i++) {

                                GroupExpenseListOutput output=outputList.get(i);

                                Double user_amount = Math.abs(Double.parseDouble(output.getUserAmount()));
                                if(user_amount!= 0.0)
                                {
                                    ExpenseListData expenseListData=new ExpenseListData(output.getExpenseId(),output.getExpenseName(),output.getExpenseDate(),output.getExpenseAmount(),output.getUserAmount());

                                    expense_list.add(expenseListData);
                                }


                            }

                        if (expense_list.isEmpty()) {

                            no_data_layout.setVisibility(View.VISIBLE);
                            expense_list_view.setVisibility(View.GONE);
                            expenseList_progressBar.setVisibility(View.GONE);


                        } else {

                            no_data_layout.setVisibility(View.GONE);
                            expense_list_view.setVisibility(View.VISIBLE);
                            expenseList_progressBar.setVisibility(View.GONE);
                            expenseListAdapter.notifyDataSetChanged();

                        }

                        refreshLayout.setRefreshing(false);

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
            public void onFailure(Call<GroupExpenseListResult> call, Throwable t) {

                Log.e(TAG,t.getMessage());
                refreshLayout.setRefreshing(false);
            }


        });

    }

    private void initRecyclerView() {

        lm_expenseList =new LinearLayoutManager(ExpenseListView.this);
        expenseListAdapter =new ExpenseListAdapter(ExpenseListView.this,expense_list,g_id,g_name);
        expense_list_view.setLayoutManager(lm_expenseList);
        expense_list_view.setAdapter(expenseListAdapter);

    }

    @Override
    public void onBackPressed() {

        Intent intent=new Intent(this,GroupActivity.class);
        intent.putExtra("group_id",g_id);
        intent.putExtra("group_name",g_name);
        startActivity(intent);
        finish();

    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        return true;

    }


}
