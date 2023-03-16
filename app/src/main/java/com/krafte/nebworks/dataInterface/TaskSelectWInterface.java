package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

//근로자가 조회할때
public interface TaskSelectWInterface
{
    String URL = "https://nepworks.net/NEBWorks/task/";
    //https://nepworks.net/NEBWorks/task/get.php?place_id=166&user_id=199&selected_date=2023-02-15
    @FormUrlEncoded
    @POST("get.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("user_id") String user_id,
            @Field("selected_date") String selected_date,
            @Field("auth") String auth
    );
}