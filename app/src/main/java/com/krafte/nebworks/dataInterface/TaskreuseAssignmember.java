package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TaskreuseAssignmember
{
    String URL = "https://nepworks.net/NEBWorks/task_reuse/";

    @FormUrlEncoded
    @POST("assign_member.php")
    Call<String> getData(
            @Field("id") String id,
            @Field("writer_id") String writer_id,
            @Field("task_date") String task_date,
            @Field("users") String users
    );
}