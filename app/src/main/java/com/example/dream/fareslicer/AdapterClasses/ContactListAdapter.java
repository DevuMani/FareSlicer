package com.example.dream.fareslicer.AdapterClasses;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dream.fareslicer.ContactData;
import com.example.dream.fareslicer.ContactDialogClass;
import com.example.dream.fareslicer.Interface.ContactSetter;
import com.example.dream.fareslicer.NewGroup;
import com.example.dream.fareslicer.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by DevuMani on 10/5/2018.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<ContactData> contactList;
    ContactDialogClass contactDialogClass;

    public ContactListAdapter(Context context, ArrayList<ContactData> contact_list, ContactDialogClass contactDialogClass) {
        mContext=context;
        contactList=contact_list;
        this.contactDialogClass=contactDialogClass;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.contact_list_cust_row, parent,false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        ContactData c_data= contactList.get(position);

        holder.name.setText(c_data.getName());
        holder.phone.setText(c_data.getNumber());

    }

    @Override
    public int getItemCount() {
//        Toast.makeText(mContext, "Size"+iconList.size(), Toast.LENGTH_SHORT).show();
        return contactList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView name,phone;
        public LinearLayout layout;


        public MyViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.contact_name);
            phone = itemView.findViewById(R.id.contact_number);

            layout=itemView.findViewById(R.id.contact_layout);

            layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if(view == layout)
            {
                ContactData c_data= contactList.get(getAdapterPosition());
                Toast.makeText(mContext, "Name : "+c_data.getName(), Toast.LENGTH_SHORT).show();
                ContactSetter contactSetter=NewGroup.newGroup;
                contactSetter.setContact(c_data.getName(),c_data.getNumber());
                contactDialogClass.dismiss();

//                IncomeExpenseActivity.iconsymbol= String.valueOf(c_data.getCategory_name());
//
//                if(getAdapterPosition()== getItemCount()-1) {
//
//                    Toast.makeText(mContext, "Last", Toast.LENGTH_SHORT).show();
//                    Intent intent=new Intent(mContext,AddCategory.class);
//                    intent.putExtra("type",type);
//                    mContext.startActivity(intent);
//                }
                //                Toast.makeText(mContext, "Dataa:::"+String.valueOf(iconList.get(getItemCount())), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
