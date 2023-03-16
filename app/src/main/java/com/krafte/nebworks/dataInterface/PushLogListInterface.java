package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PushLogListInterface {
    String URL = "https://nepworks.net/NEBWorks/push/";

    @FormUrlEncoded
    @POST("post.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("kind") String kind,
            @Field("title") String title,
            @Field("contents") String contents,
            @Field("sender_id") String sender_id,
            @Field("receiver_id") String receiver_id
    );
}