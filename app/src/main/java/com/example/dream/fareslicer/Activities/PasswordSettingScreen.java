package com.example.dream.fareslicer.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dream.fareslicer.R;
import com.example.dream.fareslicer.RetrofitClientAndInterface.RetrofitClient;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.QueryValue;
import com.example.dream.fareslicer.SupportClasses.PinMailSender;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordSettingScreen extends AppCompatActivity {

    private static final String TAG = "PasswordSettingScreen";
    String password_check="";
    int set=0;

    TextView password_heading,forgot_password;
    EditText password;
    FloatingActionButton set_fab;
    FrameLayout frameLayout;

    String user_id="";
    private String user_password="";
    private String user_email="";

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        password_check="";
        password.setText(password_check);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_setting_screen);

        SharedPreferences sp=getSharedPreferences("User",MODE_PRIVATE);
        user_id=sp.getString("user_id","");

//        user_password="1234"; remove

        fetchPasswordCall();

        initView();

        final SharedPreferences sharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
        Boolean pass=sharedPreferences.getBoolean("Password",false);

        passwordConditionCheck(pass); //This function determines the heading of this screen

        String heading=password_heading.getText().toString();

        if(heading.equalsIgnoreCase(getResources().getString(R.string.enter_password)))
        {
            forgot_password.setVisibility(View.VISIBLE);
        }
        else
        {
            forgot_password.setVisibility(View.GONE);
        }

        set_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(password.getText().toString().equalsIgnoreCase(""))
                {
                    Toast.makeText(PasswordSettingScreen.this, "Enter new password", Toast.LENGTH_SHORT).show();
                }
                else {

                    passwordSettingFunction();
                }


            }
        });


        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Boolean check=isOnline();
                if (check==true)
                {
                    forgotPasswordFunction();
                }
                else
                {
                    showAlertDialog();
                }

            }
        });

    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private void initView() {

        forgot_password=findViewById(R.id.forgot_password);
//        forgot_password.setVisibility(View.GONE);  change
        password_heading=findViewById(R.id.password_tv);
        password=findViewById(R.id.password_et);
        set_fab=findViewById(R.id.password_next);
        frameLayout=findViewById(R.id.password_frameLayout);

    }

    private void showAlertDialog() {
        //init alert dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Ooops! No Internet conncetion");
        builder.setMessage("Please turn on your internet connection!!");
        //set listeners for dialog buttons
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish the activity

                dialog.dismiss();
            }
        });

        //create the alert dialog and show it
        builder.create().show();
    }

    private void passwordConditionCheck(Boolean pass) {

        if(pass == true) {

            if (getIntent().getStringExtra("from").equalsIgnoreCase("splash")) {

                password_heading.setText(R.string.enter_password);
                set=3;
            }
            else if (getIntent().getStringExtra("from").equalsIgnoreCase("clear")){

                set=5;
                password_heading.setText(R.string.old_password);

            }else if (getIntent().getStringExtra("from").equalsIgnoreCase("nav_change")) {

//                Toast.makeText(PasswordSettingScreen.this, "Change password", Toast.LENGTH_SHORT).show();
                set=4;
                password_heading.setText(R.string.old_password);

            }
            else if (getIntent().getStringExtra("from").equalsIgnoreCase("nav_set")) {

                password_heading.setText(R.string.set_password);
            }
        }
        else
        {
            password_heading.setText(R.string.set_password);
        }

    }

    private void passwordSettingFunction() {

        if(set==0) //first password enter screen,here password s stored in temporary variable and then in next page its checked with this variable
        {
            password_check=password.getText().toString();
            set=1;
            password_heading.setText(R.string.confirm_password);
            password.setText("");
        }
        else if(set ==1) //password typed here is checked with the temporary variable and if true password is saved
        {
            String c_pass=password.getText().toString();
            if(password_check.equals(c_pass))
            {
                passwordUpdateCall(password_check,"update");

            }
            else
            {
                Toast.makeText(PasswordSettingScreen.this, "Password doesn't match", Toast.LENGTH_SHORT).show();

            }
        }
        else if (set==3) //here we check whether already set password and this password is equal or not
        {

            if(user_password.equals(password.getText().toString()))
            {
                Intent intent=new Intent(PasswordSettingScreen.this,Home.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Toast.makeText(PasswordSettingScreen.this, "Incorrect Password", Toast.LENGTH_SHORT).show();

                Snackbar.make(frameLayout,"Incorrect password!! Try again",Snackbar.LENGTH_SHORT).show();
            }

        }
        else if (set==4) //this is to change current password.. First check password is same,if yes goto set 0
        {
            if(user_password.equals(password.getText().toString()))
            {
                set=0;
                password_heading.setText(R.string.set_password);
                password.setText("");
            }
            else
            {
                Toast.makeText(PasswordSettingScreen.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                Snackbar.make(frameLayout,"Incorrect password!! Try again",Snackbar.LENGTH_SHORT).show();
            }

        }

        else if (set==5) // this is to clear already set password.. First enter already given password then if it's equal update password to null
        {

            if(user_password.equals(password.getText().toString()))
            {
                set=0;

                passwordUpdateCall("","clear");// to clear password

            }
            else
            {
                Toast.makeText(PasswordSettingScreen.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                Snackbar.make(frameLayout,"Incorrect password!! Try again",Snackbar.LENGTH_SHORT).show();
            }

        }

    }

    private void passwordUpdateCall(String newPassword, final String type) {

        String query="update tb_user set user_password='"+newPassword+"' where user_id=?";
        ArrayList<String> value=new ArrayList();
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

                            if(type.equalsIgnoreCase("update"))
                            {
                                Snackbar.make(frameLayout,"Updated password!!",Snackbar.LENGTH_LONG).show();

                                SharedPreferences sharedPreferences=getSharedPreferences("User",MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putBoolean("Password",true);
                                editor.apply();

                                Intent intent=new Intent(PasswordSettingScreen.this,Home.class);
                                startActivity(intent);
                                finish();
                            }
                            else if(type.equalsIgnoreCase("clear"))
                            {
                                Snackbar.make(frameLayout,"Cleared password!!",Snackbar.LENGTH_LONG).show();


                                SharedPreferences sharedPreferences=getSharedPreferences("User",MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putBoolean("Password",false);
                                editor.apply();


                                Intent intent=new Intent(PasswordSettingScreen.this,Home.class);
                                startActivity(intent);
                                finish();
                            }


                        }
                        else
                        {
                            Log.e(TAG,success);
                            if(type.equalsIgnoreCase("update"))
                            {
                                Snackbar.make(frameLayout,"Updation failed.. Try again!!",Snackbar.LENGTH_LONG).show();
                                Intent intent=new Intent(PasswordSettingScreen.this,PasswordSettingScreen.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();

                            }
                            else if(type.equalsIgnoreCase("clear"))
                            {
                                Snackbar.make(frameLayout,"Clearing password failed!!",Snackbar.LENGTH_LONG).show();
                                Intent intent=new Intent(PasswordSettingScreen.this,PasswordSettingScreen.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
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

    private void fetchPasswordCall() {

        String query="select user_password,user_email from tb_user where user_id=?";
        ArrayList<String> value=new ArrayList();
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
                                List<String> pass=callOutput.getValue();
                                user_password=pass.get(0);
                                user_email=pass.get(1);
//                                Toast.makeText(PasswordSettingScreen.this, "pass"+user_password, Toast.LENGTH_SHORT).show();
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


    private void forgotPasswordFunction() {

            //generate a 4 digit integer 1000 <10000
            int randomPIN = (int)(Math.random()*9000)+1000;

            //Store integer in a string
            Log.d("Random Mail",""+randomPIN);

//        // TODO Auto-generated method stub
//
//        new Thread(new Runnable() {
//
//            public void run() {
//
//                try {
//
//                    sendMailFunction();
//
//                } catch (Exception e) {
//
//                    Log.d("Mail", "error");
//
//
//                }
//
//            }
//
//        }).start();


        Log.d("Mail id :",user_email);
        if(user_email.equals(""))
        {
            Toast.makeText(PasswordSettingScreen.this, "No email id found", Toast.LENGTH_SHORT).show();
        }
        else {
            Log.d("Mail", "called asynctask");
            new PinMailSender().execute("passsword_setting_screen",user_email, ""+randomPIN);//To call AsyncTask
            updateTempPassword(randomPIN);
        }




    }

    private void updateTempPassword(int randomPIN) {

        Log.d("Mail", "called password update");

        String query="update tb_user set temp_password=? where user_id=?";
        ArrayList<String> value=new ArrayList();
        value.add(""+randomPIN);
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

                            Intent intent=new Intent(PasswordSettingScreen.this,ForgotPassword.class);
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
