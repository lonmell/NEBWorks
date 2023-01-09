package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GuideSearchAPInterface {
    String URL = "https://business.juso.go.kr/addrlink/";

    @FormUrlEncoded
    @POST("addrLinkApi.do")
    Call<String> getData(
            @Field("currentPage") String currentPage,
            @Field("countPerPage") String countPerPage,
            @Field("keyword") String keyword,
            @Field("confmKey") String confmKey,
            @Field("resultType") String resultType
    );
}