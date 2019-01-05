package com.example.dream.fareslicer.DialogClass;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dream.fareslicer.AdapterClasses.ShareAmongAdapter;
import com.example.dream.fareslicer.BeanClasses.ExpenseMemberData;
import com.example.dream.fareslicer.Interface.MyDialogBoxEventListener;
import com.example.dream.fareslicer.Interface.MyMemberListSetting;
import com.example.dream.fareslicer.R;
import com.example.dream.fareslicer.RetrofitClientAndInterface.RetrofitClient;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.QueryValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareAmongDialogClass extends Dialog {

    private static final String TAG = "ShareAmongDialog";
    private Double amount = 0.0;
    private String g_id = "";
    private String g_currency = "";
    private String paymentMode = "";
    private String paymentId = "";

    private TextView heading;
    private TextView warning_text1, warning_text2;
    private RecyclerView recyclerView;
    Button shareOk;
    private Context context;

    private LinearLayoutManager linearLayoutManager; //common for both recycler view

    //Adapter and arraylist for expense_share recycler view
    private ShareAmongAdapter shareAmongAdapter;
    private ArrayList<ExpenseMemberData> member_list;
    private ExpenseMemberData expenseMemberData;
    private MyMemberListSetting  memberListSettingListener;


    private MyDialogBoxEventListener dialogBoxEventListener = new MyDialogBoxEventListener() {
        @Override
        public void onTextTypedChange(String msg1, String msg2)
        {
            SpannableString spannableString1=new SpannableString(msg1);
            SpannableString spannableString2=new SpannableString(msg2);
            String[] separated1 = msg1.split(" ");
            if(Double.parseDouble(separated1[0])<=Double.parseDouble(separated1[2]))
            {
                //No problem same color
                shareOk.setEnabled(true);
            }
            else {
//                spannableString1.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.warning_red)), 0, msg1.indexOf(" ") , 0);
                spannableString1.setSpan(new ForegroundColorSpan(Color.RED), 0, msg1.indexOf(" ") , 0);

                shareOk.setEnabled(false);
            }

            if (!msg2.equals(""))
            {
                String[] separated2 = msg2.split(" ");
                if(Double.parseDouble(separated2[0])<0.0)
                {
//                    spannableString2.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.warning_red)), 0, msg1.indexOf(" ") , 0);
                    spannableString2.setSpan(new ForegroundColorSpan(Color.RED), 0, msg1.indexOf(" ") , 0);
                    shareOk.setEnabled(false);
                }
                else if(Double.parseDouble(separated2[0])>0.0)
                {
                    Toast.makeText(context, Math.abs(Double.parseDouble(separated2[0]))+" is left", Toast.LENGTH_SHORT).show();
                }
                else if(Double.parseDouble(separated2[0])==0.0)
                {
                    shareOk.setEnabled(true);
                }

            }
            warning_text1.setText(spannableString1);
            warning_text2.setText(spannableString2);
        }
    };



    public ShareAmongDialogClass(Context context, String g_id, String g_currency, String paymentId, String paymentMode, Double amount, MyMemberListSetting listSettingListener) {
        super(context);

        this.context = context;
        this.g_id = g_id;
        this.g_currency = g_currency;
        this.paymentId = paymentId;
        this.paymentMode = paymentMode;
        this.amount = amount;
        this.memberListSettingListener = listSettingListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_share_among);

        member_list = new ArrayList<>();
        initView();
        setDialogSize();
        memberShowCall();
        initShareAmongRecyclerView();
//        setMyDialogListener(this);
        this.setCanceledOnTouchOutside(false);
        shareOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(member_list.isEmpty()) {

                    Toast.makeText(context, "No share(s)", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    dismiss();
                    memberListSettingListener.onListSetting(member_list);
                }
            }
        });

    }

    private void setDialogSize() {

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
    }

    @Override
    public void onBackPressed() {

        dismiss();

    }
//        /**
//     * dialog close listener
//     * @param dialogClass
//     */
//    public void setMyDialogListener(ShareAmongDialogClass dialogClass)
//    {
//        dialogClass.setOnDismissListener(new OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialogInterface) {
//
//                memberListSettingListener.onListSetting(member_list);
//
//            }
//        });
//    }
//

    private void initView() {

        heading = findViewById(R.id.share_dialog_heading);
        heading.setText(paymentMode);
        warning_text1 = findViewById(R.id.warning_text1);
        warning_text2 = findViewById(R.id.warning_text2);
        recyclerView = findViewById(R.id.share_dialog_recyclerView);
        shareOk = findViewById(R.id.share_ok);


    }

    private void initShareAmongRecyclerView() {
        linearLayoutManager = new LinearLayoutManager(context);
        //shareAmongAdapter = new ShareAmongAdapter(context, paymentId, g_currency, amount,member_list);
        recyclerView.setLayoutManager(linearLayoutManager);
        //shareAmongAdapter.setListener(dialogBoxEventListener);
       // recyclerView.setAdapter(shareAmongAdapter);
    }

    private void memberShowCall() {

        String query = "select t1.member_id,t1.user_id,t2.user_name from tb_member t1,tb_user t2 where t1.user_id = t2.user_id and t1.group_id =?";
        ArrayList<String> value = new ArrayList();
        value.add("" + g_id);

        QueryValue queryValue = new QueryValue();
        queryValue.setQuery(query);
        queryValue.setValue(value);

        Call<CallResult> call = RetrofitClient.getInstance().getApi().select(queryValue);

        call.enqueue(new Callback<CallResult>() {
            @Override
            public void onResponse(Call<CallResult> call, Response<CallResult> response) {

                if (response.code() == 200) {

                    CallResult selectResult = response.body();

                    String success = "";
                    if (selectResult != null) {
                        success = selectResult.getStatus();

                        if (success.equalsIgnoreCase("true")) {

                            List<CallOutput> callOutputList = selectResult.getOutput();
                            for (int i = 0; i < callOutputList.size(); i++) {
                                CallOutput callOutput = callOutputList.get(i);
                                List<String> value = callOutput.getValue();

                                expenseMemberData = new ExpenseMemberData(value.get(0), value.get(1), value.get(2));
                                member_list.add(expenseMemberData);

                                shareAmongAdapter = new ShareAmongAdapter(context, paymentId, g_currency, amount,member_list);
                                shareAmongAdapter.setListener(dialogBoxEventListener);
                                recyclerView.setAdapter(shareAmongAdapter);

                            }


                        } else {
                            Log.e(TAG + " select", success);
//                            Snackbar.make(linearLayout,"Selection Failed",Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Log.e(TAG + " select", selectResult.toString());
//                        Snackbar.make(linearLayout,"Selection Failed",Snackbar.LENGTH_LONG).show();

                    }
                } else {

                    String s = "";
                    try {
                        if (response.errorBody() != null) {
                            s = response.errorBody().string();
                            Log.e(TAG + " select","Error body is "+s);
                        } else {
                            Log.e(TAG + " select", "Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e(TAG + " select", e.getMessage());

                    }
                }


            }

            @Override
            public void onFailure(Call<CallResult> call, Throwable t) {
//                Toast.makeText(NewGroup.this, "Insertion Call failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG + " select", t.getMessage());
            }
        });

    }

}
