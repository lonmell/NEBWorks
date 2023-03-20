package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ContractCntInterface {
    String URL = "http://krafte.net/NEBWorks/contract/";

    @FormUrlEncoded
    @POST("get.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("worker_id") String worker_id
    );
}