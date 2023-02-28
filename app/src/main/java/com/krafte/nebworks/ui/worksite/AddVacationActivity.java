package com.krafte.nebworks.ui.worksite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.krafte.nebworks.R;
import com.krafte.nebworks.bottomsheet.PlaceListBottomSheet;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.TaskInputInterface;
import com.krafte.nebworks.dataInterface.TaskSaveInterface;
import com.krafte.nebworks.databinding.ActivityAddVacationBinding;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AddVacationActivity extends AppCompatActivity {

    private ActivityAddVacationBinding binding;
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();

    Context mContext;
    Dlog dlog = new Dlog();

    String place_id;
    String place_name;
    String USER_INFO_ID;
    String overdate;
    String user_id;

    String change_place_id = "";
    String change_place_name = "";
    String change_place_owner_id = "";

    String start_time = "-99";
    String end_time = "-99";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddVacationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        place_id            = shardpref.getString("place_id", PlaceCheckData.getInstance().getPlace_id());
        place_name          = shardpref.getString("place_name", PlaceCheckData.getInstance().getPlace_name());
        USER_INFO_ID        = shardpref.getString("USER_INFO_ID", UserCheckData.getInstance().getUser_id());
        overdate            = shardpref.getString("overdate", "");
        user_id             = shardpref.getString("users", "");


        binding.storeName.setText(place_name);
        setBtnEvent();
    }

    String toDay = "";
    String Year = "";
    String Month = "";
    String Day = "";
    String getDatePicker = "";
    String getYMPicker = "";
    boolean FIXYN = false;

    public void setBtnEvent() {

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

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
                binding.storeName.setText(place_name);
            });
        });

        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Year = String.valueOf(year);
                Month = String.valueOf(month + 1);
                Day = String.valueOf(dayOfMonth);
                Day = Day.length() == 1 ? "0" + Day : Day;
                Month = Month.length() == 1 ? "0" + Month : Month;
                binding.eventStarttime.setText(year + "-" + Month + "-" + Day);
                getYMPicker = binding.eventStarttime.getText().toString().substring(0, 7);
            }
        }, mYear, mMonth, mDay);

        binding.eventStarttime.setOnClickListener(v -> {
            if (binding.eventStarttime.getText().toString().isEmpty()) {
                String today = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
                binding.eventStarttime.setText(today);
            } else {
                shardpref.putInt("timeSelect_flag", 1);
                if (binding.eventStarttime.isClickable()) {
                    datePickerDialog.show();
                }
            }
        });

        DatePickerDialog datePickerDialog2 = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Year = String.valueOf(year);
                Month = String.valueOf(month + 1);
                Day = String.valueOf(dayOfMonth);
                Day = Day.length() == 1 ? "0" + Day : Day;
                Month = Month.length() == 1 ? "0" + Month : Month;
                binding.eventEndttime.setText(year + "-" + Month + "-" + Day);
                getYMPicker = binding.eventEndttime.getText().toString().substring(0, 7);
            }
        }, mYear, mMonth, mDay);

        binding.eventEndttime.setOnClickListener(v -> {
            if (binding.eventEndttime.getText().toString().isEmpty()) {
                String today = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
                binding.eventEndttime.setText(today);
            } else {
                shardpref.putInt("timeSelect_flag", 2);
                if (binding.eventEndttime.isClickable()) {
                    datePickerDialog2.show();
                }
            }
        });

        binding.backBtn.setOnClickListener(v -> {
            finish();
        });

        binding.workSave.setOnClickListener(v -> {
            if (saveCheck()) {
                addVacation();
            }
        });
    }

    private boolean saveCheck() {
        start_time = binding.eventStarttime.getText().toString();
        end_time = binding.eventEndttime.getText().toString();

        String[] splitStartDate = start_time.split("-");
        String[] splitEndDate = end_time.split("-");

        if (start_time.isEmpty()) {
            Toast_Nomal("시작날짜를 입력해주세요.");
            return false;
        } else if (end_time.isEmpty()) {
            Toast_Nomal("종료날짜를 입력해주세요.");
            return false;
        } else if (Integer.parseInt(splitStartDate[2]) > Integer.parseInt(splitEndDate[2])) {
            if (Integer.parseInt(splitStartDate[1]) > Integer.parseInt(splitEndDate[1])) {
                if (Integer.parseInt(splitStartDate[0]) > Integer.parseInt(splitEndDate[0])) {
                    Toast_Nomal("시작날짜가 종료날짜보다 큽니다. 다시 설정해주세요.");
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } else {
            return true;
        }
    }


    RetrofitConnect rc = new RetrofitConnect();
    public void addVacation() {
        dlog.i("------------------addVacation------------------");
        String WorkTitle = binding.inputWorktitle.getText().toString();
        user_id = String.valueOf(new ArrayList<String>(Collections.singleton(USER_INFO_ID)));

        String today = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;

        dlog.i("place_id: " + place_id);
        dlog.i("USER_INFO_ID: " + USER_INFO_ID);
        dlog.i("today: " + today);
        dlog.i("start_time: " + start_time);
        dlog.i("end_time: " + end_time);
        dlog.i("WorkTitle: " + WorkTitle);
        dlog.i("user_id: " + user_id);

        if (!change_place_id.isEmpty()) {
            place_id = change_place_id;
        }

        @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
            runOnUiThread(() -> {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(TaskInputInterface.URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();
                TaskInputInterface api = retrofit.create(TaskInputInterface.class);
                //--반복 요일
                dlog.i("------------------addVacation12------------------");
                Call<String> call = api.getData(place_id, USER_INFO_ID, "휴가 신청", WorkTitle, "3"
                        , today, start_time, end_time
                        , "0", "0", "0", "0", "0", "0", "0", ""
                        , USER_INFO_ID);
                call.enqueue(new Callback<String>() {
                    @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        //반복되는 요일을 일시 초기화 해준다
                        dlog.e("addVacation function START");
                        dlog.e("response 1: " + response.isSuccessful());
                        dlog.e("response 2: " + response.body());
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            if (jsonResponse.replace("\"", "").equals("success") || jsonResponse.replace("\"", "").equals("success")) {
                                dlog.i("SelectEmployeeid : " + user_id);
                                finish();
                            } else if (jsonResponse.replace("\"", "").equals("fail") || jsonResponse.replace("\"", "").equals("fail")) {
                                Toast.makeText(mContext, "동일한 휴가가 이미 등록되어 있습니다.", Toast.LENGTH_SHORT).show();
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
//            getFCMToken();
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
}