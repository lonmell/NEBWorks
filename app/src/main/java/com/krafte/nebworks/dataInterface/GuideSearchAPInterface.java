package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GuideSearchAPInterface {
    String URL = "https://business.juso.go.kr/addrlink/";
    //https://business.juso.go.kr/addrlink/addrLinkApi.do?currentPage=1&countPerPage=10&keyword=대구동구&confmKey=U01TX0FVVEgyMDIzMDEwOTE2MjQxMDExMzQxMjE=&resultType=json
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