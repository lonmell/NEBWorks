package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface WorkStatusTapInterface
{
    String URL = "http://krafte.net/NEBWorks/commute/";
    //http://krafte.net/NEBWorks/commute/get_tap.php?place_id=116&user_id=77&tap=1&date=2022-12-30
    @FormUrlEncoded
    @POST("get_tap.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("user_id") String user_id,
            @Field("tap") String tap,
            @Field("date") String date
    );
}