package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UpdateViewInterfcae
{
    String URL = "https://nepworks.net/NEBWorks/feed/";

    @FormUrlEncoded
    @POST("update_view_cnt.php")
    Call<String> getData(
            @Field("feed_id") String feed_id
    );
}