package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FeedConfrimlistInterface
{
    String URL = "http://krafte.net/NEBWorks/feed/";
    //http://krafte.net/NEBWorks/feed/post_confirm_comment.php?feed_id=24&writer_id=
    @FormUrlEncoded
    @POST("get_confirm_comment.php")
    Call<String> getData(
            @Field("feed_id") String feed_id,
            @Field("writer_id") String writer_id
    );
}