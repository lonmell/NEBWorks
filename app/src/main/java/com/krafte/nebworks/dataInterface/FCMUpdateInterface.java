package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FCMUpdateInterface {
    String URL = "http://krafte.net/NEBWorks/fcm_token/";

    @FormUrlEncoded
    @POST("update.php")
    Call<String> getData(
            @Field("id") String id,
            @Field("token") String token,
            @Field("channel1") String channel1,
            @Field("channel2") String channel2,
            @Field("channel3") String channel3,
            @Field("channel4") String channel4
    );
}