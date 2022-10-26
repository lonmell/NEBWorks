package com.krafte.kogas.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GetWorkCntInterface {
    String URL = "http://krafte.net/kogas/work_status/";

    @FormUrlEncoded
    @POST("get_work_count.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("user_id") String user_id
    );
}