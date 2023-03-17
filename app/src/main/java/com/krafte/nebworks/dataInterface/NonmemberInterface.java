package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NonmemberInterface {
    String URL = "http://krafte.net/NEBWorks/user/";

    //http://krafte.net/NEBWorks/user/post_nonmember.php
    @FormUrlEncoded
    @POST("post_nonmember.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("name") String name,
            @Field("phone") String phone
    );
}
