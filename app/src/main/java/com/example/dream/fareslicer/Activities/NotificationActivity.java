package com.example.dream.fareslicer.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dream.fareslicer.R;
import com.example.dream.fareslicer.RetrofitClientAndInterface.RetrofitClient;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.QueryValue;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotificationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    private static final String TAG = "NotificationActivity";

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    //Navigation Header
    TextView name,phone,email;
    ImageView image,edit;

    private RadioGroup radioNotificationGroup;
    private RadioButton radioEnable,radioDisable;

    Boolean noti_mobile=false;
    Boolean noti_email=false;
    Boolean noti_both=false;

    LinearLayout notification_mobile,notification_email,notification_all;
    FrameLayout frameLayout;
    String user_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        SharedPreferences sp=getSharedPreferences("User",MODE_PRIVATE);
        user_id=sp.getString("user_id","");

        initView();

        navDrawer();

        setHeader();

        notification_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialogFunction("mobile");
            }
        });
        notification_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialogFunction("email");
            }
        });
        notification_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialogFunction("both");
            }
        });
    }

    private void alertDialogFunction(final String type) {


        final Dialog iconDialog = new Dialog(NotificationActivity.this);
        iconDialog.setTitle("Mobile Notifications :");
        iconDialog.setContentView(R.layout.dialog_notification);
        iconDialog.setCanceledOnTouchOutside(false);
        radioNotificationGroup=iconDialog.findViewById(R.id.radioNotification);
        radioEnable=iconDialog.findViewById(R.id.radioEnable);
        radioDisable=iconDialog.findViewById(R.id.radioDisable);
        setRadioButtonCall(type,iconDialog);
        radioEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(type.equalsIgnoreCase("mobile"))
                {
//                    noti_mobile=true;
                    updateNotification(type, true,iconDialog);
                }
                else if (type.equalsIgnoreCase("email"))
                {
//                    noti_email=true;
                    updateNotification(type, true, iconDialog);
                }
                else if (type.equalsIgnoreCase("both"))
                {
//                    noti_both=true;
                    updateNotification(type, true, iconDialog);
                }

            }
        });
        radioDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(type.equalsIgnoreCase("mobile"))
                {
//                    noti_mobile=false;
                    updateNotification(type, false, iconDialog);
                }
                else if (type.equalsIgnoreCase("email"))
                {
//                    noti_email=false;
                    updateNotification(type,false, iconDialog);
                }
                else if (type.equalsIgnoreCase("both"))
                {
//                    noti_both=false;
                    updateNotification(type,false, iconDialog);
                }

            }
        });

    }

    private void setRadioButtonCall(final String type, final Dialog iconDialog) {

        String query="";
        ArrayList<String> value=null;

        if(type.equalsIgnoreCase("mobile"))
        {
            query="select notification_mobile from tb_notification where user_id=? ";
            value=new ArrayList();
            value.add(user_id);

        }
        else if (type.equalsIgnoreCase("email"))
        {
            query="select notification_email from tb_notification where user_id=?";
            value=new ArrayList();
            value.add(user_id);
        }
        else if (type.equalsIgnoreCase("both"))
        {
            query="select notification_both from tb_notification where user_id=?";
            value=new ArrayList();
            value.add(user_id);
        }


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
                                String set=value.get(0);
                                if(set.equalsIgnoreCase("true"))
                                {

                                    radioEnable.setChecked(true);
//                                    if(type.equalsIgnoreCase("mobile"))
//                                    {
//                                        radioEnable.setChecked(true);
//
//                                    }
//                                    else if (type.equalsIgnoreCase("email"))
//                                    {
//                                        radioEnable.setChecked(true);
//                                    }
//                                    else if (type.equalsIgnoreCase("both"))
//                                    {
//                                        radioEnable.setChecked(true);
//                                    }




                                }
                                else
                                {
                                    radioDisable.setChecked(true);
                                }


                            }
                            iconDialog.show();

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


    private void initView() {

        notification_mobile=findViewById(R.id.notification_mobile);
        notification_email=findViewById(R.id.notification_email);
        notification_all=findViewById(R.id.notification_all);

        frameLayout=findViewById(R.id.noti_frameLayout);

    }


    private void setHeader() {

        SharedPreferences sp=getSharedPreferences("User",MODE_PRIVATE);
        String header_name=sp.getString("user_name","");
        String header_phone=sp.getString("user_phno","");
        String header_email=sp.getString("user_email","");
        String fileName=sp.getString("user_photo","");

        name.setText(header_name);
        phone.setText(header_phone);
        email.setText(header_email);


        String path= Environment.getExternalStorageDirectory() + File.separator + "FareSlicer"+ File.separator +fileName;

        if(fileName.equals("")) {

            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            image.setBackgroundColor(color);
//                imageview.setBackgroundColor(0xff00ff00);
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(path);

//            Toast.makeText(this, ""+bitmap, Toast.LENGTH_SHORT).show();
            image.setImageBitmap(bitmap);
        }


    }

    private void navDrawer() {

        toolbar = findViewById(R.id.notification_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.notification_drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.notification_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header=navigationView.getHeaderView(0);

        name=header.findViewById(R.id.nav_name);
        phone=header.findViewById(R.id.nav_phone);
        email=header.findViewById(R.id.nav_email);
        image=header.findViewById(R.id.nav_image);
        edit=header.findViewById(R.id.nav_edit);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(NotificationActivity.this,ProfilePage.class);
                startActivity(intent);
            }
        });

        setHeader();

    }
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {

            finish();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();

        switch(id){
            case R.id.menu_home:
                Toast.makeText(this, "Clicked home", Toast.LENGTH_SHORT).show();

                Intent intent=new Intent(NotificationActivity.this,Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                break;

            case R.id.menu_notification:
//                Toast.makeText(this, "Clicked Notification", Toast.LENGTH_SHORT).show();

                Intent notification_intent=new Intent(NotificationActivity.this,NotificationActivity.class);
                notification_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(notification_intent);

                break;
            case R.id.menu_passcode:
//                Toast.makeText(this, "Clicked Passcode", Toast.LENGTH_SHORT).show();
                SharedPreferences sp=getSharedPreferences("User",Context.MODE_PRIVATE);
                Boolean pass=sp.getBoolean("Password",false);
                if(pass==true)
                {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(false);
                    builder.setTitle("Change Password");
                    builder.setMessage("Do you want to change your password?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Intent intent=new Intent(NotificationActivity.this,PasswordSettingScreen.class);
                            intent.putExtra("from","nav_change");
                            startActivity(intent);

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(PasswordSettingScreen.this, "Don't Change password", Toast.LENGTH_SHORT).show();

                            dialogInterface.dismiss();

                        }
                    });

                    builder.create().show();



                }
                else
                {
                    Intent pass_intent=new Intent(NotificationActivity.this,PasswordSettingScreen.class);
                    pass_intent.putExtra("from","nav_set");
                    startActivity(pass_intent);
                }

                break;
            case R.id.menu_clear_passcode:
                AlertDialog.Builder builder = new AlertDialog.Builder(NotificationActivity.this);
                builder.setCancelable(false);
                builder.setTitle("Clear Password");
                builder.setMessage("Do you want to clear your password?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent=new Intent(NotificationActivity.this,PasswordSettingScreen.class);
                        intent.putExtra("from","clear");
                        startActivity(intent);
//                        Toast.makeText(Home.this, "Clear password", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(PasswordSettingScreen.this, "Don't clear password", Toast.LENGTH_SHORT).show();

                        dialogInterface.dismiss();

                    }
                });

                builder.create().show();
                break;
            case R.id.menu_rate:
                Toast.makeText(NotificationActivity.this, "Clicked Rate us", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_contact:
//                Toast.makeText(NotificationActivity.this, "Clicked Contact us", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setType("message/rfc822");
                i.setData(Uri.parse("mailto:"));
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"devumani10@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "FeedBack to: IdeenKreise Tech");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(NotificationActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.menu_log_out:
                Toast.makeText(this, "Clicked Log out", Toast.LENGTH_SHORT).show();
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private void updateNotification(String type, Boolean noti_value, final Dialog iconDialog) {

        String query="";
        ArrayList<String> value=null;

        if(type.equalsIgnoreCase("mobile"))
        {
            query="update tb_notification set notification_mobile=? where user_id=?";
            value=new ArrayList();
            value.add(""+noti_value);
            value.add(user_id);

        }
        else if (type.equalsIgnoreCase("email"))
        {
            query="update tb_notification set notification_email=? where user_id=?";
            value=new ArrayList();
            value.add(""+noti_value);
            value.add(user_id);
        }
        else if (type.equalsIgnoreCase("both"))
        {
            query="update tb_notification set notification_both=? where user_id=?";
            value=new ArrayList();
            value.add(""+noti_value);
            value.add(user_id);
        }

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

                            Snackbar.make(frameLayout,"Saved",Snackbar.LENGTH_LONG).show();

                            iconDialog.dismiss();
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
}
