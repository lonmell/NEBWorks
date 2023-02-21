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

    String place_name = "";

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
        binding.certi02.setBackgroundResource(R.drawable.ic_mail_icon);

        place_name = shardpref.getString("place_name","");

        return binding.getRoot();
    }

    //확인 버튼 클릭
    private void setBtnEvent() {
        binding.directlyAdd.setOnClickListener(v -> {
            pm.DirectAddMember(mContext);
            ClosePop();
        });
        binding.invateAdd.setOnClickListener(v -> {
//            pm.InviteMember(mContext);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");

            // String으로 받아서 넣기
            String sendMessage = "["+place_name+"] 사장님과 즐겁게 근무해요.\n" +
                    "\n" +
                    "[매장근무하기]\n" +
                    "\n" +
                    "1.사장님넵 다운로드 \n" +
                    "\n" +
                    "Android: https://play.google.com/store/apps/details?id=com.krafte.nebworks\n" +
                    "\n" +
                    "IOS:  https://apps.apple.com/apps/details?id=com.krafte.nebworks\n" +
                    "\n" +
                    "2.앱 설치후 회원가입 > 로그인 > 근무자님! 으로 이동 후 매장추가 버튼 터치! \n" +
                    "매장 찾기 후 사장님 번호로 매장 검색!\n" +
                    "근무 신청 터치!\n" +
                    "\n";
            intent.putExtra(Intent.EXTRA_TEXT, sendMessage);
            Intent shareIntent = Intent.createChooser(intent, "share");
            startActivity(shareIntent);
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
