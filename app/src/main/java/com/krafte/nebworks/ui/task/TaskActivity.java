package com.krafte.nebworks.ui.task;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.bottomsheet.PaySelectMemberActivity;
import com.krafte.nebworks.bottomsheet.PaySelectPlaceActivity;
import com.krafte.nebworks.bottomsheet.TaskAddOption;
import com.krafte.nebworks.databinding.ActivityTaskBinding;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.util.ArrayList;

public class TaskActivity extends AppCompatActivity {
    private ActivityTaskBinding binding;
    Context mContext;

    //Other
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    DateCurrent dc = new DateCurrent();
    ArrayList<String> mList;
    Handler mHandler;
    RetrofitConnect rc = new RetrofitConnect();

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String USER_INFO_AUTH = "";
    String place_id = "";
    String place_owner_id = "";

    String change_place_id = "";
    String change_place_name = "";
    String change_member_id = "";
    String change_member_name = "";

    int SELECT_POSITION = 0;
    int SELECT_POSITION_sub = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityTaskBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        try {
            mContext = this;
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            place_id = shardpref.getString("place_id", "");
            place_owner_id = shardpref.getString("place_owner_id", "");
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
            SELECT_POSITION = shardpref.getInt("SELECT_POSITION", 0);
            SELECT_POSITION_sub = shardpref.getInt("SELECT_POSITION_sub", 0);

            binding.changePlace.setOnClickListener(v -> {
                PaySelectPlaceActivity psp = new PaySelectPlaceActivity();
                psp.show(getSupportFragmentManager(), "PaySelectPlaceActivity");
                psp.setOnClickListener(new PaySelectPlaceActivity.OnClickListener() {
                    @Override
                    public void onClick(View v, String getplace_id, String getplace_name) {
                        change_place_id = getplace_id;
                        change_place_name = getplace_name;
                        dlog.i("change_place_id : " + getplace_id);
                        dlog.i("change_place_name : " + getplace_name);
                        if (getplace_name.equals("전체매장")) {
                            binding.changePlaceTv.setText("전체매장");
                            change_place_id = "";
                            change_place_name = "";
                            shardpref.putString("change_place_id", place_id);
                            shardpref.putString("change_place_name", "");
                        } else {
                            binding.changePlaceTv.setText(getplace_name);
                            shardpref.putString("change_place_id", getplace_id);
                            shardpref.putString("change_place_name", getplace_name);
                        }
                        dlog.i("change_place_id : " + change_place_id);
                        dlog.i("change_place_name : " + change_place_name);

//                        WritePaymentList(change_place_id.equals("") ? place_id : change_place_id, change_member_id, binding.setdate.getText().toString());
                    }
                });
            });

            binding.changeMember.setOnClickListener(v -> {
                PaySelectMemberActivity psm = new PaySelectMemberActivity();
                psm.show(getSupportFragmentManager(), "PaySelectMemberActivity");
                psm.setOnClickListener(new PaySelectMemberActivity.OnClickListener() {
                    @Override
                    public void onClick(View v, String user_id, String user_name) {
                        change_member_id = user_id;
                        change_member_name = user_name;
                        if (user_name.equals("전체직원")) {
                            binding.changeMemberTv.setText("전체직원");
                            change_place_id = "";
                            change_place_name = "";
                        } else {
                            binding.changeMemberTv.setText(user_name);
                        }
                        dlog.i("change_member_id : " + user_id);
                        dlog.i("change_member_name : " + user_name);
                        shardpref.putString("change_member_id", user_id);
                        shardpref.putString("change_member_name", user_name);
//                        WritePaymentList(change_place_id.equals("") ? place_id : change_place_id, change_member_id, binding.setdate.getText().toString());
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        setAddBtnSetting();
    }

    CardView add_worktime_btn;
    TextView addbtn_tv;
    private void setAddBtnSetting() {
        add_worktime_btn = findViewById(R.id.add_worktime_btn);
        addbtn_tv = findViewById(R.id.addbtn_tv);
        addbtn_tv.setText("할일추가");
        add_worktime_btn.setOnClickListener(v -> {
            TaskAddOption to = new TaskAddOption();
            to.show(getSupportFragmentManager(),"TaskAddOption");
        });
    }
    public void Toast_Nomal(String message){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_normal_toast, (ViewGroup)findViewById(R.id.toast_layout));
        TextView toast_textview  = layout.findViewById(R.id.toast_textview);
        toast_textview.setText(String.valueOf(message));
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); //TODO 메시지가 표시되는 위치지정 (가운데 표시)
        //toast.setGravity(Gravity.TOP, 0, 0); //TODO 메시지가 표시되는 위치지정 (상단 표시)
        toast.setGravity(Gravity.BOTTOM, 0, 0); //TODO 메시지가 표시되는 위치지정 (하단 표시)
        toast.setDuration(Toast.LENGTH_SHORT); //메시지 표시 시간
        toast.setView(layout);
        toast.show();
    }
}
