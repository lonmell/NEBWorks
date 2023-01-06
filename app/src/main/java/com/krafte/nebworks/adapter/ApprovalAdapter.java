package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.TaskCheckData;
import com.krafte.nebworks.dataInterface.ApprovalUpdateInterface;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.PushLogInputInterface;
import com.krafte.nebworks.pop.OneButtonTItlePopActivity;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApprovalAdapter extends RecyclerView.Adapter<ApprovalAdapter.ViewHolder> {
    private static final String TAG = "ApprovalAdapter";
    private ArrayList<TaskCheckData.TaskCheckData_list> mData = null;
    Context mContext;
    Dlog dlog = new Dlog();
    int user_kind;
    int setKind = 0;
    PreferenceHelper shardpref;
    String USER_INFO_AUTH = "";
    String USER_INFO_ID = "";
    PageMoveClass pm = new PageMoveClass();

    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    Calendar cal;
    String format = "yyyy-MM-dd";
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    String toDay = "";
    String place_id = "";

    List<String> user_id = new ArrayList<>();
    List<String> user_name = new ArrayList<>();
    List<String> user_img_path = new ArrayList<>();
    List<String> user_img_jikgup = new ArrayList<>();


    public ApprovalAdapter(Context context, ArrayList<TaskCheckData.TaskCheckData_list> data, int user_kind, int setKind) {
        this.mData = data;
        this.user_kind = user_kind;
        this.setKind = setKind;
        mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public ApprovalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.task_checklist_item, parent, false);
        ApprovalAdapter.ViewHolder vh = new ApprovalAdapter.ViewHolder(view);
        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint({"LongLogTag", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ApprovalAdapter.ViewHolder holder, int position) {
        TaskCheckData.TaskCheckData_list item = mData.get(position);
        user_id.clear();
        user_name.clear();
        user_img_path.clear();
        user_img_jikgup.clear();
        //체크박스 세팅
        /* getTask_kind
          1 = 일반업무
          2 = 개인업무
          3 = 휴가신청
        */
        /*getState
         * 결재 여부
          ( 승인 / 반려 / 거부 )
          0 - 처리전
          1 - 승인
          2 - 반려
          3 - 거부
         * */
        try {
            JSONArray Response = new JSONArray(item.getUsers().toString().replace("[[", "[").replace("]]", "]"));
            dlog.i("users : " + item.getUsers());
            dlog.i("users Response : " + Response.length());
            if (Response.length() == 0) {
                Log.i(TAG, "GET SIZE 1: " + Response.length());
            } else {
                Log.i(TAG, "GET SIZE 2: " + Response.length());
                user_id.removeAll(user_id);
                user_name.removeAll(user_name);
                user_img_path.removeAll(user_img_path);
                user_img_jikgup.removeAll(user_img_jikgup);
                for (int i = 0; i < Response.length(); i++) {
                    JSONObject jsonObject = Response.getJSONObject(i);
                    if (!jsonObject.getString("user_name").equals("null")) {
                        user_id.add(jsonObject.getString("user_id"));
                        user_name.add(jsonObject.getString("user_name"));
                        user_img_path.add(jsonObject.getString("img_path"));
                        user_img_jikgup.add(jsonObject.getString("jikgup"));
                    }
                }
            }

            if (item.getState().equals("0")) {
                //대기
                holder.state_tv.setText("승인대기");
                holder.state_area.setBackgroundResource(R.drawable.default_right_blue_round);
                holder.state_line.setBackgroundColor(Color.parseColor("#6395EC"));
            } else if (item.getState().equals("1")) {
                //승인
                holder.state_tv.setText("승인");
                holder.state_area.setBackgroundResource(R.drawable.default_right_gray_round);
                holder.state_line.setBackgroundColor(Color.parseColor("#C3C3C3"));
            } else if (item.getState().equals("2")) {
                //반려
                holder.state_tv.setText("반려");
                holder.state_area.setBackgroundResource(R.drawable.default_right_red_round);
                holder.state_line.setBackgroundColor(Color.parseColor("#DD6540"));
            }

            Glide.with(mContext).load(item.getRequester_img_path())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(holder.workimg);

            if (item.getComplete_kind().equals("0")) {
                Glide.with(mContext).load(R.drawable.ic_taskkind00)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(holder.kind);
            } else if (item.getComplete_kind().equals("1")) {
                Glide.with(mContext).load(R.drawable.ic_taskkind01)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(holder.kind);
            }

            holder.title.setText(item.getTitle());
            holder.name.setText(item.getRequester_name());
            cal = Calendar.getInstance();
            toDay = sdf.format(cal.getTime()).replace("-", ".");
            dlog.i("오늘 :" + toDay);
            shardpref.putString("FtoDay", toDay);


            holder.work_start_time.setText(item.getStart_time() + " 시작");
            holder.work_end_time.setText(item.getEnd_time() + " 마감");

            holder.accept_btn.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(v, position);
                }
                setUpdateWorktodo("1", item.getId()); //승인
                String message = "[" + item.getTitle() + "] 가 승인되었습니다.";
                getUserToken(item.getRequester_id(), "1", message);
                AddPush("업무결재",message,item.getRequester_id());
            });
            holder.reject_btn.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(v, position);
                }
                setUpdateWorktodo("2", item.getId()); //반려
                String message = "[" + item.getTitle() + "] 가 반려되었습니다.";
                getUserToken(item.getRequester_id(), "1", message);
                AddPush("업무결재",message,item.getRequester_id());
            });
        } catch (Exception e) {
            dlog.i("Exception : " + e);
        }
    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView state_tv;
        LinearLayout state_line, state_area;

        ImageView workimg, kind;

        TextView title, name;
        TextView work_start_time, work_end_time;

        RelativeLayout accept_btn, reject_btn;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            state_tv = itemView.findViewById(R.id.state_tv);
            state_area = itemView.findViewById(R.id.state_area);
            state_line = itemView.findViewById(R.id.state_line);
            workimg = itemView.findViewById(R.id.workimg);
            kind = itemView.findViewById(R.id.kind);
            title = itemView.findViewById(R.id.title);
            name = itemView.findViewById(R.id.name);
            work_start_time = itemView.findViewById(R.id.work_start_time);
            work_end_time = itemView.findViewById(R.id.work_end_time);
            accept_btn = itemView.findViewById(R.id.accept_btn);
            reject_btn = itemView.findViewById(R.id.reject_btn);

            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            place_id = shardpref.getString("place_id", "");

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    TaskCheckData.TaskCheckData_list item = mData.get(pos);
                    shardpref.putString("id", item.getId());
                    shardpref.putString("state", item.getState());
                    shardpref.putString("requester_id", item.getRequester_id());
                    shardpref.putString("requester_name", item.getRequester_name());
                    shardpref.putString("requester_img_path", item.getRequester_img_path());
                    shardpref.putString("title", item.getTitle());
                    shardpref.putString("contents", item.getContents());
                    shardpref.putString("complete_kind", item.getComplete_kind());
                    shardpref.putString("start_time", item.getStart_time());
                    shardpref.putString("end_time", item.getEnd_time());
                    shardpref.putString("complete_time", item.getComplete_time());
                    shardpref.putString("task_img_path", item.getTask_img_path());
                    shardpref.putString("complete_yn", item.getComplete_yn());
                    shardpref.putString("incomplete_reason", item.getIncomplete_reason());
                    shardpref.putString("reject_reason", item.getReject_reason());
                    shardpref.putString("task_date", item.getTask_date());
                    shardpref.putString("request_date", item.getRequest_date());
                    shardpref.putString("approval_date", item.getApproval_date());
                    JSONArray Response = null;
                    try {
                        user_id.removeAll(user_id);
                        user_name.removeAll(user_name);
                        user_img_path.removeAll(user_img_path);
                        user_img_jikgup.removeAll(user_img_jikgup);
                        Response = new JSONArray(item.getUsers().toString().replace("[[", "[").replace("]]", "]"));
                        for (int i = 0; i < Response.length(); i++) {
                            JSONObject jsonObject = Response.getJSONObject(i);
                            if (!jsonObject.getString("user_name").equals("null")) {
                                user_id.add(jsonObject.getString("user_id"));
                                user_name.add(jsonObject.getString("user_name"));
                                user_img_path.add(jsonObject.getString("img_path"));
                                user_img_jikgup.add(jsonObject.getString("jikgup"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    shardpref.putString("users", user_id.toString());
                    shardpref.putString("usersn", user_name.toString());
                    shardpref.putString("usersimg", user_img_path.toString());
                    shardpref.putString("usersjikgup", user_img_jikgup.toString());
                    dlog.i("users : " + user_id.toString());
                    dlog.i("usersn : " + user_name.toString());
                    dlog.i("usersimg : " + user_img_path.toString());
                    dlog.i("usersimg : " + user_img_jikgup.toString());
                    pm.ApprovalDetailGo(mContext);
                }
            });
        }
    }

    public void addItem(TaskCheckData.TaskCheckData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    RetrofitConnect rc = new RetrofitConnect();
    public void setUpdateWorktodo(String kind, String task_no) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApprovalUpdateInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ApprovalUpdateInterface api = retrofit.create(ApprovalUpdateInterface.class);
//        task_no.replace(",","|")
        Call<String> call = api.getData(task_no, USER_INFO_ID, kind, "");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "setUpdateWorktodo function START");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                Log.e(TAG, "response 2: " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = rc.getBase64decode(response.body());
                    dlog.i("jsonResponse length : " + jsonResponse.length());
                    dlog.i("jsonResponse : " + jsonResponse);
                    if (jsonResponse.replace("\"", "").equals("success")) {
                        Intent intent = new Intent(mContext, OneButtonTItlePopActivity.class);
                        if (kind.equals("1")) {
                            intent.putExtra("title", "승인 완료");
                            intent.putExtra("data", "승인처리가 완료 되었습니다.");
                        } else {
                            intent.putExtra("title", "반려 완료");
                            intent.putExtra("data", "반려처리가 완료 되었습니다.");
                        }
                        mContext.startActivity(intent);
                        ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                    } else {
                        Toast.makeText(mContext, "Error", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }


    String place_owner_id = "";
    public void getUserToken(String user_id, String type, String message) {
        dlog.i("-----getManagerToken-----");
        dlog.i("user_id : " + user_id);
        dlog.i("type : " + type);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FCMSelectInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FCMSelectInterface api = retrofit.create(FCMSelectInterface.class);
        Call<String> call = api.getData(user_id, type);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String jsonResponse = rc.getBase64decode(response.body());
                dlog.i("jsonResponse length : " + jsonResponse.length());
                dlog.i("jsonResponse : " + jsonResponse);
                try {
                    JSONArray Response = new JSONArray(jsonResponse);
                    if (Response.length() > 0) {
                        dlog.i("-----getManagerToken-----");
                        dlog.i("user_id : " + Response.getJSONObject(0).getString("user_id"));
                        dlog.i("token : " + Response.getJSONObject(0).getString("token"));
                        String id = Response.getJSONObject(0).getString("id");
                        String token = Response.getJSONObject(0).getString("token");
                        dlog.i("-----getManagerToken-----");
                        place_owner_id = shardpref.getString("place_owner_id","");
                        boolean channelId1 = Response.getJSONObject(0).getString("channel2").equals("1");
                        if (!token.isEmpty() && channelId1) {
                            PushFcmSend(id, "", message, token, "2", place_id);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러 = " + t.getMessage());
            }
        });
    }

    public void AddPush(String title, String content, String user_id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PushLogInputInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PushLogInputInterface api = retrofit.create(PushLogInputInterface.class);
        Call<String> call = api.getData(place_id, "", title, content, place_owner_id, user_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String jsonResponse = rc.getBase64decode(response.body());
                dlog.i("jsonResponse length : " + jsonResponse.length());
                dlog.i("jsonResponse : " + jsonResponse);
                if (response.isSuccessful() && response.body() != null) {

                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러1 = " + t.getMessage());
            }
        });
    }

    DBConnection dbConnection = new DBConnection();
    String click_action = "";
    //점주 > 근로자
    private void PushFcmSend(String topic, String title, String message, String token, String tag, String place_id) {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            click_action = "TaskList1";
            dlog.i("-----PushFcmSend-----");
            dlog.i("topic : " + topic);
            dlog.i("title : " + title);
            dlog.i("message : " + message);
            dlog.i("token : " + token);
            dlog.i("click_action : " + click_action);
            dlog.i("tag : " + tag);
            dlog.i("place_id : " + place_id);
            dlog.i("-----PushFcmSend-----");
            dbConnection.FcmTestFunction(topic, title, message, token, click_action, tag, place_id);
//            activity.runOnUiThread(() -> {
//            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /* -- 할일 추가 FCM 전송 영역 */
}
