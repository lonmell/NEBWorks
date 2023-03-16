package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PlaceMemberInsertDetail
{
    String URL = "https://nepworks.net/NEBWorks/place/";

    @FormUrlEncoded
    @POST("post_member_detail.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("user_id") String user_id,
            @Field("state") String state,
            @Field("jikgup") String jikgup,
            @Field("paykind") String paykind,
            @Field("pay") String pay,
            @Field("worktime") String worktime,
            @Field("workhour") String workhour,
            @Field("task") String task
    );
}