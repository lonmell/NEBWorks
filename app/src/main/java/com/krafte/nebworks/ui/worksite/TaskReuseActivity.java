package com.krafte.nebworks.ui.worksite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.Tap3ListAdapter;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.TodoReuseData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.TaskreuseSInterface;
import com.krafte.nebworks.databinding.ActivityTaskReuseBinding;
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

/*
 * 2022-11-24 방창배 작성 - 자주하는 업무 목록 페이지
 * */
public class TaskReuseActivity extends AppCompatActivity {
    private static final String TAG = "TaskReuseActivity";
    private ActivityTaskReuseBinding binding;
    Context mContext;

    //Other 클래스
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    PageMoveClass pm = new PageMoveClass();
    RetrofitConnect rc = new RetrofitConnect();

    //Other 변수
    ArrayList<TodoReuseData.TodoReuseData_list> mList;
    Tap3ListAdapter mAdapter = null;
    String USER_INFO_EMAIL = "";
    String USER_INFO_NAME = "";
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";
    String place_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityTaskReuseBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        try {
            mContext = this;
            dlog.DlogContext(mContext);
            shardpref   = new PreferenceHelper(mContext);

            //Singleton Area
            place_id        = PlaceCheckData.getInstance().getPlace_id();
            USER_INFO_ID    = UserCheckData.getInstance().getUser_id();
            USER_INFO_EMAIL = UserCheckData.getInstance().getUser_account();
            USER_INFO_NAME  = UserCheckData.getInstance().getUser_name();
            USER_INFO_AUTH  = shardpref.getString("USER_INFO_AUTH","0");

            //shardpref Area


            setBtnEvent();
            setTodoMList();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        RemoveShared();
    }

    @Override
    public void onResume() {
        super.onResume();
        setIncludeSetting();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setBtnEvent() {
        binding.backBtn.setOnClickListener(v -> {
            shardpref.putInt("SELECT_POSITION",1);
            if(USER_INFO_AUTH.equals("0")){
                pm.Main(mContext);
            }else{
                pm.Main2(mContext);
            }
        });
    }

    public void setTodoMList() {
        dlog.i("setTodoMList place_id : " + place_id);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TaskreuseSInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TaskreuseSInterface api = retrofit.create(TaskreuseSInterface.class);
        Call<String> call = api.getData(place_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG,"WorkTapListFragment1 / setRecyclerView");
                Log.e(TAG,"response 1: " + response.isSuccessful());
                Log.e(TAG,"response 2: " + response.body());
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                    String jsonResponse = rc.getBase64decode(response.body());
                    dlog.i("jsonResponse length : " + jsonResponse.length());
                    dlog.i("jsonResponse : " + jsonResponse);
                    Log.e(TAG, "GetWorkStateInfo function onSuccess : " + jsonResponse);
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(jsonResponse);
                            mList = new ArrayList<>();
                            mAdapter = new Tap3ListAdapter(mContext,mList);
                            binding.reuseList.setAdapter(mAdapter);
                            binding.reuseList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));

                            Log.i(TAG, "SetNoticeListview Thread run! ");
                            Log.i(TAG, "GET SIZE : " + rc.placeNotiData_lists.size());
                            if(Response.length() == 0){
                                binding.noData.getRoot().setVisibility(View.VISIBLE);
                                Log.i(TAG, "GET SIZE : " + rc.placeNotiData_lists.size());
                            }else{
                                binding.noData.getRoot().setVisibility(View.GONE);
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    mAdapter.addItem(new TodoReuseData.TodoReuseData_list(
                                            jsonObject.getString("id"),
                                            jsonObject.getString("writer_id"),
                                            jsonObject.getString("title"),
                                            jsonObject.getString("contents"),
                                            jsonObject.getString("complete_kind"),
                                            jsonObject.getString("start_time"),
                                            jsonObject.getString("end_time"),
                                            jsonObject.getString("sun"),
                                            jsonObject.getString("mon"),
                                            jsonObject.getString("tue"),
                                            jsonObject.getString("wed"),
                                            jsonObject.getString("thu"),
                                            jsonObject.getString("fri"),
                                            jsonObject.getString("sat"),
                                            jsonObject.getString("task_overdate")
                                    ));
                                }
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

    CardView add_worktime_btn;
    TextView addbtn_tv,no_data_txt;
    private void setIncludeSetting() {
        add_worktime_btn = binding.getRoot().findViewById(R.id.add_worktime_btn);
        addbtn_tv = binding.getRoot().findViewById(R.id.addbtn_tv);
        no_data_txt = binding.getRoot().findViewById(R.id.no_data_txt);
        addbtn_tv.setText("자주하는 업무\n추가");
        addbtn_tv.setTextSize((int)12);
        no_data_txt.setText("저장된 업무 목록이 없습니다\n업무를 추가 후 사용해보세요.");
        add_worktime_btn.setOnClickListener(v -> {
            shardpref.putInt("make_kind",2);
            pm.TaskReuesAdd(mContext);
        });
    }

    private void RemoveShared() {
        shardpref.remove("task_no");
        shardpref.remove("writer_id");
        shardpref.remove("kind");
        shardpref.remove("title");
        shardpref.remove("contents");
        shardpref.remove("complete_kind");       // 0:체크, 1:사진
        shardpref.remove("users");
        shardpref.remove("usersn");
        shardpref.remove("usersimg");
        shardpref.remove("usersjikgup");
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
        shardpref.remove("approval_state");
        shardpref.remove("overdate");
        shardpref.remove("make_kind");
    }

    @Override
    public void onBackPressed(){
//        super.onBackPressed();
        shardpref.putInt("SELECT_POSITION",1);
        if(USER_INFO_AUTH.equals("0")){
            pm.Main(mContext);
        }else{
            pm.Main2(mContext);
        }
    }
}
