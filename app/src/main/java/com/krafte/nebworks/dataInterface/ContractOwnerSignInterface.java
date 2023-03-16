package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ContractOwnerSignInterface {
    String URL = "https://nepworks.net/NEBWorks/contract/";

    @FormUrlEncoded
    @POST("update_ownersign.php")
    Call<String> getData(
            @Field("id") String id,
            @Field("owner_sign_url") String owner_sign_url
    );
}