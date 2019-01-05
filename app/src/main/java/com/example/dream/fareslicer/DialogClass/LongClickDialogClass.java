package com.example.dream.fareslicer.DialogClass;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.dream.fareslicer.Activities.EditGroup;
import com.example.dream.fareslicer.R;
import com.example.dream.fareslicer.RetrofitClientAndInterface.RetrofitClient;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.QueryValue;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupDeletion.GroupDeletionInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupDeletion.GroupDeletionOutput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LongClickDialogClass extends Dialog {

    private static final String TAG = "Long Click Dialog" ;
    LinearLayout edit,delete;

    Context context;
    String g_id="";


    public LongClickDialogClass(Context context, String group_id) {
        super(context);

        this.context=context;
        g_id=group_id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_long_click);

        initView();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fetchGroupDetailCall(); //To get user id of the creator..
//                editCall();

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                deleteCall();
                deleteCheckCall();

            }
        });

    }

    private void fetchGroupDetailCall() {


        String query="select user_id from tb_group where group_id=?";
        ArrayList<String> value=new ArrayList();
        value.add(g_id);

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

                                List<String> value=callOutput.getValue();

                                String user_id=value.get(0);
                                SharedPreferences sp=context.getSharedPreferences("User",context.MODE_PRIVATE);
                                String current_user_id=sp.getString("user_id","");
                                if(current_user_id.equalsIgnoreCase(user_id))
                                {
                                    editCall();
                                }
                                else
                                {
                                    Toast.makeText(context, "Only group owner can edit", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }

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

    private void deleteCheckCall() {

        SharedPreferences sp= context.getSharedPreferences("User",Context.MODE_PRIVATE);
        String user_id=sp.getString("user_id","");

//
//        String query="select group_id from tb_group where group_id=? and user_id=? ";
//        ArrayList<String> value=new ArrayList();
//        value.add(g_id);
//        value.add(user_id);
//
//
//        QueryValue queryValue=new QueryValue();
//        queryValue.setQuery(query);
//        queryValue.setValue(value);


        GroupDeletionInput input=new GroupDeletionInput();
        input.setGroupId(g_id);
        input.setUserId(user_id);

        Call<GroupDeletionOutput> call= RetrofitClient.getInstance().getApi().groupDeletion(input);

        call.enqueue(new Callback<GroupDeletionOutput>() {
            @Override
            public void onResponse(Call<GroupDeletionOutput> call, Response<GroupDeletionOutput> response) {

                if(response.code()==200) {

                    GroupDeletionOutput selectResult = response.body();

                    String success = "";
                    if (selectResult != null) {
                        success = selectResult.getStatus();

                        if (success.equalsIgnoreCase("true")) {

                            Toast.makeText(context, "Deletion Successful", Toast.LENGTH_SHORT).show();
//                            deleteCall();
                        }
                        else if(success.equalsIgnoreCase("false"))
                        {
                            Log.e("deletion", "deletion is false");
                            Toast.makeText(context, "Only user created can delete Group", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }
                    else
                    {
                        Log.e("deletion", "deletion is false");
                    }
                }
                else
                {
                    String s="";
                    try {
                        if (response.errorBody() != null) {
                            s=response.errorBody().string();
                            Log.e("deletion","Error body is "+s);
                        }
                        else
                        {
                            Log.e("deletion","Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e("deletion",e.getMessage());

                    }
                }
            }

            @Override
            public void onFailure(Call<GroupDeletionOutput> call, Throwable t) {

                Log.e("deletion", t.getMessage() );
            }
        });


    }

    private void initView() {

        edit=findViewById(R.id.edit);
        delete=findViewById(R.id.delete);
    }

    private void editCall() {

        Intent intent=new Intent(context,EditGroup.class);
        intent.putExtra("g_id",g_id);
        context.startActivity(intent);
        dismiss();

    }

//remove
    private void deleteCall() {

        String query="DELETE FROM tb_group WHERE group_id=?";
        ArrayList<String> value=new ArrayList();
        value.add(g_id);

        QueryValue queryValue=new QueryValue();
        queryValue.setQuery(query);
        queryValue.setValue(value);

        Call<CallResult> call= RetrofitClient.getInstance().getApi().insert(queryValue);

        call.enqueue(new Callback<CallResult>() {
            @Override
            public void onResponse(Call<CallResult> call, Response<CallResult> response) {

                if(response.code()==200) {

                    CallResult selectResult = response.body();

                    String success = "";
                    if (selectResult != null) {
                        success = selectResult.getStatus();

                        if (success.equalsIgnoreCase("true")) {

                            Toast.makeText(context, "Deletion Successful", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                        else
                        {
                            Log.e("deletion", "deletion is false");
                        }
                    }
                    else
                    {
                        Log.e("deletion", "deletion is false");
                    }
                }
                else
                {
                    String s="";
                    try {
                        if (response.errorBody() != null) {
                            s=response.errorBody().string();
                            Log.e("deletion","Error body is "+s);
                        }
                        else
                        {
                            Log.e("deletion","Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e("deletion",e.getMessage());

                    }
                }
                        }

            @Override
            public void onFailure(Call<CallResult> call, Throwable t) {

                Log.e("deletion", t.getMessage() );
            }
        });



    }

//remove
    //    private void selectCall(String tb_phn) {
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
//                            CallOutput item = output.get(0);
//                            List<String> list=item.getValue();
//                            String id=list.get(0);
//
//                            SharedPreferences sharedPreferences=getSharedPreferences("User",MODE_PRIVATE);
//                            SharedPreferences.Editor editor=sharedPreferences.edit();
//                            editor.putString("user_id",id);
//                            editor.apply();
//
//                            startActivity(new Intent(ProfilePage.this,Home.class));
//
//                        } else {
//                            Log.e("Selection", "Success is false");
//                        }
//                    }
//                    else
//                    {
//                        Log.e("Selection", "Success is null ");
//                    }
//
//                }
//                else {
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CallResult> call, Throwable t) {
//
//                Toast.makeText(ProfilePage.this, "Selection Call failed", Toast.LENGTH_SHORT).show();
//                Log.e("Selction", t.getMessage());
//            }
//
//        });
//    }


}
