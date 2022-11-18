package com.krafte.nebworks.ui.paymanagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;
import com.krafte.nebworks.R;
import com.krafte.nebworks.dataInterface.paymanaInterface;
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
    Context mContext;

    private final DateCurrent dc = new DateCurrent();

    //XML ID
    TextView store_name;
    TextView jigup_pay,input_date,jigup_total;
    TextView paydata01,paydata02,paydata03,paydata04,paydata05,paydata06,paydata07,paydata08,paydata09,paydata10,paydata11,paydata12;
    TextView gongje_total;

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String USER_INFO_AUTH = "";
    String store_no;

    String stub_store_name = "";
    String stub_store_no = "";
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
    String stub_total_workhour = "";
    String stub_payment = "";
    String stub_selectdate = "";
    String stub_meal_pay = "";

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
    RetrofitConnect rc = new RetrofitConnect();

    String get_edit_expenses = "0";
    String get_edit_overwork = "0";
    String get_edit_overworkhour = "0";

    String select0102 = "직접입력";
    String select0304 = "포함";

    String result = "";
    final DecimalFormat decimalFormat = new DecimalFormat("#,###");
    String sendTopic = "";
    String sendToken = "";
    boolean rcvchannelId2 = false;
    Handler mHandler;
    DBConnection dbConnection = new DBConnection();
    String click_action = "";
    String message = "";
    String topic = "";
    int AllPayment = 0;

    @SuppressLint({"UseCompatLoadingForDrawables", "SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paystuball);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);

        icon_off = mContext.getApplicationContext().getResources().getDrawable(R.drawable.resize_service_off);
        icon_on = mContext.getApplicationContext().getResources().getDrawable(R.drawable.resize_service_on);

        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
        USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
        store_no = shardpref.getString("store_no", "");

        //-------------------
        stub_store_name = shardpref.getString("store_name","");
        stub_store_no = shardpref.getString("stub_store_no","0");
        stub_user_id = shardpref.getString("stub_user_id","0");
        stub_user_name = shardpref.getString("stub_user_name","0");
        stub_jikgup = shardpref.getString("stub_jikgup","0");
        stub_basic_pay = shardpref.getString("stub_basic_pay","0");
        stub_second_pay = shardpref.getString("stub_second_pay","0");
        stub_overwork_pay = shardpref.getString("stub_overwork_pay","0");
        stub_meal_allowance_yn = shardpref.getString("stub_meal_allowance_yn","0");
        stub_store_insurance_yn = shardpref.getString("stub_store_insurance_yn","0");
        stub_gongjeynpay = shardpref.getString("stub_gongjeynpay","0");
        stub_total_payment = shardpref.getString("stub_total_payment","0");
        stub_workday = shardpref.getString("stub_workday","0");
        stub_total_workhour = shardpref.getString("stub_total_workhour","0");
        stub_payment = shardpref.getString("stub_payment","0");
        stub_selectdate = shardpref.getString("stub_selectdate","0000.00");
        stub_meal_pay = shardpref.getString("stub_meal_pay","0");

        setContentsLayout();
        setBtnEvent();

    }

    @Override
    public void onResume(){
        super.onResume();
        GetInsurancePercent();
    }

    private void setContentsLayout() {
        store_name = findViewById(R.id.store_name);
        jigup_pay = findViewById(R.id.jigup_pay);
        input_date = findViewById(R.id.input_date);
        jigup_total = findViewById(R.id.jigup_total);
        paydata01 = findViewById(R.id.paydata01);
        paydata02 = findViewById(R.id.paydata02);
        paydata03 = findViewById(R.id.paydata03);
        paydata04 = findViewById(R.id.paydata04);
        paydata05 = findViewById(R.id.paydata05);
        paydata06 = findViewById(R.id.paydata06);
        paydata07 = findViewById(R.id.paydata07);
        paydata08 = findViewById(R.id.paydata08);
        paydata09 = findViewById(R.id.paydata09);
        paydata10 = findViewById(R.id.paydata10);
        paydata11 = findViewById(R.id.paydata11);
        paydata12 = findViewById(R.id.paydata12);
        gongje_total = findViewById(R.id.gongje_total);
    }

    private void setBtnEvent() {
//        menu.setOnClickListener(v -> {
//            pm.PayManager(mContext);
//        });


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
        getEmployeroken(stub_user_id);
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


    @SuppressLint("LongLogTag")
    private void getEmployeroken(String USER_ID) {
//        mHandler = new Handler(Looper.getMainLooper());
//        mHandler.postDelayed(() -> {
//            Thread th = new Thread(() -> {
//                dbConnection.UserTokenUpdate("2", USER_ID, "2", "", "", "", "", "");
//                runOnUiThread(() -> {
//                    Log.i(TAG, "user_id : " + dbConnection.tokenData_lists.get(0).getUser_id());
//                    Log.i(TAG, "token : " + dbConnection.tokenData_lists.get(0).getToken());
//                    sendTopic =  dbConnection.tokenData_lists.get(0).getUser_id();
//                    sendToken = dbConnection.tokenData_lists.get(0).getToken();
//                    rcvchannelId2 = dbConnection.tokenData_lists.get(0).getChannelId2().equals("1");
//                    topic = USER_ID;
//                    FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
//                        dlog.i("rcvchannelId2 : " + rcvchannelId2);
//                        if (rcvchannelId2) {
//                            FcmTestFunctionCall(sendToken);
//                        }else{
//                            pm.PayManager(mContext);
//                        }
//                    });
//
//
//                });
//            });
//            th.start();
//            try {
//                th.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }, 0);
    }

    private void FcmTestFunctionCall(String token_get) {
//        @SuppressLint("SetTextI18n")
//        Thread th = new Thread(() -> {
//            click_action = "EmployeeMainActivity";
//            message = stub_selectdate + " 급여명세서가 재발송되었습니다.";
//            dbConnection.FcmTestFunction(topic, message, token_get, click_action, "1", store_no);
//            runOnUiThread(() -> {
//            });
//        });
//        th.start();
//        try {
//            th.join();
//            pm.PayManager(mContext);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
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

                            store_name.setText(stub_store_name);
                            jigup_pay.setText(gongjeynpay);
                            input_date.setText(stub_selectdate);
                            jigup_total.setText(GetJigupPay_tv);
                            paydata01.setText(BasicPay);

                            if(stub_jikgup.equals("알바")){
                                paydata02.setText("0원");
                                paydata04.setText(SecondPay);
                            }else{
                                paydata02.setText(SecondPay);
                                paydata04.setText("0원");
                            }
                            paydata03.setText(MealPay);
                            paydata05.setText(OverWorkPay);

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


                            gongje_total.setText(String.valueOf(myFormatter.format(GetAllGongJe)));
                            paydata06.setText(insurance1);
                            paydata07.setText(insurance2);
                            paydata08.setText(insurance3);
                            paydata09.setText(insurance4);
                            paydata10.setText(stub_workday + "일");
                            paydata11.setText(stub_total_workhour + "시간");
                            paydata12.setText(myFormatter.format(Integer.parseInt(stub_gongjeynpay)) + " 원");

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
