package com.krafte.nebworks.util;

import android.content.Context;
import android.content.Intent;

import com.krafte.nebworks.ui.PushActivity;
import com.krafte.nebworks.ui.approval.TaskApprovalDetail;
import com.krafte.nebworks.ui.approval.TaskApprovalFragment;
import com.krafte.nebworks.ui.career.CareerActivity;
import com.krafte.nebworks.ui.feed.FeedAddActivity;
import com.krafte.nebworks.ui.feed.FeedDetailActivity;
import com.krafte.nebworks.ui.feed.FeedEditActivity;
import com.krafte.nebworks.ui.feed.FeedListActivity;
import com.krafte.nebworks.ui.login.LoginActivity;
import com.krafte.nebworks.ui.main.MainFragment;
import com.krafte.nebworks.ui.main.MainFragment2;
import com.krafte.nebworks.ui.member.AddMemberDetail;
import com.krafte.nebworks.ui.member.AdddirectlyMember;
import com.krafte.nebworks.ui.member.AuthSelectActivity;
import com.krafte.nebworks.ui.member.InviteMemberActivity;
import com.krafte.nebworks.ui.member.MemberManagement;
import com.krafte.nebworks.ui.notify.NotifyListActivity;
import com.krafte.nebworks.ui.user.ChangePWActivity;
import com.krafte.nebworks.ui.user.ChangePWActivity2;
import com.krafte.nebworks.ui.user.DeleteUserActivity;
import com.krafte.nebworks.ui.user.FindEmailActivity;
import com.krafte.nebworks.ui.user.JoinActivity;
import com.krafte.nebworks.ui.user.ProfileEditActivity;
import com.krafte.nebworks.ui.user.SearchAccountActivity;
import com.krafte.nebworks.ui.user.UserPlaceMapActivity;
import com.krafte.nebworks.ui.user.VerificationActivity;
import com.krafte.nebworks.ui.worksite.PlaceAddActivity;
import com.krafte.nebworks.ui.worksite.PlaceAddActivity2;
import com.krafte.nebworks.ui.worksite.PlaceAddCompletion;
import com.krafte.nebworks.ui.worksite.PlaceAddWorkActivity;
import com.krafte.nebworks.ui.worksite.PlaceEditActivity;
import com.krafte.nebworks.ui.worksite.PlaceEditActivity2;
import com.krafte.nebworks.ui.worksite.PlaceListActivity;
import com.krafte.nebworks.ui.worksite.PlaceSearchActivity;
import com.krafte.nebworks.ui.worksite.PlaceWorkDetailActivity;
import com.krafte.nebworks.ui.worksite.WorkState2Activity;
import com.krafte.nebworks.ui.workstatus.AddWorkPartActivity;

public class PageMoveClass implements MovePage {
    PreferenceHelper shardpref;
    /*Activity*/
    @Override
    public void Login(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void Main(Context context) {
        Intent intent = new Intent(context, MainFragment.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void Main2(Context context) {
        Intent intent = new Intent(context, MainFragment2.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }



    @Override
    public void Push(Context context){
        Intent intent = new Intent(context, PushActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void JoinBefore(Context context){
        Intent intent = new Intent(context, VerificationActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void Join(Context context){
        Intent intent = new Intent(context, JoinActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    //--ui.notify
    @Override
    public void NotifyList(Context context) {
        Intent intent = new Intent(context, NotifyListActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void FeedList(Context context) {
        Intent intent = new Intent(context, FeedListActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }


    //--ui.approval
    @Override
    public void ApprovalGo(Context context) {
        Intent intent = new Intent(context, TaskApprovalFragment.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void ApprovalBack(Context context) {
        Intent intent = new Intent(context, TaskApprovalFragment.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void ApprovalDetailGo(Context context) {
        Intent intent = new Intent(context, TaskApprovalDetail.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void ApprovalDetailBack(Context context) {
        Intent intent = new Intent(context, TaskApprovalDetail.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }


    //--ui.state
    @Override
    public void WorkStateListGo(Context context) {
        Intent intent = new Intent(context, MainFragment.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void WorkStateListBack(Context context) {
        Intent intent = new Intent(context, MainFragment.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void WorkStateDetailGo(Context context) {
        Intent intent = new Intent(context, WorkState2Activity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void WorkStateDetailBack(Context context) {
        Intent intent = new Intent(context, WorkState2Activity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    //--ui.calendar
    @Override
    public void CalenderGo(Context context) {
        Intent intent = new Intent(context, MainFragment.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void CalenderBack(Context context) {
        Intent intent = new Intent(context, MainFragment.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    //--ui.feed
    @Override
    public void FeedDetailGo(Context context) {
        Intent intent = new Intent(context, FeedDetailActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void FeedDetailBack(Context context) {
        Intent intent = new Intent(context, FeedDetailActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void FeedEditGo(Context context) {
        Intent intent = new Intent(context, FeedEditActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void FeedEditBack(Context context) {
        Intent intent = new Intent(context, FeedEditActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    //--ui.worksite
    @Override
    public void PlaceList(Context context) {
        Intent intent = new Intent(context, PlaceListActivity.class);
        context.startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }


    @Override
    public void PlaceAddGo(Context context) {
        Intent intent = new Intent(context, PlaceAddActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void PlaceAdd2Go(Context context) {
        Intent intent = new Intent(context, PlaceAddActivity2.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void PlaceEdit2Go(Context context) {
        Intent intent = new Intent(context, PlaceEditActivity2.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void PlaceEditGo(Context context) {
        Intent intent = new Intent(context, PlaceEditActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void PlaceWorkGo(Context context) {
        Intent intent = new Intent(context, MainFragment.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void PlaceWorkBack(Context context) {
        Intent intent = new Intent(context, MainFragment.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void addWorkGo(Context context) {
        Intent intent = new Intent(context, PlaceAddWorkActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void addWorkGoBack(Context context) {
        Intent intent = new Intent(context, PlaceAddWorkActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void addNotiGo(Context context) {
        Intent intent = new Intent(context, FeedAddActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void addNotiBack(Context context) {
        Intent intent = new Intent(context, FeedAddActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void workDetailGo(Context context) {
        Intent intent = new Intent(context, PlaceWorkDetailActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void workDetailBack(Context context) {
        Intent intent = new Intent(context, PlaceWorkDetailActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void workCompletion(Context context) {
        Intent intent = new Intent(context, PlaceAddCompletion.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }


    //--ui.user
    @Override
    public void SearchEmail(Context context) {
        Intent intent = new Intent(context, SearchAccountActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void FindEmail(Context context) {
        Intent intent = new Intent(context, FindEmailActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void ChangePw(Context context) {
        Intent intent = new Intent(context, ChangePWActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void ChangePW2(Context context) {
        Intent intent = new Intent(context, ChangePWActivity2.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void ProfileEdit(Context context) {
        Intent intent = new Intent(context, ProfileEditActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void PlaceSearch(Context context) {
        Intent intent = new Intent(context, PlaceSearchActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void UserPlsceMapGo(Context context) {
        Intent intent = new Intent(context, UserPlaceMapActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void UserPlsceMapBack(Context context) {
        Intent intent = new Intent(context, UserPlaceMapActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void UserDel(Context context) {
        Intent intent = new Intent(context, DeleteUserActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    //--ui.career
    @Override
    public void Career(Context context) {
        Intent intent = new Intent(context, CareerActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    //--ui.BottomNavi
    @Override
    public void MoreGo(Context context) {
        shardpref = new PreferenceHelper(context);
        shardpref.putInt("SELECT_POSITION",4);
        Intent intent = new Intent(context, MainFragment.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void MoreBack(Context context) {
        shardpref = new PreferenceHelper(context);
        shardpref.putInt("SELECT_POSITION",4);
        Intent intent = new Intent(context, MainFragment.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    //--ui.member
    @Override
    public void MemberGo(Context context) {
        Intent intent = new Intent(context, MemberManagement.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void AuthSelect(Context context) {
        Intent intent = new Intent(context, AuthSelectActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void DirectAddMember(Context context) {
        Intent intent = new Intent(context, AdddirectlyMember.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void AddMemberDetail(Context context) {
        Intent intent = new Intent(context, AddMemberDetail.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void InviteMember(Context context) {
        Intent intent = new Intent(context, InviteMemberActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void MemberManagement(Context context) {
        Intent intent = new Intent(context, MemberManagement.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    //.ui.workstatus
    @Override
    public void AddWorkPart(Context context) {
        Intent intent = new Intent(context, AddWorkPartActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }


}
