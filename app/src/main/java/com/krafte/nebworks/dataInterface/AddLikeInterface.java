package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AddLikeInterface {
    String URL = "https://nepworks.net/NEBWorks/feed/";

    @FormUrlEncoded
    @POST("post_like.php")
    Call<String> getData(
            @Field("feed_id") String feed_id,
            @Field("comment_id") String comment_id,
            @Field("user_id") String user_id,
            @Field("kind") String kind
    );
}
