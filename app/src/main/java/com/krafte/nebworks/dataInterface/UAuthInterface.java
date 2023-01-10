package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 2023-01-10 추가 - 방창배
 * 점주의 경우 : 점주 권한에서 매장을 하나라도 추가하면 user 의 user_auth가 0으로 업데이트
 * 근로자의 경우 : 근로자 권한에서 매장을 하나라도 추가하거나 매장에 참가하면 user_auth가 1로 업데이트
 * */
public interface UAuthInterface
{
    String URL = "http://krafte.net/NEBWorks/user/";

    @FormUrlEncoded
    @POST("update_auth.php")
    Call<String> getData(
            @Field("id") String id,
            @Field("auth") String auth
    );
}