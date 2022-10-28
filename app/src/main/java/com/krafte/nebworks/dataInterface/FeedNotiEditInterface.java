package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FeedNotiEditInterface {
    String URL = "http://krafte.net/NEBWorks/feed/";

    @FormUrlEncoded
    @POST("update.php")
    Call<String> getData(
            @Field("id") String place_id,
            @Field("title") String title,
            @Field("contents") String contents,
            @Field("link") String writer_id,
            @Field("img_path") String img_path,
            @Field("file_path") String file_path
    );
}