package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FcmTokenDelInterface {
    String URL = "https://nepworks.net/NEBWorks/fcm_token/";

    @FormUrlEncoded
    @POST("delete.php")
    Call<String> getData(
            @Field("user_id") String user_id,
            @Field("type") String type
    );
}