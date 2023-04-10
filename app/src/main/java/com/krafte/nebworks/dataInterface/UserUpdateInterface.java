package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserUpdateInterface
{
    String URL = "http://krafte.net/NEBWorks/user/";

    @FormUrlEncoded
    @POST("update.php")
    Call<String> getData(
            @Field("id") String id,
            @Field("name") String name,
            @Field("nick_name") String nick_name,
            @Field("password") String password,
            @Field("phone") String phone,
            @Field("gender") String gender,
            @Field("img_path") String img_path
    );
}