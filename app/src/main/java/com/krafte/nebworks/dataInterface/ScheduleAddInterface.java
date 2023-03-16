package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ScheduleAddInterface
{
    String URL = "https://nepworks.net/NEBWorks/task/";

    @FormUrlEncoded
    @POST("post_schedule.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("writer_id") String writer_id,
            @Field("title") String title,
            @Field("contents") String contents,
            @Field("task_date") String task_date,
            @Field("start_time") String start_time,
            @Field("end_time") String end_time,
            @Field("sun") String Sun,
            @Field("mon") String Mon,
            @Field("tue") String Tue,
            @Field("wed") String Wed,
            @Field("thu") String Thu,
            @Field("fri") String Fri,
            @Field("sat") String Sat,
            @Field("users") String users
    );
}