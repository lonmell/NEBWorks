package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TaskreuseSInterface
{
    String URL = "https://nepworks.net/NEBWorks/task_reuse/";
    //https://nepworks.net/NEBWorks/task_reuse/get.php?place_id=116
    @FormUrlEncoded
    @POST("get.php")
    Call<String> getData(
            @Field("place_id") String place_id
    );
}