package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FeedCommentListInterface {
    String URL = "http://krafte.net/NEBWorks/comment/";

    @FormUrlEncoded
    @POST("get.php")
    Call<String> getData(
            @Field("feed_id") String feed_id,
            @Field("user_id") String user_id
    );
}
