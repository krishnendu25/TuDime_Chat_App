package com.TuDime.Retrofit;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;

    public static Retrofit getRetrofitClient(){

        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://18.219.14.108/tudime_sms/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;

    }





}
