package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PlaceMemberInsertOther
{
    String URL = "https://nepworks.net/NEBWorks/place/";

    @FormUrlEncoded
    @POST("post_member_other.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("user_id") String user_id,
            @Field("age") String age,
            @Field("email") String email,
            @Field("address") String address,
            @Field("introduce") String introduce,
            @Field("career") String career
    );
}