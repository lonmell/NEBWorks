package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FeedNotiInterface
{
    String URL = "http://krafte.net/NEBWorks/feed/";
    //http://krafte.net/NEBWorks/feed/get.php?place_id=96&feed_id=&sort=1&kind=2
    @FormUrlEncoded
    @POST("get.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("feed_id") String feed_id,
            @Field("sort") String sort,
            @Field("kind") String kind
    );
}