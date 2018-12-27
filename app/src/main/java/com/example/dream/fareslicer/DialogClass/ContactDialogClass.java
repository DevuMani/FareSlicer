package com.example.dream.fareslicer.DialogClass;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dream.fareslicer.Activities.EditGroup;
import com.example.dream.fareslicer.Activities.NewGroup;
import com.example.dream.fareslicer.AdapterClasses.ContactListAdapter;
import com.example.dream.fareslicer.BeanClasses.ContactData;
import com.example.dream.fareslicer.R;

import java.util.ArrayList;
import java.util.regex.Pattern;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ContactDialogClass extends Dialog {

    ProgressBar progressBar;
    RecyclerView recyclerView;
    ContactListAdapter adapter;
    String pageType=""; // to know from where this call is coming edit or new group

    Context context;

    ArrayList<ContactData> contact_list=new ArrayList();
    ContactData contactData;
    private LinearLayoutManager linearLayoutManager;

    public ContactDialogClass(Context context, String pageType) {
        super(context);

        this.context=context;
        this.pageType=pageType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_contact_list);
//        Toast.makeText(context, "Loding....", Toast.LENGTH_SHORT).show();

        initView();
        setRecyclerViewData();


    }
        /**
     * dialog close listener
     * @param dialogClass
     */
    public void setMyDialogListener(ContactDialogClass dialogClass)
    {
        dialogClass.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                NewGroup.member_progress_bar.setVisibility(View.GONE);
                Toast.makeText(context, "Dissmissed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {

        dismiss();
    }

    private void initView() {

        recyclerView=findViewById(R.id.contact_recyclerView);
        progressBar=findViewById(R.id.contact_progress);
        progressBar.setVisibility(View.VISIBLE);

    }

    private void setRecyclerViewData() {

        contact_list= new ArrayList<>();
        loadContacts();
        linearLayoutManager=new LinearLayoutManager(context);
        adapter = new ContactListAdapter(context, contact_list,this,pageType);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);

    }

    private void loadContacts() {

        contact_list= new ArrayList<>();

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
    }


}
