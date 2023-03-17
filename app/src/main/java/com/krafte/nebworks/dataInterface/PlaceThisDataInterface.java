package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PlaceThisDataInterface
{
    String URL = "http://krafte.net/NEBWorks/place/";
    //http://krafte.net/NEBWorks/place/get.php?place_id=50
    @FormUrlEncoded
    @POST("get.php")
    Call<String> getData(
            @Field("place_id") String place_id
    );
}