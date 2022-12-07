package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public interface ContractWorkerInterface {
    String URL = "http://krafte.net/NEBWorks/contract/";

    @FormUrlEncoded
    @POST("update_worker.php")
    Call<String> getData(
            @Field("id") String id,
            @Field("worker_name") String worker_name,
            @Field("worker_jumin") String worker_jumin,
            @Field("worker_address") String worker_address,
            @Field("worker_address_detail") String worker_address_detail,
            @Field("work_phone") String work_phone,
            @Field("worker_email") String worker_email
    );
}