package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FeedCommentInsertInterface {
    String URL = "http://krafte.net/NEBWorks/comment/";

    @FormUrlEncoded
    @POST("post.php")
    Call<String> getData(
            @Field("feed_id") String feed_id,
            @Field("comment") String comment,
            @Field("writer_id") String writer_id
    );
}
