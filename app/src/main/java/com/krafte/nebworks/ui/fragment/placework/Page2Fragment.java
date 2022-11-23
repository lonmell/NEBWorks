package com.krafte.nebworks.ui.fragment.placework;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.Tap2ListAdapter;
import com.krafte.nebworks.adapter.Tap3ListAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.TodoReuseData;
import com.krafte.nebworks.data.TodolistData;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.TaskApprovalInterface;
import com.krafte.nebworks.dataInterface.TaskSelectMInterface;
import com.krafte.nebworks.dataInterface.TaskSelectWInterface;
import com.krafte.nebworks.pop.OneButtonPopActivity;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Page2Fragment extends Fragment {
    private final static String TAG = "Page1Fragment";

    Context mContext;
    Activity activity;

    //XML ID
    RecyclerView store_checklist;
    LinearLayout nodata_area, search_date;
    RelativeLayout login_alert_text, cnt_area;
    ImageView loading_view, user_manualimg, all_checkbox, workimg;
    TextView user_manualtv, selectdate, check_cnt, work_cnt, accept_btn;
    LinearLayout all_check;
    CardView accept_area;


    //sharedPreferences
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_EMAIL = "";

    //Other
    ArrayList<TodolistData.TodolistData_list> mList;
    Tap2ListAdapter mAdapter = null;
    ArrayList<TodoReuseData.TodoReuseData_list> mList2;
    Tap3ListAdapter mAdapter2 = null;

    RetrofitConnect rc = new RetrofitConnect();
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    DateCurrent dc = new DateCurrent();
    Dlog dlog = new Dlog();

    int listitemsize = 0;
    String totalSendCheck = "";
    String totalSendUser = "";
    boolean AllCheck = false;
    //미처리인 업무 세기
    int state_null = 0;

    public static Page2Fragment newInstance(){
        return new Page2Fragment();
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
    String USER_INFO_AUTH = "";
    String USER_INFO_PROFILE = "";
    String toDay = "";
    String writer_id = "";

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.worktap_fragment2, container, false);
        mContext = inflater.getContext();

        //XML
        store_checklist = rootView.findViewById(R.id.store_checklist);
        login_alert_text = rootView.findViewById(R.id.login_alert_text);
        loading_view = rootView.findViewById(R.id.loading_view);
        nodata_area = rootView.findViewById(R.id.nodata_area);
        user_manualimg = rootView.findViewById(R.id.user_manualimg);
        user_manualtv = rootView.findViewById(R.id.user_manualtv);
        selectdate = rootView.findViewById(R.id.selectdate);
        search_date = rootView.findViewById(R.id.search_date);
        all_check = rootView.findViewById(R.id.all_check);
        all_checkbox = rootView.findViewById(R.id.all_checkbox);
        check_cnt = rootView.findViewById(R.id.check_cnt);
        work_cnt = rootView.findViewById(R.id.work_cnt);
        cnt_area = rootView.findViewById(R.id.cnt_area);
        workimg = rootView.findViewById(R.id.workimg);
        accept_area = rootView.findViewById(R.id.accept_area);
        accept_btn = rootView.findViewById(R.id.accept_btn);

        shardpref = new PreferenceHelper(mContext);
        dlog.DlogContext(mContext);

        //Shared
        try {
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "0");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "-1"); //0-관리자 / 1- 근로자
            USER_INFO_PROFILE = shardpref.getString("USER_INFO_PROFILE", "0");
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
            shardpref.putInt("SELECT_POSITION", 1);
            user_manualtv.setText("할일 배정 카테고리에 등록된 할일이 없습니다.\n+ 를 터치하여 할일 등록 해 보세요.");
            dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
            toDay = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
            nodata_area.setVisibility(View.VISIBLE);

            selectdate.setText(toDay);
            //--selectdate 변경
//                shardpref.putInt("timeSelect_flag", 6);

//                Intent intent = new Intent(mContext, DatePickerActivity.class);
//                startActivity(intent);
//                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);

            Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    String Month = String.valueOf(month+1);
                    String Day = String.valueOf(dayOfMonth);
                    Day = Day.length()==1?"0"+Day:Day;
                    Month = Month.length()==1?"0"+Month:Month;
                    selectdate.setText(year +"-" + Month + "-" + Day);
                    setTodoWList();
                }
            }, mYear, mMonth, mDay);

            selectdate.setOnClickListener(view -> {
                if (selectdate.isClickable()) {
                    datePickerDialog.show();
                }
            });
            //--selectdate 변경

            Glide.with(mContext).load(USER_INFO_PROFILE)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(workimg);

            all_check.setOnClickListener(v -> {
//            mList.clear();
                dlog.i("-------------all_check-------------");
                if (!AllCheck) {
                    AllCheck = true;
                    totalSendCheck = "";
                    all_checkbox.setBackgroundResource(R.drawable.checkbox_on);
                    if (USER_INFO_AUTH.equals("0")) {
                        setTodoMList();
                        cnt_area.setVisibility(View.VISIBLE);
                        search_date.setVisibility(View.VISIBLE);
                        accept_area.setVisibility(View.VISIBLE);
                    } else {
                        setTodoWList();
                        cnt_area.setVisibility(View.VISIBLE);
                        search_date.setVisibility(View.VISIBLE);
                        accept_area.setVisibility(View.VISIBLE);
                    }
                } else {
                    AllCheck = false;
                    totalSendCheck = "";
                    shardpref.remove("checkworkno");
                    all_checkbox.setBackgroundResource(R.drawable.checkbox_off);
                    if (USER_INFO_AUTH.equals("0")) {
                        setTodoMList();
                        search_date.setVisibility(View.GONE);
                    } else {
                        setTodoWList();
                        search_date.setVisibility(View.VISIBLE);
                    }
                    check_cnt.setText("0건");
                }
                dlog.i("-------------all_check-------------");

            });
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }

        accept_btn.setOnClickListener(v -> {
            dlog.i("----------totalSendCheck-----------");
            String getArray = shardpref.getString("checkworkno", "").replace("[", "").replace("]", "").replace(" ", "").replace(" ,","").trim();
            dlog.i("getArray : " + getArray);
            List<String> total_array = new ArrayList<>(Arrays.asList(getArray.split(",")));
//            splitArray = getArray.split(",");
//            for (int i = 0; i < splitArray.length; i++) {
//                    dlog.i("splitArray[" + i + "] =" + splitArray[i]);
//                    total_array.add(splitArray[i]);
//            }
            totalSendCheck = String.valueOf(total_array).replace("[", "").replace("]", "").replace(" ", "").trim();
            dlog.i("totalSendCheck : " + totalSendCheck);
            dlog.i("----------totalSendCheck-----------");

            dlog.i("----------totalSendUser-----------");
            String getArray1 = shardpref.getString("checkworkno", "").replace("[", "").replace("]", "").replace(",", "").replace("  ", " ").trim();
            dlog.i("getArray1 : " + getArray1);
            List<String> splitArray1 = new ArrayList<>(Arrays.asList(getArray1.split(",")));
            dlog.i("splitArray1 : " + splitArray1.size());
            totalSendUser = splitArray1.toString().replace("[", "").replace("]", "").replace(" ", "");
            dlog.i("----------totalSendUser-----------");

            if (totalSendCheck.isEmpty()) {
                Intent intent = new Intent(mContext, OneButtonPopActivity.class);
                intent.putExtra("data", "선택한 업무가 없습니다.");
                intent.putExtra("left_btn_txt", "닫기");
                startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
            } else {
                setUpdateWorktodo(totalSendCheck);
            }
            totalSendCheck = "";
        });
        return rootView;
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


    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        shardpref.remove("task_no");
        shardpref.remove("writer_id");
        shardpref.remove("kind");        // 0:할일, 1:일정
        shardpref.remove("title");
        shardpref.remove("contents");
        shardpref.remove("complete_kind");            // 0:체크, 1:사진
        shardpref.remove("users");
        shardpref.remove("task_date");
        shardpref.remove("start_time");
        shardpref.remove("end_time");
        shardpref.remove("sun");
        shardpref.remove("mon");
        shardpref.remove("tue");
        shardpref.remove("wed");
        shardpref.remove("thu");
        shardpref.remove("fri");
        shardpref.remove("sat");
        shardpref.remove("img_path");
        shardpref.remove("complete_yn");
        shardpref.remove("incomplete_reason");
        shardpref.remove("make_kind");
        shardpref.remove("checkworkno");

        selectdate.setText(toDay);

        if (USER_INFO_AUTH.equals("0")) {
            setTodoMList();
            cnt_area.setVisibility(View.GONE);
            search_date.setVisibility(View.VISIBLE);
            accept_area.setVisibility(View.VISIBLE);
        } else {
            setTodoWList();
            cnt_area.setVisibility(View.VISIBLE);
            search_date.setVisibility(View.VISIBLE);
            accept_area.setVisibility(View.VISIBLE);
        }


    }


    public void setTodoMList() {
        dlog.i("setTodoMList place_id : " + place_id);
        state_null = 0;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TaskSelectMInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TaskSelectMInterface api = retrofit.create(TaskSelectMInterface.class);
        Call<String> call = api.getData(place_id,selectdate.getText().toString().trim());
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "WorkTapListFragment1 / setRecyclerView");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                Log.e(TAG, "response 2: " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    Log.e(TAG, "GetWorkStateInfo function onSuccess : " + response.body());
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(response.body());
                        mList = new ArrayList<>();
                        mAdapter = new Tap2ListAdapter(mContext, mList, getParentFragmentManager(), AllCheck);
                        store_checklist.setAdapter(mAdapter);
                        store_checklist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                        Log.i(TAG, "SetNoticeListview Thread run! ");
                        listitemsize = Response.length();
                        Log.i(TAG, "GET SIZE : " + Response.length());
                        if (Response.length() == 0) {
                            nodata_area.setVisibility(View.VISIBLE);
                            Log.i(TAG, "SetNoticeListview Thread run! ");
                            Log.i(TAG, "GET SIZE : " + Response.length());
                            check_cnt.setText("0건");
                            all_checkbox.setClickable(false);
                            all_checkbox.setEnabled(false);
                            all_checkbox.setBackgroundResource(R.drawable.checkbox_off);
                        } else {
                            nodata_area.setVisibility(View.GONE);
                            for (int i = 0; i < Response.length(); i++) {
                                JSONObject jsonObject = Response.getJSONObject(i);
                                if(!jsonObject.getString("id").isEmpty() || !jsonObject.getString("id").equals("null")){
                                    mAdapter.addItem(new TodolistData.TodolistData_list(
                                            jsonObject.getString("id"),
                                            jsonObject.getString("writer_id"),
                                            jsonObject.getString("kind"),
                                            jsonObject.getString("title"),
                                            jsonObject.getString("contents"),
                                            jsonObject.getString("complete_kind"),
                                            Collections.singletonList(jsonObject.getString("users")),
                                            jsonObject.getString("task_date"),
                                            jsonObject.getString("start_time"),
                                            jsonObject.getString("end_time"),
                                            jsonObject.getString("sun"),
                                            jsonObject.getString("mon"),
                                            jsonObject.getString("tue"),
                                            jsonObject.getString("wed"),
                                            jsonObject.getString("thu"),
                                            jsonObject.getString("fri"),
                                            jsonObject.getString("sat"),
                                            jsonObject.getString("img_path"),
                                            jsonObject.getString("complete_yn"),
                                            jsonObject.getString("incomplete_reason"),
                                            jsonObject.getString("approval_state"),
                                            jsonObject.getString("task_overdate")
                                    ));
                                }
                            }
                            for (int a = 0; a < Response.length(); a++) {
                                dlog.i("approval_state 1 : " + Response.getJSONObject(a).getString("approval_state"));
                                if (Response.getJSONObject(a).getString("approval_state").equals("3")
                                        || Response.getJSONObject(a).getString("approval_state").equals("null")) {
                                    if(!Response.getJSONObject(a).getString("id").isEmpty() || !Response.getJSONObject(a).getString("id").equals("null")){
                                        state_null++;
                                    }
                                }
                            }

                            check_cnt.setText(state_null + "건");

                            dlog.i("state_null : " + state_null);
                            work_cnt.setText(String.valueOf(state_null));
                            mAdapter.setOnItemClickListener((v, position, Tcnt, Fcnt) -> {
                                try {
                                    writer_id = Response.getJSONObject(position).getString("writer_id");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                check_cnt.setText(Tcnt + "건");
                                if (Tcnt == mList.size() && Fcnt == 0) {
                                    all_checkbox.setBackgroundResource(R.drawable.checkbox_on);
                                } else {
                                    all_checkbox.setBackgroundResource(R.drawable.checkbox_off);
                                }
                            });
                            mAdapter.notifyDataSetChanged();
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

    public void setTodoWList() {
        dlog.i("setTodoWList place_id : " + place_id);
        dlog.i("setTodoWList USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("setTodoWList select day : " + selectdate.getText().toString().trim());
        rc.placeNotiData_lists.clear();
        state_null = 0;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TaskSelectWInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TaskSelectWInterface api = retrofit.create(TaskSelectWInterface.class);
        Call<String> call = api.getData(place_id, USER_INFO_ID, selectdate.getText().toString().trim());
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "WorkTapListFragment2 / setRecyclerView");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                Log.e(TAG, "response 2: " + response.body());
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                    Log.e(TAG, "GetWorkStateInfo function onSuccess : " + response.body());
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(response.body());
                        mList = new ArrayList<>();
                        mAdapter = new Tap2ListAdapter(mContext, mList, getParentFragmentManager(), AllCheck);
                        store_checklist.setAdapter(mAdapter);
                        store_checklist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                        Log.i(TAG, "SetNoticeListview Thread run! ");
                        listitemsize = Response.length();
                        Log.i(TAG, "GET SIZE : " + rc.placeNotiData_lists.size());
                        if (Response.length() == 0) {
                            nodata_area.setVisibility(View.VISIBLE);
                            Log.i(TAG, "GET SIZE : " + rc.placeNotiData_lists.size());
                        } else {
                            nodata_area.setVisibility(View.GONE);
                            for (int i = 0; i < Response.length(); i++) {
                                JSONObject jsonObject = Response.getJSONObject(i);
                                if(!jsonObject.getString("id").isEmpty() || !jsonObject.getString("id").equals("null")){
                                    mAdapter.addItem(new TodolistData.TodolistData_list(
                                            jsonObject.getString("id"),
                                            jsonObject.getString("writer_id"),
                                            jsonObject.getString("kind"),
                                            jsonObject.getString("title"),
                                            jsonObject.getString("contents"),
                                            jsonObject.getString("complete_kind"),
                                            Collections.singletonList(jsonObject.getString("users")),
                                            jsonObject.getString("task_date"),
                                            jsonObject.getString("start_time"),
                                            jsonObject.getString("end_time"),
                                            jsonObject.getString("sun"),
                                            jsonObject.getString("mon"),
                                            jsonObject.getString("tue"),
                                            jsonObject.getString("wed"),
                                            jsonObject.getString("thu"),
                                            jsonObject.getString("fri"),
                                            jsonObject.getString("sat"),
                                            jsonObject.getString("img_path"),
                                            jsonObject.getString("complete_yn"),
                                            jsonObject.getString("incomplete_reason"),
                                            jsonObject.getString("approval_state"),
                                            jsonObject.getString("task_overdate")
                                    ));
                                }
                            }
                            for (int a = 0; a < Response.length(); a++) {
                                dlog.i("approval_state 2 : " + Response.getJSONObject(a).getString("approval_state"));
                                if (Response.getJSONObject(a).getString("approval_state").equals("3")
                                        || Response.getJSONObject(a).getString("approval_state").equals("null")) {
                                    if(!Response.getJSONObject(a).getString("id").isEmpty() || !Response.getJSONObject(a).getString("id").equals("null")){
                                        state_null++;
                                    }
                                }
                            }
                            check_cnt.setText(state_null + "건");
                            dlog.i("state_null : " + state_null);
                            work_cnt.setText(String.valueOf(state_null));
                            mAdapter.setOnItemClickListener((v, position, Tcnt, Fcnt) -> {
                                dlog.i("checkworkno : " + shardpref.getString("checkworkno",""));
                                try {
                                    writer_id = Response.getJSONObject(position).getString("writer_id");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                check_cnt.setText(Tcnt + "건");
                                if (Tcnt == mList.size() && Fcnt == 0) {
                                    all_checkbox.setBackgroundResource(R.drawable.checkbox_on);
                                } else {
                                    all_checkbox.setBackgroundResource(R.drawable.checkbox_off);
                                }
                            });
                            mAdapter.notifyDataSetChanged();
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


    private void setUpdateWorktodo(String task_id) {
        dlog.i("setUpdateWorktodo user_id : " + task_id);
        String task_date = selectdate.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TaskApprovalInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TaskApprovalInterface api = retrofit.create(TaskApprovalInterface.class);
        Call<String> call = api.getData(place_id, task_id, task_date, USER_INFO_ID);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activity.runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            dlog.i("setUpdateWorktodo jsonResponse length : " + response.body().length());
                            dlog.i("setUpdateWorktodo jsonResponse : " + response.body());
//                            dlog.i("http://krafte.net/kogas/task_approval/post.php?place_id="+place_id+"&task_id="+task_id+"&task_date="+task_date+"&user_id="+USER_INFO_ID);

                            if (response.body().replace("\"", "").equals("success")) {
                                if (USER_INFO_AUTH.equals("0")) {
                                    setTodoMList();
                                    cnt_area.setVisibility(View.VISIBLE);
                                    search_date.setVisibility(View.VISIBLE);
                                    accept_area.setVisibility(View.VISIBLE);
                                } else {
                                    setTodoWList();
                                    message = "[" + place_name + "] 완료 된 업무보고가 있습니다.";
                                    dlog.i("place_owner_id :" +place_owner_id);
                                    getManagerToken(place_owner_id,"0", place_id,place_name);
                                    cnt_area.setVisibility(View.VISIBLE);
                                    search_date.setVisibility(View.VISIBLE);
                                    accept_area.setVisibility(View.VISIBLE);
                                }
                                Toast.makeText(mContext, "결재 요청이 완료되었습니다.", Toast.LENGTH_SHORT).show();
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
    String message = "";
    /* -- 할일 추가 FCM 전송 영역 */
    public void getManagerToken(String user_id, String type, String place_id, String place_name) {
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
                dlog.i("getManagerToken Response Result : " + response.body());
                try {
                    JSONArray Response = new JSONArray(response.body());
                    if(Response.length() > 0){
                        dlog.i("-----getManagerToken-----");
                        dlog.i("user_id : " + Response.getJSONObject(0).getString("user_id"));
                        dlog.i("token : " + Response.getJSONObject(0).getString("token"));
                        String id = Response.getJSONObject(0).getString("id");
                        String token = Response.getJSONObject(0).getString("token");
                        String department = shardpref.getString("USER_INFO_SOSOK","");
                        String position = shardpref.getString("USER_INFO_JIKGUP","");
                        String name = shardpref.getString("USER_INFO_NAME","");
                        dlog.i("-----getManagerToken-----");
                        boolean channelId1 = Response.getJSONObject(0).getString("channel1").equals("1");
                        if (!token.isEmpty() && channelId1) {
                            PushFcmSend(id, "", message, token, "0", place_id);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e( "에러 = " + t.getMessage());
            }
        });
    }

    DBConnection dbConnection = new DBConnection();
    String click_action = "";
    private void PushFcmSend(String topic,String title, String message, String token,String tag, String place_id) {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            click_action = "TaskApprovalFragment";
            dbConnection.FcmTestFunction(topic,"",message,token,click_action,tag,place_id);
            activity.runOnUiThread(() -> {
            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /* -- 할일 추가 FCM 전송 영역 */
}
