package com.example.dream.fareslicer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dream.fareslicer.AdapterClasses.ContactListAdapter;
import com.example.dream.fareslicer.AdapterClasses.MemberListAdapter;
import com.example.dream.fareslicer.BeanClasses.MemberData;
import com.example.dream.fareslicer.Interface.ContactSetter;
import com.google.android.material.snackbar.Snackbar;
import com.mynameismidori.currencypicker.CurrencyPicker;
import com.mynameismidori.currencypicker.CurrencyPickerListener;

import java.util.ArrayList;
import java.util.Objects;

public class NewGroup extends AppCompatActivity implements ContactSetter {


    ImageView currency_chooser;
    TextView currency;
    LinearLayout add_members;
    RecyclerView member_recyclerView;


    public static NewGroup newGroup =new NewGroup();

    String selected_user_id="";
    String selected_name="";
    String selected_number="";
    static ArrayList<MemberData> member_list=new ArrayList<>();
    MemberData memberData;
    private static RecyclerView.LayoutManager linearLayoutManager;
    private static MemberListAdapter adapter;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);


        initView();
        initRecyclerView();

        Objects.requireNonNull(getSupportActionBar()).setTitle("Add Group");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        currency_chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(NewGroup.this, "Currency list coming soon.....", Toast.LENGTH_SHORT).show();

                final CurrencyPicker picker = CurrencyPicker.newInstance("Select Currency");  // dialog title

                picker.show(getSupportFragmentManager(), "CURRENCY_PICKER");

                picker.setListener(new CurrencyPickerListener() {
                    @Override
                    public void onSelectCurrency(String name, String code, String symbol, int flagDrawableResID) {
                        // Implement your code here

                        Toast.makeText(NewGroup.this, "Currency "+name, Toast.LENGTH_SHORT).show();
//                        im_currency.setImageDrawable();

                        if(code.equalsIgnoreCase("INR"))
                        {
                            currency.setText(R.string.Rs);
                        }
                        else {
                            currency.setText(symbol);
                        }
                        picker.dismiss();
                    }

                });

//                startActivity(new Intent(NewGroup.this,ChooseCurrency.class));


            }
        });

        add_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Dialog for contact list
                ContactDialogClass contactDialogClass=new ContactDialogClass(NewGroup.this);
                contactDialogClass.setCancelable(true);
                contactDialogClass.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                contactDialogClass.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


        initView();
        initRecyclerView();

    }

    private void initView() {

        linearLayout=findViewById(R.id.new_group_layout);
        currency_chooser=findViewById(R.id.profile_currency_select);
        currency=findViewById(R.id.profile_currency);

        add_members=findViewById(R.id.layout_add_members);
        member_recyclerView=findViewById(R.id.group_recycler_view);


    }

    private void initRecyclerView() {
        linearLayoutManager=new LinearLayoutManager(NewGroup.this);
        adapter = new MemberListAdapter(NewGroup.this, member_list);
        member_recyclerView.setLayoutManager(linearLayoutManager);
        member_recyclerView.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void setContact(String id, String name, String number) {

        selected_user_id=id;
        selected_name=name;
        selected_number=number;


        setMemberDataList(selected_user_id,selected_name,selected_number);

    }

    private void setMemberDataList(String selected_user_id, String selected_name, String selected_number) {

        memberData=new MemberData();
        memberData.setSelected_user_id(selected_user_id);
        memberData.setSelected_name(selected_name);
        memberData.setSelected_number(selected_number);

//        recreate();
//        initView();
//        initRecyclerView();
//        if(member_list.contains(memberData))
//        {
//            Snackbar.make(linearLayout,"Already added this member",Snackbar.LENGTH_SHORT).show();
//        }
//        else
//        {
            member_list.add(memberData);

//        }
        adapter.notifyDataSetChanged();
    }


    
}
