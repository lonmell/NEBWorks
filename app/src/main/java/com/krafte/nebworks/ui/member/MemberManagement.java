package com.krafte.nebworks.ui.member;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.ViewPagerFregmentAdapter;
import com.krafte.nebworks.adapter.WorkplaceMemberAdapter;
import com.krafte.nebworks.bottomsheet.MemberOption;
import com.krafte.nebworks.bottomsheet.PlaceListBottomSheet;
import com.krafte.nebworks.data.WorkPlaceMemberListData;
import com.krafte.nebworks.dataInterface.AllMemberInterface;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.FeedNotiInterface;
import com.krafte.nebworks.dataInterface.MemberOutPlaceInterface;
import com.krafte.nebworks.dataInterface.MemberUpdateBasicInterface;
import com.krafte.nebworks.dataInterface.PushLogInputInterface;
import com.krafte.nebworks.databinding.ActivityMemberManageBinding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MemberManagement extends AppCompatActivity {
    private static final String TAG = "MemberManagement";
    private ActivityMemberManageBinding binding;
    Context mContext;
    private final DateCurrent dc = new DateCurrent();
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    //BottomNavigation
    ImageView bottom_icon01, bottom_icon02, bottom_icon03, bottom_icon04, bottom_icon05;

    // shared 저장값
    PreferenceHelper shardpref;
    ArrayList<WorkPlaceMemberListData.WorkPlaceMemberListData_list> mList = new ArrayList<>();;
    WorkplaceMemberAdapter mAdapter = null;
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String USER_INFO_AUTH = "";
    String place_id = "";
    String place_owner_id = "";

    int SELECT_POSITION = 0;
    int SELECT_POSITION_sub = 0;
    boolean wifi_certi_flag = false;
    boolean gps_certi_flag = false;

    //Other
    /*라디오 버튼들 boolean*/
    Drawable icon_off;
    Drawable icon_on;
    PageMoveClass pm = new PageMoveClass();
    ViewPagerFregmentAdapter viewPagerFregmentAdapter;
    int paging_position = 0;
    Dlog dlog = new Dlog();
    String return_page = "";

    int total_member_cnt = 0;

    @SuppressLint({"UseCompatLoadingForDrawables", "SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_mainfragment);
        binding = ActivityMemberManageBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);

        try {
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
            return_page = shardpref.getString("return_page", "");
            shardpref.putString("returnPage", "BusinessApprovalActivity");

//            binding.addMemberBtn.setOnClickListener(v -> {
//                MemberOption mo = new MemberOption();
//                mo.show(getSupportFragmentManager(),"MemberOption");
//            });

            binding.changePlace.setOnClickListener(v -> {
                PlaceListBottomSheet plb = new PlaceListBottomSheet();
                plb.show(getSupportFragmentManager(),"PlaceListBottomSheet");
                plb.setOnClickListener01((v1, place_id, place_name, place_owner_id) -> {
                    shardpref.putString("change_place_id",place_id);
                    dlog.i("change_place_id : " + place_id);
                    SetAllMemberList(place_id);
                    binding.changePlace.setTag(place_name);
                });
            });
            binding.notiArea.setOnClickListener(v -> {
                pm.FeedList(mContext);
            });
            binding.backBtn.setOnClickListener(v -> {
                shardpref.putInt("SELECT_POSITION",SELECT_POSITION);
                shardpref.putInt("SELECT_POSITION_sub",SELECT_POSITION_sub);
                if(USER_INFO_AUTH.equals("0")){
                    pm.Main(mContext);
                }else{
                    pm.Main2(mContext);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        shardpref.putInt("SELECT_POSITION",SELECT_POSITION);
        shardpref.putInt("SELECT_POSITION_sub",SELECT_POSITION_sub);
        if(USER_INFO_AUTH.equals("0")){
            pm.Main(mContext);
        }else{
            pm.Main2(mContext);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SetAllMemberList(place_id);
        setAddBtnSetting();
        getNotReadFeedcnt();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void getNotReadFeedcnt() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedNotiInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedNotiInterface api = retrofit.create(FeedNotiInterface.class);
        Call<String> call = api.getData("", "", "","1",USER_INFO_ID);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.e( "WorkTapListFragment1 / setRecyclerView");
                dlog.e( "response 1: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                    dlog.e( "GetWorkStateInfo function onSuccess : " + response.body());
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(response.body());
                        if(!response.body().equals("[]")){
                            String NotRead = Response.getJSONObject(0).getString("notread_feed");
                            if(NotRead.equals("0")){
                                binding.notiRed.setVisibility(View.INVISIBLE);
                            }else{
                                binding.notiRed.setVisibility(View.VISIBLE);
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
                dlog.e( "에러 = " + t.getMessage());
            }
        });
    }
    
    /*직원 전체 리스트 START*/
    RetrofitConnect rc = new RetrofitConnect();
    public void SetAllMemberList(String place_id) {
        mList.clear();
        total_member_cnt = 0;
        dlog.i("-----SetAllMemberList------");
        dlog.i("place_id : " + place_id);
        dlog.i("place_owner_id : " + place_owner_id);
        dlog.i("-----SetAllMemberList------");
        @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AllMemberInterface.URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            AllMemberInterface api = retrofit.create(AllMemberInterface.class);
            Call<String> call = api.getData(place_id,"");

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        dlog.e("GetInsurancePercent function START");
                        dlog.e("response 1: " + response.isSuccessful());
                        dlog.e("response 2: " + rc.getBase64decode(response.body()));
                        Log.e("onSuccess : ", response.body());
                        try {
                            String jsonResponse = rc.getBase64decode(response.body());
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(jsonResponse);

                            mList = new ArrayList<>();
                            mAdapter = new WorkplaceMemberAdapter(mContext, mList, getSupportFragmentManager());
                            binding.allMemberlist.setAdapter(mAdapter);
                            binding.allMemberlist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));

                            if (Response.length() == 0) {
                                total_member_cnt = 0;
                                binding.nodataArea.setVisibility(View.VISIBLE);
                                binding.allMemberlist.setVisibility(View.GONE);
                            } else {
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    if(!place_owner_id.equals(jsonObject.getString("id"))){
                                        total_member_cnt ++;
                                        mAdapter.addItem(new WorkPlaceMemberListData.WorkPlaceMemberListData_list(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("place_name"),
                                                jsonObject.getString("account"),
                                                jsonObject.getString("name"),
                                                jsonObject.getString("phone"),
                                                jsonObject.getString("gender"),
                                                jsonObject.getString("img_path"),
                                                jsonObject.getString("jumin"),
                                                jsonObject.getString("kind"),
                                                jsonObject.getString("join_date"),
                                                jsonObject.getString("state"),
                                                jsonObject.getString("jikgup"),
                                                jsonObject.getString("pay"),
                                                jsonObject.getString("worktime"),
                                                jsonObject.getString("contract_cnt")
                                        ));
                                    }
                                }

                                if(total_member_cnt == 0){
                                    binding.nodataArea.setVisibility(View.VISIBLE);
                                    binding.allMemberlist.setVisibility(View.GONE);
                                }else{
                                    binding.nodataArea.setVisibility(View.GONE);
                                    binding.allMemberlist.setVisibility(View.VISIBLE);
                                }
                                mAdapter.setOnItemClickListener2(new WorkplaceMemberAdapter.OnItemClickListener2() {
                                    @Override
                                    public void onItemClick(View v, int position, int kind) {
                                        try{
                                            dlog.i("mAdapter setOnItemClickListener2 Click!");
                                            dlog.i("position : " + position);

                                            String getid = mList.get(position).getId();
                                            String place_name = mList.get(position).getPlace_name();
                                            String name = mList.get(position).getName();
                                            String phone = mList.get(position).getPhone();
                                            String jumin = mList.get(position).getJumin();
                                            String join_date = mList.get(position).getJoin_date();
                                            if(kind == 1){
                                                dlog.i("kind : " + kind);
                                                dlog.i("id : " + getid);
                                                TaskDel(getid);
                                            }else if(kind == 2){
                                                dlog.i("kind : " + kind);
                                                dlog.i("id : " + getid);
                                                UpdateBasic(getid, name, phone, jumin, "1", join_date, place_name);
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                mAdapter.notifyDataSetChanged();
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
    /*직원 전체 리스트 END*/

    public void TaskDel(String mem_id) {
//        매장 멤버 삭제 (매장에서 나가기, 매장에서 내보내기)
//        http://krafte.net/kogas/place/delete_member.php?place_id=28&user_id=24
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MemberOutPlaceInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        MemberOutPlaceInterface api = retrofit.create(MemberOutPlaceInterface.class);
        Call<String> call = api.getData(place_id, mem_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            dlog.i("TaskDel jsonResponse length : " + response.body().length());
                            dlog.i("TaskDel jsonResponse : " + response.body());
                            try {
                                if (response.body().replace("\"", "").equals("success")) {
                                    Toast_Nomal("해당 직원의 데이터 삭제가 완료되었습니다.");
                                    SetAllMemberList(place_id);
                                }
                            } catch (Exception e) {
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
    public void UpdateBasic(String mem_id,String name, String phone, String jumin, String kind, String join_date, String place_name) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MemberUpdateBasicInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        MemberUpdateBasicInterface api = retrofit.create(MemberUpdateBasicInterface.class);
        Call<String> call = api.getData(place_id, mem_id, name, phone, jumin, kind, join_date);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dlog.i("UpdateBasic jsonResponse length : " + response.body().length());
                    dlog.i("UpdateBasic jsonResponse : " + response.body());
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                if (response.body().replace("\"", "").equals("success")) {
                                    Toast_Nomal("해당 직원의 데이터가 업데이트되었습니다.");
                                    SetAllMemberList(place_id);

                                    String message = "[" + place_name + "]매장에서 근무신청이 수락되었습니다.";
                                    getUserToken(mem_id,"1",message);
                                    AddPush("근무신청",message,mem_id);
                                }
                            } catch (Exception e) {
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

    //점주 > 근로자 ( 근무신청 수락 FCM )
    public void getUserToken(String user_id, String type, String message) {
        dlog.i("-----getManagerToken-----");
        dlog.i("user_id : " + user_id);
        dlog.i("type : " + type);
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
                dlog.i("Response Result : " + response.body());
                try {
                    JSONArray Response = new JSONArray(response.body());
                    if (Response.length() > 0) {
                        dlog.i("-----getManagerToken-----");
                        dlog.i("user_id : " + Response.getJSONObject(0).getString("user_id"));
                        dlog.i("token : " + Response.getJSONObject(0).getString("token"));
                        String id = Response.getJSONObject(0).getString("id");
                        String token = Response.getJSONObject(0).getString("token");
                        dlog.i("-----getManagerToken-----");
                        boolean channelId1 = Response.getJSONObject(0).getString("channel1").equals("1");
                        if (!token.isEmpty() && channelId1) {
                            PushFcmSend(id, "", message, token, "1", place_id);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러 = " + t.getMessage());
            }
        });
    }

    public void AddPush(String title, String content, String user_id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PushLogInputInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PushLogInputInterface api = retrofit.create(PushLogInputInterface.class);
        Call<String> call = api.getData(place_id, "", title, content, place_owner_id, user_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.i("AddStroeNoti Callback : " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            dlog.i("AddStroeNoti jsonResponse length : " + response.body().length());
                            dlog.i("AddStroeNoti jsonResponse : " + response.body());
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

    DBConnection dbConnection = new DBConnection();
    String click_action = "";

    private void PushFcmSend(String topic, String title, String message, String token, String tag, String place_id) {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            click_action = "Member1";
            dlog.i("-----PushFcmSend-----");
            dlog.i("topic : " + topic);
            dlog.i("title : " + title);
            dlog.i("message : " + message);
            dlog.i("token : " + token);
            dlog.i("click_action : " + click_action);
            dlog.i("tag : " + tag);
            dlog.i("place_id : " + place_id);
            dlog.i("-----PushFcmSend-----");
            dbConnection.FcmTestFunction(topic, title, message, token, click_action, tag, place_id);
            runOnUiThread(() -> {
            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    CardView add_worktime_btn;
    TextView addbtn_tv;
    private void setAddBtnSetting() {
        add_worktime_btn = binding.getRoot().findViewById(R.id.add_worktime_btn);
        addbtn_tv = binding.getRoot().findViewById(R.id.addbtn_tv);
        addbtn_tv.setText("직원추가");
        add_worktime_btn.setOnClickListener(v -> {
            MemberOption mo = new MemberOption();
            mo.show(getSupportFragmentManager(),"MemberOption");
        });
    }
//    //-------몰입화면 설정
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            hideSystemUI();
//        }
//    }
//
//    private void hideSystemUI() {
//        // Enables regular immersive mode.
//        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
//        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                        // Set the content to appear under the system bars so that the
//                        // content doesn't resize when the system bars hide and show.
//                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        // Hide the nav bar and status bar
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
//    }
//    //-------몰입화면 설정
}
