package com.krafte.kogas.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TaskApprovalInterface
{
    String URL = "http://krafte.net/kogas/task_approval/";

    @FormUrlEncoded
    @POST("post.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("task_id") String task_id,
            @Field("task_date") String task_date,
            @Field("user_id") String user_id
    );
}