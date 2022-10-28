package com.krafte.nebworks.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class Dlog {
    static final String TAG = "TedPark";

    Context mContext;
    boolean debug;

    public void DlogContext(Context mContext){
        this.mContext = mContext;
    }
    /**
     * Log Level Error
     **/
    public void e(String message) {
        if (isDebuggable(mContext)) Log.e(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Warning
     **/
    public void w(String message) {
        if (isDebuggable(mContext)) Log.w(TAG, buildLogMsg(message));
    }


    /**
     * Log Level Information
     **/
    public void i(String message) {
        if (isDebuggable(mContext)) Log.i(TAG, buildLogMsg(message));
    }


    /**
     * Log Level Debug
     **/
    public void d(String message) {
        if (isDebuggable(mContext)) Log.d(TAG, buildLogMsg(message));
    }


    /**
     * Log Level Verbose
     **/
    public void v(String message) {
        if (isDebuggable(mContext)) Log.v(TAG, buildLogMsg(message));
    }


    public String buildLogMsg(String message) {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(ste.getFileName().replace(".java", ""));
        sb.append("::");
        sb.append(ste.getMethodName());
        sb.append("]");
        sb.append(message);
        return sb.toString();
    }

    /**
     * 현재 디버그모드여부를 리턴 * * @param context * @return
     */
    public boolean isDebuggable(Context context){
        boolean debuggable=false;

        PackageManager pm=context.getPackageManager();

        try{
            ApplicationInfo appinfo=pm.getApplicationInfo(context.getPackageName(),0);
            debuggable=(0!=(appinfo.flags&ApplicationInfo.FLAG_DEBUGGABLE));
        }catch(PackageManager.NameNotFoundException e){
            /* debuggable variable will remain false */
        }
        return debuggable;
    }
}
