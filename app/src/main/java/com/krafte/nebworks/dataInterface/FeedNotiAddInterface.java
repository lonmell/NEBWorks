package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FeedNotiAddInterface {
    String URL = "http://krafte.net/NEBWorks/feed/";

    @FormUrlEncoded
    @POST("post.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("title") String title,
            @Field("contents") String contents,
            @Field("writer_id") String writer_id,
            @Field("link") String link,
            @Field("img_path") String img_path,
            @Field("file_path") String file_path
    );
}