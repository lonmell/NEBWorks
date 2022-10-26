package com.krafte.kogas.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserDelInterface
{
    String URL = "http://krafte.net/kogas/user/";

    @FormUrlEncoded
    @POST("delete.php")
    Call<String> getData(
            @Field("id") String id
    );
}

