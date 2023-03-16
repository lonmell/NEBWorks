package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TaskSapprovalInterface
{
    String URL = "https://nepworks.net/NEBWorks/task_approval/";
    // https://nepworks.net/NEBWorks/task_approval/get.php?place_id=96&state=&approval_date=2022-11-25
    @FormUrlEncoded
    @POST("get.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("state") String state,
            @Field("approval_date") String approval_date
    );
}