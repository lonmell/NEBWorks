package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PlaceEditInterface
{
    String URL = "http://krafte.net/NEBWorks/place/";

    @FormUrlEncoded
    @POST("update.php")
    Call<String> getData(
            @Field("id") String id,
            @Field("name") String name,
            @Field("management_office") String management_office,
            @Field("address") String address,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("start_time") String start_time,
            @Field("end_time") String end_time,
            @Field("img_path") String img_path,
            @Field("start_date") String start_date
    );
}