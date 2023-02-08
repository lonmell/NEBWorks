package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MyListInterface
{
    String URL = "http://krafte.net/NEBWorks/user/";

    //http://krafte.net/NEBWorks/user/get_mylist.php?user_id=199&kind=0
    @FormUrlEncoded
    @POST("get_mylist.php")
    Call<String> getData(
            @Field("user_id") String user_id,
            @Field("kind") String kind
    );
}