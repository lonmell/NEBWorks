package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PlaceMemberAddInterface
{
    String URL = "http://krafte.net/NEBWorks/place/";

    @FormUrlEncoded
    @POST("add_member.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("user_id") String user_id,
            @Field("jumin") String jumin,
            @Field("kind") String kind,//--kind는 직접입력시에는 2 / 초대시에는 0으로 저장, 초대받은 멤버가 승인하면 1
            @Field("join_date") String join_date
    );
}