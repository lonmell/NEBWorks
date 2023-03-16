package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ContractPayInterface {
    String URL = "https://nepworks.net/NEBWorks/contract/";

    @FormUrlEncoded
    @POST("update_pay.php")
    Call<String> getData(
            @Field("id") String id,
            @Field("pay_type") String pay_type,
            @Field("payment") String payment,
            @Field("pay_conference") String pay_conference,
            @Field("pay_loop") String pay_loop,
            @Field("insurance") String insurance,
            @Field("add_contents") String add_contents
    );
}