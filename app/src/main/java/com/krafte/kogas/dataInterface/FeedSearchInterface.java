package com.krafte.kogas.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FeedSearchInterface
{
    String URL = "http://krafte.net/kogas/feed/";

    @FormUrlEncoded
    @POST("search_feed.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("word") String word
    );
}