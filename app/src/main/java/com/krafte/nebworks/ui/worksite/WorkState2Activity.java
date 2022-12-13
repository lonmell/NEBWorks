package com.krafte.nebworks.ui.worksite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.WorkgotoListAdapter;
import com.krafte.nebworks.data.WorkGotoListData;
import com.krafte.nebworks.dataInterface.GetWorkCntInterface;
import com.krafte.nebworks.dataInterface.WorkGotoListInterface;
import com.krafte.nebworks.pop.DatePickerYearActivity;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class WorkState2Activity extends AppCompatActivity {
    private final static String TAG = "EmployerWorkGotoList";
    Context mContext;

    //XML ID
    TextView user_name, user_in_date, detail_status, detail_status2;
    TextView detail_time01, payment_txt;
    TextView notend_work, end_work;
    TextView approval1_txt, approval2_txt, approval3_txt;
    TextView search_weekMonth;
    TextView selectdate;

    ImageView user_img;
    LinearLayout minus_week, plus_week;
    LinearLayout detail_settingwork;
    RecyclerView weekend_gotolist;
    ProgressBar detail_time02;

    CardView member_call, member_contract, member_setting,placework_go;
    RelativeLayout member_contract_box;
    TextView contract_txt, no_data_txt;
    ImageView arrow03;
    LinearLayout approval1_go,approval2_go,approval3_go;

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String USER_INFO_AUTH = "";
    String place_id;
    String place_name;
    String userthumnail = "";
    String USER_INFO_EMAIL = "";
    String employee_joindate = "";

    String search_id = "";
    String search_name = "";
    String search_kind = "";
    String search_img_path = "";
    String search_department = "";
    String search_position = "";
    String search_commute = "";

    int SELECTED_POSITION = 0;
    boolean wifi_certi_flag = false;
    boolean gps_certi_flag = false;

    //Other
    DateCurrent dateCurrent = new DateCurrent();
    ArrayList<WorkGotoListData.WorkGotoListData_list> mList;
    WorkgotoListAdapter mAdapter;
    RetrofitConnect rc = new RetrofitConnect();
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    DateCurrent dc = new DateCurrent();

    int topNum1 = 0;
    int topNum2 = 0;
    int topNum3 = 0;
    int topNum4 = 0;

    float total_pay = 0;

    String[] setMonth = new String[12];
    //월,주차 선택시 변경되는 전역변수
    int montharray = 0;

    //해당 월의 주차 값(EX.1주차,2주차)
    int weekNum = 0;
    int WeekLast = 0;
    String picker_year = "";
    String contract_cnt = "0";
    String week_numlastday = "0";

    public void setContentLayout() {
        //main xml
        user_name = findViewById(R.id.user_name);
        user_in_date = findViewById(R.id.user_in_date);
        detail_status = findViewById(R.id.detail_status);
        detail_status2 = findViewById(R.id.detail_status2);
        user_img = findViewById(R.id.user_img);
        weekend_gotolist = findViewById(R.id.weekend_gotolist);
        detail_time01 = findViewById(R.id.detail_time01);
        payment_txt = findViewById(R.id.payment_txt);
        detail_time02 = findViewById(R.id.detail_time02);
        notend_work = findViewById(R.id.notend_work);
        end_work = findViewById(R.id.end_work);
        no_data_txt = findViewById(R.id.no_data_txt);
        detail_settingwork = findViewById(R.id.detail_settingwork);
        approval1_txt = findViewById(R.id.approval1_txt);
        approval2_txt = findViewById(R.id.approval2_txt);
        approval3_txt = findViewById(R.id.approval3_txt);
        member_setting = findViewById(R.id.member_setting);

        search_weekMonth = findViewById(R.id.search_weekMonth);
        minus_week = findViewById(R.id.minus_week);
        plus_week = findViewById(R.id.plus_week);

        member_call = findViewById(R.id.member_call);
        placework_go = findViewById(R.id.placework_go);
        approval1_go = findViewById(R.id.approval1_go);
        approval2_go = findViewById(R.id.approval2_go);
        approval3_go = findViewById(R.id.approval3_go);

        //-- 근로계약서 표시 부분
        member_contract = findViewById(R.id.member_contract);
        contract_txt = findViewById(R.id.contract_txt);
        arrow03 = findViewById(R.id.arrow03);
        member_contract_box = findViewById(R.id.member_contract_box);
        selectdate = findViewById(R.id.selectdate);
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gotowork_detail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        setContentLayout();
        setMonthString();
        setBtnEvent();

        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
        USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
        wifi_certi_flag = shardpref.getBoolean("wifi_certi_flag", false);
        gps_certi_flag = shardpref.getBoolean("gps_certi_flag", false);
        place_id = shardpref.getString("place_id", "");
        search_id = shardpref.getString("search_id", "");

        search_name = shardpref.getString("search_name", "");
        search_kind = shardpref.getString("search_kind", "");
        search_img_path = shardpref.getString("search_img_path", "");
        search_department = shardpref.getString("search_department", "");
        search_position = shardpref.getString("search_position", "");
        search_commute = shardpref.getString("search_commute", "");
        SELECTED_POSITION = shardpref.getInt("SELECTED_POSITION", 0);

        shardpref.putInt("SELECT_POSITION", 3);

        user_name.setText(search_name);
        user_in_date.setText(search_department + " " + search_position);

        Glide.with(mContext).load(search_img_path)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(1f)
                .placeholder(R.drawable.certi01)
                .into(user_img);

        detail_status.setText((search_kind.equals("0") ? "정직원" : "협력업체"));
        detail_status2.setText(search_commute.equals("0") ? "작업중" : "퇴근");


        picker_year = shardpref.getString("picker_year", dc.GET_YEAR);
        selectdate.setText("  " + picker_year);

        Calendar calendar = Calendar.getInstance();
        System.out.println("현재 주 : " + calendar.get(Calendar.WEEK_OF_MONTH));        // calendar 현재 주를 구한다.
        System.out.println("마지막 날 : " + calendar.getActualMaximum(Calendar.DATE));    // 마지막 날을 구한다.
        calendar.set(Calendar.YEAR, Calendar.MONTH + 1, calendar.getActualMaximum(Calendar.DATE));    // 마지막 날짜를 넣어서 셋팅해준다.
        System.out.println("마지막 주 : " + calendar.get(Calendar.WEEK_OF_MONTH));    // calendar 현재 주를 구한다.
        week_numlastday = String.valueOf(calendar.get(Calendar.WEEK_OF_MONTH));

        montharray = Integer.parseInt(getMonthString(Integer.parseInt(dc.GET_MONTH) - 1));
        search_weekMonth.setText(getMonthString(Integer.parseInt(dc.GET_MONTH) - 1) + "월 ");
        dlog.i("onCreate Set YMDate : " + (picker_year + "-" + (dc.GET_MONTH.length() == 1 ? "0" + dc.GET_MONTH : dc.GET_MONTH)));
        dlog.i("onCreate dc.GET_MONTH : " + dc.GET_MONTH);
        dlog.i("onCreate getMonthString : " + getMonthString(Integer.parseInt(dc.GET_MONTH) - 1));
        dlog.i("onCreate montharray : " + montharray);
    }


    @SuppressLint("SetTextI18n")
    private void setBtnEvent() {
        placework_go.setOnClickListener(v -> {
            pm.PlaceWorkGo(mContext);
            shardpref.putString("return_page","WorkState2Activity");
            shardpref.putInt("SELECT_POSITION",1);
            shardpref.putInt("SELECT_POSITION_sub",1);
        });
        approval1_go.setOnClickListener(v -> {
            pm.Approval(mContext);
            shardpref.putString("return_page","WorkState2Activity");
            shardpref.putInt("SELECT_POSITION",0);
        });
        approval2_go.setOnClickListener(v -> {
            pm.Approval(mContext);
            shardpref.putString("return_page","WorkState2Activity");
            shardpref.putInt("SELECT_POSITION",1);
        });
        approval3_go.setOnClickListener(v -> {
            pm.Approval(mContext);
            shardpref.putString("return_page","WorkState2Activity");
            shardpref.putInt("SELECT_POSITION",2);
        });

        minus_week.setOnClickListener(v -> {
            if (montharray == 1) {
                montharray = 13;
                montharray--;
            } else {
                montharray--;
            }
            dlog.i("montharray : " + montharray);
            SetGotoWorkDayList(place_id, search_id, picker_year + "-" + getMonthString(montharray - 1));
            search_weekMonth.setText(montharray + "월");
        });
        plus_week.setOnClickListener(v -> {
            if (montharray == 12) {
                montharray = 0;
                montharray++;
            } else {
                montharray++;
            }
            dlog.i("montharray : " + montharray);
            SetGotoWorkDayList(place_id, search_id, picker_year + "-" + getMonthString(montharray - 1));
            search_weekMonth.setText(montharray + "월");
        });

        member_call.setOnClickListener(v -> {
            //타 플랫폼 간편 로그인시 전화번호를 따로 입력하도록 해야함
            String mNum = search_id;
            String tel = "tel:" + mNum;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tel));
            startActivity(intent);
        });
        member_contract.setOnClickListener(v -> {
//            if (contract_cnt.equals("0")) {
////                pm.ContractActivity01(mContext);
//                pm.ContractReady01(mContext);
//            } else {
//                shardpref.putString("search_id", search_id);
//                shardpref.putString("contract_cnt", contract_cnt);
//                pm.ContractActivity(mContext);
//            }
            LockTost();
        });

        member_setting.setOnClickListener(v -> {
//            shardpref.putString("employee_thumnail", userthumnail);
//            pm.WorkUserTimeSetting(mContext);
            LockTost();
        });

        selectdate.setOnClickListener(v -> {
            Intent intent = new Intent(this, DatePickerYearActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
        });
    }

    private void LockTost() {
        Toast.makeText(mContext, "잠겨있는 기능입니다.", Toast.LENGTH_SHORT).show();
    }

    private void setMonthString() {
        setMonth[0] = "01";
        setMonth[1] = "02";
        setMonth[2] = "03";
        setMonth[3] = "04";
        setMonth[4] = "05";
        setMonth[5] = "06";
        setMonth[6] = "07";
        setMonth[7] = "08";
        setMonth[8] = "09";
        setMonth[9] = "10";
        setMonth[10] = "11";
        setMonth[11] = "12";
    }

    private String getMonthString(int i) {
        return setMonth[i];
    }

    public void btnOnclick(View view) {
        if (view.getId() == R.id.out_store) {
            pm.PlaceList(mContext);
        } else if (view.getId() == R.id.bottom_navigation01) {
            if(USER_INFO_AUTH.equals("0")){
                pm.Main(mContext);
            }else{
                pm.Main2(mContext);
            }
        } else if (view.getId() == R.id.bottom_navigation02) {
            pm.PlaceWorkGo(mContext);
        } else if (view.getId() == R.id.bottom_navigation03) {
            pm.CalenderGo(mContext);
        } else if (view.getId() == R.id.bottom_navigation05) {
            pm.MoreGo(mContext);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();

        DateCurrent dc = new DateCurrent();

        picker_year = shardpref.getString("picker_year", dc.GET_YEAR);
        selectdate.setText("  " + picker_year);

        topNum1 = 0;
        topNum2 = 0;
        topNum3 = 0;
        topNum4 = 0;

        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
        SELECTED_POSITION = shardpref.getInt("SELECTED_POSITION", 0);
        place_id = shardpref.getString("place_id", "");
        place_name = shardpref.getString("place_name", "");
        search_id = shardpref.getString("search_id", "");
        userthumnail = shardpref.getString("employee_thumnail", "");
        USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "");
        employee_joindate = shardpref.getString("employee_joindate", "");


        getWorkCnt(place_id, search_id);
        SetGotoWorkDayList(place_id, search_id, picker_year + "-" + getMonthString(Integer.parseInt(dateCurrent.GET_MONTH) - 1));
        getContractList(place_id, search_id);
    }

    /*근로계악서 여부 */
    public void getContractList(String place_id, String user_id) {
//        @SuppressLint({"NotifyDataSetChanged", "LongLogTag", "SetTextI18n"}) Thread th = new Thread(() -> {
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl(WorkContractListInterface.URL)
//                    .addConverterFactory(ScalarsConverterFactory.create())
//                    .build();
//            WorkContractListInterface api = retrofit.create(WorkContractListInterface.class);
//            Call<String> call = api.getData(place_id, user_id);
//            call.enqueue(new Callback<String>() {
//                @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
//                @Override
//                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//                    dlog.e("getContractList function START");
//                    dlog.e("response 1: " + response.isSuccessful());
//                    dlog.e("response 2: " + rc.getBase64decode(response.body()));
//                    runOnUiThread(() -> {
//                        if (response.isSuccessful() && response.body() != null) {
//                            String jsonResponse = rc.getBase64decode(response.body());
//                            try {
//                                JSONArray Response = new JSONArray(jsonResponse);
//                                dlog.e("ContractDate : " + (Response.getJSONObject(0).getString("ContractDate").length() == 0?"":Response.getJSONObject(0).getString("ContractDate")));
//                                if (Response.getJSONObject(0).getString("ContractDate").equals("null")) {
//                                    contract_cnt = "0";
//                                    member_contract.setCardBackgroundColor(Color.parseColor("#696969"));
//                                    member_contract_box.setBackgroundResource(R.drawable.member_contract);
//                                    contract_txt.setTextColor(Color.parseColor("#696969"));
//                                    arrow03.setBackgroundResource(R.drawable.detail_white_gray);
//                                } else {
//                                    contract_cnt = "1";
//                                    member_contract.setCardBackgroundColor(Color.parseColor("#6395EC"));
//                                    member_contract_box.setBackgroundResource(R.drawable.member_contract_on);
//                                    contract_txt.setTextColor(Color.parseColor("#6395EC"));
//                                    arrow03.setBackgroundResource(R.drawable.detail_white_blue);
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                }
//
//                @Override
//                @SuppressLint("LongLogTag")
//                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                    dlog.e("에러2 = " + t.getMessage());
//                }
//            });
//        });
//        th.start();
//        try {
//            th.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }



    public void getWorkCnt(String place_id, String user_id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GetWorkCntInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        GetWorkCntInterface api = retrofit.create(GetWorkCntInterface.class);
        Call<String> call = api.getData(place_id, user_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
//                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("UserCheck jsonResponse length : " + response.body().length());
                            dlog.i("UserCheck jsonResponse : " + response.body());
                            try {
                                if (!response.body().equals("[]")) {
                                    JSONArray Response = new JSONArray(response.body());
                                    String task_total_cnt = Response.getJSONObject(0).getString("task_total_cnt");
                                    String task_complete_cnt = Response.getJSONObject(0).getString("task_complete_cnt");
                                    String task_incomplete_cnt = Response.getJSONObject(0).getString("task_incomplete_cnt");
                                    String approval_total_cnt = Response.getJSONObject(0).getString("approval_total_cnt"); //-- 가입할때의 게정
                                    String waiting_cnt = Response.getJSONObject(0).getString("waiting_cnt"); //-- 사번
                                    String approval_cnt = Response.getJSONObject(0).getString("approval_cnt");
                                    String reject_cnt = Response.getJSONObject(0).getString("reject_cnt");

                                    try {
                                        dlog.i("------getWorkCnt-------");
                                        dlog.i("할일 전체 : " + task_total_cnt);
                                        dlog.i("완료된 업무 : " + task_complete_cnt);
                                        dlog.i("미완료 업무 : " + task_incomplete_cnt);
                                        dlog.i("결재 전체 : " + approval_total_cnt);
                                        dlog.i("결재 대기 : " + waiting_cnt);
                                        dlog.i("결재 승인 : " + approval_cnt);
                                        dlog.i("결재 반려 : " + reject_cnt);
                                        dlog.i("------getWorkCnt-------");

                                        notend_work.setText(task_incomplete_cnt);
                                        end_work.setText(task_complete_cnt);
                                        approval1_txt.setText(waiting_cnt);
                                        approval2_txt.setText(approval_cnt);
                                        approval3_txt.setText(reject_cnt);
                                    } catch (Exception e) {
                                        dlog.i("UserCheck Exception : " + e);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

    /*출퇴근 리스트 START*/
    public void SetGotoWorkDayList(String place_id, String user_id, String getYMdate) {
        dlog.i("-----SetGotoWorkDayList-----");
        dlog.i("place_id : " + place_id);
        dlog.i("user_id : " + user_id);
        dlog.i("getYMdate : " + getYMdate);
        dlog.i("-----SetGotoWorkDayList-----");
        @SuppressLint({"NotifyDataSetChanged", "LongLogTag", "SetTextI18n"}) Thread th = new Thread(() -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(WorkGotoListInterface.URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            WorkGotoListInterface api = retrofit.create(WorkGotoListInterface.class);
            Call<String> call = api.getData(place_id, user_id, getYMdate);
            call.enqueue(new Callback<String>() {
                @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    dlog.e("SetGotoWorkDayList function START");
                    dlog.e("response 1: " + response.isSuccessful());
                    dlog.e("response 2: " + response.body());
                    runOnUiThread(() -> {
                        if (response.body().equals("[]")) {
                            no_data_txt.setVisibility(View.VISIBLE);
                        } else {
                            no_data_txt.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null) {
                                try {
                                    JSONArray Response = new JSONArray(response.body());
                                    mList = new ArrayList<>();
                                    mAdapter = new WorkgotoListAdapter(mContext, mList);
                                    weekend_gotolist.setAdapter(mAdapter);
                                    weekend_gotolist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                                    Log.i(TAG, "SetNoticeListview Thread run! ");

                                    if (Response.length() == 0) {
                                        Log.i(TAG, "(gotoWorkData_list)GET SIZE : " + Response.length());
                                    } else {
                                        for (int i = 0; i < Response.length(); i++) {
                                            JSONObject jsonObject = Response.getJSONObject(i);
                                            if (!jsonObject.getString("in_time").equals("null")) {
                                                mAdapter.addItem(new WorkGotoListData.WorkGotoListData_list(
                                                        jsonObject.getString("day"),
                                                        jsonObject.getString("yoil"),
                                                        jsonObject.getString("in_time"),
                                                        jsonObject.getString("out_time"),
                                                        jsonObject.getString("workdiff"),
                                                        jsonObject.getString("state"),
                                                        jsonObject.getString("sieob1"),
                                                        jsonObject.getString("sieob2"),
                                                        jsonObject.getString("jongeob1"),
                                                        jsonObject.getString("jongeob2")
                                                ));
                                            }
                                        }
                                        mAdapter.notifyDataSetChanged();
                                        WeekLast = Integer.parseInt(week_numlastday);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                }

                @Override
                @SuppressLint("LongLogTag")
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    dlog.e("에러2 = " + t.getMessage());
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

    @Override
    public void onBackPressed() {
        shardpref.putInt("SELECTED_POSITION",3);
        pm.WorkStateListBack(mContext);
        shardpref.remove("search_id");
    }

    //    //-------몰입화면 설정
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            hideSystemUI();
//        }
//    }
//
//    private void hideSystemUI() {
//        // Enables regular immersive mode.
//        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
//        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                        // Set the content to appear under the system bars so that the
//                        // content doesn't resize when the system bars hide and show.
//                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        // Hide the nav bar and status bar
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
//    }
//    //-------몰입화면 설정
}
