package com.example.dream.fareslicer.RetrofitClientAndInterface;


import com.example.dream.fareslicer.RetrofitInputOutputClasses.CallResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.QueryValue;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Api {


    @POST("insert")
    Call<CallResult> insert(@Body QueryValue queryValue);


    @POST("select")
    Call<CallResult> select(@Body QueryValue queryValue);

    @POST("delete")
    Call<CallResult> delete(@Body QueryValue queryValue);

}
