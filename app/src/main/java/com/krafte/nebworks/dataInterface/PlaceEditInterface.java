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
            @Field("registr_num") String registr_num,
            @Field("store_kind") String store_kind,
            @Field("address") String address,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("pay_day") String pay_day,
            @Field("test_period") String test_period,
            @Field("vacation_select") String vacation_select,
            @Field("insurance") String insurance,
            @Field("start_time") String start_time,
            @Field("end_time") String end_time,
            @Field("img_path") String img_path,
            @Field("save_kind") String save_kind,
            @Field("start_date") String start_date,
            @Field("wifi_name") String wifi_name
    );
}