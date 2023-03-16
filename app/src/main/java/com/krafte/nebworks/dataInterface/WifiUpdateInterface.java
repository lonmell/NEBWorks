package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface WifiUpdateInterface
{
    String URL = "https://nepworks.net/NEBWorks/place/";

    @FormUrlEncoded
    @POST("post_wifi.php")
    Call<String> getData(
            @Field("id") String id,
            @Field("save_kind") String save_kind,
            @Field("wifi_name") String wifi_name
    );
}