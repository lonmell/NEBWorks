package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface paymanaInterface
{
    String URL = "https://nepworks.net/NEBWorks/pay/";
    //https://nepworks.net/NEBWorks/pay/paymanager.php?flag=1&place_id=183&GET_DATE=2023-02&user_id=199&basic_pay=&second_pay=&overwork_hour=&overwork_pay=&meal_allowance_yn=&meal_pay=&store_insurance_yn=&other_memo=&all_payment=&selectym=
    @FormUrlEncoded
    @POST("paymanager.php")
    Call<String> getData(
            @Field("flag") String flag,
            @Field("place_id") String store_no,
            @Field("GET_DATE") String GET_DATE,
            @Field("user_id") String user_id,
            @Field("basic_pay") String basic_pay,
            @Field("second_pay") String second_pay,
            @Field("overwork_hour") String overwork_hour,
            @Field("overwork_pay") String overwork_pay,
            @Field("meal_allowance_yn") String meal_allowance_yn,
            @Field("meal_pay") String meal_pay,
            @Field("store_insurance_yn") String store_insurance_yn,
            @Field("other_memo") String other_memo,
            @Field("all_payment") String all_payment,
            @Field("selectym") String selectym
    );
}
