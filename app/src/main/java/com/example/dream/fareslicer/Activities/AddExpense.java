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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dream.fareslicer.AdapterClasses.ExpenseModeAdapter;
import com.example.dream.fareslicer.AdapterClasses.ShareDataSetAdapter;
import com.example.dream.fareslicer.BeanClasses.ExpenseMemberData;
import com.example.dream.fareslicer.BeanClasses.ExpenseModeData;
import com.example.dream.fareslicer.Interface.MyMemberListSetting;
import com.example.dream.fareslicer.Interface.PaymentModeSetter;
import com.example.dream.fareslicer.R;
import com.example.dream.fareslicer.RetrofitClientAndInterface.RetrofitClient;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.QueryValue;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.ExpenseCreation.ExpenseCreationInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.ExpenseCreation.ExpenseCreationOutput;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddExpense extends AppCompatActivity implements PaymentModeSetter,MyMemberListSetting {

    private static final String TAG = "Add expense" ;
    String user_id="";                  //variable for storing shared preference value

    private static String g_id="";      // variable for intent, to store group id
    private static String g_name="";    // variable for intent, to store group name

    private String currency="";

    public static EditText tot_amount; //static because need this value in two adapters; one when selecting payment mode in it's adapter
    public static String amount="";
    private EditText expense_name;
    private TextView expense_currency,shareAmong;
    private RecyclerView expense_mode,expense_share;
    private static LinearLayout expense_members_layout;
    private Button save;

    //Adapter and arraylist for expense_mode recycler view
    private ExpenseModeAdapter expenseModeAdapter;
    ArrayList<ExpenseModeData> mode_list=new ArrayList<>();
    private ExpenseModeData expenseModeData;

    private LinearLayoutManager linearLayoutManager; //common for both recycler view

    //Adapter and arraylist for expense_share recycler view
    private ShareDataSetAdapter shareDataSetAdapter;
    ArrayList<ExpenseMemberData> member_list=new ArrayList<>();
    private ExpenseMemberData expenseMemberData;

    public static AddExpense addExpense=new AddExpense(); //To get split mode data here from ExpenseModeAdapter
    private static String paymentId="";
    private static String paymentMode="";

    private ArrayList<ExpenseMemberData> arrayListWithData;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // To clear initial data of static variables
        g_id="";
        g_name="";

        SharedPreferences sp=getSharedPreferences("User",MODE_PRIVATE);
        user_id=sp.getString("user_id","");
        currency=sp.getString("user_currency","");
//        currency=getResources().getString(R.string.currency); remove currency

        g_id=getIntent().getStringExtra("g_id");
        g_name=getIntent().getStringExtra("g_name");



        getSupportActionBar().setTitle(g_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        initView();
        expense_currency.setText(currency);
        initExpenseModeRecyclerView();

        modeSetCall();
        memberShowCall();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//remove
//                saveFunction1();//Old save function
                fetchMemberId();

            }
        });

        tot_amount.addTextChangedListener(myAmountChangeListener);

    }

    private void fetchMemberId() {

        String query="select member_id from tb_member where group_id=? and user_id=?";
        final ArrayList<String> value=new ArrayList();
        value.add(g_id);
        value.add(user_id);

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
                                String member_id=values.get(0);

                                saveFunction(member_id);

                            }
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

    public TextWatcher myAmountChangeListener=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            amount=tot_amount.getText().toString();
            if(amount.equalsIgnoreCase(""))
            {
                expense_members_layout.setVisibility(View.GONE);
            }
        }
    };

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

    private void initView() {

        expense_name=findViewById(R.id.new_expense_name);
        expense_currency=findViewById(R.id.expense_currency);
        tot_amount=findViewById(R.id.expense_amount);
        expense_mode=findViewById(R.id.expense_mode_recyclerView);
        shareAmong=findViewById(R.id.expense_share);
        expense_share=findViewById(R.id.expense_share_recyclerView);
        expense_members_layout =findViewById(R.id.expense_member_select);
        expense_members_layout.setVisibility(View.GONE);// For not showing list at first. it should be shown only if user selects fare slicing mode other than Split equally
        save=findViewById(R.id.expense_save);
        frameLayout=findViewById(R.id.addexpense_frameLayout);
    }

    private void initExpenseModeRecyclerView() {

        linearLayoutManager=new LinearLayoutManager(AddExpense.this);
        expenseModeAdapter = new ExpenseModeAdapter(AddExpense.this, mode_list,g_id,currency,this);
        expense_mode.setLayoutManager(linearLayoutManager);
        expense_mode.setAdapter(expenseModeAdapter);
    }

    private void initDataRecyclerView() {

        linearLayoutManager=new LinearLayoutManager(AddExpense.this);
        shareDataSetAdapter = new ShareDataSetAdapter(AddExpense.this, arrayListWithData,currency);
        expense_share.setLayoutManager(linearLayoutManager);
        expense_share.setAdapter(shareDataSetAdapter);
    }


    private void modeSetCall() {

        String query="select payment_id,payment_name from tb_payment_mode";

        QueryValue queryValue=new QueryValue();
        queryValue.setQuery(query);

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

                            List<CallOutput> callOutputList=selectResult.getOutput() ;
                            for(int i=0;i<callOutputList.size();i++)
                            {
                                CallOutput callOutput=callOutputList.get(i);
                                List<String> value=callOutput.getValue();

                                expenseModeData=new ExpenseModeData(value.get(0),value.get(1));
                                mode_list.add(expenseModeData);

                                expenseModeAdapter.notifyDataSetChanged();

                            }



                        }
                        else
                        {
                            Log.e(TAG + "selection",success);
//                            Snackbar.make(linearLayout,"Selection Failed",Snackbar.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Log.e(TAG + "selection",selectResult.toString());
//                        Snackbar.make(linearLayout,"Selection Failed",Snackbar.LENGTH_LONG).show();

                    }
                }
                else
                {

                    String s="";
                    try {
                        if (response.errorBody() != null) {
                            s=response.errorBody().string();
                            Log.e(TAG + "selection","Error body is "+s);
                        }
                        else
                        {
                            Log.e(TAG + "selection","Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e(TAG + "selection",e.getMessage());

                    }
                }


            }

            @Override
            public void onFailure(Call<CallResult> call, Throwable t) {
//                Toast.makeText(NewGroup.this, "Insertion Call failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG + "selection", t.getMessage());
            }
        });

    }

    private void memberShowCall() {

        String query="select t1.member_id,t1.user_id,t2.user_name from tb_member t1,tb_user t2 where t1.user_id = t2.user_id and t1.group_id =?";
        ArrayList<String> value=new ArrayList();
        value.add(""+g_id);

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

                            List<CallOutput> callOutputList=selectResult.getOutput() ;
                            for(int i=0;i<callOutputList.size();i++)
                            {
                                CallOutput callOutput=callOutputList.get(i);
                                List<String> value=callOutput.getValue();

                                expenseMemberData=new ExpenseMemberData(value.get(0),value.get(1),value.get(2));
                                member_list.add(expenseMemberData);

//                                shareAmongAdapter.notifyDataSetChanged();

                            }



                        }
                        else
                        {
                            Log.e(TAG + "selection",success);
//                            Snackbar.make(linearLayout,"Selection Failed",Snackbar.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Log.e(TAG + "selection",selectResult.toString());
//                        Snackbar.make(linearLayout,"Selection Failed",Snackbar.LENGTH_LONG).show();

                    }
                }
                else
                {

                    String s="";
                    try {
                        if (response.errorBody() != null) {
                            s=response.errorBody().string();
                            Log.e(TAG + "selection","Error body is "+s);
                        }
                        else
                        {
                            Log.e(TAG + "selection","Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e(TAG + "selection",e.getMessage());

                    }
                }

            }

            @Override
            public void onFailure(Call<CallResult> call, Throwable t) {
//                Toast.makeText(NewGroup.this, "Insertion Call failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG + "selection", t.getMessage());
            }
        });

    }

    @Override
    public void setPaymentId(String payId, String payment_mode) {

        paymentId=payId;
        paymentMode=payment_mode;

        switch (paymentId)
        {
            case "1":
                expense_members_layout.setVisibility(View.VISIBLE);
                break;
            case "2":
            case "3":
            case "4":
                expense_members_layout.setVisibility(View.VISIBLE);
                break;
            default:
                expense_members_layout.setVisibility(View.GONE);
                break;

        }
    }

    private void saveFunction(String member_id) {

        if (!arrayListWithData.isEmpty()) {

            final ExpenseCreationInput input=new ExpenseCreationInput();
            input.setGroupId(g_id);
            input.setExpenseName(expense_name.getText().toString());
            input.setMemberId(member_id);
            input.setPaymentId(paymentId);
            input.setExpenseAmount(amount);

            List<String> memberIds=new ArrayList<>();
            List<String> shareAmounts=new ArrayList<>();


            for (int i = 0; i <arrayListWithData.size() ; i++) {

                ExpenseMemberData expenseMemberData=arrayListWithData.get(i);
                if(!expenseMemberData.getMember_id().equals(member_id)) {
                    memberIds.add(expenseMemberData.getMember_id());
                    shareAmounts.add("" + expenseMemberData.getValue());
                }
                else
                {
                    Log.d(TAG,"Skipped self share before calling API "+expenseMemberData.getMember_id()+" "+expenseMemberData.getValue());
                }
            }
            input.setMemberIds(memberIds);
            input.setShareAmount(shareAmounts);

            Call<ExpenseCreationOutput> call = RetrofitClient.getInstance().getApi().expenseCreation(input);

            call.enqueue(new Callback<ExpenseCreationOutput>() {
                @Override
                public void onResponse(Call<ExpenseCreationOutput> call, Response<ExpenseCreationOutput> response) {

                    if (response.code() == 200) {

                        ExpenseCreationOutput selectResult = response.body();

                        String success = selectResult.getStatus();

                        if (success.equalsIgnoreCase("true")) {

                            Snackbar.make(frameLayout,"Created Expense successfully",Snackbar.LENGTH_LONG).show();

                            Intent intent=new Intent(AddExpense.this,GroupActivity.class);
                            intent.putExtra("group_id",g_id);
                            intent.putExtra("group_name",g_name);
                            startActivity(intent);
                            finish();

                        } else {
                            Log.e(TAG, success);
                            Snackbar.make(frameLayout,"Expense Creation Failed",Snackbar.LENGTH_LONG).show();
                        }

                    } else {

                        String s = "";
                        try {
                            if (response.errorBody() != null) {
                                s = response.errorBody().string();

                                Log.e(TAG+" insertion", "Error body is "+s);
                            } else {
                                Log.e(TAG+" insertion", "Error body is null");
                            }
                        } catch (IOException e) {
                            Log.e(TAG+" insertion", e.getMessage());

                        }
                    }


                }

                @Override
                public void onFailure(Call<ExpenseCreationOutput> call, Throwable t) {
                    //                Toast.makeText(NewGroup.this, "Insertion Call failed", Toast.LENGTH_SHORT).show();
                    Log.e(TAG+" insertion", t.getMessage());
                }
            });
        }
        else
        {
            Toast.makeText(AddExpense.this, "Choose a method of share", Toast.LENGTH_SHORT).show();
        }


    }

    //remove
//    private void sendEmail(ExpenseCreationInput input) {
//
//        String group_name=g_name;
//        String expense_name=input.getExpenseName();
//        String tot_amount=input.getExpenseAmount();
//        String self_member_id=input.getMemberId();
//        List<String> other_memberIds=input.getMemberIds();
//
//        fetchUserName();
//
//        List<String> others_shareAmount=input.getShareAmount();
//    }
//
//    private void fetchUserName() {
//
//
//    }

    /**
     * this method is called when the pop up dialog box is closed
     * when pop up dialog closed the member list value is passed back
     * with onDismiss ,
     */

    @Override
    public void onListSetting(ArrayList<ExpenseMemberData> memberData) {

        this.arrayListWithData=memberData;

        ExpenseMemberData memberDatalist;

        for (int i = 0; i < member_list.size() ; i++) {
            memberDatalist=member_list.get(i);
            Log.d("AddExpense ","actual array: "+i+" "+memberDatalist.getMember_id()+" "+memberDatalist.getValue());

        }

        Log.d(TAG, "onListSetting:");

        ExpenseMemberData expenseMemberData;

        for (int i = 0; i < memberData.size() ; i++) {
            expenseMemberData=memberData.get(i);
            Log.d("AddExpense ","returned array list: "+i+" "+expenseMemberData.getMember_id()+" "+expenseMemberData.getValue());

        }

        initDataRecyclerView();


    }

    //remove

//    private void saveFunction1() {
//
//        if (!arrayListWithData.isEmpty()) {
//
//            Date c = Calendar.getInstance().getTime();
//            System.out.println("Current time => " + c);
//
//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//            String formattedDate = df.format(c);
//
//            String query = "insert into tb_expense(group_id,user_id,payment_id,expense_amount,expense_created_date) values(?,?,?,?,?)";
//            ArrayList<String> value = new ArrayList();
//            value.add(g_id);
//            value.add(user_id);
//            value.add(paymentId);
//            value.add(amount);
//            value.add(formattedDate);
//
//            QueryValue queryValue = new QueryValue();
//            queryValue.setQuery(query);
//            queryValue.setValue(value);
//
//            Call<CallResult> call = RetrofitClient.getInstance().getApi().insert(queryValue);
//
//            call.enqueue(new Callback<CallResult>() {
//                @Override
//                public void onResponse(Call<CallResult> call, Response<CallResult> response) {
//
//                    if (response.code() == 200) {
//
//                        CallResult selectResult = response.body();
//
//                        String success = "";
//                        if (selectResult != null) {
//                            success = selectResult.getStatus();
//
//                            if (success.equalsIgnoreCase("true")) {
//
//                                List<CallOutput> output = selectResult.getOutput();
//
//                                for(int i=0;i<output.size();i++) {
//                                    CallOutput item = output.get(i);
//                                    List<String> list = item.getValue();
//
//                                    String expense_id=list.get(0);
//
//                                    saveExpenseShare(expense_id);
//
//                                }
//
//
//                                } else {
//                                Log.e(TAG, success);
////                                Snackbar.make(linearLayout,"Selection Failed",Snackbar.LENGTH_LONG).show();
//                            }
//                        } else {
//                            Log.e(TAG, selectResult.toString());
////                            Snackbar.make(linearLayout,"Selection Failed",Snackbar.LENGTH_LONG).show();
//
//                        }
//                    } else {
//
//                        String s = "";
//                        try {
//                            if (response.errorBody() != null) {
//                                s = response.errorBody().string();
//
//                                Log.e("NewGroup insertion", "Error body is "+s);
//                            } else {
//                                Log.e("NewGroup insertion", "Error body is null");
//                            }
//                        } catch (IOException e) {
//                            Log.e("NewGroup insertion", e.getMessage());
//
//                        }
//                    }
//
//
//                }
//
//                @Override
//                public void onFailure(Call<CallResult> call, Throwable t) {
//                    //                Toast.makeText(NewGroup.this, "Insertion Call failed", Toast.LENGTH_SHORT).show();
//                    Log.e("NewGroup insertion", t.getMessage());
//                }
//            });
//        }
//        else
//        {
//            Toast.makeText(AddExpense.this, "Choose a method of share", Toast.LENGTH_SHORT).show();
//        }
//
//
//    }
//
//    private void saveExpenseShare(String expense_id) {
//
//        final int[] count = {0};
//
//        for(int i=0;i<arrayListWithData.size();i++) {
//
//            ExpenseMemberData expenseMemberData=arrayListWithData.get(i);
//            String query = "insert into tb_share (expense_id,member_id,share_amount) values(?,?,?)";
//            ArrayList<String> value = new ArrayList<>();
//            value.add(expense_id);
//            value.add(expenseMemberData.getMember_id());
//            value.add(""+expenseMemberData.getValue());
//
//            QueryValue queryValue = new QueryValue();
//            queryValue.setQuery(query);
//            queryValue.setValue(value);
//
//            Call<CallResult> call = RetrofitClient.getInstance().getApi().insert(queryValue);
//
//            call.enqueue(new Callback<CallResult>() {
//                @Override
//                public void onResponse(Call<CallResult> call, Response<CallResult> response) {
//
//                    if (response.code() == 200) {
//
//                        CallResult selectResult = response.body();
//
//                        String success = "";
//                        if (selectResult != null) {
//                            success = selectResult.getStatus();
//
//                            if (success.equalsIgnoreCase("true")) {
//
//                                count[0] = count[0] +1;
//
//                                if (count[0] == arrayListWithData.size())
//                                {
//                                    Snackbar.make(frameLayout,"Expense Added",Snackbar.LENGTH_LONG).show();
//                                }
//
//
//
//                            } else {
//                                Log.e(TAG, success);
//                            Snackbar.make(frameLayout,"Expense creation Failed",Snackbar.LENGTH_LONG).show();
//                            }
//                        } else {
//                            Log.e(TAG, selectResult.toString());
//                            Snackbar.make(frameLayout,"Expense creation Failed",Snackbar.LENGTH_LONG).show();
//
//                        }
//                    } else {
//
//                        String s = "";
//                        try {
//                            if (response.errorBody() != null) {
//                                s = response.errorBody().string();
//                                Log.e(TAG+"insertion","Error body is "+s);
//                            } else {
//                                Log.e(TAG+"insertion", "Error body is null");
//                            }
//                        } catch (IOException e) {
//                            Log.e(TAG+" insertion", e.getMessage());
//
//                        }
//                    }
//
//
//                }
//
//                @Override
//                public void onFailure(Call<CallResult> call, Throwable t) {
////                Toast.makeText(NewGroup.this, "Insertion Call failed", Toast.LENGTH_SHORT).show();
//                    Log.e(TAG+" insertion", t.getMessage());
//                }
//            });
//
//        }
//    }


}
