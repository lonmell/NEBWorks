package com.krafte.nebworks.ui.user;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.adapter.WorkplaceListAdapter;
import com.krafte.nebworks.data.PlaceListData;
import com.krafte.nebworks.dataInterface.PlaceMyInterface;
import com.krafte.nebworks.dataInterface.UserSelectInterface;
import com.krafte.nebworks.databinding.ActivityWorksiteBinding;
import com.krafte.nebworks.util.DateCurrent;
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

public class MyPlaceListActivity extends AppCompatActivity {

    private ActivityWorksiteBinding binding;
    Context mContext;

    //Other 클래스
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    Handler handler = new Handler();
    RetrofitConnect rc = new RetrofitConnect();
    PageMoveClass pm = new PageMoveClass();

    //Other 변수
    ArrayList<PlaceListData.PlaceListData_list> mList;
    WorkplaceListAdapter mAdapter = null;
    int listitemsize = 0;
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String USER_INFO_EMAIL = "";

    //사용자 정보 체크
    String id = "";
    String name = "";
    String email = "";
    String employee_no = "";
    String department = "";
    String jikchk = "";
    String img_path = "";

    String return_page = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityWorksiteBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        try {
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "");
            USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
            return_page = shardpref.getString("return_page", "");
            binding.addPlace.setVisibility(View.GONE);
            dlog.i("------onCreate DataCheck-----");
            dlog.i("USER_INFO_ID : " + USER_INFO_ID);
            LoginCheck(USER_INFO_EMAIL);
            setBtnEvent();
            GetMyPlaceList(USER_INFO_ID);
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }


    private void setBtnEvent() {
        binding.addPlace.setOnClickListener(v -> {
            pm.PlaceAddGo(mContext);
        });

        binding.refreshBtn.setOnClickListener(v -> {
            GetMyPlaceList(USER_INFO_ID);
        });
    }

    public void LoginCheck(String account) {
        dlog.i("LoginCheck account : " + account);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserSelectInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        UserSelectInterface api = retrofit.create(UserSelectInterface.class);
        Call<String> call = api.getData(account);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
//                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("LoginCheck jsonResponse length : " + response.body().length());
                            dlog.i("LoginCheck jsonResponse : " + response.body());
                            try {
                                if (!response.body().equals("[]")) {
                                    JSONArray Response = new JSONArray(response.body());
                                    id = Response.getJSONObject(0).getString("id");
                                    name = Response.getJSONObject(0).getString("name");
                                    email = Response.getJSONObject(0).getString("account");
                                    employee_no = Response.getJSONObject(0).getString("employee_no");
                                    department = Response.getJSONObject(0).getString("department");
                                    jikchk = Response.getJSONObject(0).getString("position");
                                    img_path = Response.getJSONObject(0).getString("img_path");

                                    shardpref.putString("USER_INFO_ID", id);
                                    shardpref.putString("USER_INFO_NAME", name);
                                    shardpref.putString("USER_INFO_EMAIL", account);
                                    shardpref.putString("USER_INFO_SABUN", employee_no);
                                    shardpref.putString("USER_INFO_SOSOK", department);
                                    shardpref.putString("USER_INFO_JIKGUP", jikchk);
                                    shardpref.putString("USER_INFO_PROFILE_URL", img_path);
                                    dlog.i("id : " + id);
                                    dlog.i("USER_INFO_ID : " +USER_INFO_ID);
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


    public void GetMyPlaceList(String account) {
        dlog.i("GetPlaceList account : " + account);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceMyInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceMyInterface api = retrofit.create(PlaceMyInterface.class);
        Call<String> call = api.getData("");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
//                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("GetPlaceList jsonResponse length : " + response.body().length());
                            dlog.i("GetPlaceList jsonResponse : " + response.body());
                            try {
                                //Array데이터를 받아올 때
                                JSONArray Response = new JSONArray(response.body());

                                if (listitemsize != Response.length()) {
                                    mList = new ArrayList<>();
                                    mAdapter = new WorkplaceListAdapter(mContext, mList);
                                    binding.placeList.setAdapter(mAdapter);
                                    binding.placeList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                                    listitemsize = Response.length();
                                    if (Response.length() == 0) {
                                        dlog.i("SetNoticeListview Thread run! ");
                                        dlog.i("GET SIZE : " + Response.length());
                                    } else {

                                        for (int i = 0; i < Response.length(); i++) {
                                            JSONObject jsonObject = Response.getJSONObject(i);
                                            //작업 일자가 없으면 표시되지 않음.
                                            if (!jsonObject.getString("start_date").equals("null")) {
                                                mAdapter.addItem(new PlaceListData.PlaceListData_list(
                                                        jsonObject.getString("id"),
                                                        jsonObject.getString("name"),
                                                        jsonObject.getString("owner_id"),
                                                        jsonObject.getString("owner_name"),
                                                        jsonObject.getString("management_office"),
                                                        jsonObject.getString("address"),
                                                        jsonObject.getString("latitude"),
                                                        jsonObject.getString("longitude"),
                                                        jsonObject.getString("start_time"),
                                                        jsonObject.getString("end_time"),
                                                        jsonObject.getString("img_path"),
                                                        jsonObject.getString("start_date"),
                                                        jsonObject.getString("total_cnt"),
                                                        jsonObject.getString("i_cnt"),
                                                        jsonObject.getString("o_cnt"),
                                                        jsonObject.getString("created_at")
                                                ));
                                            }
                                        }

                                        mAdapter.notifyDataSetChanged();
                                        mAdapter.setOnItemClickListener(new WorkplaceListAdapter.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(View v, int position) {
                                                try {
                                                    dlog.i("place_latitude : " + shardpref.getString("place_latitude", ""));
                                                    dlog.i("place_longitude : " + shardpref.getString("place_longitude", ""));
                                                    shardpref.putString("retrun_page","MyPlaceListActivity");
                                                    if (department.equals("null") || jikchk.equals("null")) {
                                                        pm.ProfileEditGo(mContext);
                                                    } else {
                                                        pm.UserPlsceMapGo(mContext);
                                                    }
                                                } catch (Exception e) {
                                                    dlog.i("GetPlaceList OnItemClickListener Exception :" + e);
                                                }

                                            }
                                        });
                                    }
                                }
                                dlog.i("SetNoticeListview Thread run! ");
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(return_page.equals("MoreActivity")){
            pm.MoreBack(mContext);
        }else if(return_page.equals("MainActivity")){
            pm.MainBack(mContext);
        }else{
            super.onBackPressed();
        }

    }
}
