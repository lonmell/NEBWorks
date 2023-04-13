package com.krafte.nebworks.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.PushLogInputInterface;
import com.krafte.nebworks.dataInterface.UpdateCommuteInterface;
import com.krafte.nebworks.databinding.FragmentCommuteBottomSheetBinding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class CommuteBottomSheet extends BottomSheetDialogFragment {

    private FragmentCommuteBottomSheetBinding binding;
    private Context mContext;
    private PreferenceHelper shardpref;
    private boolean SELECT_POSITION = true; // True = 출근 시간, False = 퇴근 시간

    private String userName = "";
    private String workTime = "";
    private String goToWorkTime = "-1";
    private String goOffWorkTime = "-1";
    private String commute_date = "";
    private String commute_place_id = "";
    private String commute_user_id = "";

    Activity activity;

    Dlog dlog = new Dlog();

    String beforegoToTime = "";
    String beforegoOffTime = "";
    boolean SelectIn = false;
    boolean SelectOut = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCommuteBottomSheetBinding.inflate(getLayoutInflater());
        mContext = getLayoutInflater().getContext();
        shardpref = new PreferenceHelper(mContext);
        dlog.DlogContext(mContext);

        userName = shardpref.getString("commute_name", "");
        workTime = shardpref.getString("commute_work_time", "");
        goToWorkTime = shardpref.getString("commute_in_time", "");
        goOffWorkTime = shardpref.getString("commute_out_time", "");
        commute_date = shardpref.getString("commute_date", "").replace("년", "-").replace("월", "-").replace("일", "").replace(" ", "");
        dlog.i("commute_date: " + commute_date);
        commute_place_id = shardpref.getString("commute_place_id", "");
        commute_user_id = shardpref.getString("commute_user_id", "");

        setData();
        setBtnEvent();

        return binding.getRoot();
    }

    boolean successtf = false;

    private void setData() {
        dlog.i("-----CommuteBottomSheet setData-----");
        dlog.i("userName : " + userName);
        dlog.i("workTime : " + workTime);
        dlog.i("goToWorkTime : " + goToWorkTime);
        dlog.i("goOffWorkTime : " + goOffWorkTime);

        binding.commuteUser.setText(userName);
        binding.commuteWorkingHours.setText("근무시간 " + (workTime.equals("null") ? "미정" : workTime));

        if (!(goToWorkTime.replace("null","").length() == 0)) {
            String[] splitGoToWorkTime = goToWorkTime.split(":");
            dlog.i("splitGoToWorkTime[0]: " + Integer.parseInt(splitGoToWorkTime[0]));
            dlog.i("splitGoToWorkTime[1] : " + Integer.parseInt(splitGoToWorkTime[1]));
            dlog.i("splitGoToWorkTime[2] : " + Integer.parseInt(splitGoToWorkTime[2]));

            binding.gotoworkTextTime.setText((Integer.parseInt(splitGoToWorkTime[0]) < 12 ? "오전 " : "오후 ") +
                    String.format("%02d:%02d", Integer.parseInt(splitGoToWorkTime[0]), Integer.parseInt(splitGoToWorkTime[1])));
            beforegoToTime = (Integer.parseInt(splitGoToWorkTime[0]) < 12 ? "오전 " : "오후 ") +
                    String.format("%02d:%02d", Integer.parseInt(splitGoToWorkTime[0]), Integer.parseInt(splitGoToWorkTime[1]));
        } else {
            binding.gotoworkTextTime.setText("");
            beforegoToTime = "";
        }
        if (!(goOffWorkTime.replace("null","").length() == 0)) {
            String[] splitGoOffWorkTime = goOffWorkTime.split(":");
            dlog.i("splitGoOffWorkTime[0]: " + Integer.parseInt(splitGoOffWorkTime[0]));
            dlog.i("splitGoOffWorkTime[1] : " + Integer.parseInt(splitGoOffWorkTime[1]));
            dlog.i("splitGoOffWorkTime[2] : " + Integer.parseInt(splitGoOffWorkTime[2]));
            binding.gooffworkTextTime.setText((Integer.parseInt(splitGoOffWorkTime[0]) < 12 ? "오전 " : "오후 ") +
                    String.format("%02d:%02d", Integer.parseInt(splitGoOffWorkTime[0]), Integer.parseInt(splitGoOffWorkTime[1])));
            beforegoOffTime = (Integer.parseInt(splitGoOffWorkTime[0]) < 12 ? "오전 " : "오후 ") +
                    String.format("%02d:%02d", Integer.parseInt(splitGoOffWorkTime[0]), Integer.parseInt(splitGoOffWorkTime[1]));
        } else {
            binding.gooffworkTextTime.setText("");
            beforegoOffTime = "";
        }
        dlog.i("-----CommuteBottomSheet setData-----");
    }

    // 출근 시간과 퇴근 시간 을 클릭시 선택 된 시간의 영역 색이 변하고 그 시간을 변경가능하게 함.
    private void setBtnEvent() {
        //출근 시간
        binding.gotowork.setOnClickListener(v -> {

            SELECT_POSITION = true;
            SelectIn = true;
            binding.commuteTimepicker.setVisibility(View.VISIBLE);

            binding.gotowork.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
            binding.gotoworkText.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            binding.gotoworkTextTime.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            binding.gotoworkTimeDel.setBackgroundResource(R.drawable.ic_baseline_cancel_24);

            binding.gooffwork.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            binding.gooffworkText.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            binding.gooffworkTextTime.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            binding.gooffworkTimeDel.setBackgroundResource(R.drawable.ic_baseline_cancel_24_black);

            if (!goToWorkTime.equals("-1")) {
                int hour = binding.commuteTimepicker.getHour();
                int minute = binding.commuteTimepicker.getMinute();
                goToWorkTime = hour + ":" + minute;
                binding.gotoworkTextTime.setText((hour < 12 ? "오전" : "오후") + " " + hour + ":" + minute);
                if (!(goToWorkTime.replace("null","").length() == 0)) {
                    String[] splitGoToWorkTime = goToWorkTime.split(":");
                    binding.commuteTimepicker.setHour(Integer.parseInt(splitGoToWorkTime[0]));
                    binding.commuteTimepicker.setMinute(Integer.parseInt(splitGoToWorkTime[1]));
                    binding.gotoworkTimeDel.setVisibility(View.VISIBLE);
                } else {
                    binding.gotoworkTimeDel.setVisibility(View.INVISIBLE);
                }
            }
        });

        // 퇴근 시간
        binding.gooffwork.setOnClickListener(v -> {

            SELECT_POSITION = false;
            SelectOut = true;
            binding.commuteTimepicker.setVisibility(View.VISIBLE);

            binding.gooffwork.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
            binding.gooffworkText.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            binding.gooffworkTextTime.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            binding.gooffworkTimeDel.setBackgroundResource(R.drawable.ic_baseline_cancel_24);

            binding.gotowork.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            binding.gotoworkText.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            binding.gotoworkTextTime.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            binding.gotoworkTimeDel.setBackgroundResource(R.drawable.ic_baseline_cancel_24_black);

            if (!goOffWorkTime.equals("-1")) {
                // 변경되기 전의 시간 값 가져오기
                int hour = binding.commuteTimepicker.getHour();
                int minute = binding.commuteTimepicker.getMinute();
                goOffWorkTime = hour + ":" + minute;
                binding.gooffworkTextTime.setText((hour < 12 ? "오전" : "오후") + " " + hour + ":" + minute);
                if (!(goOffWorkTime.replace("null","").length() == 0)) {
                    String[] splitGoOffWorkTime = goOffWorkTime.split(":");
                    binding.commuteTimepicker.setHour(Integer.parseInt(splitGoOffWorkTime[0]));
                    binding.commuteTimepicker.setMinute(Integer.parseInt(splitGoOffWorkTime[1]));
                    binding.gooffworkTimeDel.setVisibility(View.VISIBLE);
                } else {
                    binding.gooffworkTimeDel.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Time Picker 시간 변경
        binding.commuteTimepicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                String HOUR = String.valueOf(hourOfDay);
                String MIN = String.valueOf(minute);
                binding.commuteTimepicker.clearFocus();
                if (SELECT_POSITION) {
                    goToWorkTime = HOUR + ":" + MIN;
                    binding.gotoworkTextTime.setText((hourOfDay < 12 ? "오전" : "오후") + " " + (HOUR.length() == 1 ? "0" + HOUR : HOUR) + ":" + (MIN.length() == 1 ? "0" + MIN : MIN));
                    if (binding.gotoworkTextTime.getText().length() != 0) {
                        binding.gotoworkTimeDel.setVisibility(View.VISIBLE);
                    } else {
                        binding.gotoworkTimeDel.setVisibility(View.INVISIBLE);
                    }
                } else {
                    goOffWorkTime = HOUR + ":" + MIN;
                    binding.gooffworkTextTime.setText((hourOfDay < 12 ? "오전" : "오후") + " " + (HOUR.length() == 1 ? "0" + HOUR : HOUR) + ":" + (MIN.length() == 1 ? "0" + MIN : MIN));
                    if (binding.gooffworkTextTime.getText().length() != 0) {
                        binding.gooffworkTimeDel.setVisibility(View.VISIBLE);
                    } else {
                        binding.gooffworkTimeDel.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        binding.gotoworkTimeDel.setOnClickListener(v -> {
            goToWorkTime = "-1";
            binding.gotoworkTextTime.setText("");
            binding.commuteTimepicker.setVisibility(View.GONE);

        });

        binding.gooffworkTimeDel.setOnClickListener(v -> {
            goOffWorkTime = "-1";
            binding.gooffworkTextTime.setText("");
            binding.commuteTimepicker.setVisibility(View.GONE);
        });

        // 저장 버튼
        binding.commuteOk.setOnClickListener(v -> {
            if (!beforegoToTime.equals(goToWorkTime)) {
                UpdateCommute("0", goToWorkTime);
            }
            if (mListener != null) {
                mListener.onItemClick(v);
            }

            dismiss();
        });
    }

    RetrofitConnect rc = new RetrofitConnect();
    int SendCnt = 0;
    public void UpdateCommute(String kind, String time) {
        dlog.i("------UpdateCommute------");
        dlog.i("commute_place_id : " + commute_place_id);
        dlog.i("commute_user_id : " + commute_user_id);
        dlog.i("kind : " + kind);
        dlog.i("commute_date : " + commute_date);
        dlog.i("time : " + time);
        dlog.i("------UpdateCommute------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UpdateCommuteInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        UpdateCommuteInterface api = retrofit.create(UpdateCommuteInterface.class);
        Call<String> call = api.getData(commute_place_id, commute_user_id, kind, commute_date, time.equals("-1") ? "" : time);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activity.runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("UpdateCommute jsonResponse length : " + jsonResponse.length());
                            dlog.i("UpdateCommute jsonResponse : " + jsonResponse);
                            try {
                                if(SendCnt == 0){
                                    SendCnt = SendCnt + 1;
                                    String place_name = PlaceCheckData.getInstance().getPlace_name();
                                    String message = "[" + place_name + "]" + commute_date + "날짜의 출퇴근 데이터가 변경되었습니다";
                                    if(!commute_user_id.isEmpty()){
                                        getUserToken(commute_user_id, "1", message);
                                        AddPush("직원출퇴근데이터변경", message, commute_user_id);
                                        Toast.makeText(mContext, commute_date + "날짜의" + userName + " 직원의 출퇴근 데이터가 변경되었습니다", Toast.LENGTH_SHORT).show();
                                    }
                                    dlog.i("UpdateCommute commute_user_id : " + commute_user_id);
                                }
                                if(kind.equals("0") && !(goOffWorkTime.replace("null","").length() == 0)){
                                    if (!beforegoOffTime.equals(goOffWorkTime)) {
                                        UpdateCommute("1", goOffWorkTime);
                                    }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        shardpref.remove("commute_place_id");
        shardpref.remove("commute_user_id");
        shardpref.remove("commute_name");
        shardpref.remove("commute_work_time");
        shardpref.remove("commute_in_time");
        shardpref.remove("commute_out_time");
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v);
    }

    String place_owner_id = "";

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
                        place_owner_id = PlaceCheckData.getInstance().getPlace_owner_id();
                        boolean channelId1 = Response.getJSONObject(0).getString("channel2").equals("1");
                        if (!token.isEmpty() && channelId1) {
                            PushFcmSend(id, "", message, token, "2", commute_place_id);
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
        Call<String> call = api.getData(commute_place_id, "", title, content, place_owner_id, user_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String jsonResponse = rc.getBase64decode(response.body());
                dlog.i("jsonResponse length : " + jsonResponse.length());
                dlog.i("jsonResponse : " + jsonResponse);
                if (response.isSuccessful() && response.body() != null) {

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

    //점주 > 근로자
    private void PushFcmSend(String topic, String title, String message, String token, String tag, String place_id) {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            click_action = "status1";
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
//            activity.runOnUiThread(() -> {
//            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}