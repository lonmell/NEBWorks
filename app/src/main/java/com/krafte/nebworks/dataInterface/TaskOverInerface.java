package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

//근로자가 본인이 추가한 매장에서 본인이 추가한 할일을 종료할대 사용
public interface TaskOverInerface
{
    String URL = "http://krafte.net/NEBWorks/task_approval/";
    //http://krafte.net/NEBWorks/task_approval/post_accept.php?place_id=208&task_id=307&user_id=199
    @FormUrlEncoded
    @POST("post_accept.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("task_id") String task_id,
            @Field("user_id") String user_id
    );
}