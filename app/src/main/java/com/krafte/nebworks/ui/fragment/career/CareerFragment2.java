package com.krafte.nebworks.ui.fragment.career;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.databinding.CareerFragment2Binding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.util.ArrayList;

public class CareerFragment2  extends Fragment {
    private CareerFragment2Binding binding;
    private static final String TAG = "CareerFragment2";
    Context mContext;

    //shardpref
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String place_id = "";

    //Other
    Activity activity;
//    MyCareerListAdapter mAdapter = null;
//    ArrayList<MyCareerListData.MyCareerListData_list> mList;
//
//    MyEduListAdapter mAdapter2 = null;
//    ArrayList<MyEduListData.MyEduListData_list> mList2;
//
//    CareerTaskListAdapter mAdapter3 = null;
//    ArrayList<CareerTaskData.CareerTaskData_list> mList3;

    PageMoveClass pm = new PageMoveClass();
    RetrofitConnect rc = new RetrofitConnect();
    Dlog dlog = new Dlog();

    GetResultData resultData = new GetResultData();
    ArrayList<String> SetWorkPlace;
    ArrayAdapter<String> SetWorkPlaceFilter;
    String SelectEmployeeStore = "";
    String SelectDay = "";
    float CareerYear = 0;
    int PercnetInt = 0;

    String CareerYear_Char = "";
    String CareerChar1 = "";
    String CareerChar2 = "";

    public static CareerFragment2 newInstance(int number) {
        CareerFragment2 fragment = new CareerFragment2();
        Bundle bundle = new Bundle();
        bundle.putInt("number", number);
        fragment.setArguments(bundle);
        return fragment;
    }

    /*Fragment 콜백함수*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            int num = getArguments().getInt("number");
        }
    }

    @SuppressLint("LongLogTag")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.career_fragment2, container, false);
        binding = CareerFragment2Binding.inflate(inflater);
        mContext = inflater.getContext();
        shardpref = new PreferenceHelper(mContext);

        dlog.DlogContext(mContext);


        //Shared
        place_id = shardpref.getString("place_id","");
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");

//        career_txt03.setOnClickListener(v -> {
//            pm.EmployeeCareerEidt(mContext);
//        });
//
//        education_txt03.setOnClickListener(v -> {
//            pm.EmployeeEduEdit(mContext);
//        });
//
//        introduce_txt03.setOnClickListener(v -> {
//            pm.EmployeeIntroduce(mContext);
//        });
//
//        task_txt03.setOnClickListener(v -> {
//            pm.EmployeeTaskEdit(mContext);
//        });

//        CareerManagement("2", "", USER_INFO_ID, "", "", "", "", "", "", "", "", "");
//        EduManagement("2", "", USER_INFO_ID, "", "", "", "", "", "", "");
//        IntroduceManagement("2", "", USER_INFO_ID, "", "", "", "", "", "", "", ""
//                , "", "", 0, 0, 0, 0, 0, 0);
//        TaskManagement("2", "", USER_INFO_ID, "", "");


        return binding.getRoot();
    }
    @Override
    public void onResume(){
        super.onResume();
        PercnetInt = 0;
        CareerManagement("2", "", USER_INFO_ID, "", "", "", "", "", "", "", "", "");
        EduManagement("2", "", USER_INFO_ID, "", "", "", "", "", "", "");
        IntroduceManagement("2", "", USER_INFO_ID, "", "", "", "", "", "", "", ""
                , "", "", 0, 0, 0, 0, 0, 0);
        TaskManagement("2", "", USER_INFO_ID, "", "");
    }
    //--------------레트로핏 영역 START-------------------
    //경력
    public void CareerManagement(String flag, String career_no, String user_id, String company_name
            , String employee_state, String incompany_date
            , String outcompany_date
            , String state_position, String countersignature
            , String workdetail_comment, String business_type
            , String country) {
//        CareerYear = 0;
//        rc.myCareerListData_lists.clear();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(CareerManagementInterface.URL)
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .build();
//        CareerManagementInterface api = retrofit.create(CareerManagementInterface.class);
//        Call<String> call = api.getData(flag, career_no, user_id, company_name, employee_state, incompany_date, outcompany_date
//                , state_position, countersignature, workdetail_comment, business_type, country);
//        call.enqueue(new Callback<String>() {
//            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n", "LongLogTag"})
//            @Override
//            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    String jsonResponse = rc.getBase64decode(response.body());
////                    Log.e("onSuccess", jsonResponse);
//                    try {
//                        //Array데이터를 받아올 때
//                        JSONArray Response = new JSONArray(jsonResponse);
//
//                        if (flag.equals("1") || flag.equals("3") || flag.equals("4")) {
//                            dlog.i( "(CareerManagement)결과 문자열 / flag :" + flag);
//                            resultData.setRESULT(jsonResponse.replaceAll("\"", "").replaceAll("\"", ""));
//                        } else if (flag.equals("2")) {
//                            dlog.i( "(CareerManagement)결과 문자열 / flag :" + flag);
//                            mList = new ArrayList<>();
//                            mAdapter = new MyCareerListAdapter(mContext, mList);
//                            career_list.setAdapter(mAdapter);
//                            career_list.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
//                            if(Response.length() == 0){
//                                dlog.i("career response length is 0");
//                            }else{
//                                //PercnetInt++;
//                                dlog.i("PercnetInt++");
//                                for (int i = 0; i < Response.length(); i++) {
//                                    JSONObject jsonObject = Response.getJSONObject(i);
//                                    mAdapter.addItem(new MyCareerListData.MyCareerListData_list(
//                                            jsonObject.getString("no"),
//                                            jsonObject.getString("write_id"),
//                                            jsonObject.getString("company_name"),
//                                            jsonObject.getString("state"),
//                                            jsonObject.getString("incompany_date"),
//                                            jsonObject.getString("outcompany_date"),
//                                            jsonObject.getString("input_date"),
//                                            jsonObject.getString("state_position"),
//                                            jsonObject.getString("countersignature"),
//                                            jsonObject.getString("workdetail_comment"),
//                                            jsonObject.getString("business_type"),
//                                            jsonObject.getString("country"),
//                                            jsonObject.getString("workyear"),
//                                            jsonObject.getString("careercnt"),
//                                            jsonObject.getString("educnt"),
//                                            jsonObject.getString("taskcnt"),
//                                            jsonObject.getString("introducecnt")
//                                    ));
//
//                                    CareerYear = CareerYear + Float.parseFloat(Response.getJSONObject(i).getString("workyear"));
//                                }
//                            }
//                            int careercnt = Integer.parseInt(Response.getJSONObject(0).getString("careercnt").equals("null")?"0":Response.getJSONObject(0).getString("careercnt"));
//                            int educnt = Integer.parseInt(Response.getJSONObject(0).getString("educnt").equals("null")?"0":Response.getJSONObject(0).getString("educnt"));
//                            int taskcnt = Integer.parseInt(Response.getJSONObject(0).getString("taskcnt").equals("null")?"0":Response.getJSONObject(0).getString("taskcnt"));
//                            int introducecnt = Integer.parseInt(Response.getJSONObject(0).getString("introducecnt").equals("null")?"0":Response.getJSONObject(0).getString("introducecnt"));
//                            PercnetInt = careercnt + educnt + taskcnt + introducecnt;
//                            dlog.e("careercnt : " + careercnt);
//                            dlog.e("educnt : " + educnt);
//                            dlog.e("taskcnt : " + taskcnt);
//                            dlog.e("introducecnt : " + introducecnt);
//
//                            mAdapter.notifyDataSetChanged();
//                            CareerYear_Char = String.valueOf(Math.round(CareerYear * 10) / 10.0);
//
//                            int idx = CareerYear_Char.indexOf(".");
//
//                            CareerChar1 = CareerYear_Char.substring(0, idx);
//                            CareerChar2 = CareerYear_Char.substring(idx + 1);
//                            dlog.i( "CareerChar1 : " + CareerChar1);
//                            dlog.i( "CareerChar2 : " + CareerChar2);
//                            career_txt02.setText(CareerChar1 + "년 " + CareerChar2 + "개월");
//                            dlog.i( "CareerYear : " + CareerChar1 + "년 " + CareerChar2 + "개월");
//
//                            dlog.i("PercnetInt : " + PercnetInt);
//                            if (PercnetInt == 0) {
//                                career_percent01.setBackgroundColor(Color.parseColor("#696969"));
//                                career_percent02.setBackgroundColor(Color.parseColor("#696969"));
//                                career_percent03.setBackgroundColor(Color.parseColor("#696969"));
//                                career_percent04.setBackgroundColor(Color.parseColor("#696969"));
//                            } else if (PercnetInt == 1) {
//                                career_percent01.setBackgroundColor(Color.parseColor("#6495ed"));
//                                career_percent02.setBackgroundColor(Color.parseColor("#696969"));
//                                career_percent03.setBackgroundColor(Color.parseColor("#696969"));
//                                career_percent04.setBackgroundColor(Color.parseColor("#696969"));
//                            } else if (PercnetInt == 2) {
//                                career_percent01.setBackgroundColor(Color.parseColor("#6495ed"));
//                                career_percent02.setBackgroundColor(Color.parseColor("#6495ed"));
//                                career_percent03.setBackgroundColor(Color.parseColor("#696969"));
//                                career_percent04.setBackgroundColor(Color.parseColor("#696969"));
//                            } else if (PercnetInt == 3) {
//                                career_percent01.setBackgroundColor(Color.parseColor("#6495ed"));
//                                career_percent02.setBackgroundColor(Color.parseColor("#6495ed"));
//                                career_percent03.setBackgroundColor(Color.parseColor("#6495ed"));
//                                career_percent04.setBackgroundColor(Color.parseColor("#696969"));
//                            } else if (PercnetInt == 4) {
//                                career_percent01.setBackgroundColor(Color.parseColor("#6495ed"));
//                                career_percent02.setBackgroundColor(Color.parseColor("#6495ed"));
//                                career_percent03.setBackgroundColor(Color.parseColor("#6495ed"));
//                                career_percent04.setBackgroundColor(Color.parseColor("#6495ed"));
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @SuppressLint("LongLogTag")
//            @Override
//            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                Log.e(TAG, "에러 = " + t.getMessage());
//            }
//        });

    }

    public void EduManagement(String flag, String edu_no, String user_id, String school_name
            , String degree
            , String major, String into_school_date
            , String out_school_date
            , String state, String paper_comment) {
//        rc.myEduListData_lists.clear();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(EduManagementInterface.URL)
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .build();
//        EduManagementInterface api = retrofit.create(EduManagementInterface.class);
//        Call<String> call = api.getData(flag, edu_no, user_id, school_name
//                , degree, major, into_school_date, out_school_date
//                , state, paper_comment);
//        call.enqueue(new Callback<String>() {
//            @SuppressLint({"LongLogTag", "NotifyDataSetChanged"})
//            @Override
//            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    String jsonResponse = rc.getBase64decode(response.body());
////                    Log.e("onSuccess", jsonResponse);
//                    try {
//                        //Array데이터를 받아올 때
//                        JSONArray Response = new JSONArray(jsonResponse);
//
//                        if (flag.equals("1") || flag.equals("3") || flag.equals("4")) {
//                            dlog.i( "(CareerManagement)결과 문자열 / flag :" + flag);
//                            resultData.setRESULT(jsonResponse.replaceAll("\"", ""));
//                        } else if (flag.equals("2")) {
//                            dlog.i( "(CareerManagement)결과 문자열 / flag :" + flag);
//                            for (int i = 0; i < Response.length(); i++) {
//                                JSONObject jsonObject = Response.getJSONObject(i);
//
//                                rc.myEduListData_lists.add(new MyEduListData.MyEduListData_list(
//                                        jsonObject.getString("no"),
//                                        jsonObject.getString("write_id"),
//                                        jsonObject.getString("school_name"),
//                                        jsonObject.getString("degree"),
//                                        jsonObject.getString("major"),
//                                        jsonObject.getString("into_school_date"),
//                                        jsonObject.getString("out_school_date"),
//                                        jsonObject.getString("state"),
//                                        jsonObject.getString("paper_comment")
//                                ));
//                            }
//                            mList2 = new ArrayList<>();
//                            mAdapter2 = new MyEduListAdapter(mContext, mList2);
//                            education_list.setAdapter(mAdapter2);
//                            education_list.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
//
//                            if (rc.myEduListData_lists.size() == 0) {
//                                dlog.i( "GET SIZE : " + rc.myEduListData_lists.size());
//                            } else {
//                                //PercnetInt++;
//                                dlog.i("PercnetInt++");
//                                dlog.i( "setEduList PercnetInt : " + PercnetInt);
//                                for (int i = 0; i < rc.myEduListData_lists.size(); i++) {
//                                    mAdapter2.addItem(new MyEduListData.MyEduListData_list(
//                                            rc.myEduListData_lists.get(i).getNo(),
//                                            rc.myEduListData_lists.get(i).getWrite_id(),
//                                            rc.myEduListData_lists.get(i).getSchool_name(),
//                                            rc.myEduListData_lists.get(i).getDegree(),
//                                            rc.myEduListData_lists.get(i).getMajor(),
//                                            rc.myEduListData_lists.get(i).getInto_school_date(),
//                                            rc.myEduListData_lists.get(i).getOut_school_date(),
//                                            rc.myEduListData_lists.get(i).getState(),
//                                            rc.myEduListData_lists.get(i).getPaper_comment()
//                                    ));
//                                }
//                                mAdapter2.notifyDataSetChanged();
//                            }
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @SuppressLint("LongLogTag")
//            @Override
//            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                Log.e(TAG, "에러 = " + t.getMessage());
//            }
//        });
    }


    public void IntroduceManagement(String flag, String intro_no, String user_id, String contents
            , String kor_name, String eng_name, String call_phone, String email, String address
            , String hope_money, String hope_workdate, String hope_work_yoil, String hope_work_time
            , int name_yn, int phone_yn, int mail_yn, int address_yn, int hope_yn, int gujik_yn) {
//        rc.introduceData_lists.clear();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(IntroduceManagementInterface.URL)
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .build();
//        IntroduceManagementInterface api = retrofit.create(IntroduceManagementInterface.class);
//        Call<String> call = api.getData(flag, intro_no, user_id, contents
//                , kor_name, eng_name, call_phone, email, address
//                , hope_money, hope_workdate, hope_work_yoil, hope_work_time
//                , name_yn, phone_yn, mail_yn, address_yn, hope_yn, gujik_yn);
//        call.enqueue(new Callback<String>() {
//            @SuppressLint("LongLogTag")
//            @Override
//            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    String jsonResponse = rc.getBase64decode(response.body());
////                    Log.e("onSuccess", jsonResponse);
//                    //Array데이터를 받아올 때
//                    try {
//                        JSONArray Response = new JSONArray(jsonResponse);
//                        resultData.setRESULT(jsonResponse.replaceAll("\"", ""));
//                        if (flag.equals("1") || flag.equals("3")) {
//                            resultData.setRESULT(jsonResponse.replaceAll("\"", ""));
//                        } else if (flag.equals("2")) {
////                            for (int i = 0; i < Response.length(); i++) {
////                                JSONObject jsonObject = Response.getJSONObject(i);
////
////                                rc.introduceData_lists.add(new IntroduceData.IntroduceData_list(
////                                        jsonObject.getString("no"),
////                                        jsonObject.getString("write_id"),
////                                        jsonObject.getString("contents"),
////                                        jsonObject.getString("input_date"),
////                                        jsonObject.getString("kor_name"),
////                                        jsonObject.getString("eng_name"),
////                                        jsonObject.getString("call_phone"),
////                                        jsonObject.getString("email"),
////                                        jsonObject.getString("address"),
////                                        jsonObject.getString("hope_money"),
////                                        jsonObject.getString("hope_workdate"),
////                                        jsonObject.getString("hope_work_yoil"),
////                                        jsonObject.getString("hope_work_time"),
////                                        jsonObject.getString("name_yn"),
////                                        jsonObject.getString("phone_yn"),
////                                        jsonObject.getString("mail_yn"),
////                                        jsonObject.getString("address_yn"),
////                                        jsonObject.getString("hope_yn"),
////                                        jsonObject.getString("gujik_yn"),
////                                        jsonObject.getString("thumnail_url")
////                                ));
////                            }
//                            if(Response.getJSONObject(0).getString("contents").length() > 5){
//                                //PercnetInt++;
//                                dlog.i("PercnetInt++");
//                            }
//                            introduce_text04.setText(Response.getJSONObject(0).getString("contents"));
//                            introduce_text04.setOnClickListener(v -> {
//                                try {
//                                    shardpref.putString("intro_no", Response.getJSONObject(0).getString("no"));
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                pm.EmployeeIntroduce(mContext);
//                            });
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//
//                }
//            }
//
//            @SuppressLint("LongLogTag")
//            @Override
//            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                Log.e(TAG, "에러 = " + t.getMessage());
//            }
//        });
    }

    //메인 페이지 회원정보(2022-06-09)
    public void TaskManagement(String flag, String task_no, String user_id, String contents01, String contents02) {
//        rc.taskData_lists.clear();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(TaskManagementInterface.URL)
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .build();
//        TaskManagementInterface api = retrofit.create(TaskManagementInterface.class);
//        Call<String> call = api.getData(flag, task_no, user_id, contents01, contents02);
//        call.enqueue(new Callback<String>() {
//            @SuppressLint({"LongLogTag", "NotifyDataSetChanged"})
//            @Override
//            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    String jsonResponse = rc.getBase64decode(response.body());
////                    Log.e("onSuccess", jsonResponse);
//                    try {
//                        //Array데이터를 받아올 때
//                        JSONArray Response = new JSONArray(jsonResponse);
//                        if (flag.equals("1") || flag.equals("3") || flag.equals("4")) {
//                            dlog.i( "(TaskManagement)flag :" + flag);
//                            resultData.setRESULT(jsonResponse.replaceAll("\"", ""));
//                        } else if (flag.equals("2")) {
//                            dlog.i( "(TaskManagement)flag :" + flag);
//                            for (int i = 0; i < Response.length(); i++) {
//                                JSONObject jsonObject = Response.getJSONObject(i);
//
//                                rc.taskData_lists.add(new CareerTaskData.CareerTaskData_list(
//                                        jsonObject.getString("no"),
//                                        jsonObject.getString("write_id"),
//                                        jsonObject.getString("contents01"),
//                                        jsonObject.getString("contents02")
//                                ));
//                            }
//                        }
//                        if (flag.equals("2")) {
//                            mList3 = new ArrayList<>();
//                            mAdapter3 = new CareerTaskListAdapter(mContext, mList3);
//                            task_list.setAdapter(mAdapter3);
//                            task_list.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
//                            dlog.i( "setCareerTaskList Thread run! ");
//
//                            if (rc.taskData_lists.size() == 0) {
//                                dlog.i( "GET SIZE : " + rc.taskData_lists.size());
//                            } else {
//                                //PercnetInt++;
//                                dlog.i("PercnetInt++");
//                                dlog.i( "setCareerTaskList PercnetInt : " + PercnetInt);
//                                for (int i = 0; i < rc.taskData_lists.size(); i++) {
//                                    mAdapter3.addItem(new CareerTaskData.CareerTaskData_list(
//                                            rc.taskData_lists.get(i).getNo(),
//                                            rc.taskData_lists.get(i).getWrite_id(),
//                                            rc.taskData_lists.get(i).getContents01(),
//                                            rc.taskData_lists.get(i).getContents02()
//                                    ));
//                                }
//                                mAdapter3.notifyDataSetChanged();
//                                mAdapter3.setOnItemClickListener((v, position) -> {
//                                    PercnetInt = PercnetInt - 1;
//                                    DeleteCareerTask("4", rc.taskData_lists.get(position).getNo());
//                                });
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @SuppressLint("LongLogTag")
//            @Override
//            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                Log.e(TAG, "에러 = " + t.getMessage());
//            }
//        });

    }

    private void DeleteCareerTask(String flag, String task_no) {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(TaskManagementInterface.URL)
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .build();
//        TaskManagementInterface api = retrofit.create(TaskManagementInterface.class);
//        Call<String> call = api.getData(flag, task_no, USER_INFO_ID, "", "");
//        call.enqueue(new Callback<String>() {
//            @SuppressLint("LongLogTag")
//            @Override
//            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    String jsonResponse = rc.getBase64decode(response.body());
//                    Log.e("onSuccess", response.body());
//                    //Array데이터를 받아올 때
//
//                    dlog.i( "setCareerList Thread run! ");
//                    dlog.i( "GetMessage : " + resultData.getRESULT());
//                    if (jsonResponse.replaceAll("\"", "").equals("success") || response.body().replaceAll("\"", "").equals("success")) {
//                        dlog.i("success");
//                        TaskManagement("2", "", USER_INFO_ID, "", "");
//                    } else {
//                        Toast.makeText(mContext, "통신에 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//            }
//
//            @SuppressLint("LongLogTag")
//            @Override
//            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                Log.e(TAG, "에러 = " + t.getMessage());
//            }
//        });
    }
}
