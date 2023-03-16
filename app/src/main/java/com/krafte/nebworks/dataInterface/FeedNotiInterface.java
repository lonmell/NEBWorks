package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FeedNotiInterface
{
    String URL = "https://nepworks.net/NEBWorks/feed/";
    //https://nepworks.net/NEBWorks/feed/get.php?place_id=&feed_id=159&sort=&kind=2&user_id=199
    @FormUrlEncoded
    @POST("get.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("feed_id") String feed_id,
            @Field("sort") String sort,
            @Field("kind") String kind,
            @Field("user_id") String user_id //해당게시물에 사용자의 좋아요가 있는지 확인하기 위한 user_id 전송
    );
}