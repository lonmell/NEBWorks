package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FobiddenInterface {
    String URL = "http://krafte.net/NEBWorks/user/";
    //http://krafte.net/NEBWorks/user/fobidden_word.php?word=&kind=1
    @FormUrlEncoded
    @POST("fobidden_word.php")
    Call<String> getData(
            @Field("word") String word,
            @Field("kind") String kind
    );
}