package com.example.dream.fareslicer.SupportClasses;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.core.app.ActivityCompat;

public class PermissionSetter extends ActivityCompat {

    private static final String TAG = "PermissionSetter" ;
    ArrayList<String> permissions=new ArrayList<>();
    Context context;
    Activity activity;

    public PermissionSetter(Context context) {
        this.context = context;

        setPermission();
    }

    private void setPermission()
    {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
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
            int res = context.checkCallingOrSelfPermission(permission.get(i));

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

            ActivityCompat.requestPermissions((Activity) context.getApplicationContext(),p, 0);

        } else {
            Toast.makeText(context, "You have to give "+p.length+" permission externally", Toast.LENGTH_SHORT).show();
        }

    }

}
