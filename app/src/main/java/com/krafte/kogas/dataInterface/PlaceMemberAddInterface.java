package com.krafte.kogas.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PlaceMemberAddInterface
{
    String URL = "http://krafte.net/kogas/place/";

    @FormUrlEncoded
    @POST("add_member.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("user_id") String user_id
    );
}