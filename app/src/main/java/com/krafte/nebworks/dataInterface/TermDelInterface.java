package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TermDelInterface
{
    String URL = "http://krafte.net/NEBWorks/contract/";

    @FormUrlEncoded
    @POST("term_delete.php")
    Call<String> getData(
            @Field("id") String id
    );
}