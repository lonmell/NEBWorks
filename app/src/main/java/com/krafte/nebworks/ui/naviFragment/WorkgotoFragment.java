package com.krafte.nebworks.ui.naviFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.ViewPagerFregmentAdapter;
import com.krafte.nebworks.adapter.WorkStatusCalenderAdapter;
import com.krafte.nebworks.bottomsheet.PaySelectMemberActivity;
import com.krafte.nebworks.bottomsheet.PaySelectPlaceActivity;
import com.krafte.nebworks.bottomsheet.TaskAddOption;
import com.krafte.nebworks.data.CalendarSetStatusData;
import com.krafte.nebworks.data.WorkCalenderData;
import com.krafte.nebworks.dataInterface.WorkCalenderInterface;
import com.krafte.nebworks.dataInterface.WorkstatusCalendersetData;
import com.krafte.nebworks.databinding.WorkgotofragmentBinding;
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
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class WorkgotoFragment extends Fragment {
    private final static String TAG = "WorkgotoFragment";
    private WorkgotofragmentBinding binding;
    Context mContext;

    Activity activity;

    //Other 클래스
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    Handler handler = new Handler();
    RetrofitConnect rc = new RetrofitConnect();
    ViewPagerFregmentAdapter viewPagerFregmentAdapter;

    WorkStatusCalenderAdapter mAdapter;
    ArrayList<WorkCalenderData.WorkCalenderData_list> mList;
    //Task all data
    ArrayList<CalendarSetStatusData.CalendarSetStatusData_list> mList2 = new ArrayList<>();

    //shared
    String place_id = "";
    String place_name = "";
    String place_owner_id = "";
    String place_owner_name = "";
    String place_management_office = "";
    String place_address = "";
    String place_latitude = "";
    String place_longitude = "";
    String place_start_time = "";
    String place_end_time = "";
    String place_img_path = "";
    String place_start_date = "";
    String place_created_at = "";
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";

    String return_page = "";
    int SELECT_POSITION = 0;
    int SELECT_POSITION_sub = 0;
    int rotate_addwork = 0;
    String tap_kind = "";
    Fragment fg;

    boolean chng_icon = false;
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

    String change_place_id = "";
    String change_place_name = "";
    String change_member_id = "";
    String change_member_name = "";

    public static WorkgotoFragment newInstance(int number) {
        WorkgotoFragment fragment = new WorkgotoFragment();
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
//        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.workgotofragment, container, false);
        binding = WorkgotofragmentBinding.inflate(inflater);
        mContext = inflater.getContext();
        //UI 데이터 세팅
        try {
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);

            place_id = shardpref.getString("place_id", "0");
            place_name = shardpref.getString("place_name", "0");
            place_owner_id = shardpref.getString("place_owner_id", "0");
            place_owner_name = shardpref.getString("place_owner_name", "0");
            place_management_office = shardpref.getString("place_management_office", "0");
            place_address = shardpref.getString("place_address", "0");
            place_latitude = shardpref.getString("place_latitude", "0");
            place_longitude = shardpref.getString("place_longitude", "0");
            place_start_time = shardpref.getString("place_start_time", "0");
            place_end_time = shardpref.getString("place_end_time", "0");
            place_img_path = shardpref.getString("place_img_path", "0");
            place_start_date = shardpref.getString("place_start_date", "0");
            place_created_at = shardpref.getString("place_created_at", "0");
            SELECT_POSITION = shardpref.getInt("SELECT_POSITION", 0);
            SELECT_POSITION_sub = shardpref.getInt("SELECT_POSITION_sub",0);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH","-1"); //0-관리자 / 1- 근로자
            return_page = shardpref.getString("return_page","");
            setBtnEvent();



            dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
            dlog.i("PlaceWorkActivity SELECT_POSITION_sub : " + SELECT_POSITION_sub);
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        shardpref.remove("picker_year");
        shardpref.remove("picker_month");
        shardpref.remove("picker_day");
        shardpref.remove("input_pop_time");
        shardpref.remove("SET_TASK_TIME_VALUE");
        shardpref.remove("yoillist");
        shardpref.remove("overdate");
        shardpref.remove("item_user_id");
        shardpref.remove("item_user_name");
        shardpref.remove("item_user_img");
        shardpref.remove("item_user_position");
        setAddBtnSetting();
    }

    public void setBtnEvent() {
        cal = Calendar.getInstance();
        toDay = sdf.format(cal.getTime());
        dlog.i("오늘 :" + toDay);
        binding.setdate.setText(toDay);
        Year = toDay.substring(0, 4);
        Month = toDay.substring(5, 7);
        binding.setdate.setText(Year + "년 " + Month + "월");

        binding.prevDate.setOnClickListener(v -> {
            cal.add(Calendar.DATE, -1);
            toDay = sdf.format(cal.getTime());
            shardpref.putString("FtoDay",toDay);
            gYear = toDay.substring(0,4);
            gMonth = toDay.substring(5,7);
            binding.setdate.setText("   " + gYear + "년 " + gMonth + "월   ");
            if(!gYear.equals(gYear) || !bMonth.equals(gMonth)){
                dlog.i("gYear : " + gYear);
                dlog.i("bYear : " + bYear);
                dlog.i("gMonth : " + gMonth);
                dlog.i("bMonth : " + bMonth);
                bYear = gYear;
                bMonth = gMonth;
                SetCalenderData(gYear,gMonth);
            }
        });
        binding.nextDate.setOnClickListener(v -> {
            cal.add(Calendar.DATE, +1);
            toDay = sdf.format(cal.getTime());
            shardpref.putString("FtoDay",toDay);
            gYear = toDay.substring(0,4);
            gMonth = toDay.substring(5,7);
            binding.setdate.setText("   " + gYear + "년 " + gMonth + "월   ");
            if(!gYear.equals(gYear) || !bMonth.equals(gMonth)){
                dlog.i("gYear : " + gYear);
                dlog.i("bYear : " + bYear);
                dlog.i("gMonth : " + gMonth);
                dlog.i("bMonth : " + bMonth);
                bYear = gYear;
                bMonth = gMonth;
                SetCalenderData(gYear,gMonth);
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
                Month = String.valueOf(month+1);
                Day = String.valueOf(dayOfMonth);
                Day = Day.length()==1?"0"+Day:Day;
                Month = Month.length()==1?"0"+Month:Month;
                binding.setdate.setText("   " + Year + "년 " + Month + "월   ");
                getYMPicker = binding.setdate.getText().toString().substring(0,7);
                SetCalenderData(gYear,gMonth);
            }
        }, mYear, mMonth, mDay);

        binding.setdate.setOnClickListener(view -> {
            if (binding.setdate.isClickable()) {
                datePickerDialog.show();
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
//                        WritePaymentList(change_place_id.equals("") ? place_id : change_place_id, change_member_id, binding.setdate.getText().toString());
                }
            });
        });

        binding.changeMember.setOnClickListener(v -> {
            PaySelectMemberActivity psm = new PaySelectMemberActivity();
            psm.show(getParentFragmentManager(), "PaySelectMemberActivity");
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
//                        WritePaymentList(change_place_id.equals("") ? place_id : change_place_id, change_member_id, binding.setdate.getText().toString());
                }
            });
        });

        binding.changeIcon.setOnClickListener(v -> {
            if(!chng_icon){
                chng_icon = true;
                binding.calendarArea.setVisibility(View.VISIBLE);
                binding.changeIcon.setBackgroundResource(R.drawable.list_up_icon);
                SetCalenderData(gYear,gMonth);
            }else{
                chng_icon = false;
                binding.calendarArea.setVisibility(View.GONE);
                binding.changeIcon.setBackgroundResource(R.drawable.calendar_resize);
            }
        });
    }

    private void SetCalenderData(String Year,String Month){
        mList2.clear();
        dlog.i("------SetCalenderData------");
        dlog.i("place_id :" + place_id);
        dlog.i("USER_INFO_ID :" + USER_INFO_ID);
        dlog.i("getYMPicker :" + getYMPicker);
        dlog.i("------SetCalenderData------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WorkstatusCalendersetData.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        WorkstatusCalendersetData api = retrofit.create(WorkstatusCalendersetData.class);
        Call<String> call2 = api.getData(place_id, USER_INFO_ID, Year,Month);
        call2.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call2, @NonNull Response<String> response2) {
                Log.e(TAG, "SetCalenderData function START");
                Log.e(TAG, "response 1: " + response2.isSuccessful());
                Log.e(TAG, "response 2: " + (response2.body() != null ? response2.body().length() : 0));
                Log.e(TAG, "response 3: " + response2.body());
                activity.runOnUiThread(() -> {
                    //캘린더 내용 (업무가) 있을때
                    if (response2.isSuccessful() && response2.body() != null) {
                        try {
                            JSONArray Response2 = new JSONArray(response2.body());
                            if (Response2.length() == 0) {
                                dlog.i("GET SIZE : " + Response2.length());
                                GetCalenderList(Year, Month, mList2);
                            } else {
                                for (int i = 0; i < Response2.length(); i++) {
                                    JSONObject jsonObject = Response2.getJSONObject(i);
                                    mList2.add(new CalendarSetStatusData.CalendarSetStatusData_list(
                                            jsonObject.getString("day"),
                                            jsonObject.getString("week"),
                                            Collections.singletonList(jsonObject.getString("users"))
                                    ));
                                }
                                GetCalenderList(Year, Month, mList2);
                            }

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

    ArrayList<String> user_id = new ArrayList<>();
    ArrayList<String> user_name = new ArrayList<>();
    ArrayList<String> img_path = new ArrayList<>();
    ArrayList<String> jikgup = new ArrayList<>();
    ArrayList<String> worktime = new ArrayList<>();
    ArrayList<String> workyoil = new ArrayList<>();
    public void GetCalenderList(String Year, String Month, ArrayList<CalendarSetStatusData.CalendarSetStatusData_list> mList2) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WorkCalenderInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        WorkCalenderInterface api = retrofit.create(WorkCalenderInterface.class);
        Call<String> call = api.getData(Year,Month);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "GetCalenderList function START");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                Log.e(TAG, "response 2: " + response.body());
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        dlog.i("onResume place_id :" + place_id);
                        dlog.i("onResume USER_INFO_ID :" + USER_INFO_ID);
                        dlog.i("onResume getYMPicker :" + getYMPicker);
                        dlog.i("onResume mList2 :" + mList2);
                        try{
                            String select_date = Year + "-" + Month;
                            JSONArray Response = new JSONArray(response.body());
                            mList = new ArrayList<>();
                            mAdapter = new WorkStatusCalenderAdapter(mContext, mList, mList2, place_id, USER_INFO_ID, select_date);
                            binding.createCalender.setAdapter(mAdapter);
                            binding.createCalender.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                            dlog.i("SetNoticeListview Thread run! ");

                            if (Response.length() == 0) {
                                dlog.i("GET SIZE : " + Response.length());
                            } else {
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    mAdapter.addItem(new WorkCalenderData.WorkCalenderData_list(
                                            jsonObject.getString("ym"),
                                            jsonObject.getString("Sun"),
                                            jsonObject.getString("Mon"),
                                            jsonObject.getString("Tue"),
                                            jsonObject.getString("Wed"),
                                            jsonObject.getString("Thu"),
                                            jsonObject.getString("Fri"),
                                            jsonObject.getString("Sat")
                                    ));
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        }catch (JSONException e){
                            dlog.i("JSONException :" + e);
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

    CardView add_worktime_btn;
    TextView addbtn_tv;
    private void setAddBtnSetting() {
        add_worktime_btn = binding.getRoot().findViewById(R.id.add_worktime_btn);
        addbtn_tv = binding.getRoot().findViewById(R.id.addbtn_tv);
        addbtn_tv.setText("할일추가");
        add_worktime_btn.setOnClickListener(v -> {
            TaskAddOption to = new TaskAddOption();
            to.show(getChildFragmentManager(),"TaskAddOption");
        });
    }
    public void Toast_Nomal(String message){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_normal_toast, null);
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
}
