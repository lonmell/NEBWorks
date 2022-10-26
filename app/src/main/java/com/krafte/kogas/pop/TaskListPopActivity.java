package com.krafte.kogas.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.kogas.R;
import com.krafte.kogas.adapter.TaskListPopAdapter;
import com.krafte.kogas.data.TaskPointPopData;
import com.krafte.kogas.databinding.ActivityTasklistPopBinding;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PreferenceHelper;

import java.util.ArrayList;

public class TaskListPopActivity extends Activity {
    private ActivityTasklistPopBinding binding;
    private static final String TAG = "TaskListPopActivity";

    Context mContext;
    Intent intent;

    //shared Data
    PreferenceHelper shardpref;
    String store_no = "";
    String USER_INFO_ID = "";
    String USER_LOGIN_METHOD = "";

    //Other
    Dlog dlog = new Dlog();
    ArrayList<String> kind;
    ArrayList<String> title;
    String date = "";
    String yoil = "";
    String write_name = "";
    ArrayList<TaskPointPopData.TaskPointPopData_list> taskpointlist;
    TaskListPopAdapter mAdapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_twobutton_pop);
        binding = ActivityTasklistPopBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        getWindow().getAttributes().width = (int) (dm.widthPixels * 0.8);

        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        setBtnEvent();


        //데이터 가져오기
        intent = getIntent();
        kind = intent.getStringArrayListExtra("kind");
        title = intent.getStringArrayListExtra("title");
        date = intent.getStringExtra("date");
        yoil = intent.getStringExtra("yoil");
        write_name = intent.getStringExtra("write_name");

        dlog.i("kind :" + kind);
        dlog.i("title :" + title);
        dlog.i("date :" + date);
        dlog.i("yoil :" + yoil);
        dlog.i("kind.size():" + kind.size());

        binding.date.setText(date + "일");
        binding.yoil.setText(yoil);

        taskpointlist = new ArrayList<>();
        mAdapter = new TaskListPopAdapter(mContext, taskpointlist);
        binding.taskListPop.setAdapter(mAdapter);
        binding.taskListPop.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        if(title.size() == 0){
            binding.noDataTxt.setVisibility(View.VISIBLE);
        }else{
            binding.noDataTxt.setVisibility(View.GONE);
        }
        taskpointlist.clear();
        for (int i = 0; i < kind.size(); i++) {
            dlog.i("kind : " + kind.get(i) );
            dlog.i("title : " + title.get(i) );
            mAdapter.addItem(new TaskPointPopData.TaskPointPopData_list(
                    kind.get(i),
                    title.get(i)
            ));
        }
        mAdapter.notifyDataSetChanged();
        kind = new ArrayList<>();
        title = new ArrayList<>();

        USER_INFO_ID = shardpref.getString("USER_INFO_EMAIL","");
        USER_LOGIN_METHOD = shardpref.getString("USER_LOGIN_METHOD","");
    }

    //확인 버튼 클릭
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setBtnEvent() {

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
            super.onBackPressed();
        });
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        //바깥레이어 클릭시 안닫히게
//        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public void onBackPressed() {
        //액티비티(팝업) 닫기
        finish();
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);
        overridePendingTransition(0, R.anim.translate_down);
    }
}
