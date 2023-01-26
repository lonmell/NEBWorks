package com.krafte.nebworks.bottomsheet;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.krafte.nebworks.R;
import com.krafte.nebworks.databinding.FragmentCommuteBottomSheetBinding;
import com.krafte.nebworks.util.PreferenceHelper;

public class CommuteBottomSheet extends BottomSheetDialogFragment {

    private FragmentCommuteBottomSheetBinding binding;
    private Context mContext;
    private PreferenceHelper shardpref;
    private boolean SELECT_POSITION = true; // True = 출근 시간, False = 퇴근 시간

    private String userName = "";
    private String workTime = "";
    private String goToWorkTime = "";
    private String goOffWorkTime = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCommuteBottomSheetBinding.inflate(getLayoutInflater());
        mContext = getLayoutInflater().getContext();
        shardpref = new PreferenceHelper(mContext);

        userName = shardpref.getString("commute_name", "");
        workTime = shardpref.getString("commute_work_time", "");
        goToWorkTime = shardpref.getString("commute_in_time", "");
        goOffWorkTime = shardpref.getString("commute_out_time", "");

        setData();
        setBtnEvent();

        return binding.getRoot();
    }

    private void setData() {
        binding.commuteUser.setText(userName);
        binding.commuteWorkingHours.setText("근무시간 " + (workTime.equals("null") ? "미정" : workTime));

        if (!goToWorkTime.equals("null")) {
            String[] splitGoToWorkTime = goToWorkTime.split(":");
            binding.gotoworkTextTime.setText(Integer.parseInt(splitGoToWorkTime[0]) < 12 ? "오전 " : "오후 " +
                    String.format("%02d:%02d", Integer.parseInt(splitGoToWorkTime[0]), Integer.parseInt(splitGoToWorkTime[1])));
        }
        if (!goOffWorkTime.equals("null")) {
            String[] splitGoOffWorkTime = goOffWorkTime.split(":");
            binding.gooffworkTextTime.setText(Integer.parseInt(splitGoOffWorkTime[0]) < 12 ? "오전 " : "오후 " +
                    String.format("%02d:%02d", Integer.parseInt(splitGoOffWorkTime[0]), Integer.parseInt(splitGoOffWorkTime[1])));
        }
    }

    // 출근 시간과 퇴근 시간 을 클릭시 선택 된 시간의 영역 색이 변하고 그 시간을 변경가능하게 함.
    private void setBtnEvent() {
        //출근 시간
        binding.gotowork.setOnClickListener(v -> {
            String[] splitGoToWorkTime = goToWorkTime.split(":");

            SELECT_POSITION = true;

            binding.commuteTimepicker.setVisibility(View.VISIBLE);

            binding.gotowork.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
            binding.gotoworkText.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            binding.gotoworkTextTime.setTextColor(ContextCompat.getColor(mContext, R.color.white));

            binding.gooffwork.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            binding.gooffworkText.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            binding.gooffworkTextTime.setTextColor(ContextCompat.getColor(mContext, R.color.black));

            if (!goToWorkTime.equals("null")) {
                binding.commuteTimepicker.setHour(Integer.parseInt(splitGoToWorkTime[0]));
                binding.commuteTimepicker.setMinute(Integer.parseInt(splitGoToWorkTime[1]));
            }
        });

        // 퇴근 시간
        binding.gooffwork.setOnClickListener(v -> {
            String[] splitGoOffWorkTime = goOffWorkTime.split(":");

            SELECT_POSITION = false;

            binding.commuteTimepicker.setVisibility(View.VISIBLE);

            binding.gooffwork.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
            binding.gooffworkText.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            binding.gooffworkTextTime.setTextColor(ContextCompat.getColor(mContext, R.color.white));

            binding.gotowork.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            binding.gotoworkText.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            binding.gotoworkTextTime.setTextColor(ContextCompat.getColor(mContext, R.color.black));

            if (!goOffWorkTime.equals("null")) {
                binding.commuteTimepicker.setHour(Integer.parseInt(splitGoOffWorkTime[0]));
                binding.commuteTimepicker.setMinute(Integer.parseInt(splitGoOffWorkTime[1]));
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
                } else {
                    goOffWorkTime = HOUR + ":" + MIN;
                    binding.gooffworkTextTime.setText((hourOfDay < 12 ? "오전" : "오후") + " " + (HOUR.length() == 1 ? "0" + HOUR : HOUR) + ":" + (MIN.length() == 1 ? "0" + MIN : MIN));
                }
            }
        });

        // 저장 버튼
        binding.commuteOk.setOnClickListener(v -> {

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}