package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TermInputInterface
{
    String URL = "http://krafte.net/NEBWorks/contract/";

    @FormUrlEncoded
    @POST("term_post.php")
    Call<String> getData(
            @Field("contract_id") String contract_id,
            @Field("term") String term
    );
}