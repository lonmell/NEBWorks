package com.krafte.kogas.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;

import com.krafte.kogas.R;
import com.krafte.kogas.data.GetResultData;
import com.krafte.kogas.dataInterface.MemberOutPlaceInterface;
import com.krafte.kogas.util.DateCurrent;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PageMoveClass;
import com.krafte.kogas.util.PreferenceHelper;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class WorkMemberOptionActivity extends Activity {

    private static final String TAG = "WorkMemberOptionActivity";
    Context mContext;
    private View view;

    Activity activity;
    FragmentManager fragmentManager;

    //XML ID
    TextView list_settingitem02;
    CardView close_btn;

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String USER_INFO_AUTH = "";
    int SELECTED_POSITION = 0;
    String Employee_id = "";
    String Employer_id = "";
    String notify_store_no = "";
    String TaskNo = "";
    int make_kind = 0;
    String store_no = "";

    String place_id            = "";
    String user_id             = "";
    Intent intent;

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
        setContentView(R.layout.activity_memberoption);
        mContext = this;

        dlog.DlogContext(mContext);

        //데이터 가져오기
        intent = getIntent();
        place_id             = intent.getStringExtra("place_id");
        user_id             = intent.getStringExtra("user_id");
        Log.i(TAG,"place_id : " + place_id);
        Log.i(TAG,"user_id : " + user_id);

        shardpref = new PreferenceHelper(mContext);

        list_settingitem02 = findViewById(R.id.list_settingitem02);
        close_btn = findViewById(R.id.close_btn);
        setBtnEvent();

    }

    private void setBtnEvent(){

        list_settingitem02.setOnClickListener(v -> {
            TaskDel();
            finish();
            Intent intent = new Intent();
            intent.putExtra("result", "Close Popup");
            setResult(RESULT_OK, intent);
            overridePendingTransition(0, R.anim.translate_down);
        });

        close_btn.setOnClickListener(v -> {
            finish();
            Intent intent = new Intent();
            intent.putExtra("result", "Close Popup");
            setResult(RESULT_OK, intent);
            overridePendingTransition(0, R.anim.translate_down);
        });
    }


    public void TaskDel() {
//        현장 멤버 삭제 (현장에서 나가기, 현장에서 내보내기)
//        http://krafte.net/kogas/place/delete_member.php?place_id=28&user_id=24
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MemberOutPlaceInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        MemberOutPlaceInterface api = retrofit.create(MemberOutPlaceInterface.class);
        Call<String> call = api.getData(place_id,user_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            dlog.i("TaskDel jsonResponse length : " + response.body().length());
                            dlog.i("TaskDel jsonResponse : " + response.body());
                            try {
                                if(response.body().replace("\"","").equals("success")){
                                    Toast.makeText(mContext,"해당 직원의 내보내기가 완료되었습니다.",Toast.LENGTH_SHORT).show();
                                    finish();
                                    Intent intent = new Intent();
                                    intent.putExtra("result", "Close Popup");
                                    setResult(RESULT_OK, intent);
                                    overridePendingTransition(0, R.anim.translate_down);
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
}
