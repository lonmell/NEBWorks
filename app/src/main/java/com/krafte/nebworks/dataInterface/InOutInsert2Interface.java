package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface InOutInsert2Interface {
    String URL = "https://nepworks.net/NEBWorks/commute/";

    @FormUrlEncoded
    @POST("post.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("user_id") String user_id,
            @Field("kind") String kind,
            @Field("io_date") String io_date,
            @Field("io_time") String io_time
    );
}
