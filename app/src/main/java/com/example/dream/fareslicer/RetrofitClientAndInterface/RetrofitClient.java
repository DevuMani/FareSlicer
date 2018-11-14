package com.example.dream.fareslicer.RetrofitClientAndInterface;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BaseUrl="http://codenext.us-east-2.elasticbeanstalk.com/";

    private static RetrofitClient minstance;
    private Retrofit retrofit;

    public RetrofitClient(){
        retrofit=new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public static synchronized RetrofitClient getInstance(){

        if (minstance==null)
        {
            minstance=new RetrofitClient();

        }
        return minstance;
    }

    public Api getApi()
    {
        return retrofit.create(Api.class);

    }
}
