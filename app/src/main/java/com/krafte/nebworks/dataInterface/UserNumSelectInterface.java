package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserNumSelectInterface
{
    String URL = "http://krafte.net/NEBWorks/user/";
    //http://krafte.net/NEBWorks/user/getNamePhone.php?name=김도운&phone=01085529025
    @FormUrlEncoded
    @POST("getNamePhone.php")
    Call<String> getData(
            @Field("name") String name,
            @Field("phone") String phone
    );
}