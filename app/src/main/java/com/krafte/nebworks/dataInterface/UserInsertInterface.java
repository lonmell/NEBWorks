package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserInsertInterface
{
    String URL = "https://nepworks.net/NEBWorks/user/";
//    https://nepworks.net/NEBWorks/user/post.php?account=lonmell0213@gmail.com&name=방창배&password=yento0213!!&phone=01085529025&gender=1&img_path=
    @FormUrlEncoded
    @POST("post.php")
    Call<String> getData(
            @Field("account") String account,
            @Field("name") String name,
            @Field("nick_name") String nick_name,
            @Field("password") String password,
            @Field("phone") String phone,
            @Field("gender") String gender,
            @Field("img_path") String img_path,
            @Field("platform") String platform
    );
}