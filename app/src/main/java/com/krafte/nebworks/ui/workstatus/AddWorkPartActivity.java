package com.krafte.nebworks.ui.workstatus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.R;
import com.krafte.nebworks.bottomsheet.PlaceListBottomSheet;
import com.krafte.nebworks.bottomsheet.SelectYoilActivity;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.ReturnPageData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.WorkPartGetInterface;
import com.krafte.nebworks.dataInterface.WorkPartSaveInterface;
import com.krafte.nebworks.databinding.ActivityAddworkpartBinding;
import com.krafte.nebworks.pop.SelectMemberPop;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AddWorkPartActivity extends AppCompatActivity {
    private ActivityAddworkpartBinding binding;
    Context mContext;

    //Other
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    PreferenceHelper shardpref;
    Dlog dlog = new Dlog();
    DateCurrent dc = new DateCurrent();
    ArrayList<String> mList;
    Handler mHandler;
    RetrofitConnect rc = new RetrofitConnect();


    String Time01 = "-99";
    String Time02 = "-99";
    String i_cnt = "";

    //Shared
    String USER_INFO_EMAIL = "";
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";
    String USER_INFO_NAME = "";
    String place_owner_id = "";

    String place_id = "";
    String place_name = "";
    String setYoil = "";
    String sieob_get = "";
    String jong_eob_get = "";
    String total_work_time_get = "";
    String break_time_get01 = "";
    String break_time_get02 = "";
    String diff_break_time_get = "";
    //--매장 정보 수정할때

    boolean workPartState = false;
    String ContractWorkKind = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityAddworkpartBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        try {
            mContext = this;
            dlog.DlogContext(mContext);
            shardpref   = new PreferenceHelper(mContext);

            //Singleton Area
            USER_INFO_ID    = UserCheckData.getInstance().getUser_id();
            USER_INFO_NAME  = UserCheckData.getInstance().getUser_name();
            USER_INFO_EMAIL = UserCheckData.getInstance().getUser_account();
            USER_INFO_AUTH  = shardpref.getString("USER_INFO_AUTH","");
            place_owner_id  = PlaceCheckData.getInstance().getPlace_owner_id();
            place_id        = PlaceCheckData.getInstance().getPlace_id();
            place_name      = PlaceCheckData.getInstance().getPlace_name();

            workPartState   = shardpref.getBoolean("workpart_state", false);

            dlog.i("workPartState: " + workPartState);

            //shardpref Area
            i_cnt           = shardpref.getString("i_cnt", "0");
            change_place_id = place_id;
            dlog.i("------AddWorkPartActivity------");
            dlog.i("i_cnt: " + i_cnt);
            dlog.i("change_place_id: " + change_place_id);
            dlog.i("------AddWorkPartActivity------");
            binding.storeName.setText(place_name);


            setBtnEvent();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    String item_user_id = "";
    String item_user_name = "";

    @Override
    public void onResume() {
        super.onResume();
        dlog.i("-----onResume-----");
        dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
        dlog.i("place_owner_id : " + place_owner_id);
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);

        if(place_owner_id.equals(USER_INFO_ID) && USER_INFO_AUTH.equals("1")){
            //근로자가 본인것 추가할때
            binding.memName.setVisibility(View.VISIBLE);
            binding.memCnt.setVisibility(View.GONE);
            binding.memSelect.setVisibility(View.GONE);
            item_user_id = USER_INFO_ID;
            item_user_name = USER_INFO_NAME;
            dlog.i("item_user_id : " + item_user_id);
            dlog.i("item_user_name : " + item_user_name);
            binding.memName.setText(item_user_name);
            binding.selectMem.setClickable(false);
            String rpd = ReturnPageData.getInstance().getPage();
            ContractWorkKind = "1";
            if(rpd.equals("EmployeeProcess")){
                ContractWorkKind = "1";
            }
        }else if(place_owner_id.equals(USER_INFO_ID) && USER_INFO_AUTH.equals("0")){
            //점주가 추가할때
            //부여할 사용자 가져오기
            binding.memName.setVisibility(View.GONE);
            binding.memCnt.setVisibility(View.VISIBLE);
            binding.memSelect.setVisibility(View.VISIBLE);
            item_user_id = shardpref.getString("item_user_id", "");
            item_user_name = shardpref.getString("item_user_name", "");
            dlog.i("item_user_id : " + item_user_id);
            dlog.i("item_user_name : " + item_user_name);
            if (!item_user_id.isEmpty() && !item_user_name.isEmpty()) {
                binding.memName.setVisibility(View.VISIBLE);
                binding.memCnt.setVisibility(View.GONE);
                binding.memSelect.setVisibility(View.GONE);
                binding.memName.setText(item_user_name);
                binding.selectMem.setClickable(false);
            } else {
//                binding.memName.setVisibility(View.GONE);
//                binding.memCnt.setVisibility(View.VISIBLE);
//                binding.memSelect.setVisibility(View.VISIBLE);
                if(i_cnt.equals("0")){
                    binding.memCnt.setVisibility(View.GONE);
                    binding.memSelect.setVisibility(View.GONE);
                    binding.memName.setVisibility(View.VISIBLE);
                    binding.memName.setText("근무자를 등록해주세요 >");
                    binding.memName.setTextColor(Color.parseColor("#1445D0"));
                    binding.memName.setOnClickListener(v -> {
                        pm.MemberManagement(mContext);
                    });
                }else{
                    binding.memName.setVisibility(View.GONE);
                    binding.memCnt.setVisibility(View.VISIBLE);
                    binding.memSelect.setVisibility(View.VISIBLE);
                    binding.memCnt.setText("총 " + i_cnt + "명 근무 중");
                }
            }
            binding.selectMem.setClickable(true);
        }else if(!place_owner_id.equals(USER_INFO_ID) && USER_INFO_AUTH.equals("1")){
            //근로자가 본인것 추가할때
            binding.memName.setVisibility(View.VISIBLE);
            binding.memCnt.setVisibility(View.GONE);
            binding.memSelect.setVisibility(View.GONE);
            item_user_id = USER_INFO_ID;
            item_user_name = USER_INFO_NAME;
            dlog.i("item_user_id : " + item_user_id);
            dlog.i("item_user_name : " + item_user_name);
            binding.memName.setText(item_user_name);
            binding.selectMem.setClickable(false);
            workPartState = true;
            String rpd = ReturnPageData.getInstance().getPage();
            if(rpd.equals("EmployeeProcess")){
                ContractWorkKind = "2";
            }
        }

        if (workPartState) {
            getWorkPart();
        }

        //시간 지정하기
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        dlog.i("-----onResume-----");
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy(){
        ReturnPageData.getInstance().setPage("Main2");
        super.onDestroy();
    }

    boolean SELECTTIME01 = false; // 근무시간 false - 시작시간 / true - 종료시간
    boolean SELECTTIME02 = false; // 휴식시간 false - 시작시간 / true - 종료시간
    String GetTime = "";
    List<String> resultYoil = new ArrayList<>();
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
                binding.storeName.setText(place_name);
            });
        });
        binding.backBtn.setOnClickListener(v -> {
            shardpref.remove("item_user_id");
            shardpref.remove("item_user_name");
            super.onBackPressed();
        });
        binding.yoil.setOnClickListener(v -> {
            SelectYoilActivity sya = new SelectYoilActivity();
            shardpref.putString("select_yoil", setYoil);
            sya.show(getSupportFragmentManager(), "SelectYoilActivity");
            sya.setOnItemClickListener(new SelectYoilActivity.OnItemClickListener() {
                @Override
                public void onItemClick(View v, String category) {
                    setYoil = category.replace("요일", "").replace(" ", "");
                    dlog.i("setYoil : " + setYoil);
                    List<Integer> listYoil = new ArrayList<>();
                    resultYoil.clear();
                    for(String str : setYoil.replace("[", "").replace("]", "").split(",")){
                        dlog.i("str : " + str);
                        switch (str) {
                            case "월":
                                listYoil.add(1);
                                break;
                            case "화":
                                listYoil.add(2);
                                break;
                            case "수":
                                listYoil.add(3);
                                break;
                            case "목":
                                listYoil.add(4);
                                break;
                            case "금":
                                listYoil.add(5);
                                break;
                            case "토":
                                listYoil.add(6);
                                break;
                            case "일":
                                listYoil.add(7);
                                break;
                        }
                    }

                    Collections.sort(listYoil);
                    dlog.i("listYoil : " + listYoil);
                    for(String str : String.valueOf(listYoil).replace("[", "").replace("]", "").replace(" ","").split(",")){
                        switch (str) {
                            case "1":
                                resultYoil.add("월");
                                break;
                            case "2":
                                resultYoil.add("화");
                                break;
                            case "3":
                                resultYoil.add("수");
                                break;
                            case "4":
                                resultYoil.add("목");
                                break;
                            case "5":
                                resultYoil.add("금");
                                break;
                            case "6":
                                resultYoil.add("토");
                                break;
                            case "7":
                                resultYoil.add("일");
                                break;
                        }
                    }
                    dlog.i("resultYoil : " + String.valueOf(resultYoil).replace("[", "").replace("]", "").replace(" ",""));
                    setYoil = String.valueOf(resultYoil).replace("[", "").replace("]", "").replace(" ","");
                    binding.yoilTv.setText(setYoil);
                }
            });
        });

        binding.selectMem.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, SelectMemberPop.class);
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        });

        binding.selectTime01.setText("시간선택");
        binding.selectTime02.setText("시간선택");
        binding.selectTime03.setText("시간선택");
        binding.selectTime04.setText("시간선택");
        binding.inputbox01box.setOnClickListener(v -> {
            SELECTTIME01 = false;
            binding.inputbox01box.setCardBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.inputbox02box.setCardBackgroundColor(Color.parseColor("#ffffff"));
            binding.timeSetpicker1.setVisibility(View.VISIBLE);
            binding.timeSetpicker2.setVisibility(View.GONE);
        });
        binding.inputbox02box.setOnClickListener(v -> {
            SELECTTIME01 = true;
            binding.inputbox01box.setCardBackgroundColor(Color.parseColor("#ffffff"));
            binding.inputbox02box.setCardBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.timeSetpicker1.setVisibility(View.VISIBLE);
            binding.timeSetpicker2.setVisibility(View.GONE);
        });
        binding.inputbox03box.setOnClickListener(v -> {
            SELECTTIME02 = false;
            binding.inputbox03box.setCardBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.inputbox04box.setCardBackgroundColor(Color.parseColor("#ffffff"));
            binding.timeSetpicker1.setVisibility(View.GONE);
            binding.timeSetpicker2.setVisibility(View.VISIBLE);
        });
        binding.inputbox04box.setOnClickListener(v -> {
            SELECTTIME02 = true;
            binding.inputbox03box.setCardBackgroundColor(Color.parseColor("#ffffff"));
            binding.inputbox04box.setCardBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.timeSetpicker1.setVisibility(View.GONE);
            binding.timeSetpicker2.setVisibility(View.VISIBLE);
        });


        binding.timeSetpicker1.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    String HOUR = String.valueOf(hourOfDay);
                    String MIN = String.valueOf(minute);
                    binding.timeSetpicker1.clearFocus();
                    if(!SELECTTIME01){
                        sieob_get = (HOUR.length() == 1?"0"+HOUR:HOUR) + ":" + (MIN.length() == 1?"0"+MIN:MIN);
                        binding.selectTime01.setText((hourOfDay < 12?"오전":"오후") + " " + (HOUR.length() == 1?"0"+HOUR:HOUR) + ":" + (MIN.length() == 1?"0"+MIN:MIN));
                    }else{
                        jong_eob_get = (HOUR.length() == 1?"0"+HOUR:HOUR) + ":" + (MIN.length() == 1?"0"+MIN:MIN);
                        binding.selectTime02.setText((hourOfDay < 12?"오전":"오후") + " " + (HOUR.length() == 1?"0"+HOUR:HOUR) + ":" + (MIN.length() == 1?"0"+MIN:MIN));
                    }
            }
        });

        binding.timeSetpicker2.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                String HOUR = String.valueOf(hourOfDay);
                String MIN = String.valueOf(minute);
                binding.timeSetpicker2.clearFocus();
                if(!SELECTTIME02){
                    break_time_get01 = (HOUR.length() == 1?"0"+HOUR:HOUR) + ":" + (MIN.length() == 1?"0"+MIN:MIN);
                    binding.selectTime03.setText((hourOfDay < 12?"오전":"오후") + " " + (HOUR.length() == 1?"0"+HOUR:HOUR) + ":" + (MIN.length() == 1?"0"+MIN:MIN));
                }else{
                    break_time_get02 = (HOUR.length() == 1?"0"+HOUR:HOUR) + ":" + (MIN.length() == 1?"0"+MIN:MIN);
                    binding.selectTime04.setText((hourOfDay < 12?"오전":"오후") + " " + (HOUR.length() == 1?"0"+HOUR:HOUR) + ":" + (MIN.length() == 1?"0"+MIN:MIN));
                }
            }
        });
        binding.saveWorkpart.setOnClickListener(v -> {
            if (SaveCheck()) {
                SaveWorkPartTime(item_user_id);
            }
        });
    }

    private boolean SaveCheck() {
        if(sieob_get.equals("시간선택") || jong_eob_get.equals("시간선택")){
            Toast_Nomal("입력되지 않은 시간이 있습니다.");
            return false;
        }else{
            SimpleDateFormat f = new SimpleDateFormat("HH:mm", Locale.KOREA);
            try {
                if (!sieob_get.isEmpty() && !jong_eob_get.isEmpty()) {
                    dlog.i("근로시간 계산");
                    //근로시간 계산
                    Date d1 = f.parse(jong_eob_get);
                    Date d2 = f.parse(sieob_get);

                    long diff = d1.getTime() - d2.getTime();
                    dlog.i("diff : " + diff);
                    long min = diff / 60000;

                    long getH = min / 60;
                    long getM = min % 60;
                    if (getM != 0) {
                        total_work_time_get = getH + "h " + getM + "m";
                    } else {
                        total_work_time_get = getH + "h";
                    }

                } else if (!break_time_get02.isEmpty() && !break_time_get01.isEmpty()) {
                    dlog.i("휴게시간 계산");
                    //휴게시간 계산
                    Date d3 = f.parse(break_time_get02);
                    Date d4 = f.parse(break_time_get01);

                    long diff2 = d3.getTime() - d4.getTime();
                    dlog.i("diff : " + diff2);
                    long min2 = diff2 / 60000;

                    long getH2 = min2 / 60;
                    long getM2 = min2 % 60;
                    if (getM2 != 0) {
                        diff_break_time_get = getH2 + "h " + getM2 + "m";
                    } else if (getH2 == 0) {
                        diff_break_time_get = getM2 + "m";
                    } else {
                        diff_break_time_get = getH2 + "h";
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            dlog.i("-----SaveCheck-----");
            dlog.i("yoil : " + setYoil);
            dlog.i("item_user_id : " + item_user_id);
            dlog.i("WorkStartTime : " + sieob_get);
            dlog.i("WorkEndTime : " + jong_eob_get);
            dlog.i("TotalWorkTime : " + total_work_time_get);
            dlog.i("RestStart : " + break_time_get01);
            dlog.i("RestEnd : " + break_time_get02);
            dlog.i("RestTotal : " + diff_break_time_get);
            dlog.i("-----SaveCheck-----");
            if (setYoil.isEmpty()) {
                Toast_Nomal("요일을 선택해주세요.");
                return false;
            } else if(item_user_id.isEmpty()){
                Toast_Nomal("근무자를 배정해주세요.");
                return false;
            } else if (sieob_get.isEmpty()) {
                Toast_Nomal("근무 시작시간을 선택해주세요.");
                return false;
            } else if (jong_eob_get.isEmpty()) {
                Toast_Nomal("근무 종료시간을 선택해주세요.");
                return false;
            } else if (break_time_get01.isEmpty()) {
                Toast_Nomal("휴게시작시간을 선택해주세요.");
                return false;
            } else if (break_time_get02.isEmpty()) {
                Toast_Nomal("휴게종료시간을 선택해주세요.");
                return false;
            } else {
                return true;
            }
        }
    }

    private void getWorkPart() {
        dlog.i("getWorkPart");
        dlog.i("getWorkPart place_id: " + place_id);
        dlog.i("getWorkPart item_user_id: " + item_user_id);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WorkPartGetInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        WorkPartGetInterface api = retrofit.create(WorkPartGetInterface.class);
        Call<String> call = api.getData(place_id, item_user_id, "월");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                dlog.i("GetWorkPartTime Callback : " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            try {
                                if (!jsonResponse.equals("[]")) {
                                    List<JSONObject> objectList = new ArrayList<>();
                                    JSONArray Response = new JSONArray(jsonResponse);
                                    for (int i = 0; i < Response.length(); i++) {
                                        JSONObject jsonObject = Response.getJSONObject(i);
                                        objectList.add(jsonObject);
                                    }
                                    String rpd = ReturnPageData.getInstance().getPage();
                                    if(rpd.equals("EmployeeProcess")){
                                        StringBuilder sb = new StringBuilder();
                                        setYoil = objectList.get(0).getString("yoil");
                                        for (int i = 1; i < objectList.size(); i++) {
                                            sb.append(",");
                                            sb.append(objectList.get(i).getString("yoil"));
                                        }
                                        setYoil += sb.toString();
                                        dlog.i("setYoilobject: " + setYoil);
                                        shardpref.putString("setYoilobject",setYoil);

                                    }else{
                                        setWorkTimeContent(objectList);
                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dlog.e("에러 = " + t.getMessage());
            }
        });
    }

    private void setWorkTimeContent(List<JSONObject> objects) throws JSONException {
        // 요일 세팅
        binding.title.setText("근무시간 수정");
        binding.addWorkpart.setText("수정하기");
        StringBuilder sb = new StringBuilder();
        setYoil = objects.get(0).getString("yoil");
        dlog.i("setWorkTimeContent object: " + objects);
        for (int i = 1; i < objects.size(); i++) {
            sb.append(",");
            sb.append(objects.get(i).getString("yoil"));
        }
        setYoil += sb.toString();
        binding.yoilTv.setText(setYoil);

        sieob_get = objects.get(0).getString("sieob");
        jong_eob_get = objects.get(0).getString("jongeob");
        break_time_get01 = objects.get(0).getString("breaktime01");
        break_time_get02 = objects.get(0).getString("breaktime02");

        String[] workStart = sieob_get.split(":");
        String[] workEnd = jong_eob_get.split(":");
        String[] breakStart = break_time_get01.split(":");
        String[] breakEnd = break_time_get02.split(":");

        setContentTime(workStart[0], workStart[1], binding.selectTime01);
        setContentTime(workEnd[0], workEnd[1], binding.selectTime02);
        setContentTime(breakStart[0], breakStart[1], binding.selectTime03);
        setContentTime(breakEnd[0], breakEnd[1], binding.selectTime04);

        total_work_time_get = objects.get(0).getString("workhour");
        diff_break_time_get = objects.get(0).getString("breaktime");
    }

    private void setContentTime(String hour, String min, TextView view) {
        if (Integer.parseInt(hour) < 12) {
            view.setText("오전 " + hour + ":" + min);
        } else {
            view.setText("오후 " + hour + ":" + min);
        }
    }

    public void SaveWorkPartTime(String user_id) {
        dlog.i("setYoil : " + setYoil);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WorkPartSaveInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        WorkPartSaveInterface api = retrofit.create(WorkPartSaveInterface.class);
        Call<String> call = api.getData(change_place_id, user_id, setYoil.replace("[", "").replace("]", "")
                , total_work_time_get, sieob_get, jong_eob_get, break_time_get01
                , break_time_get02, diff_break_time_get,ContractWorkKind);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.i("SaveWorkPartTime Callback : " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            try {
                                if (!jsonResponse.equals("[]") && jsonResponse.replace("\"", "").equals("success")) {
                                    if(ReturnPageData.getInstance().getPage().equals("EmployeeProcess")){
                                        Toast_Nomal("추가 근무가 생성되었습니다");
                                    }else{
                                        Toast_Nomal("근무시간이 업데이트 되었습니다.");
                                    }

                                    shardpref.remove("item_user_id");
                                    shardpref.remove("item_user_name");
                                    shardpref.putInt("SELECT_POSITION", 2);
                                    if(place_owner_id.equals(USER_INFO_ID) && USER_INFO_AUTH.equals("1")){
                                        pm.Main2(mContext);
                                    }else if(place_owner_id.equals(USER_INFO_ID) && USER_INFO_AUTH.equals("0")){
                                        if(ReturnPageData.getInstance().getPage().equals("AddMemberDetail")){
                                            shardpref.putInt("SELECT_POSITION",0);
                                            shardpref.putInt("SELECT_POSITION_sub",0);
                                        }else{
                                            shardpref.putInt("SELECT_POSITION",2);
                                            shardpref.putInt("SELECT_POSITION_sub",0);
                                        }
                                        pm.Main(mContext);
                                    }else if(!place_owner_id.equals(USER_INFO_ID) && USER_INFO_AUTH.equals("1")){
                                        pm.Main2(mContext);
                                    }
//                                    getPartTimeYoil(setYoil);
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

    @Override
    public void onBackPressed() {
        shardpref.remove("item_user_id");
        shardpref.remove("item_user_name");
        super.onBackPressed();
    }
}
