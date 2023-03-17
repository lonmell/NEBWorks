package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

//업무를 임시 저장할때
public interface TaskSaveInterface
{
    String URL = "http://krafte.net/NEBWorks/task_temp/";

    @FormUrlEncoded
    @POST("post_update.php")
    Call<String> getData(
            @Field("task_id") String task_id,
            @Field("task_title") String task_title,
            @Field("task_date") String task_date,
            @Field("img_path") String img_path,
            @Field("complete_yn") String complete_yn,
            @Field("incomplete_reason") String incomplete_reason
    );
}