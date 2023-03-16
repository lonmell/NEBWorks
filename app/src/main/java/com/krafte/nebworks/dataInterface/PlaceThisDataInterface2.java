package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PlaceThisDataInterface2
{
    String URL = "https://nepworks.net//v2/place/";
    //https://nepworks.net/NEBWorks/place/get.php?place_id=50
    @FormUrlEncoded
    @POST("get/")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("user_id") String user_id,
            @Field("auth") String auth
    );
}