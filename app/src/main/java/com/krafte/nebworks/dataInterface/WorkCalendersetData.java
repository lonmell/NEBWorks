package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface WorkCalendersetData
{
    String URL = "http://krafte.net/NEBWorks/task/";
    //http://krafte.net/NEBWorks/task/get_calendar.php?place_id=76&user_id=140&selected_date=2022-10
    @FormUrlEncoded
    @POST("get_calendar.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("user_id") String user_id,
            @Field("selected_date") String selected_date
    );
}