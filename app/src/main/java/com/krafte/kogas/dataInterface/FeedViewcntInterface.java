package com.krafte.kogas.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FeedViewcntInterface {
    String URL = "http://krafte.net/kogas/feed/";

    @FormUrlEncoded
    @POST("get.php")
    Call<String> getData(
            @Field("feed_id") String feed_id
    );
}
