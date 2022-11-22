package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;

import androidx.annotation.RequiresApi;

import com.krafte.nebworks.R;
import com.krafte.nebworks.databinding.ActivityRepeatsetpopBinding;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RepeatSetPop extends Activity {
    private ActivityRepeatsetpopBinding binding;
    private static final String TAG = "SelectTaskDatePop";
    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;

    //Other
    Dlog dlog = new Dlog();
    DateCurrent dc = new DateCurrent();

    Calendar cal;
    String format = "yyyy-MM-dd";
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    String USER_INFO_ID = "";
    String SET_TASK_TIME_VALUE = "";

    String overdate = "";
    String yoillist_String = "";//수정할때 필요한 변수
    boolean RepeatTF = false;
    //-- 1 : 매일 / 2 : 주중 매일(월~금) / 3 : 맞춤설정(요일맞춤)
    int RepeatKind = 0;
    List<String> yoillist = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ActivityRepeatsetpopBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2

        mContext = this;
        dlog.DlogContext(mContext);

        //기초 데이터
        RepeatTF = true;
        RepeatKind = 1;

        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID","0");
        //0 - 시작시간 / 1 - 마감시간
        SET_TASK_TIME_VALUE = shardpref.getString("SET_TASK_TIME_VALUE","0");
        yoillist_String = shardpref.getString("yoillist","");
        RepeatTF = shardpref.getBoolean("RepeatTF",false);
        RepeatKind = shardpref.getInt("RepeatKind",0);
        overdate = shardpref.getString("overdate","");

        dlog.i("----RepeatSetPop----");
        dlog.i("RepeatTF : " + RepeatTF);
        dlog.i("RepeatKind : " + RepeatKind);
        dlog.i("yoillist_String : " + yoillist_String);
        dlog.i("overdate : " + overdate);
        dlog.i("----RepeatSetPop----");

        //-- 기본데이터 세팅 START
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Year = String.valueOf(year);
                Month = String.valueOf(month+1);
                Day = String.valueOf(dayOfMonth);
                Day = Day.length()==1?"0"+Day:Day;
                Month = Month.length()==1?"0"+Month:Month;
                binding.autoaddOverdate.setText("   " + Year + "년 " + Month + "월 " + Day + "일");
            }
        }, mYear, mMonth, mDay);

        if(RepeatTF){
            binding.select01Round.setBackgroundResource(R.drawable.ic_full_round);
            binding.select01Box.setBackgroundResource(R.drawable.default_select_round);
            binding.select02Round.setBackgroundResource(R.drawable.ic_empty_round);
            binding.select02Box.setBackgroundResource(R.drawable.default_gray_round);

            binding.select03Box.setClickable(true);
            binding.select04Box.setClickable(true);
            binding.select05Box.setClickable(true);
            if(RepeatKind == 1){
                //매일
                binding.select03Box.setBackgroundResource(R.drawable.default_select_round);
                binding.select03Round.setBackgroundResource(R.drawable.ic_full_round);
                binding.select04Box.setBackgroundResource(R.drawable.default_gray_round);
                binding.select04Round.setBackgroundResource(R.drawable.ic_empty_round);
                binding.select05Box.setBackgroundResource(R.drawable.default_gray_round);
                binding.select05Round.setBackgroundResource(R.drawable.ic_empty_round);
                binding.yoilArea.setVisibility(View.GONE);
                yoillist.clear();
                yoillist.add("월");
                yoillist.add("화");
                yoillist.add("수");
                yoillist.add("목");
                yoillist.add("금");
                yoillist.add("토");
                yoillist.add("일");
            }else if(RepeatKind == 2){
                //주중 매일(월 ~ 금)
                binding.select03Box.setBackgroundResource(R.drawable.default_gray_round);
                binding.select03Round.setBackgroundResource(R.drawable.ic_empty_round);
                binding.select04Box.setBackgroundResource(R.drawable.default_select_round);
                binding.select04Round.setBackgroundResource(R.drawable.ic_full_round);
                binding.select05Box.setBackgroundResource(R.drawable.default_gray_round);
                binding.select05Round.setBackgroundResource(R.drawable.ic_empty_round);
                binding.yoilArea.setVisibility(View.GONE);
                yoillist.clear();
                yoillist.add("월");
                yoillist.add("화");
                yoillist.add("수");
                yoillist.add("목");
                yoillist.add("금");
            }else if(RepeatKind == 3){
                //맞춤설정
                binding.select03Box.setBackgroundResource(R.drawable.default_gray_round);
                binding.select03Round.setBackgroundResource(R.drawable.ic_empty_round);
                binding.select04Box.setBackgroundResource(R.drawable.default_gray_round);
                binding.select04Round.setBackgroundResource(R.drawable.ic_empty_round);
                binding.select05Box.setBackgroundResource(R.drawable.default_select_round);
                binding.select05Round.setBackgroundResource(R.drawable.ic_full_round);
                binding.yoilArea.setVisibility(View.VISIBLE);
                for(String str:yoillist_String.split(",")){
                    yoillist.add(str);
                    if(str.contains("월")){
                        montf = true;
                        binding.mon.setBackgroundResource(R.drawable.default_select_round);
                    }else if(str.contains("화")){
                        tuetf = true;
                        binding.tue.setBackgroundResource(R.drawable.default_select_round);
                    }else if(str.contains("수")){
                        wedtf = true;
                        binding.wed.setBackgroundResource(R.drawable.default_select_round);
                    }else if(str.contains("목")){
                        thutf = true;
                        binding.thu.setBackgroundResource(R.drawable.default_select_round);
                    }else if(str.contains("금")){
                        fritf = true;
                        binding.fri.setBackgroundResource(R.drawable.default_select_round);
                    }else if(str.contains("토")){
                        sattf = true;
                        binding.sat.setBackgroundResource(R.drawable.default_select_round);
                    }else if(str.contains("일")){
                        suntf = true;
                        binding.sun.setBackgroundResource(R.drawable.default_select_round);
                    }
                }
            }
        }else{
            binding.select01Round.setBackgroundResource(R.drawable.ic_empty_round);
            binding.select01Box.setBackgroundResource(R.drawable.default_gray_round);
            binding.select02Round.setBackgroundResource(R.drawable.ic_full_round);
            binding.select02Box.setBackgroundResource(R.drawable.default_select_round);

            binding.select03Box.setBackgroundResource(R.drawable.grayback_gray_round);
            binding.select03Round.setBackgroundResource(R.drawable.ic_empty_round);
            binding.select04Box.setBackgroundResource(R.drawable.grayback_gray_round);
            binding.select04Round.setBackgroundResource(R.drawable.ic_empty_round);
            binding.select05Box.setBackgroundResource(R.drawable.grayback_gray_round);
            binding.select05Round.setBackgroundResource(R.drawable.ic_empty_round);
            binding.yoilArea.setVisibility(View.GONE);
            binding.select03Box.setClickable(false);
            binding.select04Box.setClickable(false);
            binding.select05Box.setClickable(false);
        }
        binding.autoaddOverdate.setText(overdate);
        binding.autoaddOverdate.setOnClickListener(v -> {
            if(binding.autoaddOverdate.getText().toString().length() == 0){
                binding.autoaddOverdate.setText(dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY);
            }else{
                if (binding.autoaddOverdate.isClickable()) {
                    datePickerDialog.show();
                }
            }
        });

        //-- 기본데이터 세팅 END
        setBtnEvent();
    }


    String Year = "";
    String Month = "";
    String Day = "";
    boolean montf = false;
    boolean tuetf = false;
    boolean wedtf = false;
    boolean thutf = false;
    boolean fritf = false;
    boolean sattf = false;
    boolean suntf = false;

    @Override
    public void onResume(){
        super.onResume();
    }

    private void setBtnEvent(){
        binding.backBtn.setOnClickListener(v -> {
            closePop();
        });

        binding.select01Box.setOnClickListener(v -> {
            if(!RepeatTF){
                binding.select03Box.setBackgroundResource(R.drawable.default_gray_round);
                binding.select03Round.setBackgroundResource(R.drawable.ic_empty_round);
                binding.select04Box.setBackgroundResource(R.drawable.default_gray_round);
                binding.select04Round.setBackgroundResource(R.drawable.ic_empty_round);
                binding.select05Box.setBackgroundResource(R.drawable.default_gray_round);
                binding.select05Round.setBackgroundResource(R.drawable.ic_empty_round);
            }
            RepeatTF = true;
            binding.select01Round.setBackgroundResource(R.drawable.ic_full_round);
            binding.select01Box.setBackgroundResource(R.drawable.default_select_round);
            binding.select02Round.setBackgroundResource(R.drawable.ic_empty_round);
            binding.select02Box.setBackgroundResource(R.drawable.default_gray_round);

            binding.select03Box.setClickable(true);
            binding.select04Box.setClickable(true);
            binding.select05Box.setClickable(true);
        });
        binding.select02Box.setOnClickListener(v -> {
            RepeatTF = false;
            binding.select01Round.setBackgroundResource(R.drawable.ic_empty_round);
            binding.select01Box.setBackgroundResource(R.drawable.default_gray_round);
            binding.select02Round.setBackgroundResource(R.drawable.ic_full_round);
            binding.select02Box.setBackgroundResource(R.drawable.default_select_round);

            binding.select03Box.setBackgroundResource(R.drawable.grayback_gray_round);
            binding.select03Round.setBackgroundResource(R.drawable.ic_empty_round);
            binding.select04Box.setBackgroundResource(R.drawable.grayback_gray_round);
            binding.select04Round.setBackgroundResource(R.drawable.ic_empty_round);
            binding.select05Box.setBackgroundResource(R.drawable.grayback_gray_round);
            binding.select05Round.setBackgroundResource(R.drawable.ic_empty_round);
            binding.yoilArea.setVisibility(View.GONE);
            binding.select03Box.setClickable(false);
            binding.select04Box.setClickable(false);
            binding.select05Box.setClickable(false);
            yoillist.clear();
        });

        binding.select03Box.setOnClickListener(v -> {
            //매일
            RepeatKind = 1;
            binding.select03Box.setBackgroundResource(R.drawable.default_select_round);
            binding.select03Round.setBackgroundResource(R.drawable.ic_full_round);
            binding.select04Box.setBackgroundResource(R.drawable.default_gray_round);
            binding.select04Round.setBackgroundResource(R.drawable.ic_empty_round);
            binding.select05Box.setBackgroundResource(R.drawable.default_gray_round);
            binding.select05Round.setBackgroundResource(R.drawable.ic_empty_round);
            binding.yoilArea.setVisibility(View.GONE);
            yoillist.clear();
            yoillist.add("월");
            yoillist.add("화");
            yoillist.add("수");
            yoillist.add("목");
            yoillist.add("금");
            yoillist.add("토");
            yoillist.add("일");
        });
        binding.select04Box.setOnClickListener(v -> {
            //주중 매일(월 ~ 금)
            RepeatKind = 2;
            binding.select03Box.setBackgroundResource(R.drawable.default_gray_round);
            binding.select03Round.setBackgroundResource(R.drawable.ic_empty_round);
            binding.select04Box.setBackgroundResource(R.drawable.default_select_round);
            binding.select04Round.setBackgroundResource(R.drawable.ic_full_round);
            binding.select05Box.setBackgroundResource(R.drawable.default_gray_round);
            binding.select05Round.setBackgroundResource(R.drawable.ic_empty_round);
            binding.yoilArea.setVisibility(View.GONE);
            yoillist.clear();
            yoillist.add("월");
            yoillist.add("화");
            yoillist.add("수");
            yoillist.add("목");
            yoillist.add("금");
        });
        binding.select05Box.setOnClickListener(v -> {
            //맞춤설정
            RepeatKind = 3;
            binding.select03Box.setBackgroundResource(R.drawable.default_gray_round);
            binding.select03Round.setBackgroundResource(R.drawable.ic_empty_round);
            binding.select04Box.setBackgroundResource(R.drawable.default_gray_round);
            binding.select04Round.setBackgroundResource(R.drawable.ic_empty_round);
            binding.select05Box.setBackgroundResource(R.drawable.default_select_round);
            binding.select05Round.setBackgroundResource(R.drawable.ic_full_round);
            binding.yoilArea.setVisibility(View.VISIBLE);
            yoillist.clear();
        });
        binding.mon.setOnClickListener(v -> {
            if(!montf){
                montf = true;
                yoillist.add("월");
                binding.mon.setBackgroundResource(R.drawable.default_select_round);
            }else{
                montf = false;
                yoillist.remove("월");
                binding.mon.setBackgroundResource(R.drawable.grayback_gray_round);
            }
        });
        binding.tue.setOnClickListener(v -> {
            if(!tuetf){
                tuetf = true;
                yoillist.add("화");
                binding.tue.setBackgroundResource(R.drawable.default_select_round);
            }else{
                tuetf = false;
                yoillist.remove("화");
                binding.tue.setBackgroundResource(R.drawable.grayback_gray_round);
            }
        });
        binding.wed.setOnClickListener(v -> {
            if(!wedtf){
                wedtf = true;
                yoillist.add("수");
                binding.wed.setBackgroundResource(R.drawable.default_select_round);
            }else{
                wedtf = false;
                yoillist.remove("수");
                binding.wed.setBackgroundResource(R.drawable.grayback_gray_round);
            }
        });
        binding.thu.setOnClickListener(v -> {
            if(!thutf){
                thutf = true;
                yoillist.add("목");
                binding.thu.setBackgroundResource(R.drawable.default_select_round);
            }else{
                thutf = false;
                yoillist.remove("목");
                binding.thu.setBackgroundResource(R.drawable.grayback_gray_round);
            }
        });
        binding.fri.setOnClickListener(v -> {
            if(!fritf){
                fritf = true;
                yoillist.add("금");
                binding.fri.setBackgroundResource(R.drawable.default_select_round);
            }else{
                fritf = false;
                yoillist.remove("금");
                binding.fri.setBackgroundResource(R.drawable.grayback_gray_round);
            }
        });
        binding.sat.setOnClickListener(v -> {
            if(!sattf){
                sattf = true;
                yoillist.add("토");
                binding.sat.setBackgroundResource(R.drawable.default_select_round);
            }else{
                sattf = false;
                yoillist.remove("토");
                binding.sat.setBackgroundResource(R.drawable.grayback_gray_round);
            }
        });
        binding.sun.setOnClickListener(v -> {
            if(!suntf){
                suntf = true;
                yoillist.add("일");
                binding.sun.setBackgroundResource(R.drawable.default_select_round);
            }else{
                suntf = false;
                yoillist.remove("일");
                binding.sun.setBackgroundResource(R.drawable.grayback_gray_round);
            }
        });

        binding.workSave.setOnClickListener(v -> {
            String overdate = binding.autoaddOverdate.getText().toString();
            dlog.i("-----RepeatSetPop-----");
            dlog.i("yoil List : " + yoillist);
            dlog.i("overdate : " + overdate);
            shardpref.putBoolean("RepeatTF",RepeatTF);
            shardpref.putInt("RepeatKind",RepeatKind);
            shardpref.putString("yoillist", String.valueOf(yoillist).replace("[","").replace("]",""));
            shardpref.putString("overdate", overdate);
            closePop();
            dlog.i("-----RepeatSetPop-----");
        });
    }



    private void closePop() {
        finish();
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);
        overridePendingTransition(0, R.anim.translate_down);
    }
}
