package com.example.dream.fareslicer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dream.fareslicer.Interface.ContactSetter;
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

    String selected_name="";
    String selected_number="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        initView();

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

    private void initView() {

        currency_chooser=findViewById(R.id.profile_currency_select);
        currency=findViewById(R.id.profile_currency);

        add_members=findViewById(R.id.layout_add_members);
        member_recyclerView=findViewById(R.id.group_recycler_view);

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
    public void setContact(String name, String number) {

        selected_name=name;
        selected_number=number;


        setMemberDataList();
    }

    private void setMemberDataList() {



    }
}
