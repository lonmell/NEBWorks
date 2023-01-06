package com.krafte.nebworks.util;

import android.content.Context;
import android.content.Intent;

import com.krafte.nebworks.ui.PushActivity;
import com.krafte.nebworks.ui.approval.TaskApprovalDetail;
import com.krafte.nebworks.ui.approval.TaskApprovalFragment;
import com.krafte.nebworks.ui.career.CareerActivity;
import com.krafte.nebworks.ui.community.CommunityAddActivity;
import com.krafte.nebworks.ui.community.CommunityDetailActivity;
import com.krafte.nebworks.ui.community.MoreListCommunityActivity;
import com.krafte.nebworks.ui.community.OwnerFeedDetailActivity;
import com.krafte.nebworks.ui.contract.AddContractPage01;
import com.krafte.nebworks.ui.contract.AddContractPage02;
import com.krafte.nebworks.ui.contract.AddContractPage03;
import com.krafte.nebworks.ui.contract.AddContractPage04;
import com.krafte.nebworks.ui.contract.AddContractPage05;
import com.krafte.nebworks.ui.contract.AddContractPage06;
import com.krafte.nebworks.ui.contract.AddContractPage07;
import com.krafte.nebworks.ui.contract.AddContractPage08;
import com.krafte.nebworks.ui.contract.AddContractPage09;
import com.krafte.nebworks.ui.contract.ContractAllDataActivity;
import com.krafte.nebworks.ui.contract.ContractFragmentActivity;
import com.krafte.nebworks.ui.contract.ContractWorkerAccept;
import com.krafte.nebworks.ui.contract.ContractWorkerSignActivity;
import com.krafte.nebworks.ui.feed.FeedAddActivity;
import com.krafte.nebworks.ui.feed.FeedDetailActivity;
import com.krafte.nebworks.ui.feed.FeedEditActivity;
import com.krafte.nebworks.ui.feed.FeedListActivity;
import com.krafte.nebworks.ui.login.LoginActivity;
import com.krafte.nebworks.ui.main.EmployeeProcess;
import com.krafte.nebworks.ui.main.MainFragment;
import com.krafte.nebworks.ui.main.MainFragment2;
import com.krafte.nebworks.ui.member.AddMemberDetail;
import com.krafte.nebworks.ui.member.AdddirectlyMember;
import com.krafte.nebworks.ui.member.AuthSelectActivity;
import com.krafte.nebworks.ui.member.InviteMemberActivity;
import com.krafte.nebworks.ui.member.MemberDetailActivity;
import com.krafte.nebworks.ui.member.MemberManagement;
import com.krafte.nebworks.ui.notify.NotifyListActivity;
import com.krafte.nebworks.ui.paymanagement.AddPaystubActivity;
import com.krafte.nebworks.ui.paymanagement.PayManagementActivity;
import com.krafte.nebworks.ui.paymanagement.PaystuballActivity;
import com.krafte.nebworks.ui.user.ChangePWActivity;
import com.krafte.nebworks.ui.user.ChangePWActivity2;
import com.krafte.nebworks.ui.user.DeleteUserActivity;
import com.krafte.nebworks.ui.user.FindEmailActivity;
import com.krafte.nebworks.ui.user.JoinActivity;
import com.krafte.nebworks.ui.user.ProfileEditActivity;
import com.krafte.nebworks.ui.user.SearchAccountActivity;
import com.krafte.nebworks.ui.user.VerificationActivity;
import com.krafte.nebworks.ui.worksite.PlaceAddActivity;
import com.krafte.nebworks.ui.worksite.PlaceAddActivity2;
import com.krafte.nebworks.ui.worksite.PlaceAddCompletion;
import com.krafte.nebworks.ui.worksite.PlaceEditActivity;
import com.krafte.nebworks.ui.worksite.PlaceEditActivity2;
import com.krafte.nebworks.ui.worksite.PlaceListActivity;
import com.krafte.nebworks.ui.worksite.PlaceSearchActivity;
import com.krafte.nebworks.ui.worksite.PlaceWorkDetailActivity;
import com.krafte.nebworks.ui.worksite.TaskAddWorkActivity;
import com.krafte.nebworks.ui.worksite.TaskDetailActivity;
import com.krafte.nebworks.ui.worksite.TaskListActivity;
import com.krafte.nebworks.ui.worksite.TaskReportActivity;
import com.krafte.nebworks.ui.worksite.TaskReportDetailActivity;
import com.krafte.nebworks.ui.worksite.TaskReuesAddActivity;
import com.krafte.nebworks.ui.worksite.TaskReuseActivity;
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
    public void EmployeeProcess(Context context) {
        Intent intent = new Intent(context, EmployeeProcess.class);
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
    public void Approval(Context context) {
        Intent intent = new Intent(context, TaskApprovalFragment.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
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
        Intent intent = new Intent(context, TaskAddWorkActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void addWorkGoBack(Context context) {
        Intent intent = new Intent(context, TaskAddWorkActivity.class);
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

    @Override
    public void TaskReuse(Context context) {
        Intent intent = new Intent(context, TaskReuseActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void TaskReuesAdd(Context context) {
        Intent intent = new Intent(context, TaskReuesAddActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void TaskDetail(Context context) {
        Intent intent = new Intent(context, TaskDetailActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void TaskReport(Context context) {
        Intent intent = new Intent(context, TaskReportActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void TaskReportDetail(Context context) {
        Intent intent = new Intent(context, TaskReportDetailActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void TaskList(Context context) {
        Intent intent = new Intent(context, TaskListActivity.class);
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

    //--ui.community
    @Override
    public void CommunityAdd(Context context) {
        Intent intent = new Intent(context, CommunityAddActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void MoreListCommunity(Context context) {
        Intent intent = new Intent(context, MoreListCommunityActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void CommunityDetail(Context context) {
        Intent intent = new Intent(context, CommunityDetailActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void OwnerFeedDetail(Context context) {
        Intent intent = new Intent(context, OwnerFeedDetailActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    //--ui.contract
    @Override
    public void ContractFragment(Context context) {
        Intent intent = new Intent(context, ContractFragmentActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void AddContractPage01(Context context) {
        Intent intent = new Intent(context, AddContractPage01.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void AddContractPage02(Context context) {
        Intent intent = new Intent(context, AddContractPage02.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void AddContractPage03(Context context) {
        Intent intent = new Intent(context, AddContractPage03.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void AddContractPage04(Context context) {
        Intent intent = new Intent(context, AddContractPage04.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void AddContractPage05(Context context) {
        Intent intent = new Intent(context, AddContractPage05.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void AddContractPage06(Context context) {
        Intent intent = new Intent(context, AddContractPage06.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void AddContractPage07(Context context) {
        Intent intent = new Intent(context, AddContractPage07.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void AddContractPage08(Context context) {
        Intent intent = new Intent(context, AddContractPage08.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void AddContractPage09(Context context) {
        Intent intent = new Intent(context, AddContractPage09.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void ContractWorkerAccept(Context context) {
        Intent intent = new Intent(context, ContractWorkerAccept.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void ContractWorkerSign(Context context) {
        Intent intent = new Intent(context, ContractWorkerSignActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    @Override
    public void ContractAll(Context context) {
        Intent intent = new Intent(context, ContractAllDataActivity.class);
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

    @Override
    public void MemberDetail(Context context) {
        Intent intent = new Intent(context, MemberDetailActivity.class);
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

    //.ui.paymanagement
    @Override
    public void PayManagement(Context context) {
        Intent intent = new Intent(context, PayManagementActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void AddPaystubAlba(Context context) {
        Intent intent = new Intent(context, AddPaystubActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void PaystubAll(Context context) {
        Intent intent = new Intent(context, PaystuballActivity.class);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
}
