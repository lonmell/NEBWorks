package com.krafte.nebworks.ui.notify;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.WorkplaceNotifyAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.WorkPlaceEmloyeeNotifyData;
import com.krafte.nebworks.dataInterface.NotifyListInterface;
import com.krafte.nebworks.util.DBConnection;
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
    private final static String TAG = "EmployeeNotifyListActivity";
    Context mContext;

    //XML ID
    RecyclerView workplace_notify_list = null;
    LinearLayout back_btn;
    ArrayList<WorkPlaceEmloyeeNotifyData.WorkPlaceEmloyeeNotifyData_list> mList;
    //리사이클러뷰 데이터 변화시 위치 변경 없이 고정
    private Parcelable recyclerViewState;
    WorkplaceNotifyAdapter mAdapter = null;

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID, USER_INFO_NAME;
    String place_id = "";

    //Other
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();

    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    RetrofitConnect rc = new RetrofitConnect();
    int listitemsize = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);

        setContentLayout();
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


    private void setContentLayout() {
        workplace_notify_list = findViewById(R.id.workplace_notify_list);
        back_btn = findViewById(R.id.back_btn);
    }

    private void setBtnEvent() {
        back_btn.setOnClickListener(v -> {
            super.onBackPressed();
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
                dlog.e("response 1: " + response.isSuccessful());
                dlog.e("response 2: " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body();
                    try {
                        JSONArray Response = new JSONArray(jsonResponse);

                        if (listitemsize != Response.length()) {
                            mList = new ArrayList<>();
                            // 순서는 상관없을지도 모르지만 내 경우 이렇게 하면 작동했기 때문에 그냥 이렇게 쓰는 것이다

                            mAdapter = new WorkplaceNotifyAdapter(mContext, mList);
                            workplace_notify_list.setAdapter(mAdapter);
                            workplace_notify_list.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                            Log.i(TAG, "SetNoticeListview Thread run! ");
                            listitemsize = Response.length();
                            if (Response.length() == 0) {
                                Log.i(TAG, "GET SIZE : " + Response.length());
                            } else {
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    mAdapter.addItem(new WorkPlaceEmloyeeNotifyData.WorkPlaceEmloyeeNotifyData_list(
                                            jsonObject.getString("id"),
                                            jsonObject.getString("kind"),
                                            jsonObject.getString("title"),
                                            jsonObject.getString("contents"),
                                            jsonObject.getString("read_yn"),
                                            jsonObject.getString("sender_id"),
                                            jsonObject.getString("sender_name"),
                                            jsonObject.getString("push_date")
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
                                mAdapter.setOnItemClickListener(new WorkplaceNotifyAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position) {
                                        /*
                                        1 = 미완료 업무 >> 근로자 업무리스트 페이지로 이동
                                        2 = 그룹초대    >> 그룹초대 승인/거절 페이지 따로 만들기
                                        3 = 결재 요청 결과 알림 >> 결재 내역 페이지로 이동
                                        4 = 휴가 신청 결과 알림 >> 결재 내역 페이지로 이동
                                        5 = 일반 공지사항 >> 공지사항 상세보기 페이지
                                        6 = 이벤트 공지사항 >> 공지사항 상세보기 페이지
                                        7 = 근로계약서 >> 근로자 사인이 있을때 -> EmployerWriteContractAlba / 근로자 사인이 없을때 -> ContractActivity01 ~ ContractActivity04_1
                                        12 = 출퇴근 이력알림
                                        */
                                        try {
                                            String no = Response.getJSONObject(position).getString("no");
                                            int pos = position;
                                            String store_no = Response.getJSONObject(position).getString("store_no");
                                            String getSend_user_id = Response.getJSONObject(position).getString("send_user_id");
                                            String getRcv_user_id = Response.getJSONObject(position).getString("rcv_user_id");
                                            String store_name = Response.getJSONObject(position).getString("store_name");
                                            String notify_title = Response.getJSONObject(position).getString("notify_title");
                                            String readn_cnt = Response.getJSONObject(position).getString("readn_cnt");
                                            String notify_date = Response.getJSONObject(position).getString("notify_date");
                                            String notify_contents = Response.getJSONObject(position).getString("notify_contents");
                                            String notify_method = Response.getJSONObject(position).getString("notify_method");
                                            String notify_groupyn = Response.getJSONObject(position).getString("group_yn");
                                            String notify_readyn = Response.getJSONObject(position).getString("notify_readyn");
                                            String contractCnt = Response.getJSONObject(position).getString("contractCnt");

                                            Log.i("WorkplaceListAdapter", "No : " + no);
                                            Log.i("WorkplaceListAdapter", "pos : " + pos);
                                            Log.i("WorkplaceListAdapter", "store_no : " + store_no);
                                            Log.i("WorkplaceListAdapter", "getSend_user_id : " + getSend_user_id);
                                            Log.i("WorkplaceListAdapter", "getRcv_user_id : " + getRcv_user_id);
                                            Log.i("WorkplaceListAdapter", "item 1 : " + store_name);
                                            Log.i("WorkplaceListAdapter", "item 2 : " + notify_title);
                                            Log.i("WorkplaceListAdapter", "item 3 : " + readn_cnt);

                                            shardpref.putString("notify_no", no);
                                            shardpref.putInt("notify_pos", pos);
                                            shardpref.putString("notify_store_no", store_no);
                                            shardpref.putString("notify_title", notify_title);
                                            shardpref.putString("notify_date", notify_date);
                                            shardpref.putString("notify_store_name", store_name);
                                            shardpref.putString("notify_contents", notify_contents);
                                            shardpref.putString("notify_method", notify_method);
                                            shardpref.putString("notify_groupyn", notify_groupyn);
                                            shardpref.putString("notify_readyn", notify_readyn);
                                            shardpref.putString("returnPage", "EmployeeNotifyListActivity");
                                            if (USER_INFO_ID.equals(getRcv_user_id)) {
                                                shardpref.putString("Employer_id", getRcv_user_id);
                                                shardpref.putString("Employee_id", getSend_user_id);
                                            } else {
                                                shardpref.putString("Employer_id", getSend_user_id);
                                                shardpref.putString("Employee_id", getRcv_user_id);
                                            }

                                            UpdateWorkNotifyReadYn("3", no, store_no);

                                            if (notify_method.equals("2") || notify_method.equals("5")
                                                    || notify_method.equals("6") || notify_method.equals("9")) {
//                                                pm.EmployeeNotifyDetailR(mContext);
                                            } else if (notify_method.equals("7")) {
                                                int ContractCnt = Integer.parseInt(contractCnt);
                                                Log.i("WorkplaceListAdapter", "ContractCnt : " + ContractCnt);
                                                if (ContractCnt == 0) {
//                            pm.ContractActivity01(mContext);
                                                    shardpref.putString("returnPage","EmployeeNotifyListActivity");
//                                                    pm.ContractReady01(mContext);
                                                } else {
                                                    String Contract_uri = "";
                                                    Contract_uri = "http://krafte.net/mobile/ContractAlba.php?store_no=" + store_no
                                                            + "&employer_id=" + getSend_user_id + "&employee_id=" + getRcv_user_id;

                                                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Contract_uri)));
                                                }
                                            } else if (notify_method.equals("8")) {
                                                //지원자(근로자)가 점주(고용주)에게 보낸 이력서
//                                                pm.EmployerApplicantData(mContext);
                                            } else if (notify_method.equals("10")) {
//                                                pm.EmployeeStoreDetail(mContext);
                                            } else if (notify_method.equals("12")) {
                                                //출퇴근 알림 - 터치하면 삭제
                                                Log.i(TAG,"notify_method : " + notify_method);
                                                UpdateWorkNotifyReadYn("4", no, store_no);
                                                SetWorkplaceList();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
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
    private void UpdateWorkNotifyReadYn(String flag, String notify_no, String notify_store_no) {

//        @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
//            dbConnection.WorkNotifyManagement(flag, notify_no, notify_store_no, "", "", 0, "", "");
////                    Log.i(TAG, "Result = " + resultData.getRESULT());
//            String getMessage = resultData.getRESULT().replaceAll("\"", "");
//            dlog.i("getMessage = " + getMessage);
//
//            if (getMessage.equals("success")) {
//                if (flag.equals("4")) {
//
//                } else {
//                    dlog.i("getMessage = " + getMessage);
//                }
//            } else {
//                Toast.makeText(mContext, "이력서전송에 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
//            }
//        });
//        th.start();
//        try {
//            th.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
