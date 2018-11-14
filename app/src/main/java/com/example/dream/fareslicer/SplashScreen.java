package com.example.dream.fareslicer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

public class SplashScreen extends AppCompatActivity {

    String userID="";
    String user_phone="";

    ProgressBar progressBar;

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        progressBar=findViewById(R.id.progressBarHorizontal);

        setProgressValue(0);
        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){

            @Override
            public void run() {

                /* Create an Intent that will start the Menu-Activity. */
                SharedPreferences sp=getSharedPreferences("User",MODE_PRIVATE);
                userID=sp.getString("user_id","");
                user_phone=sp.getString("user_phno","");
                if(!user_phone.equals("")) {
                    Intent mainIntent = new Intent(SplashScreen.this, Home.class);
                    SplashScreen.this.startActivity(mainIntent);
                    SplashScreen.this.finish();

                }
                else
                {
                    Intent mainIntent = new Intent(SplashScreen.this, RegistrationActivity.class);
                    SplashScreen.this.startActivity(mainIntent);
                    SplashScreen.this.finish();
//                    progressBar.setVisibility(View.GONE);

                }
            }
        }, SPLASH_DISPLAY_LENGTH);
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
}
