package com.example.dream.fareslicer.AdapterClasses;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dream.fareslicer.Activities.ExpenseActivity;
import com.example.dream.fareslicer.BeanClasses.ExpenseListData;
import com.example.dream.fareslicer.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by DevuMani on 10/5/2018.
 */

public class ExpenseListAdapter extends RecyclerView.Adapter<ExpenseListAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<ExpenseListData> expense_list;
    private String currency="";

//just for intent passing
    String g_id="";
    String g_name="";

    public ExpenseListAdapter(Context context, ArrayList<ExpenseListData> expense_list, String g_id, String g_name) {
        mContext=context;
        this.expense_list=expense_list;

        SharedPreferences sharedPreferences=mContext.getSharedPreferences("User",Context.MODE_PRIVATE);
//        currency=mContext.getResources().getString(R.string.currency);//Hardcoded
        currency=sharedPreferences.getString("user_currency","");

        this.g_id=g_id;
        this.g_name=g_name;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.cust_row_expense_list, parent,false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        ExpenseListData e_data = expense_list.get(position);

        holder.name.setText(e_data.getExpenseName());

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat user_sdf=new SimpleDateFormat("dd-MM-yyyy");

        String user_date="";
        Date date=null;

        try {
            date=sdf.parse(e_data.getExpenseDate());
            user_date=user_sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.date.setText(user_date);

        holder.expense_amount.setText("Expense Amount "+ currency+" "+ e_data.getExpenseAmount());
        if (Double.parseDouble(e_data.getUserAmount())<0)
        {
            Double x = Math.abs(Double.parseDouble(e_data.getUserAmount()));
            holder.user_amount.setText("You should give "+ currency+" "+ x );
            holder.user_amount.setTextColor(mContext.getResources().getColor(R.color.warning_red));
        }
        else if (Double.parseDouble(e_data.getUserAmount())>=0)
        {
            holder.user_amount.setText("You get "+ currency+" "+ e_data.getUserAmount());
            holder.user_amount.setTextColor(mContext.getResources().getColor(R.color.caribbean_green));
        }


    }

    @Override
    public int getItemCount() {
//        Toast.makeText(mContext, "Size"+iconList.size(), Toast.LENGTH_SHORT).show();
        return expense_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CardView cardView;
        private TextView name,date,expense_amount,user_amount;

        private MyViewHolder(View itemView) {
            super(itemView);

            cardView=itemView.findViewById(R.id.expense_cardView);
            name = itemView.findViewById(R.id.expense_list_name);

            date=itemView.findViewById(R.id.expense_list_date);
            expense_amount=itemView.findViewById(R.id.expense_list_amount);
            user_amount=itemView.findViewById(R.id.expense_list_user_amount);

            cardView.setOnClickListener(this);
//            cardView.setOnLongClickListener(this);

        }


        @Override
        public void onClick(View view) {

            if (view == cardView) {

                ExpenseListData e_data = expense_list.get(getAdapterPosition());

//                Toast.makeText(mContext, "Id : " +e_data.getExpenseId(), Toast.LENGTH_SHORT).show();

                Intent intent=new Intent(mContext,ExpenseActivity.class);
                intent.putExtra("expense_id",e_data.getExpenseId());
                intent.putExtra("expense_name",e_data.getExpenseName());

                //just for return intent passing
                intent.putExtra("group_id",g_id);
                intent.putExtra("group_name",g_name);

                mContext.startActivity(intent);

            }

        }

    }
}
