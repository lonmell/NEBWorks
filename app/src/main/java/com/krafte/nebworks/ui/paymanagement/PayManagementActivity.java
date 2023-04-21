package com.krafte.nebworks.ui.paymanagement;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.FragmentStateAdapter;
import com.krafte.nebworks.adapter.PayCalenderAdapter;
import com.krafte.nebworks.adapter.PaymentMemberAdapter;
import com.krafte.nebworks.bottomsheet.PaySelectMemberActivity;
import com.krafte.nebworks.bottomsheet.PaySelectPlaceActivity;
import com.krafte.nebworks.data.PaymentData;
import com.krafte.nebworks.data.WorkCalenderData;
import com.krafte.nebworks.data.WorkGetallData;
import com.krafte.nebworks.dataInterface.PayGetallInterface;
import com.krafte.nebworks.dataInterface.paymanaInterface;
import com.krafte.nebworks.databinding.ActivityPaymanagementBinding;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
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
/*
* memo : 2023-02-09 / 방창배 작성 / 이 페이지는 점주, 관리자 전용 급여관리 페이지로 지정, 근로자는 PayManagementActivity2로 이동하는것으로 수정
* */
public class PayManagementActivity extends AppCompatActivity {
    private static final String TAG = "PayManagementActivity";
    private ActivityPaymanagementBinding binding;
    Context mContext;

    ArrayList<PaymentData.PaymentData_list> mList = new ArrayList<>();
    PaymentMemberAdapter mAdapter = null;

    PayCalenderAdapter mAdapter2;
    ArrayList<WorkCalenderData.WorkCalenderData_list> mList2 = new ArrayList<>();

    ArrayList<WorkGetallData.WorkGetallData_list> mList3 = new ArrayList<>();

    FragmentStateAdapter fragmentStateAdapter;
    PayManagementActivity thisActivity = this;

    RetrofitConnect rc = new RetrofitConnect();
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String USER_INFO_AUTH = "";
    String place_id = "";
    String place_owner_id = "";

    String change_place_id = "";
    String change_place_name = "";
    String change_member_id = "";
    String change_member_name = "";

    int SELECT_POSITION = 0;
    int SELECT_POSITION_sub = 0;
    boolean wifi_certi_flag = false;
    boolean gps_certi_flag = false;

    //Other
    /*라디오 버튼들 boolean*/
    Drawable icon_off;
    Drawable icon_on;
    String Tap = "0";
    boolean chng_icon = false;

    @SuppressLint({"UseCompatLoadingForDrawables", "SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_mainfragment);
        binding = ActivityPaymanagementBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        try {
            icon_off = mContext.getApplicationContext().getResources().getDrawable(R.drawable.menu_gray_bar);
            icon_on = mContext.getApplicationContext().getResources().getDrawable(R.drawable.menu_blue_bar);

            //Singleton Area
            place_id        = shardpref.getString("place_id","");
            place_owner_id  = shardpref.getString("place_owner_id","");
            USER_INFO_ID    = shardpref.getString("USER_INFO_ID","");
            USER_INFO_NAME  = shardpref.getString("USER_INFO_NAME","");
            USER_INFO_AUTH  = shardpref.getString("USER_INFO_AUTH", "");
            change_place_id = shardpref.getString("change_place_id", shardpref.getString("place_id",""));

            //shardpref Area
            SELECT_POSITION = shardpref.getInt("SELECT_POSITION", 0);
            SELECT_POSITION_sub = shardpref.getInt("SELECT_POSITION_sub", 0);
            wifi_certi_flag = shardpref.getBoolean("wifi_certi_flag", false);
            gps_certi_flag = shardpref.getBoolean("gps_certi_flag", false);
            Tap = shardpref.getString("Tap", "0");

            fragmentStateAdapter = new FragmentStateAdapter(this, 4,mList3);
//            calenderFragment.CalenderContext(mContext);
            binding.calenderViewpager.setAdapter(fragmentStateAdapter);
            binding.calenderViewpager.setCurrentItem(fragmentStateAdapter.returnPosition(), false);
            binding.calenderViewpager.setOffscreenPageLimit(1);

            binding.backBtn.setOnClickListener(v -> {
                super.onBackPressed();
            });

            binding.select01.setOnClickListener(v -> {
                shardpref.putString("Tap", "0");
                binding.line01.setBackgroundColor(Color.parseColor("#6395EC"));
                binding.line02.setBackgroundColor(Color.parseColor("#ffffff"));
                WritePaymentList(change_place_id.equals("") ? place_id : change_place_id, change_member_id, setDate , "0");
            });
            binding.select02.setOnClickListener(v -> {
                shardpref.putString("Tap", "1");
                binding.line01.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.line02.setBackgroundColor(Color.parseColor("#6395EC"));
                WritePaymentList(change_place_id.equals("") ? place_id : change_place_id, change_member_id, setDate , "1");
            });

            Log.i(TAG, "USER_INFO_AUTH : " + USER_INFO_AUTH);
            if(USER_INFO_AUTH.equals("1")){
                binding.tabLayout.setVisibility(View.GONE);
                binding.selectArea.setVisibility(View.GONE);
                change_place_id = place_id;
                change_member_id = USER_INFO_ID;
            }
            binding.changeIcon.setOnClickListener(v -> {
                if (!chng_icon) {
                    chng_icon = true;
                    binding.tabLayout.setVisibility(View.GONE);
                    binding.changeIcon.setBackgroundResource(R.drawable.list_up_icon);
                    binding.setdate.setText(Year + "년 " + Month + "월 ");
                    binding.dateLayout.setVisibility(View.VISIBLE);
                    binding.dateSelect.setVisibility(View.GONE);
                    binding.allMemberlist.setVisibility(View.GONE);
                    binding.calendarArea.setVisibility(View.VISIBLE);
//                    SetCalenderData(Year, Month);
                } else {
                    chng_icon = false;
                    binding.tabLayout.setVisibility(View.VISIBLE);
                    binding.changeIcon.setBackgroundResource(R.drawable.calendar_resize);
                    binding.setdate.setText(Year + "년 " + Month + "월");
                    binding.dateLayout.setVisibility(View.VISIBLE);
                    binding.dateSelect.setVisibility(View.GONE);
                    binding.allMemberlist.setVisibility(View.VISIBLE);
                    binding.calendarArea.setVisibility(View.GONE);
                    WritePaymentList(change_place_id.equals("") ? place_id : change_place_id, change_member_id, setDate , "0");
                }
            });


            binding.changePlace.setOnClickListener(v -> {
                PaySelectPlaceActivity psp = new PaySelectPlaceActivity();
                psp.show(getSupportFragmentManager(), "PaySelectPlaceActivity");
                psp.setOnClickListener(new PaySelectPlaceActivity.OnClickListener() {
                    @Override
                    public void onClick(View v, String getplace_id, String getplace_name) {
                        change_place_id = getplace_id;
                        change_place_name = getplace_name;
                        dlog.i("change_place_id : " + getplace_id);
                        dlog.i("change_place_name : " + getplace_name);
                        binding.calenderViewpager.setAdapter(fragmentStateAdapter);
                        binding.calenderViewpager.setCurrentItem(fragmentStateAdapter.returnPosition(), false);
                        binding.calenderViewpager.setOffscreenPageLimit(1);

                        if (getplace_name.equals("전체매장")) {
                            binding.changePlaceTv.setText("전체매장");
                            change_place_id = "";
                            change_place_name = "";
                            shardpref.putString("change_place_id", place_id);
                            shardpref.putString("change_place_name", "");
                        } else {
                            binding.changePlaceTv.setText(getplace_name);
                            shardpref.putString("change_place_id", getplace_id);
                            shardpref.putString("change_place_name", getplace_name);
                        }
                        shardpref.putString("change_place_id", getplace_id);
                        shardpref.putString("change_place_name", getplace_name);

                        dlog.i("change_place_id : " + change_place_id);
                        dlog.i("change_place_name : " + change_place_name);

                        if (chng_icon) {
                            SetPayCalenderData();
                        } else {
                            WritePaymentList(change_place_id.equals("") ? place_id : change_place_id, change_member_id, Year + "-" + Month, Tap);
                        }
                    }
                });
            });

            binding.changeMember.setOnClickListener(v -> {
                PaySelectMemberActivity psm = new PaySelectMemberActivity();
                psm.show(getSupportFragmentManager(), "PaySelectMemberActivity");
                psm.setOnClickListener(new PaySelectMemberActivity.OnClickListener() {
                    @Override
                    public void onClick(View v, String user_id, String user_name) {
                        change_member_id = user_id;
                        change_member_name = user_name;
                        if (user_name.equals("전체직원")) {
                            binding.changeMemberTv.setText("전체직원");
                            change_place_id = "";
                            change_place_name = "";
                        } else {
                            binding.changeMemberTv.setText(user_name);
                        }
                        dlog.i("change_member_id : " + user_id);
                        dlog.i("change_member_name : " + user_name);
                        shardpref.putString("change_member_id", user_id);
                        shardpref.putString("change_member_name", user_name);
                        if (chng_icon) {
//                            SetCalenderData(Year, Month);
                        } else {
                            WritePaymentList(change_place_id.equals("") ? place_id : change_place_id, change_member_id, Year + "-" + Month, Tap);
                        }
                    }
                });
            });

            TimeSetFun();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Fragment fg;
    Calendar cal;
    String format = "yyyy-MM-dd";
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    String toDay = "";
    String Year = "";
    String Month = "";
    String Day = "";
    String getYMPicker = "";
    FragmentTransaction transaction;
    String setDate = "";
    int before_pos = 0;
    int calPos = 0;

    private void ScrollState(int kind) {
        if (kind == 0) {
            //왼쪽으로 슬라이드 - 버튼으로
            before_pos = 0;
            binding.setdate.setText(Year + "년 " + Month + "월 ");
            int currentPosition = binding.calenderViewpager.getCurrentItem();
            binding.calenderViewpager.setCurrentItem(currentPosition - 1, true);
            binding.calenderViewpager.setOffscreenPageLimit(1);
        } else if (kind == 1) {
            //오른쪽으로 슬라이드 - 버튼으로
            before_pos = 0;
            binding.setdate.setText(Year + "년 " + Month + "월 ");
            int currentPosition = binding.calenderViewpager.getCurrentItem();
            binding.calenderViewpager.setCurrentItem(currentPosition + 1, true);
            binding.calenderViewpager.setOffscreenPageLimit(1);
        } else if (kind == 3) {
            //왼쪽으로 슬라이드
            cal.add(Calendar.MONTH, -1);
            toDay = sdf.format(cal.getTime());
            Year = toDay.substring(0, 4);
            Month = toDay.substring(5, 7);
            Day = toDay.substring(8, 10);
            getYMPicker = Year + "-" + Month;
            binding.setdate.setText(Year + "년 " + Month + "월 ");
            binding.calenderViewpager.setOffscreenPageLimit(1);
        } else if (kind == 4) {
            //왼쪽으로 슬라이드
            cal.add(Calendar.MONTH, +1);
            toDay = sdf.format(cal.getTime());
            Year = toDay.substring(0, 4);
            Month = toDay.substring(5, 7);
            Day = toDay.substring(8, 10);
            getYMPicker = Year + "-" + Month;
            binding.setdate.setText(Year + "년 " + Month + "월 ");
            binding.calenderViewpager.setOffscreenPageLimit(1);
        }
    }

    private void TimeSetFun() {
        transaction = getSupportFragmentManager().beginTransaction();

        cal = Calendar.getInstance();
        toDay = sdf.format(cal.getTime());
        dlog.i("오늘 :" + toDay);
        Year = toDay.substring(0, 4);
        Month = toDay.substring(5, 7);
        binding.setdate.setText(Year + "년 " + Month + "월");
        setDate = Year + "-" + Month;
        if (chng_icon) {
//            SetCalenderData(Year, Month);
        } else {
            WritePaymentList(change_place_id.equals("") ? place_id : change_place_id, change_member_id, Year + "-" + Month, Tap);
        }

        binding.calenderViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // 슬라이드가 끝난 후 작동할 이벤트
                if (before_pos != position) {
                    if (before_pos != 0) {
                        calPos = position - before_pos;
                        dlog.i("onPageScrollStateChanged state : " + calPos);
                        if (calPos > 0) {
                            ScrollState(4);
                        } else {
                            ScrollState(3);
                        }
                    }
                    before_pos = position;
                }
            }
        });

        binding.prevDate.setOnClickListener(v -> {
            dlog.i("prevDate Click!! PayManagementActivity");
            cal.add(Calendar.MONTH, -1);
            toDay = sdf.format(cal.getTime());
            Year = toDay.substring(0, 4);
            Month = toDay.substring(5, 7);
            setDate = Year + "-" + Month;
            binding.setdate.setText(Year + "년 " + Month + "월");
            if (chng_icon) {
                ScrollState(0);
            } else {
                WritePaymentList(change_place_id.equals("") ? place_id : change_place_id, change_member_id, Year + "-" + Month, Tap);
            }
        });

        binding.nextDate.setOnClickListener(v -> {
            dlog.i("nextDate Click!! PayManagementActivity");
            cal.add(Calendar.MONTH, +1);
            toDay = sdf.format(cal.getTime());
            Year = toDay.substring(0, 4);
            Month = toDay.substring(5, 7);
            setDate = Year + "-" + Month;
            binding.setdate.setText(Year + "년 " + Month + "월");
            if (chng_icon) {
                ScrollState(1);
            } else {
                WritePaymentList(change_place_id.equals("") ? place_id : change_place_id, change_member_id, Year + "-" + Month, Tap);
            }
        });

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (month < Integer.parseInt(Month)) {
                    cal.add(Calendar.MONTH, - (Integer.parseInt(Month) - (month + 1)));
                    cal.add(Calendar.DAY_OF_MONTH, - (Integer.parseInt(Day) - (dayOfMonth)));
                } else {
                    cal.add(Calendar.MONTH, ((month + 1)  - Integer.parseInt(Month)));
                    cal.add(Calendar.DAY_OF_MONTH, ((dayOfMonth)  - Integer.parseInt(Day)));
                }
                Year = String.valueOf(year);
                Month = String.valueOf(month + 1);
                Day = String.valueOf(dayOfMonth);
                Day = Day.length() == 1 ? "0" + Day : Day;
                Month = Month.length() == 1 ? "0" + Month : Month;
                binding.setdate.setText(year + "-" + Month);
                getYMPicker = binding.setdate.getText().toString().substring(0, 7);
                if (chng_icon) {
                    binding.calenderViewpager.setSaveFromParentEnabled(false);
//                    fragmentStateAdapter = new FragmentStateAdapter(thisActivity, true, Year, Month, 4);
                    binding.calenderViewpager.setAdapter(fragmentStateAdapter);
                    binding.calenderViewpager.setCurrentItem(fragmentStateAdapter.returnPosition(), false);
                    binding.calenderViewpager.setOffscreenPageLimit(1);
                    binding.setdate.setText(Year + "년 " + Month + "월 ");
//                    SetCalenderData(Year, Month);
                } else {
                    WritePaymentList(change_place_id.equals("") ? place_id : change_place_id, change_member_id, Year + "-" + Month, Tap);
                }
            }
        }, mYear, mMonth, mDay);

        binding.setdate.setOnClickListener(view -> {
            if (binding.setdate.isClickable()) {
                datePickerDialog.show();
            }
        });

        binding.dateSelect.setOnClickListener(view -> {
            if (binding.setdate.isClickable()) {
                datePickerDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Tap.equals("0")) {
            binding.line01.setBackgroundColor(Color.parseColor("#6395EC"));
            binding.line02.setBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            binding.line01.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.line02.setBackgroundColor(Color.parseColor("#6395EC"));
        }
        if (chng_icon) {
//            SetCalenderData(Year, Month);
        } else {
            WritePaymentList(change_place_id.equals("") ? place_id : change_place_id, change_member_id, Year + "-" + Month, Tap);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        SetPayCalenderData();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // 직원 급여 명세서 리스트
    int row_cnt = 0;
    public void WritePaymentList(String place_id, String SelectId, String GET_DATE, String tap) {
        GetInsurancePercent();
        dlog.i("------------PaymentFragment2 List------------");
        dlog.i("place_id : " + place_id);
        dlog.i("GET_DATE : " + GET_DATE);
        dlog.i("SelectId : " + SelectId);
        dlog.i("tap : " + tap);
        dlog.i("------------PaymentFragment2 List------------");
        mList.clear();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(paymanaInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        paymanaInterface api = retrofit.create(paymanaInterface.class);
        Call<String> call = api.getData("1", place_id, GET_DATE, SelectId, "", "", "", "", "", "", "", "", "", "");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "WritePaymentList / setRecyclerView");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                Log.e(TAG, "response 2: " + rc.getBase64decode(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = rc.getBase64decode(response.body());
//                    Log.e(TAG, "GetWorkStateInfo function onSuccess : " + jsonResponse);
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(jsonResponse);
                        mList = new ArrayList<>();
                        mAdapter = new PaymentMemberAdapter(mContext, mList, GET_DATE, insurance01p, insurance02p, insurance03p, insurance04p, tap);
                        binding.allMemberlist.setAdapter(mAdapter);
                        binding.allMemberlist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));

                        if (Response.length() == 0) {
                            binding.nodataArea.setVisibility(View.GONE);
                            Log.i(TAG, "GET SIZE : " + rc.paymentData_lists.size());
                        } else {
                            binding.nodataArea.setVisibility(View.GONE);
                            for (int i = 0; i < Response.length(); i++) {
                                JSONObject jsonObject = Response.getJSONObject(i);
                                if(tap.equals("1")){
                                    if(!jsonObject.getString("gongjeynpay").equals("null")){
                                        mAdapter.addItem(new PaymentData.PaymentData_list(
                                                jsonObject.getString("place_id"),
                                                jsonObject.getString("user_id"),
                                                jsonObject.getString("user_name"),
                                                jsonObject.getString("img_path"),
                                                jsonObject.getString("account"),
                                                jsonObject.getString("jikgup"),
                                                jsonObject.getString("basic_pay"),
                                                jsonObject.getString("second_pay"),
                                                jsonObject.getString("overwork_pay"),
                                                jsonObject.getString("meal_allowance_yn"),
                                                jsonObject.getString("store_insurance_yn"),
                                                jsonObject.getString("gongjeynpay"),
                                                jsonObject.getString("total_pay"),
                                                jsonObject.getString("meal_pay"),
                                                jsonObject.getString("set_month"),
                                                jsonObject.getString("workday"),
                                                jsonObject.getString("workhour"),
                                                jsonObject.getString("total_workday"),
                                                jsonObject.getString("payment")
                                        ));
                                        row_cnt++;
                                    }
                                }else{
                                    mAdapter.addItem(new PaymentData.PaymentData_list(
                                            jsonObject.getString("place_id"),
                                            jsonObject.getString("user_id"),
                                            jsonObject.getString("user_name"),
                                            jsonObject.getString("img_path"),
                                            jsonObject.getString("account"),
                                            jsonObject.getString("jikgup"),
                                            jsonObject.getString("basic_pay"),
                                            jsonObject.getString("second_pay"),
                                            jsonObject.getString("overwork_pay"),
                                            jsonObject.getString("meal_allowance_yn"),
                                            jsonObject.getString("store_insurance_yn"),
                                            jsonObject.getString("gongjeynpay"),
                                            jsonObject.getString("total_pay"),
                                            jsonObject.getString("meal_pay"),
                                            jsonObject.getString("set_month"),
                                            jsonObject.getString("workday"),
                                            jsonObject.getString("workhour"),
                                            jsonObject.getString("total_workday"),
                                            jsonObject.getString("payment")
                                    ));
                                    row_cnt++;
                                }
                            }
                            if(row_cnt == 0){
                                binding.nodataArea.setVisibility(View.GONE);
                            }
                            mAdapter.notifyDataSetChanged();
                            mAdapter.setOnItemClickListener((v, position) -> {
                                try {
                                    shardpref.putString("select_month", binding.setdate.getText().toString().trim());
                                    shardpref.putString("select_user_id", Response.getJSONObject(position).getString("user_id"));
                                    shardpref.putString("select_place_id", Response.getJSONObject(position).getString("place_id"));
                                    shardpref.putString("select_user_name", Response.getJSONObject(position).getString("user_name"));
                                    shardpref.putString("select_total_payment", Response.getJSONObject(position).getString("total_pay"));
                                    shardpref.putString("select_workday", Response.getJSONObject(position).getString("total_workday"));
                                    shardpref.putString("select_total_workhour", Response.getJSONObject(position).getString("workhour"));
                                    shardpref.putString("select_payment", Response.getJSONObject(position).getString("payment"));
                                    shardpref.putString("select_GET_DATE", GET_DATE);

                                    dlog.i("-----mAdapter setOnItemClickListener-----");
                                    dlog.i("select_month : " + binding.setdate.getText().toString().trim());
                                    dlog.i("select_user_id : " + Response.getJSONObject(position).getString("user_id"));
                                    dlog.i("select_place_id : " + Response.getJSONObject(position).getString("place_id"));
                                    dlog.i("select_user_name : " + Response.getJSONObject(position).getString("user_name"));
                                    dlog.i("select_total_payment : " + Response.getJSONObject(position).getString("total_pay"));
                                    dlog.i("select_workday : " + Response.getJSONObject(position).getString("total_workday"));
                                    dlog.i("select_payment : " + Response.getJSONObject(position).getString("payment"));
                                    dlog.i("select_GET_DATE : " + GET_DATE);
                                    dlog.i("-----mAdapter setOnItemClickListener-----");
                                    pm.AddPaystubAlba(mContext);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }
    //급여 현황 다시 조회
    private void SetPayCalenderData() {
        mList3.clear();
        String USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH","");
        String getYMDate = Year + "-" + Month;
        Log.i(TAG, "------SetPayCalenderData------");
        Log.i(TAG, "place_id : " + change_place_id);
        Log.i(TAG, "USER_INFO_ID : " + USER_INFO_ID);
        Log.i(TAG, "select_date : " + getYMDate);
        Log.i(TAG, "------SetPayCalenderData------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PayGetallInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PayGetallInterface api = retrofit.create(PayGetallInterface.class);
        Call<String> call2 = api.getData(change_place_id, USER_INFO_ID, getYMDate, USER_INFO_AUTH);
        call2.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call2, @NonNull Response<String> response2) {
                runOnUiThread(() -> {
                    //캘린더 내용 (업무가) 있을때
                    if (response2.isSuccessful() && response2.body() != null) {
                        String jsonResponse = rc.getBase64decode(response2.body());
                        dlog.i("SetPayCalenderData jsonResponse length : " + jsonResponse.length());
                        dlog.i("SetPayCalenderData jsonResponse : " + jsonResponse);
                        try {
                            JSONArray Response2 = new JSONArray(jsonResponse);
//                            fragmentStateAdapter = new FragmentStateAdapter(this, 4, mList3);
                            binding.calenderViewpager.setAdapter(fragmentStateAdapter);
                            binding.calenderViewpager.setCurrentItem(fragmentStateAdapter.returnPosition(), false);
                            binding.calenderViewpager.setOffscreenPageLimit(1);
                            if (Response2.length() == 0) {
                                dlog.i("SetPayCalenderData GET SIZE : " + Response2.length());
                            } else {
                                for (int i = 0; i < Response2.length(); i++) {
                                    JSONObject jsonObject = Response2.getJSONObject(i);
                                    mList3.add(new WorkGetallData.WorkGetallData_list(
                                            jsonObject.getString("task_month"),
                                            jsonObject.getString("day"),
                                            jsonObject.getString("id"),
                                            jsonObject.getString("place_id"),
                                            jsonObject.getString("kind"),
                                            jsonObject.getString("title"),
                                            jsonObject.getString("task_date")
                                    ));
                                }
                            }
                            fragmentStateAdapter.notifyDataSetChanged();
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

    float insurance01p = 0;//국민연금 퍼센트
    float insurance02p = 0;//건강보험 퍼센트
    float insurance03p = 0;//고용보험 퍼센트
    float insurance04p = 0;//장기요양보험료 퍼센트

    /*4대보험 공제율(퍼센트)*/
    public void GetInsurancePercent() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(paymanaInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        paymanaInterface api = retrofit.create(paymanaInterface.class);
        Call<String> call = api.getData("4", "", "", "", "", "", "", "", "", "", "", "", "", "");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.e("GetInsurancePercent function START");
                dlog.e("response 1: " + response.isSuccessful());
                dlog.e("response 2: " + rc.getBase64decode(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = rc.getBase64decode(response.body());
//                    Log.e(TAG, "GetWorkStateInfo function onSuccess : " + jsonResponse);
                    try {
                        JSONArray Response = new JSONArray(jsonResponse);

                        insurance01p = Float.parseFloat(Response.getJSONObject(0).getString("insurance01"));//국민연금 퍼센트
                        insurance02p = Float.parseFloat(Response.getJSONObject(0).getString("insurance02"));//건강보험 퍼센트
                        insurance03p = Float.parseFloat(Response.getJSONObject(0).getString("insurance03"));//고용보험 퍼센트
                        insurance04p = Float.parseFloat(Response.getJSONObject(0).getString("insurance04"));//장기요양보험료 퍼센트
                        dlog.i("insurance01p : " + insurance01p);
                        dlog.i("insurance02p : " + insurance02p);
                        dlog.i("insurance03p : " + insurance03p);
                        dlog.i("insurance04p : " + insurance04p);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            @SuppressLint("LongLogTag")
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "에러2 = " + t.getMessage());
            }
        });
    }

    public void isAuth() {
        Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
        intent.putExtra("flag","더미");
        intent.putExtra("data","먼저 매장등록을 해주세요!");
        intent.putExtra("left_btn_txt", "닫기");
        intent.putExtra("right_btn_txt", "매장추가");
        startActivity(intent);
        overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
}
