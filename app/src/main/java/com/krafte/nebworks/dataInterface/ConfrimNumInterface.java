package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ConfrimNumInterface {
    String URL = "http://krafte.net/NEBWorks/place/";

    @FormUrlEncoded
    @POST("confirm_Registrnum.php")
    Call<String> getData(
            @Field("registr_num") String registr_num
    );
}