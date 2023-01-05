package com.krafte.nebworks.util;

import android.util.Base64;
import android.util.Log;

import com.krafte.nebworks.data.CertiNumData;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.LastVersion;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.UserCheckData;

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

    GetResultData resultData = new GetResultData();
    UserCheckData ucd = new UserCheckData();
    PlaceCheckData pcd = new PlaceCheckData();
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
            String page = URL + "/NEBWorks/fcmsend.php";
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

    //앱 마지막 버전 번호 조회
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

    //사용자 정보 조회 (2023-01-04)
    public void UserCheck(String place_id, String user_id){
        // HttpUrlConnection
        try {
//            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            String page = URL + "/NEBWorks/place/get_member.php";
            // URL 객체 생성
            URL url = new URL(page);
            // 연결 객체 생성
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Post 파라미터
            String params = "place_id=" + place_id + "&user_id=" + user_id;
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
//                    JSONArray jsonResponse = new JSONArray(sb.toString());
                    JSONArray jsonResponse = new JSONArray(getBase64decode(sb.toString()));
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonObject = jsonResponse.getJSONObject(i);
                        UserCheckData.getInstance().setUser_id(jsonObject.getString("id"));
                        UserCheckData.getInstance().setUser_password(jsonObject.getString("password"));
                        UserCheckData.getInstance().setPlace_name(jsonObject.getString("place_name"));

                        UserCheckData.getInstance().setUser_account(jsonObject.getString("account"));
                        UserCheckData.getInstance().setUser_name(jsonObject.getString("name"));
                        UserCheckData.getInstance().setUser_nick_name(jsonObject.getString("nick_name"));
                        UserCheckData.getInstance().setUser_phone(jsonObject.getString("phone"));
                        UserCheckData.getInstance().setUser_gender(jsonObject.getString("gender"));
                        UserCheckData.getInstance().setUser_img_path(jsonObject.getString("img_path"));
                        UserCheckData.getInstance().setUser_jumin(jsonObject.getString("jumin"));
                        UserCheckData.getInstance().setUser_kind(jsonObject.getString("kind"));
                        UserCheckData.getInstance().setUser_join_date(jsonObject.getString("join_date"));
                        UserCheckData.getInstance().setUser_state(jsonObject.getString("state"));
                        UserCheckData.getInstance().setUser_jikgup(jsonObject.getString("jikgup"));
                        UserCheckData.getInstance().setUser_pay(jsonObject.getString("pay"));
                        UserCheckData.getInstance().setUser_worktime(jsonObject.getString("worktime"));
                        UserCheckData.getInstance().setUser_inoutstate(jsonObject.getString("inoutstate"));
                        UserCheckData.getInstance().setUser_sieob(jsonObject.getString("sieob"));
                        UserCheckData.getInstance().setUser_jongeob(jsonObject.getString("jongeob"));
                        UserCheckData.getInstance().setUser_platform(jsonObject.getString("platform"));
                        UserCheckData.getInstance().setUser_contract_cnt(jsonObject.getString("contract_cnt"));



//                        ucd.setUser_id(jsonObject.getString("id"));
//                        ucd.setUser_password(jsonObject.getString("password"));
//                        ucd.setPlace_name(jsonObject.getString("place_name"));
//                        ucd.setUser_account(jsonObject.getString("account"));
//                        ucd.setUser_name(jsonObject.getString("name"));
//                        ucd.setUser_nick_name(jsonObject.getString("nick_name"));
//                        ucd.setUser_phone(jsonObject.getString("phone"));
//                        ucd.setUser_gender(jsonObject.getString("gender"));
//                        ucd.setUser_img_path(jsonObject.getString("img_path"));
//                        ucd.setUser_jumin(jsonObject.getString("jumin"));
//                        ucd.setUser_kind(jsonObject.getString("kind"));
//                        ucd.setUser_join_date(jsonObject.getString("join_date"));
//                        ucd.setUser_state(jsonObject.getString("state"));
//                        ucd.setUser_jikgup(jsonObject.getString("jikgup"));
//                        ucd.setUser_pay(jsonObject.getString("pay"));
//                        ucd.setUser_worktime(jsonObject.getString("worktime"));
//                        ucd.setUser_inoutstate(jsonObject.getString("inoutstate"));
//                        ucd.setUser_sieob(jsonObject.getString("sieob"));
//                        ucd.setUser_jongeob(jsonObject.getString("jongeob"));
//                        ucd.setUser_platform(jsonObject.getString("platform"));
//                        ucd.setUser_contract_cnt(jsonObject.getString("contract_cnt"));
                    }
                }
                // 연결 끊기
                conn.disconnect();
                // // Log.i(TAG, "conn 연결 끊기");
            }

        } catch (Exception e) {
            // Log.i(TAG, "error :" + e);
        }
    }

    public void UserAccountCheck(String account){
        // HttpUrlConnection
        try {
//            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            String page = URL + "/NEBWorks/user/get.php";
            // URL 객체 생성
            URL url = new URL(page);
            // 연결 객체 생성
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Post 파라미터
            String params = "account=" + account;
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
                    // 응답 Json 타입일 경우
//                    JSONArray jsonResponse = new JSONArray(sb.toString());
                    JSONArray jsonResponse = new JSONArray(getBase64decode(sb.toString()));
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonObject = jsonResponse.getJSONObject(i);
                        ucd.setUser_id(jsonObject.getString("id"));
                        ucd.setUser_password(jsonObject.getString("password"));
                        ucd.setUser_account(jsonObject.getString("account"));
                        ucd.setUser_name(jsonObject.getString("name"));
                        ucd.setUser_nick_name(jsonObject.getString("nick_name"));
                        ucd.setUser_phone(jsonObject.getString("phone"));
                        ucd.setUser_gender(jsonObject.getString("gender"));
                        ucd.setUser_img_path(jsonObject.getString("img_path"));
                        ucd.setUser_platform(jsonObject.getString("platform"));
                    }
                }
                // 연결 끊기
                conn.disconnect();
                // // Log.i(TAG, "conn 연결 끊기");
            }

        } catch (Exception e) {
            // Log.i(TAG, "error :" + e);
        }
    }

    //매장 정보 조회 (2023-01-04)
    public void PlacegetData(String place_id){
        // HttpUrlConnection
        try {
//            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            String page = URL + "/NEBWorks/place/get.php";
            // URL 객체 생성
            URL url = new URL(page);
            // 연결 객체 생성
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Post 파라미터
            String params = "place_id=" + place_id;
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
//                    JSONArray jsonResponse = new JSONArray(sb.toString());
                    JSONArray jsonResponse = new JSONArray(getBase64decode(sb.toString()));
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonObject = jsonResponse.getJSONObject(i);
                        pcd.setPlace_name(jsonObject.getString("name"));
                        pcd.setPlace_owner_id(jsonObject.getString("owner_id"));
                        pcd.setPlace_owner_name(jsonObject.getString("owner_name"));
                        pcd.setRegistr_num(jsonObject.getString("registr_num"));
                        pcd.setStore_kind(jsonObject.getString("store_kind"));
                        pcd.setPlace_address(jsonObject.getString("address"));
                        pcd.setPlace_latitude(jsonObject.getString("latitude"));
                        pcd.setPlace_longitude(jsonObject.getString("longitude"));
                        pcd.setPlace_pay_day(jsonObject.getString("pay_day"));
                        pcd.setPlace_test_period(jsonObject.getString("test_period"));
                        pcd.setPlace_vacation_select(jsonObject.getString("vacation_select"));
                        pcd.setPlace_insurance(jsonObject.getString("insurance"));
                        pcd.setPlace_start_time(jsonObject.getString("start_time"));
                        pcd.setPlace_end_time(jsonObject.getString("end_time"));
                        pcd.setPlace_save_kind(jsonObject.getString("save_kind"));
                        pcd.setPlace_wifi_name(jsonObject.getString("wifi_name"));
                        pcd.setPlace_img_path(jsonObject.getString("img_path"));
                        pcd.setPlace_start_date(jsonObject.getString("start_date"));
                        pcd.setPlace_created_at(jsonObject.getString("created_at"));
                        pcd.setPlace_icnt(jsonObject.getString("i_cnt"));
                        pcd.setPlace_ocnt(jsonObject.getString("o_cnt"));
                        pcd.setPlace_totalcnt(jsonObject.getString("total_cnt"));
                    }
                }
                // 연결 끊기
                conn.disconnect();
                // // Log.i(TAG, "conn 연결 끊기");
            }

        } catch (Exception e) {
            // Log.i(TAG, "error :" + e);
        }
    }

}
