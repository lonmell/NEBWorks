package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NotifyListInterface
{
    String URL = "https://nepworks.net/NEBWorks/push/";
    //https://nepworks.net/NEBWorks/push/get.php?place_id=116&receiver_id=69
    @FormUrlEncoded
    @POST("get.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("receiver_id") String receiver_id
    );
}