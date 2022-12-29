package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.AssignmentMemberAdapter;
import com.krafte.nebworks.data.PlaceMemberListData;
import com.krafte.nebworks.dataInterface.AllMemberInterface;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.TaskreuseAssignmember;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class WorkAssigmentContentsPop extends Activity {
    private static final String TAG = "WorkAssigmentContentsPop";
    Context mContext;

    TextView txtText, pop_event, not_data_txt, txtText2, title_tv;
    LinearLayout assignment_flag_area, member_list_area;
    RecyclerView member_list;
    ImageView workimg;

    String flag = "";
    String data = "";
    String title = "";
    Intent intent;

    //shared Data
    PreferenceHelper shardpref;
    String place_id = "";
    String place_name = "";
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String task_no = "";

    String sendToken = "";

    String user_id = "";
    int EmployeeSelect = 0;
    String SelectEmployeeid = "";
    String user_thumnail_url = "";
    int assignment_kind;
    String name = "";

    //Other
    Dlog dlog = new Dlog();
    DateCurrent dc = new DateCurrent();
    RetrofitConnect rc = new RetrofitConnect();

    String message = "";
    String topic = "";

    List<String> Ac_memberArray = new ArrayList<>();
    AssignmentMemberAdapter mAdapter;
    ArrayList<PlaceMemberListData.PlaceMemberListData_list> mList = new ArrayList<>();
    int total_member_cnt;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"SetTextI18n", "LongLogTag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.workassigment_contentspop);

        mContext = this;
        dlog.DlogContext(mContext);

        shardpref = new PreferenceHelper(mContext);
        task_no = shardpref.getString("task_no", "");
        assignment_kind = shardpref.getInt("assignment_kind", 0);
        place_id = shardpref.getString("place_id", "");
        place_name = shardpref.getString("place_name", "");
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");

        //데이터 가져오기
        intent = getIntent();
        data = intent.getStringExtra("data");
        flag = intent.getStringExtra("flag");
        task_no = intent.getStringExtra("task_no");
        name = intent.getStringExtra("name");
        user_thumnail_url = intent.getStringExtra("profileimg");
        title = intent.getStringExtra("title");

        setContentLayout();
        setBtnEvent();

        if (flag.equals("tap2")) {
            txtText2.setVisibility(View.GONE);
            member_list_area.setVisibility(View.GONE);
            member_list.setVisibility(View.GONE);
            pop_event.setText("닫기");
        } else if (flag.equals("tap3")) {
            txtText2.setVisibility(View.VISIBLE);
            member_list_area.setVisibility(View.VISIBLE);
            member_list.setVisibility(View.VISIBLE);
            pop_event.setText("배정");
            SetAllMemberList();
        }

        shardpref = new PreferenceHelper(mContext);

        title_tv.setText(title);
        txtText.setText(data);
    }

    //UI 객체생성
    private void setContentLayout() {
        txtText = findViewById(R.id.txtText);
        not_data_txt = findViewById(R.id.not_data_txt);
        pop_event = findViewById(R.id.pop_event);
        member_list = findViewById(R.id.member_list);
        workimg = findViewById(R.id.workimg);
        txtText2 = findViewById(R.id.txtText2);
        member_list_area = findViewById(R.id.member_list_area);
        title_tv = findViewById(R.id.title_tv);
    }

    public interface OnClickListener {
        void OnClick();
    }

    private WorkAssigmentContentsPop.OnClickListener mListener = null;

    public void setOnClickListener(WorkAssigmentContentsPop.OnClickListener listener) {
        this.mListener = listener;
    }


    //확인 버튼 클릭
    @SuppressLint("LongLogTag")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setBtnEvent() {
        dlog.i("flag : " + flag);
        pop_event.setOnClickListener(v -> {
            if (flag.equals("tap2")) {
                //액티비티(팝업) 닫기
                finish();
                Intent intent = new Intent();
                intent.putExtra("result", "Close Popup");
                setResult(RESULT_OK, intent);
                overridePendingTransition(0, R.anim.translate_down);
            } else if (flag.equals("tap3")) {
                if (mListener != null) {
                    mListener.OnClick();
                }
                SendUserCheck(1);
                SaveAddWork();
            }

        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //액티비티(팝업) 닫기
        finish();
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);
        overridePendingTransition(0, R.anim.translate_down);
    }

    @Override
    public void onStop() {
        super.onStop();
        shardpref.remove("task_no");
    }

    /*직원 전체 리스트 START*/
    public void SetAllMemberList() {
        total_member_cnt = 0;
        @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AllMemberInterface.URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            AllMemberInterface api = retrofit.create(AllMemberInterface.class);
            Call<String> call = api.getData(place_id,"");
//            @Field("flag") int flag,
//            @Field("place_id") String place_id,
//            @Field("user_id") String user_id,
//            @Field("getMonth") String getMonth
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.e("onSuccess : ", response.body());
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(response.body());

                            mList = new ArrayList<>();
                            mAdapter = new AssignmentMemberAdapter(mContext, mList);
                            member_list.setAdapter(mAdapter);
                            member_list.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));

                            if (Response.length() == 0) {
                                not_data_txt.setVisibility(View.VISIBLE);
                                member_list.setVisibility(View.GONE);
                            } else {
                                not_data_txt.setVisibility(View.GONE);
                                member_list.setVisibility(View.VISIBLE);
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    mAdapter.addItem(new PlaceMemberListData.PlaceMemberListData_list(
                                            jsonObject.getString("id"),
                                            jsonObject.getString("name"),
                                            jsonObject.getString("phone"),
                                            jsonObject.getString("gender"),
                                            jsonObject.getString("img_path"),
                                            jsonObject.getString("jumin"),
                                            jsonObject.getString("join_date"),
                                            jsonObject.getString("state"),
                                            jsonObject.getString("jikgup"),
                                            jsonObject.getString("pay")
                                    ));
                                }
                                mAdapter.notifyDataSetChanged();

                                mAdapter.setOnItemClickListener(new AssignmentMemberAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position, List<String> memberArray) {
                                        try {
                                            int cnt = 0;
                                            dlog.i("Select Member id :" + Response.getJSONObject(position).getString("id"));
                                            user_id = Response.getJSONObject(position).getString("id");
                                            EmployeeSelect = 1;

                                            Ac_memberArray = memberArray.stream().distinct().collect(Collectors.toList());
                                            dlog.i("Ac_memberArray :" + Ac_memberArray);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
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


    //업무 저장(추가)
    private void SaveAddWork() {
        String today = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
        String users = shardpref.getString("users", "0");
        if (Ac_memberArray.size() == 0) {
            Ac_memberArray.addAll(Arrays.asList(users.split(", ")));
        }
        Ac_memberArray.remove("0");
        user_id = Ac_memberArray.toString().replace(" ", "").replace("[", "").replace("]", "").trim();

        total_member_cnt = Ac_memberArray.size();
        dlog.i("-----SaveAddWork-----");
        dlog.i("id = " + task_no);
        dlog.i("writer_id = " + USER_INFO_ID);
        dlog.i("task_date = " + today);
        dlog.i("users = " + user_id);
        dlog.i("-----SaveAddWork-----");

        if (user_id.equals("")) {
            Toast.makeText(mContext, "업무를 배정할 직원을 선택해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
                runOnUiThread(() -> {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(TaskreuseAssignmember.URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .build();
                    TaskreuseAssignmember api = retrofit.create(TaskreuseAssignmember.class);
                    Call<String> call = api.getData(task_no, USER_INFO_ID, today, user_id);
                    call.enqueue(new Callback<String>() {
                        @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            dlog.e("setRecyclerView function START");
                            dlog.e("response 1: " + response.isSuccessful());
                            dlog.e("response 2: " + response.body());
                            if (response.isSuccessful() && response.body() != null) {
                                String jsonResponse = response.body();
                                if (jsonResponse.replace("\"", "").equals("success")) {
//                                            getEmployeeToken(SelectEmployeeid);
//                                            message = "업무가 배정되었습니다.";
//                                            click_action = "EmployeeMyWorkList";
//                                            if (EmployeeChannelId1) {
//                                                FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
//                                                    dlog.i("token : " + token);
//                                                    FcmTestFunctionCall();
//                                                });
//                                            }
                                    Toast.makeText(mContext, "업무배정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    dlog.i("success");
                                    //액티비티(팝업) 닫기
                                    finish();
                                    Intent intent = new Intent();
                                    intent.putExtra("result", "Close Popup");
                                    setResult(RESULT_OK, intent);
                                    overridePendingTransition(0, R.anim.translate_down);
                                } else {
                                    Toast.makeText(mContext, "동일한 업무가 이미 등록되어 있습니다.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }

                        @SuppressLint("LongLogTag")
                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            dlog.e("에러 = " + t.getMessage());
                        }
                    });
                });
            });
            th.start();
            try {
                th.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    /* -- 할일 추가 FCM 전송 영역 */
    private void SendUserCheck(int flag){
        List<String> member = new ArrayList<>();
        dlog.i("보내야 하는 직원 배열 :" + user_id);
        member.addAll(Arrays.asList(user_id.split(",")));
        dlog.i("보내야 하는 직원 수 :" + member.size());
        dlog.i("보내야 하는 직원 List<String>  :" + member);
        if(flag == 1){
            message = "[배정업무] :" + title_tv.getText().toString();
        }else if(flag == 2){
            message = "[배정업무수정] :" + title_tv.getText().toString();
        }
        for(int a = 0; a < member.size(); a++){
            getManagerToken(member.get(a),"1", place_id,place_name);
        }
    }
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
                dlog.i("Response Result : " + response.body());
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
                        boolean channelId1 = Response.getJSONObject(0).getString("channel2").equals("1");
                        if (!token.isEmpty() && channelId1) {
                            String message = department + " " + position + " " + name + " 님 " + place_name + "에서 업무가 배정되었습니다.";
                            PushFcmSend(id, "", message, token, "2", place_id);
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
            click_action = "PlaceListActivity";
            dbConnection.FcmTestFunction(topic,title,message,token,click_action,tag,place_id);
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
    /* -- 할일 추가 FCM 전송 영역 */

}
