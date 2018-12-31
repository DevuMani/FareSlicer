package com.example.dream.fareslicer.DialogClass;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dream.fareslicer.Activities.NewGroup;
import com.example.dream.fareslicer.R;
import com.example.dream.fareslicer.RetrofitClientAndInterface.RetrofitClient;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.QueryValue;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupShareSettle.GroupShareSettleInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupShareSettle.GroupShareSettleOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.IndividualShareSettle.IndividualShareSettleInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.IndividualShareSettle.IndividualShareSettleOutput;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class SettleUpDialogClass extends Dialog implements View.OnClickListener {

    private EditText et_amount;
    private ImageView self_image,other_image;
    private TextView settle_amount_display,settle_text,amount_currency;
    private Button save;
    private String TAG="Settle Dialog";

    Context context;
    String type;
    String amount="";
    String cur_user_id="";
    String other_user_name="";
    String currency="";

    //For individual settle
    String other_user_id="";

    //For group_settle
    String memberId="";
    String group_id="";



    //Called from transaction List Adapter
    public SettleUpDialogClass(Context context,String amount,String type,String other_user_id,String other_user_name) {
        super(context);

        this.context=context;
        this.type=type;
        this.amount=amount;
        this.other_user_id=other_user_id;
        this.other_user_name=other_user_name;
    }

    //Called from Group Member List Adapter
    public SettleUpDialogClass(Context context, String amount, String type, String memberId, String group_id,String other_user_name) {
        super(context);

        this.context=context;
        this.type=type;
        this.amount=amount;
        this.memberId=memberId;
        this.group_id=group_id;
        this.other_user_name=other_user_name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_settle_up);

        initView();
        init();

        SharedPreferences sp=context.getSharedPreferences("User",MODE_PRIVATE);
        String fileName=sp.getString("user_photo","");
//        currency=context.getResources().getString(R.string.currency);//remove
        currency=sp.getString("user_currency","");

        String path= Environment.getExternalStorageDirectory() + File.separator + "FareSlicer"+ File.separator +fileName;

        if(fileName.equals("")) {

            self_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_user));
//                imageview.setBackgroundColor(0xff00ff00);
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(path);

//            Toast.makeText(this, ""+bitmap, Toast.LENGTH_SHORT).show();
            self_image.setImageBitmap(bitmap);
        }

        et_amount.addTextChangedListener(myTextWatcher);

        save.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void initView() {

        amount_currency=findViewById(R.id.settle_amount_currency);
        et_amount=findViewById(R.id.settle_amount);
        self_image=findViewById(R.id.settle_image_self);
        other_image=findViewById(R.id.settle_image_other);
        settle_amount_display=findViewById(R.id.settle_amount_display);
        settle_text=findViewById(R.id.settle_text);
        settle_text.setVisibility(View.GONE);

        save=findViewById(R.id.settle_save);

    }

    private void init() {

        SharedPreferences sp=context.getSharedPreferences("User",MODE_PRIVATE);
        this.cur_user_id=sp.getString("user_id","");

        settle_amount_display.setText("Amount "+other_user_name+" paying you " );

        amount_currency.setText(currency);

        et_amount.setText(amount);

    }

    private TextWatcher myTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (charSequence.toString().equalsIgnoreCase(""))
            {
                charSequence="0";
            }
            Double cur_amount=Double.parseDouble(charSequence.toString());
            Double actual_amount=Double.parseDouble(amount);
            if(cur_amount>actual_amount)
            {
                save.setEnabled(false);
                settle_text.setVisibility(View.VISIBLE);
                settle_text.setText("Amount should not be greater than "+ "$"+ actual_amount);

            }
            else
            {
                save.setEnabled(true);
                settle_text.setVisibility(View.GONE);
                settle_text.setText("");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    public void onClick(View view) {

        if (view==save)
        {
            Toast.makeText(context, "Share amount"+ et_amount.getText().toString(), Toast.LENGTH_SHORT).show();
            if (type.equalsIgnoreCase("group_member"))
            {

                settleGroupCall();
            }
            else if (type.equalsIgnoreCase("transaction_member"))
            {
                settleIndividualCall();
            }
        }

    }

    private void settleGroupCall() {

        GroupShareSettleInput input=new GroupShareSettleInput();
        input.setUserId(cur_user_id);
        input.setGroupId(group_id);
        input.setMemberId(memberId);
        input.setAmount(et_amount.getText().toString());

        Call<GroupShareSettleOutput> call=RetrofitClient.getInstance().getApi().groupShareSettle(input);

        call.enqueue(new Callback<GroupShareSettleOutput>() {
            @Override
            public void onResponse(Call<GroupShareSettleOutput> call, Response<GroupShareSettleOutput> response) {

                if(response.code()==200) {

                    GroupShareSettleOutput selectResult = response.body();

                    try {


                        if (selectResult.getStatus().equalsIgnoreCase("true"))
                        {
                                dismiss();
                        }
                    }catch (NullPointerException n)
                    {
                        Log.e(TAG,n.getMessage());
                    }

                }
                else
                {
                    String s="";
                    try {
                        if (response.errorBody() != null) {
                            s=response.errorBody().string();
                            Log.e(TAG,"Error body is "+s);
                        }
                        else
                        {
                            Log.e(TAG,"Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e(TAG,e.getMessage());

                    }
                }

                }

            @Override
            public void onFailure(Call<GroupShareSettleOutput> call, Throwable t) {

            }
        });

    }


    private void settleIndividualCall() {

        IndividualShareSettleInput input=new IndividualShareSettleInput();
        input.setUserId(other_user_id);
        input.setCuruserId(cur_user_id);
        input.setAmount(et_amount.getText().toString());

        Call<IndividualShareSettleOutput> call= RetrofitClient.getInstance().getApi().individualShareSettle(input);

        call.enqueue(new Callback<IndividualShareSettleOutput>() {
            @Override
            public void onResponse(Call<IndividualShareSettleOutput> call, Response<IndividualShareSettleOutput> response) {

                if(response.code()==200) {

                    IndividualShareSettleOutput selectResult = response.body();

                    try {


                    if (selectResult.getStatus().equalsIgnoreCase("true"))
                    {
                        Toast.makeText(context, "Settled "+ "$"+et_amount.getText().toString()+ other_user_name, Toast.LENGTH_SHORT).show();
                    }

                    }catch (NullPointerException n)
                    {
                        Log.e(TAG,n.getMessage());
                    }

                }
                else
                {
                    String s="";
                    try {
                        if (response.errorBody() != null) {
                            s=response.errorBody().string();
                            Log.e(TAG,"Error body is "+s);
                        }
                        else
                        {
                            Log.e(TAG,"Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e(TAG,e.getMessage());

                    }
                }
            }

            @Override
            public void onFailure(Call<IndividualShareSettleOutput> call, Throwable t) {

                Log.e(TAG, t.getMessage() );
            }
        });



    }

    //    private void selectCall(String tb_phn) {
//
//        call.enqueue(new Callback<CallResult>() {
//            @Override
//            public void onResponse(Call<CallResult> call, Response<CallResult> response) {
//
//                if(response.code()==200) {
//
//                    CallResult selectResult=response.body();
//
//                    String success= "";
//                    if (selectResult != null) {
//                        success = selectResult.getStatus();
//
//                        if (success.equalsIgnoreCase("true")) {
//
//                            List<CallOutput> output = selectResult.getOutput();
//
//                            CallOutput item = output.get(0);
//                            List<String> list=item.getValue();
//                            String id=list.get(0);
//
//                            SharedPreferences sharedPreferences=getSharedPreferences("User",MODE_PRIVATE);
//                            SharedPreferences.Editor editor=sharedPreferences.edit();
//                            editor.putString("user_id",id);
//                            editor.apply();
//
//                            startActivity(new Intent(ProfilePage.this,Home.class));
//
//                        } else {
//                            Log.e("Selection", "Success is false");
//                        }
//                    }
//                    else
//                    {
//                        Log.e("Selection", "Success is null ");
//                    }
//
//                }
//                else {
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CallResult> call, Throwable t) {
//
//                Toast.makeText(ProfilePage.this, "Selection Call failed", Toast.LENGTH_SHORT).show();
//                Log.e("Selction", t.getMessage());
//            }
//
//        });
//    }


}
