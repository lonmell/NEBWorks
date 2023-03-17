package com.krafte.nebworks.ui.member;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.ApprovalAdapter;
import com.krafte.nebworks.adapter.MemberInoutAdapter;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.data.WorkGotoListData;
import com.krafte.nebworks.dataInterface.AllMemberInterface;
import com.krafte.nebworks.dataInterface.ContractListInterface;
import com.krafte.nebworks.dataInterface.MainContentsInterface;
import com.krafte.nebworks.dataInterface.MainWorkCntInterface;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    String stub_user_name = "";

    //직원관리페이지에서 넘어왔을때
    String mem_id = "";

    int SELECT_POSITION = 0;
    int SELECT_POSITION_sub = 0;
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
    String place_name = "";

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
        shardpref = new PreferenceHelper(mContext);
        try {
            icon_off = mContext.getApplicationContext().getResources().getDrawable(R.drawable.menu_gray_bar);
            icon_on = mContext.getApplicationContext().getResources().getDrawable(R.drawable.menu_blue_bar);

            //Singleton Area
            place_id = PlaceCheckData.getInstance().getPlace_id();
            place_owner_id = PlaceCheckData.getInstance().getPlace_owner_id();
            USER_INFO_ID = UserCheckData.getInstance().getUser_id();
            USER_INFO_NAME = UserCheckData.getInstance().getUser_name();
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");

            //shardpref Area
            stub_place_id = shardpref.getString("stub_place_id", "0");
            stub_user_id = shardpref.getString("stub_user_id", "0");
            stub_user_account = shardpref.getString("stub_user_account", "");
            change_place_name = shardpref.getString("change_place_name", "");
            SELECT_POSITION = shardpref.getInt("SELECT_POSITION", 0);
            SELECT_POSITION_sub = shardpref.getInt("SELECT_POSITION_sub", 0);
            wifi_certi_flag = shardpref.getBoolean("wifi_certi_flag", false);
            gps_certi_flag = shardpref.getBoolean("gps_certi_flag", false);
            return_page = shardpref.getString("return_page", "");
            item_user_id = shardpref.getString("item_user_id", "");
            stub_user_name = shardpref.getString("stub_user_name", "");

            dlog.i("stub_user_name : " + stub_user_name);
            setBtnEvent();
            drawerLayout = findViewById(R.id.drawer_layout);
            drawerView = findViewById(R.id.drawer2);
            drawerLayout.addDrawerListener(listener);
            drawerView.setOnTouchListener((v, event) -> false);
            setAddBtnSetting();

            if (USER_INFO_AUTH.equals("1")) {
                binding.addBtn.getRoot().setVisibility(View.GONE);
            }

            if (place_owner_id.equals(USER_INFO_ID) && USER_INFO_AUTH.equals("1")) {
                binding.contractPhoneInfo.setVisibility(View.GONE);
            } else if (place_owner_id.equals(USER_INFO_ID) && USER_INFO_AUTH.equals("0")) {
                binding.contractPhoneInfo.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            //슬라이드 했을때
        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {
            //Drawer가 오픈된 상황일때 호출
        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {
            // 닫힌 상황일 때 호출
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            // 특정상태가 변결될 때 호출
        }
    };


    //navbar.xml
    DrawerLayout drawerLayout;
    View drawerView;
    ImageView close_btn, user_profile, my_setting;
    TextView user_name, jikgup, store_name;
    /*본인 정보 START*/
    String Navname = "";
    String Navimg_path = "";
    String Navgetjikgup = "";

    @SuppressLint("LongLogTag")
    public void setNavBarBtnEvent() {
        drawerView = findViewById(R.id.drawer2);
        close_btn = findViewById(R.id.close_btn);
        user_profile = findViewById(R.id.user_profile);
        my_setting = findViewById(R.id.my_setting);
        user_name = findViewById(R.id.user_name);
        jikgup = findViewById(R.id.jikgup);
        store_name = findViewById(R.id.store_name);

        dlog.i("Navname : " + Navname);
        dlog.i("Navimg_path : " + Navimg_path);
        dlog.i("Navgetjikgup : " + Navgetjikgup);

        user_name.setText(Navname);
        jikgup.setText(Navgetjikgup);
        Glide.with(mContext).load(Navimg_path)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(user_profile);

        store_name.setText(place_name);

        close_btn.setOnClickListener(v -> {
            drawerLayout.closeDrawer(drawerView);
        });
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
            cal.add(Calendar.MONTH, -1);
            toDay = sdf.format(cal.getTime());
            dlog.i("prevDate :" + toDay);
            Year = toDay.substring(0, 4);
            Month = toDay.substring(5, 7);
            binding.setdate.setText(Year + "년 " + Month + "월");
            if (!stub_place_id.equals("0")) {
                SetGotoWorkDayList(stub_place_id, stub_user_id, Year + "-" + Month);
            } else {
                SetGotoWorkDayList(place_id, USER_INFO_ID, Year + "-" + Month);
            }
        });
        binding.nextDate.setOnClickListener(v -> {
            cal.add(Calendar.MONTH, +1);
            toDay = sdf.format(cal.getTime());
            dlog.i("nextDate :" + toDay);
            Year = toDay.substring(0, 4);
            Month = toDay.substring(5, 7);
            binding.setdate.setText(Year + "년 " + Month + "월");
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

        binding.inoutPrint.setOnClickListener(v -> {
            String date = Year + "-" + Month;
//            dlog.i("-----inoutPrint-----");
//            dlog.i("user_id : " + stub_user_id);
//            dlog.i("place_id : " + place_id);
//            dlog.i("date : " + binding.setdate.getText().toString());
//            dlog.i("-----inoutPrint-----");
////            http://krafte.net/NEBWorks/Commute.php?user_id=64&place_id=97&date=2022-12
//            String Contract_uri = "http://krafte.net/NEBWorks/Commute.php?user_id=" + stub_user_id + "&place_id=" + place_id + "&date=" + date;
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Contract_uri));
//            startActivity(intent);
            getExcelGotoList(stub_place_id, stub_user_id, Year + "-" + Month);
        });

        binding.todayTodo.setOnClickListener(v -> {
            shardpref.putInt("SELECT_POSITION", 1);
            pm.Main2(mContext);
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
        SetAllMemberList();
        SetAllMemberList(stub_place_id, stub_user_id);
        SetContractList();
        MainWorkCnt(stub_place_id, stub_user_id);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shardpref.remove("stub_place_id");
        shardpref.remove("stub_user_id");
        shardpref.remove("stub_user_account");
    }

    /*업무카운팅 START*/
    RetrofitConnect rc = new RetrofitConnect();
    String contract_id = "";

    public void MainWorkCnt(String place_id, String user_id) {
        dlog.i("SetAllMemberList place_id : " + place_id);
        dlog.i("SetAllMemberList user_id : " + user_id);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MainWorkCntInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        MainWorkCntInterface api = retrofit.create(MainWorkCntInterface.class);
        Call<String> call = api.getData(place_id, user_id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = rc.getBase64decode(response.body());
                    dlog.i("MainWorkCnt jsonResponse length : " + jsonResponse.length());
                    dlog.i("MainWorkCnt jsonResponse : " + jsonResponse);
                    Log.e("onSuccess : ", response.body());
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(jsonResponse);
                        if (Response.length() != 0) {
                            String task_complete_cnt = Response.getJSONObject(0).getString("task_complete_cnt");
                            String task_incomplete_cnt = Response.getJSONObject(0).getString("task_incomplete_cnt");
                            String approval_total_cnt = Response.getJSONObject(0).getString("approval_total_cnt");
                            String waiting_cnt = Response.getJSONObject(0).getString("waiting_cnt");
                            String approval_cnt = Response.getJSONObject(0).getString("approval_cnt");
                            String reject_cnt = Response.getJSONObject(0).getString("reject_cnt");
                            String contract_cnt = Response.getJSONObject(0).getString("contract_cnt");
                            String owner_sign_id = Response.getJSONObject(0).getString("owner_sign_id");
                            String worker_sign_id = Response.getJSONObject(0).getString("worker_sign_id");

                            contract_id = Response.getJSONObject(0).getString("id");

                            dlog.i("-----MainWorkCnt-----");
                            dlog.i("task_complete_cnt : " + task_complete_cnt);
                            dlog.i("task_incomplete_cnt : " + task_incomplete_cnt);
                            dlog.i("waiting_cnt : " + waiting_cnt);
                            dlog.i("approval_cnt : " + approval_cnt);
                            dlog.i("reject_cnt : " + reject_cnt);
                            dlog.i("contract_cnt : " + contract_cnt);
                            dlog.i("contract_id : " + contract_id);
                            dlog.i("owner_sign_id : " + owner_sign_id);
                            dlog.i("worker_sign_id : " + worker_sign_id);
                            dlog.i("-----MainWorkCnt-----");
                            binding.workdata01.setText(task_complete_cnt);
                            binding.workdata02.setText(task_incomplete_cnt);
                            binding.workdata03.setText(waiting_cnt);
                            binding.workdata04.setText(approval_cnt);
                            binding.workdata05.setText(reject_cnt);
                            if (!worker_sign_id.equals("null") && !owner_sign_id.equals("null")) {
                                binding.contractState.setTextColor(Color.parseColor("#6395EC"));
                                binding.contractState.setText("작성완료");
                                binding.contractAllGo.setOnClickListener(v -> {
                                    if (mem_id.equals(USER_INFO_ID) || USER_INFO_AUTH.equals("0")) {
                                        shardpref.putString("contract_id", contract_id);
                                        pm.ContractAll(mContext);
                                    }
                                });
                            } else {
                                if (worker_sign_id.equals("null") && owner_sign_id.equals("null")) {
                                    binding.contractState.setTextColor(Color.parseColor("#DD6540"));
                                    binding.contractState.setText("미처리");
                                    //미처리 일때 근로계약서 리스트 페이지로
                                    binding.contractAllGo.setOnClickListener(v -> {
                                        if (mem_id.equals(USER_INFO_ID) || USER_INFO_AUTH.equals("0")) {
                                            if (USER_INFO_AUTH.equals("0")) {
                                                pm.AddContractPage01(mContext);
                                            } else {
                                                Toast.makeText(mContext, "작성된 근로계약서가 없습니다. ", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    binding.contractState.setTextColor(Color.parseColor("#DD6540"));
                                    binding.contractState.setText("작성중");
                                    binding.contractAllGo.setOnClickListener(v -> {
                                        if (mem_id.equals(USER_INFO_ID) || USER_INFO_AUTH.equals("0")) {
                                            shardpref.putString("contract_id", contract_id);
                                            shardpref.putString("progress_pos", getprogress_pos);
                                            /* item.getContract_id()
                                            *   현재 진행중인 페이지
                                                0 or null - 작성안됨
                                                1 - 사업장 기본사항
                                                2 - 근무 기본사항
                                                3 - 급여 기본사항
                                                4 - 특약
                                                5 - 근로자 인적사항
                                                6 - 서명
                                                7 - 완료
                                            * */
                                            if (USER_INFO_AUTH.equals("0")) {
                                                if (getprogress_pos.equals("0")) {
                                                    pm.AddContractPage03(mContext);
                                                } else if (getprogress_pos.equals("1")) {
                                                    //근무 기본사항 부터
                                                    pm.AddContractPage04(mContext);
                                                } else if (getprogress_pos.equals("2")) {
                                                    //급여 기본사항 부터
                                                    pm.AddContractPage05(mContext);
                                                } else if (getprogress_pos.equals("3")) {
                                                    //특약 부터
                                                    pm.AddContractPage06(mContext);
                                                } else if (getprogress_pos.equals("4")) {
                                                    //근로자 인적사항 부터
                                                    pm.AddContractPage07(mContext);
                                                } else if (getprogress_pos.equals("5")) {
                                                    //서명 부터
                                                    pm.AddContractPage08(mContext);
                                                } else if (getprogress_pos.equals("7")) {
                                                    //해당 근로계약서 전체 상세 페이지로
                                                    shardpref.putString("contract_id", contract_id);
                                                    pm.ContractAll(mContext);
                                                }
                                            } else {
                                                //근로자일경우
                                                if (getprogress_pos.equals("6")) {
                                                    pm.ContractWorkerAccept(mContext);
                                                } else if (getprogress_pos.equals("7")) {
                                                    //해당 근로계약서 전체 상세 페이지로
                                                    shardpref.putString("contract_id", contract_id);
                                                    pm.ContractAll(mContext);
                                                }
                                            }
                                        }
                                    });
                                }
//                                    Toast_Nomal("근로계약서 작성이 완료되지 않았습니다.");
                            }
                        }
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

    }

    /*업무카운팅 START*/

    /*직원 정보 START*/
    String workpay = "";
    String CallNum = "";

    public void SetAllMemberList(String place_id, String user_id) {
        dlog.i("SetAllMemberList place_id : " + place_id);
        dlog.i("SetAllMemberList user_id : " + user_id);
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
                            if (Response.length() != 0) {
                                mem_id = Response.getJSONObject(0).getString("id");
                                String name = Response.getJSONObject(0).getString("name");
                                String place_name = Response.getJSONObject(0).getString("place_name");
                                String join_date = Response.getJSONObject(0).getString("join_date").replace("-", ".");
                                String img_path = Response.getJSONObject(0).getString("img_path");
                                String phone = Response.getJSONObject(0).getString("phone");
                                String owner_phone = Response.getJSONObject(0).getString("owner_phone");
                                String jumin = Response.getJSONObject(0).getString("jumin");
                                String gender = Response.getJSONObject(0).getString("gender");
                                String kind = Response.getJSONObject(0).getString("kind");
                                String state = Response.getJSONObject(0).getString("state");
                                String pay = Response.getJSONObject(0).getString("pay");

                                if (!owner_phone.isEmpty()) {
                                    owner_phone = Response.getJSONObject(0).getString("owner_phone").substring(0, 3) + "-"
                                            + Response.getJSONObject(0).getString("owner_phone").substring(3, 7) + "-"
                                            + Response.getJSONObject(0).getString("owner_phone").substring(7, 11);
                                }
                                if (!phone.isEmpty()) {
                                    phone = Response.getJSONObject(0).getString("phone").substring(0, 3) + "-"
                                            + Response.getJSONObject(0).getString("phone").substring(3, 7) + "-"
                                            + Response.getJSONObject(0).getString("phone").substring(7, 11);
                                }
                                String jikgup = Response.getJSONObject(0).getString("jikgup");

                                binding.name.setText(name);
                                binding.placeNametv.setText(place_name);
                                binding.joinDatetv.setText(join_date + "부터 가입");
                                Glide.with(mContext).load(img_path)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .into(binding.profileImg);

                                String inoutstate = Response.getJSONObject(0).getString("inoutstate");
                                if (inoutstate.equals("-1")) {
                                    binding.workState.setText("미출근");
                                } else if (inoutstate.equals("0")) {
                                    binding.workState.setText("출근");
                                } else if (inoutstate.equals("1")) {
                                    binding.workState.setText("퇴근");
                                }
                                workpay = Response.getJSONObject(0).getString("pay").equals("null") ? "미정" : Response.getJSONObject(0).getString("pay");
                                binding.workPay.setText(Response.getJSONObject(0).getString("pay").equals("null") ? "미정" : Response.getJSONObject(0).getString("pay"));

                                if (phone.isEmpty() || phone.equals("null")) {
                                    phone = "미입력";
                                }
                                if (owner_phone.isEmpty()) {
                                    owner_phone = "미입력";
                                }
                                if (USER_INFO_AUTH.equals("0")) {
                                    binding.callNumber.setText("전화걸기");
                                    binding.userPhone.setText(phone);
                                    CallNum = phone;
                                } else {
                                    if (mem_id.equals(USER_INFO_ID)) {
                                        binding.callNumber.setText("사장님께 전화걸기");
                                        binding.userPhone.setText(owner_phone);
                                        CallNum = owner_phone;
                                    } else {
                                        binding.callNumber.setText("전화걸기");
                                        binding.userPhone.setText(phone);
                                        CallNum = phone;
                                    }
                                }

                                binding.userCall.setOnClickListener(v -> {
                                    Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + CallNum));
                                    mContext.startActivity(mIntent);
                                });
                                setNavBarBtnEvent();
                                PlaceWorkCheck(stub_place_id, "1", "3");

                                binding.memOption.setOnClickListener(v -> {
                                    shardpref.putString("mem_id", mem_id);
                                    shardpref.putString("mem_account", stub_user_account);
                                    shardpref.putString("mem_name", name);
                                    shardpref.putString("mem_phone", CallNum);
                                    shardpref.putString("mem_gender", gender);
                                    shardpref.putString("mem_jumin", jumin);
                                    shardpref.putString("mem_kind", kind);
                                    shardpref.putString("mem_join_date", join_date);
                                    shardpref.putString("mem_state", state);
                                    shardpref.putString("mem_jikgup", jikgup);
                                    shardpref.putString("mem_pay", pay);
                                    pm.AddMemberDetail(mContext);
                                });
                            }
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
    /*직원 정보 리스트 END*/

    public void SetAllMemberList() {
        dlog.i("-----SetAllMemberList-----");
        dlog.i("place_id : " + place_id);
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("-----SetAllMemberList-----");
        @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AllMemberInterface.URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            AllMemberInterface api = retrofit.create(AllMemberInterface.class);
            Call<String> call = api.getData(place_id, USER_INFO_ID);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = rc.getBase64decode(response.body());
                        Log.e("SetAllMemberList onSuccess : ", jsonResponse);
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(jsonResponse);
                            Navname = Response.getJSONObject(0).getString("name");
                            Navimg_path = Response.getJSONObject(0).getString("img_path");
                            Navgetjikgup = Response.getJSONObject(0).getString("jikgup");
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
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            dlog.e("SetGotoWorkDayList function START");
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.e("response 1: " + response.isSuccessful());
                            dlog.e("response 2: " + jsonResponse);
                            if (jsonResponse.equals("[]")) {
                                binding.noDataTxt.setVisibility(View.VISIBLE);
                            } else {
                                binding.noDataTxt.setVisibility(View.GONE);
                                try {
                                    JSONArray Response = new JSONArray(jsonResponse);
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
                                                    jsonObject.getString("yoil"),
                                                    jsonObject.getString("in_time"),
                                                    jsonObject.getString("out_time"),
                                                    jsonObject.getString("workdiff"),
                                                    jsonObject.getString("state"),
                                                    jsonObject.getString("sieob1"),
                                                    jsonObject.getString("sieob2"),
                                                    jsonObject.getString("jongeob1"),
                                                    jsonObject.getString("jongeob2"),
                                                    jsonObject.getString("vaca_accept"),
                                                    jsonObject.getString("hdd")
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


    public void getExcelGotoList(String place_id, String user_id, String getYMdate) {
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
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            dlog.e("SetGotoWorkDayList function START");
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.e("response 1: " + response.isSuccessful());
                            dlog.e("response 2: " + jsonResponse);
                            if (jsonResponse.equals("[]")) {
                                binding.noDataTxt.setVisibility(View.VISIBLE);
                            } else {
                                binding.noDataTxt.setVisibility(View.GONE);
                                try {
                                    JSONArray Response = new JSONArray(jsonResponse);
                                    Log.i(TAG, "SetNoticeListview Thread run! ");

                                    if (Response.length() == 0) {
                                        Log.i(TAG, "(gotoWorkData_list)GET SIZE : " + Response.length());
                                    } else {
                                        // 새 통합 문서 만들기
                                        File file_path;
                                        String SD_PATH = "/sdcard/Download/nepworks/";
                                        try {
                                            file_path = new File(SD_PATH);
                                            if (!file_path.isDirectory()) {//해당 경로의 파일이 없으면 생성시켜준다
                                                file_path.mkdirs();
                                            }
                                            dlog.i("(binding.inoutPrint)file_path : " + file_path);
                                            dlog.i("(binding.inoutPrint)file_name : " + "TESTXSECL1234.pdf");

                                            Workbook workbook = new Workbook();
                                            // 셀에 값 추가
                                            for (int i = 0; i < Response.length(); i++) {
                                                JSONObject jsonObject = Response.getJSONObject(i);
//                                            inoutmAdapter.addItem(new WorkGotoListData.WorkGotoListData_list(
//                                                    jsonObject.getString("day"),
//                                                    jsonObject.getString("yoil"),
//                                                    jsonObject.getString("in_time"),
//                                                    jsonObject.getString("out_time"),
//                                                    jsonObject.getString("workdiff"),
//                                                    jsonObject.getString("state"),
//                                                    jsonObject.getString("sieob1"),
//                                                    jsonObject.getString("sieob2"),
//                                                    jsonObject.getString("jongeob1"),
//                                                    jsonObject.getString("jongeob2"),
//                                                    jsonObject.getString("vaca_accept"),
//                                                    jsonObject.getString("hdd")
//                                            ));
                                                workbook.getWorksheets().get(i).getCells().get("A1").putValue(jsonObject.getString("day"));
                                            }

                                            // Excel XLSX 파일로 저장
                                            workbook.save(SD_PATH + "TESTXSECL1234.pdf", SaveFormat.PDF);
                                        } catch (FileNotFoundException exception) {
                                            dlog.e("FileNotFoundException : " + exception.getMessage());
                                        } catch (IOException exception) {
                                            dlog.e("IOException : " + exception.getMessage());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
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
    /*출퇴근 리스트 END*/

    public void PlaceWorkCheck(String place_id, String auth, String kind) {
        dlog.i("PlaceWorkCheck place_id : " + place_id);
        dlog.i("PlaceWorkCheck auth : " + auth);
        dlog.i("PlaceWorkCheck kind : " + kind);
        dlog.i("PlaceWorkCheck USER_INFO_ID : " + stub_user_id);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MainContentsInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        MainContentsInterface api = retrofit.create(MainContentsInterface.class);
        Call<String> call = api.getData(place_id, auth, stub_user_id, kind);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("PlaceWorkCheck jsonResponse length : " + jsonResponse.length());
                            dlog.i("PlaceWorkCheck jsonResponse : " + jsonResponse);
                            try {
                                if (!jsonResponse.equals("[]")) {
                                    JSONArray Response = new JSONArray(jsonResponse);

                                    try {
                                        int allPay = 0;//pay_diff_bar
                                        String getPay = "";
                                        for (int i = 0; i < Response.length(); i++) {
                                            getPay = Response.getJSONObject(i).getString("recent_pay").replace(",", "");
                                            allPay += Integer.parseInt(getPay);
                                        }
                                        String allwcnt = Response.getJSONObject(0).getString("allwcnt"); //근무해야하는 횟수
                                        String iocnt = Response.getJSONObject(0).getString("iocnt"); //근무한 횟수

                                        DecimalFormat myFormatter = new DecimalFormat("###,###");
                                        workpay = workpay.replace(",", "");
                                        int WorkPaY = Integer.parseInt(workpay);
                                        int UntilNowPay = (allPay * 100) / WorkPaY;
                                        dlog.i("allPay : " + allPay);
                                        dlog.i("WorkPaY : " + WorkPaY);
                                        dlog.i("UntilNowPay : " + UntilNowPay);
                                        dlog.i("allwcnt : " + allwcnt);
                                        dlog.i("iocnt : " + iocnt);
                                        binding.nowPayTv.setText("예상급여 " + String.valueOf(myFormatter.format(allPay)) + "원");
                                        binding.nowPay.setText(String.valueOf(myFormatter.format(allPay)) + "원");
                                        binding.payTv.setText(String.valueOf(myFormatter.format(Integer.parseInt(workpay))) + "원");
                                        binding.payDiffBar.setMax(Integer.parseInt(allwcnt));
                                        binding.payDiffBar.setProgress(Integer.parseInt(workpay));
                                        binding.payDiffBar.setOnTouchListener((v, event) -> {
                                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                                return false;
                                            }
                                            return true;
                                        });
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


    String getcontract_id = "";
    String getcontract_kind = "";
    String getprogress_pos = "";

    private void SetContractList() {
        dlog.i("-----SetContractList1-----");
        dlog.i("place_id : " + place_id);
        dlog.i("stub_user_id : " + stub_user_id);
        dlog.i("-----SetContractList1-----");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ContractListInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ContractListInterface api = retrofit.create(ContractListInterface.class);
        Call<String> call = api.getData(place_id, stub_user_id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = rc.getBase64decode(response.body());
                    dlog.i("SetContractList jsonResponse length : " + jsonResponse.length());
                    dlog.i("SetContractList jsonResponse : " + jsonResponse);
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(jsonResponse);
                        getcontract_id = Response.getJSONObject(0).getString("contract_yn");
                        getcontract_kind = Response.getJSONObject(0).getString("kind");
                        getprogress_pos = Response.getJSONObject(0).getString("progress_pos");
                        shardpref.putString("worker_id", stub_user_id);
                        shardpref.putString("worker_name", Navname);
                        shardpref.putString("contract_place_id", place_id);
                        shardpref.putString("contract_user_id", stub_user_id);
                        dlog.i("-----SetContractList2-----");
                        dlog.i("worker_id : " + stub_user_id);
                        dlog.i("worker_name : " + Navname);
                        dlog.i("contract_place_id : " + place_id);
                        dlog.i("contract_user_id : " + stub_user_id);
                        dlog.i("-----SetContractList2-----");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러3 = " + t.getMessage());
            }
        });
    }

    public void btnOnclick(View view) {
        if (view.getId() == R.id.menu) {
            drawerLayout.openDrawer(drawerView);
        } else if (view.getId() == R.id.out_store) {
            pm.PlaceList(mContext);
        } else if (view.getId() == R.id.select_nav01) {
            pm.PlaceList(mContext);
        } else if (view.getId() == R.id.select_nav02) {
            pm.PlaceAddGo(mContext);
        } else if (view.getId() == R.id.select_nav03) {
            drawerLayout.closeDrawer(drawerView);
        } else if (view.getId() == R.id.select_nav04) {
            shardpref.putInt("SELECT_POSITION", 2);
            shardpref.putInt("SELECT_POSITION_sub", 0);
            pm.Main(mContext);
            drawerLayout.closeDrawer(drawerView);
        } else if (view.getId() == R.id.select_nav05) {
            shardpref.putString("Tap", "0");
            if (USER_INFO_AUTH.equals("0")) {
                pm.PayManagement(mContext);
            } else {
                pm.PayManagement2(mContext);
            }
        } else if (view.getId() == R.id.select_nav06) {
            shardpref.putString("Tap", "1");
            if (USER_INFO_AUTH.equals("0")) {
                pm.PayManagement(mContext);
            } else {
                pm.PayManagement2(mContext);
            }
        } else if (view.getId() == R.id.select_nav07) {//캘린더보기 | 할일페이지
            shardpref.putInt("SELECT_POSITION", 1);
            shardpref.putInt("SELECT_POSITION_sub", 0);
            pm.Main(mContext);
            drawerLayout.closeDrawer(drawerView);
        } else if (view.getId() == R.id.select_nav08) {//할일추가하기 - 작성페이지로
            pm.addWorkGo(mContext);
        } else if (view.getId() == R.id.select_nav09) {
            pm.Approval(mContext);
        } else if (view.getId() == R.id.select_nav12) {
            dlog.i("커뮤니티 Click!");
            if (USER_INFO_AUTH.equals("0")) {
                shardpref.putInt("SELECT_POSITION", 3);
                pm.Main(mContext);
            } else {
                shardpref.putInt("SELECT_POSITION", 3);
                pm.Main(mContext);
            }
            drawerLayout.closeDrawer(drawerView);
        } else if (view.getId() == R.id.select_nav10) {
            dlog.i("근로계약서 전체 관리");
            pm.ContractFragment(mContext);
        }

    }

    CardView add_worktime_btn;
    TextView addbtn_tv;

    private void setAddBtnSetting() {
        add_worktime_btn = binding.getRoot().findViewById(R.id.add_worktime_btn);
        addbtn_tv = binding.getRoot().findViewById(R.id.addbtn_tv);
        addbtn_tv.setText("근무추가");
        add_worktime_btn.setOnClickListener(v -> {
            shardpref.putString("item_user_id", stub_user_id);
            shardpref.putString("item_user_name", stub_user_name);
            pm.AddWorkPart(mContext);
        });
    }

    public void Toast_Nomal(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_normal_toast, (ViewGroup) findViewById(R.id.toast_layout));
        TextView toast_textview = layout.findViewById(R.id.toast_textview);
        toast_textview.setText(String.valueOf(message));
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); //TODO 메시지가 표시되는 위치지정 (가운데 표시)
        //toast.setGravity(Gravity.TOP, 0, 0); //TODO 메시지가 표시되는 위치지정 (상단 표시)
        toast.setGravity(Gravity.BOTTOM, 0, 0); //TODO 메시지가 표시되는 위치지정 (하단 표시)
        toast.setDuration(Toast.LENGTH_SHORT); //메시지 표시 시간
        toast.setView(layout);
        toast.show();
    }
}
