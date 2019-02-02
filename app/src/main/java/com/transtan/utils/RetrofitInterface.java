package com.transtan.utils;

/**
 * Created by djayakum on 3/25/2018.
 */

import com.transtan.model.DonorData;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @Headers("Referer: https://transtan.org")
    @Multipart
    @POST("appPhp/mobileform.php")
    Call<ResponseBody> upload(
            @PartMap Map<String, RequestBody> data,
            @Part MultipartBody.Part photo
    );

    @Headers("Referer: https://transtan.org")
    @Multipart
    @POST("appPhp/mobileform.php")
    Call<ResponseBody> uploadWithoutPhoto(
            @PartMap Map<String, RequestBody> data
    );

    @Headers("Referer: https://transtan.org")
    @GET("appPhp/mobileform.php")
    Call<List<DonorData>> getDonorData(
            @Query("username") String username,
            @Query("password") String password
    );
}
