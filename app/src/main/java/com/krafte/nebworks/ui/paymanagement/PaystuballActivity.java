package com.krafte.nebworks.ui.paymanagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.PushLogInputInterface;
import com.krafte.nebworks.dataInterface.paymanaInterface;
import com.krafte.nebworks.databinding.ActivityPaystuballBinding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class PaystuballActivity extends AppCompatActivity {
    private static final String TAG = "PaystuballActivity";
    private ActivityPaystuballBinding binding;
    Context mContext;

    private final DateCurrent dc = new DateCurrent();

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String USER_INFO_AUTH = "";
    String place_id;

    String stub_store_name = "";
    String stub_place_id = "";
    String stub_user_id = "";
    String stub_user_name = "";
    String stub_jikgup = "";
    String stub_basic_pay = "";
    String stub_second_pay = "";
    String stub_overwork_pay = "";
    String stub_meal_allowance_yn = "";
    String stub_store_insurance_yn = "";
    String stub_gongjeynpay = "";
    String stub_total_payment = "";
    String stub_workday = "";
    String stub_total_workday = "";
    String stub_total_workhour = "";
    String stub_payment = "";
    String stub_selectdate = "";
    String stub_meal_pay = "";
    String select_month = "";
    String place_name = "";
    String place_owner_id = "";
    String click_action = "";

    float insurance01p = 0;//국민연금 퍼센트
    float insurance02p = 0;//건강보험 퍼센트
    float insurance03p = 0;//고용보험 퍼센트
    float insurance04p = 0;//장기요양보험료 퍼센트

    String insurance1 = "";
    String insurance2 = "";
    String insurance3 = "";
    String insurance4 = "";

    //Other
    /*라디오 버튼들 boolean*/
    Drawable icon_off;
    Drawable icon_on;
    PageMoveClass pm = new PageMoveClass();
    int paging_position = 0;
    Dlog dlog = new Dlog();
    Handler mHandler = new Handler(Looper.getMainLooper());
    RetrofitConnect rc = new RetrofitConnect();

    String get_edit_expenses = "0";
    String get_edit_overwork = "0";
    String get_edit_overworkhour = "0";

    String select0102 = "직접입력";
    String select0304 = "포함";

    int AllPayment = 0;
    String GET_DATE = "";
    String Year = "";
    String Month = "";
    String Day = "";


    @SuppressLint({"UseCompatLoadingForDrawables", "SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_paystuball);
        binding = ActivityPaystuballBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        icon_off = mContext.getApplicationContext().getResources().getDrawable(R.drawable.resize_service_off);
        icon_on = mContext.getApplicationContext().getResources().getDrawable(R.drawable.ic_full_round_check);

        //Singleton Area
        USER_INFO_ID    = UserCheckData.getInstance().getUser_id();
        USER_INFO_NAME  = UserCheckData.getInstance().getUser_name();
        USER_INFO_AUTH  = shardpref.getString("USER_INFO_AUTH","");
        place_id        = PlaceCheckData.getInstance().getPlace_id();
        place_name      = PlaceCheckData.getInstance().getPlace_name();
        place_owner_id  = PlaceCheckData.getInstance().getPlace_owner_id();
        //shardpref Area

        //-------------------
        select_month            = shardpref.getString("select_month","");
        stub_store_name         = shardpref.getString("store_name","");
        stub_place_id           = shardpref.getString("stub_place_id","0");
        stub_user_id            = shardpref.getString("stub_user_id","0");
        stub_user_name          = shardpref.getString("stub_user_name","0");
        stub_jikgup             = shardpref.getString("stub_jikgup","0");
        stub_basic_pay          = shardpref.getString("stub_basic_pay","0");
        stub_second_pay         = shardpref.getString("stub_second_pay","0");
        stub_overwork_pay       = shardpref.getString("stub_overwork_pay","0");
        stub_meal_allowance_yn  = shardpref.getString("stub_meal_allowance_yn","0");
        stub_store_insurance_yn = shardpref.getString("stub_store_insurance_yn","0");
        stub_gongjeynpay        = shardpref.getString("stub_gongjeynpay","0");
        stub_total_payment      = shardpref.getString("stub_total_payment","0");
        stub_workday            = shardpref.getString("stub_workday","0");
        stub_total_workday      = shardpref.getString("stub_total_workday","0");
        stub_total_workhour     = shardpref.getString("stub_total_workhour","0");
        stub_payment            = shardpref.getString("stub_payment","0");
        stub_selectdate         = shardpref.getString("stub_selectdate","0000.00");
        stub_meal_pay           = shardpref.getString("stub_meal_pay","0");

        binding.selectMonth.setText(select_month + "월");
    }

    @Override
    public void onResume(){
        super.onResume();
        DataCheck();
        GetInsurancePercent();
        if(USER_INFO_AUTH.equals("0")){
            binding.resendBtn.setText("급여명세서 재발송");
        }else{
            binding.resendBtn.setText("목록으로");
        }
        binding.resendBtn.setOnClickListener(v -> {
            if(USER_INFO_AUTH.equals("0")){
                String message = "["+PlaceCheckData.getInstance().getPlace_name()+"] \n ["+stub_user_name+"] 님의 " + select_month + "월 급여명세서가 도착했습니다.";
                getUserToken(stub_user_id,"1",message);
            }else{
                if(USER_INFO_AUTH.equals("0")){
                    pm.PayManagement(mContext);
                }else{
                    pm.PayManagement2(mContext);
                }
            }
        });
    }

    private void DataCheck(){
        dlog.i("----------------AddPaystubActivityAlba DataCheck----------------");
        get_edit_expenses = get_edit_expenses.replace(",","");
        get_edit_overwork = get_edit_overwork.replace(",","");
        AllPayment = Integer.parseInt(String.valueOf(AllPayment).replace(",",""));
        dlog.i("edit_expenses : " + get_edit_expenses);
        dlog.i("edit_overwork : " + get_edit_overwork);
        dlog.i("edit_overworkhour : " + get_edit_overworkhour);
        dlog.i("select0102 : " + select0102);
        dlog.i("select0304 : " + select0304);
        dlog.i("ALL PAYMENT 0 : " + Integer.parseInt(stub_total_payment) +"+"+ Integer.parseInt(get_edit_expenses.replace(",","")) +"+"+ Integer.parseInt(get_edit_overwork.replace(",","")));
        dlog.i("insurance01p : " + insurance01p);
        dlog.i("insurance02p : " + insurance02p);
        dlog.i("insurance03p : " + insurance03p);
        dlog.i("insurance04p : " + insurance04p);
        dlog.i("insurance_01(국민연금) : " + AllPayment * (insurance01p/100));
        dlog.i("insurance_02(건강보험) : " + AllPayment * (insurance02p/100));
        dlog.i("insurance_03(고용보험) : " + AllPayment * (insurance03p/100));
        dlog.i("insurance_04(장기요양보험) : " + AllPayment * (insurance04p/100));
        dlog.i("----------------AddPaystubActivityAlba DataCheck----------------");
        getFCMToken();
    }


    @SuppressLint("LongLogTag")
    public void getFCMToken() {
        final String[] token = {null};
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w(TAG, "Fetching FCM registration token failed");
            }
            token[0] = task.getResult();
//            Log.d(TAG, "FCM Token is " + token[0]);
        });
    }

    public void getUserToken(String user_id, String type, String message) {
        dlog.i("-----getManagerToken-----");
        dlog.i("user_id : " + user_id);
        dlog.i("type : " + type);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FCMSelectInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FCMSelectInterface api = retrofit.create(FCMSelectInterface.class);
        Call<String> call = api.getData(user_id, type);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String jsonResponse = rc.getBase64decode(response.body());
                dlog.i("jsonResponse length : " + jsonResponse.length());
                dlog.i("jsonResponse : " + jsonResponse);
                try {
                    JSONArray Response = new JSONArray(jsonResponse);
                    if (Response.length() > 0) {
                        dlog.i("-----getManagerToken-----");
                        dlog.i("user_id : " + Response.getJSONObject(0).getString("user_id"));
                        dlog.i("token : " + Response.getJSONObject(0).getString("token"));
                        String id = Response.getJSONObject(0).getString("id");
                        String token = Response.getJSONObject(0).getString("token");
                        dlog.i("-----getManagerToken-----");
                        boolean channelId1 = Response.getJSONObject(0).getString("channel1").equals("1");
                        if (!token.isEmpty() && channelId1) {
                            PushFcmSend(id, "", message, token, "1", place_id);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러 = " + t.getMessage());
            }
        });
    }
    public void AddPush(String title, String content, String user_id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PushLogInputInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PushLogInputInterface api = retrofit.create(PushLogInputInterface.class);
        Call<String> call = api.getData(place_id, "", title, content, place_owner_id , user_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.i("AddStroeNoti Callback : " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            dlog.i("AddStroeNoti jsonResponse length : " + response.body().length());
                            dlog.i("AddStroeNoti jsonResponse : " + response.body());
                        }
                    });
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러1 = " + t.getMessage());
            }
        });
    }

    DBConnection dbConnection = new DBConnection();
    private void PushFcmSend(String topic, String title, String message, String token, String tag, String place_id) {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            click_action = "Payment0";
            dlog.i("-----PushFcmSend-----");
            dlog.i("topic : " + topic);
            dlog.i("title : " + title);
            dlog.i("message : " + message);
            dlog.i("token : " + token);
            dlog.i("click_action : " + click_action);
            dlog.i("tag : " + tag);
            dlog.i("place_id : " + place_id);
            dlog.i("-----PushFcmSend-----");
            dbConnection.FcmTestFunction(topic, title, message, token, click_action, tag, place_id);
            runOnUiThread(() -> {
                if(USER_INFO_AUTH.equals("0")){
                    pm.PayManagement(mContext);
                }else{
                    pm.PayManagement2(mContext);
                }
            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    /*4대보험 공제율(퍼센트)*/
    public void GetInsurancePercent() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(paymanaInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        paymanaInterface api = retrofit.create(paymanaInterface.class);
        Call<String> call = api.getData("4","", "", "","","","","","","","","","","");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.e("GetInsurancePercent function START");
                dlog.e("response 1: " + response.isSuccessful());
                dlog.e("response 2: " + rc.getBase64decode(response.body()));
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = rc.getBase64decode(response.body());
//                    Log.e(TAG, "GetWorkStateInfo function onSuccess : " + jsonResponse);
                        try {
                            JSONArray Response = new JSONArray(jsonResponse);

                            insurance01p = Float.parseFloat(Response.getJSONObject(0).getString("insurance01"));//국민연금 퍼센트
                            insurance02p = Float.parseFloat(Response.getJSONObject(0).getString("insurance02"));//건강보험 퍼센트
                            insurance03p = Float.parseFloat(Response.getJSONObject(0).getString("insurance03"));//고용보험 퍼센트
                            insurance04p = Float.parseFloat(Response.getJSONObject(0).getString("insurance04"));//장기요양보험료 퍼센트
                            dlog.i("--------GetInsurancePercent--------");
                            dlog.i("insurance01p : " + insurance01p);
                            dlog.i("insurance02p : " + insurance02p);
                            dlog.i("insurance03p : " + insurance03p);
                            dlog.i("insurance04p : " + insurance04p);
                            dlog.i("--------GetInsurancePercent--------");

                            int GetJigupPay = Integer.parseInt(stub_basic_pay) + Integer.parseInt(stub_second_pay) + Integer.parseInt(stub_overwork_pay) + Integer.parseInt(stub_meal_pay);
                            int GetGongJePay = 0;

                            DecimalFormat myFormatter = new DecimalFormat("###,###");
                            String GetJigupPay_tv = myFormatter.format(GetJigupPay);
                            String BasicPay = myFormatter.format(Integer.parseInt(stub_basic_pay));
                            String SecondPay = myFormatter.format(Integer.parseInt(stub_second_pay));
                            String MealPay = myFormatter.format(Integer.parseInt(stub_meal_pay));
                            String OverWorkPay = myFormatter.format(Integer.parseInt(stub_overwork_pay));

                            String total_payment = myFormatter.format(Integer.parseInt(stub_total_payment));
                            String gongjeynpay = myFormatter.format(Integer.parseInt(stub_gongjeynpay));

                            binding.name.setText(stub_user_name);
                            binding.paydata01.setText(BasicPay + "원");

                            binding.paydata02.setText(SecondPay + "원");
                            binding.paydata03.setText(MealPay + "원");
                            binding.paydata05.setText(OverWorkPay + "원");

                            AllPayment = Integer.parseInt(stub_total_payment) + Integer.parseInt(stub_second_pay) + Integer.parseInt(stub_overwork_pay) + Integer.parseInt(stub_meal_pay);
                            insurance1 = myFormatter.format(Math.round((AllPayment * insurance01p)/100));
                            insurance2 = myFormatter.format(Math.round((AllPayment * insurance02p)/100));
                            insurance3 = myFormatter.format(Math.round((AllPayment * insurance03p)/100));
                            insurance4 = myFormatter.format(Math.round((Math.round((AllPayment * insurance02p)/100) * insurance04p)/100));

                            int GetAllGongJe = Integer.parseInt(insurance1.replace(",",""))
                                    + Integer.parseInt(insurance2.replace(",",""))
                                    + Integer.parseInt(insurance3.replace(",",""))
                                    + Integer.parseInt(insurance4.replace(",",""));

                            dlog.i("---------공제내역---------");
                            dlog.i("AllPayment : " + AllPayment);
                            dlog.i("국민연금 공제율 : " + insurance01p);
                            dlog.i("건강보험 공제율 : " + insurance02p);
                            dlog.i("고용보험 공제율 : " + insurance03p);
                            dlog.i("장기요양보험 공제율 : " + insurance04p);
                            dlog.i("국민연금 : " + insurance1);
                            dlog.i("건강보험 : " + insurance2);
                            dlog.i("고용보험 : " + insurance3);
                            dlog.i("장기요양보험 : " + insurance4);
                            dlog.i("총 공제 내역 : " + GetAllGongJe);
                            dlog.i("---------공제내역---------");


                            binding.paydata06.setText(insurance1 + "원");
                            binding.paydata07.setText(insurance2 + "원");
                            binding.paydata08.setText(insurance3 + "원");
                            binding.paydata09.setText(insurance4 + "원");
                            binding.paydata10.setText(stub_total_workday + "일");
                            binding.paydata11.setText(stub_total_workhour + "시간");
                            binding.paydata12.setText(myFormatter.format(Integer.parseInt(stub_gongjeynpay)) + " 원");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }

            @Override
            @SuppressLint("LongLogTag")
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "에러2 = " + t.getMessage());
            }
        });
    }
}
