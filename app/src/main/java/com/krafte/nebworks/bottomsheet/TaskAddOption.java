package com.krafte.nebworks.bottomsheet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.krafte.nebworks.R;
import com.krafte.nebworks.databinding.ActivityBottomMemberoptionBinding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

public class TaskAddOption extends BottomSheetDialogFragment {
    private ActivityBottomMemberoptionBinding binding;
    private static final String TAG = "MemberOption";
    Context mContext;

    //Other
    String btn01 = "";
    String btn02 = "";
    String data = "";
    Intent intent;

    // shared 저장값
    PreferenceHelper shardpref;

    //Other
    Dlog dlog = new Dlog();
    PageMoveClass pm = new PageMoveClass();

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.activity_storedivisionpop, container, false);
        binding = ActivityBottomMemberoptionBinding.inflate(getLayoutInflater(), container, false);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
        mContext = inflater.getContext();
        shardpref = new PreferenceHelper(mContext);
        dlog.DlogContext(mContext);

        binding.title.setText("신규 할일 추가");
        binding.selecttv01.setText("할일추가");
        binding.selecttv011.setText("새로운 할일을 추가합니다.");
        binding.selecttv02.setText("자주하는 업무 추가");
        binding.selecttv021.setText("저장된 자주하는 업무를 불러옵니다.");

        binding.certi01.setBackgroundResource(R.drawable.ic_icon_report);
        binding.certi01.setBackgroundResource(R.drawable.ic_contact_icon);

        setBtnEvent();
        Log.i(TAG, "data : " + data);
        shardpref.remove("make_kind");
        return binding.getRoot();
    }

    //확인 버튼 클릭
    private void setBtnEvent() {
        //할일추가
        binding.directlyAdd.setOnClickListener(v -> {
            pm.addWorkGo(mContext);
            shardpref.putInt("make_kind",1);
            ClosePop();
        });
        //자주하는 업무 추가
        binding.invateAdd.setOnClickListener(v -> {
            pm.TaskReuse(mContext);
            ClosePop();
        });
        binding.cancel.setOnClickListener(v -> {
            ClosePop();
        });
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        //바깥레이어 클릭시 안닫히게
//        return event.getAction() != MotionEvent.ACTION_OUTSIDE;
//    }

    private void ClosePop() {
        dismiss();
    }
}
