package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FeedConfrimInterface
{
    String URL = "http://krafte.net/NEBWorks/feed/";
    //http://krafte.net/NEBWorks/feed/post_confirm_comment.php?feed_id=9&writer_id=43
    @FormUrlEncoded
    @POST("post_confirm_comment.php")
    Call<String> getData(
            @Field("feed_id") String feed_id,
            @Field("write_id") String write_id
    );
}