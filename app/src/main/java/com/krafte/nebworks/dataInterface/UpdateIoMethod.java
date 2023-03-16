package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UpdateIoMethod {
    String URL = "https://nepworks.net/NEBWorks/place/";

    @FormUrlEncoded
    @POST("update_iomethod.php")
    Call<String> getData(
            @Field("id") String id,
            @Field("io_method") String io_method
    );
}
