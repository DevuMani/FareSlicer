package com.example.dream.fareslicer.AdapterClasses;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dream.fareslicer.BeanClasses.ContactData;
import com.example.dream.fareslicer.ContactDialogClass;
import com.example.dream.fareslicer.Interface.ContactSetter;
import com.example.dream.fareslicer.NewGroup;
import com.example.dream.fareslicer.R;
import com.example.dream.fareslicer.RetrofitClientAndInterface.RetrofitClient;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CallOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CallResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.QueryValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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

                selectCall(c_data.getName(),c_data.getNumber());

//                if(b==true)
//                {
//                    ContactSetter contactSetter=NewGroup.newGroup;
//                    contactSetter.setContact(c_data.getName(),c_data.getNumber());
//                    contactDialogClass.dismiss();
//                }
//                else
//                {
//                    showAlertDialog();
//                }



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

        private void showAlertDialog() {



            //init alert dialog
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setCancelable(false);
            builder.setTitle("Person not using this app");
            builder.setMessage("Do you want to invite this person?");
            builder.setCancelable(true);
            //set listeners for dialog buttons
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //finish the activity
                    String phoneNumber="9544645653";
                    String message="Hey user, install FareSlicer ";

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
                    intent.putExtra("sms_body", message);
                    mContext.startActivity(intent);
                }
            });

            //create the alert dialog and show it
            builder.create().show();
        }
        private void selectCall(final String name, final String number) {

            String query="select user_id from tb_user where user_phno=?";
            ArrayList<String> value=new ArrayList();
            value.add(number);

            QueryValue queryValue=new QueryValue();
            queryValue.setQuery(query);
            queryValue.setValue(value);



            final Call<CallResult> call= RetrofitClient. getInstance().getApi().select(queryValue);

            call.enqueue(new Callback<CallResult>() {
                @Override
                public void onResponse(Call<CallResult> call, Response<CallResult> response) {

                    if(response.code()==200) {

                        CallResult selectResult=response.body();

                        String success= "";
                        if (selectResult != null) {
                            success = selectResult.getStatus();

                            if (success.equalsIgnoreCase("true")) {

                                List<CallOutput> output = selectResult.getOutput();

                                CallOutput item = output.get(0);
                                List<String> list=item.getValue();
                                String id=list.get(0);

                                if(!id.equalsIgnoreCase(""))
                                {
                                    ContactSetter contactSetter=NewGroup.newGroup;
                                    contactSetter.setContact(id,name,number);
                                    contactDialogClass.dismiss();

                                }


                            } else {
                                Log.e("Selection", "Success is false");
                                showAlertDialog();
                            }
                        }
                        else
                        {
                            Log.e("Selection", "Success is null ");

                        }

                    }
                    else {
                        String s="";
                        try {
                            if (response.errorBody() != null) {
                                s=response.errorBody().string();
                            }
                            else
                            {
                                Log.e("Selection","Error body is null");
                            }
                        } catch (IOException e) {
                            Log.e("Insertion",e.getMessage());

                        }
                    }


                }

                @Override
                public void onFailure(Call<CallResult> call, Throwable t) {

                    Toast.makeText(mContext, "Selection Call failed", Toast.LENGTH_SHORT).show();
                    Log.e("Selction", t.getMessage());
                }

            });


        }

    }

}
