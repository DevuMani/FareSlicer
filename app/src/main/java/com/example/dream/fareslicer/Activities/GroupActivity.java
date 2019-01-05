package com.example.dream.fareslicer.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dream.fareslicer.AdapterClasses.ExpenseListAdapter;
import com.example.dream.fareslicer.AdapterClasses.GroupMemberListAdapter;
import com.example.dream.fareslicer.BeanClasses.ExpenseListData;
import com.example.dream.fareslicer.BeanClasses.GroupMemberData;
import com.example.dream.fareslicer.R;
import com.example.dream.fareslicer.RetrofitClientAndInterface.RetrofitClient;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.QueryValue;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupDetailFetch.GroupDetailFetchInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupDetailFetch.GroupDetailFetchOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupDetailFetch.GroupDetailFetchResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupExpenseList.GroupExpenseListInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupExpenseList.GroupExpenseListOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupExpenseList.GroupExpenseListResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupMembersDetailsFetch.GroupMembersDetailsFetchInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupMembersDetailsFetch.GroupMembersDetailsFetchOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupMembersDetailsFetch.GroupMembersDetailsFetchResult;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {

    private static final String TAG = "Group Activity" ;

    TextView total,tv_owe_text,tv_owe_amount;
    TextView tv_own_text,tv_own_amount;
    RecyclerView recyclerView;
    ProgressBar progressBar, group_total_progressBar;

    FloatingActionButton fab;
    LinearLayout group_total_layout,heading_layout;
    SwipeRefreshLayout swipeRefreshLayout;

    String user_id="";
    String currency="";
    static ArrayList<GroupMemberData> group_member_list=new ArrayList<>();

    private String g_id="";
    private String g_name="";
    private String g_owe="";
    private String g_own="";
    private String g_amount="";

    private LinearLayoutManager linearLayoutManager;
    private GroupMemberListAdapter adapter;
    private GroupMemberData groupMemberData;

    //Bottom sheet variables and views
    BottomSheetBehavior bottomSheetBehavior;
    LinearLayout expense_view_layout,lv_bottomsheet_heading,whole_layout;
    private ImageView im_bottom_dheet_direction;
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
        setContentView(R.layout.activity_group);


        SharedPreferences sp=getSharedPreferences("User",MODE_PRIVATE);
        user_id=sp.getString("user_id","");
//        currency=getResources().getString(R.string.currency);//remove currency
        currency=sp.getString("user_currency","");

        g_id=getIntent().getStringExtra("group_id");
        g_name=getIntent().getStringExtra("group_name");

        getSupportActionBar().setTitle(g_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initView();

        totalDetailSet();


        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(TAG, "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        myUpdateOperation();
                    }
                }
        );

//      BottomSheet functions and data setting part

        initViewBottomSheet();
        setBottomSheetExpenseListData();
        initBottomSheetRecyclerView();



        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_EXPANDED) {

                    getSupportActionBar().hide();
                    im_bottom_dheet_direction.setImageDrawable(getResources().getDrawable(R.drawable.bottom_sheet_down_arrow));
                    lv_bottomsheet_heading.setBackground(getDrawable(R.drawable.bottom_sheet_heading_outline));
                } else if (i == BottomSheetBehavior.STATE_COLLAPSED) {
                    im_bottom_dheet_direction.setImageDrawable(getResources().getDrawable(R.drawable.bottom_sheet_up_arrow));
//                    expense_view_layout.setBackground(getDrawable(android.R.color.transparent));
                    expense_view_layout.setBackground(getDrawable(R.drawable.bottom_sheet_heading_outline));
                    getSupportActionBar().show();
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                getSupportActionBar().hide();
                expense_view_layout.setBackgroundColor(getResources().getColor(R.color.white));
                lv_bottomsheet_heading.setBackground(getDrawable(R.drawable.bottom_sheet_heading_outline));

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gotoAddExpensePage();

            }
        });

        //remove because swipe refresh is not working in bottom sheet
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                myUpdateOperationBottomSheet();

//                Toast.makeText(ExpenseListView.this, "Called Swipe", Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void initBottomSheetRecyclerView() {

        lm_expenseList =new LinearLayoutManager(GroupActivity.this);
        expenseListAdapter =new ExpenseListAdapter(GroupActivity.this,expense_list,g_id,g_name);
        expense_list_view.setLayoutManager(lm_expenseList);
        expense_list_view.setAdapter(expenseListAdapter);

    }

    private void setBottomSheetExpenseListData() {

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

    private void initView() {

        swipeRefreshLayout=findViewById(R.id.group_refreshLayout);

        progressBar=findViewById(R.id.group_progressBar);
        progressBar.setVisibility(View.VISIBLE);

        whole_layout=findViewById(R.id.group_layout);

        group_total_progressBar=findViewById(R.id.group_total_progressbar);
        group_total_progressBar.setVisibility(View.VISIBLE);

        heading_layout=findViewById(R.id.group_transaction_heading_layout);
        heading_layout.setVisibility(View.GONE);

        group_total_layout=findViewById(R.id.group_total_layout);

        total=findViewById(R.id.group_total);
        tv_owe_text=findViewById(R.id.group_text_owe);
        tv_owe_amount=findViewById(R.id.group_amount_owe);

        tv_own_text=findViewById(R.id.group_text_own);
        tv_own_amount=findViewById(R.id.group_amount_own);

        recyclerView=findViewById(R.id.group_member_recyclerView);

        fab=findViewById(R.id.group_fab);

    }

    private void initViewBottomSheet() {

        expense_view_layout=findViewById(R.id.layout_expense_list);
        expense_view_layout.setBackground(getResources().getDrawable(R.drawable.bottom_sheet_heading_outline));

        bottomSheetBehavior = BottomSheetBehavior.from(expense_view_layout);

        lv_bottomsheet_heading=findViewById(R.id.linear_bottomsheet_heading);

        expense_list_view=findViewById(R.id.expense_list_recyclerView);
        im_bottom_dheet_direction=findViewById(R.id.expense_list_direction);
        refreshLayout=findViewById(R.id.expense_list_refresh);
        expenseList_progressBar =findViewById(R.id.expense_progress_bar);
        expenseList_progressBar.setVisibility(View.VISIBLE);

        no_data_layout=findViewById(R.id.expense_no_transaction_layout);
        no_data_layout.setVisibility(View.GONE);

    }

    private void myUpdateOperation() {

        totalDetailSet();


    }

    private void myUpdateOperationBottomSheet() {

        setBottomSheetExpenseListData();
        initBottomSheetRecyclerView();

    }

    private void gotoAddExpensePage() {

        Intent intent=new Intent(GroupActivity.this,AddExpense.class);
        intent.putExtra("g_id",g_id);
        intent.putExtra("g_name",g_name);

        startActivity(intent);
    }


    private void totalDetailSet() {

        GroupDetailFetchInput input=new GroupDetailFetchInput();
        input.setGroupId(g_id);
        input.setUserId(user_id);

        Call<GroupDetailFetchResult> call=RetrofitClient.getInstance().getApi().groupDetailFetch(input);

        call.enqueue(new Callback<GroupDetailFetchResult>() {
            @Override
            public void onResponse(Call<GroupDetailFetchResult> call, Response<GroupDetailFetchResult> response) {

                swipeRefreshLayout.setRefreshing(false);

                if (response.code() == 200) {

                    setMemberDataList();

                    GroupDetailFetchResult groupDetailFetchResult = response.body();

                    List<GroupDetailFetchOutput> outputList=groupDetailFetchResult.getGroupDetailFetchOutput();

                    if (outputList.size()>0)
                    {
                        GroupDetailFetchOutput output=outputList.get(0);

                        init(output.getOwe(),output.getOwn(),output.getAmount());

                    }
                    else
                    {
                        //Warning snackbar
                        Snackbar.make(whole_layout,"Sorry for your inconvenience.. Please try refreshing page by swiping down..",Snackbar.LENGTH_SHORT).show();

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
            public void onFailure(Call<GroupDetailFetchResult> call, Throwable t) {

                Log.e(TAG,t.getMessage());
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }


    private void init(String owe, String own, String amount) {

        group_total_progressBar.setVisibility(View.GONE);
        heading_layout.setVisibility(View.VISIBLE);

        g_owe=String.format("%.2f", Double.parseDouble(owe));
        g_own=String.format("%.2f", Double.parseDouble(own));
        g_amount=String.format("%.2f", Double.parseDouble(amount));

        if (Double.parseDouble(g_amount)>=0)
        {

            total.setText(currency+" "+g_amount);
            total.setTextColor(getResources().getColor(R.color.caribbean_green));

//            group_total_layout.setBackground(getDrawable(R.drawable.layout_outline_green)); remove or change
//            linearHead.setBackground(getDrawable(R.drawable.layout_outline_green));
        }
        else
        {

            Double x = Math.abs(Double.parseDouble(g_amount));

            total.setText(currency+" "+x);
            total.setTextColor(getResources().getColor(R.color.warning_red));

//            group_total_layout.setBackground(getDrawable(R.drawable.layout_outline_red));
//            linearHead.setBackground(getDrawable(R.drawable.layout_outline_red));

        }

        Double x = Math.abs(Double.parseDouble(g_owe));
        tv_owe_text.setText( getResources().getString(R.string.group_owe_text));
        tv_owe_amount.setText(currency +" "+x);
        tv_owe_amount.setTextColor(getResources().getColor(R.color.warning_red));

        tv_own_text.setText(getResources().getString(R.string.group_own_text));
        tv_own_amount.setText(currency +" "+g_own);
        tv_own_amount.setTextColor(getResources().getColor(R.color.caribbean_green));

    }


    @Override
    public void onBackPressed() {

        Intent intent=new Intent(this,Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("tab_position",1);
        startActivity(intent);
        finish();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initRecyclerView() {

        linearLayoutManager=new LinearLayoutManager(GroupActivity.this);
        adapter = new GroupMemberListAdapter(GroupActivity.this, group_member_list,user_id,g_id);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setMemberDataList() {

        GroupMembersDetailsFetchInput input=new GroupMembersDetailsFetchInput();
        input.setGroupId(g_id);
        input.setUserId(user_id);

        Call<GroupMembersDetailsFetchResult> call=RetrofitClient.getInstance().getApi().groupMembersDetailsFetch(input);

        group_member_list= new ArrayList<>();

        group_member_list.clear();

        call.enqueue(new Callback<GroupMembersDetailsFetchResult>() {
            @Override
            public void onResponse(Call<GroupMembersDetailsFetchResult> call, Response<GroupMembersDetailsFetchResult> response) {

                if (response.code()==200)
                {

                    GroupMembersDetailsFetchResult result=response.body();

                    List<GroupMembersDetailsFetchOutput> outputList=result.getOutput();

                    for (int i = 0; i < outputList.size() ; i++) {
                        GroupMembersDetailsFetchOutput output=outputList.get(i);

                        groupMemberData=new GroupMemberData(output.getMemberId(),output.getUserId(),output.getUserName(),output.getAmount());

                        group_member_list.add(groupMemberData);

                    }



                    if (group_member_list.isEmpty())//this if will not work since there will not be such a case as member list empty
                    {
                        Log.e("Group Activity Select", "No data found");
                        progressBar.setVisibility(View.GONE);

                    } else {
//                         adapter.notifyDataSetChanged();
                        initRecyclerView();
                        progressBar.setVisibility(View.GONE);
                    }



            }
                else
                {
                    String s="";
                    try {
                        if (response.errorBody() != null) {
                            s=response.errorBody().string();
                            Log.e("Selection","Error body is "+s);
                        }
                        else
                        {
                            Log.e("Selection","Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e("Selection",e.getMessage());

                    }
                }
            }

            @Override
            public void onFailure(Call<GroupMembersDetailsFetchResult> call, Throwable t) {

                Log.e("Selection",t.getMessage());
            }
        });



    }

}
