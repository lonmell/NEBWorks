package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GetNotHourInterface {
    String URL = "https://nepworks.net/NEBWorks/member/";
    //https://nepworks.net/NEBWorks/member/getNotHour.php?place_id=24
    @FormUrlEncoded
    @POST("getNotHour.php")
    Call<String> getData(
            @Field("place_id") String place_id
    );
}