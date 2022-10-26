package com.krafte.kogas.fragment.approval;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.kogas.R;
import com.krafte.kogas.adapter.ApprovalAdapter;
import com.krafte.kogas.data.TaskCheckData;
import com.krafte.kogas.dataInterface.ApprovalUpdateInterface;
import com.krafte.kogas.dataInterface.FCMSelectInterface;
import com.krafte.kogas.dataInterface.TaskSapprovalInterface;
import com.krafte.kogas.pop.DatePickerActivity;
import com.krafte.kogas.pop.OneButtonPopActivity;
import com.krafte.kogas.pop.OneButtonTItlePopActivity;
import com.krafte.kogas.util.DBConnection;
import com.krafte.kogas.util.DateCurrent;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PageMoveClass;
import com.krafte.kogas.util.PreferenceHelper;
import com.krafte.kogas.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApprovalFragment2 extends Fragment {

    private static final String TAG = "ApprovalFragment2";
    Context mContext;
    Activity activity;
    public static int SUCCESS_POSITION = 0;

    //XML ID
    RecyclerView total_applove_list1;
    TextView reject_btn, accept_btn;
    LinearLayout all_check;
    ImageView all_checkbox;
    TextView check_cnt,selectdate;
    LinearLayout search_date,bottom_area;
    RelativeLayout cnt_area;

    //sharedPreferences
    PreferenceHelper shardpref;
    String place_id = "";
    String place_name = "";
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";

    //Other
    RetrofitConnect rc = new RetrofitConnect();
    ArrayList<TaskCheckData.TaskCheckData_list> mList;
    ApprovalAdapter mAdapter = null;
    PageMoveClass pm = new PageMoveClass();
    DateCurrent dc = new DateCurrent();
    Dlog dlog = new Dlog();

    boolean AllCheck = false;
    int i = 0;
    String totalSendCheck = "";
    String totalSendUser = "";
    String message = "";
    String toDay = "";

    public static ApprovalFragment2 newInstance(int number) {
        ApprovalFragment2 fragment = new ApprovalFragment2();
        Bundle bundle = new Bundle();
        bundle.putInt("number", number);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            int num = getArguments().getInt("number");
        }
    }

    /*Fragment 콜백함수*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.approval_fragment1, container, false);
        mContext = inflater.getContext();
        dlog.DlogContext(mContext);

        total_applove_list1 = rootView.findViewById(R.id.total_applove_list1);
        accept_btn = rootView.findViewById(R.id.accept_btn);
        reject_btn = rootView.findViewById(R.id.reject_btn);
        all_check = rootView.findViewById(R.id.all_check);
        all_checkbox = rootView.findViewById(R.id.all_checkbox);
        check_cnt = rootView.findViewById(R.id.check_cnt);
        search_date = rootView.findViewById(R.id.search_date);
        selectdate = rootView.findViewById(R.id.selectdate);
        cnt_area = rootView.findViewById(R.id.cnt_area);
        bottom_area = rootView.findViewById(R.id.bottom_area);


        shardpref = new PreferenceHelper(mContext);
        place_id = shardpref.getString("place_id", "0");
        place_name = shardpref.getString("place_name", "0");
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
        USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "1");

        Log.i(TAG, "place_id :" + place_id);
        Log.i(TAG, "USER_INFO_ID :" + USER_INFO_ID);
        shardpref.remove("checkworkno");
        shardpref.remove("ConductUser");

        toDay = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
        selectdate.setText(toDay);
        selectdate.setOnClickListener(v -> {
            shardpref.putInt("timeSelect_flag", 6);
            Intent intent = new Intent(mContext, DatePickerActivity.class);
            startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
        });
//        Timer timer = new Timer();
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                rc.workCheckListData_lists.clear();
//                GetApprovalList();
//            }
//        };
//        timer.schedule(timerTask,0,5000);
//
        reject_btn.setText("승인취소");
        reject_btn.setOnClickListener(v -> {
            //반려
            dlog.i("----------totalSendCheck-----------");
            String getArray = shardpref.getString("checkworkno", "").replace("[", "").replace("]", "").replace(",", "").replace("  ", " ").trim();
            dlog.i("getArray : " + getArray);
            String[] splitArray;
            splitArray = getArray.split(" ");
            dlog.i("splitArray : " + splitArray);
            totalSendCheck = Arrays.toString(splitArray).replace("[", "").replace("]", "").replace(" ", "");
            dlog.i("totalSendCheck : " + totalSendCheck);
            dlog.i("----------totalSendCheck-----------");

            dlog.i("----------totalSendUser-----------");
            String getArray1 = shardpref.getString("ConductUser", "").replace("[", "").replace("]", "").replace(",", "").replace("  ", " ").trim();
            dlog.i("getArray1 : " + getArray1);
            String[] splitArray1;
            splitArray1 = getArray1.replace("  ", " ").split(" ");
            dlog.i("splitArray1 : " + splitArray1.length);
            totalSendUser = Arrays.toString(splitArray1).replace("[", "").replace("]", "").replace(" ", "");
            dlog.i("----------totalSendUser-----------");

            if (totalSendCheck.isEmpty()) {
                Intent intent = new Intent(mContext, OneButtonPopActivity.class);
                intent.putExtra("data", "선택한 업무가 없습니다.");
                intent.putExtra("left_btn_txt", "닫기");
                startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
            } else {
                SUCCESS_POSITION = 2;
                setUpdateWorktodo("0",totalSendCheck);
                SendUserCheck(1, "승인취소");
            }
//            totalSendCheck = "";
//            totalSendUser = "";
        });

        accept_btn.setText("반려");
        accept_btn.setOnClickListener(v -> {
            //승인
            dlog.i("----------totalSendCheck-----------");
            String getArray = shardpref.getString("checkworkno", "").replace("[", "").replace("]", "").replace(",", "").replace("  ", " ").trim();
            dlog.i("getArray : " + getArray);
            String[] splitArray;
            splitArray = getArray.replace("  ", " ").split(" ");
            dlog.i("splitArray : " + splitArray.length);
            totalSendCheck = Arrays.toString(splitArray).replace("[", "").replace("]", "").replace(" ", "");
            dlog.i("totalSendCheck : " + totalSendCheck);
            dlog.i("----------totalSendCheck-----------");

            dlog.i("----------totalSendUser-----------");
            String getArray1 = shardpref.getString("ConductUser", "").replace("[", "").replace("]", "").replace(",", "").replace("  ", " ").trim();
            dlog.i("getArray1 : " + getArray1);
            String[] splitArray1;
            splitArray1 = getArray1.replace("  ", " ").split(" ");
            dlog.i("splitArray1 : " + splitArray1.length);
            totalSendUser = Arrays.toString(splitArray1).replace("[", "").replace("]", "").replace(" ", "");
            dlog.i("----------totalSendUser-----------");

            if (totalSendCheck.isEmpty()) {
                Intent intent = new Intent(mContext, OneButtonPopActivity.class);
                intent.putExtra("data", "선택한 업무가 없습니다.");
                intent.putExtra("left_btn_txt", "닫기");
                startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
            } else {
                SUCCESS_POSITION = 1;
                setUpdateWorktodo("2", totalSendCheck);
                SendUserCheck(1, "반려");
            }
//            totalSendCheck = "";
//            totalSendUser = "";
        });

        all_check.setOnClickListener(v -> {
//            mList.clear();
            dlog.i("-------------all_check-------------");
            if (!AllCheck) {
                AllCheck = true;
                totalSendCheck = "";
                totalSendUser = "";
                all_checkbox.setBackgroundResource(R.drawable.checkbox_on);
                GetApprovalList();
                check_cnt.setText(mList.size() + "건");
                dlog.i("totalSendCheck : " + totalSendCheck);
            } else {
                AllCheck = false;
                totalSendCheck = "";
                totalSendUser = "";
                shardpref.remove("checkworkno");
                shardpref.remove("ConductUser");
                all_checkbox.setBackgroundResource(R.drawable.checkbox_off);
                GetApprovalList();
                check_cnt.setText("0건");
            }
            dlog.i("-------------all_check-------------");
        });
        return rootView;
    }


    /* -- 할일 추가 FCM 전송 영역 */
    private void SendUserCheck(int flag, String state) {
        List<String> member = new ArrayList<>();
        dlog.i("보내야 하는 직원 배열 :" + totalSendUser);
        member.addAll(Arrays.asList(totalSendUser.split(",")));
        dlog.i("보내야 하는 직원 수 :" + member.size());
        dlog.i("보내야 하는 직원 List<String>  :" + member);

        for (int a = 0; a < member.size(); a++) {
            getManagerToken(member.get(a), "1", place_id, place_name,state);
        }
    }

    public void getManagerToken(String user_id, String type, String place_id, String place_name, String state) {
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
                        String department = shardpref.getString("USER_INFO_SOSOK", "");
                        String position = shardpref.getString("USER_INFO_JIKGUP", "");
                        String name = shardpref.getString("USER_INFO_NAME", "");
                        dlog.i("-----getManagerToken-----");
                        boolean channelId1 = Response.getJSONObject(0).getString("channel1").equals("1");
                        if (!token.isEmpty() && channelId1) {
                            message = "[" + place_name + "]" + state + "된 업무보고가 있습니다.";
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

    DBConnection dbConnection = new DBConnection();
    String click_action = "";

    private void PushFcmSend(String topic, String title, String message, String token, String tag, String place_id) {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            click_action = "TaskApprovalFragment";
            dlog.i("------FcmTestFunction------");
            dlog.i("topic : " + topic);
            dlog.i("title : " + title);
            dlog.i("message : " + message);
            dlog.i("token : " + token);
            dlog.i("click_action : " + click_action);
            dlog.i("tag : " + tag);
            dlog.i("place_id : " + place_id);
            dlog.i("------FcmTestFunction------");
            dbConnection.FcmTestFunction(topic, title, message, token, click_action, tag, place_id);
            activity.runOnUiThread(() -> {
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

    public void setUpdateWorktodo(String kind, String task_no) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApprovalUpdateInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ApprovalUpdateInterface api = retrofit.create(ApprovalUpdateInterface.class);
//        task_no.replace(",","|")
        Call<String> call = api.getData(task_no,USER_INFO_ID,kind,"");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG,"setUpdateWorktodo function START");
                Log.e(TAG,"response 1: " + response.isSuccessful());
                Log.e(TAG,"response 2: " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().replace("\"", "").equals("success")) {
                        Intent intent = new Intent(mContext, OneButtonTItlePopActivity.class);
                        if(kind.equals("0")){
                            intent.putExtra("title", "승인 취소");
                            intent.putExtra("data", "승인취소 처리가 완료 되었습니다.");
                        }else{
                            intent.putExtra("title", "반려 완료");
                            intent.putExtra("data", "반려처리가 완료 되었습니다.");
                        }
                        mContext.startActivity(intent);
                        ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                        GetApprovalList();
                    } else {
                        Toast.makeText(mContext, "Error", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }


    public void GetApprovalList() {
        dlog.i("-----GetApprovalList-----");
        dlog.i("place_id : " + place_id);
        dlog.i("state : 1");
        dlog.i("selectdate : " + selectdate.getText().toString());
        dlog.i("-----GetApprovalList-----");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TaskSapprovalInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TaskSapprovalInterface api = retrofit.create(TaskSapprovalInterface.class);
        Call<String> call = api.getData(place_id,"1",selectdate.getText().toString());
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//                Log.e(TAG,"GetApprovalList1 function START");
//                Log.e(TAG,"response 1: " + response.isSuccessful());
//                Log.e(TAG,"response 2: " + rc.getBase64decode(response.body()));
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
//                    Log.e(TAG,"GetWorkStateInfo2 function onSuccess : " + jsonResponse);
                    try {
                        JSONArray Response = new JSONArray(response.body());

                        mList = new ArrayList<>();
                        mAdapter = new ApprovalAdapter(mContext, mList, 3, 1, AllCheck);
                        total_applove_list1.setAdapter(mAdapter);
                        total_applove_list1.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                        Log.i(TAG, "SetNoticeListview Thread run! ");

                        if (Response.length() == 0) {
                            Log.i(TAG, "GET SIZE : " + Response.length());
                            check_cnt.setText("0건");
                            all_checkbox.setClickable(false);
                            all_checkbox.setEnabled(false);
                            all_checkbox.setBackgroundResource(R.drawable.checkbox_off);
                        } else {
                            for (int i = 0; i < Response.length(); i++) {
                                JSONObject jsonObject = Response.getJSONObject(i);
                                mAdapter.addItem(new TaskCheckData.TaskCheckData_list(
                                        jsonObject.getString("id"),
                                        jsonObject.getString("state"),
                                        jsonObject.getString("requester_id"),
                                        jsonObject.getString("requester_name"),
                                        jsonObject.getString("requester_img_path"),
                                        jsonObject.getString("requester_department"),
                                        jsonObject.getString("requester_position"),
                                        jsonObject.getString("title"),
                                        jsonObject.getString("contents"),
                                        jsonObject.getString("complete_kind"),
                                        jsonObject.getString("end_time"),
                                        jsonObject.getString("complete_time"),
                                        jsonObject.getString("task_img_path"),
                                        jsonObject.getString("complete_yn"),
                                        jsonObject.getString("incomplete_reason"),
                                        jsonObject.getString("reject_reason"),
                                        jsonObject.getString("task_date"),
                                        jsonObject.getString("request_date"),
                                        jsonObject.getString("approval_date")
                                ));
                            }

                            mAdapter.notifyDataSetChanged();
                            mAdapter.setOnItemClickListener((v, position, Tcnt, Fcnt) -> {
                                check_cnt.setText(Tcnt + "건");
                                if (Tcnt == mList.size() && Fcnt == 0) {
                                    all_checkbox.setBackgroundResource(R.drawable.checkbox_on);
                                } else {
                                    all_checkbox.setBackgroundResource(R.drawable.checkbox_off);
                                }

                                dlog.i("----------totalSendUser-----------");
                                String getUArray = shardpref.getString("ConductUser", "").replace("[", "").replace("]", "").replace(",", "").replace("  ", " ").trim();
                                String[] splitUArray;
                                splitUArray = getUArray.split(" ");
                                totalSendUser = Arrays.toString(splitUArray).replace("[", "").replace("]", "").replace(" ", "");
                                dlog.i("----------totalSendUser-----------");
                            });
                            mAdapter.setOnItemClickListener2((v, position) -> {
                                try {
                                    shardpref.putString("id", Response.getJSONObject(position).getString("id"));
                                    shardpref.putString("state", Response.getJSONObject(position).getString("state"));
                                    shardpref.putString("requester_id", Response.getJSONObject(position).getString("requester_id"));
                                    shardpref.putString("requester_name", Response.getJSONObject(position).getString("requester_name"));
                                    shardpref.putString("requester_img_path", Response.getJSONObject(position).getString("requester_img_path"));
                                    shardpref.putString("title", Response.getJSONObject(position).getString("title"));
                                    shardpref.putString("contents", Response.getJSONObject(position).getString("contents"));
                                    shardpref.putString("complete_kind", Response.getJSONObject(position).getString("complete_kind"));
                                    shardpref.putString("end_time", Response.getJSONObject(position).getString("end_time"));
                                    shardpref.putString("complete_time", Response.getJSONObject(position).getString("complete_time"));
                                    shardpref.putString("task_img_path", Response.getJSONObject(position).getString("task_img_path"));
                                    shardpref.putString("complete_yn", Response.getJSONObject(position).getString("complete_yn"));
                                    shardpref.putString("incomplete_reason", Response.getJSONObject(position).getString("incomplete_reason"));
                                    shardpref.putString("reject_reason", Response.getJSONObject(position).getString("reject_reason"));
                                    shardpref.putString("task_date", Response.getJSONObject(position).getString("task_date"));
                                    shardpref.putString("request_date", Response.getJSONObject(position).getString("request_date"));
                                    shardpref.putString("approval_date", Response.getJSONObject(position).getString("approval_date"));
                                    pm.ApprovalDetailGo(mContext);
                                } catch (Exception e) {
                                    dlog.i("mAdapter setOnItemClickListener2 Exception : " + e);
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
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (selectdate.getText().toString().isEmpty()) {
            String getDatePicker = shardpref.getString("vDateGetDate", "");
            if (getDatePicker.isEmpty()) {
                selectdate.setText(toDay);
            } else {
                selectdate.setText(getDatePicker);
            }
        }
        if (!USER_INFO_AUTH.equals("0")) {
            cnt_area.setVisibility(View.GONE);
            search_date.setVisibility(View.GONE);
            bottom_area.setVisibility(View.GONE);
        } else {
            cnt_area.setVisibility(View.VISIBLE);
            search_date.setVisibility(View.VISIBLE);
            bottom_area.setVisibility(View.VISIBLE);
        }
        GetApprovalList();


        int timeSelect_flag = shardpref.getInt("timeSelect_flag", 0);
        if (timeSelect_flag == 6) {
            //-- DatePickerActivity에서 받아오는 값
            String getDatePicker = shardpref.getString("vDateGetDate", "");
            selectdate.setText(getDatePicker);
            shardpref.remove("timeSelect_flag");
            GetApprovalList();
        }
    }

}
