package com.krafte.nebworks.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.databinding.ActivityStoredivisionpopBinding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.PreferenceHelper;

import java.text.DecimalFormat;

public class StoreDivisionPopActivity extends BottomSheetDialogFragment {
    private ActivityStoredivisionpopBinding binding;
    private static final String TAG = "StoreDivisionPopActivity";
    Context mContext;
    View view;
    Activity activity;

    //XML ID
    NumberPicker select_picker;
    CardView sendpicker_value;

    // shared 저장값
    PreferenceHelper shardpref;

    //Other
    DateCurrent dc = new DateCurrent();
    DecimalFormat decimalFormat = new DecimalFormat("#,###");
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    int setSelectPicker = 0;


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.activity_storedivisionpop, container, false);
        binding = ActivityStoredivisionpopBinding.inflate(getLayoutInflater(),container,false);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
        mContext = inflater.getContext();

        //첫번째줄
        binding.selectCate01.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(v, "일반음식점");
            }
            dismiss();
        });
        binding.selectCate02.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(v,"편의점");
            }
            dismiss();
        });
        binding.selectCate03.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(v,"피시방");
            }
            dismiss();
        });

        //두번째줄
        binding.selectCate04.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(v,"테마파크");
            }
            dismiss();
        });
        binding.selectCate05.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(v,"호텔숙박");
            }
            dismiss();
        });
        binding.selectCate06.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(v,"패스트푸드");
            }
            dismiss();
        });

        //세번째줄
        binding.selectCate07.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(v,"아이스크림&amp;디저트");
            }
            dismiss();
        });
        binding.selectCate08.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(v,"호프/일반주점");
            }
            dismiss();
        });
        binding.selectCate09.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(v,"피트니스");
            }
            dismiss();
        });

        //네번째줄
        binding.selectCate10.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(v,"패밀리레스토랑");
            }
            dismiss();
        });

        return binding.getRoot();
    }

    /*Fragment 콜백함수*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, String category);
    }


}
