package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TermGetInterface
{
    String URL = "https://nepworks.net/NEBWorks/contract/";

    @FormUrlEncoded
    @POST("term_get.php")
    Call<String> getData(
            @Field("contract_id") String contract_id
    );
}