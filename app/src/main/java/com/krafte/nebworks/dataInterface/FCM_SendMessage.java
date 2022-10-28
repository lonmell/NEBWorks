package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FCM_SendMessage {
    String URL = "http://krafte.net/kogas/";

    @FormUrlEncoded
    @POST("kogas_fcmsend.php")
    Call<String> getData(
            @Field("topic") String topic,
            @Field("title") String title,
            @Field("message") String message,
            @Field("token") String token,
            @Field("click_action") String click_action,
            @Field("tag") String tag,
            @Field("place_id") String place_id
    );
}