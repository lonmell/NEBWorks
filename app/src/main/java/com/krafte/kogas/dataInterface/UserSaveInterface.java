package com.krafte.kogas.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserSaveInterface
{
    String URL = "http://krafte.net/kogas/user/";

    @FormUrlEncoded
    @POST("update.php")
    Call<String> getData(
            @Field("id") String id, //- user_id (account와 다른 기존 no 개념입니다.)
            @Field("name") String name, //- 이름
            @Field("kind") String kind, //- 0:정직원, 1:협력업체
            @Field("employee_no") String employee_no, //- 사번
            @Field("department") String department, //- 부서명
            @Field("position") String position, //- 직급
            @Field("img_path") String img_path //- 프로필 사진
    );
}