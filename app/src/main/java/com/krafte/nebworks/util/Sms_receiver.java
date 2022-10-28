package com.krafte.nebworks.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class Sms_receiver extends BroadcastReceiver {
    public static final String TAG = "Sms_receiver";
    public static String receiverNum = "";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            Log.d(TAG, "SmsReceiver : onReceiver");
            switch (status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    Log.d(TAG, "SmsReceiver : onReceiver(CommonStatusCodes.SUCCESS)");
                    Log.d(TAG, "message : " + message);
                    receiverNum = message.substring(message.indexOf("인증번호 [")+6, message.indexOf("]를 입력해주세요"));
                    Log.d(TAG, "receiverNum : " + receiverNum);

                    // 본인은 문자를 받았을 때 EventBus를 통해 처리해 줬다.
                    // 이 부분이 문자메시지를 받은것이니 각자 message를 가공해서 숫자를 뽑아낸 다음 세팅시켜주면 될 듯 하다.
                    // EventBus.getDefault().post(new SmsReceiverEvent(message));
                    break;
                case CommonStatusCodes.TIMEOUT:
                    Log.d(TAG, "SmsReceiver : onReceiver(CommonStatusCodes.TIMEOUT)");
                    break;
            }
        }
    }

}