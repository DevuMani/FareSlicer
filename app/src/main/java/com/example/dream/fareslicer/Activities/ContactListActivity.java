package com.example.dream.fareslicer.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import android.widget.Toast;

import com.example.dream.fareslicer.AdapterClasses.ContactListAdapter;
import com.example.dream.fareslicer.AdapterClasses.ContactListPageAdapter;
import com.example.dream.fareslicer.BeanClasses.ContactData;
import com.example.dream.fareslicer.Interface.ContactSetter;
import com.example.dream.fareslicer.R;
import com.example.dream.fareslicer.SupportClasses.DBFunction;

import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity implements ContactSetter,SearchView.OnQueryTextListener {

    private static final int REQUEST_CODE = 0;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    ContactListPageAdapter adapter;
    String pageType="";
    Toolbar toolbar;

    ArrayList<ContactData> contact_list=new ArrayList();
    ContactData contactData;
    private LinearLayoutManager linearLayoutManager;

    private String selected_user_id="";
    private String selected_name="";
    private String selected_number="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        initView();


        pageType=getIntent().getStringExtra("pageType");


        setRecyclerViewData();
    }

    @Override
    public void onBackPressed() {

        Intent intent=new Intent();
        setResult(REQUEST_CODE,intent);
        finish();

    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();

        return false;
    }

    private void initView() {

        toolbar = findViewById(R.id.contact_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Choose a member");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        recyclerView=findViewById(R.id.contact_recyclerView);
        progressBar=findViewById(R.id.contact_progress);
        progressBar.setVisibility(View.VISIBLE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu,menu);

        MenuItem menuItem=menu.findItem(R.id.action_search);
        SearchView searchView= (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    private void setRecyclerViewData() {

        DBFunction dbFunction=new DBFunction(ContactListActivity.this);

        loadContacts1();//remove contact

//        contact_list=dbFunction.loadContactsToDisplay();
        linearLayoutManager=new LinearLayoutManager(ContactListActivity.this);
        adapter = new ContactListPageAdapter(ContactListActivity.this, contact_list,ContactListActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);

    }

    //remove contact
    private void loadContacts() {

        contact_list= new ArrayList<>();

//        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
//        String orderBy = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
//        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        ContentResolver contentResolver= getContentResolver();
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
//                    Cursor cursor1 = contentResolver.query(uri, projection,
//                            null, null, orderBy);

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
    }

    private void loadContacts1() {

        contact_list= new ArrayList<>();

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};

        String orderBy = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME +" ASC";
        String filter = ""+ ContactsContract.Contacts.HAS_PHONE_NUMBER + " > 0 and " + ContactsContract.CommonDataKinds.Phone.TYPE +"=" + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        ContentResolver contentResolver= getContentResolver();
        Cursor cursor=contentResolver.query(uri,projection,filter,null,orderBy);

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                contactData = new ContactData();

                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                contactData.setName(name);
                contactData.setNumber(phoneNumber);
                contact_list.add(contactData);

//                if (contact_list.isEmpty())
//                {
//                    contact_list.add(contactData);
//                }
//                else {
//
//                    for (ContactData contact : contact_list) //to avoid data duplication
//                    {
//                        // Now you have the product. Just get the Id
//                        if (!contact.getNumber().equals(phoneNumber)) {
//                            contact_list.add(contactData);
//                        }
//                    }
//                }

            }

        }
        cursor.close();
    }

    @Override
    public void setContact(String id, String name, String number) {

        selected_user_id=id;
        selected_name=name;
        selected_number=number;

//        onBackPressed();
//        Toast.makeText(ContactListActivity.this, "Name"+selected_name, Toast.LENGTH_SHORT).show();
        Log.d("Contact set data",name+" "+number);
        Intent intent=new Intent();
        intent.putExtra("id",selected_user_id);
        intent.putExtra("name",selected_name);
        intent.putExtra("number",selected_number);
        setResult(REQUEST_CODE,intent);
        finish();

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {

        String userInput=s.toLowerCase();
        ArrayList<ContactData> new_contact_list=new ArrayList();

        for (ContactData data:contact_list)
        {
            if(data.getName().toLowerCase().contains(userInput))
            {
                new_contact_list.add(data);
            }

        }
        adapter.updateList(new_contact_list);
        return true;
    }
}
