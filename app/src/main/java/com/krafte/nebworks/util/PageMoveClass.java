package com.krafte.nebworks.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.krafte.nebworks.R;
import com.krafte.nebworks.ui.member.JoinActivity;
import com.krafte.nebworks.ui.worksite.WorkState2Activity;
import com.krafte.nebworks.ui.PushActivity;
import com.krafte.nebworks.ui.approval.TaskApprovalDetail;
import com.krafte.nebworks.ui.approval.TaskApprovalFragment;
import com.krafte.nebworks.ui.feed.FeedAddActivity;
import com.krafte.nebworks.ui.feed.FeedDetailActivity;
import com.krafte.nebworks.ui.feed.FeedEditActivity;
import com.krafte.nebworks.ui.login.LoginActivity;
import com.krafte.nebworks.ui.main.MainFragment;
import com.krafte.nebworks.ui.member.MemberManagement;
import com.krafte.nebworks.ui.notify.NotifyListActivity;
import com.krafte.nebworks.ui.user.DeleteUserActivity;
import com.krafte.nebworks.ui.user.MyPlaceListActivity;
import com.krafte.nebworks.ui.user.ProfileEditActivity;
import com.krafte.nebworks.ui.user.UserPlaceMapActivity;
import com.krafte.nebworks.ui.worksite.PlaceAddActivity;
import com.krafte.nebworks.ui.worksite.PlaceAddWorkActivity;
import com.krafte.nebworks.ui.worksite.PlaceEditActivity;
import com.krafte.nebworks.ui.worksite.PlaceListActivity;
import com.krafte.nebworks.ui.worksite.PlaceWorkDetailActivity;

public class PageMoveClass implements MovePage {
    PreferenceHelper shardpref;
    /*Activity*/
    @Override
    public void LoginGo(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void LoginBack(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void MainGo(Context context) {
        Intent intent = new Intent(context, MainFragment.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void MainBack(Context context) {
        Intent intent = new Intent(context, MainFragment.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void Push(Context context){
        Intent intent = new Intent(context, PushActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void Join(Context context){
        Intent intent = new Intent(context, JoinActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    //--ui.notify
    @Override
    public void NotifyListGo(Context context) {
        Intent intent = new Intent(context, NotifyListActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void NotifyListBack(Context context) {
        Intent intent = new Intent(context, NotifyListActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    //--ui.approval
    @Override
    public void ApprovalGo(Context context) {
        Intent intent = new Intent(context, TaskApprovalFragment.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void ApprovalBack(Context context) {
        Intent intent = new Intent(context, TaskApprovalFragment.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void ApprovalDetailGo(Context context) {
        Intent intent = new Intent(context, TaskApprovalDetail.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void ApprovalDetailBack(Context context) {
        Intent intent = new Intent(context, TaskApprovalDetail.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }


    //--ui.state
    @Override
    public void WorkStateListGo(Context context) {
        Intent intent = new Intent(context, MainFragment.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void WorkStateListBack(Context context) {
        Intent intent = new Intent(context, MainFragment.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void WorkStateDetailGo(Context context) {
        Intent intent = new Intent(context, WorkState2Activity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void WorkStateDetailBack(Context context) {
        Intent intent = new Intent(context, WorkState2Activity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    //--ui.calendar
    @Override
    public void CalenderGo(Context context) {
        Intent intent = new Intent(context, MainFragment.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void CalenderBack(Context context) {
        Intent intent = new Intent(context, MainFragment.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    //--ui.feed
    @Override
    public void FeedDetailGo(Context context) {
        Intent intent = new Intent(context, FeedDetailActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void FeedDetailBack(Context context) {
        Intent intent = new Intent(context, FeedDetailActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void FeedEditGo(Context context) {
        Intent intent = new Intent(context, FeedEditActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void FeedEditBack(Context context) {
        Intent intent = new Intent(context, FeedEditActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    //--ui.worksite
    @Override
    public void PlaceListGo(Context context) {
        Intent intent = new Intent(context, PlaceListActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void PlaceListBack(Context context) {
        Intent intent = new Intent(context, PlaceListActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void PlaceAddGo(Context context) {
        Intent intent = new Intent(context, PlaceAddActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void PlaceEidtGo(Context context) {
        Intent intent = new Intent(context, PlaceEditActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void PlaceEditGo(Context context) {
        Intent intent = new Intent(context, PlaceEditActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void PlaceWorkGo(Context context) {
        Intent intent = new Intent(context, MainFragment.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void PlaceWorkBack(Context context) {
        Intent intent = new Intent(context, MainFragment.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void addWorkGo(Context context) {
        Intent intent = new Intent(context, PlaceAddWorkActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void addWorkGoBack(Context context) {
        Intent intent = new Intent(context, PlaceAddWorkActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void addNotiGo(Context context) {
        Intent intent = new Intent(context, FeedAddActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void addNotiBack(Context context) {
        Intent intent = new Intent(context, FeedAddActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void workDetailGo(Context context) {
        Intent intent = new Intent(context, PlaceWorkDetailActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void workDetailBack(Context context) {
        Intent intent = new Intent(context, PlaceWorkDetailActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    //--ui.user
    @Override
    public void ProfileEditGo(Context context) {
        Intent intent = new Intent(context, ProfileEditActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void UserPlsceMapGo(Context context) {
        Intent intent = new Intent(context, UserPlaceMapActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void UserPlsceMapBack(Context context) {
        Intent intent = new Intent(context, UserPlaceMapActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void MyPlsceGo(Context context) {
        Intent intent = new Intent(context, MyPlaceListActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void MyPlsceBack(Context context) {
        Intent intent = new Intent(context, MyPlaceListActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void UserDel(Context context) {
        Intent intent = new Intent(context, DeleteUserActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    //--ui.BottomNavi
    @Override
    public void MoreGo(Context context) {
        shardpref = new PreferenceHelper(context);
        shardpref.putInt("SELECT_POSITION",4);
        Intent intent = new Intent(context, MainFragment.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void MoreBack(Context context) {
        shardpref = new PreferenceHelper(context);
        shardpref.putInt("SELECT_POSITION",4);
        Intent intent = new Intent(context, MainFragment.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    //--ui.member
    @Override
    public void MemberGo(Context context) {
        Intent intent = new Intent(context, MemberManagement.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void MemberBack(Context context) {
        Intent intent = new Intent(context, MemberManagement.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

}
