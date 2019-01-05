package com.example.dream.fareslicer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.dream.fareslicer.SupportClasses.DBFunction;

public class MyContactService extends Service {

    DBFunction dbFunction=new DBFunction(MyContactService.this);

    public MyContactService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {

        super.onCreate();
        Boolean b = dbFunction.loadContactsFromPhone();
        if (b==true)
        {
            stopSelf();
        }
        else
        {
            Intent intent=new Intent(this,MyContactService.class);
            startService(intent);
        }
    }
}
