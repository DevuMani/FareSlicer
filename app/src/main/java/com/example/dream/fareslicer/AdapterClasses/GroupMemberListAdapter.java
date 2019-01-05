package com.example.dream.fareslicer.AdapterClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dream.fareslicer.BeanClasses.GroupMemberData;
import com.example.dream.fareslicer.DialogClass.SettleUpDialogClass;
import com.example.dream.fareslicer.R;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by DevuMani on 10/5/2018.
 */

public class GroupMemberListAdapter extends RecyclerView.Adapter<GroupMemberListAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<GroupMemberData> groupMemberList;
    private String user_id;
    private String currency="";
    private String group_id;


    public GroupMemberListAdapter(Context context, ArrayList<GroupMemberData> group_member_list, String user_id, String g_id) {
        mContext=context;
        groupMemberList=group_member_list;
        this.user_id = user_id;
//        this.currency=context.getResources().getString(R.string.currency);//currency remove
        SharedPreferences sp=mContext.getSharedPreferences("User",Context.MODE_PRIVATE);
        this.currency=sp.getString("user_currency","");
        group_id=g_id;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.cust_row_group_member_list, parent,false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        GroupMemberData g_data= groupMemberList.get(position);

        String amount=String.format("%.2f", Double.parseDouble(g_data.getAmount()));
        if(user_id.equalsIgnoreCase(g_data.getUserId()))
        {
            holder.name.setText("You");
        }
        else {

            holder.name.setText(g_data.getUserName());
        }

        if (Double.parseDouble(amount)<0)
        {
            Double x = Math.abs(Double.parseDouble(amount));

            holder.amount.setText(" "+currency+" "+x);
            holder.amount.setTextColor(mContext.getResources().getColor(R.color.warning_red));

        }
        else if (Double.parseDouble(amount)>=0)
        {

            holder.amount.setText(" "+currency+" "+amount);
            holder.amount.setTextColor(mContext.getResources().getColor(R.color.caribbean_green));

        }


    }

    @Override
    public int getItemCount() {
//        Toast.makeText(mContext, "Size"+iconList.size(), Toast.LENGTH_SHORT).show();
        return groupMemberList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name,amount;
        private CardView member_settle;

        private MyViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.group_member_name);
            amount= itemView.findViewById(R.id.member_settle_amount);
            member_settle=itemView.findViewById(R.id.member_settle);

//            name.setOnClickListener(this);
            member_settle.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            GroupMemberData g_data = groupMemberList.get(getAdapterPosition());

            if (view == name) {

//                Toast.makeText(mContext, "Id : " +g_data.getMemberId(), Toast.LENGTH_SHORT).show();


            }
            else if (view==member_settle)
            {
                String amount=String.format("%.2f", Double.parseDouble(g_data.getAmount()));
                if (Double.parseDouble(amount)>0) {

                    SettleUpDialogClass settleUpDialogClass = new SettleUpDialogClass(mContext, amount, "group_member", g_data.getMemberId(),group_id,g_data.getUserName());
//                    Window window = settleUpDialogClass.getWindow();
//                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(settleUpDialogClass.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.gravity = Gravity.CENTER;

                    settleUpDialogClass.getWindow().setAttributes(lp);

                    settleUpDialogClass.show();
                }
                else
                {
                    Toast.makeText(mContext, g_data.getUserName()+" have nothing to pay you", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
}
