package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface WorkPartSaveInterface
{
    String URL = "http://krafte.net/NEBWorks/work_status/";

    @FormUrlEncoded
    @POST("post_contractworkhour.php")
    Call<String> getData(
            @Field("place_id")    String place_id,
            @Field("user_id")     String user_id,
            @Field("yoil")        String yoil,
            @Field("workhour")    String workhour,
            @Field("sieob")       String sieob,
            @Field("jongeob")     String jongeob,
            @Field("breaktime01") String breaktime01,
            @Field("breaktime02") String breaktime02,
            @Field("breaktime")   String breaktime
    );
}
