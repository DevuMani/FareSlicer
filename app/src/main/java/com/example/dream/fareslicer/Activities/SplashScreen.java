package com.example.dream.fareslicer.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dream.fareslicer.MyContactService;
import com.example.dream.fareslicer.R;
import com.example.dream.fareslicer.SupportClasses.ConnectionDetector;
import com.example.dream.fareslicer.SupportClasses.PermissionSetter;

import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG = "Splash Screen";
    ConnectionDetector cd;
    ArrayList<String> permissions=new ArrayList<>();

    String userID="";
    String user_phone="";
    Boolean pass=false;

    ProgressBar progressBar;

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;


    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

//        contactInsert();

        cd = new ConnectionDetector(this);

        if (!cd.isConnectingToInternet()) {
            Toast.makeText(this, "Connect to internet", Toast.LENGTH_SHORT).show();
            showAlertDialog();
        } else {


            progressBar = findViewById(R.id.progressBarHorizontal);

            setProgressValue(0);
            /* New Handler to start the Menu-Activity
             * and close this Splash-Screen after some seconds.*/
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    /* Create an Intent that will start the Menu-Activity. */
                    SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
                    userID = sp.getString("user_id", "");
                    user_phone = sp.getString("user_phno", "");
                    pass = sp.getBoolean("Password", false);
                    if (!userID.equals("")) {

                        if (pass == true) {
                            Intent mainIntent = new Intent(SplashScreen.this, PasswordSettingScreen.class);
                            mainIntent.putExtra("from", "splash");
                            SplashScreen.this.startActivity(mainIntent);
                            SplashScreen.this.finish();
                        } else {
                            Intent mainIntent = new Intent(SplashScreen.this, Home.class);
                            SplashScreen.this.startActivity(mainIntent);
                            SplashScreen.this.finish();
                        }
                    } else {
                        Intent mainIntent = new Intent(SplashScreen.this, RegistrationActivity.class);
                        SplashScreen.this.startActivity(mainIntent);
                        SplashScreen.this.finish();
//                    progressBar.setVisibility(View.GONE);

                    }
                }
            }, SPLASH_DISPLAY_LENGTH);

        }
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
                Intent intent=new Intent(SplashScreen.this,SplashScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        //create the alert dialog and show it
        builder.create().show();
    }

    private void setProgressValue(final int progress) {

        progressBar.setProgress(progress);
        //thread is used to give delay and set value accordingly
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(200);
                } catch(InterruptedException e){
                    e.printStackTrace();
                }
                setProgressValue(progress + 10);
            }
        });
        thread.start();
    }


    private void contactInsert() {

        //thread is used to give delay and set value accordingly
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                //Called to insert contacts from phone to app database
                Intent intent = new Intent(SplashScreen.this, MyContactService.class);
                startService(intent);


            }
        });
        thread.start();


    }

}
