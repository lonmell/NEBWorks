package com.krafte.nebworks.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.adapter.ApprovalAdapter;
import com.krafte.nebworks.adapter.PayCalenderAdapter;
import com.krafte.nebworks.adapter.WorkCalenderAdapter;
import com.krafte.nebworks.adapter.WorkStatusCalenderAdapter;
import com.krafte.nebworks.bottomsheet.WorkgotoBottomSheet;
import com.krafte.nebworks.bottomsheet.WorkstatusBottomSheet;
import com.krafte.nebworks.data.CalendarSetData;
import com.krafte.nebworks.data.CalendarSetStatusData;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.TaskCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.data.WorkCalenderData;
import com.krafte.nebworks.dataInterface.PayCalendersetData;
import com.krafte.nebworks.dataInterface.WorkCalenderInterface;
import com.krafte.nebworks.dataInterface.WorkCalendersetData;
import com.krafte.nebworks.dataInterface.WorkstatusCalendersetData;
import com.krafte.nebworks.databinding.FragmentCalenderBinding;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class CalenderFragment extends Fragment {
    private final static String TAG = "CalenderFragment";
    String USER_INFO_ID = "";
    String place_id = "";
    DateCurrent dc = new DateCurrent();

    PreferenceHelper shardpref;
    RetrofitConnect rc = new RetrofitConnect();
    WorkCalenderAdapter workCalenderAdapter;
    WorkStatusCalenderAdapter workStatusCalenderAdapter;
    Dlog dlog = new Dlog();
    private FragmentCalenderBinding binding = null;

    Context mContext;
    Activity activity;
    String year;
    String month;
    int state;

    String change_place_id = "";
    String change_member_id = "";

    ArrayList<WorkCalenderData.WorkCalenderData_list> workGotoList;
    ArrayList<CalendarSetData.CalendarSetData_list> workGotoList2 = new ArrayList<>();

    ArrayList<WorkCalenderData.WorkCalenderData_list> workStatusList;
    //Task all data
    ArrayList<CalendarSetStatusData.CalendarSetStatusData_list> workStatusList2 = new ArrayList<>();

    ArrayList<CalendarSetData.CalendarSetData_list> ApprovalList = new ArrayList<>();
    ArrayList<WorkCalenderData.WorkCalenderData_list> ApprovalList2;
    WorkCalenderAdapter ApprovalAdapter;

    PayCalenderAdapter payAdapter;
    ArrayList<WorkCalenderData.WorkCalenderData_list> payList = new ArrayList<>();
    //Task all data
    ArrayList<CalendarSetStatusData.CalendarSetStatusData_list> payList2 = new ArrayList<>();

    // state 1: WorkGoto
    public CalenderFragment(String year, String month, int state) {
        this.year = year;
        this.month = month;
        this.state = state;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCalenderBinding.inflate(inflater);
        mContext = inflater.getContext();
        shardpref = new PreferenceHelper(mContext);
        dlog.DlogContext(mContext);

        USER_INFO_ID        = UserCheckData.getInstance().getUser_id();
        place_id            = PlaceCheckData.getInstance().getPlace_id();

        shardpref.putString("calendar_year", year);
        shardpref.putString("calendar_month", month);

        binding.calendarYear.setText(year + "년");
        binding.calendarMonth.setText(month + "월");

        Log.d(TAG, "state: " + state);
        switch (state) {
            case 1:
                SetWorkGotoCalenderData();
                break;
            case 2:
                SetWorkStatusCalenderData(year, month);
                break;
            case 3:
                SetApprovalCalenderData();
                break;
            case 4:
                SetPayCalenderData(year, month);
                break;
        }
        return binding.getRoot();
    }

    private void SetWorkGotoCalenderData() {
        workGotoList2.clear();
        String getYMPicker = year + "-" + month;
        Log.i(TAG, "------SetWorkGotoCalenderData------");
        Log.i(TAG, "place_id : " + place_id);
        Log.i(TAG, "USER_INFO_ID : " + USER_INFO_ID);
        Log.i(TAG, "select_date : " + getYMPicker);
        Log.i(TAG, "------SetWorkGotoCalenderData------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WorkCalendersetData.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        WorkCalendersetData api = retrofit.create(WorkCalendersetData.class);
        Call<String> call2 = api.getData(place_id, USER_INFO_ID, getYMPicker);
        call2.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call2, @NonNull Response<String> response2) {
                activity.runOnUiThread(() -> {
                    //캘린더 내용 (업무가) 있을때
                    if (response2.isSuccessful() && response2.body() != null) {
                        String jsonResponse = rc.getBase64decode(response2.body());
                        Log.i(TAG, "jsonResponse length : " + jsonResponse.length());
                        Log.i(TAG, "jsonResponse : " + jsonResponse);
                        Log.i(TAG, "SetWorkGotoCalenderData function START");
                        try {
                            JSONArray Response2 = new JSONArray(jsonResponse);
                            if (Response2.length() == 0) {
                                Log.i(TAG, "GET SIZE : " + Response2.length());
                                GetWorkGotoCalenderList(year, month, workGotoList2);
                            } else {
                                for (int i = 0; i < Response2.length(); i++) {
                                    JSONObject jsonObject = Response2.getJSONObject(i);
                                    workGotoList2.add(new CalendarSetData.CalendarSetData_list(
                                            jsonObject.getString("day"),
                                            jsonObject.getString("week"),
                                            Collections.singletonList(jsonObject.getString("task"))
                                    ));
                                }
                                GetWorkGotoCalenderList(year, month, workGotoList2);
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

    ArrayList<String> kind = new ArrayList<>();
    ArrayList<String> title = new ArrayList<>();
    public void GetWorkGotoCalenderList(String Year, String Month, ArrayList<CalendarSetData.CalendarSetData_list> mList2) {
        Log.d(TAG, year);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WorkCalenderInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        WorkCalenderInterface api = retrofit.create(WorkCalenderInterface.class);
        Call<String> call = api.getData(Year, Month);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "GetWorkGotoCalenderList function START");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                Log.e(TAG, "response 2: " + response.body());
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.i(TAG,"onResume place_id :" + place_id);
                        Log.i(TAG,"onResume USER_INFO_ID :" + USER_INFO_ID);
                        Log.i(TAG,"onResume mList2 :" + mList2);
                        try {
                            String select_date = Year + "-" + Month;
                            JSONArray Response = new JSONArray(response.body());
                            workGotoList = new ArrayList<>();
                            workCalenderAdapter = new WorkCalenderAdapter(mContext, workGotoList, mList2, place_id, USER_INFO_ID, select_date, Month);
                            binding.createCalender.setAdapter(workCalenderAdapter);
                            binding.createCalender.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                            Log.i(TAG, "SetNoticeListview Thread run! ");

                            if (Response.length() == 0) {
                                Log.i(TAG, "GET SIZE : " + Response.length());
                            } else {
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    workCalenderAdapter.addItem(new WorkCalenderData.WorkCalenderData_list(
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
                                workCalenderAdapter.notifyDataSetChanged();
                                workCalenderAdapter.setOnItemClickListener(new WorkCalenderAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position, String data, String yoil, String WorkDay) {
                                        Log.i(TAG, "data :" + data);
                                        try {
                                                kind = new ArrayList<>();
                                                title = new ArrayList<>();
                                                for (int i = 0; i < mList2.size(); i++) {
                                                    if (data.equals(mList2.get(i).getDay().length() == 1 ? "0" + mList2.get(i).getDay() : mList2.get(i).getDay())) {
                                                        JSONArray Response = new JSONArray(mList2.get(i).getTask().toString().replace("[[", "[").replace("]]", "]"));
                                                        for (int i3 = 0; i3 < Response.length(); i3++) {
                                                            JSONObject jsonObject = Response.getJSONObject(i3);
                                                            kind.add(jsonObject.getString("kind"));
                                                            title.add(jsonObject.getString("title"));
                                                        }
                                                    }
                                                }
                                                shardpref.putString("task_date", WorkDay);
                                                Log.i(TAG, "WorkDay :" + WorkDay);

                                                shardpref.putString("change_place_id", change_place_id.isEmpty() ? place_id : change_place_id);
                                                shardpref.putString("change_member_id", change_member_id.isEmpty() ? "" : change_member_id);
//                                            Intent intent = new Intent(mContext, TaskListPopActivity.class);
//                                            intent.putStringArrayListExtra("kind", kind);
//                                            intent.putStringArrayListExtra("title", title);
//                                            intent.putExtra("date", data);
//                                            intent.putExtra("yoil", yoil);
//                                            intent.putExtra("write_name","");
//                                            startActivity(intent);
//                                            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                                            if (!WorkDay.contains("null")) {
                                                WorkgotoBottomSheet wgb = new WorkgotoBottomSheet();
                                                wgb.show(getChildFragmentManager(), "WorkgotoBottomSheet");
                                            }

                                        } catch (Exception e) {
                                            Log.i(TAG, "onItemClick Exception :" + e);
                                        }

                                    }
                                });
//                                if (USER_INFO_AUTH.equals("0")) {
//                                    //관리자일때
//                                    binding.addWorkBtn.setVisibility(View.VISIBLE);
//                                } else {
//                                    //근로자일때때
//                                    binding.addWorkBtn.setVisibility(View.GONE);
//                                }
                            }
                        } catch (JSONException e) {
                            Log.i(TAG, "JSONException :" + e);
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

    private void SetWorkStatusCalenderData(String Year, String Month) {
        workGotoList2.clear();
        String getYMPicker = year + "-" + month;
        dlog.i("------SetWorkStatusCalenderData------");
        dlog.i("place_id :" + place_id);
        dlog.i("USER_INFO_ID :" + USER_INFO_ID);
        dlog.i("getYMPicker :" + getYMPicker);
        dlog.i("------SetWorkStatusCalenderData------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WorkstatusCalendersetData.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        WorkstatusCalendersetData api = retrofit.create(WorkstatusCalendersetData.class);
        Call<String> call2 = api.getData(place_id, USER_INFO_ID, Year, Month);
        call2.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call2, @NonNull Response<String> response2) {
                activity.runOnUiThread(() -> {
                    //캘린더 내용 (업무가) 있을때
                    if (response2.isSuccessful() && response2.body() != null) {
                        String jsonResponse = rc.getBase64decode(response2.body());
                        dlog.i("jsonResponse length : " + jsonResponse.length());
                        dlog.i("jsonResponse : " + jsonResponse);
                        try {
                            JSONArray Response2 = new JSONArray(jsonResponse);
                            if (Response2.length() == 0) {
                                dlog.i("GET SIZE : " + Response2.length());
                                GetWorkStatusCalenderList(Year, Month, workStatusList2);
                            } else {
                                for (int i = 0; i < Response2.length(); i++) {
                                    JSONObject jsonObject = Response2.getJSONObject(i);
                                    workStatusList2.add(new CalendarSetStatusData.CalendarSetStatusData_list(
                                            jsonObject.getString("day"),
                                            jsonObject.getString("week"),
                                            Collections.singletonList(jsonObject.getString("users"))
                                    ));
                                }
                                GetWorkStatusCalenderList(Year, Month, workStatusList2);
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

    public void GetWorkStatusCalenderList(String Year, String Month, ArrayList<CalendarSetStatusData.CalendarSetStatusData_list> mList2) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WorkCalenderInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        WorkCalenderInterface api = retrofit.create(WorkCalenderInterface.class);
        Call<String> call = api.getData(Year, Month);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "GetCalenderList function START");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                Log.e(TAG, "response 2: " + response.body());
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
//                        String jsonResponse = rc.getBase64decode(response.body());
//                        dlog.i("jsonResponse length : " + jsonResponse.length());
//                        dlog.i("jsonResponse : " + jsonResponse);
                        dlog.i("onResume place_id :" + place_id);
                        dlog.i("onResume USER_INFO_ID :" + USER_INFO_ID);
//                        dlog.i("onResume getYMPicker :" + getYMPicker);
                        dlog.i("onResume mList2 :" + mList2);
                        try {
                            String select_date = Year + "-" + Month;
                            JSONArray Response = new JSONArray(response.body());
                            workGotoList = new ArrayList<>();
                            workStatusCalenderAdapter = new WorkStatusCalenderAdapter(mContext, workGotoList, mList2, place_id, USER_INFO_ID, select_date, Month);
                            binding.createCalender.setAdapter(workStatusCalenderAdapter);
                            binding.createCalender.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                            dlog.i("SetNoticeListview Thread run! ");

                            if (Response.length() == 0) {
                                dlog.i("GET SIZE : " + Response.length());
                            } else {
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    workStatusCalenderAdapter.addItem(new WorkCalenderData.WorkCalenderData_list(
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
                                workStatusCalenderAdapter.notifyDataSetChanged();
                                workStatusCalenderAdapter.setOnItemClickListener(new WorkStatusCalenderAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position, String data, String yoil, String WorkDay) {
                                        try {
                                            dlog.i("onItemClick WorkDay :" + WorkDay);
                                            shardpref.putString("FtoDay", WorkDay);
                                            if (!WorkDay.contains("null")) {
                                                WorkstatusBottomSheet wgb = new WorkstatusBottomSheet();
                                                wgb.show(getChildFragmentManager(), "WorkstatusBottomSheet");
                                            }
                                        } catch (Exception e) {
                                            dlog.i("onItemClick Exception :" + e);
                                        }

                                    }
                                });
                            }
                        } catch (JSONException e) {
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

    private void SetApprovalCalenderData() {
        ApprovalList.clear();
        String getYMPicker = year + "-" + month;
        dlog.i("------SetCalenderData------");
        dlog.i("place_id : " + change_place_id);
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("select_date : " + getYMPicker);
//        dlog.i("select_date2 : " + binding.setdate.getText().toString().replace("년 ","-").replace("월 ","-").replace("일","").substring(0,7));
        dlog.i("------SetCalenderData------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WorkCalendersetData.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        WorkCalendersetData api = retrofit.create(WorkCalendersetData.class);
        Call<String> call2 = api.getData(change_place_id, USER_INFO_ID, getYMPicker);
        call2.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call2, @NonNull Response<String> response2) {
                activity.runOnUiThread(() -> {
                    //캘린더 내용 (업무가) 있을때
                    if (response2.isSuccessful() && response2.body() != null) {
                        String jsonResponse = rc.getBase64decode(response2.body());
                        dlog.i("jsonResponse length : " + jsonResponse.length());
                        dlog.i("jsonResponse : " + jsonResponse);
                        dlog.i("SetApprovalCalenderData function START");
                        try {
                            JSONArray Response2 = new JSONArray(jsonResponse);
                            if (Response2.length() == 0) {
                                dlog.i("GET SIZE : " + Response2.length());
                                GetApprovalCalenderList(year, month, ApprovalList, getYMPicker);
                            } else {
                                for (int i = 0; i < Response2.length(); i++) {
                                    JSONObject jsonObject = Response2.getJSONObject(i);
                                    ApprovalList.add(new CalendarSetData.CalendarSetData_list(
                                            jsonObject.getString("day"),
                                            jsonObject.getString("week"),
                                            Collections.singletonList(jsonObject.getString("task"))
                                    ));
                                }
                                GetApprovalCalenderList(year, month, ApprovalList, getYMPicker);
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

    ArrayList<String> akind = new ArrayList<>();
    ArrayList<String> atitle = new ArrayList<>();
    public void GetApprovalCalenderList(String Year, String Month, ArrayList<CalendarSetData.CalendarSetData_list> mList2, String date) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WorkCalenderInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        WorkCalenderInterface api = retrofit.create(WorkCalenderInterface.class);
        Call<String> call = api.getData(Year, Month);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "GetApprovalCalenderList function START");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                Log.e(TAG, "response 2: " + response.body());
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        dlog.i("onResume place_id :" + place_id);
                        dlog.i("onResume USER_INFO_ID :" + USER_INFO_ID);
//                        dlog.i("onResume getYMPicker :" + getYMPicker);
                        dlog.i("onResume mList2 :" + mList2);
                        try {
                            String select_date = Year + "-" + Month;
                            JSONArray Response = new JSONArray(response.body());
                            ApprovalList2 = new ArrayList<>();
                            ApprovalAdapter = new WorkCalenderAdapter(mContext, ApprovalList2, ApprovalList, place_id, USER_INFO_ID, select_date, Month);
                            binding.createCalender.setAdapter(ApprovalAdapter);
                            binding.createCalender.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                            dlog.i("SetNoticeListview Thread run! ");

                            if (Response.length() == 0) {
                                dlog.i("GET SIZE : " + Response.length());
                            } else {
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    ApprovalAdapter.addItem(new WorkCalenderData.WorkCalenderData_list(
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
                                ApprovalAdapter.notifyDataSetChanged();
                                ApprovalAdapter.setOnItemClickListener(new WorkCalenderAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position, String data, String yoil, String WorkDay) {
                                        dlog.i("data :" + data);
                                        try {
                                            akind = new ArrayList<>();
                                            atitle = new ArrayList<>();
                                            for (int i = 0; i < mList2.size(); i++) {
                                                if (data.equals(mList2.get(i).getDay().length() == 1 ? "0" + mList2.get(i).getDay() : mList2.get(i).getDay())) {
                                                    JSONArray Response = new JSONArray(mList2.get(i).getTask().toString().replace("[[", "[").replace("]]", "]"));
                                                    for (int i3 = 0; i3 < Response.length(); i3++) {
                                                        JSONObject jsonObject = Response.getJSONObject(i3);
                                                        akind.add(jsonObject.getString("kind"));
                                                        atitle.add(jsonObject.getString("title"));
                                                    }
                                                }
                                            }
                                            shardpref.putString("task_date", WorkDay);
                                            dlog.i("WorkDay :" + WorkDay);

                                            shardpref.putString("change_place_id", change_place_id.isEmpty() ? place_id : change_place_id);
                                            shardpref.putString("change_member_id", change_member_id.isEmpty() ? "" : change_member_id);
//                                            Intent intent = new Intent(mContext, TaskListPopActivity.class);
//                                            intent.putStringArrayListExtra("kind", kind);
//                                            intent.putStringArrayListExtra("title", title);
//                                            intent.putExtra("date", data);
//                                            intent.putExtra("yoil", yoil);
//                                            intent.putExtra("write_name","");
//                                            startActivity(intent);
//                                            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                                            if (!WorkDay.contains("null")) {
                                                WorkgotoBottomSheet wgb = new WorkgotoBottomSheet();
                                                wgb.show(getChildFragmentManager(), "WorkgotoBottomSheet");
                                            }

                                        } catch (Exception e) {
                                            dlog.i("onItemClick Exception :" + e);
                                        }

                                    }
                                });
//                                if (USER_INFO_AUTH.equals("0")) {
//                                    //관리자일때
//                                    binding.addWorkBtn.setVisibility(View.VISIBLE);
//                                } else {
//                                    //근로자일때때
//                                    binding.addWorkBtn.setVisibility(View.GONE);
//                                }
                            }
                        } catch (JSONException e) {
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

    private void SetPayCalenderData(String Year, String Month) {
        dlog.i("------SetPayCalenderData------");
        dlog.i("place_id :" + place_id);
        dlog.i("USER_INFO_ID :" + USER_INFO_ID);
        dlog.i("------SetPayCalenderData------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PayCalendersetData.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PayCalendersetData api = retrofit.create(PayCalendersetData.class);
        Call<String> call2 = api.getData(place_id, "", Year, Month);
        call2.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call2, @NonNull Response<String> response2) {
                activity.runOnUiThread(() -> {
                    //캘린더 내용 (업무가) 있을때
                    if (response2.isSuccessful() && response2.body() != null) {
                        String jsonResponse = rc.getBase64decode(response2.body());
                        dlog.i("SetPayCalenderData jsonResponse length : " + jsonResponse.length());
                        dlog.i("SetPayCalenderData jsonResponse : " + jsonResponse);
                        try {
                            JSONArray Response2 = new JSONArray(jsonResponse);
                            if (Response2.length() == 0) {
                                dlog.i("SetPayCalenderData GET SIZE : " + Response2.length());
                                GetPayCalenderList(Year, Month, payList2);
                            } else {
                                for (int i = 0; i < Response2.length(); i++) {
                                    JSONObject jsonObject = Response2.getJSONObject(i);
                                    payList2.add(new CalendarSetStatusData.CalendarSetStatusData_list(
                                            jsonObject.getString("day"),
                                            jsonObject.getString("week"),
                                            Collections.singletonList(jsonObject.getString("users"))
                                    ));
                                    dlog.i(jsonObject.getString("day") + " / SetPayCalenderData jsonObject.getString(\"users\") : " + Collections.singletonList(jsonObject.getString("users")));
                                }
                                GetPayCalenderList(Year, Month, payList2);
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

    public void GetPayCalenderList(String Year, String Month, ArrayList<CalendarSetStatusData.CalendarSetStatusData_list> mList3) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WorkCalenderInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        WorkCalenderInterface api = retrofit.create(WorkCalenderInterface.class);
        Call<String> call = api.getData(Year, Month);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "GetPayCalenderList function START");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                Log.e(TAG, "response 2: " + response.body());
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        dlog.i("onResume place_id :" + place_id);
                        dlog.i("onResume USER_INFO_ID :" + USER_INFO_ID);
                        try {
                            String select_date = Year + "-" + Month;
                            JSONArray Response = new JSONArray(response.body());
                            payList = new ArrayList<>();
                            payAdapter = new PayCalenderAdapter(mContext, payList, payList2, place_id, USER_INFO_ID, select_date, Month);
                            binding.createCalender.setAdapter(payAdapter);
                            binding.createCalender.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                            dlog.i("SetNoticeListview Thread run! ");

                            if (Response.length() == 0) {
                                dlog.i("GET SIZE : " + Response.length());
                            } else {
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    payAdapter.addItem(new WorkCalenderData.WorkCalenderData_list(
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
                                payAdapter.notifyDataSetChanged();
                                payAdapter.setOnItemClickListener(new PayCalenderAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position, String data, String yoil, String WorkDay) {
                                        try {
//                                                dlog.i("onItemClick WorkDay :" + WorkDay);
//                                                shardpref.putString("FtoDay", WorkDay);
//                                                WorkstatusBottomSheet wsb = new WorkstatusBottomSheet();
//                                                wsb.show(getSupportFragmentManager(), "WorkstatusBottomSheet");
                                        } catch (Exception e) {
                                            dlog.i("onItemClick Exception :" + e);
                                        }

                                    }
                                });
                            }
                        } catch (JSONException e) {
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

}