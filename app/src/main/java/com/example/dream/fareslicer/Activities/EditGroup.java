package com.example.dream.fareslicer.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dream.fareslicer.AdapterClasses.NewMemberListAdapter;
import com.example.dream.fareslicer.BeanClasses.MemberData;
import com.example.dream.fareslicer.DialogClass.ContactDialogClass;
import com.example.dream.fareslicer.Interface.ContactSetter;
import com.example.dream.fareslicer.R;
import com.example.dream.fareslicer.RetrofitClientAndInterface.RetrofitClient;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.QueryValue;
import com.google.android.material.snackbar.Snackbar;
import com.mynameismidori.currencypicker.CurrencyPicker;
import com.mynameismidori.currencypicker.CurrencyPickerListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditGroup extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG ="Edit Group" ;
    private static final int REQUEST_CODE = 0;

    private EditText group_name;
    private LinearLayout add_members;
    private RecyclerView member_recyclerView;
    private Button update;

    //    private ImageView currency_chooser;
//    private TextView group_currency;

    public static EditGroup editGroup =new EditGroup();

    String selected_user_id="";
    String selected_name="";
    String selected_number="";
    static ArrayList<MemberData> member_list=new ArrayList<>();

    MemberData memberData;
    private static RecyclerView.LayoutManager linearLayoutManager;
    private static NewMemberListAdapter adapter;
    private LinearLayout linearLayout;

    String group_id="";

    private static ProgressBar member_progress_bar;

    private static int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        flag=0;
        initView();

        member_list.clear();

        group_id=getIntent().getStringExtra("g_id");

        setUpdateData();


        initRecyclerView();

//        currency_chooser.setOnClickListener(this);
        add_members.setOnClickListener(this);
        update.setOnClickListener(this);

        group_name.addTextChangedListener(myTextChangeListener);

    }

    public TextWatcher myTextChangeListener=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            SharedPreferences sp=getSharedPreferences("User",MODE_PRIVATE);
            String user_id=sp.getString("user_id","");
            String g_name=group_name.getText().toString();

            if(!g_name.equals(""))
            {
                checkGroupName("check");
            }
            else
            {
                Toast.makeText(EditGroup.this, "Enter group name", Toast.LENGTH_SHORT).show();

            }

        }
    };

    private void initView() {

        //To set back button in tool bar
        Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Group");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        linearLayout=findViewById(R.id.new_group_layout);
        group_name=findViewById(R.id.edit_group_name);
//        currency_chooser=findViewById(R.id.edit_currency_select);
//        group_currency =findViewById(R.id.edit_profile_currency);

        add_members=findViewById(R.id.edit_layout_add_members);
        member_progress_bar=findViewById(R.id.edit_member_progress);
        member_progress_bar.setVisibility(View.GONE);
        member_recyclerView=findViewById(R.id.edit_group_recycler_view);
        update=findViewById(R.id.group_update);


    }

    //This function is used to fill data corresponding to group_id
    private void setUpdateData() {

        String query="select group_name,group_currency from tb_group where group_id=?";
        ArrayList<String> value=new ArrayList();
        value.add(group_id);

        QueryValue queryValue=new QueryValue();
        queryValue.setQuery(query);
        queryValue.setValue(value);

        Call<CallResult> call= RetrofitClient.getInstance().getApi().select(queryValue);

        call.enqueue(new Callback<CallResult>() {
            @Override
            public void onResponse(Call<CallResult> call, Response<CallResult> response) {


                if(response.code()==200) {

                    CallResult selectResult = response.body();

                    String success = "";
                    if (selectResult != null) {
                        success = selectResult.getStatus();

                        if (success.equalsIgnoreCase("true")) {

                            List<CallOutput> output = selectResult.getOutput();

                            CallOutput item = output.get(0);
                            List<String> list=item.getValue();
                            String g_name=list.get(0);
                            group_name.setText(g_name);

//                            String g_currency=list.get(1);
//                            group_currency.setText(g_currency);

                            membersSelectCall();

                        }
                        else
                        {
                            Snackbar.make(linearLayout,success,Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Log.e("NewGroup Selection","Response body is null");
                    }
                }
                else
                {
                    String s="";
                    try {
                        if (response.errorBody() != null) {
                            s=response.errorBody().string();
                            Log.e("NewGroup Selection","Error body is "+s);
                        }
                        else
                        {
                            Log.e("NewGroup Selection","Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e("NewGroup Selection",e.getMessage());

                    }
                }
            }

            @Override
            public void onFailure(Call<CallResult> call, Throwable t) {

                Log.e("NewGroup Selection",t.getMessage());
            }
        });
    }

    //This function fetches user_id and user_name members in corresponding group_id
    private void membersSelectCall() {

        String query="select t1.user_id,t2.user_name from tb_member t1, tb_user t2 where t2.user_id=t1.user_id and t1.group_id=?";
        ArrayList<String> value=new ArrayList();
        value.add(group_id);

        QueryValue queryValue=new QueryValue();
        queryValue.setQuery(query);
        queryValue.setValue(value);

        member_list.clear();

        Call<CallResult> call= RetrofitClient.getInstance().getApi().select(queryValue);

        call.enqueue(new Callback<CallResult>() {
            @Override
            public void onResponse(Call<CallResult> call, Response<CallResult> response) {

                if(response.code()==200) {

                    CallResult selectResult=response.body();

                    String success= "";
                    if (selectResult != null) {
                        success = selectResult.getStatus();

                        if (success.equalsIgnoreCase("true")) {

                            List<CallOutput> output = selectResult.getOutput();



                            for (int i=0;i<output.size();i++)
                            {
                                CallOutput item = output.get(i);
                                List<String> list=item.getValue();

                                String user_id=list.get(0);
                                String user_name=list.get(1);

                                memberData=new MemberData();
                                memberData.setSelected_user_id(user_id);
                                memberData.setSelected_name(user_name);

                                member_list.add(memberData);

                            }

                            adapter.notifyDataSetChanged();


                        } else {
                            Log.e("NewGroup Selection", "Success is false");
                        }
                    }
                    else
                    {
                        Log.e("NewGroup Selection", "Success is null ");
                    }

                }
                else {
                    String s="";
                    try {
                        if (response.errorBody() != null) {
                            s=response.errorBody().string();
                            Log.e("NewGroup Selection","Error body is "+s);
                        }
                        else
                        {
                            Log.e("NewGroup Selection","Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e("NewGroup Selection",e.getMessage());

                    }
                }
            }

            @Override
            public void onFailure(Call<CallResult> call, Throwable t) {

//                Toast.makeText(EditGroup.this, "Selection Call failed", Toast.LENGTH_SHORT).show();
                Log.e("NewGroup Selection", t.getMessage());
            }

        });
    }

    private void initRecyclerView() {

        linearLayoutManager=new LinearLayoutManager(EditGroup.this);
        adapter = new NewMemberListAdapter(EditGroup.this, member_list, group_id);
        member_recyclerView.setLayoutManager(linearLayoutManager);
        member_recyclerView.setAdapter(adapter);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //To add the selected people to this page
    private void setMemberDataList(String selected_user_id, String selected_name, String selected_number) {

        memberData=new MemberData();
        memberData.setSelected_user_id(selected_user_id);
        memberData.setSelected_name(selected_name);
        memberData.setSelected_number(selected_number);

        member_list.add(memberData);

        member_progress_bar.setVisibility(View.GONE);

        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View view) {

        if(view==add_members)
        {

//            memberAddDialog();

            Intent intent=new Intent(EditGroup.this,ContactListActivity.class);
            intent.putExtra("pageType","edit");
            startActivityForResult(intent,REQUEST_CODE);
        }
        else if(view==update)
        {
            checkGroupName("update");
        }
    }

//remove

//    private void currencyPicker() {
//
//        final CurrencyPicker picker = CurrencyPicker.newInstance("Select Currency");  // dialog title
//
//        picker.show(getSupportFragmentManager(), "CURRENCY_PICKER");
//
//        picker.setListener(new CurrencyPickerListener() {
//            @Override
//            public void onSelectCurrency(String name, String code, String symbol, int flagDrawableResID) {
//                // Implement your code here
//
//                Toast.makeText(EditGroup.this, "Currency "+name, Toast.LENGTH_SHORT).show();
////                        im_currency.setImageDrawable();
//
//                if(code.equalsIgnoreCase("INR"))
//                {
//                    group_currency.setText(R.string.Rs);
//                }
//                else {
//                    group_currency.setText(symbol);
//                }
//                picker.dismiss();
//            }
//
//        });
//
//    }
//
    private void memberAddDialog() {

        member_progress_bar.setVisibility(View.VISIBLE);
        //Dialog for contact list
        ContactDialogClass contactDialogClass=new ContactDialogClass(EditGroup.this,"edit");
        contactDialogClass.setCancelable(true);
        contactDialogClass.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        contactDialogClass.show();

    }


    //if check is true ,we don't use this member list in this function
    private void checkGroupName(final String function) {

        String g_name=group_name.getText().toString();

        String query="select group_id from tb_group where group_name=? and group_id<>?";
        ArrayList<String> value=new ArrayList();
        value.add(g_name);
        value.add(group_id);

        QueryValue queryValue=new QueryValue();
        queryValue.setQuery(query);
        queryValue.setValue(value);

        Call<CallResult> call=RetrofitClient.getInstance().getApi().select(queryValue);

        call.enqueue(new Callback<CallResult>() {
            @Override
            public void onResponse(Call<CallResult> call, Response<CallResult> response) {

                if (response.code() == 200) {

                    CallResult selectResult = response.body();

                    String success = "";
                    if (selectResult != null) {
                        success = selectResult.getStatus();

                        if (success.equalsIgnoreCase("true")) {

                            group_name.findFocus();
                            Snackbar.make(linearLayout,"Group name alreay exists...",Snackbar.LENGTH_LONG).show();
                            Toast.makeText(EditGroup.this, "Group name alreay exists...", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            if(function.equalsIgnoreCase("update"))
                            {
                                updateFunction();
                            }
                            else
                            {
                                Log.d(TAG,"Continue...");
                            }


                        }
                    }
                }
                else
                {
                    String s="";
                    try {
                        if (response.errorBody() != null) {
                            s=response.errorBody().string();
                            Log.e(TAG+" updation","Error body is "+s);
                        }
                        else
                        {
                            Log.e(TAG+" updation","Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e(TAG+" updation",e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CallResult> call, Throwable t) {

//                Toast.makeText(EditGroup.this, "Insertion Call failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG+" updation", t.getMessage());
            }
        });
    }

    private void updateFunction() {

        SharedPreferences sp=getSharedPreferences("User",MODE_PRIVATE);
        String user_id=sp.getString("user_id","");
        String g_name=group_name.getText().toString();
        ArrayList<MemberData> g_memberList=member_list;
        //Also needs member_list


        updateGroupCall(user_id,g_name);

    }

    private void updateGroupCall(String user_id, String g_name) {

        String query="update tb_group set group_name=?,user_id=? where group_id=?";
        ArrayList<String> value=new ArrayList();

        value.add(g_name);
        value.add(user_id);
        value.add(group_id);

        QueryValue queryValue=new QueryValue();
        queryValue.setQuery(query);
        queryValue.setValue(value);

        Call<CallResult> call=RetrofitClient.getInstance().getApi().insert(queryValue);

        call.enqueue(new Callback<CallResult>() {
            @Override
            public void onResponse(Call<CallResult> call, Response<CallResult> response) {
                if(response.code()==200) {

                    CallResult selectResult = response.body();

                    String success = "";
                    if (selectResult != null) {
                        success = selectResult.getStatus();

                        if (success.equalsIgnoreCase("true")) {

                            insertMembersCall();

                        }
                        else
                        {
                            Log.e(TAG,success);
                            Snackbar.make(linearLayout,"Updation Failed",Snackbar.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Log.e(TAG,selectResult.toString());
                        Snackbar.make(linearLayout,"Updation Failed",Snackbar.LENGTH_LONG).show();

                    }
                }
                else
                {
                    String s="";
                    try {
                        if (response.errorBody() != null) {
                            s=response.errorBody().string();
                            Log.e(TAG+" updation","Error body is "+s);
                        }
                        else
                        {
                            Log.e(TAG+" updation","Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e(TAG+" updation",e.getMessage());

                    }
                }
            }

            @Override
            public void onFailure(Call<CallResult> call, Throwable t) {
                Log.e(TAG+" updation", t.getMessage());
            }
        });
    }

    private void insertMembersCall() {

        for (int i = 0; i < member_list.size(); i++) {

            checkGroupMember(group_id,member_list.get(i).getSelected_user_id());


        }
    }

    private void checkGroupMember(final String group_id, final String selected_user_id) {

        String query = "select member_id from tb_member where group_id=? and user_id=?";
        ArrayList<String> value = new ArrayList();
        value.add(group_id);
        value.add(selected_user_id);

        QueryValue queryValue = new QueryValue();
        queryValue.setQuery(query);
        queryValue.setValue(value);

        Call<CallResult> call = RetrofitClient.getInstance().getApi().select(queryValue);

        call.enqueue(new Callback<CallResult>() {
            @Override
            public void onResponse(Call<CallResult> call, Response<CallResult> response) {

                flag=flag+1;//This is given to know when to mark the insertion as complete and redirects to a page.

                if (response.code() == 200) {

                    CallResult selectResult = response.body();

                    String success = "";
                    if (selectResult != null) {
                        success = selectResult.getStatus();

                        if (success.equalsIgnoreCase("true")) {

                            List<CallOutput> callOutputList=selectResult.getOutput();
                            CallOutput callOutput=callOutputList.get(0);

                            if (member_list.size() == flag) {

                                Snackbar.make(linearLayout, "Successfully Updated Group", Snackbar.LENGTH_LONG).show();
                                Intent intent = new Intent(EditGroup.this, Home.class);
                                intent.putExtra("tab_position", 1);
                                startActivity(intent);

                            } else {
//                                    g_memberList.remove(g_memberList.indexOf(u_id));
                                Log.e(TAG, "has member Id so skipped"+(callOutput.getValue()).get(0));
                            }

                        } else {

                            insertMembers(group_id,selected_user_id);
                        }
                    } else {
                        Log.e(TAG, selectResult.toString());

                    }
                } else {

                    String s = "";
                    try {
                        if (response.errorBody() != null) {
                            s = response.errorBody().string();
                            Log.e(TAG+" updation", "Error body is " + s);
                        } else {
                            Log.e(TAG+" updation", "Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e(TAG+" updation", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CallResult> call, Throwable t) {
//                Toast.makeText(NewGroup.this, "Insertion Call failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG + " updation" , t.getMessage());
            }
        });



    }

    private void insertMembers(String group_id, String selected_user_id) {

        String query = "insert into tb_member(group_id,user_id)values(?,?)";
        ArrayList<String> value = new ArrayList();
        value.add(group_id);
        value.add(selected_user_id);

        QueryValue queryValue = new QueryValue();
        queryValue.setQuery(query);
        queryValue.setValue(value);

        Call<CallResult> call = RetrofitClient.getInstance().getApi().insert(queryValue);

        call.enqueue(new Callback<CallResult>() {
            @Override
            public void onResponse(Call<CallResult> call, Response<CallResult> response) {

                if (response.code() == 200) {

                    CallResult selectResult = response.body();

                    String success = "";
                    if (selectResult != null) {
                        success = selectResult.getStatus();

                        if (success.equalsIgnoreCase("true")) {

                            if (member_list.size() == flag) {
                                Snackbar.make(linearLayout, "Successfully Updated Group", Snackbar.LENGTH_LONG).show();
                                Intent intent = new Intent(EditGroup.this, Home.class);
                                intent.putExtra("tab_position", 1);
                                startActivity(intent);
                            } else {
//                                    g_memberList.remove(g_memberList.indexOf(u_id));
                                Log.d(TAG, "1");

                            }
                        } else {
                            Log.e(TAG, success);
                        }
                    } else {
                        Log.e(TAG, selectResult.toString());

                    }
                } else {

                    String s = "";
                    try {
                        if (response.errorBody() != null) {
                            s = response.errorBody().string();
                            Log.e(TAG+" updation", "Error body is " + s);
                        } else {
                            Log.e(TAG+" updation", "Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e(TAG+" updation", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CallResult> call, Throwable t) {
//                Toast.makeText(NewGroup.this, "Insertion Call failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG+" updation", t.getMessage());
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==0) {
            //get id, name and number
            if (data != null) {

                selected_user_id = data.getStringExtra("id");
                selected_name = data.getStringExtra("name");
                selected_number = data.getStringExtra("number");

                if (selected_user_id!=null)//for not calling data set function if user_id is null
                    setMemberDataList(selected_user_id, selected_name, selected_number);

            }
        }

    }

    //remove
//    @Override
//    public void setContact(String id, String name, String number) {
//
//        selected_user_id=id;
//        selected_name=name;
//        selected_number=number;
//
//        setMemberDataList(selected_user_id,selected_name,selected_number);
//
//    }


}
