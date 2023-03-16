package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public interface ContractPagePosUp {
    String URL = "https://nepworks.net/NEBWorks/contract/";

    @FormUrlEncoded
    @POST("update.php")
    Call<String> getData(
            @Field("id") String id,
            @Field("pos") String pos
    );
}