package com.krafte.nebworks.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.krafte.nebworks.R;
import com.krafte.nebworks.util.Dlog;

public class WebViewActivity  extends Activity {

    private WebView browser;
    Dlog dlog = new Dlog();
    Context mContext;
    class MyJavaScriptInterface
    {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processDATA(String data) {
            Bundle extra = new Bundle();
            Intent intent = new Intent();
            extra.putString("data", data);
            dlog.i("우편번호 : " + data);
            intent.putExtras(extra);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @SuppressLint({"SetTextI18n", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);//캡쳐막기
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_web_view);
        browser = findViewById(R.id.webView);
        mContext = this;
        dlog.DlogContext(mContext);
        browser.setWebViewClient(new WebViewClient());  // 새 창 띄우기 않기
        browser.setWebChromeClient(new WebChromeClient());

//        browser.getSettings().setLoadWithOverviewMode(true);  // WebView 화면크기에 맞추도록 설정 - setUseWideViewPort 와 같이 써야함
//        browser.getSettings().setUseWideViewPort(true);  // wide viewport 설정 - setLoadWithOverviewMode 와 같이 써야함
//
//        browser.getSettings().setSupportZoom(false);  // 줌 설정 여부
//        browser.getSettings().setBuiltInZoomControls(false);  // 줌 확대/축소 버튼 여부

        browser.getSettings().setJavaScriptEnabled(true); // 자바스크립트 사용여부
        browser.addJavascriptInterface(new MyJavaScriptInterface(), "android");
        browser.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); // javascript가 window.open()을 사용할 수 있도록 설정
//        browser.getSettings().setSupportMultipleWindows(true); // 멀티 윈도우 사용 여부
//        browser.getSettings().setDomStorageEnabled(true);  // 로컬 스토리지 (localStorage) 사용여부


        //웹페이지 호출
        browser.addJavascriptInterface(new MyJavaScriptInterface(), "Android");
        browser.setWebViewClient(new SSLTolerentWebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                browser.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });
        browser.loadUrl("https://krafte.net/app_php/daum.html");
    }

    // SSL Error Tolerant Web View Client
    private static class SSLTolerentWebViewClient extends WebViewClient {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.cancel(); // Ignore SSL certificate errors
        }
    }

}
