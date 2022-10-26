package com.krafte.kogas.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserSelectInterface
{
    String URL = "http://krafte.net/kogas/user/";

    @FormUrlEncoded
    @POST("get.php")
    Call<String> getData(
            @Field("account") String account
    );
}