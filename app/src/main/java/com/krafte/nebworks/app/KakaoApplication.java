package com.krafte.nebworks.app;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;
import com.krafte.nebworks.R;

public class KakaoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        KakaoSdk.init(this, getString(R.string.kakao_native_key));
    }
}
