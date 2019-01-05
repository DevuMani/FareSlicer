package com.example.dream.fareslicer.SupportClasses;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dreams on 8/2/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    Context c;
    SQLiteDatabase sqLiteDb;

    public DBHelper(Context context) {
        super(context, "db_m_tracker", null, 1);

    }

    public void openConnection() {
        sqLiteDb = getWritableDatabase();
    }

    public void closeConnection() {
        sqLiteDb.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String contact_table = "create table tb_contacts(contact_id INTEGER PRIMARY KEY AUTOINCREMENT,contact_name VARCHAR(30),contact_phno VARCHAR(15))";

        db.execSQL(contact_table);
        Log.i("user_table creation", "Successful " + contact_table);

    }


    public boolean insertData(String query) {

        Log.d("Insert/Delete query", query);
        try {
            sqLiteDb.execSQL(query);
            return true;
        } catch (Exception e) {
            Log.e("Insert/Delete row", e.toString());
            return false;
        }
    }

    public Cursor selectData(String query) {
        Log.d("Select Query", query);
        Cursor c = sqLiteDb.rawQuery(query, null);
        return c;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }
}
