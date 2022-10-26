package com.krafte.kogas.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TaskreuseInputInterface
{
    String URL = "http://krafte.net/kogas/task_reuse/";

    @FormUrlEncoded
    @POST("post.php")
    Call<String> getData(
            @Field("place_id") String place_id,
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