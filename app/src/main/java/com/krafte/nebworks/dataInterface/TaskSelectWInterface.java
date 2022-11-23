package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

//근로자가 조회할때
public interface TaskSelectWInterface
{
    String URL = "http://krafte.net/NEBWorks/task/";
    //http://krafte.net/NEBWorks/task/get.php?place_id=70&user_id=47&selected_date=2022-11-23
    @FormUrlEncoded
    @POST("get.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("user_id") String user_id,
            @Field("selected_date") String selected_date
    );
}