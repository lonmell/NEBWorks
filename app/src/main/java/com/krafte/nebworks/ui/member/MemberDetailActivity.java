package com.krafte.nebworks.ui.member;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.ApprovalAdapter;
import com.krafte.nebworks.adapter.MemberInoutAdapter;
import com.krafte.nebworks.data.WorkGotoListData;
import com.krafte.nebworks.dataInterface.AllMemberInterface;
import com.krafte.nebworks.dataInterface.WorkGotoListInterface;
import com.krafte.nebworks.databinding.ActivityMemberdetailBinding;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MemberDetailActivity extends AppCompatActivity {
    private static final String TAG = "MemberManagement";
    private ActivityMemberdetailBinding binding;
    Context mContext;
    private final DateCurrent dc = new DateCurrent();
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    // shared 저장값
    PreferenceHelper shardpref;
    ApprovalAdapter mAdapter = null;
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String USER_INFO_AUTH = "";
    String place_id = "";
    String place_owner_id = "";
    String item_user_id = "";

    //급여관리페이지에서 넘어올때
    String stub_place_id = "";
    String stub_user_id = "";
    String stub_user_account = "";
    String change_place_name = "";

    //직원관리페이지에서 넘어왔을때
    String mem_id = "";

    int SELECT_POSITION = 0;
    int SELECT_POSITION_sub = 0;
    String store_no;
    boolean wifi_certi_flag = false;
    boolean gps_certi_flag = false;


    Calendar cal;
    String format = "yyyy-MM-dd";
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    String toDay = "";
    String Year = "";
    String Month = "";
    String Day = "";
    String getYMPicker = "";
    String bYear = "";
    String bMonth = "";


    //Other
    /*라디오 버튼들 boolean*/
    Drawable icon_off;
    Drawable icon_on;
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    String return_page = "";

    ArrayList<WorkGotoListData.WorkGotoListData_list> inoutmList;
    MemberInoutAdapter inoutmAdapter;


    @SuppressLint({"UseCompatLoadingForDrawables", "SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_mainfragment);
        binding = ActivityMemberdetailBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);

        try {
            icon_off = mContext.getApplicationContext().getResources().getDrawable(R.drawable.menu_gray_bar);
            icon_on = mContext.getApplicationContext().getResources().getDrawable(R.drawable.menu_blue_bar);

            shardpref = new PreferenceHelper(mContext);
            stub_place_id = shardpref.getString("stub_place_id", "0");
            stub_user_id = shardpref.getString("stub_user_id", "0");
            stub_user_account = shardpref.getString("stub_user_account", "");
            change_place_name = shardpref.getString("change_place_name", "");

            place_id = shardpref.getString("place_id", "");
            place_owner_id = shardpref.getString("place_owner_id", "");
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
            SELECT_POSITION = shardpref.getInt("SELECT_POSITION", 0);
            SELECT_POSITION_sub = shardpref.getInt("SELECT_POSITION_sub", 0);
            wifi_certi_flag = shardpref.getBoolean("wifi_certi_flag", false);
            gps_certi_flag = shardpref.getBoolean("gps_certi_flag", false);
            return_page = shardpref.getString("return_page", "");
            store_no = shardpref.getString("store_no", "");
            item_user_id = shardpref.getString("item_user_id", "");

            setBtnEvent();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setBtnEvent() {
        cal = Calendar.getInstance();
        toDay = sdf.format(cal.getTime());
        dlog.i("오늘 :" + toDay);
        binding.setdate.setText(toDay);
        Year = toDay.substring(0, 4);
        Month = toDay.substring(5, 7);
        binding.setdate.setText(Year + "년 " + Month + "월");
        if (!stub_place_id.equals("0")) {
            SetGotoWorkDayList(stub_place_id, stub_user_id, Year + "-" + Month);
        } else {
            SetGotoWorkDayList(place_id, USER_INFO_ID, Year + "-" + Month);
        }

        binding.prevDate.setOnClickListener(v -> {
            cal.add(Calendar.DATE, -1);
            toDay = sdf.format(cal.getTime());
            Year = toDay.substring(0, 4);
            Month = toDay.substring(5, 7);
            binding.setdate.setText(Year + "년 " + Month + "월");
            if (!Year.equals(bYear) || !Month.equals(bMonth)) {
                dlog.i("Year : " + Year);
                dlog.i("Year : " + bYear);
                dlog.i("gMonth : " + Month);
                dlog.i("bMonth : " + bMonth);
                bYear = Year;
                bMonth = Month;
            }
            if (!stub_place_id.equals("0")) {
                SetGotoWorkDayList(stub_place_id, stub_user_id, Year + "-" + Month);
            } else {
                SetGotoWorkDayList(place_id, USER_INFO_ID, Year + "-" + Month);
            }
        });
        binding.nextDate.setOnClickListener(v -> {
            cal.add(Calendar.DATE, +1);
            toDay = sdf.format(cal.getTime());
            Year = toDay.substring(0, 4);
            Month = toDay.substring(5, 7);
            binding.setdate.setText(Year + "년 " + Month + "월");
            if (!Year.equals(bYear) || !Month.equals(bMonth)) {
                dlog.i("Year : " + Year);
                dlog.i("Year : " + bYear);
                dlog.i("gMonth : " + Month);
                dlog.i("bMonth : " + bMonth);
                bYear = Year;
                bMonth = Month;
            }
            if (!stub_place_id.equals("0")) {
                SetGotoWorkDayList(stub_place_id, stub_user_id, Year + "-" + Month);
            } else {
                SetGotoWorkDayList(place_id, USER_INFO_ID, Year + "-" + Month);
            }
        });

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Year = String.valueOf(year);
                Month = String.valueOf(month + 1);
                Day = String.valueOf(dayOfMonth);
                Day = Day.length() == 1 ? "0" + Day : Day;
                Month = Month.length() == 1 ? "0" + Month : Month;
                binding.setdate.setText(Year + "년 " + Month + "월");
                if (!stub_place_id.equals("0")) {
                    SetGotoWorkDayList(stub_place_id, stub_user_id, Year + "-" + Month);
                } else {
                    SetGotoWorkDayList(place_id, USER_INFO_ID, Year + "-" + Month);
                }
            }
        }, mYear, mMonth, mDay);

        binding.setdate.setOnClickListener(view -> {
            if (binding.setdate.isClickable()) {
                datePickerDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        pm.MemberManagement(mContext);
    }

    @Override
    public void onResume() {
        super.onResume();

        SetAllMemberList(stub_place_id,stub_user_id);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shardpref.remove("stub_place_id");
        shardpref.remove("stub_user_id");
        shardpref.remove("stub_user_account");
    }

    /*직원 전체 리스트 START*/
    RetrofitConnect rc = new RetrofitConnect();
    public void SetAllMemberList(String place_id, String user_id) {
        @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AllMemberInterface.URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            AllMemberInterface api = retrofit.create(AllMemberInterface.class);
            Call<String> call = api.getData(place_id, user_id);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = rc.getBase64decode(response.body());
                        dlog.i("jsonResponse length : " + jsonResponse.length());
                        dlog.i("jsonResponse : " + jsonResponse);
                        Log.e("onSuccess : ", response.body());
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(jsonResponse);
                            String name = Response.getJSONObject(0).getString("name");
                            String place_name = Response.getJSONObject(0).getString("place_name");
                            String join_date = Response.getJSONObject(0).getString("join_date");
                            binding.name.setText(name);
                            binding.placeNametv.setText(place_name);
                            binding.joinDatetv.setText(join_date);
                            String inoutstate = Response.getJSONObject(0).getString("inoutstate");
                            if(inoutstate.equals("-1")){
                                binding.workState.setText("미출근");
                            }else if(inoutstate.equals("0")){
                                binding.workState.setText("출근");
                            }else if(inoutstate.equals("1")){
                                binding.workState.setText("퇴근");
                            }
                            binding.workPay.setText(Response.getJSONObject(0).getString("pay").equals("null")?"미정":Response.getJSONObject(0).getString("pay"));
                            binding.userPhone.setText(Response.getJSONObject(0).getString("phone").equals("null")?"미입력":Response.getJSONObject(0).getString("phone"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    dlog.e("에러 = " + t.getMessage());
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
    /*직원 전체 리스트 END*/

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
                            binding.noDataTxt.setVisibility(View.VISIBLE);
                        } else {
                            binding.noDataTxt.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null) {
                                try {
                                    JSONArray Response = new JSONArray(response.body());
                                    inoutmList = new ArrayList<>();
                                    inoutmAdapter = new MemberInoutAdapter(mContext, inoutmList, Month);
                                    binding.inoutList.setAdapter(inoutmAdapter);
                                    binding.inoutList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                                    Log.i(TAG, "SetNoticeListview Thread run! ");

                                    if (Response.length() == 0) {
                                        Log.i(TAG, "(gotoWorkData_list)GET SIZE : " + Response.length());
                                    } else {
                                        for (int i = 0; i < Response.length(); i++) {
                                            JSONObject jsonObject = Response.getJSONObject(i);
                                            inoutmAdapter.addItem(new WorkGotoListData.WorkGotoListData_list(
                                                    jsonObject.getString("day"),
                                                    jsonObject.getString("day_off"),
                                                    jsonObject.getString("in_time"),
                                                    jsonObject.getString("out_time"),
                                                    jsonObject.getString("late_time"),
                                                    jsonObject.getString("working_time")
                                            ));
                                        }
                                        inoutmAdapter.notifyDataSetChanged();

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

}
