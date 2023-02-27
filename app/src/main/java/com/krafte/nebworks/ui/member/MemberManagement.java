package com.krafte.nebworks.ui.member;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.krafte.nebworks.dataInterface.GetNotDetailInterface;
import com.krafte.nebworks.dataInterface.MemberOutPlaceInterface;
import com.krafte.nebworks.dataInterface.MemberUpdateBasicInterface;
import com.krafte.nebworks.dataInterface.PushLogInputInterface;
import com.krafte.nebworks.databinding.ActivityMemberManageBinding;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
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

    // shared 저장값
    PreferenceHelper shardpref;
    ArrayList<WorkPlaceMemberListData.WorkPlaceMemberListData_list> mList = new ArrayList<>();

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
    /*
        0 - 승인 대기중 ( 직원이 근무신청 )
        1 - 승인
        2 - 직접입력한 멤버
        3 - 초대한 멤버 ( 점주가 직원초대 )
        4 - 퇴직한 멤버
    */
    int memkind = -1;
    float oldXvalue;
    float oldYvalue;

    private static final String IMAGEVIEW_TAG = "드래그 이미지";

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
        shardpref = new PreferenceHelper(mContext);

        try {
            icon_off = mContext.getApplicationContext().getResources().getDrawable(R.drawable.menu_gray_bar);
            icon_on = mContext.getApplicationContext().getResources().getDrawable(R.drawable.menu_blue_bar);

            //Singleton Area
            place_id = shardpref.getString("place_id", "0");
            place_owner_id = shardpref.getString("getPlace_owner_id", "0");
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
            USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "0");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
            return_page = shardpref.getString("return_page", "");
            shardpref.putString("BusinessApprovalActivity", "");

            //shardpref Area
            SELECT_POSITION = shardpref.getInt("SELECT_POSITION", 0);
            SELECT_POSITION_sub = shardpref.getInt("SELECT_POSITION_sub", 0);
            wifi_certi_flag = shardpref.getBoolean("wifi_certi_flag", false);
            gps_certi_flag = shardpref.getBoolean("gps_certi_flag", false);

            dlog.i("------MemberManagement onCreate------");
            dlog.i("place_id : " + place_id);
            dlog.i("place_owner_id : " + place_owner_id);
            dlog.i("USER_INFO_ID : " + USER_INFO_ID);
            dlog.i("USER_INFO_NAME : " + USER_INFO_NAME);
            dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
            dlog.i("return_page : " + return_page);
            dlog.i("SELECT_POSITION : " + SELECT_POSITION);
            dlog.i("SELECT_POSITION_sub : " + SELECT_POSITION_sub);
            dlog.i("wifi_certi_flag : " + wifi_certi_flag);
            dlog.i("gps_certi_flag : " + gps_certi_flag);
            dlog.i("------MemberManagement onCreate------");

            binding.changePlace.setOnClickListener(v -> {
                PlaceListBottomSheet plb = new PlaceListBottomSheet();
                plb.show(getSupportFragmentManager(), "PlaceListBottomSheet");
                plb.setOnClickListener01((v1, place_id, place_name, place_owner_id) -> {
                    shardpref.putString("change_place_id", place_id);
                    dlog.i("change_place_id : " + place_id);
                    SetAllMemberList(place_id);
                    binding.selectPlace.setText(place_name);
                    binding.changePlace.setTag(place_name);
                });
            });
            binding.notiArea.setOnClickListener(v -> {
                pm.FeedList(mContext);
            });
            binding.backBtn.setOnClickListener(v -> {
//                super.onBackPressed();
                getWorkCnt();
            });

            binding.memMenu01.setOnClickListener(v -> {
                ChangeMenu(1);
                SetAllMemberList(place_id);
            });
            binding.memMenu02.setOnClickListener(v -> {
                ChangeMenu(2);
                SetAllMemberList(place_id);
            });
            binding.memMenu03.setOnClickListener(v -> {
                ChangeMenu(3);
                SetAllMemberList(place_id);
            });
            binding.memMenu04.setOnClickListener(v -> {
                ChangeMenu(4);
                SetAllMemberList(place_id);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        getWorkCnt();
    }


    @Override
    public void onResume() {
        super.onResume();
        setAddBtnSetting();
        getNotReadFeedcnt();
        SetAllMemberList(place_id);
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
        Call<String> call = api.getData("", "", "", "1", USER_INFO_ID);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.e("getNotReadFeedcnt");
                dlog.e("response 1: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                    String jsonResponse = rc.getBase64decode(response.body());
                    dlog.i("jsonResponse length : " + jsonResponse.length());
                    dlog.i("jsonResponse : " + jsonResponse);
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(jsonResponse);
                        if (!jsonResponse.equals("[]") && Response.length() != 0) {
                            String NotRead = Response.getJSONObject(0).getString("notread_feed");
                            if (NotRead.equals("0") || NotRead.isEmpty()) {
                                binding.notiRed.setVisibility(View.INVISIBLE);
                            } else {
                                binding.notiRed.setVisibility(View.VISIBLE);
                            }
                        } else {
                            binding.notiRed.setVisibility(View.INVISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러 = " + t.getMessage());
            }
        });
    }

    int wccnt = 0;

    private void getWorkCnt() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GetNotDetailInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        GetNotDetailInterface api = retrofit.create(GetNotDetailInterface.class);
        Call<String> call = api.getData(place_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.e("getWorkCnt");
                dlog.e("response 1: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                    String jsonResponse = rc.getBase64decode(response.body());
                    dlog.i("jsonResponse length : " + jsonResponse.length());
                    dlog.i("jsonResponse : " + jsonResponse);
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(jsonResponse);
                        for (int i = 0; i < Response.length(); i++) {
                            String workcnt = Response.getJSONObject(i).getString("workcnt");
                            if (workcnt.equals("0")) {
                                wccnt++;
                            }
                        }
                        dlog.i("wccnt : " + wccnt);
                        shardpref.putInt("SELECT_POSITION", SELECT_POSITION);
                        shardpref.putInt("SELECT_POSITION_sub", SELECT_POSITION_sub);
                        if (USER_INFO_AUTH.equals("0")) {
                            if (wccnt == 0) {
                                //직원상세정보가 모두 추가되었을 때
                                pm.Main(mContext);
//                                finish();
                            } else {
                                //직원상세정보 추가 안한 사람이 있을 때
                                Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
                                intent.putExtra("data", "상세정보가 입력되지 않은 직원이 있습니다.");
                                intent.putExtra("flag", "직원미입력");
                                intent.putExtra("left_btn_txt", "닫기");
                                intent.putExtra("right_btn_txt", "뒤로가기");
                                startActivity(intent);
                            }
                        } else {
                            pm.Main2(mContext);
//                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러 = " + t.getMessage());
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
            Call<String> call = api.getData(place_id, "");

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        dlog.e("SetAllMemberList function START");
                        dlog.e("SetAllMemberList response 1: " + response.isSuccessful());
                        dlog.e("SetAllMemberList response 2: " + rc.getBase64decode(response.body()));
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
                                binding.nodataArea.setOnClickListener(v -> {
                                    MemberOption mo = new MemberOption();
                                    mo.show(getSupportFragmentManager(), "MemberOption");
                                });
                            } else {
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    if (memkind == -1) {
                                        total_member_cnt++;
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
                                    } else if (memkind == 1) {
                                        if (jsonObject.getString("kind").equals("1") && (!jsonObject.getString("jikgup").equals("null") || !jsonObject.getString("pay").equals("null"))) {
                                            total_member_cnt++;
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
                                    } else if (memkind == 2) {
                                        if (jsonObject.getString("jikgup").equals("null") || jsonObject.getString("pay").equals("null")) {
                                            total_member_cnt++;
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
                                    } else if (memkind == 3) {
                                        if (jsonObject.getString("kind").equals("4")) {
                                            total_member_cnt++;
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
                                }

                                if (Response.length() == 0) {
                                    binding.nodataArea.setVisibility(View.VISIBLE);
                                    binding.allMemberlist.setVisibility(View.GONE);
                                } else {
                                    binding.nodataArea.setVisibility(View.GONE);
                                    binding.allMemberlist.setVisibility(View.VISIBLE);
                                    binding.memberCnt.setText(Response.length() + "명");
                                }

                                mAdapter.setOnItemClickListener2(new WorkplaceMemberAdapter.OnItemClickListener2() {
                                    @Override
                                    public void onItemClick(View v, int position, int kind) {
                                        try {
                                            dlog.i("mAdapter setOnItemClickListener2 Click!");
                                            dlog.i("position : " + position);

                                            String getid = mList.get(position).getId();
                                            String place_name = mList.get(position).getPlace_name();
                                            String name = mList.get(position).getName();
                                            String phone = mList.get(position).getPhone();
                                            String jumin = mList.get(position).getJumin();
                                            String join_date = mList.get(position).getJoin_date();
                                            if (kind == 1) {
                                                dlog.i("kind : " + kind);
                                                dlog.i("id : " + getid);
                                                MemDel(getid);
                                            } else if (kind == 2) {
                                                dlog.i("kind : " + kind);
                                                dlog.i("id : " + getid);
                                                UpdateBasic(getid, name, phone, jumin, "1", join_date, place_name);
                                            } else if (kind == 3) {
                                                shardpref.putString("mem_id", getid);
                                                Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
                                                intent.putExtra("flag", "직원삭제2");
                                                intent.putExtra("data", "근로계약서, 출근표가 삭제됩니다\n삭제하시겠습니까?");
                                                intent.putExtra("left_btn_txt", "닫기");
                                                intent.putExtra("right_btn_txt", "삭제");
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            }
                                        } catch (Exception e) {
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

    public void MemDel(String mem_id) {
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
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            try {
                                if (jsonResponse.replace("\"", "").equals("success")) {
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

    public void UpdateBasic(String mem_id, String name, String phone, String jumin, String kind, String join_date, String place_name) {
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
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            try {
                                if (jsonResponse.replace("\"", "").equals("success")) {
                                    Toast_Nomal("해당 직원의 데이터가 업데이트되었습니다.");
                                    SetAllMemberList(place_id);

                                    String message = "[" + place_name + "]매장에서 근무신청이 수락되었습니다.";
                                    getUserToken(mem_id, "1", message);
                                    AddPush("근무신청", message, mem_id);
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
                String jsonResponse = rc.getBase64decode(response.body());
                dlog.i("jsonResponse length : " + jsonResponse.length());
                dlog.i("jsonResponse : " + jsonResponse);
                try {
                    JSONArray Response = new JSONArray(jsonResponse);
                    if (Response.length() > 0) {
                        dlog.i("-----getManagerToken-----");
                        dlog.i("user_id : " + Response.getJSONObject(0).getString("user_id"));
                        dlog.i("token : " + Response.getJSONObject(0).getString("token"));
                        String id = Response.getJSONObject(0).getString("id");
                        String token = Response.getJSONObject(0).getString("token");
                        dlog.i("-----getManagerToken-----");
                        boolean channelId1 = Response.getJSONObject(0).getString("channel4").equals("1");
                        if (!token.isEmpty() && channelId1) {
                            PushFcmSend(id, "", message, token, "4", place_id);
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
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
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

    private void ChangeMenu(int i) {
        binding.memMenu01.setTextColor(Color.parseColor("#DBDBDB"));
        binding.memMenu02.setTextColor(Color.parseColor("#DBDBDB"));
        binding.memMenu03.setTextColor(Color.parseColor("#DBDBDB"));
        binding.memMenu04.setTextColor(Color.parseColor("#DBDBDB"));
        binding.memLine01.setBackgroundColor(Color.parseColor("#DBDBDB"));
        binding.memLine02.setBackgroundColor(Color.parseColor("#DBDBDB"));
        binding.memLine03.setBackgroundColor(Color.parseColor("#DBDBDB"));
        binding.memLine04.setBackgroundColor(Color.parseColor("#DBDBDB"));

        if (i == 1) {
            memkind = -1;
            binding.memMenu01.setTextColor(Color.parseColor("#1445D0"));
            binding.memLine01.setBackgroundColor(Color.parseColor("#1445D0"));
        } else if (i == 2) {
            memkind = 1;
            binding.memMenu02.setTextColor(Color.parseColor("#1445D0"));
            binding.memLine02.setBackgroundColor(Color.parseColor("#1445D0"));
        } else if (i == 3) {
            memkind = 2; //(0,2,3)
            binding.memMenu03.setTextColor(Color.parseColor("#1445D0"));
            binding.memLine03.setBackgroundColor(Color.parseColor("#1445D0"));
        } else if (i == 4) {
            memkind = 3;
            binding.memMenu04.setTextColor(Color.parseColor("#1445D0"));
            binding.memLine04.setBackgroundColor(Color.parseColor("#1445D0"));
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
    private boolean isDragging = false;
    private int xMax;
    private int yMax;
    private int xDelta;
    private int yDelta;

    private void setAddBtnSetting() {
        add_worktime_btn = binding.getRoot().findViewById(R.id.add_worktime_btn);
        addbtn_tv = binding.getRoot().findViewById(R.id.addbtn_tv);
        addbtn_tv.setText("직원추가");
        add_worktime_btn.setVisibility(place_owner_id.equals(USER_INFO_ID) ? View.VISIBLE : View.GONE);

        // 제한된 영역
        int maxX = (int) binding.layoutTotal.getScaleX();
        int maxY = (int) binding.layoutTotal.getScaleY();
        // Set OnTouchListener to ImageView
        add_worktime_btn.setOnTouchListener(new View.OnTouchListener() {
            private int lastAction;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            int newX;
            int newY;
            private int lastnewX = 0;
            private int lastnewY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = v.getLeft();
                        initialY = v.getTop();
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        lastAction = MotionEvent.ACTION_DOWN;
                        isDragging = false;

                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (!isDragging) {
                            isDragging = true;
                        }

                        int dx = (int) (event.getRawX() - initialTouchX);
                        int dy = (int) (event.getRawY() - initialTouchY);

                        newX = initialX + dx;
                        newY = initialY + dy;

                        if(lastnewX == 0){ lastnewX = newX; }
                        if(lastnewY == 0){ lastnewY = newY; }

                        int parentWidth = ((ViewGroup) v.getParent()).getWidth();
                        int parentHeight = ((ViewGroup) v.getParent()).getHeight();
                        int childWidth = v.getWidth();
                        int childHeight = v.getHeight();

                        newX = Math.max(0, Math.min(newX, parentWidth - childWidth));
                        newY = Math.max(0, Math.min(newY, parentHeight - childHeight));

                        // Update the position of the ImageView
                        v.layout(newX, newY, newX + v.getWidth(), newY + v.getHeight());
                        break;

                    case MotionEvent.ACTION_UP:
                        lastAction = MotionEvent.ACTION_UP;
                        int Xdistance = (newX - lastnewX);
                        int Ydistance = (newY - lastnewY);
                        if (Math.abs(Xdistance) < 10 && Math.abs(Ydistance) < 10) {
                            MemberOption mo = new MemberOption();
                            mo.show(getSupportFragmentManager(), "MemberOption");
                        } else {
                            lastnewX = newX;
                            lastnewY = newY;
                        }
                        isDragging = false;
                        break;

                    default:
                        return false;
                }
                return true;
            }
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
