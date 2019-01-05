package com.example.dream.fareslicer.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class ExpenseListViewFragment extends BottomSheetDialogFragment {

    private static final String TAG = "Expense List View";
    private String g_id="";      // variable for intent, to store group id
    private String g_name="";    // variable for intent, to store group name

    private BottomSheetBehavior mBehavior;

    private String currency="";

    private RecyclerView expense_list_view;
    private SwipeRefreshLayout refreshLayout;
    private ExpenseListAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<ExpenseListData> expense_list;

    ProgressBar progressBar;
    LinearLayout no_data_layout;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.activity_expense_list_view, null);

        //To recieve values passed from activity
        Bundle args = getArguments();
        g_id=args.getString("g_id");
        g_name=args.getString("g_name");

        SharedPreferences sp=getActivity().getSharedPreferences("User",MODE_PRIVATE);
//        currency=getResources().getString(R.string.currency); remove currency
        currency=sp.getString("user_currency","");

//        getSupportActionBar().setTitle(g_name);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initView(view);
        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());

        setExpenseListData();


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                myUpdateOperation();

//                Toast.makeText(ExpenseListView.this, "Called Swipe", Toast.LENGTH_SHORT).show();

            }
        });

        return dialog;
    }


    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void myUpdateOperation() {

        setExpenseListData();

    }

    private void initView(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.findViewById(R.id.fakeShadow).setVisibility(View.GONE);
        }

        expense_list_view=view.findViewById(R.id.expense_list_recyclerView);
        refreshLayout=view.findViewById(R.id.expense_list_refresh);
        progressBar=view.findViewById(R.id.expense_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        no_data_layout=view.findViewById(R.id.expense_no_transaction_layout);
        no_data_layout.setVisibility(View.GONE);

    }

    private void setExpenseListData() {

        SharedPreferences sp=getActivity().getSharedPreferences("User",MODE_PRIVATE);
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
                            progressBar.setVisibility(View.GONE);


                        } else {

                            no_data_layout.setVisibility(View.GONE);
                            expense_list_view.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
//                            adapter.notifyDataSetChanged();
                            initRecyclerView();

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

        linearLayoutManager=new LinearLayoutManager(getContext());
        adapter=new ExpenseListAdapter(getContext(),expense_list,g_id,g_name);
        expense_list_view.setLayoutManager(linearLayoutManager);
        expense_list_view.setAdapter(adapter);

    }


}
