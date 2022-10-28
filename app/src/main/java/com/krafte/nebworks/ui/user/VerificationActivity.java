package com.krafte.nebworks.ui.user;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.adapter.WorkplaceListAdapter;
import com.krafte.nebworks.data.PlaceListData;
import com.krafte.nebworks.databinding.ActivityWorksiteBinding;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.util.ArrayList;

public class VerificationActivity  extends AppCompatActivity {
    private ActivityWorksiteBinding binding;
    Context mContext;

    //Other 클래스
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    Handler handler = new Handler();
    RetrofitConnect rc = new RetrofitConnect();
    PageMoveClass pm = new PageMoveClass();

    //Other 변수
    ArrayList<PlaceListData.PlaceListData_list> mList;
    WorkplaceListAdapter mAdapter = null;
    int listitemsize = 0;
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String USER_INFO_EMAIL = "";

    //사용자 정보 체크
    String id = "";
    String name = "";
    String email = "";
    String employee_no = "";
    String department = "";
    String jikchk = "";
    String img_path = "";

    String return_page = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityWorksiteBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        try {
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "");
            USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");

        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
