package com.krafte.kogas.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MainWorkCntInterface {
    String URL = "http://krafte.net/kogas/place/";

    @FormUrlEncoded
    @POST("get_main.php")
    Call<String> getData(
            @Field("place_id") String place_id
    );
}