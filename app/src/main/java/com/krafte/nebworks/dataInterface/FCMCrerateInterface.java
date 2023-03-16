package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FCMCrerateInterface {
    String URL = "https://nepworks.net/NEBWorks/fcm_token/";

    @FormUrlEncoded
    @POST("post.php")
    Call<String> getData(
            @Field("user_id") String user_id,
            @Field("type") String type,
            @Field("token") String token,
            @Field("channel1") String channel1,
            @Field("channel2") String channel2,
            @Field("channel3") String channel3,
            @Field("channel4") String channel4,
            @Field("channel5") String channel5
    );
}