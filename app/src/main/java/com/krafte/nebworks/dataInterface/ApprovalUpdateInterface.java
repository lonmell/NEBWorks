package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApprovalUpdateInterface {
    String URL = "https://nepworks.net/NEBWorks/task_approval/";

    @FormUrlEncoded
    @POST("update.php")
    Call<String> getData(
            @Field("id") String id,
            @Field("user_id") String user_id,
            @Field("state") String state,
            @Field("reject_reason") String reject_reason
    );
}