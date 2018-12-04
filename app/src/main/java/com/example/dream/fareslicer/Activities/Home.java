package com.example.dream.fareslicer.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.dream.fareslicer.AdapterClasses.HomePagerAdapter;
import com.example.dream.fareslicer.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Home";
    ArrayList<String> permissions=new ArrayList<>();

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    //Navigation Header
    TextView name,phone,email;
    ImageView image,edit;

    int tab_pos=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        tab_pos=getIntent().getIntExtra("tab_position",-1);

        navDrawer();

        setHeader();

        tabLayout();

        setPermission();
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

    //  Tab layout
    private void tabLayout() {

//        Toolbar toolbar = (Toolbar) findViewById(R.id.leave_head_toolbar);
//        setSupportActionBar(toolbar);

        // Create an instance of the tab layout from the view.
        TabLayout tabLayout = (TabLayout) findViewById(R.id.home_tab_layout);
        // Set the text for each tab.
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_transaction)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_groups)));
//        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_groups ));
        tabLayout.setTabTextColors(ColorStateList.valueOf(Color.WHITE));
        // Set the tabs to fill the entire layout.
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Using PagerAdapter to manage page views in fragments.
        // Each page is represented by its own fragment.
        // This is another example of the adapter pattern.
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final HomePagerAdapter adapter = new HomePagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),getApplicationContext());
        viewPager.setAdapter(adapter);
        if(tab_pos!=-1)
        {
            viewPager.setCurrentItem(tab_pos);
        }

        // Setting a listener for clicks.
        viewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void navDrawer() {

        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);


        drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.navigation_view);
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

                Intent intent=new Intent(Home.this,ProfilePage.class);
                startActivity(intent);
            }
        });
        setHeader();

    }

    public void setPermission()
    {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
            if (info.requestedPermissions != null) {
                for (String p : info.requestedPermissions) {
                    Log.d(TAG, "Permission : " + p);
                    permissions.add(p);
                }

                checkWriteExternalPermission(permissions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkWriteExternalPermission(ArrayList<String> permission)
    {
        ArrayList<String> notgranted =new ArrayList<>();

        for(int i=0;i<permission.size();i++) {

            String per_name=permission.get(i);
            int res = checkCallingOrSelfPermission(permission.get(i));

            if (res == PackageManager.PERMISSION_GRANTED) {

//                Toast.makeText(getApplicationContext(), permission + "permission granted", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Permission granted : " + permission.get(i));

            }
            else {
//                Toast.makeText(getApplicationContext(), permission + "permission not granted", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Permission not granted : " + permission.get(i));

                notgranted.add(permission.get(i));


            }
        }

        String[] p=new String[notgranted.size()];

        for (int i=0;i<notgranted.size();i++) {
            p = notgranted.toArray(new String[i]);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //
//                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                        //                            takePictureButton.setEnabled(false);
            requestPermissions(p, 0);

//                    } else {
//                        Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
//                    }

        } else {
            Toast.makeText(this, "You have to give permission externally", Toast.LENGTH_SHORT).show();
        }

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

                Intent intent=new Intent(Home.this,Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                break;

            case R.id.menu_notification:
//                Toast.makeText(this, "Clicked Notification", Toast.LENGTH_SHORT).show();
                Intent notification_intent=new Intent(Home.this,NotificationActivity.class);
                notification_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(notification_intent);
                break;
            case R.id.menu_passcode:
                Toast.makeText(this, "Clicked Passcode", Toast.LENGTH_SHORT).show();
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

                            Intent intent=new Intent(Home.this,PasswordSettingScreen.class);
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
                    Intent pass_intent=new Intent(Home.this,PasswordSettingScreen.class);
                    pass_intent.putExtra("from","nav_set");
                    startActivity(pass_intent);
                }

                break;
            case R.id.menu_clear_passcode:
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                builder.setCancelable(false);
                builder.setTitle("Clear Password");
                builder.setMessage("Do you want to clear your password?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent=new Intent(Home.this,PasswordSettingScreen.class);
                        intent.putExtra("from","clear");
                        startActivity(intent);
                        Toast.makeText(Home.this, "Clear password", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Clicked Rate us", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_contact:
                Toast.makeText(this, "Clicked Contact us", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setType("message/rfc822");
                i.setData(Uri.parse("mailto:"));
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"devumani10@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "FeedBack to: IdeenKreise Tech");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(Home.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.menu_log_out:
                Toast.makeText(this, "Clicked Log out", Toast.LENGTH_SHORT).show();
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
