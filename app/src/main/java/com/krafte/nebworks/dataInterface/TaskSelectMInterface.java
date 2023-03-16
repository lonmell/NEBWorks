package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

//관리자가 조회할때
public interface TaskSelectMInterface
{
    String URL = "https://nepworks.net/NEBWorks/task/";

    @FormUrlEncoded
    @POST("get.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("selected_date") String selected_date
    );
}