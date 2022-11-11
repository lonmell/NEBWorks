package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MainWorkCntInterface {
    String URL = "http://krafte.net/NEBWorks/place/";
    //http://krafte.net/NEBWorks/place/get_main.php?place_id=24&user_id=16
    @FormUrlEncoded
    @POST("get_main.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("user_id") String user_id
    );
}