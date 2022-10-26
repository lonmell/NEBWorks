package com.krafte.kogas.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.krafte.kogas.ui.login.LoginActivity;

public interface MovePage {

    /*Activity*/

    /*Activity*/
    void LoginGo(Context context);

    /*Activity*/
    void LoginBack(Context context);

    void MainGo(Context context);

    void MyPlsceGo(Context context);

    void MyPlsceBack(Context context);

    void UserDel(Context context);

    void MoreGo(Context context);

    void MoreBack(Context context);

    void MainBack(Context context);

    void Push(Context context);

    //--ui.notify
    void NotifyListGo(Context context);

    void NotifyListBack(Context context);

    //--ui.approval
    void ApprovalGo(Context context);

    void ApprovalBack(Context context);

    void ApprovalDetailGo(Context context);

    void ApprovalDetailBack(Context context);

    //--ui.state
    void WorkStateListGo(Context context);

    void WorkStateListBack(Context context);

    void WorkStateDetailGo(Context context);

    void WorkStateDetailBack(Context context);

    //--ui.calendar
    void CalenderGo(Context context);

    void CalenderBack(Context context);

    //--ui.feed
    void FeedDetailGo(Context context);

    void FeedDetailBack(Context context);

    void FeedEditGo(Context context);

    void FeedEditBack(Context context);

    void PlaceListGo(Context context);

    void PlaceListBack(Context context);

    void PlaceAddGo(Context context);

    void PlaceEidtGo(Context context);

    void PlaceEditGo(Context context);

    void PlaceWorkGo(Context context);

    void PlaceWorkBack(Context context);

    void addWorkGo(Context context);

    void addWorkGoBack(Context context);

    void addNotiGo(Context context);

    void addNotiBack(Context context);

    void workDetailGo(Context context);

    void workDetailBack(Context context);

    void ProfileEditGo(Context context);

    void UserPlsceMapGo(Context context);

    void UserPlsceMapBack(Context context);

    //--ui.member
    void MemberGo(Context context);

    void MemberBack(Context context);
}
