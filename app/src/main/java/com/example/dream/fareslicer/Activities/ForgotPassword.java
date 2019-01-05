package com.example.dream.fareslicer.Activities;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dream.fareslicer.R;
import com.example.dream.fareslicer.RetrofitClientAndInterface.RetrofitClient;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.QueryValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ForgotPassword extends AppCompatActivity {


    private static final String TAG = "Forgot Passwor";
    EditText pin,new_pass,confirm_pass;
    Button save;
    static int set=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        set=0;
        initView();


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(set==0)
                {
                    checkPinCall(pin.getText().toString());
                }
                else if(set==1)
                {
                    if(new_pass.getText().toString().equalsIgnoreCase(""))
                    {
                        Toast.makeText(ForgotPassword.this, "Enter new password", Toast.LENGTH_SHORT).show();
                    }
                    else if (confirm_pass.getText().toString().equalsIgnoreCase(""))
                    {
                        Toast.makeText(ForgotPassword.this, "Enter confirm password", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                        if(new_pass.getText().toString().equals(confirm_pass.getText().toString()))
                        {
                            updatePasswordCall();
                        }
                        else
                        {
                            Toast.makeText(ForgotPassword.this, "Password doesn't Match", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        });

    }



    private void checkPinCall(String s) {

        SharedPreferences sp=getSharedPreferences("User",MODE_PRIVATE);
        String user_id=sp.getString("user_id","");
        String query="select user_id from tb_user where temp_password=? and user_id=?";
        ArrayList<String> value=new ArrayList();
        value.add(s);
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

                            set=1;
                                pin.setVisibility(View.GONE);
                                new_pass.setVisibility(View.VISIBLE);
                                confirm_pass.setVisibility(View.VISIBLE);

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

    private void initView() {

        pin=findViewById(R.id.pin_send);
        new_pass=findViewById(R.id.new_password);
        confirm_pass=findViewById(R.id.confirm_password);

        save=findViewById(R.id.password_save);

        new_pass.setVisibility(View.GONE);
        confirm_pass.setVisibility(View.GONE);

    }

    private void updatePasswordCall() {

        SharedPreferences sp=getSharedPreferences("User",MODE_PRIVATE);
        String user_id=sp.getString("user_id","");

        String query="update tb_user set user_password=? where user_id=?";
        ArrayList<String> value=new ArrayList();
        value.add(new_pass.getText().toString());
        value.add(user_id);

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

                            SharedPreferences sharedPreferences=getSharedPreferences("User",MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putBoolean("Password",true);
                            editor.apply();


                            Intent intent=new Intent(ForgotPassword.this,Home.class);
                            startActivity(intent);
                            finish();

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

}
