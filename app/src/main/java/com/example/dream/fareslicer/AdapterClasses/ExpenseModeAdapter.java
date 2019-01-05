package com.example.dream.fareslicer.AdapterClasses;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dream.fareslicer.Activities.AddExpense;
import com.example.dream.fareslicer.BeanClasses.ExpenseModeData;
import com.example.dream.fareslicer.BeanClasses.MemberData;
import com.example.dream.fareslicer.BeanClasses.ShareDataModel;
import com.example.dream.fareslicer.DialogClass.ContactDialogClass;
import com.example.dream.fareslicer.DialogClass.ShareAmongDialogClass;
import com.example.dream.fareslicer.Interface.MyMemberListSetting;
import com.example.dream.fareslicer.Interface.PaymentModeSetter;
import com.example.dream.fareslicer.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by DevuMani on 10/5/2018.
 */

public class ExpenseModeAdapter extends RecyclerView.Adapter<ExpenseModeAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<ExpenseModeData> modeList;
    private String g_id="";
    private String g_currency="";
    private Double tot_amount=0.0;
    private MyMemberListSetting listSettingListener;
    int selectedPosition=-1;

    public ExpenseModeAdapter(Context context, ArrayList<ExpenseModeData> mode_list, String g_id, String g_currency, MyMemberListSetting listSettingListener) {
        mContext=context;
        modeList=mode_list;
        this.g_id=g_id;
        this.g_currency=g_currency;
        this.listSettingListener = listSettingListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.cust_row_expense_mode_list, parent,false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        ExpenseModeData e_data= modeList.get(position);
        holder.name.setText(e_data.getPayment_mode());

        if(selectedPosition==position) {
            holder.name.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryLight));
        }
        else
            holder.name.setTextColor(mContext.getResources().getColor(R.color.Black));


    }

    @Override
    public int getItemCount() {
        return modeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
        private LinearLayout share_mode_name;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.mode_name);
            share_mode_name=itemView.findViewById(R.id.share_mode_layout);


            share_mode_name.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {

            selectedPosition=getAdapterPosition();

            if (!AddExpense.tot_amount.getText().toString().equalsIgnoreCase("")) {

                tot_amount=Double.parseDouble(AddExpense.tot_amount.getText().toString());


                    ExpenseModeData e_data = modeList.get(getAdapterPosition());

//                    Toast.makeText(mContext, "Id : " + e_data.getPayment_id(), Toast.LENGTH_SHORT).show();

                    PaymentModeSetter paymentModeSetter = AddExpense.addExpense;
                    paymentModeSetter.setPaymentId(e_data.getPayment_id(),e_data.getPayment_mode());

                    ShareAmongDialogClass shareAmongDialogClass=new ShareAmongDialogClass(mContext,g_id,g_currency,e_data.getPayment_id(),e_data.getPayment_mode(),tot_amount,listSettingListener);
                    shareAmongDialogClass.show();


            }
            else
            {
                name.setTextColor(mContext.getResources().getColor(R.color.Black));
                Toast.makeText(mContext, "Enter an Amount", Toast.LENGTH_SHORT).show();
            }
            notifyDataSetChanged();

        }
    }
}
