package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ContractWorkerSignInterface {
    String URL = "https://nepworks.net/NEBWorks/contract/";

    @FormUrlEncoded
    @POST("update_workersign.php")
    Call<String> getData(
            @Field("id") String id,
            @Field("worker_sign_url") String worker_sign_url
    );
}