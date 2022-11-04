package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.ui.feed.FeedEditActivity;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;

import java.text.DecimalFormat;

public class CommunityOptionActivity extends Activity {
    private static final String TAG = "CommunityOptionActivity";
    Context mContext;
    private View view;

    Activity activity;

    //XML ID
    TextView feed_edit, feed_delete, cancel;


    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_NO = "";
    String USER_INFO_ID = "";

    String state = "";
    String board_no = "";
    String click_htn = "";
    String comment_no = "";
    String comment_contents = "";

    //Other
    private final DateCurrent dc = new DateCurrent();
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");
    Dlog dlog = new Dlog();
    Intent intent;

    private String result = "";
    GetResultData resultData = new GetResultData();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_community_option);
        mContext = this;

        dlog.DlogContext(mContext);

        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");


        intent = getIntent();
        state = intent.getStringExtra("state");
        comment_no = intent.getStringExtra("comment_no");
        comment_contents = intent.getStringExtra("comment_contents");

        setContentLayout();
        setBtnEvent();

        if(state.equals("EditComment")){
            feed_edit.setText("댓글 수정");
            feed_delete.setText("댓글 삭제");
        }else if(state.equals("EditFeed")){
            feed_edit.setText("공지 수정");
            feed_delete.setText("공지 삭제");
        }

    }

    private void setContentLayout() {
        feed_edit = findViewById(R.id.feed_edit);
        feed_delete = findViewById(R.id.feed_delete);
        cancel = findViewById(R.id.cancel);
    }

    private void setBtnEvent() {
        feed_edit.setOnClickListener(v -> {
            if(state.equals("EditComment")){
                shardpref.putString("editstate","EditComment");
                shardpref.putString("comment_no",comment_no);
                shardpref.putString("comment_contents",comment_contents);
            }else if(state.equals("EditFeed")){
                dlog.i("edit_feed_id : " + shardpref.getString("edit_feed_id","0"));
            }
            setUpdateWorktodo();
            closePop();
        });
        feed_delete.setOnClickListener(v -> {
            click_htn = "icon_trash";
            Intent intent = new Intent(this, TwoButtonPopActivity.class);
            if(state.equals("EditComment")){
                intent.putExtra("data", "해당 댓글을 삭제하시겠습니까?");
                intent.putExtra("flag", "댓글삭제");
            }else if(state.equals("EditFeed")){
                intent.putExtra("data", "해당 공지사항을 삭제하시겠습니까?");
                intent.putExtra("flag", "공지삭제");
            }
            intent.putExtra("left_btn_txt", "취소");
            intent.putExtra("right_btn_txt", "삭제");
            startActivity(intent);
            overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            closePop();
        });
        cancel.setOnClickListener(v -> {
            closePop();
        });
    }

    private void closePop() {
        finish();
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);
        overridePendingTransition(0, R.anim.translate_down);
    }
//    /*Fragment 콜백함수*/
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
//        if (context instanceof Activity)
//            activity = (Activity) context;
//    }


    //게시글 내용 조회
    private void setUpdateWorktodo(){
        if(state.equals("EditComment")){
            finish();
            Intent intent = new Intent();
            intent.putExtra("result", "Close Popup");
            setResult(RESULT_OK, intent);
            overridePendingTransition(0, R.anim.translate_down);
        }else if(state.equals("EditFeed")){
            Intent intent = new Intent(mContext, FeedEditActivity.class);
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }


    }
}
