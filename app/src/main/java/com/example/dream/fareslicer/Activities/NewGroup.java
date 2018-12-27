package com.example.dream.fareslicer.Activities;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewGroup extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG ="New Group" ;
    private static final int REQUEST_CODE = 0;
    private EditText group_name;
    private LinearLayout add_members;
    private RecyclerView member_recyclerView;
    private Button save;

    public static NewGroup newGroup =new NewGroup();

    String selected_user_id=""; //To know which user is selected
    String selected_name="";    //To know selected user's name
    String selected_number="";  // To know selected user's number

    static ArrayList<MemberData> member_list=new ArrayList<>();

    MemberData memberData;
    private static RecyclerView.LayoutManager linearLayoutManager;
    private static NewMemberListAdapter adapter;
    private LinearLayout linearLayout;

    String group_created_date="";
    public static ProgressBar member_progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        initView();

        member_list.clear();

//        This is for making ourselves a member of group
        SharedPreferences sp=getSharedPreferences("User",MODE_PRIVATE);
        MemberData memberData=new MemberData();
        memberData.setSelected_user_id(sp.getString("user_id",""));
        memberData.setSelected_name(sp.getString("user_name",""));
        member_list.add(memberData);


        //To set group created date
        Calendar calendar=Calendar.getInstance();
        Date date=calendar.getTime();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        group_created_date=simpleDateFormat.format(date);


        initRecyclerView();

//        group_id=getIntent().getStringExtra("g_id");
//
//        if(!group_id.equalsIgnoreCase(""))
//        {
////            dataFillCall();
//        }

//        currency_chooser.setOnClickListener(this);
        add_members.setOnClickListener(this);
        save.setOnClickListener(this);

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

//member list is passed only because the call need this member list and it is not used in that function if check is true
//                    selectGroupId(g_name,"true");
                checkGroupName("check");

            }
            else
            {
//                Toast.makeText(NewGroup.this, "Enter group name", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"Enter group name");

            }

        }
    };

    @Override
    protected void onStart() {
        super.onStart();


        initView();
        initRecyclerView();

    }

    private void initView() {

        //To set back button in tool bar
        Objects.requireNonNull(getSupportActionBar()).setTitle("Add Group");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        linearLayout=findViewById(R.id.new_group_layout);
        group_name=findViewById(R.id.new_group_name);
//        currency_chooser=findViewById(R.id.profile_currency_select);
//        group_currency =findViewById(R.id.profile_currency);

        add_members=findViewById(R.id.layout_add_members);
        member_progress_bar=findViewById(R.id.group_member_progress);
        member_progress_bar.setVisibility(View.GONE);
        member_recyclerView=findViewById(R.id.group_recycler_view);
        save=findViewById(R.id.group_save);


    }

    private void initRecyclerView() {

        linearLayoutManager=new LinearLayoutManager(NewGroup.this);
        adapter = new NewMemberListAdapter(NewGroup.this, member_list);
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

//    remove

//    //This function is used to fill data corresponding to group_id



//    private void dataFillCall() {
//
//        String query="select group_name,group_currency from tb_group where group_id=?";
//        ArrayList<String> value=new ArrayList();
//        value.add(group_id);
//
//        QueryValue queryValue=new QueryValue();
//        queryValue.setQuery(query);
//        queryValue.setValue(value);
//
//        Call<CallResult> call= RetrofitClient.getInstance().getApi().select(queryValue);
//
//        call.enqueue(new Callback<CallResult>() {
//            @Override
//            public void onResponse(Call<CallResult> call, Response<CallResult> response) {
//
//
//                if(response.code()==200) {
//
//                    CallResult selectResult = response.body();
//
//                    String success = "";
//                    if (selectResult != null) {
//                        success = selectResult.getStatus();
//
//                        if (success.equalsIgnoreCase("true")) {
//
//                            List<CallOutput> output = selectResult.getOutput();
//
//                            CallOutput item = output.get(0);
//                            List<String> list=item.getValue();
//                            String g_name=list.get(0);
//                            String g_currency=list.get(1);
//
//                            group_name.setText(g_name);
//                            group_currency.setText(g_currency);
//
//                            membersSelectCall();
//
//
//                        }
//                        else
//                        {
//                            Snackbar.make(linearLayout,success,Snackbar.LENGTH_SHORT).show();
//                        }
//                    }
//                    else
//                    {
//                        Log.e("NewGroup Selection","Response body is null");
//                    }
//                }
//                else
//                {
//                    String s="";
//                    try {
//                        if (response.errorBody() != null) {
//                            s=response.errorBody().string();
//                            Log.e("NewGroup Selection","Error body is "+s);
//                        }
//                        else
//                        {
//                            Log.e("NewGroup Selection","Error body is null");
//                        }
//                    } catch (IOException e) {
//                        Log.e("NewGroup Selection",e.getMessage());
//
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CallResult> call, Throwable t) {
//
//                Log.e("NewGroup Selection",t.getMessage());
//            }
//        });
//    }
//
//    //This function fetches user_id and user_name members in corresponding group_id
//    private void membersSelectCall() {
//
//        String query="select t1.user_id,t2.user_name from tb_member t1, tb_user t2 where t2.user_id=t1.user_id and t1.group_id=?";
//        ArrayList<String> value=new ArrayList();
//        value.add(group_id);
//
//        QueryValue queryValue=new QueryValue();
//        queryValue.setQuery(query);
//        queryValue.setValue(value);
//
//        member_list.clear();
//
//        Call<CallResult> call= RetrofitClient.getInstance().getApi().select(queryValue);
//
//        call.enqueue(new Callback<CallResult>() {
//            @Override
//            public void onResponse(Call<CallResult> call, Response<CallResult> response) {
//
//                if(response.code()==200) {
//
//                    CallResult selectResult=response.body();
//
//                    String success= "";
//                    if (selectResult != null) {
//                        success = selectResult.getStatus();
//
//                        if (success.equalsIgnoreCase("true")) {
//
//                            List<CallOutput> output = selectResult.getOutput();
//
//
//
//                            for (int i=0;i<output.size();i++)
//                            {
//                                CallOutput item = output.get(i);
//                                List<String> list=item.getValue();
//
//                                String user_id=list.get(0);
//                                String user_name=list.get(1);
//
//                                memberData=new MemberData();
//                                memberData.setSelected_user_id(user_id);
//                                memberData.setSelected_name(user_name);
//
//                                member_list.add(memberData);
//
//                            }
//
//                            adapter.notifyDataSetChanged();
//
//
//                        } else {
//                            Log.e("NewGroup Selection", "Success is false");
//                        }
//                    }
//                    else
//                    {
//                        Log.e("NewGroup Selection", "Success is null ");
//                    }
//
//                }
//                else {
//                    String s="";
//                    try {
//                        if (response.errorBody() != null) {
//                            s=response.errorBody().string();
//                            Log.e("NewGroup Selection","Error body is "+s);
//                        }
//                        else
//                        {
//                            Log.e("NewGroup Selection","Error body is null");
//                        }
//                    } catch (IOException e) {
//                        Log.e("NewGroup Selection",e.getMessage());
//
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CallResult> call, Throwable t) {
//
//                Toast.makeText(NewGroup.this, "Selection Call failed", Toast.LENGTH_SHORT).show();
//                Log.e("NewGroup Selection", t.getMessage());
//            }
//
//        });
//    }

    private void saveFunction() {

        SharedPreferences sp=getSharedPreferences("User",MODE_PRIVATE);
        String user_id=sp.getString("user_id","");
        String g_name=group_name.getText().toString();
//        String g_currency=group_currency.getText().toString();
        ArrayList<MemberData> g_memberList=member_list;
        //Also needs member_list
        String date=group_created_date;


        insertGroupCall(user_id,g_name,date);

    }

    //if check is true ,we don't use this member list in this function
    private void checkGroupName(final String function) {

        String g_name=group_name.getText().toString();
        String query="select group_id from tb_group where group_name=?";
        ArrayList<String> value=new ArrayList();
        value.add(g_name);

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
                                Toast.makeText(NewGroup.this, "Group name alreay exists...", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            if (function.equalsIgnoreCase("save"))
                            {
                                saveFunction();
                            }
                            else if (function.equalsIgnoreCase("check"))
                            {
                                Log.d("NewGroup ","Not insertion::: Continue...");
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
                            Log.e("NewGroup insertion","Error body is "+s);
                        }
                        else
                        {
                            Log.e("NewGroup insertion","Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e("NewGroup insertion",e.getMessage());
                    }
                }
            }
            @Override
            public void onFailure(Call<CallResult> call, Throwable t) {

                Toast.makeText(NewGroup.this, "Insertion Call failed", Toast.LENGTH_SHORT).show();
                Log.e("NewGroup insertion", t.getMessage());
            }
        });
    }

    private void insertGroupCall(String user_id, final String g_name, String date) {

        String query="insert into tb_group(group_name,user_id,group_date_created) values(?,?,?)";
        ArrayList<String> value=new ArrayList();
        value.add(g_name);
        value.add(user_id);
        value.add(date);

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

//                            List<CallOutput> callOutputList=selectResult.getOutput();
//                            CallOutput callOutput=callOutputList.get(0);
//                            List<String> value=callOutput.getValue();
//                            String groupId=value.get(0);

                            selectGroupId(g_name);


                        }

                        else
                        {
                            Log.e(TAG,success);
                            Snackbar.make(linearLayout,"Insertion Failed",Snackbar.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Log.e(TAG,selectResult.toString());
                        Snackbar.make(linearLayout,"Insertion Failed",Snackbar.LENGTH_LONG).show();

                    }
                }
                else
                {
                    String s="";
                    try {
                        if (response.errorBody() != null) {
                            s=response.errorBody().string();
                            Log.e("NewGroup insertion","Error body is "+s);
                        }
                        else
                        {
                            Log.e("NewGroup insertion","Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e("NewGroup insertion",e.getMessage());

                    }
                }
            }
            @Override
            public void onFailure(Call<CallResult> call, Throwable t) {

//                Toast.makeText(NewGroup.this, "Insertion Call failed", Toast.LENGTH_SHORT).show();
                Log.e("NewGroup insertion", t.getMessage());
            }
        });
    }


    private void selectGroupId(String g_name) {

        SharedPreferences sp=getSharedPreferences("User",MODE_PRIVATE);
        String user_id=sp.getString("user_id","");
        String query="select group_id from tb_group where group_name=? and user_id=?";
        ArrayList<String> value=new ArrayList();
        value.add(g_name);
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

                            List<CallOutput> output = selectResult.getOutput();
                            CallOutput item = output.get(0);
                            List<String> list=item.getValue();

                            String g_id=list.get(0);
                            insertMembersCall(g_id);

                            //remove
//                                insertMembersCall(g_id,g_memberList);
//                            else
//                            {
//                                group_name.findFocus();
//                                Snackbar.make(linearLayout,"Group name alreay exists...",Snackbar.LENGTH_LONG).show();
//                                Toast.makeText(NewGroup.this, "Group name alreay exists...", Toast.LENGTH_SHORT).show();
//                            }

                        }
                        else
                        {
                            //remove
//                            if(check.equalsIgnoreCase("false")) {
//
//                                Log.e(TAG,"Selection failed No group_id Found"+selectResult.toString());
//                            }
//                            else if (check.equalsIgnoreCase("true"))
//                            {
                            //Continue with currency selection
                            Log.e(TAG,"No group_id Found So no repetition continue with currency"+selectResult.toString());

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
                            Log.e("NewGroup insertion","Error body is "+s);
                        }
                        else
                        {
                            Log.e("NewGroup insertion","Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e("NewGroup insertion",e.getMessage());

                    }
                }


            }

            @Override
            public void onFailure(Call<CallResult> call, Throwable t) {

                Toast.makeText(NewGroup.this, "Insertion Call failed", Toast.LENGTH_SHORT).show();
                Log.e("NewGroup insertion", t.getMessage());
            }
        });
    }


//    remove


//    private void updateGroupCall(String user_id, String g_name, String g_currency, String date) {
//
//        String query="update tb_group set group_name=?,group_currency=?,user_id=?,group_date_created=? where group_id=?";
//        ArrayList<String> value=new ArrayList();
//
//        value.add(g_name);
//        value.add(g_currency);
//        value.add(user_id);
//        value.add(date);
//        value.add(group_id);
//
//        QueryValue queryValue=new QueryValue();
//        queryValue.setQuery(query);
//        queryValue.setValue(value);
//
//        Call<CallResult> call=RetrofitClient.getInstance().getApi().insert(queryValue);
//
//        call.enqueue(new Callback<CallResult>() {
//            @Override
//            public void onResponse(Call<CallResult> call, Response<CallResult> response) {
//                if(response.code()==200) {
//
//                    CallResult selectResult = response.body();
//
//                    String success = "";
//                    if (selectResult != null) {
//                        success = selectResult.getStatus();
//
//                        if (success.equalsIgnoreCase("true")) {
//
//                            insertMembersCall(group_id);
//
//                        }
//                        else
//                        {
//                            Log.e(TAG,success);
//                            Snackbar.make(linearLayout,"Updation Failed",Snackbar.LENGTH_LONG).show();
//                        }
//                    }
//                    else
//                    {
//                        Log.e(TAG,selectResult.toString());
//                        Snackbar.make(linearLayout,"Updation Failed",Snackbar.LENGTH_LONG).show();
//
//                    }
//                }
//                else
//                {
//                    String s="";
//                    try {
//                        if (response.errorBody() != null) {
//                            s=response.errorBody().string();
//                            Log.e("NewGroup updation","Error body is "+s);
//                        }
//                        else
//                        {
//                            Log.e("NewGroup updation","Error body is null");
//                        }
//                    } catch (IOException e) {
//                        Log.e("NewGroup updation",e.getMessage());
//
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CallResult> call, Throwable t) {
//                Log.e("NewGroup updation", t.getMessage());
//            }
//        });
//    }

    private void insertMembersCall(String groupId) {

        for (int i = 0; i < member_list.size(); i++) {

            String query = "insert into tb_member(group_id,user_id)values(?,?)";
            ArrayList<String> value = new ArrayList();
            value.add(groupId);
            value.add(member_list.get(i).getSelected_user_id());

            final int j = i;

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

                                if (member_list.size() - 1 == j) {
                                    Snackbar.make(linearLayout, "Successfully Created Group", Snackbar.LENGTH_LONG).show();
                                    Intent intent = new Intent(NewGroup.this, Home.class);
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
                                Log.e("NewGroup insertion", "Error body is " + s);
                            } else {
                                Log.e("NewGroup insertion", "Error body is null");
                            }
                        } catch (IOException e) {
                            Log.e("NewGroup insertion", e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<CallResult> call, Throwable t) {
//                Toast.makeText(NewGroup.this, "Insertion Call failed", Toast.LENGTH_SHORT).show();
                    Log.e("NewGroup insertion", t.getMessage());
                }
            });

        }
    }

    @Override
    public void onClick(View view) {

        //remove
            if(view==add_members)
        {
//            member_progress_bar.setVisibility(View.VISIBLE);
//            memberAddDialog();



            Intent intent=new Intent(NewGroup.this,ContactListActivity.class);
            intent.putExtra("pageType","add");
            startActivityForResult(intent,REQUEST_CODE);


        }
        else if(view==save)
        {
            checkGroupName("save");
        }
    }



    private void memberAddDialog() {


        //Dialog for contact list
        ContactDialogClass contactDialogClass=new ContactDialogClass(NewGroup.this,"add");
        contactDialogClass.setCancelable(true);
        contactDialogClass.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        contactDialogClass.show();


        contactDialogClass.setMyDialogListener(contactDialogClass);
        contactDialogClass.setCanceledOnTouchOutside(true);

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

//    @Override
//    public void setContact(String id, String name, String number) {
//
//        member_progress_bar.setVisibility(View.GONE);
//
//        selected_user_id=id;
//        selected_name=name;
//        selected_number=number;
//
//        setMemberDataList(selected_user_id,selected_name,selected_number);
//
//    }
}
