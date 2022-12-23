package com.krafte.nebworks.ui.notify;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.WorkplaceNotifyAdapter;
import com.krafte.nebworks.data.WorkPlaceEmloyeeNotifyData;
import com.krafte.nebworks.dataInterface.NotifyListInterface;
import com.krafte.nebworks.dataInterface.NotifyReadyUInterface;
import com.krafte.nebworks.databinding.ActivityNotifyBinding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NotifyListActivity extends AppCompatActivity {
    private ActivityNotifyBinding binding;
    private final static String TAG = "EmployeeNotifyListActivity";
    Context mContext;

    //XML ID
    ArrayList<WorkPlaceEmloyeeNotifyData.WorkPlaceEmloyeeNotifyData_list> mList;
    WorkplaceNotifyAdapter mAdapter = null;

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID, USER_INFO_NAME;
    String place_id = "";

    //Other
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    RetrofitConnect rc = new RetrofitConnect();
    int listitemsize = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_notify);
        binding = ActivityNotifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);
        setBtnEvent();
        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
        place_id = shardpref.getString("place_id", "");
        SetWorkplaceList();
    }

    @Override
    public void onResume() {
        super.onResume();
        SetWorkplaceList();
    }


    private void setBtnEvent() {
        binding.backBtn.setOnClickListener(v -> {
            super.onBackPressed();
        });
        binding.allReadY.setOnClickListener(v -> {
            UpdateWorkNotifyReadYn();
        });
    }


    public void SetWorkplaceList() {
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("place_id : " + place_id);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NotifyListInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        NotifyListInterface api = retrofit.create(NotifyListInterface.class);
        Call<String> call = api.getData(place_id, USER_INFO_ID);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.e("SetWorkplaceList function START");
                String jsonResponse = rc.getBase64decode(response.body());
                dlog.e("response 1: " + response.isSuccessful());
                dlog.e("response 2: " + jsonResponse);
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONArray Response = new JSONArray(jsonResponse);
                        if (Response.length() == 0) {
                            binding.workplaceNotifyList.setVisibility(View.GONE);
                            binding.noData.setVisibility(View.VISIBLE);
                        } else {
                            binding.workplaceNotifyList.setVisibility(View.VISIBLE);
                            binding.noData.setVisibility(View.GONE);
                            mList = new ArrayList<>();
                            mAdapter = new WorkplaceNotifyAdapter(mContext, mList);
                            binding.workplaceNotifyList.setAdapter(mAdapter);
                            binding.workplaceNotifyList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                            Log.i(TAG, "SetNoticeListview Thread run! ");
                            listitemsize = Response.length();
                            if (Response.length() == 0) {
                                Log.i(TAG, "GET SIZE : " + Response.length());
                            } else {
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    mAdapter.addItem(new WorkPlaceEmloyeeNotifyData.WorkPlaceEmloyeeNotifyData_list(
                                            jsonObject.getString("id"),
                                            jsonObject.getString("push_date"),
                                            jsonObject.getString("push_time"),
                                            jsonObject.getString("user_id"),
                                            jsonObject.getString("img_path"),
                                            jsonObject.getString("title"),
                                            jsonObject.getString("contents"),
                                            jsonObject.getString("read_yn")
                                    ));
                                }
                                mAdapter.notifyDataSetChanged();

//                                int MovePosition = shardpref.getInt("notify_pos", 0);
//                                if(MovePosition != 0){
//                                    RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(workplace_notify_list.getContext()) {
//                                        @Override protected int getVerticalSnapPreference() {
//                                            return LinearSmoothScroller.SNAP_TO_START;
//                                        }
//                                    };
//
//                                    smoothScroller.setTargetPosition( MovePosition ); //itemPosition - 이동시키고자 하는 Item의 Position
//                                    workplace_notify_list.getLayoutManager().startSmoothScroll(smoothScroller);
//                                }
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

    @SuppressLint("LongLogTag")
    private void UpdateWorkNotifyReadYn() {
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("place_id : " + place_id);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NotifyReadyUInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        NotifyReadyUInterface api = retrofit.create(NotifyReadyUInterface.class);
        Call<String> call = api.getData(place_id, USER_INFO_ID);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.e("SetWorkplaceList function START");
//                String jsonResponse = rc.getBase64decode(response.body());
                dlog.e("response 1: " + response.isSuccessful());
                dlog.e("response 2: " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    try {
//                        JSONArray Response = new JSONArray(jsonResponse);
                        if (response.body().replace("\"", "").equals("success")) {
//                            Toast_Nomal("");
                            SetWorkplaceList();
                            mAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
}
