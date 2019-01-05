package com.example.dream.fareslicer.AdapterClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dream.fareslicer.BeanClasses.TransactionData;
import com.example.dream.fareslicer.BeanClasses.TransactionFragmentData;
import com.example.dream.fareslicer.DialogClass.SettleUpDialogClass;
import com.example.dream.fareslicer.R;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by DevuMani on 10/5/2018.
 */

public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<TransactionFragmentData> transactionList;
    private String currency="";

    public TransactionListAdapter(Context context, ArrayList<TransactionFragmentData> transaction_list) {
        mContext=context;
        transactionList=transaction_list;
        SharedPreferences sp=mContext.getSharedPreferences("User",Context.MODE_PRIVATE);
        currency=sp.getString("user_currency","");

//        currency=mContext.getResources().getString(R.string.currency);//remove currency
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.cust_row_transaction_list, parent,false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        TransactionFragmentData t_data= transactionList.get(position);

        holder.tv_name.setText(t_data.getUserName());

        String amount=String.format("%.2f", Double.parseDouble(t_data.getAmount()));

//        String text_owe = mContext.getResources().getString(R.string.individual_owe_text)+" "+currency+" ";
//        String text_own = mContext.getResources().getString(R.string.individual_own_text)+" "+currency+" ";

        if(Double.parseDouble(amount)<0) //owe
        {
            Double x = Math.abs(Double.parseDouble(amount));
            holder.tv_text.setText(mContext.getResources().getString(R.string.individual_owe_text));
            holder.tv_amount.setText(currency+" "+x);
            holder.tv_amount.setTextColor(mContext.getResources().getColor(R.color.warning_red));
        }
        else if(Double.parseDouble(amount)>=0) //own
        {
            holder.tv_text.setText(mContext.getResources().getString(R.string.individual_own_text));

            holder.tv_amount.setText(currency+" "+amount);
            holder.tv_amount.setTextColor(mContext.getResources().getColor(R.color.caribbean_green));
        }


    }

    @Override
    public int getItemCount() {
//        Toast.makeText(mContext, "Size"+iconList.size(), Toast.LENGTH_SHORT).show();
        return transactionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView tv_name,tv_text,tv_amount;
        private CardView cardView;
//        public ImageView icon;
//        public ImageView delete;


        public MyViewHolder(View itemView) {
            super(itemView);

            cardView=itemView.findViewById(R.id.trans_all_settle);
            tv_name = itemView.findViewById(R.id.trans_member_name);
            tv_text=itemView.findViewById(R.id.trans_member_text);
            tv_amount =itemView.findViewById(R.id.trans_member_amount);

            cardView.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {

            TransactionFragmentData t_data = transactionList.get(getAdapterPosition());

//            if (view == name) {
//
//                Toast.makeText(mContext, "Id : " +t_data.getUserId(), Toast.LENGTH_SHORT).show();
//
//            }
//
            if (Double.parseDouble(t_data.getAmount())>0) {

                SettleUpDialogClass settleUpDialogClass=new SettleUpDialogClass(mContext,t_data.getAmount(),"transaction_member",t_data.getUserId(),t_data.getUserName());
                Window window =settleUpDialogClass.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                settleUpDialogClass.show();
            }
            else
            {
                Toast.makeText(mContext, t_data.getUserName()+" have nothing to pay you", Toast.LENGTH_SHORT).show();
            }


        }

        @Override
        public boolean onLongClick(View view) {

            TransactionFragmentData t_data = transactionList.get(getAdapterPosition());


            return false;
        }
    }
}
