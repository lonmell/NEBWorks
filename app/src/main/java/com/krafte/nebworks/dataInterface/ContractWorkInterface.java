package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ContractWorkInterface {
    String URL = "https://nepworks.net/NEBWorks/contract/";

    @FormUrlEncoded
    @POST("update_work.php")
    Call<String> getData(
            @Field("id") String id,
            @Field("contract_start") String contract_start,
            @Field("contract_end") String contract_end,
            @Field("contract_type") String contract_type,
            @Field("work_yoil") String work_yoil,
            @Field("rest_yoil") String rest_yoil,
            @Field("work_start") String work_start,
            @Field("work_end") String work_end,
            @Field("rest_start") String rest_start,
            @Field("rest_end") String rest_end,
            @Field("work_contents") String work_contents
    );
}