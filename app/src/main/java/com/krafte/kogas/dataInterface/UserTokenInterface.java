package com.krafte.kogas.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserTokenInterface {
    String URL = "http://krafte.net/app_php/";

    @FormUrlEncoded
    @POST("mobile_fcmtoken_manager.php")
    Call<String> setData(
            @Field("flag") String flag,
            @Field("user_id") String user_id,
            @Field("type") String type,
            @Field("token") String token,
            @Field("channelId1") String channelId1,
            @Field("channelId2") String channelId2,
            @Field("channelId3") String channelId3,
            @Field("channelId4") String channelId4
    );
}
