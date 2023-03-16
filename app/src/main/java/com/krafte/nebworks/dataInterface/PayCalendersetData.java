package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PayCalendersetData
{
    String URL = "https://nepworks.net/NEBWorks/pay/";
    //https://nepworks.net/NEBWorks/pay/get_calendar.php?place_id=1660&user_id=199&selected_date=2023-02
    @FormUrlEncoded
    @POST("get_calendar.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("user_id") String user_id,
            @Field("year") String year,
            @Field("month") String month
    );
}