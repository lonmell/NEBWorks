package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface WorkStatusTapInterface
{
    String URL = "https://nepworks.net/NEBWorks/commute/";
    //https://nepworks.net/NEBWorks/commute/get_tap.php?place_id=183&user_id=199&tap=&date=2023-02-28
    @FormUrlEncoded
    @POST("get_tap.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("user_id") String user_id,
            @Field("tap") String tap,
            @Field("date") String date
    );
}