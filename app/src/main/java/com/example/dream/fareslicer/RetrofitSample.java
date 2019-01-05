package com.example.dream.fareslicer;

import android.util.Log;

import com.example.dream.fareslicer.RetrofitClientAndInterface.RetrofitClient;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.QueryValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitSample {


    private static final String TAG = "RetrofitSample";

    private void selectGroupId(String g_name, String user_id) {

        String query="select group_id from tb_group where group_name=? and user_id=?";
        ArrayList<String> value=new ArrayList();
        value.add(g_name);
        value.add(user_id);

        QueryValue queryValue=new QueryValue();
        queryValue.setQuery(query);
        queryValue.setValue(value);

        Call<CallResult> call=RetrofitClient.getInstance().getApi().select(queryValue);

        call.enqueue(new Callback<CallResult>() {
            @Override
            public void onResponse(Call<CallResult> call, Response<CallResult> response) {

                if(response.code()==200) {

                    CallResult selectResult = response.body();

                    String success = "";
                    if (selectResult != null) {
                        success = selectResult.getStatus();

                        if (success.equalsIgnoreCase("true")) {


                            List<CallOutput> outputList=selectResult.getOutput();
                            for (int i = 0; i <outputList.size() ; i++) {

                                CallOutput callOutput=outputList.get(i);

                            }

                        }
                        else
                        {
                            Log.e(TAG,success);
//                            Snackbar.make(linearLayout,"Selection Failed",Snackbar.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Log.e(TAG,selectResult.toString());
//                        Snackbar.make(linearLayout,"Selection Failed",Snackbar.LENGTH_LONG).show();

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
            public void onFailure(Call<CallResult> call, Throwable t) {
//                Toast.makeText(NewGroup.this, "Insertion Call failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG, t.getMessage());
            }
        });


    }

}
