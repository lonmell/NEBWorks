package com.krafte.nebworks.dataInterface;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PlaceMemberUpdateBasic
{
    String URL = "http://krafte.net/NEBWorks/place/";

    @FormUrlEncoded
    @POST("update_member_basic.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("user_id") String user_id,
            @Field("name") String name,
            @Field("phone") String phone,
            @Field("jumin") String jumin,
            @Field("kind") String kind,
            @Field("join_date") String join_date
    );
}
