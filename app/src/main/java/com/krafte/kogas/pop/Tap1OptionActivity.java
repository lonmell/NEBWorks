package com.krafte.kogas.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import androidx.annotation.RequiresApi;

import com.krafte.kogas.R;
import com.krafte.kogas.data.GetResultData;
import com.krafte.kogas.databinding.ActivityTap1optionBinding;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PageMoveClass;
import com.krafte.kogas.util.PreferenceHelper;

/*
 * 2022-10-07 방창배 작성
 * */
public class Tap1OptionActivity extends Activity {
    private ActivityTap1optionBinding binding;
    private static final String TAG = "Tap1OptionActivity";
    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;

    //Other
    Dlog dlog = new Dlog();
    PageMoveClass pm = new PageMoveClass();
    GetResultData resultData = new GetResultData();
    Handler mHandler;
    String storenoti_no = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_tap1option);
        binding = ActivityTap1optionBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2

        mContext = this;
        dlog.DlogContext(mContext);

        shardpref = new PreferenceHelper(mContext);
        storenoti_no = shardpref.getString("storenoti_no","0");



        binding.storeEdit.setText("공지 수정");
        binding.storeEdit.setOnClickListener(v -> {
//            Intent intent = new Intent(mContext, StoreNotiEditActivity.class);
//            intent.putExtra("no", shardpref.getString("storenoti_no",""));
//            intent.putExtra("title", shardpref.getString("storenoti_title",""));
//            intent.putExtra("contents", shardpref.getString("storenoti_contents",""));
//            intent.putExtra("imgpath", shardpref.getString("storenoti_imgpath",""));
//            intent.putExtra("open_date", shardpref.getString("storenoti_open_date",""));
//            intent.putExtra("close_date", shardpref.getString("storenoti_close_date",""));
//            mContext.startActivity(intent);
//            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        });

        binding.storeDelete.setText("공지 삭제");
        binding.storeDelete.setOnClickListener(v -> {
            finish();
            Intent intent = new Intent(this, TwoButtonPopActivity.class);
            intent.putExtra("flag","공지삭제");
            intent.putExtra("data","해당 공지를 삭제하시겠습니까?");
            intent.putExtra("left_btn_txt", "삭제");
            intent.putExtra("right_btn_txt", "취소");
            startActivity(intent);
            overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        });

        binding.cancel.setOnClickListener(v -> {
            finish();
            Intent intent = new Intent();
            intent.putExtra("result", "Close Popup");
            setResult(RESULT_OK, intent);
            overridePendingTransition(0, R.anim.translate_down);
        });
    }

    @Override
    public void onStop(){
        super.onStop();
        finish();
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);
        overridePendingTransition(0, R.anim.translate_down);
    }

}
