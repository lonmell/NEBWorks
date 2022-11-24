package com.krafte.nebworks.ui.worksite;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.R;
import com.krafte.nebworks.bottomsheet.PlaceListBottomSheet;
import com.krafte.nebworks.dataInterface.TaskreuseInputInterface;
import com.krafte.nebworks.dataInterface.TaskreuseUpInterface;
import com.krafte.nebworks.databinding.ActivityPlaceaddworkBinding;
import com.krafte.nebworks.pop.WorkTimePicker;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/*
 * 2022-11-24 방창배 작성 - 자주하는 업무 추가 페이지 - UI는 업무추가 XML 그대로 사용
 * */
public class TaskReuesAddActivity extends AppCompatActivity {
    private static final String TAG = "TaskReuesAddActivity";
    Context mContext;
    private ActivityPlaceaddworkBinding binding;

    // shared 저장값
    PreferenceHelper shardpref;
    boolean channelId1 = false;
    boolean channelId2 = false;
    boolean EmployeeChannelId1 = false;

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
    String task_no = "";
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";

    DateCurrent dc = new DateCurrent();
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    int assignment_kind;
    String user_id = "";
    String WorkTitle = "";
    String WorkContents = "";
    String WorkDay = "";
    int make_kind = 0;

    //반복설정
    String[] WorkAddSecond;

    String TaskKind = "1";
    String start_time = "-99";
    String end_time = "-99";
    String EndTime02 = "-99";

    Drawable icon_on, icon_off;
    String searchDate = "";

    String Sun = "0", Mon = "0", Tue = "0", Wed = "0", Thu = "0", Fri = "0", Sat = "0";
    String toDay = "";
    String return_page = "";
    List<String> getYoil = new ArrayList<>();
    int a = 0;
    boolean NeedReportTF = false;

    @SuppressLint({"UseCompatLoadingForDrawables", "SimpleDateFormat", "LongLogTag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_placeaddwork);
        binding = ActivityPlaceaddworkBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        try {
            mContext = this;
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            shardpref.putInt("SELECT_POSITION_sub", 1);
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
            return_page = shardpref.getString("return_page", "0");
            make_kind = shardpref.getInt("make_kind", 0);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "-1");
            shardpref.putInt("SELECT_POSITION", 0);
            shardpref.putInt("SELECT_POSITION_sub", 1);

            //캘린더에서 넘어온 경우 - 선택한 날짜를 가져옴
            searchDate = shardpref.getString("searchDate", "");

            channelId1 = shardpref.getBoolean("channelId1", false);
            channelId2 = shardpref.getBoolean("channelId2", false);
            WorkAddSecond = new String[7];

            //수정할때 필요
            task_no = shardpref.getString("task_no", "0");
            icon_off = getApplicationContext().getResources().getDrawable(R.drawable.resize_service_off);
            icon_on = getApplicationContext().getResources().getDrawable(R.drawable.resize_service_on);
            //--처음에는 공통임무로 설정된채로 시작
            if (task_no.equals("0")) {
                user_id = "";
            }

            //--자주하는 업무 추가전 ui 세팅
            binding.loopworkTitleArea.setVisibility(View.GONE);
            binding.selectRepeatBtn.setVisibility(View.GONE);
            binding.eventStarttime.setHint("시작 시간을 입력해주세요");
            binding.eventEndttime.setHint("종료 시간을 입력해주세요");
            binding.selectMemberTitle.setVisibility(View.GONE);
            binding.selectMemberArea.setVisibility(View.GONE);
            binding.flagLine.setVisibility(View.GONE);
            binding.subtitle.setVisibility(View.INVISIBLE);
            setBtnEvent();
            toDay = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
            binding.storeName.setText(place_name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String change_place_id = "";
    String change_place_name = "";
    String change_place_owner_id = "";

    private void setBtnEvent() {
        binding.placeChangeArea.setOnClickListener(v -> {
            PlaceListBottomSheet plb = new PlaceListBottomSheet();
            plb.show(getSupportFragmentManager(), "PlaceListBottomSheet");
            plb.setOnClickListener01((v1, place_id, place_name, place_owner_id) -> {
                change_place_id = place_id;
                change_place_name = place_name;
                change_place_owner_id = place_owner_id;
                shardpref.putString("change_place_id", place_id);
                shardpref.putString("change_place_name", place_name);
                shardpref.putString("change_place_owner_id", place_owner_id);
                dlog.i("change_place_id : " + place_id);
                dlog.i("change_place_name : " + place_name);
                dlog.i("change_place_owner_id : " + place_owner_id);
                binding.storeName.setText(place_name);
            });
        });

        binding.eventStarttime.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, WorkTimePicker.class);
            intent.putExtra("timeSelect_flag", 2);
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        });

        binding.eventEndttime.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, WorkTimePicker.class);
            intent.putExtra("timeSelect_flag", 3);
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        });


        binding.needReport.setOnClickListener(v -> {
            if (!NeedReportTF) {
                NeedReportTF = true;
                binding.needReport.setBackgroundColor(Color.parseColor("#6395EC"));
                binding.reportTv.setTextColor(Color.parseColor("#ffffff"));
                binding.reportVisible.setVisibility(View.VISIBLE);
                TaskKind = "1";
                binding.select01Box.setBackgroundColor(Color.parseColor("#6395EC"));
                binding.select01.setTextColor(Color.parseColor("#ffffff"));
            } else {
                NeedReportTF = false;
                binding.needReport.setBackgroundColor(Color.parseColor("#F5F6F8"));
                binding.reportTv.setTextColor(Color.parseColor("#000000"));
                binding.reportVisible.setVisibility(View.GONE);
            }
        });

        binding.select01Box.setOnClickListener(v -> {
            TaskKind = "1";
            dlog.i("select01Box click [TaskKind : " + TaskKind + "]");
            binding.select01Box.setBackgroundColor(Color.parseColor("#6395EC"));
            binding.select01.setTextColor(Color.parseColor("#ffffff"));
            binding.select02Box.setBackgroundColor(Color.parseColor("#F5F6F8"));
            binding.select02.setTextColor(Color.parseColor("#000000"));
        });

        binding.select02Box.setOnClickListener(v -> {
            TaskKind = "0";
            dlog.i("select02Box click [TaskKind : " + TaskKind + "]");
            binding.select01Box.setBackgroundColor(Color.parseColor("#F5F6F8"));
            binding.select01.setTextColor(Color.parseColor("#000000"));
            binding.select02Box.setBackgroundColor(Color.parseColor("#6395EC"));
            binding.select02.setTextColor(Color.parseColor("#ffffff"));
        });

        binding.bottomBtnBox.setOnClickListener(v -> {
            if (SaveCheck()) {
                SaveAddWork();
            }
        });
    }

    String input_pop_time = "";
    String SET_TASK_TIME_VALUE = "";
    String Time01 = "-99";
    String Time02 = "-99";
    InputMethodManager imm;

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        try {
            imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            SET_TASK_TIME_VALUE = shardpref.getString("SET_TASK_TIME_VALUE", "-1");

            //반복요일 세팅
            int timeSelect_flag = shardpref.getInt("timeSelect_flag", 0);
            int hourOfDay = shardpref.getInt("Hour", 0);
            int minute = shardpref.getInt("Min", 0);

            dlog.i("------------------Data Check onResume------------------");
            dlog.i("kind : " + shardpref.getInt("timeSelect_flag", 0));
            dlog.i("Hour : " + shardpref.getInt("Hour", 0));
            dlog.i("Min : " + shardpref.getInt("Min", 0));
            dlog.i("timeSelect_flag : " + timeSelect_flag);
            dlog.i("------------------Data Check onResume------------------");

            if (timeSelect_flag == 2) {
                Time01 = String.valueOf(hourOfDay).length() == 1 ? "0" + String.valueOf(hourOfDay) : String.valueOf(hourOfDay);
                Time02 = String.valueOf(minute).length() == 1 ? "0" + String.valueOf(minute) : String.valueOf(minute);
                shardpref.remove("timeSelect_flag");
                shardpref.remove("Hour");
                shardpref.remove("Min");
                dlog.i("hourOfDay : " + hourOfDay);
                dlog.i("Time01 : " + Time01);
                dlog.i("Time02 : " + Time02);
                Time01 = Time01.equals("00") ? "12" : Time01;

                binding.eventStarttime.setText(Time01 + ":" + Time02);
                imm.hideSoftInputFromWindow(binding.inputWorktitle.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(binding.inputWorkcontents.getWindowToken(), 0);
            } else if (timeSelect_flag == 3) {
                Time01 = String.valueOf(hourOfDay).length() == 1 ? "0" + String.valueOf(hourOfDay) : String.valueOf(hourOfDay);
                Time02 = String.valueOf(minute).length() == 1 ? "0" + String.valueOf(minute) : String.valueOf(minute);
                shardpref.remove("timeSelect_flag");
                shardpref.remove("Hour");
                shardpref.remove("Min");
                dlog.i("hourOfDay : " + hourOfDay);
                dlog.i("Time01 : " + Time01);
                dlog.i("Time02 : " + Time02);
                Time01 = Time01.equals("00") ? "12" : Time01;
                binding.eventEndttime.setText(Time01 + ":" + Time02);
                imm.hideSoftInputFromWindow(binding.inputWorktitle.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(binding.inputWorkcontents.getWindowToken(), 0);
            }

            if(!task_no.equals("0") && a == 0){
                a ++;
                getTaskContents();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getTaskContents() {
        dlog.i("-----getTaskContents START-----");
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        task_no = shardpref.getString("task_no", "0");
        WorkTitle = shardpref.getString("title", "0");
        WorkContents = shardpref.getString("contents", "0");
        TaskKind = shardpref.getString("complete_kind", "0");            // 0:체크, 1:사진
        user_id = shardpref.getString("users", "0");
        WorkDay = shardpref.getString("task_date", "0");
        start_time = shardpref.getString("start_time", "0");
        end_time = shardpref.getString("end_time", "0");
        Sun = shardpref.getString("sun", "0");
        Mon = shardpref.getString("mon", "0");
        Tue = shardpref.getString("tue", "0");
        Wed = shardpref.getString("wed", "0");
        Thu = shardpref.getString("thu", "0");
        Fri = shardpref.getString("fri", "0");
        Sat = shardpref.getString("sat", "0");

        dlog.i("getTaskContents users : " + user_id);
        dlog.i("getTaskContents complete_kind : " + TaskKind);
        dlog.i("getTaskContents Mon : " + Mon);
        dlog.i("getTaskContents Tue : " + Tue);
        dlog.i("getTaskContents Wed : " + Wed);
        dlog.i("getTaskContents Thu : " + Thu);
        dlog.i("getTaskContents Fri : " + Fri);
        dlog.i("getTaskContents Sat : " + Sat);
        dlog.i("getTaskContents Sun : " + Sun);
        dlog.i("getTaskContents start_time : " + start_time);
        dlog.i("getTaskContents end_time : " + end_time);

        binding.workSave.setText("자주하는 업무 수정");
        binding.inputWorktitle.setText(WorkTitle);
        binding.inputWorkcontents.setText(WorkContents);


        shardpref.putString("yoillist", String.valueOf(getYoil));

        binding.eventStarttime.setText(start_time);
        binding.eventEndttime.setText(end_time);

        if(!TaskKind.isEmpty()){
            dlog.i("if(!TaskKind) : " + TaskKind);
            NeedReportTF = true;
            binding.needReport.setBackgroundColor(Color.parseColor("#6395EC"));
            binding.reportTv.setTextColor(Color.parseColor("#ffffff"));
            binding.reportVisible.setVisibility(View.VISIBLE);
            if(TaskKind.equals("0")){
                TaskKind = "0";
                binding.select01Box.setBackgroundColor(Color.parseColor("#6395EC"));
                binding.select01.setTextColor(Color.parseColor("#ffffff"));
                binding.select02Box.setBackgroundColor(Color.parseColor("#F5F6F8"));
                binding.select02.setTextColor(Color.parseColor("#000000"));
            }else if(TaskKind.equals("1")){
                TaskKind = "1";
                binding.select01Box.setBackgroundColor(Color.parseColor("#F5F6F8"));
                binding.select01.setTextColor(Color.parseColor("#000000"));
                binding.select02Box.setBackgroundColor(Color.parseColor("#6395EC"));
                binding.select02.setTextColor(Color.parseColor("#ffffff"));
            }
        }
        dlog.i("-----getTaskContents END-----");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public interface OnClickListener {
        void onClick();
    }

    private TaskAddWorkActivity.OnClickListener mListener = null;

    public void setOnClickListener(TaskAddWorkActivity.OnClickListener listener) {
        this.mListener = listener;
    }

    //업무 저장(추가)
    private void SaveAddWork() {
        String getYoil = shardpref.getString("yoillist", "").replace("  ", "").replace("[", "").replace("]", "");
        Sun = "0";
        Mon = "0";
        Tue = "0";
        Wed = "0";
        Thu = "0";
        Fri = "0";
        Sat = "0";//한번 초기화
        for (String str : getYoil.split(",")) {
            if (str.trim().equals("일")) {
                Sun = "1";
            } else if (str.trim().equals("월")) {
                Mon = "1";
            } else if (str.trim().equals("화")) {
                Tue = "1";
            } else if (str.trim().equals("수")) {
                Wed = "1";
            } else if (str.trim().equals("목")) {
                Thu = "1";
            } else if (str.trim().equals("금")) {
                Fri = "1";
            } else if (str.trim().equals("토")) {
                Sat = "1";
            }
        }
        if (searchDate.isEmpty()) {
            toDay = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
        } else {
            toDay = searchDate;
        }
        WorkTitle = binding.inputWorktitle.getText().toString();
        WorkContents = binding.inputWorkcontents.getText().toString();
        start_time = binding.eventStarttime.getText().toString();
        end_time = binding.eventEndttime.getText().toString();
        dlog.i("------------------SaveAddWork------------------");
        dlog.i("task_no : " + task_no);
        dlog.i("place_id : " + place_id);
        dlog.i("writer_id : " + USER_INFO_ID);
        dlog.i("kind : " + make_kind);
        dlog.i("title : " + WorkTitle);
        dlog.i("contents : " + WorkContents);
        dlog.i("complete_kind : " + String.valueOf(TaskKind));
        dlog.i("task_date : " + toDay);
        dlog.i("start_time : " + start_time);
        dlog.i("end_time : " + end_time);
        dlog.i("sun : " + Sun);
        dlog.i("mon : " + Mon);
        dlog.i("tue : " + Tue);
        dlog.i("wed : " + Wed);
        dlog.i("thu : " + Thu);
        dlog.i("fri : " + Fri);
        dlog.i("sat : " + Sat);
        dlog.i("users : " + user_id);

        if (task_no.equals("0")) {
            @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
                runOnUiThread(() -> {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(TaskreuseInputInterface.URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .build();
                    TaskreuseInputInterface api = retrofit.create(TaskreuseInputInterface.class);
                    //--반복 요일
                    dlog.i("------------------SaveAddWork12------------------");
                    Call<String> call = api.getData(place_id, USER_INFO_ID, WorkTitle, WorkContents, TaskKind
                            , start_time, end_time
                            , Sun, Mon, Tue, Wed, Thu, Fri, Sat);
                    call.enqueue(new Callback<String>() {
                        @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            //반복되는 요일을 일시 초기화 해준다
                            dlog.e("SaveAddWork function START");
                            dlog.e("response 1: " + response.isSuccessful());
                            dlog.e("response 2: " + response.body());
                            if (response.isSuccessful() && response.body() != null) {
                                String jsonResponse = response.body();
                                if (jsonResponse.replace("\"", "").equals("success") || response.body().replace("\"", "").equals("success")) {
                                    dlog.i("assignment_kind : " + assignment_kind);
                                    if (return_page.equals("TaskCalenderActivity")) {
                                        pm.CalenderBack(mContext);
                                    } else {
                                        shardpref.putInt("SELECT_POSITION", 1);
                                        pm.TaskReuse(mContext);
                                    }
                                    RemoveShared();

                                } else if (jsonResponse.replace("\"", "").equals("fail") || response.body().replace("\"", "").equals("fail")) {
                                    Toast.makeText(mContext, "동일한 업무가 이미 등록되어 있습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, "서버입력 오류! 데이터를 입력할 수 없습니다.", Toast.LENGTH_SHORT).show();
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
        } else {
            @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
                runOnUiThread(() -> {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(TaskreuseUpInterface.URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .build();
                    TaskreuseUpInterface api = retrofit.create(TaskreuseUpInterface.class);

                    //--반복 요일
                    dlog.i("------------------SaveAddWork22------------------");
                    Call<String> call = api.getData(task_no, USER_INFO_ID, WorkTitle, WorkContents, TaskKind
                            , start_time, end_time
                            , Sun, Mon, Tue, Wed, Thu, Fri, Sat);
                    call.enqueue(new Callback<String>() {
                        @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            //반복되는 요일을 일시 초기화 해준다
                            dlog.e("SaveAddWork function START");
                            dlog.e("response 1: " + response.isSuccessful());
                            dlog.e("response 2: " + response.body());
                            if (response.isSuccessful() && response.body() != null) {
                                String jsonResponse = response.body();
                                if (jsonResponse.replace("\"", "").equals("success") || response.body().replace("\"", "").equals("success")) {
                                    dlog.i("assignment_kind : " + assignment_kind);
                                    if (return_page.equals("TaskCalenderActivity")) {
                                        pm.CalenderBack(mContext);
                                    } else {
                                        shardpref.putInt("SELECT_POSITION", 1);
                                        pm.TaskReuse(mContext);
                                    }
                                    RemoveShared();

                                } else if (jsonResponse.replace("\"", "").equals("fail") || response.body().replace("\"", "").equals("fail")) {
                                    Toast.makeText(mContext, "동일한 업무가 이미 등록되어 있습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, "서버입력 오류! 데이터를 입력할 수 없습니다.", Toast.LENGTH_SHORT).show();
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

    private boolean SaveCheck() {
        WorkContents = binding.inputWorkcontents.getText().toString();
        WorkTitle = binding.inputWorktitle.getText().toString();
        start_time = binding.eventStarttime.getText().toString();
        end_time = binding.eventEndttime.getText().toString();

        if (WorkTitle.equals("")) {
            dlog.i("WorkTitle");
            Toast.makeText(this, "할일을 입력해주세요", Toast.LENGTH_SHORT).show();
            return false;
        } else if (WorkContents.isEmpty()) {
            dlog.i("WorkContents");
            Toast.makeText(this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TaskKind.isEmpty() && make_kind == 1) {
            Toast.makeText(this, "완료방법을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TaskKind.isEmpty() && make_kind == 4) {
            return true;
        } else if (start_time.equals("-99")) {
            dlog.i("StarTime");
            Toast.makeText(this, "시작시간을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (end_time.equals("-99")) {
            dlog.i("EndTime");
            Toast.makeText(this, "마감시간을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            dlog.i("제목2 : " + WorkTitle);
            dlog.i("내용 : " + WorkContents);
            dlog.i("완료방법2 : " + TaskKind);
            dlog.i("마감시간2 : " + end_time + ":" + EndTime02);
            dlog.i("반복 : " + getYoil.toString().replace("[", "").replace("]", ""));
            dlog.i("작업날짜 : " + WorkDay);
            dlog.i("선택된 직원 : " + user_id);
            return true;
        }
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
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        pm.TaskReuse(mContext);
    }
}
