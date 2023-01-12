package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;

import androidx.fragment.app.FragmentManager;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.databinding.ActivityWorkAssignmentBinding;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import java.text.DecimalFormat;

public class PlaceBottomNaviActivity extends Activity {
    private ActivityWorkAssignmentBinding binding;
    private static final String TAG = "EmployerOptionActivity";
    Context mContext;
    private View view;

    Activity activity;
    FragmentManager fragmentManager;

    //XML ID
//    TextView list_settingitem01,list_settingitem02;
//    CardView close_btn;

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String USER_INFO_AUTH = "";
    int SELECTED_POSITION = 0;
    String place_id = "";
    String place_name = "";
    int make_kind = 0;

    //Other
    private final DateCurrent dc = new DateCurrent();
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private String result = "";
    GetResultData resultData = new GetResultData();
    Handler mHandler;
    Dlog dlog = new Dlog();
    PageMoveClass pm = new PageMoveClass();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_work_assignment);
        binding = ActivityWorkAssignmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this;

        dlog.DlogContext(mContext);

        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
        USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH","-99");
        SELECTED_POSITION = shardpref.getInt("SELECTED_POSITION", 0);
        place_id = shardpref.getString("place_id", "-1");
        place_name = shardpref.getString("place_name", "-1");
        make_kind = shardpref.getInt("make_kind", 0);

        binding.listSettingitem01.setText("매장 수정");
        binding.listSettingitem02.setText("매장 삭제");

        setBtnEvent();
    }


    //list_settingitem01
    public interface OnClickListener01 {
        void onClick(View v) ;
    }
    private OnClickListener01 mListener01 = null ;
    public void setOnClickListener01(OnClickListener01 listener) {
        this.mListener01 = listener ;
    }

    //list_settingitem02
    public interface OnClickListener02 {
        void onClick(View v) ;
    }
    private OnClickListener02 mListener02 = null ;
    public void setOnClickListener02(OnClickListener02 listener) {
        this.mListener02 = listener ;
    }


    private void setBtnEvent(){
        binding.listSettingitem01.setOnClickListener(v -> {
            finish();
            Intent intent = new Intent();
            intent.putExtra("result", "Close Popup");
            setResult(RESULT_OK, intent);
            overridePendingTransition(0, R.anim.translate_down);
            shardpref.getInt("make_kind",make_kind);
            pm.PlaceEditGo(mContext);
        });
        binding.listSettingitem02.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
            intent.putExtra("data", "[" + place_name + "]\n" + "매장을 삭제하시겠습니까?");
            intent.putExtra("flag", "매장삭제");
            intent.putExtra("left_btn_txt", "취소");
            intent.putExtra("right_btn_txt", "삭제");
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            PlaceDel(place_id);
            ClosePop();
        });

        binding.closeBtn.setOnClickListener(v -> {
            ClosePop();
        });
    }

    private void ClosePop(){
        runOnUiThread(() -> {
                finish();
                Intent intent = new Intent();
                intent.putExtra("result", "Close Popup");
                setResult(RESULT_OK, intent);
                overridePendingTransition(0, R.anim.translate_down);
        });
    }

}
