package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserSaveInterface
{
    String URL = "https://nepworks.net/NEBWorks/user/";

    @FormUrlEncoded
    @POST("update.php")
    Call<String> getData(
            @Field("id") String id,
            @Field("name") String name,
            @Field("account") String kind,
            @Field("password") String employee_no,
            @Field("phone") String department,
            @Field("gender") String position,
            @Field("img_path") String img_path
    );
}