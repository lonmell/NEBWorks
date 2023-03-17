package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FCMSelectInterface {
    String URL = "http://krafte.net/NEBWorks/fcm_token/";
    //http://krafte.net/NEBWorks/fcm_token/get.php?user_id=69&type=0
    @FormUrlEncoded
    @POST("get.php")
    Call<String> getData(
            @Field("user_id") String user_id,
            @Field("type") String type
    );
}