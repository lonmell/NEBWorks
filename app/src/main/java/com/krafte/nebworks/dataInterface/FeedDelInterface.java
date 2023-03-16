package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FeedDelInterface {
    String URL = "https://nepworks.net/NEBWorks/feed/";

    @FormUrlEncoded
    @POST("delete.php")
    Call<String> getData(
            @Field("id") String id
    );
}
