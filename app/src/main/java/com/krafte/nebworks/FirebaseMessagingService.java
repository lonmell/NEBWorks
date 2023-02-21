package com.krafte.nebworks;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.krafte.nebworks.ui.approval.TaskApprovalFragment;
import com.krafte.nebworks.ui.contract.ContractFragmentActivity;
import com.krafte.nebworks.ui.feed.FeedListActivity;
import com.krafte.nebworks.ui.main.MainFragment;
import com.krafte.nebworks.ui.main.MainFragment2;
import com.krafte.nebworks.ui.member.MemberManagement;
import com.krafte.nebworks.ui.paymanagement.PayManagementActivity;
import com.krafte.nebworks.ui.worksite.PlaceListActivity;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RandomOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "FirebaseMsgService";
    RandomOut ro = new RandomOut();

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("jslee314", "Token : " + s);
    }

    Intent intent;
    Intent notificationIntent;
    PendingIntent pendingIntent;
    Context mContext;
    private NotificationManager notificationManager;
    PreferenceHelper shardpref;
    boolean channelId1 = false;
    boolean channelId2 = false;
    boolean channelId3 = false;
    boolean channelId4 = false;

    String Channel = "";
    String message0 = "";
    String message1 = "";
    String USER_INFO_AUTH = "";
    int setId = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        mContext = getApplicationContext();
        shardpref = new PreferenceHelper(mContext);
        intent = new Intent();
        notificationIntent = new Intent();
//        setId      = shardpref.getInt("setId", 0);
        setId      = Integer.parseInt(ro.getRandomNum(2));
        channelId1 = shardpref.getBoolean("channelId1", false);
        channelId2 = shardpref.getBoolean("channelId2", false);
        channelId3 = shardpref.getBoolean("channelId3", false);
        channelId4 = shardpref.getBoolean("channelId4", false);
        USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "0");

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        /*
         * TAG = 1 : 일반 업무 알람
         * TAG = 2 : 커뮤니티 알람
         * */
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            //Background
            Log.d(TAG, "1getData data payload: " + remoteMessage.getData());
            Log.d(TAG, "1getData Notification title: " + remoteMessage.getData().get("title"));
            Log.d(TAG, "1getData Notification message: " + remoteMessage.getData().get("message"));
            Log.d(TAG, "1getData Notification clickAction: " + remoteMessage.getData().get("click_action"));
            Log.d(TAG, "1Message Notification getTag: " + remoteMessage.getData().get("tag"));

            List<String> splitTag = new ArrayList<>(Arrays.asList(String.valueOf(remoteMessage.getData().get("tag")).split(",")));
            if (String.valueOf(remoteMessage.getData().get("tag")).length() != 1 && !String.valueOf(remoteMessage.getData().get("tag")).equals("null")) {
                Log.d(TAG, "splitTag 0 : " + splitTag.get(0));
                Log.d(TAG, "splitTag 1 : " + splitTag.get(1));
                message0 = splitTag.get(0);
                message1 = splitTag.get(1);
                Log.i(TAG, "1message0[0] : " + message0);
                Log.i(TAG, "1message1[1] : " + message1);
                Log.d(TAG, "1getData Notification TAG : " + message0);
                Log.d(TAG, "1getData Notification place_id : " + message1);
                shardpref.putString("place_id",message1);
            }
            Log.d(TAG, "1channelId1: " + channelId1);
            Log.d(TAG, "1channelId2: " + channelId2);
            Log.d(TAG, "1channelId3: " + channelId3);
            Log.d(TAG, "1channelId4: " + channelId4);

            if (message0.equals("1") && channelId1) {
                showNotification(String.valueOf(remoteMessage.getData().get("title"))
                        , String.valueOf(remoteMessage.getData().get("message")), String.valueOf(remoteMessage.getData().get("click_action")));
                sendNotification(String.valueOf(remoteMessage.getData().get("title"))
                        , String.valueOf(remoteMessage.getData().get("message"))
                        , String.valueOf(remoteMessage.getData().get("click_action")));
            } else if (message0.equals("2") && channelId2) {
                showNotification(String.valueOf(remoteMessage.getData().get("title"))
                        , String.valueOf(remoteMessage.getData().get("message")), String.valueOf(remoteMessage.getData().get("click_action")));
                sendNotification(String.valueOf(remoteMessage.getData().get("title"))
                        , String.valueOf(remoteMessage.getData().get("message"))
                        , String.valueOf(remoteMessage.getData().get("click_action")));
            } else if (message0.equals("3") && channelId3) {
                showNotification(String.valueOf(remoteMessage.getData().get("title"))
                        , String.valueOf(remoteMessage.getData().get("message")), String.valueOf(remoteMessage.getData().get("click_action")));
                sendNotification(String.valueOf(remoteMessage.getData().get("title"))
                        , String.valueOf(remoteMessage.getData().get("message"))
                        , String.valueOf(remoteMessage.getData().get("click_action")));
            } else if (message0.equals("4") && channelId4) {
                showNotification(String.valueOf(remoteMessage.getData().get("title"))
                        , String.valueOf(remoteMessage.getData().get("message")), String.valueOf(remoteMessage.getData().get("click_action")));
                sendNotification(String.valueOf(remoteMessage.getData().get("title"))
                        , String.valueOf(remoteMessage.getData().get("message"))
                        , String.valueOf(remoteMessage.getData().get("click_action")));
            } else if (message0.equals("9")) {
                showNotification(String.valueOf(remoteMessage.getData().get("title"))
                        , String.valueOf(remoteMessage.getData().get("message")), String.valueOf(remoteMessage.getData().get("click_action")));
                sendNotification(String.valueOf(remoteMessage.getData().get("title"))
                        , String.valueOf(remoteMessage.getData().get("message"))
                        , String.valueOf(remoteMessage.getData().get("click_action")));
            }

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
            } else {
                // Handle message within 10 seconds
//                handleNow();
            }
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendNotification(String title, String message,String click_action) {
        //ForGround
        Log.i(TAG, "notificationIntent : " + notificationIntent);
        //푸시를 클릭했을때 이동//
        // 0. Pending Intent
        if(click_action.equals("PlaceList0") || click_action.equals("PlaceList1")){
            //점주 매장리스트 //근로자 매장리스트
            notificationIntent = new Intent(this, PlaceListActivity.class);
        }else if(click_action.equals("TaskList0")){
            shardpref.putInt("SELECT_POSITION",1);
            notificationIntent = new Intent(this, MainFragment.class);
        }else if(click_action.equals("TaskList1")){
            shardpref.putInt("SELECT_POSITION",1);
            notificationIntent = new Intent(this, MainFragment2.class);
        }else if(click_action.equals("Member0") || click_action.equals("Member1")){
            notificationIntent = new Intent(this, MemberManagement.class);
        }else if(click_action.equals("contract0") || click_action.equals("contract1")){
            notificationIntent = new Intent(this, ContractFragmentActivity.class);
        }else if(click_action.equals("Payment0") || click_action.equals("Payment1")){
            notificationIntent = new Intent(this, PayManagementActivity.class);
        }else if(click_action.equals("PlaceWorkFragment")){
            notificationIntent = new Intent(this, FeedListActivity.class);
        }else if(click_action.equals("TaskApprovalFragment")){
            notificationIntent = new Intent(this, TaskApprovalFragment.class);
        }else if(click_action.equals("EmployeeProcess")){
            shardpref.putInt("SELECT_POSITION",0);
            notificationIntent = new Intent(this, MainFragment2.class);
        }

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        notificationIntent = new Intent(this, IntroActivity.class);
//        notificationIntent.putExtra("click_action", click_action);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // 1. 알림 메시지를 관리하는 notificationManager 객체 추출
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = getNotificationBuilder(notificationManager, "chennal id", "첫번째 채널입니다");

        builder.setContentTitle("사장님!넵")       // 콘솔에서 설정한 타이틀
                .setContentText(message)         // 콘솔에서 설정한 내용
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)// 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
                .setAutoCancel(true);             // 메시지를 터치하면 메시지가 자동으로 제거됨

        notificationManager.notify(setId, builder.build()); // 고유숫자로 노티피케이션 동작시킴

        shardpref.putInt("setId", setId);
    }

    @SuppressLint({"ObsoleteSdkInt", "LongLogTag"})
    private void showNotification(String title, String message, String click_action) {
        Log.i(TAG, "click_action : " + click_action);
        Log.i(TAG, "intent : " + intent);
        //푸시를 클릭했을때 이동//
        // 0. Pending Intent
        if(click_action.equals("PlaceList0") || click_action.equals("PlaceList1")){
            //점주 매장리스트 //근로자 매장리스트
            intent = new Intent(this, PlaceListActivity.class);
        }else if(click_action.equals("TaskList0")){
            shardpref.putInt("SELECT_POSITION",1);
            intent = new Intent(this, MainFragment.class);
        }else if(click_action.equals("TaskList1")){
            shardpref.putInt("SELECT_POSITION",1);
            intent = new Intent(this, MainFragment2.class);
        }else if(click_action.equals("Member0") || click_action.equals("Member1")){
            intent = new Intent(this, MemberManagement.class);
        }else if(click_action.equals("contract0") || click_action.equals("contract1")){
            intent = new Intent(this, ContractFragmentActivity.class);
        }else if(click_action.equals("Payment0") || click_action.equals("Payment1")){
            intent = new Intent(this, PayManagementActivity.class);
        }else if(click_action.equals("PlaceWorkFragment")){
            intent = new Intent(this, FeedListActivity.class);
        }else if(click_action.equals("TaskApprovalFragment")){
            intent = new Intent(this, TaskApprovalFragment.class);
        }else if(click_action.equals("EmployeeProcess")){
            shardpref.putInt("SELECT_POSITION",0);
            intent = new Intent(this, MainFragment2.class);
        }

//        Intent intent = new Intent(this, IntroActivity.class);
//        intent.putExtra("click_action", click_action);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_MUTABLE);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        Notification mNotification;

        String NOTIFICATION_CHANNEL1 = String.valueOf(R.string.channel_1);
        String NOTIFICATION_CHANNEL2 = String.valueOf(R.string.channel_2);
        String NOTIFICATION_CHANNEL3 = String.valueOf(R.string.channel_3);
        String NOTIFICATION_CHANNEL4 = String.valueOf(R.string.channel_4);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getBaseContext().getSystemService(NotificationManager.class);

            //매장 알림
            NotificationChannel serviceChannel1 = new NotificationChannel(
                    NOTIFICATION_CHANNEL1,
                    String.valueOf(R.string.channel_1),
                    NotificationManager.IMPORTANCE_NONE
            );
            manager.createNotificationChannel(serviceChannel1);

            //결재 알림
            NotificationChannel serviceChannel2 = new NotificationChannel(
                    NOTIFICATION_CHANNEL2,
                    String.valueOf(R.string.channel_2),
                    NotificationManager.IMPORTANCE_NONE
            );
            manager.createNotificationChannel(serviceChannel2);

            //근무시간 알림
            NotificationChannel serviceChannel3 = new NotificationChannel(
                    NOTIFICATION_CHANNEL3,
                    String.valueOf(R.string.channel_3),
                    NotificationManager.IMPORTANCE_NONE
            );
            manager.createNotificationChannel(serviceChannel3);

            //이력서/면접 알림
            NotificationChannel serviceChannel4 = new NotificationChannel(
                    NOTIFICATION_CHANNEL4,
                    String.valueOf(R.string.channel_4),
                    NotificationManager.IMPORTANCE_NONE
            );
            manager.createNotificationChannel(serviceChannel4);
        }
        if (message0.equals("1") && channelId1) {
            Channel = NOTIFICATION_CHANNEL1;
        } else if (message0.equals("2") && channelId2) {
            Channel = NOTIFICATION_CHANNEL2;
        } else if (message0.equals("3") && channelId3) {
            Channel = NOTIFICATION_CHANNEL3;
        } else if (message0.equals("4") && channelId4) {
            Channel = NOTIFICATION_CHANNEL4;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i(TAG,"mNotification SHOW 1");

            mNotification =
                    new Notification.Builder(this,Channel)
                            .setContentTitle("사장님!넵")
                            .setContentText(message)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentIntent(pendingIntent)
                            .build();
            startForeground(1, mNotification);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(setId /* ID of notification */, mNotification);
            shardpref.putInt("setId", setId);
        } else {
            Log.i(TAG,"mNotification SHOW 2");
            mNotification =
                    new NotificationCompat.Builder(this,Channel)
                            .setContentTitle("사장님!넵")
                            .setContentText(message)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent)
                            .build();
            startForeground(1, mNotification);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(setId /* ID of notification */, mNotification);
            shardpref.putInt("setId", setId);
        }

    }

    /**
     * @내용 : 안드로이드 8.0 이상부터 Notification은 채널별로 관리해야만 함
     * (channel별이라함은 Notification을 보낼때마다 막쌓이는것이 아니고 앱별로 그룹지어서 쌓이도록 하는 것 )
     **/
    protected NotificationCompat.Builder getNotificationBuilder(NotificationManager notificationManager, String channelId, CharSequence channelName) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // 2. NotificationChannel채널 객체 생성 (첫번재 인자: 관리id, 두번째 인자: 사용자에게 보여줄 채널 이름)
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);

            // 3. 알림 메시지를 관리하는 객체에 노티피케이션 채널을 등록
            notificationManager.createNotificationChannel(channel);
            builder.setSmallIcon(R.drawable.ic_launcher); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            return builder;

        } else { // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남
            builder.setSmallIcon(R.drawable.ic_launcher);
            return builder;
        }
    }
}
