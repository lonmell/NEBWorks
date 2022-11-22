package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TaskInputInterface
{
    String URL = "http://krafte.net/NEBWorks/task/";
    //http://krafte.net/NEBWorks/task/post.php?place_id=70&writer_id=47&kind=0&title=yucyxyxyx&contents=icucuxyxx&complete_kind=1&task_date=2022-11-22&start_time=17:55&end_time=21:55&sun=1&mon=1&tue=1&wed=1&thu=1&fri=1&sat=1&users=53,47
    @FormUrlEncoded
    @POST("post.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("writer_id") String writer_id,
            @Field("title") String title,
            @Field("contents") String contents,
            @Field("complete_kind") String complete_kind,
            @Field("task_date") String task_date,
            @Field("start_time") String start_time,
            @Field("end_time") String end_time,
            @Field("sun") String sun,
            @Field("mon") String mon,
            @Field("tue") String tue,
            @Field("wed") String wed,
            @Field("thu") String thu,
            @Field("fri") String fri,
            @Field("sat") String sat,
            @Field("task_overdate") String task_overdate,
            @Field("users") String users
    );
}