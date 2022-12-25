package com.krafte.nebworks.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.krafte.nebworks.R;
import com.krafte.nebworks.databinding.ActivityTimepickerPopBinding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;

public class WorkTimePicker extends BottomSheetDialogFragment {
    private static final String TAG = "WorkTimePicker";
    private ActivityTimepickerPopBinding binding;
    Context mContext;
    private View view;

    Activity activity;
    FragmentManager fragmentManager;

    // shared 저장값
    Dlog dlog = new Dlog();
    int data;
    int Hour = 0;
    int Min = 0;
    PreferenceHelper shardpref;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.activity_placelist, container, false);
        binding = ActivityTimepickerPopBinding.inflate(getLayoutInflater(), container, false);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
        try{
            mContext = inflater.getContext();
            dlog.DlogContext(mContext);
            fragmentManager = getParentFragmentManager();

            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);

            setBtnEvent();
            binding.timeSetpicker.setIs24HourView(false);
        }catch (Exception e){
            e.printStackTrace();
        }
        return binding.getRoot();
    }

    //list_settingitem01
    public interface OnClickListener {
        void onClick(View v, String hour, String min) ;
    }
    private OnClickListener mListener = null ;
    public void setOnClickListener(OnClickListener listener) {
        this.mListener = listener ;
    }

    //확인 버튼 클릭
    @SuppressLint("ObsoleteSdkInt")
    private void setBtnEvent() {
        binding.saveBtn.setOnClickListener(v -> {
            binding.timeSetpicker.clearFocus();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(binding.timeSetpicker.getHour() == 0){
                    Hour = 12;
                }else{
                    Hour = binding.timeSetpicker.getHour();
                }
                Min = binding.timeSetpicker.getMinute();
            }else{
                if(binding.timeSetpicker.getCurrentHour() == 0){
                    Hour = 12;
                }else{
                    Hour = binding.timeSetpicker.getCurrentHour();
                }
                Min = binding.timeSetpicker.getCurrentMinute();
            }

            dlog.i("WorkTimePicker Hour : " + Hour);
            dlog.i("WorkTimePicker Min : " + Min);
            //데이터 전달하기
            //액티비티(팝업) 닫기
            mListener.onClick(view , String.valueOf(Hour) ,String.valueOf(Min));
            dismiss();
        });
        binding.cancelBtn.setOnClickListener(v -> {
            //데이터 전달하기
            //액티비티(팝업) 닫기
           dismiss();
        });
    }


    /*Fragment 콜백함수*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }
}
