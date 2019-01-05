package com.example.dream.fareslicer.AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dream.fareslicer.BeanClasses.ExpenseMemberData;
import com.example.dream.fareslicer.Interface.MyMemberListSetting;
import com.example.dream.fareslicer.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by DevuMani on 10/5/2018.
 * To show details in AddExpense Activity
 */

public class ShareDataSetAdapter extends RecyclerView.Adapter<ShareDataSetAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<ExpenseMemberData> memberListWithData;
    private String g_id="";
    private String g_currency="";
    private Double tot_amount=0.0;
    private MyMemberListSetting listSettingListener;


    public ShareDataSetAdapter(Context context, ArrayList<ExpenseMemberData> memberListWithData, String g_currency) {

        mContext=context;
        this.memberListWithData=memberListWithData;
        this.g_currency=g_currency;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.cust_row_expense_member_list_data, parent,false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        ExpenseMemberData e_data= memberListWithData.get(position);
        holder.name.setText(e_data.getMember_name());
        holder.amount.setText(g_currency+" "+String.format("%s",e_data.getValue()));

    }

    @Override
    public int getItemCount() {
        return memberListWithData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView name,amount,date;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.expense_data_member_name);
            amount=itemView.findViewById(R.id.expense_data_amount);

            date=itemView.findViewById(R.id.expense_data_date);//Date is used in ShareDataDisplayAdapter; here no use of date
            date.setVisibility(View.GONE);

        }


    }
}
