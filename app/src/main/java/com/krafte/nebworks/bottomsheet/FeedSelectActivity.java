package com.krafte.nebworks.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.FeedSelectAdapter;
import com.krafte.nebworks.data.CategorySpinner;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class FeedSelectActivity extends BottomSheetDialogFragment {
    private static final String TAG = "FeedSelectActivity";
    Context mContext;
    private View view;
    private BottomSheetListener mListener;
    Activity activity;

    private final DateCurrent dc = new DateCurrent();
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private String result = "";

    //XML
//    RecyclerView category_list;
//    TextView close_btn;
    TextView feed_tx01,feed_tx02,feed_tx03,feed_tx04,feed_tx05,feed_tx06,feed_tx07,feed_tx08,feed_tx09,feed_tx10;
    TextView select_kind01,select_kind02,select_kind03;

    //CheckData Param

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String USER_INFO_AUTH = "";
    String state = "";

    int SELECTED_POSITION = 0;
    String store_no;
    boolean wifi_certi_flag = false;
    boolean gps_certi_flag = false;


    //Other
    GetResultData resultData = new GetResultData();
    ArrayList<CategorySpinner.CategorySpinner_list> mList;
    FeedSelectAdapter mAdapter = null;
    Handler mHandler = new Handler(Looper.getMainLooper());
    //    WorkCommunityWriteAcitivy ww = new WorkCommunityWriteAcitivy();
    Dlog dlog = new Dlog();
    //상단 피드텍스트에 들어갈 String Value
    String FeedKind = "";
    String FeedTag = "";

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_select_category, container, false);
        setCancelable(false);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
        mContext = inflater.getContext();

        dlog.DlogContext(mContext);

        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
        USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH","");
        SELECTED_POSITION = shardpref.getInt("SELECTED_POSITION", 0);
        wifi_certi_flag = shardpref.getBoolean("wifi_certi_flag", false);
        gps_certi_flag = shardpref.getBoolean("gps_certi_flag", false);
        store_no = shardpref.getString("store_no", "");

        setContentsLayout(view);
        setBtnEvent();

        mListener = (FeedSelectActivity.BottomSheetListener) getContext();
        Log.i(TAG,"(FeedSelectActivity)USER_INFO_AUTH : " + USER_INFO_AUTH);
        Log.i(TAG,"(FeedSelectActivity)SELECTED_POSITION : " + SELECTED_POSITION);

//        category_list = view.findViewById(R.id.category_list);
//        close_btn = view.findViewById(R.id.close_btn);

//        mHandler = new Handler(Looper.getMainLooper());
//        mHandler.postDelayed(() -> {
//            setCategoryList(USER_INFO_AUTH);
//        }, 0);
//
//        close_btn.setOnClickListener(v -> {
//            dismiss();
//        });
        return view;
    }

    public interface BottomSheetListener {
        void onButtonClicked(String boardkind,String category);
    }

    private boolean CheckDATA() {

        return false;
    }

    private void setContentsLayout(View view){
        feed_tx01 = view.findViewById(R.id.feed_tx01);
        feed_tx02 = view.findViewById(R.id.feed_tx02);
        feed_tx03 = view.findViewById(R.id.feed_tx03);
        feed_tx04 = view.findViewById(R.id.feed_tx04);
        feed_tx05 = view.findViewById(R.id.feed_tx05);
        feed_tx06 = view.findViewById(R.id.feed_tx06);
        feed_tx07 = view.findViewById(R.id.feed_tx07);
        feed_tx08 = view.findViewById(R.id.feed_tx08);
        feed_tx09 = view.findViewById(R.id.feed_tx09);
        feed_tx10 = view.findViewById(R.id.feed_tx10);

        select_kind01 = view.findViewById(R.id.select_kind01);
        select_kind02 = view.findViewById(R.id.select_kind02);
        select_kind03 = view.findViewById(R.id.select_kind03);
    }

    private void setBtnEvent(){
        feed_tx01.setOnClickListener(v -> {
            setFeedChange(1);
        });
        feed_tx02.setOnClickListener(v -> {
            setFeedChange(2);
        });
        feed_tx03.setOnClickListener(v -> {
            setFeedChange(3);
        });
        feed_tx04.setOnClickListener(v -> {
            setFeedChange(4);
        });
        feed_tx05.setOnClickListener(v -> {
            setFeedChange(5);
        });
        feed_tx06.setOnClickListener(v -> {
            setFeedChange(6);
        });
        feed_tx07.setOnClickListener(v -> {
            setFeedChange(7);
        });
        feed_tx08.setOnClickListener(v -> {
            setFeedChange(8);
        });
        feed_tx09.setOnClickListener(v -> {
            setFeedChange(9);
        });
        feed_tx10.setOnClickListener(v -> {
            setFeedChange(10);
        });
        select_kind01.setOnClickListener(v -> {
            setFeedKind(1);
        });
        select_kind02.setOnClickListener(v -> {
            setFeedKind(2);
        });
        select_kind03.setOnClickListener(v -> {
            setFeedKind(3);
        });
    }

    private void setFeedChange(int pos){
        feed_tx01.setTextColor(Color.parseColor("#000000"));
        feed_tx02.setTextColor(Color.parseColor("#000000"));
        feed_tx03.setTextColor(Color.parseColor("#000000"));
        feed_tx04.setTextColor(Color.parseColor("#000000"));
        feed_tx05.setTextColor(Color.parseColor("#000000"));
        feed_tx06.setTextColor(Color.parseColor("#000000"));
        feed_tx07.setTextColor(Color.parseColor("#000000"));
        feed_tx08.setTextColor(Color.parseColor("#000000"));
        feed_tx09.setTextColor(Color.parseColor("#000000"));
        feed_tx10.setTextColor(Color.parseColor("#000000"));
        if(pos == 1){
            FeedTag = "정보에요";
            feed_tx01.setTextColor(Color.parseColor("#1483FE"));
        }else if(pos == 2){
            FeedTag = "화나요";
            feed_tx02.setTextColor(Color.parseColor("#1483FE"));
        }else if(pos == 3){
            FeedTag = "억울해요";
            feed_tx03.setTextColor(Color.parseColor("#1483FE"));
        }else if(pos == 4){
            FeedTag = "자랑해요";
            feed_tx04.setTextColor(Color.parseColor("#1483FE"));
        }else if(pos == 5){
            FeedTag = "점주가 말한다";
            feed_tx05.setTextColor(Color.parseColor("#1483FE"));
        }else if(pos == 6){
            FeedTag = "알바가 말한다";
            feed_tx06.setTextColor(Color.parseColor("#1483FE"));
        }else if(pos == 7){
            FeedTag = "구직(알바 구합니다)";
            feed_tx07.setTextColor(Color.parseColor("#1483FE"));
        }else if(pos == 8){
            FeedTag = "구인(알바 구합니다)";
            feed_tx08.setTextColor(Color.parseColor("#1483FE"));
        }else if(pos == 9){
            FeedTag = "이런 사람 조심하세요";
            feed_tx09.setTextColor(Color.parseColor("#1483FE"));
        }else if(pos == 10){
            FeedTag = "이런 상황 조심하세요";
            feed_tx10.setTextColor(Color.parseColor("#1483FE"));
        }
    }

    private void setFeedKind(int pos){
        if(DataCheck()){
            String TopFeed = "";
            if(pos == 1){
                FeedKind = "자유게시판";
                TopFeed = FeedKind + " " + FeedTag;
                dlog.i("TopFeed : " + TopFeed);
                mListener.onButtonClicked(FeedKind,FeedTag);
                dismiss();
            }else if(pos == 2){
                FeedKind = "사장님페이지";
                TopFeed = FeedKind + " " + FeedTag;
                dlog.i("TopFeed : " + TopFeed);
                mListener.onButtonClicked(FeedKind,FeedTag);
                dismiss();
            }else if(pos == 3){
                FeedKind = "세금/노무";
                TopFeed = FeedKind + " " + FeedTag;
                dlog.i("TopFeed : " + TopFeed);
                mListener.onButtonClicked(FeedKind,FeedTag);
                dismiss();
            }
        }else{
            Toast.makeText(mContext,"게시판 주제(태그)를 선택해주세요.",Toast.LENGTH_SHORT).show();
        }
    }
    private boolean DataCheck(){
//        if(FeedTag.isEmpty()){
//            return false;
//        }else{
        return true;
//        }
    }
    /*Fragment 콜백함수*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }


}
