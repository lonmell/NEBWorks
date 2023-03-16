package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PlaceSearchListInterface
{
    String URL = "https://nepworks.net/NEBWorks/place/";
    //https://nepworks.net/NEBWorks/place/get.php?place_id=-99&user_id=199&auth=0
    @FormUrlEncoded
    @POST("SearchPlace_get.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("user_id") String user_id,
            @Field("auth") String auth
    );
}