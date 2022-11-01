package com.krafte.nebworks.util;

import android.util.Base64;
import android.util.Log;

import com.krafte.nebworks.data.CertiNumData;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.LastVersion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DBConnection {
    private static final String TAG = "DBConnection";
    private final String URL = "http://krafte.net";
    private final String URL_SMS = "http://devlon.site";

    GetResultData resultData = new GetResultData();

    /*버전구분*/
    public LastVersion lastVersion = new LastVersion();
    CertiNumData certiNumData = new CertiNumData();

    public static String getBase64decode(String content) {
        return new String(Base64.decode(content, 0)); //TODO Base64 암호화된 문자열을 >> 복호화된 원본 문자열로 반환
    }

    //FCM 테스트용
    public void FcmTestFunction(String topic, String title ,String message, String token, String click_action, String tag, String place_id) {
        // HttpUrlConnection

        // Log.i(TAG, "WorkPlaceEmployeeNotifyUpdate Starting...");
        try {
            String page = URL + "/NEBWorks/kogas_fcmsend.php";
            // URL 객체 생성
            java.net.URL url = new URL(page);
            // 연결 객체 생성
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Post 파라미터
            String params = "topic=" + topic + "&title=" + title + "&message=" + message + "&token=" + token + "&click_action=" + click_action + "&tag=" + tag + "&place_id=" + place_id;
            Log.i(TAG, "POST URL = " + page + "?" + params);


            // 결과값 저장 문자열
            final StringBuilder sb = new StringBuilder();
            // 연결되면
            if (conn != null) {
                // // Log.i(TAG, "conn 연결");
                // 응답 타임아웃 설정
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(10000);
                // POST 요청방식
                conn.setRequestMethod("POST");
                // 포스트 파라미터 전달
                conn.getOutputStream().write(params.getBytes(StandardCharsets.UTF_8));

                // url에 접속 성공하면 (200)
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // // // Log.i(TAG, "conn.getResponseCode() = " + conn.getResponseCode());
                    // 결과 값 읽어오는 부분
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            conn.getInputStream(), StandardCharsets.UTF_8
                    ));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    // 버퍼리더 종료
                    br.close();
                    // Log.i(TAG, "결과 문자열 :" + sb.toString());
                    resultData.setRESULT(sb.toString().replaceAll("\"", ""));

                }
                // 연결 끊기
                conn.disconnect();
                // // Log.i(TAG, "conn 연결 끊기");
            }

        } catch (Exception e) {
            // Log.i(TAG, "error :" + e);
        }
        // Log.i(TAG, "NoticePage_NoticeConfirm Over...");
    }

    public void GetLastAPPVersionCode(String platform) {
        // HttpUrlConnection

        // Log.i(TAG, "GetLastAPPVersionCode Starting...");
        try {
//            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            String page = URL + "/NEBWorks/getlast_version.php";


            // URL 객체 생성
            URL url = new URL(page);
            // 연결 객체 생성
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Post 파라미터
            String params = "platform=" + platform;
            Log.i(TAG, "POST URL = " + page + "?" + params);
            // Log.i(TAG, "POST URL : " + page + "?" + params);

            // 결과값 저장 문자열
            final StringBuilder sb = new StringBuilder();
            // 연결되면
            if (conn != null) {
                // // Log.i(TAG, "conn 연결");
                // 응답 타임아웃 설정
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(10000);
                // POST 요청방식
                conn.setRequestMethod("POST");
                // 포스트 파라미터 전달
                conn.getOutputStream().write(params.getBytes(StandardCharsets.UTF_8));

                // url에 접속 성공하면 (200)HttpsURLConnection.HTTP_OK
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // // // Log.i(TAG, "conn.getResponseCode() = " + conn.getResponseCode());
                    // 결과 값 읽어오는 부분
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            conn.getInputStream(), StandardCharsets.UTF_8
                    ));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    // 버퍼리더 종료
                    br.close();
//                    // Log.i(TAG, "(LoginPage_CheckAccount)sb.toString() : " + sb.toString());

                    // 응답 Json 타입일 경우
                    JSONArray jsonResponse = new JSONArray(sb.toString());
//                    JSONArray jsonResponse = new JSONArray(getBase64decode(sb.toString()));

                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonObject = jsonResponse.getJSONObject(i);
                        String code = jsonObject.getString("version_code");
                        lastVersion.setLast_version(code);
                        // Log.i(TAG,"code : " + lastVersion.getLast_version());
                    }
                }
                // 연결 끊기
                conn.disconnect();
                // // Log.i(TAG, "conn 연결 끊기");
            }
        } catch (Exception e) {
            // Log.i(TAG, "error :" + e);
        }
        // Log.i(TAG, "GetLastAPPVersionCode Over...");
    }

    //JoinActivity 인증번호 저장 (2022-02-15)
    public void ConfrimNumSave(String USER_ID, String CERTI_NUM, String flag) {
        // HttpUrlConnection
        // Log.i(TAG, "ConfrimNumSave Starting...");
        try {
//            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            String page = URL + "/app_php/mobile_usercerti_num.php";
            // URL 객체 생성
            URL url = new URL(page);
            // 연결 객체 생성
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Post 파라미터
            String params = "user_id=" + USER_ID + "&certi_num=" + CERTI_NUM + "&flag=" + flag;
            // Log.i(TAG, page + "?" + params);

            // 결과값 저장 문자열
            final StringBuilder sb = new StringBuilder();
            // 연결되면
            if (conn != null) {
                // // Log.i(TAG, "conn 연결");
                // 응답 타임아웃 설정
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(10000);
                // POST 요청방식
                conn.setRequestMethod("POST");
                // 포스트 파라미터 전달
                conn.getOutputStream().write(params.getBytes(StandardCharsets.UTF_8));

                // url에 접속 성공하면 (200)
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // // // Log.i(TAG, "conn.getResponseCode() = " + conn.getResponseCode());
                    // 결과 값 읽어오는 부분
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            conn.getInputStream(), StandardCharsets.UTF_8
                    ));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    // 버퍼리더 종료
                    br.close();
                    // Log.i(TAG, "(ConfrimNumSave)sb.toString() : " + getBase64decode(sb.toString()));

                    int idx = sb.toString().indexOf("getMessage=");
                    String getMessage = getBase64decode(sb.substring(idx + 1).replaceAll("\"", ""));
                    resultData.setRESULT(getMessage.replaceAll("\"", ""));
                    // Log.i(TAG, "getMessage=" + getMessage.replaceAll("\"", ""));

                }
                // 연결 끊기
                conn.disconnect();
                // // Log.i(TAG, "conn 연결 끊기");
            }

        } catch (Exception e) {
            // Log.i(TAG, "error :" + e);
        }
        // Log.i(TAG, "ConfrimNumSave End...");
    }

    //JoinActivity 인증번호 발송 (2022-01-20)
    public void ConfrimNumSend(String SND_PHONE, String SND_NAME, String SND_NUM, String HASH) {
        // HttpUrlConnection

        // Log.i(TAG, "ConfrimNumSend Starting...");
        try {
//            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
//            String page = URL_SMS + "/SendSMS.php";
            String page = URL + "/mobile/SendSMS_neb.php";
            // URL 객체 생성
            URL url = new URL(page);
            // 연결 객체 생성
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Post 파라미터
            String params = "rcv=" + SND_PHONE + "&rcvnm=" + SND_NAME + "&msg=" + SND_NUM + "&hash=" + HASH;
            Log.i(TAG, page + "?" + params);

            // 결과값 저장 문자열
            final StringBuilder sb = new StringBuilder();
            // 연결되면
            if (conn != null) {
                // // Log.i(TAG, "conn 연결");
                // 응답 타임아웃 설정
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(10000);
                // POST 요청방식
                conn.setRequestMethod("POST");
                // 포스트 파라미터 전달
                conn.getOutputStream().write(params.getBytes(StandardCharsets.UTF_8));

                // url에 접속 성공하면 (200)
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // // // Log.i(TAG, "conn.getResponseCode() = " + conn.getResponseCode());
                    // 결과 값 읽어오는 부분
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            conn.getInputStream(), StandardCharsets.UTF_8
                    ));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    // 버퍼리더 종료
                    br.close();
//                    // Log.i(TAG, "(JoinPage_InsertAccount)sb.toString() : " + getBase64decode(sb.toString()));
//
//                    int idx = sb.toString().indexOf("getMessage=");
//                    String getMessage = getBase64decode(sb.substring(idx + 1).replaceAll("\"", ""));
//                    resultData.setRESULT(getMessage.replaceAll("\"", ""));
//                    // Log.i(TAG, "getMessage=" + getMessage.replaceAll("\"", ""));

                }
                // 연결 끊기
                conn.disconnect();
                // // Log.i(TAG, "conn 연결 끊기");
            }

        } catch (Exception e) {
            // Log.i(TAG, "error :" + e);
        }
        // Log.i(TAG, "ConfrimNumSend End...");
    }

    //JoinActivity 인증번호 조회 (2022-02-15)
    public void ConfrimNumSelect(String USER_ID, String CERTI_NUM, String flag) {
        // HttpUrlConnection
        // Log.i(TAG, "ConfrimNumSelect Starting...");
        try {
//            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            String page = URL + "/app_php/mobile_usercerti_num.php";
            // URL 객체 생성
            URL url = new URL(page);
            // 연결 객체 생성
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Post 파라미터
            String params = "user_id=" + USER_ID + "&certi_num=" + CERTI_NUM + "&flag=" + flag;
            // Log.i(TAG, page + "?" + params);

            // 결과값 저장 문자열
            final StringBuilder sb = new StringBuilder();
            // 연결되면
            if (conn != null) {
                // // Log.i(TAG, "conn 연결");
                // 응답 타임아웃 설정
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(10000);
                // POST 요청방식
                conn.setRequestMethod("POST");
                // 포스트 파라미터 전달
                conn.getOutputStream().write(params.getBytes(StandardCharsets.UTF_8));

                // url에 접속 성공하면 (200)
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // // // Log.i(TAG, "conn.getResponseCode() = " + conn.getResponseCode());
                    // 결과 값 읽어오는 부분
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            conn.getInputStream(), StandardCharsets.UTF_8
                    ));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    // 버퍼리더 종료
                    br.close();
                    // 응답 Json 타입일 경우
                    JSONArray jsonResponse = new JSONArray(sb.toString());
//                    JSONArray jsonResponse = new JSONArray(getBase64decode(sb.toString()));

                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonObject = jsonResponse.getJSONObject(i);
                        String code = jsonObject.getString("certi_num");
                        certiNumData.setCerti_num(code);
                        Log.i(TAG,"code : " + certiNumData.getCerti_num());
                    }

                }
                // 연결 끊기
                conn.disconnect();
                // // Log.i(TAG, "conn 연결 끊기");
            }

        } catch (Exception e) {
            // Log.i(TAG, "error :" + e);
        }
        // Log.i(TAG, "ConfrimNumSelect End...");
    }
}
