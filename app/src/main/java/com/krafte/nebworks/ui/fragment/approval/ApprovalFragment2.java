package com.krafte.nebworks.ui.fragment.approval;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.ApprovalAdapter;
import com.krafte.nebworks.data.TaskCheckData;
import com.krafte.nebworks.dataInterface.ApprovalUpdateInterface;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.TaskSapprovalInterface;
import com.krafte.nebworks.dataInterface.TaskSelectMInterface;
import com.krafte.nebworks.databinding.ApprovalFragment1Binding;
import com.krafte.nebworks.pop.OneButtonPopActivity;
import com.krafte.nebworks.pop.OneButtonTItlePopActivity;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApprovalFragment2 extends Fragment {
    private ApprovalFragment1Binding binding;
    private static final String TAG = "ApprovalFragment2";
    Context mContext;
    Activity activity;
    public static int SUCCESS_POSITION = 0;

    //sharedPreferences
    PreferenceHelper shardpref;
    String place_id = "";
    String place_name = "";
    String place_owner_id = "";
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";

    //Other
    ArrayList<TaskCheckData.TaskCheckData_list> mList;
    ApprovalAdapter mAdapter = null;
    PageMoveClass pm = new PageMoveClass();
    DateCurrent dc = new DateCurrent();
    Dlog dlog = new Dlog();

    boolean AllCheck = false;
    String totalSendCheck = "";
    String totalSendUser = "";
    String totalTaskno = "";
    String totalTaskdate = "";

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

//        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.approval_fragment1, container, false);
        binding = ApprovalFragment1Binding.inflate(inflater);
        mContext = inflater.getContext();
        dlog.DlogContext(mContext);

        shardpref = new PreferenceHelper(mContext);
        place_id = shardpref.getString("place_id", "0");
        place_name = shardpref.getString("place_name", "0");
        place_owner_id = shardpref.getString("place_owner_id", "0");
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
        USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "1");

        Log.i(TAG, "place_id :" + place_id);
        Log.i(TAG, "USER_INFO_ID :" + USER_INFO_ID);
        shardpref.remove("checkworkno");
        shardpref.remove("ConductUser");

        toDay = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
        binding.selectdate.setText(toDay);
        //--binding.selectdate 변경
//                shardpref.putInt("timeSelect_flag", 6);

//                Intent intent = new Intent(mContext, DatePickerActivity.class);
//                startActivity(intent);
//                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String Month = String.valueOf(month+1);
                String Day = String.valueOf(dayOfMonth);
                Day = Day.length()==1?"0"+Day:Day;
                Month = Month.length()==1?"0"+Month:Month;
                binding.selectdate.setText(year +"-" + Month + "-" + Day);
                GetApprovalList();
            }
        }, mYear, mMonth, mDay);

        binding.selectdate.setOnClickListener(view -> {
            if (binding.selectdate.isClickable()) {
                datePickerDialog.show();
            }
        });
        //--binding.selectdate 변경
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
        binding.rejectBtn.setText("승인취소");
        binding.rejectBtn.setOnClickListener(v -> {
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

            dlog.i("----------totaltask_no-----------");
            String getArray2 = shardpref.getString("tasknoList", "").replace("[", "").replace("]", "").replace(",", "").replace("  ", " ").trim();
            dlog.i("getArray2 : " + getArray1);
            List<String> tasknoList = new ArrayList<>(Arrays.asList(getArray2.split(",")));
            dlog.i("splitArray2 : " + tasknoList.size());
            totalTaskno = tasknoList.toString();
            dlog.i("----------totaltask_no-----------");

            dlog.i("----------totaltaskdate-----------");
            String getArray3 = shardpref.getString("taskdate", "").replace("[", "").replace("]", "").replace(",", "").replace("  ", " ").trim();
            dlog.i("getArray3 : " + getArray3);
            List<String> taskdate = new ArrayList<>(Arrays.asList(getArray3.split(",")));
            dlog.i("splitArray3 : " + taskdate.size());
            totalTaskdate = taskdate.toString().replace("[", "").replace("]", "").replace(" ", "");
            dlog.i("----------totaltaskdate-----------");

            if (totalSendCheck.isEmpty()) {
                Intent intent = new Intent(mContext, OneButtonPopActivity.class);
                intent.putExtra("data", "선택한 업무가 없습니다.");
                intent.putExtra("left_btn_txt", "닫기");
                startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
            } else {
                SUCCESS_POSITION = 0;
                setUpdateWorktodo("0", totalSendCheck);
//                SendUserCheck(1, "승인취소");
                for(int i = 0; i < tasknoList.size(); i++){
                    setTodoData(taskdate.get(i),tasknoList.get(i));
                }
            }
            totalSendCheck = "";
            totalSendUser = "";
        });

        binding.acceptBtn.setText("반려");
        binding.acceptBtn.setOnClickListener(v -> {
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

            dlog.i("----------totaltask_no-----------");
            String getArray2 = shardpref.getString("tasknoList", "").replace("[", "").replace("]", "").replace(",", "").replace("  ", " ").trim();
            dlog.i("getArray2 : " + getArray1);
            List<String> tasknoList = new ArrayList<>(Arrays.asList(getArray2.split(",")));
            dlog.i("splitArray2 : " + tasknoList.size());
            totalTaskno = tasknoList.toString();
            dlog.i("----------totaltask_no-----------");

            dlog.i("----------totaltaskdate-----------");
            String getArray3 = shardpref.getString("taskdate", "").replace("[", "").replace("]", "").replace(",", "").replace("  ", " ").trim();
            dlog.i("getArray3 : " + getArray3);
            List<String> taskdate = new ArrayList<>(Arrays.asList(getArray3.split(",")));
            dlog.i("splitArray3 : " + taskdate.size());
            totalTaskdate = taskdate.toString().replace("[", "").replace("]", "").replace(" ", "");
            dlog.i("----------totaltaskdate-----------");

            if (totalSendCheck.isEmpty()) {
                Intent intent = new Intent(mContext, OneButtonPopActivity.class);
                intent.putExtra("data", "선택한 업무가 없습니다.");
                intent.putExtra("left_btn_txt", "닫기");
                startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
            } else {
                SUCCESS_POSITION = 2;
                setUpdateWorktodo("2", totalSendCheck);
                for(int i = 0; i < tasknoList.size(); i++){
                    setTodoData(taskdate.get(i),tasknoList.get(i));
                }
            }
            totalSendCheck = "";
            totalSendUser = "";
        });

        binding.allCheck.setOnClickListener(v -> {
//            mList.clear();
            dlog.i("-------------binding.allCheck-------------");
            if (!AllCheck) {
                AllCheck = true;
                totalSendCheck = "";
                totalSendUser = "";
                binding.allCheckbox.setBackgroundResource(R.drawable.checkbox_on);
                GetApprovalList();
                binding.checkCnt.setText(mList.size() + "건");
                dlog.i("totalSendCheck : " + totalSendCheck);
            } else {
                AllCheck = false;
                totalSendCheck = "";
                totalSendUser = "";
                shardpref.remove("checkworkno");
                shardpref.remove("ConductUser");
                binding.allCheckbox.setBackgroundResource(R.drawable.checkbox_off);
                GetApprovalList();
                binding.checkCnt.setText("0건");
            }
            dlog.i("-------------binding.allCheck-------------");
        });
        return binding.getRoot();
    }


    List<String> inmember = new ArrayList<>();
    List<String> user_id = new ArrayList<>();
    List<String> task_member_id = new ArrayList<>();
    public void setTodoData(String selectdate, String task_no) {
        dlog.i("setTodoMList place_id : " + place_id);
        dlog.i("setTodoMList binding.selectdate : " + selectdate);
        dlog.i("setTodoMList task_no : " + task_no);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TaskSelectMInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TaskSelectMInterface api = retrofit.create(TaskSelectMInterface.class);
        Call<String> call = api.getData(place_id,selectdate);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "WorkTapListFragment1 / setRecyclerView");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                Log.e(TAG, "response 2: " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    Log.e(TAG, "GetWorkStateInfo function onSuccess : " + response.body());
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(response.body());
                        Log.i(TAG, "GET SIZE : " + Response.length());
                        if (Response.length() == 0) {
                            Log.i(TAG, "SetNoticeListview Thread run! ");
                            Log.i(TAG, "GET SIZE : " + Response.length());
                        } else {
                            for (int i = 0; i < Response.length(); i++) {
                                JSONObject jsonObject = Response.getJSONObject(i);
                                if(jsonObject.getString("id").equals(task_no)){
                                    task_member_id = Collections.singletonList(jsonObject.getString("users"));
                                }
                            }
                            JSONArray Response2 = new JSONArray(task_member_id.toString().replace("[[", "[").replace("]]", "]"));
                            if (Response2.length() > 3) {
                                for (int i = 0; i < 3; i++) {
                                    JSONObject jsonObject = Response2.getJSONObject(i);
                                    inmember.add(jsonObject.getString("user_id"));
                                }
                            } else {
                                for (int i = 0; i < Response2.length(); i++) {
                                    JSONObject jsonObject = Response2.getJSONObject(i);
                                    inmember.add(jsonObject.getString("user_id"));
                                }
                            }
                            user_id.removeAll(user_id);
                            for (int i = 0; i < Response2.length(); i++) {
                                JSONObject jsonObject = Response2.getJSONObject(i);
                                if (!jsonObject.getString("user_name").equals("null")) {
                                    user_id.add(jsonObject.getString("user_id"));
                                }
                            }
                            dlog.i("이 작업에 배정된 직원 : " + user_id);
                            if(SUCCESS_POSITION == 2){
                                SendUserCheck("반려");
                            }else if(SUCCESS_POSITION == 0){
                                SendUserCheck("승인취소");
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

    /* -- 할일 추가 FCM 전송 영역 */
    private void SendUserCheck(String state) {
//        List<String> member = new ArrayList<>();
//        dlog.i("보내야 하는 직원 배열 :" + user_id);
//        member.addAll(Arrays.asList(user_id.split(",")));
//        dlog.i("보내야 하는 직원 수 :" + member.size());
//        dlog.i("보내야 하는 직원 List<String>  :" + member);

        for (int a = 0; a < user_id.size(); a++) {
            if(place_owner_id.equals(user_id.get(a))){
                getManagerToken(user_id.get(a), "0", place_id, place_name,state);
            }else{
                getManagerToken(user_id.get(a), "1", place_id, place_name,state);
            }
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
                            PushFcmSend(id, "", message, token, String.valueOf(SUCCESS_POSITION), place_id);
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
        dlog.i("binding.selectdate : " + binding.selectdate.getText().toString());
        dlog.i("-----GetApprovalList-----");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TaskSapprovalInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TaskSapprovalInterface api = retrofit.create(TaskSapprovalInterface.class);
        Call<String> call = api.getData(place_id,"1",binding.selectdate.getText().toString());
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
                        binding.totalApploveList1.setAdapter(mAdapter);
                        binding.totalApploveList1.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                        Log.i(TAG, "SetNoticeListview Thread run! ");

                        if (Response.length() == 0) {
                            binding.noDataTxt.setVisibility(View.VISIBLE);
                            Log.i(TAG, "GET SIZE : " + Response.length());
                            binding.checkCnt.setText("0건");
                            binding.allCheckbox.setClickable(false);
                            binding.allCheckbox.setEnabled(false);
                            binding.allCheckbox.setBackgroundResource(R.drawable.checkbox_off);
                        } else {
                            binding.noDataTxt.setVisibility(View.GONE);
                            for (int i = 0; i < Response.length(); i++) {
                                JSONObject jsonObject = Response.getJSONObject(i);
                                mAdapter.addItem(new TaskCheckData.TaskCheckData_list(
                                        jsonObject.getString("id"),
                                        jsonObject.getString("state"),
                                        jsonObject.getString("request_task_no"),
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
                                binding.checkCnt.setText(Tcnt + "건");
                                if (Tcnt == mList.size() && Fcnt == 0) {
                                    binding.allCheckbox.setBackgroundResource(R.drawable.checkbox_on);
                                } else {
                                    binding.allCheckbox.setBackgroundResource(R.drawable.checkbox_off);
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
        if (!USER_INFO_AUTH.equals("0")) {
            binding.cntArea.setVisibility(View.GONE);
            binding.searchDate.setVisibility(View.VISIBLE);
            binding.bottomArea.setVisibility(View.GONE);
        } else {
            binding.cntArea.setVisibility(View.VISIBLE);
             binding.searchDate.setVisibility(View.VISIBLE);
            binding.bottomArea.setVisibility(View.VISIBLE);
        }
        GetApprovalList();
    }

}
