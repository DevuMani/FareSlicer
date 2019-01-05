package com.example.dream.fareslicer.AdapterClasses;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dream.fareslicer.BeanClasses.ExpenseMemberData;
import com.example.dream.fareslicer.BeanClasses.ShareDataModel;
import com.example.dream.fareslicer.Interface.MyDialogBoxEventListener;
import com.example.dream.fareslicer.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by DevuMani on 10/5/2018.
 */

public class ShareAmongAdapter extends RecyclerView.Adapter<ShareAmongAdapter.MyViewHolder> {

    private int arraySizeTotal = 0;
    private Context mContext;
    private String paymentId = "";
    private String g_currency = "";
    private Double tot_amount = 0.0;
    private Double total = 0.0;
    private Double currentTotal = 0.0;
    public Double total_share = 0.0;
    private ArrayList<ExpenseMemberData> member_list;
    private MyDialogBoxEventListener listener;
    private static final String TAG = "ShareAmongAdapter_basil";
    private ArrayList<ShareDataModel> shareDataModels;
    private ArrayList<ShareDataModel> shareEqualDataModels;

    ArrayList<String> positions=new ArrayList<>();

    public ShareAmongAdapter(Context context, String paymentId, String g_currency, Double tot_amount, ArrayList<ExpenseMemberData> member_list) {

        mContext = context;
        currentTotal = 0.0;
        this.paymentId = paymentId;
        this.g_currency = g_currency;
        this.tot_amount = tot_amount;
        total = tot_amount;
        this.member_list = member_list;
        shareDataModels = new ArrayList<>();
        shareEqualDataModels = new ArrayList<>();

        for (int i = 0; i < member_list.size(); i++) {
            ShareDataModel model = new ShareDataModel();
            model.setShareValue("0.0");
            shareDataModels.add(model);
        }

        for (int i = 0; i < member_list.size(); i++) {
            ShareDataModel model = new ShareDataModel();
            model.setShareValue("0.0");
            shareEqualDataModels.add(model);
        }
        Log.i("Total amount ", "" + tot_amount);
        Log.i("Total  ", "" + total);

    }


    public void setListener(MyDialogBoxEventListener listener) {
        this.listener = listener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.cust_row_expense_member_list, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;

    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        ExpenseMemberData e_data = member_list.get(position);
        holder.member_name.setText(e_data.getMember_name());

    }

    @Override
    public int getItemCount() {
        return member_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CheckBox member_checkBox;
        public TextView member_name, mode_symbol;
        public EditText amount;
        private int currentPosition;
        private String currentValueEntered;
        private LinearLayout mMemberListLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            member_checkBox = itemView.findViewById(R.id.new_expense_checkbox);
            member_name = itemView.findViewById(R.id.new_expense_text);
            mode_symbol = itemView.findViewById(R.id.mode_symbol);

            mMemberListLayout = itemView.findViewById(R.id.member_show_case);

            amount = itemView.findViewById(R.id.new_expense_amount);
            amount.setHint("0.0");

            if(paymentId.equals("1"))
            {
                member_checkBox.setVisibility(View.VISIBLE);
                amount.setVisibility(View.GONE);

//                setByEqually();

            }
            else
            {
                member_checkBox.setVisibility(View.GONE);
                amount.setVisibility(View.VISIBLE);
            }
            amount.addTextChangedListener(myTextWatcher);

            member_checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    ExpenseMemberData expenseMemberData=member_list.get(getAdapterPosition());

//                    if (member_checkBox.isChecked()==true)
//                    {
//                        Toast.makeText(mContext, "Id "+expenseMemberData.getMember_id()+"Position"+getAdapterPosition(), Toast.LENGTH_SHORT).show();
//
//
//                    }
                    setByEqually();

                }
            });

        }

        public TextWatcher myTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (paymentId.equals("2"))                 //split Unequally
                {
                    if (!TextUtils.isEmpty(charSequence.toString())) {
                        currentTotal = currentTotal - Double.parseDouble(charSequence.toString());
                    }
                } else if ((paymentId.equals("3"))) {
                    if (!TextUtils.isEmpty(charSequence.toString())) {
                        currentTotal = currentTotal - Double.parseDouble(charSequence.toString());
                    }
                } else if (paymentId.equals("4")) {
                    if (!TextUtils.isEmpty(charSequence.toString())) {
                        currentTotal = currentTotal - Double.parseDouble(charSequence.toString());
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (paymentId.equals("2"))                 //split Unequally
                {
                    setUnEqually(charSequence);
                } else if (paymentId.equals("3"))          //split by percentage
                {
                    setByPercentage(charSequence);
                } else if (paymentId.equals("4"))          //split by share
                {
                    setByShare(charSequence);
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        /**
         * calculation of share equally method
         */
        private void setByEqually() {

            ShareDataModel model = new ShareDataModel();
            if(member_checkBox.isChecked()) {
                model.setShareValue("true");
            }
            else
            {
                model.setShareValue("false");
            }
            try {
                int pos = getAdapterPosition();
                shareEqualDataModels.remove(pos);
            } catch (Exception exp) {
                Log.d(TAG, "onTextChanged: ex");
            }

            shareEqualDataModels.add(getAdapterPosition(), model);

            double shareCount = 0.0;

            for (int j = 0; j < shareEqualDataModels.size(); j++) {
                if (shareEqualDataModels.get(j).getShareValue().equalsIgnoreCase("true")) {
                    shareCount = shareCount + 1;
                }
            }

            DecimalFormat df = new DecimalFormat("#.##");
            double singleShareAmount = 0.0;
            if(shareCount<=0.0)
            {
                singleShareAmount=0.0;
            }
            else {
                singleShareAmount = tot_amount / shareCount;
            }
            singleShareAmount = Double.parseDouble(df.format(singleShareAmount));

            for (int j = 0; j < shareEqualDataModels.size(); j++)
            {
                if (shareEqualDataModels.get(j).getShareValue().equalsIgnoreCase("true")) {
//                    double indivual_share = singleShareAmount * Double.parseDouble(shareEqualDataModels.get(j).getShareValue());
                    member_list.get(j).setValue(singleShareAmount);
                } else {
                    member_list.get(j).setValue(0.0);
                }
            }

            String firstmsg = "Per share" + singleShareAmount + "of " + tot_amount;
//            if(singleShareAmount>tot_amount)
            listener.onTextTypedChange(firstmsg, "");

        }

        /**
         * calculation of unequal method
         *
         * @param charSequence
         */
        private void setUnEqually(CharSequence charSequence) {
            currentPosition = getAdapterPosition();
            currentValueEntered = charSequence.toString();
            if (TextUtils.isEmpty(charSequence.toString())) {
                member_list.get(currentPosition).setValue(0.0);
            } else {
                member_list.get(currentPosition).setValue(Double.parseDouble(currentValueEntered));
                currentTotal = currentTotal + Double.parseDouble(currentValueEntered);
            }
            String firstMsg = currentTotal + " of " + tot_amount;
            String secondMsg = (tot_amount - currentTotal) + " left";
            listener.onTextTypedChange(firstMsg, secondMsg);
        }

        /**
         * calculation of percentage method
         *
         * @param charSequence
         */
        private void setByPercentage(CharSequence charSequence) {
            currentPosition = getAdapterPosition();
            currentValueEntered = charSequence.toString();
            if (TextUtils.isEmpty(charSequence.toString())) {
                member_list.get(currentPosition).setValue(0.0);
            } else {
                DecimalFormat df = new DecimalFormat("#.##");
                String inputValue = df.format((tot_amount / 100) * Double.parseDouble(currentValueEntered));
                Log.d(TAG, "onTextChanged: " + inputValue);
                member_list.get(currentPosition).setValue(Double.parseDouble(inputValue));
                currentTotal = currentTotal + Double.parseDouble(currentValueEntered);

                String firstMsg = currentTotal + " % of 100%";
                String secondMsg = (100 - currentTotal) + "left";
                listener.onTextTypedChange(firstMsg, secondMsg);
            }
        }

        /**
         * calculation of share method
         *
         * @param charSequence
         */
        private void setByShare(CharSequence charSequence) {

            ShareDataModel model = new ShareDataModel();
            model.setShareValue(charSequence.toString());
            try {
                int pos = getAdapterPosition();
                shareDataModels.remove(pos);
            } catch (Exception exp) {
                Log.d(TAG, "onTextChanged: ex");
            }

            shareDataModels.add(getAdapterPosition(), model);

            double shareTotalCount = 0.0;
            for (int j = 0; j < shareDataModels.size(); j++) {
                if (shareDataModels.get(j).getShareValue() != null) {
                    shareTotalCount = shareTotalCount + Double.parseDouble(shareDataModels.get(j).getShareValue());
                }
            }

            DecimalFormat df = new DecimalFormat("#.##");
            double singleShareAmount = tot_amount / shareTotalCount;
            singleShareAmount = Double.parseDouble(df.format(singleShareAmount));

            for (int j = 0; j < shareDataModels.size(); j++) {
                if (shareDataModels.get(j).getShareValue() != null) {
                    double indivual_share = singleShareAmount * Double.parseDouble(shareDataModels.get(j).getShareValue());
                    member_list.get(j).setValue(indivual_share);
                } else {
                    member_list.get(j).setValue(0.0);
                }
            }

            String firstmsg = "Total" + shareTotalCount + "of " + tot_amount;
            listener.onTextTypedChange(firstmsg, "");

        }

    }
}
