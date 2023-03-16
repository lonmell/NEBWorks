package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MainContentsInterface {
    String URL = "https://nepworks.net/NEBWorks/place/";
    //https://nepworks.net/NEBWorks/place/get_main.php?place_id=183&auth=1&user_id=199&kind=1
    @FormUrlEncoded
    @POST("get_main.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("auth") String auth,
            @Field("user_id") String user_id,
            @Field("kind") String kind
    );
}