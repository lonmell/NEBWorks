package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TaskreuseUpInterface
{
    String URL = "http://krafte.net/NEBWorks/task_reuse/";

    @FormUrlEncoded
    @POST("update.php")
    Call<String> getData(
            @Field("id") String id,
            @Field("writer_id") String writer_id,
            @Field("title") String title,
            @Field("contents") String contents,
            @Field("complete_kind") String complete_kind,
            @Field("start_time") String start_time,
            @Field("end_time") String end_time,
            @Field("sun") String sun,
            @Field("mon") String mon,
            @Field("tue") String tue,
            @Field("wed") String wed,
            @Field("thu") String thu,
            @Field("fri") String fri,
            @Field("sat") String sat
    );
}