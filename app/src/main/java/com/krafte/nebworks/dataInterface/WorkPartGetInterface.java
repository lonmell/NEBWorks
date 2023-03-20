package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface WorkPartGetInterface
{
    String URL = "http://krafte.net/NEBWorks/work_status/";

    @FormUrlEncoded
    @POST("get_contractworkhour.php")
    Call<String> getData(
            @Field("place_id")    String place_id,
            @Field("user_id")     String user_id,
            @Field("yoil")        String yoil
    );
}