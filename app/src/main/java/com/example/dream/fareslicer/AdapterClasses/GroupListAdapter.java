package com.example.dream.fareslicer.AdapterClasses;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dream.fareslicer.BeanClasses.GroupData;
import com.example.dream.fareslicer.Activities.GroupActivity;
import com.example.dream.fareslicer.DialogClass.LongClickDialogClass;
import com.example.dream.fareslicer.R;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by DevuMani on 10/5/2018.
 */

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<GroupData> groupList;
    private String currency="";

    public GroupListAdapter(Context context, ArrayList<GroupData> group_list) {
        mContext=context;
        groupList=group_list;
//        currency=mContext.getResources().getString(R.string.currency);//remove currency
        SharedPreferences sp=mContext.getSharedPreferences("User",Context.MODE_PRIVATE);
        currency=sp.getString("user_currency","");

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.cust_row_group_list, parent,false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        GroupData g_data= groupList.get(position);

        holder.tv_name.setText(g_data.getGroupName());

        String amount=String.format("%.2f", Double.parseDouble(g_data.getAmount()));
        String owe=String.format("%.2f", Double.parseDouble(g_data.getOwe()));
        String own=String.format("%.2f", Double.parseDouble(g_data.getOwn()));

        if(amount.equals("0"))
        {
            holder.tv_group_settle_text.setText("You are all settled up");
            holder.tv_group_settle_amount.setVisibility(View.GONE);
        }
        else {

            if (Double.parseDouble(amount) < 0) //Owe
            {
                Double x = Math.abs(Double.parseDouble(amount));
                holder.tv_group_settle_text.setText("Amount");
                holder.tv_group_settle_amount.setVisibility(View.VISIBLE);
                holder.tv_group_settle_amount.setText(currency+" "+x);
                holder.tv_group_settle_amount.setTextColor(mContext.getResources().getColor(R.color.warning_red));
            }
            else //own
            {
                holder.tv_group_settle_text.setText("Amount");
                holder.tv_group_settle_amount.setVisibility(View.VISIBLE);
                holder.tv_group_settle_amount.setText(currency+" "+amount);
                holder.tv_group_settle_amount.setTextColor(mContext.getResources().getColor(R.color.caribbean_green));
            }
        }

            Double x = Math.abs(Double.parseDouble(owe));
            holder.tv_group_owe_text.setText(mContext.getResources().getString(R.string.group_owe_text));
            holder.tv_group_owe_amount.setText(currency+" "+ x);
            holder.tv_group_owe_amount.setTextColor(mContext.getResources().getColor(R.color.warning_red));


//            holder.layout_group_own.setVisibility(View.VISIBLE);
            holder.tv_group_own_text.setText(mContext .getResources().getString(R.string.group_own_text));
            holder.tv_group_own_amount.setText(currency+" "+ own);
            holder.tv_group_own_amount.setTextColor(mContext.getResources().getColor(R.color.caribbean_green));

    }

    @Override
    public int getItemCount() {
//        Toast.makeText(mContext, "Size"+iconList.size(), Toast.LENGTH_SHORT).show();
        return groupList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private CardView cardView;
        private TextView tv_name;
        private TextView tv_group_settle_text,tv_group_settle_amount;//amount portion
        private TextView tv_group_owe_text,tv_group_owe_amount;//owe portion
        private TextView tv_group_own_text,tv_group_own_amount;//own portion
        private LinearLayout layout_group_amount,layout_group_owe,layout_group_own;

        private MyViewHolder(View itemView) {
            super(itemView);

            cardView=itemView.findViewById(R.id.group_cardView);
            tv_name = itemView.findViewById(R.id.group_name);

            //Group amount data's
            layout_group_amount=itemView.findViewById(R.id.layout_group_amount);
            tv_group_settle_text=itemView.findViewById(R.id.group_settle_text);
            tv_group_settle_amount=itemView.findViewById(R.id.group_settle_amount);

            //Group Owe data's
            layout_group_owe=itemView.findViewById(R.id.layout_group_owe);
            tv_group_owe_text=itemView.findViewById(R.id.group_owe_text);
            tv_group_owe_amount=itemView.findViewById(R.id.group_owe_amount);

            //Group Own data's
            layout_group_own=itemView.findViewById(R.id.layout_group_own);
            tv_group_own_text=itemView.findViewById(R.id.group_own_text);
            tv_group_own_amount=itemView.findViewById(R.id.group_own_amount);

            cardView.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View view) {

            if (view == cardView) {

                GroupData g_data = groupList.get(getAdapterPosition());

//                Toast.makeText(mContext, "Id : " +g_data.getGroupId(), Toast.LENGTH_SHORT).show();

                Intent intent=new Intent(mContext,GroupActivity.class);
                intent.putExtra("group_id",g_data.getGroupId());
                intent.putExtra("group_name",g_data.getGroupName());
                intent.putExtra("group_owe",g_data.getOwe());
                intent.putExtra("group_own",g_data.getOwn());
                intent.putExtra("group_amount",g_data.getAmount());
                mContext.startActivity(intent);

            }

        }

        @Override
        public boolean onLongClick(View view) {

            GroupData g_data = groupList.get(getAdapterPosition());

//            Toast.makeText(mContext, "Id : " +g_data.getGroup_id(), Toast.LENGTH_SHORT).show();

            LongClickDialogClass cd=new LongClickDialogClass(mContext,g_data.getGroupId());
            Window window = cd.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cd.show();

            return false;
        }
    }
}
