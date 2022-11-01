package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegistrSearchInterface
{
    String URL = "http://krafte.net/NEBWorks/";

    @FormUrlEncoded
    @POST("Registrnum_state.php")
    Call<String> getData(
            @Field("b_no") String b_no
    );

}
