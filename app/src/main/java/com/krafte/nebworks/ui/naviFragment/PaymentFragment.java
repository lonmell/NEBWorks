package com.krafte.nebworks.ui.naviFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.PaymentMemberAdapter;
import com.krafte.nebworks.adapter.ViewPagerFregmentAdapter;
import com.krafte.nebworks.bottomsheet.PaySelectMemberActivity;
import com.krafte.nebworks.bottomsheet.PaySelectPlaceActivity;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.PaymentData;
import com.krafte.nebworks.dataInterface.paymanaInterface;
import com.krafte.nebworks.databinding.ActivityPaymanagementBinding;
import com.krafte.nebworks.util.DBConnection;
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

public class PaymentFragment extends Fragment {
    private final static String TAG = "PaymentFragment";
    private ActivityPaymanagementBinding binding;
    Context mContext;
    Activity activity;

    ArrayList<PaymentData.PaymentData_list> mList = new ArrayList<>();
    PaymentMemberAdapter mAdapter = null;


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
    String store_no;
    boolean wifi_certi_flag = false;
    boolean gps_certi_flag = false;

    //Other
    DateCurrent dc = new DateCurrent();
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    RetrofitConnect rc = new RetrofitConnect();
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    ViewPagerFregmentAdapter viewPagerFregmentAdapter;
    int paging_position = 0;
    Fragment fg;

    //Other
    /*라디오 버튼들 boolean*/
    Drawable icon_off;
    Drawable icon_on;
    String Tap = "0";
    int total_member_cnt = 0;

    public static PaymentFragment newInstance(int number) {
        PaymentFragment fragment = new PaymentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("number", number);
        fragment.setArguments(bundle);
        return fragment;
    }

    /*Fragment 콜백함수*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            int num = getArguments().getInt("number");
            Log.i(TAG, "num : " + num);
        }
    }


    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.morefragment, container, false);
        binding = ActivityPaymanagementBinding.inflate(inflater);
        mContext = inflater.getContext();

        //UI 데이터 세팅
        try {
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);

            setBtnEvent();

            icon_off = mContext.getApplicationContext().getResources().getDrawable(R.drawable.menu_gray_bar);
            icon_on = mContext.getApplicationContext().getResources().getDrawable(R.drawable.menu_blue_bar);

            shardpref = new PreferenceHelper(mContext);
            place_id = shardpref.getString("place_id", "");
            place_owner_id = shardpref.getString("place_owner_id", "");
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
            SELECT_POSITION = shardpref.getInt("SELECT_POSITION", 0);
            SELECT_POSITION_sub = shardpref.getInt("SELECT_POSITION_sub", 0);
            wifi_certi_flag = shardpref.getBoolean("wifi_certi_flag", false);
            gps_certi_flag = shardpref.getBoolean("gps_certi_flag", false);
            Tap = shardpref.getString("Tap", "0");


            Log.i(TAG, "USER_INFO_AUTH : " + USER_INFO_AUTH);
            setAddBtnSetting();
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }

        return binding.getRoot();
//        return rootView;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
    }

    public void setBtnEvent() {
        binding.backBtn.setOnClickListener(v -> {
            shardpref.putInt("SELECT_POSITION", 0);
            shardpref.putInt("SELECT_POSITION_sub", 0);
            if (USER_INFO_AUTH.equals("0")) {
                pm.Main(mContext);
            } else {
                pm.Main2(mContext);
            }
        });

        binding.changePlace.setOnClickListener(v -> {
            PaySelectPlaceActivity psp = new PaySelectPlaceActivity();
            psp.show(getChildFragmentManager(), "PaySelectPlaceActivity");
            psp.setOnClickListener(new PaySelectPlaceActivity.OnClickListener() {
                @Override
                public void onClick(View v, String getplace_id, String getplace_name) {
                    change_place_id = getplace_id;
                    change_place_name = getplace_name;
                    dlog.i("change_place_id : " + getplace_id);
                    dlog.i("change_place_name : " + getplace_name);
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
                    dlog.i("change_place_id : " + change_place_id);
                    dlog.i("change_place_name : " + change_place_name);
                    WritePaymentList(change_place_id.equals("") ? place_id : change_place_id, change_member_id, binding.setdate.getText().toString(), Tap);
                }
            });
        });

        binding.changeMember.setOnClickListener(v -> {
            PaySelectMemberActivity psm = new PaySelectMemberActivity();
            psm.show(getChildFragmentManager(), "PaySelectMemberActivity");
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
                    WritePaymentList(change_place_id.equals("") ? place_id : change_place_id, change_member_id, binding.setdate.getText().toString(), Tap);
                }
            });
        });

        binding.select01.setOnClickListener(v -> {
            ChangePage(0);
        });
        binding.select02.setOnClickListener(v -> {
            ChangePage(1);
        });

        TimeSetFun();
        binding.topBar.setVisibility(View.GONE);
    }

    private void ChangePage(int i) {
        binding.select01tv.setTextColor(Color.parseColor("#696969"));
        binding.line01.setBackgroundColor(Color.parseColor("#FFFFFF"));
        binding.select02tv.setTextColor(Color.parseColor("#696969"));
        binding.line02.setBackgroundColor(Color.parseColor("#FFFFFF"));

        paging_position = i;
        if (i == 0) {
            binding.select01tv.setTextColor(Color.parseColor("#6395EC"));
            binding.line01.setBackgroundColor(Color.parseColor("#6395EC"));
            WritePaymentList(change_place_id.equals("") ? place_id : change_place_id, change_member_id, binding.setdate.getText().toString(), "0");
        } else if (i == 1) {
            binding.select02tv.setTextColor(Color.parseColor("#6395EC"));
            binding.line02.setBackgroundColor(Color.parseColor("#6395EC"));
            WritePaymentList(change_place_id.equals("") ? place_id : change_place_id, change_member_id, binding.setdate.getText().toString(), "1");
        }
    }

    CardView add_worktime_btn;
    TextView addbtn_tv;
    private void setAddBtnSetting() {
        add_worktime_btn = binding.getRoot().findViewById(R.id.add_worktime_btn);
        addbtn_tv = binding.getRoot().findViewById(R.id.addbtn_tv);
        addbtn_tv.setText("게시글 작성");
        add_worktime_btn.setOnClickListener(v -> {
            if (paging_position == 0) {
                shardpref.putString("state","AddCommunity");
                pm.CommunityAdd(mContext);
            } else if (paging_position == 1) {
                Toast_Nomal("사장님 게시글");
            } else if (paging_position == 2) {
                Toast_Nomal("세금/노무");
            }
        });
    }

    Calendar cal;
    String format = "yyyy-MM-dd";
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    String toDay = "";
    String Year = "";
    String Month = "";
    String Day = "";
    String getYMPicker = "";
    String gYear = "";
    String gMonth = "";
    String bYear = "";
    String bMonth = "";
    FragmentTransaction transaction;

    private void TimeSetFun() {
        if(USER_INFO_AUTH.equals("1")){
            binding.tabLayout.setVisibility(View.GONE);
            binding.selectArea.setVisibility(View.GONE);
            change_place_id = place_id;
            change_member_id = USER_INFO_ID;
            binding.select01.setVisibility(View.GONE);
        }

        transaction = getChildFragmentManager().beginTransaction();

        cal = Calendar.getInstance();
        toDay = sdf.format(cal.getTime());
        dlog.i("오늘 :" + toDay);
        Year = toDay.substring(0, 4);
        Month = toDay.substring(5, 7);
        binding.setdate.setText(Year + "-" + Month);
        if(USER_INFO_AUTH.equals("1")){
            binding.tabLayout.setVisibility(View.GONE);
            binding.selectArea.setVisibility(View.GONE);
            change_place_id = place_id;
            change_member_id = USER_INFO_ID;
            binding.select01.setVisibility(View.GONE);
        }

        WritePaymentList(change_place_id.equals("") ? place_id : change_place_id, change_member_id, binding.setdate.getText().toString(), Tap);

        binding.prevDate.setOnClickListener(v -> {
            dlog.i("prevDate Click!! PaymentFragment");
            cal.add(Calendar.DATE, -1);
            toDay = sdf.format(cal.getTime());
            binding.setdate.setText(toDay);
            Year = toDay.substring(0, 4);
            Month = toDay.substring(5, 7);
            binding.setdate.setText(Year + "-" + Month);
            if (!gYear.equals(gYear) || !bMonth.equals(gMonth)) {
                dlog.i("gYear : " + gYear);
                dlog.i("bYear : " + bYear);
                dlog.i("gMonth : " + gMonth);
                dlog.i("bMonth : " + bMonth);
                bYear = gYear;
                bMonth = gMonth;
            }
            WritePaymentList(change_place_id.equals("") ? place_id : change_place_id, change_member_id, binding.setdate.getText().toString(), Tap);
        });
        binding.nextDate.setOnClickListener(v -> {
            dlog.i("nextDate Click!! PaymentFragment");
            cal.add(Calendar.DATE, +1);
            toDay = sdf.format(cal.getTime());
            Year = toDay.substring(0, 4);
            Month = toDay.substring(5, 7);
            binding.setdate.setText(Year + "-" + Month);
            if (!gYear.equals(gYear) || !bMonth.equals(gMonth)) {
                dlog.i("gYear : " + gYear);
                dlog.i("bYear : " + bYear);
                dlog.i("gMonth : " + gMonth);
                dlog.i("bMonth : " + bMonth);
                bYear = gYear;
                bMonth = gMonth;
            }

            WritePaymentList(change_place_id.equals("") ? place_id : change_place_id, change_member_id, binding.setdate.getText().toString(), Tap);
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
                binding.setdate.setText(year + "-" + Month);
                getYMPicker = binding.setdate.getText().toString().substring(0, 7);
                WritePaymentList(change_place_id.equals("") ? place_id : change_place_id, change_member_id, binding.setdate.getText().toString(), Tap);
            }
        }, mYear, mMonth, mDay);

        binding.setdate.setOnClickListener(view -> {
            if (binding.setdate.isClickable()) {
                datePickerDialog.show();
            }
        });
    }

    public void Toast_Nomal(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_normal_toast, (ViewGroup) binding.getRoot().findViewById(R.id.toast_layout));
        TextView toast_textview = layout.findViewById(R.id.toast_textview);
        toast_textview.setText(String.valueOf(message));
        Toast toast = new Toast(mContext);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); //TODO 메시지가 표시되는 위치지정 (가운데 표시)
        //toast.setGravity(Gravity.TOP, 0, 0); //TODO 메시지가 표시되는 위치지정 (상단 표시)
        toast.setGravity(Gravity.BOTTOM, 0, 0); //TODO 메시지가 표시되는 위치지정 (하단 표시)
        toast.setDuration(Toast.LENGTH_SHORT); //메시지 표시 시간
        toast.setView(layout);
        toast.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(USER_INFO_AUTH.equals("1")){
            binding.tabLayout.setVisibility(View.GONE);
            binding.selectArea.setVisibility(View.GONE);
            change_place_id = place_id;
            change_member_id = USER_INFO_ID;
            binding.select01.setVisibility(View.GONE);
            ChangePage(1);
        }else{
            ChangePage(0);
        }
//        WritePaymentList(change_place_id.equals("") ? place_id : change_place_id, change_member_id, binding.setdate.getText().toString(), Tap);
    }

    @Override
    public void onStop() {
        super.onStop();
        shardpref.remove("Tap");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shardpref.remove("Tap");
    }
    // 직원 급여 명세서 리스트
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
//        String url = "http://krafte.net/NEBWorks/pay/paymanager.php?flag=1&place_id="+place_id+"&GET_DATE="+GET_DATE+"&user_id="+SelectId+"&basic_pay=&second_pay=&overwork_hour=&overwork_pay=&meal_allowance_yn=&meal_pay=&store_insurance_yn=&other_memo=&all_payment=&selectym=";
//        dlog.i("url : " + url);
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
                            binding.nodataArea.setVisibility(View.VISIBLE);
                            Log.i(TAG, "GET SIZE : " + rc.paymentData_lists.size());
                        } else {
                            binding.nodataArea.setVisibility(View.GONE);
                            for (int i = 0; i < Response.length(); i++) {
                                JSONObject jsonObject = Response.getJSONObject(i);
                                mAdapter.addItem(new PaymentData.PaymentData_list(
                                        jsonObject.getString("place_id"),
                                        jsonObject.getString("user_id"),
                                        jsonObject.getString("user_name"),
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
                                        jsonObject.getString("workday"),
                                        jsonObject.getString("workhour"),
                                        jsonObject.getString("total_workday"),
                                        jsonObject.getString("payment")
                                ));
                            }
                            mAdapter.notifyDataSetChanged();
                            mAdapter.setOnItemClickListener((v, position) -> {
                                try {
                                    shardpref.putString("select_month", binding.setdate.getText().toString().trim());
                                    shardpref.putString("select_user_id", Response.getJSONObject(position).getString("user_id"));
                                    shardpref.putString("select_place_id", Response.getJSONObject(position).getString("place_id"));
                                    shardpref.putString("select_user_name", Response.getJSONObject(position).getString("user_name"));
                                    shardpref.putString("select_total_payment", Response.getJSONObject(position).getString("total_pay"));
                                    shardpref.putString("select_workday", Response.getJSONObject(position).getString("workday"));
                                    shardpref.putString("select_total_workhour", Response.getJSONObject(position).getString("workhour"));
                                    shardpref.putString("select_payment", Response.getJSONObject(position).getString("payment"));
                                    shardpref.putString("select_GET_DATE", GET_DATE);

                                    dlog.i("-----mAdapter setOnItemClickListener-----");
                                    dlog.i("select_month : " + binding.setdate.getText().toString().trim());
                                    dlog.i("select_user_id : " + Response.getJSONObject(position).getString("user_id"));
                                    dlog.i("select_place_id : " + Response.getJSONObject(position).getString("place_id"));
                                    dlog.i("select_user_name : " + Response.getJSONObject(position).getString("user_name"));
                                    dlog.i("select_total_payment : " + Response.getJSONObject(position).getString("total_pay"));
                                    dlog.i("select_workday : " + Response.getJSONObject(position).getString("workday"));
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
}
