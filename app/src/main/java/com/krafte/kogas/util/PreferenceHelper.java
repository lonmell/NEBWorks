package com.krafte.kogas.util;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class PreferenceHelper {
    private final SharedPreferences app_prefs;
    private final Context context;

    public PreferenceHelper(Context context)
    {
        app_prefs = context.getSharedPreferences("shared", 0);
        this.context = context;
    }

    public void putString(String key,String value){
        app_prefs.edit().putString(key,value).apply();
    }
    public void putBoolean(String key,boolean value){
        app_prefs.edit().putBoolean(key,value).apply();
    }
    public void putInt(String key,int value){
        app_prefs.edit().putInt(key,value).apply();
    }

    public String getString(String key,String value){
        return app_prefs.getString(key,value);
    }

    public boolean getBoolean(String key,boolean value){
        return app_prefs.getBoolean(key,value);
    }

    public int getInt(String key,int value){
        return app_prefs.getInt(key,value);
    }


    public void remove(String key){
        app_prefs.edit().remove(key).apply();
    }
    public void clear(){
        app_prefs.edit().clear().apply();
    }


    // HashMap 저장
    public void SaveUrlMap(Context context, HashMap<String, String> hashMapData) {

        if (app_prefs != null) {
            JSONObject jsonObject = new JSONObject(hashMapData);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = app_prefs.edit();
            editor.remove("hashMapName").apply();
            editor.putString("hashMapName", jsonString);
            editor.apply();
        }
    }

    // HashMap 불러오기
    public HashMap<String, String> LoadUrlMap(Context context) {
        HashMap<String, String> outputMap = new HashMap<String, String>();

        try {
            if (app_prefs != null) {
                String jsonString = app_prefs.getString("hashMapName", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);

                Iterator<String> keysItr = jsonObject.keys();
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    String value = (String) jsonObject.get(key);
                    outputMap.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputMap;
    }
}
