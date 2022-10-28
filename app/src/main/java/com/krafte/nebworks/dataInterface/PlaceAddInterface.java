package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PlaceAddInterface
{
    String URL = "http://krafte.net/NEBWorks/place/";

    @FormUrlEncoded
    @POST("post.php")
    Call<String> getData(
            @Field("name") String name,
            @Field("owner_id") String owner_id,
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