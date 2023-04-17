package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.dataInterface.TaskDelInterface;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Tap2OptionActivity extends Activity {
    private static final String TAG = "Tap2OptionActivity";
    Context mContext;
    private View view;

    Activity activity;
    FragmentManager fragmentManager;

    //XML ID
    TextView list_settingitem01,list_settingitem02;
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
        setContentView(R.layout.activity_work_assignment);
        mContext = this;

        dlog.DlogContext(mContext);

        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        store_no = shardpref.getString("store_no", "");
        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
        USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH","");
        SELECTED_POSITION = shardpref.getInt("SELECTED_POSITION", 0);
        TaskNo = shardpref.getString("task_no", "-1");
        make_kind = shardpref.getInt("make_kind", 0);

        list_settingitem01 = findViewById(R.id.list_settingitem01);
        list_settingitem02 = findViewById(R.id.list_settingitem02);
        close_btn = findViewById(R.id.close_btn);
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
    private Tap2OptionActivity.OnClickListener02 mListener02 = null ;
    public void setOnClickListener02(Tap2OptionActivity.OnClickListener02 listener) {
        this.mListener02 = listener ;
    }


    private void setBtnEvent(){
        list_settingitem01.setOnClickListener(v -> {
            finish();
            Intent intent = new Intent();
            intent.putExtra("result", "Close Popup");
            setResult(RESULT_OK, intent);
            overridePendingTransition(0, R.anim.translate_down);
            shardpref.putInt("make_kind", 1);
            pm.addWorkGo(mContext);
        });
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

    RetrofitConnect rc = new RetrofitConnect();
    public void TaskDel() {
        dlog.i("TaskDel id : " + TaskNo);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TaskDelInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TaskDelInterface api = retrofit.create(TaskDelInterface.class);
        Call<String> call = api.getData(TaskNo);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            try {
                                if(response.body().replace("\"","").equals("success")){
                                    Toast.makeText(mContext,"해당 업무가 삭제완료되었습니다.",Toast.LENGTH_SHORT).show();
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
