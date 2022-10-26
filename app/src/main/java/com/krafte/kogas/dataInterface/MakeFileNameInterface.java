package com.krafte.kogas.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MakeFileNameInterface
{
    String URL = "http://krafte.net/kogas/image/";

    @FormUrlEncoded
    @POST("get_img_id.php")
    Call<String> getData(
            @Field("id") String id
    );
}