package com.TuDime.Retrofit;


import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitCallback {

    @POST("user_profile_images.php")
    Call<ResponseBody> uploadMultiFile(@Body RequestBody file);




}
