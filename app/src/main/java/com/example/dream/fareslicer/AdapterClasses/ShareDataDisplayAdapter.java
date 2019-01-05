package com.example.dream.fareslicer.AdapterClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dream.fareslicer.BeanClasses.ShareDataList;
import com.example.dream.fareslicer.Interface.MyMemberListSetting;
import com.example.dream.fareslicer.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by Devu Mani on 13/12/2018.
 * To show shares details in ExpenseActivity
 */

public class ShareDataDisplayAdapter extends RecyclerView.Adapter<ShareDataDisplayAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<ShareDataList> shareList;
    private String currency="";
    private Double tot_amount=0.0;
    private MyMemberListSetting listSettingListener;


    public ShareDataDisplayAdapter(Context context, ArrayList<ShareDataList> shareList) {

        mContext=context;
        this.shareList=shareList;
//        currency=mContext.getResources().getString(R.string.currency);//Hardcoded

        SharedPreferences sharedPreferences=mContext.getSharedPreferences("User",Context.MODE_PRIVATE);
        currency=sharedPreferences.getString("user_currency","");

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.cust_row_expense_member_list_data, parent,false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        ShareDataList s_data= shareList.get(position);

        holder.name.setText(s_data.getUser_name());

        if(s_data.getUser_name().equalsIgnoreCase("you")) {

            holder.name.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryLight));
        }
        else
        {
            holder.name.setTextColor(mContext.getResources().getColor(R.color.Black));

        }

        holder.date.setText(s_data.getDate());

        if(Double.parseDouble(s_data.getShare_amount())>=0) {
            holder.amount.setText(currency+" "+String.format("%s", s_data.getShare_amount()));
            holder.amount.setTextColor(mContext.getResources().getColor(R.color.caribbean_green));
        }
        else
        {
            Double x = Math.abs(Double.parseDouble(s_data.getShare_amount()));

            holder.amount.setText(currency+" "+String.format("%s", x));
            holder.amount.setTextColor(mContext.getResources().getColor(R.color.warning_red));
        }

    }

    @Override
    public int getItemCount() {
        return shareList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView name,amount,date;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.expense_data_member_name);
            amount=itemView.findViewById(R.id.expense_data_amount);
            date=itemView.findViewById(R.id.expense_data_date);

        }


    }
}
