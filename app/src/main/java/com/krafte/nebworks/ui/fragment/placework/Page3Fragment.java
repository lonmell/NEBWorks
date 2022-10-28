package com.krafte.nebworks.ui.fragment.placework;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.Tap3ListAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.TodoReuseData;
import com.krafte.nebworks.dataInterface.TaskreuseSInterface;
import com.krafte.nebworks.pop.DatePickerActivity;
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

public class Page3Fragment extends Fragment {
    private final static String TAG = "Page1Fragment";

    Context mContext;
    Activity activity;

    //XML ID
    RecyclerView store_checklist;
    LinearLayout nodata_area,search_date;
    RelativeLayout login_alert_text;
    ImageView loading_view,user_manualimg;
    TextView user_manualtv,selectdate;

    //sharedPreferences
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_EMAIL = "";

    //Other
    ArrayList<TodoReuseData.TodoReuseData_list> mList;
    Tap3ListAdapter mAdapter = null;
    RetrofitConnect rc = new RetrofitConnect();
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    DateCurrent dc = new DateCurrent();

    int listitemsize = 0;
    Dlog dlog = new Dlog();

//    public static Page3Fragment newInstance(int number) {
//        Page3Fragment fragment = new Page3Fragment();
//        Bundle bundle = new Bundle();
//        bundle.putInt("number", number);
//        fragment.setArguments(bundle);
//        return fragment;
//    }

    public static Page3Fragment newInstance(){
        return new Page3Fragment();
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
            Log.i(TAG,"num : " + num);
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

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.worktap_fragment3, container, false);
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

        shardpref = new PreferenceHelper(mContext);
        dlog.DlogContext(mContext);

        //Shared
        try {
            USER_INFO_ID = shardpref.getString("USER_INFO_ID","0");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL","0");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH","-1"); //0-관리자 / 1- 근로자
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
            shardpref.putInt("SELECT_POSITION", 2);
            user_manualtv.setText("할일 배정 카테고리에 등록된 할일이 없습니다.\n+ 를 터치하여 할일 등록 해 보세요.");
            dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
            String toDay = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
            nodata_area.setVisibility(View.VISIBLE);

            selectdate.setText(toDay);
            selectdate.setOnClickListener(v -> {
                shardpref.putInt("timeSelect_flag",6);
                Intent intent = new Intent(mContext, DatePickerActivity.class);
                startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
            });
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }




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
    public void onResume(){
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

        setTodoMList();
    }

    public void setTodoMList() {
        dlog.i("setTodoMList place_id : " + place_id);
        rc.placeNotiData_lists.clear();
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
                    Log.e(TAG, "GetWorkStateInfo function onSuccess : " + response.body());
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(response.body());
                        if(listitemsize != Response.length()){
                            mList = new ArrayList<>();
                            mAdapter = new Tap3ListAdapter(mContext,mList);
                            store_checklist.setAdapter(mAdapter);
                            store_checklist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                            Log.i(TAG, "SetNoticeListview Thread run! ");
                            listitemsize = Response.length();
                            Log.i(TAG, "GET SIZE : " + rc.placeNotiData_lists.size());
                            if(Response.length() == 0){
                                nodata_area.setVisibility(View.VISIBLE);
                                Log.i(TAG, "GET SIZE : " + rc.placeNotiData_lists.size());
                            }else{
                                nodata_area.setVisibility(View.GONE);
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
                                            jsonObject.getString("sat")
                                    ));
                                }
                                mAdapter.notifyDataSetChanged();
                            }
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

}
