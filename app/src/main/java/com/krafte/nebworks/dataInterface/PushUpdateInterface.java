package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PushUpdateInterface
{
    String URL = "http://krafte.net/kogas/push/";

    @FormUrlEncoded
    @POST("update.php")
    Call<String> getData(
            @Field("id") String id
    );
}