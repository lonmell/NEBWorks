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

import com.krafte.nebworks.adapter.WorkCalenderAdapter;
import com.krafte.nebworks.bottomsheet.WorkgotoBottomSheet;
import com.krafte.nebworks.data.CalendarSetData;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.data.WorkCalenderData;
import com.krafte.nebworks.dataInterface.WorkCalenderInterface;
import com.krafte.nebworks.dataInterface.WorkCalendersetData;
import com.krafte.nebworks.databinding.FragmentCalenderBinding;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

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
    WorkCalenderAdapter mAdapter;
    Dlog dlog = new Dlog();
    private FragmentCalenderBinding binding = null;

    Context mContext;
    Activity activity;
    String year;
    String month;
    long time;

    String change_place_id = "";
    String change_member_id = "";

    ArrayList<WorkCalenderData.WorkCalenderData_list> mList;
    ArrayList<CalendarSetData.CalendarSetData_list> mList2 = new ArrayList<>();

    // state 1: WorkGoto
    public CalenderFragment(long year, long month) {
        this.year = String.valueOf(Math.toIntExact(year));
        this.month = String.format("%02d", Math.toIntExact(month));
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

    String timeDate = "";
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

        time = time * 1000;
        Date date = new Date(time);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        timeDate = dateFormat.format(date);

        Log.d(TAG, timeDate);

        SetCalenderData();
        return binding.getRoot();
    }

    private void SetCalenderData() {
        mList2.clear();
        String getYMPicker = year + "-" + month;
        Log.i(TAG, "------SetCalenderData------");
        Log.i(TAG, "place_id : " + place_id);
        Log.i(TAG, "USER_INFO_ID : " + USER_INFO_ID);
        Log.i(TAG, "select_date : " + getYMPicker);
        Log.i(TAG, "------SetCalenderData------");
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
                        Log.i(TAG, "SetCalenderData function START");
                        try {
                            JSONArray Response2 = new JSONArray(jsonResponse);
                            if (Response2.length() == 0) {
                                Log.i(TAG, "GET SIZE : " + Response2.length());
                                GetCalenderList(year, month, mList2);
                            } else {
                                for (int i = 0; i < Response2.length(); i++) {
                                    JSONObject jsonObject = Response2.getJSONObject(i);
                                    mList2.add(new CalendarSetData.CalendarSetData_list(
                                            jsonObject.getString("day"),
                                            jsonObject.getString("week"),
                                            Collections.singletonList(jsonObject.getString("task"))
                                    ));
                                }
                                GetCalenderList(year, month, mList2);
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
    public void GetCalenderList(String Year, String Month, ArrayList<CalendarSetData.CalendarSetData_list> mList2) {
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
                Log.e(TAG, "GetCalenderList function START");
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
                            mList = new ArrayList<>();
                            mAdapter = new WorkCalenderAdapter(mContext, mList, mList2, place_id, USER_INFO_ID, select_date, Month);
                            binding.createCalender.setAdapter(mAdapter);
                            binding.createCalender.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                            Log.i(TAG, "SetNoticeListview Thread run! ");

                            if (Response.length() == 0) {
                                Log.i(TAG, "GET SIZE : " + Response.length());
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
                                mAdapter.setOnItemClickListener(new WorkCalenderAdapter.OnItemClickListener() {
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
                                                WorkgotoBottomSheet wgb = new WorkgotoBottomSheet();
                                                wgb.show(getChildFragmentManager(), "WorkgotoBottomSheet");

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
}