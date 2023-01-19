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

public class MemberOption extends BottomSheetDialogFragment {
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
    int optionkind = 0;//0 -- 직원관리 , 1 -- 할일 관리

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

        setBtnEvent();
        Log.i(TAG, "data : " + data);
        binding.certi01.setBackgroundResource(R.drawable.ic_pencil_icon);
        binding.certi01.setBackgroundResource(R.drawable.ic_mail_icon);

        return binding.getRoot();
    }

    //확인 버튼 클릭
    private void setBtnEvent() {
        binding.directlyAdd.setOnClickListener(v -> {
            pm.DirectAddMember(mContext);
            ClosePop();
        });
        binding.invateAdd.setOnClickListener(v -> {
            pm.InviteMember(mContext);
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
