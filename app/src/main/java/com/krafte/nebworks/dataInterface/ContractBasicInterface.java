package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ContractBasicInterface {
    String URL = "http://krafte.net/NEBWorks/contract/";

    @FormUrlEncoded
    @POST("post.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("owner_id") String owner_id,
            @Field("worker_id") String worker_id,
            @Field("buisness_kind") String buisness_kind,
            @Field("owner_name") String owner_name,
            @Field("registr_num") String registr_num,
            @Field("zipcode") String zipcode,
            @Field("address") String address,
            @Field("address_detail") String address_detail,
            @Field("place_size") String place_size,
            @Field("owner_phone") String owner_phone,
            @Field("owner_email") String owner_email
    );
}