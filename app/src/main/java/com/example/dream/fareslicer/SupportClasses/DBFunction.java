package com.example.dream.fareslicer.SupportClasses;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.dream.fareslicer.BeanClasses.ContactData;

import java.util.ArrayList;

public class DBFunction {


    private static final String TAG = "DBFunction";

    Context context;
    DBHelper dbHelper;

    public DBFunction(Context context) {

        this.context = context;
        dbHelper = new DBHelper(context);
    }

    public Boolean loadContactsFromPhone() {

        ContactData contactData;

        ArrayList<ContactData> contact_list= new ArrayList<>();

        ContentResolver contentResolver= context.getContentResolver();
        Cursor cursor=contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");


        if (cursor != null && cursor.getCount() > 0) {


            while (cursor.moveToNext()) {

                contactData = new ContactData();

                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0) {
                    Cursor cursor1 = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                    while (cursor1.moveToNext()) {
                        String phoneNumber = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                        contactData.setName(name);
                        contactData.setNumber(phoneNumber);
                        contact_list.add(contactData);
                    }

                    cursor1.close();
                }

            }
        }
        cursor.close();

        Boolean b=contactInsert(contact_list);

        return b;
    }

    private boolean contactInsert(ArrayList<ContactData> contact_list) {

        dbHelper.openConnection();
//
        for (int i = 0; i < contact_list.size(); i++) {

            ContactData contactData=contact_list.get(i);

            String number=contactData.getNumber();
            String formattedNumber=number.replaceAll("\\s","");

            Boolean isContact=check(formattedNumber);

            if(isContact==false) {

                String ins = "insert into tb_contacts(contact_name,contact_phno) values('" + contactData.getName().replaceAll("'","") + "','" + formattedNumber + "')";
                Boolean b = dbHelper.insertData(ins);
                Log.d("Contact Insert",contactData.getName()+" "+b);

            }
            else
            {
                Log.d("Contact Insert",contactData.getName()+" skipped");
            }

        }

        dbHelper.closeConnection();

        return true;
    }

    private void contactInsertIndividual(String name, String phone) {
        dbHelper.openConnection();
//
        String ins = "insert into tb_contacts(contact_name,contact_phno) values('" + name + "','" + phone + "')";
        Boolean b = dbHelper.insertData(ins);


        dbHelper.closeConnection();


    }

    private Boolean userUpdation(String old_name, String new_name, String phone, String currency) {
        String tb_name = "";
        String tb_email = "";
        String tb_password = "";

        dbHelper.openConnection();

        String sel = "";
        if (old_name.equals(new_name)) {
            sel = "select user_email,user_password from tb_user where user_name='" + old_name + "'";
            tb_name = old_name;
        } else {
            sel = "select * from tb_user where user_name='" + old_name + "'";
            tb_name = new_name;
        }

        Cursor user_sel = dbHelper.selectData(sel);

        while (user_sel.moveToNext()) {

            tb_email = user_sel.getString(0);
            tb_password = user_sel.getString(1);
        }


        String del = "DELETE from tb_user where user_name='" + old_name + "'";
        Boolean user_del = dbHelper.insertData(del);
        Boolean b = false;
        if (user_del == true) {
            String ins = "insert into tb_user(user_name,user_phno,user_email,user_currency,user_password,user_status) values('" + tb_name + "','" + phone + "','" + tb_email + "','" + currency + "','" + tb_password + "'," + 1 + ")";
            b = dbHelper.insertData(ins);

//            Log.d("Insert",ins);
            if (b == true) {

                SharedPreferences sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Name", tb_name);
                editor.putString("Email", tb_email);
                editor.putString("Phone", phone);
                editor.putString("Currency", currency);
                editor.apply();

            } else {


            }

        } else {
            Log.d("Deletion unsuccessful ", del);
        }

        dbHelper.closeConnection();

        return b;

    }

    public ArrayList<ContactData> loadContactsToDisplay() {
        dbHelper.openConnection();

        ContactData contactData;

        ArrayList<ContactData> contact_list= new ArrayList<>();

        String sel_contacts = "SELECT contact_name,contact_phno FROM tb_contacts";
        Cursor contact_cursor = dbHelper.selectData(sel_contacts);

        Log.d("Contact Table", "Type_id   Type_name");
        while (contact_cursor.moveToNext()) {

            contactData=new ContactData();
            Log.d("Contact Table", contact_cursor.getString(0) + " " + contact_cursor.getString(1));
            contactData.setName(contact_cursor.getString(0));
            contactData.setNumber(contact_cursor.getString(1));
            contact_list.add(contactData);
        }

        dbHelper.closeConnection();

        return contact_list;
    }


    private Boolean check(String number) {

        DBHelper dbHelper=new DBHelper(context);
        dbHelper.openConnection();

        String sel_contacts = "SELECT * FROM tb_contacts WHERE contact_phno='" + number + "'";
        Cursor currency_cursor = dbHelper.selectData(sel_contacts);

        if (currency_cursor.moveToNext()) {

            dbHelper.closeConnection();
            return true;
        } else {

            dbHelper.closeConnection();
            return false;
        }

    }




    public void delete() {

        dbHelper.openConnection();
        String del = "delete from tb_currency";
        if (dbHelper.insertData(del)) {
        } else {
        }


    }


}