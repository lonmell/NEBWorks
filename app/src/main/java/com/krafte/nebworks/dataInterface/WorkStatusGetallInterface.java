package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface WorkStatusGetallInterface
{
    String URL = "http://krafte.net/NEBWorks/work_status/";
    //http://krafte.net/NEBWorks/task/get_all.php?place_id=183&user_id=199&selected_date=2023&auth=1
    @FormUrlEncoded
    @POST("get_all.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("user_id") String user_id,
            @Field("selected_date") String selected_date,
            @Field("auth") String auth
    );
}