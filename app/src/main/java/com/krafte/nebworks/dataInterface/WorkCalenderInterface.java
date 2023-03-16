package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface WorkCalenderInterface
{
    String URL = "https://nepworks.net/NEBWorks/";

    @FormUrlEncoded
    @POST("create_calender.php")
    Call<String> getData(
            @Field("getYear") String getYear,
            @Field("getMonth") String getMonth
    );
}