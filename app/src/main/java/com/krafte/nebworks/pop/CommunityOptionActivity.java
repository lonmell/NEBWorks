package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.databinding.ActivityCommunityOptionBinding;
import com.krafte.nebworks.ui.community.CommunityAddActivity;
import com.krafte.nebworks.ui.feed.FeedEditActivity;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;

public class CommunityOptionActivity extends Activity {
    private ActivityCommunityOptionBinding binding;
    private static final String TAG = "CommunityOptionActivity";
    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";

    String title = "";
    String state = "";
    String click_htn = "";
    String comment_id = "";
    String comment_contents = "";

    //Other
    Dlog dlog = new Dlog();
    Intent intent;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_community_option);
        binding = ActivityCommunityOptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = UserCheckData.getInstance().getUser_id();

        intent = getIntent();
        state = intent.getStringExtra("state");
        comment_id = intent.getStringExtra("comment_id");
        comment_contents = intent.getStringExtra("comment_contents");
        title = intent.getStringExtra("title");

        setBtnEvent();

        if(state.equals("EditComment")){
            binding.feedEdit.setText("댓글 수정");
            binding.feedDelete.setText("댓글 삭제");
        }else if(state.equals("EditFeed")){
            binding.feedEdit.setText("공지 수정");
            binding.feedDelete.setText("공지 삭제");
        }else if(state.equals("EditCommunity")){
            binding.feedEdit.setText("게시글 수정");
            binding.feedDelete.setText("게시글 삭제");
        }

    }

    private void setBtnEvent() {
        binding.feedEdit.setOnClickListener(v -> {
            if(state.equals("EditComment")){
                shardpref.putString("editstate","EditComment");
                shardpref.putString("comment_title",title);
                shardpref.putString("comment_id",comment_id);
                shardpref.putString("comment_contents",comment_contents);
            }else if(state.equals("EditFeed")){
                dlog.i("edit_feed_id : " + shardpref.getString("edit_feed_id","0"));
            }else if(state.equals("EditCommunity")){
                dlog.i("edit_feed_id : " + shardpref.getString("edit_feed_id","0"));
            }
            setUpdateWorktodo();
            closePop();
        });
        binding.feedDelete.setOnClickListener(v -> {
            click_htn = "icon_trash";
            Intent intent = new Intent(this, TwoButtonPopActivity.class);
            if(state.equals("EditComment")){
                intent.putExtra("data", "해당 댓글을 삭제하시겠습니까?");
                intent.putExtra("flag", "댓글삭제");
            }else if(state.equals("EditFeed")){
                intent.putExtra("data", "해당 공지사항을 삭제하시겠습니까?");
                intent.putExtra("flag", "공지삭제");
            }else if(state.equals("EditCommunity")){
                intent.putExtra("data", "해당 게시글을 삭제하시겠습니까?");
                intent.putExtra("flag", "게시글삭제");
            }else if(state.equals("EditComment2")){
                intent.putExtra("data", "해당 게시글을 삭제하시겠습니까?");
                intent.putExtra("flag", "게시글삭제2");
            }
            intent.putExtra("left_btn_txt", "취소");
            intent.putExtra("right_btn_txt", "삭제");
            startActivity(intent);
            overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            closePop();
        });
        binding.cancel.setOnClickListener(v -> {
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
        }else if(state.equals("EditCommunity")){
            Intent intent = new Intent(mContext, CommunityAddActivity.class);
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
    }

}
