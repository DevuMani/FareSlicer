package com.example.dream.fareslicer.DialogClass;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.dream.fareslicer.Activities.EditGroup;
import com.example.dream.fareslicer.R;
import com.example.dream.fareslicer.RetrofitClientAndInterface.RetrofitClient;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.QueryValue;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupDeletion.GroupDeletionInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupDeletion.GroupDeletionOutput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AmountPayClass extends Dialog {

    private static final String TAG = "Amount Pay Dialog" ;
    private Button btn_pay;
    private EditText et_amount;

    Context context;

    public AmountPayClass(Context context) {
        super(context);

        this.context=context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.amount_pay);

        initView();

        setDialogSize();

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                google wallet();
                Toast.makeText(context, "Amount"+et_amount.getText().toString(), Toast.LENGTH_SHORT).show();

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


    private void initView() {

        et_amount=findViewById(R.id.pay_amount);
        btn_pay=findViewById(R.id.pay_button);

    }


}
