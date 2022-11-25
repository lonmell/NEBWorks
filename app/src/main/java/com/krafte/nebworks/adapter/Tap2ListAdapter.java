package com.krafte.nebworks.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.TodolistData;
import com.krafte.nebworks.data.UsersData;
import com.krafte.nebworks.pop.Tap2OptionActivity;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Tap2ListAdapter extends RecyclerView.Adapter<Tap2ListAdapter.ViewHolder> {
    private static final String TAG = "Tap2ListAdapter";
    private ArrayList<TodolistData.TodolistData_list> mData = null;
    Activity activity;
    Context mContext;
    PageMoveClass pm = new PageMoveClass();
    PreferenceHelper shardpref;
    FragmentManager fragmentManager;
    DateCurrent dc = new DateCurrent();
    ArrayList<UsersData.UsersData_list> mList;
    AdaperInMemberAdapter mAdapter;
    Dlog dlog = new Dlog();

    String[] yoil = new String[7];
    String setYoil = "";
    String USER_INFO_AUTH = "0";
    String USER_INFO_ID = "";
    String AMPM = "";
    List<String> user_id = new ArrayList<>();
    List<String> user_name = new ArrayList<>();
    List<String> user_img_path = new ArrayList<>();
    List<String> user_img_jikgup = new ArrayList<>();
    boolean[] checkareatf;
    List<String> checkworkno = new ArrayList<>();
    int kind = 0;
    String startTime = "";
    String endTime = "";

    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    public interface OnItemClickListener {
        void onItemClick(View v, int position, int Tcnt, int Fcnt);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public Tap2ListAdapter(Context context, ArrayList<TodolistData.TodolistData_list> data, FragmentManager fragmentManager, int kind) {//kind : 조회위치 [ 1 = 할일탭 / 2 = 캘린더>날짜선택>BottomSheet ]
        this.mData = data;
        this.mContext = context;
        this.kind = kind;
        this.fragmentManager = fragmentManager;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.tap2listitem, parent, false);
        ViewHolder vh = new ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TodolistData.TodolistData_list item = mData.get(position);
        try {
            shardpref = new PreferenceHelper(mContext);
            user_id.clear();
            user_name.clear();
            user_img_path.clear();
            user_img_jikgup.clear();
        /*
        getTask_kind
        1 = 일반업무
        2 = 개인업무
        3 = 휴가신청
        * */


        /*
        getTask_settime
        1 = 주간
        2 = 야간
        3 = 주/야간( 24시간 )
        */

        /*getState
        결재 여부
        ( 승인 / 반려 / 거부 )
        0 - 처리중
        1 - 승인
        2 - 반려
        3 - 거부
        */

        /*
        1 = 인증사진
        2 = 체크
        */
            try {
                if (item.getWriter_id().equals(USER_INFO_ID)) {
                    holder.list_setting.setVisibility(View.VISIBLE);
                } else {
                    holder.list_setting.setVisibility(View.GONE);
                }

                JSONArray Response = new JSONArray(item.getUsers().toString().replace("[[", "[").replace("]]", "]"));
                dlog.i("users : " + item.getUsers());
                dlog.i("users Response : " + Response.length());
                List<String> join_member = new ArrayList<>();
//                mList = new ArrayList<>();
//                mAdapter = new AdaperInMemberAdapter(mContext, mList);
//                holder.member_list.setAdapter(mAdapter);
//                holder.member_list.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
                if (Response.length() == 0) {
                    Log.i(TAG, "GET SIZE 1: " + Response.length());
                } else {
                    Log.i(TAG, "GET SIZE 2: " + Response.length());
                    for (int i = 0; i < Response.length(); i++) {
                        JSONObject jsonObject = Response.getJSONObject(i);
                        join_member.add(jsonObject.getString("user_name"));
                    }
                    if (Response.length() == 1) {
                        Glide.with(mContext).load(Response.getJSONObject(0).getString("img_path"))
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .placeholder(R.drawable.certi01)
                                .into(holder.workimg1);
                        holder.workimg_url02.setVisibility(View.GONE);
                        holder.workimg_url03.setVisibility(View.GONE);
                        holder.workimg_url04.setVisibility(View.GONE);
                    } else if (Response.length() == 2) {
                        Glide.with(mContext).load(Response.getJSONObject(0).getString("img_path"))
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .placeholder(R.drawable.certi01)
                                .into(holder.workimg1);
                        Glide.with(mContext).load(Response.getJSONObject(1).getString("img_path"))
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .placeholder(R.drawable.certi01)
                                .into(holder.workimg2);
                        holder.workimg_url03.setVisibility(View.GONE);
                        holder.workimg_url04.setVisibility(View.GONE);
                    } else if (Response.length() == 3) {
                        Glide.with(mContext).load(Response.getJSONObject(0).getString("img_path"))
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .placeholder(R.drawable.certi01)
                                .into(holder.workimg1);
                        Glide.with(mContext).load(Response.getJSONObject(1).getString("img_path"))
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .placeholder(R.drawable.certi01)
                                .into(holder.workimg2);
                        Glide.with(mContext).load(Response.getJSONObject(2).getString("img_path"))
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .placeholder(R.drawable.certi01)
                                .into(holder.workimg3);
                        holder.workimg_url04.setVisibility(View.GONE);
                    } else if (Response.length() > 3) {
                        Glide.with(mContext).load(Response.getJSONObject(0).getString("img_path"))
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .placeholder(R.drawable.certi01)
                                .into(holder.workimg1);
                        Glide.with(mContext).load(Response.getJSONObject(1).getString("img_path"))
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .placeholder(R.drawable.certi01)
                                .into(holder.workimg2);
                        Glide.with(mContext).load(Response.getJSONObject(2).getString("img_path"))
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .placeholder(R.drawable.certi01)
                                .into(holder.workimg3);
                        int Cnt = Response.length() - 3;
                        holder.other_cnt.setText("+" + Cnt);
                    }
                    String join_membertv = String.valueOf(join_member).replace(",", "/").replace("[", "").replace("]", "");
                    dlog.i("join_member.size() : " + join_member.size());
                    if (join_member.size() > 3) {
                        int Cnt2 = join_member.size() - 3;
                        holder.member_name.setText(join_member.get(0) + "/" + join_member.get(1) + "/" + join_member.get(2) + " " + "외" + Cnt2);
                    } else {
                        holder.member_name.setText(join_membertv);
                    }
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


                dlog.i("work_title : " + item.getTitle());
                dlog.i("item.getStart_time() : " + item.getStart_time());
                dlog.i("item.getEnd_time() : " + item.getEnd_time());
                holder.work_title.setText(item.getTitle());
                if (item.getStart_time().length() > 5) {
                    String date = item.getStart_time().substring(0, 10);
                    String time = item.getStart_time().substring(11, 16);
                    holder.work_start_time.setText(date.replace("-", ".") + " | " + time + " 시작");
                } else {
                    holder.work_start_time.setText(item.getStart_time() + " 시작");
                }

                if (item.getEnd_time().length() > 5) {
                    String date = item.getEnd_time().substring(0, 10);
                    String time = item.getEnd_time().substring(11, 16);
                    holder.work_end_time.setText(date.replace("-", ".") + " | " + time + " 마감");
                } else {
                    holder.work_end_time.setText(item.getEnd_time() + " 마감");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            if(item.getStart_time().length() > 3){
//
//            }else{
//
//            }

//            String startTime = item.getStart_time().substring(0,4) + "." + item.getStart_time().substring(5,7) + "." +
//            holder.work_end_time.setText(item.getStart_time() + " 시작");
//
//            if (item.getApproval_state().equals("3")) {
//                if(item.getComplete_yn().equals("n")){
//                    holder.work_confirm.setText("미완료");
//                }
//                holder.work_confirm.setVisibility(View.VISIBLE);
//            } else {
//                if (item.getApproval_state().equals("2")) {
//                    //반려
//                } else if (item.getApproval_state().equals("1")) {
//                    //승인
//                } else if (item.getApproval_state().equals("0")) {
//                    //결재대기중
//                }
//                if(item.getComplete_yn().equals("n")){
//                    holder.work_confirm.setText("미완료");
//                }
//                holder.work_confirm.setVisibility(View.GONE);
//            }

            holder.list_setting.setOnClickListener(v -> {
                if(kind == 1){
                    shardpref.putString("task_no", item.getId());
                    shardpref.putString("writer_id", item.getWriter_id());
                    shardpref.putString("kind", item.getKind());            // 0:할일, 1:일정
                    shardpref.putString("title", item.getTitle());
                    shardpref.putString("contents", item.getContents());
                    shardpref.putString("complete_kind", item.getComplete_kind());               // 0:체크, 1:사진
                    shardpref.putString("users", user_id.toString());
                    shardpref.putString("usersn", user_name.toString());
                    shardpref.putString("usersimg", user_img_path.toString());
                    shardpref.putString("usersjikgup", user_img_jikgup.toString());
                    shardpref.putString("task_date", item.getTask_date());
                    shardpref.putString("start_time", item.getStart_time());
                    shardpref.putString("end_time", item.getEnd_time());
                    shardpref.putString("sun", item.getSun());
                    shardpref.putString("mon", item.getMon());
                    shardpref.putString("tue", item.getTue());
                    shardpref.putString("wed", item.getWed());
                    shardpref.putString("thu", item.getThu());
                    shardpref.putString("fri", item.getFri());
                    shardpref.putString("sat", item.getSat());
                    shardpref.putString("img_path", item.getImg_path());
                    shardpref.putString("complete_yn", item.getComplete_yn());// y:완료, n:미완료
                    shardpref.putString("incomplete_reason", item.getIncomplete_reason()); // 미완료 사유
                    shardpref.putString("approval_state", item.getApproval_state()); // 결재상태
                    shardpref.putString("overdate", item.getTask_overdate()); // 업무종료날짜
                    shardpref.putString("reject_reason", item.getReject_reason());
                    shardpref.putInt("make_kind", Integer.parseInt(item.getKind()));
                    Intent intent = new Intent(mContext, Tap2OptionActivity.class);
                    intent.putExtra("left_btn_txt", "닫기");
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                }
            });
            //--아이템에 나타나기 애니메이션 줌
            holder.item_total.setTranslationY(150);
            holder.item_total.setAlpha(0.f);
            holder.item_total.animate().translationY(0).alpha(1.f)
                    .setStartDelay(delayEnterAnimation ? 20 * (position) : 0) // position 마다 시간차를 조금 주고..
                    .setInterpolator(new DecelerateInterpolator(2.f))
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animationsLocked = true; // 진입시에만 animation 하도록 하기 위함
                        }
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

        LinearLayout item_total, member_img_array;
        TextView work_title, work_start_time, work_end_time, member_name;
        RecyclerView member_list;
        RelativeLayout list_setting;

        CardView workimg_url01, workimg_url02, workimg_url03, workimg_url04;
        ImageView workimg1, workimg2, workimg3;
        TextView other_cnt;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            item_total = itemView.findViewById(R.id.item_total);
            member_img_array = itemView.findViewById(R.id.member_img_array);

            work_title = itemView.findViewById(R.id.work_title);
            work_start_time = itemView.findViewById(R.id.work_start_time);
            work_end_time = itemView.findViewById(R.id.work_end_time);
            member_name = itemView.findViewById(R.id.member_name);

            member_list = itemView.findViewById(R.id.member_list);
            list_setting = itemView.findViewById(R.id.list_setting);

            workimg_url01 = itemView.findViewById(R.id.workimg_url01);
            workimg_url02 = itemView.findViewById(R.id.workimg_url02);
            workimg_url03 = itemView.findViewById(R.id.workimg_url03);
            workimg_url04 = itemView.findViewById(R.id.workimg_url04);

            workimg1 = itemView.findViewById(R.id.workimg1);
            workimg2 = itemView.findViewById(R.id.workimg2);
            workimg3 = itemView.findViewById(R.id.workimg3);
            other_cnt = itemView.findViewById(R.id.other_cnt);

            shardpref = new PreferenceHelper(mContext);
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "0");
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");

            shardpref.remove("task_no");
            shardpref.remove("writer_id");
            shardpref.remove("kind");        // 0:할일, 1:일정
            shardpref.remove("title");
            shardpref.remove("contents");
            shardpref.remove("complete_kind");            // 0:체크, 1:사진
            shardpref.remove("users");
            shardpref.remove("task_date");
            shardpref.remove("start_time");
            shardpref.remove("end_time");
            shardpref.remove("sun");
            shardpref.remove("mon");
            shardpref.remove("tue");
            shardpref.remove("wed");
            shardpref.remove("thu");
            shardpref.remove("fri");
            shardpref.remove("sat");
            shardpref.remove("img_path");
            shardpref.remove("complete_yn");
            shardpref.remove("incomplete_reason");
            shardpref.remove("make_kind");
            shardpref.remove("reject_reason");
            dlog.DlogContext(mContext);
            checkareatf = new boolean[mData.size()];

//            if (allcheck) {
//                checkarea.setBackgroundResource(R.drawable.checkbox_on);
//                Log.i(TAG, "mData.size() : " + mData.size());
//                for (int i = 0; i < mData.size(); i++) {
//                    try {
//                        JSONArray Response = new JSONArray(mData.get(i).getUsers().toString().replace("[[", "[").replace("]]", "]"));
//                        dlog.i("users : " + mData.get(i).getUsers().toString().replace("[[", "[").replace("]]", "]"));
//                        dlog.i("users Response : " + Response.length());
//                        if (Response.length() == 0) {
//                            Log.i(TAG, "GET SIZE : " + Response.length());
//                        } else {
//                            user_id.removeAll(user_id);
//                            for (int a = 0; a < Response.length(); a++) {
//                                JSONObject jsonObject = Response.getJSONObject(a);
//                                user_id.add(jsonObject.getString("user_id"));
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    if(!mData.get(i).getApproval_state().equals("0") || !mData.get(i).getApproval_state().equals("1") || !user_id.contains(USER_INFO_ID)){
//                        checkareatf[i] = true;
//                        checkworkno.add(mData.get(i).getId());
//                    }
//                }
//                Log.i(TAG, "checkworkno : " + checkworkno);
//                shardpref.putString("checkworkno", String.valueOf(checkworkno));
//            } else {
//                checkarea.setBackgroundResource(R.drawable.checkbox_off);
//                Log.i(TAG, "mData.size() : " + mData.size());
//                for (int i = 0; i < mData.size(); i++) {
//                        checkareatf[i] = false;
//                        checkworkno.removeAll(mData.get(i).getUsers());
//                }
//                Log.i(TAG, "checkworkno : " + checkworkno);
//            }

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    TodolistData.TodolistData_list item = mData.get(pos);
                    try {
                        JSONArray Response = new JSONArray(item.getUsers().toString().replace("[[", "[").replace("]]", "]"));
                        dlog.i("users : " + item.getUsers().toString().replace("[[", "[").replace("]]", "]"));
                        dlog.i("users Response : " + Response.length());
                        if (Response.length() == 0) {
                            Log.i(TAG, "GET SIZE : " + Response.length());
                        } else {
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
//                        item.getApproval_state()
                        shardpref.putString("task_no", item.getId());
                        shardpref.putString("writer_id", item.getWriter_id());
                        shardpref.putString("kind", item.getKind());
                        shardpref.putString("title", item.getTitle());
                        shardpref.putString("contents", item.getContents());
                        shardpref.putString("complete_kind", item.getComplete_kind());
                        shardpref.putString("users", user_id.toString());
                        shardpref.putString("usersn", user_name.toString());
                        shardpref.putString("usersimg", user_img_path.toString());
                        shardpref.putString("usersjikgup", user_img_jikgup.toString());
                        shardpref.putString("task_date", item.getTask_date());
                        shardpref.putString("start_time", item.getStart_time());
                        shardpref.putString("end_time", item.getEnd_time());
                        shardpref.putString("sun", item.getSun());
                        shardpref.putString("mon", item.getMon());
                        shardpref.putString("tue", item.getTue());
                        shardpref.putString("wed", item.getWed());
                        shardpref.putString("thu", item.getThu());
                        shardpref.putString("fri", item.getFri());
                        shardpref.putString("sat", item.getSat());
                        shardpref.putString("img_path", item.getImg_path());
                        shardpref.putString("complete_yn", item.getComplete_yn());// y:완료, n:미완료
                        shardpref.putString("incomplete_reason", item.getIncomplete_reason()); // n: 미완료 사요
                        shardpref.putString("approval_state", item.getApproval_state());// 0: 결재대기, 1:승인, 2:반려, 3:결재요청 전
                        shardpref.putString("reject_reason", item.getReject_reason());
                        dlog.i("users : " + user_id.toString());
                        dlog.i("usersn : " + user_name.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    pm.TaskDetail(mContext);
                }
            });
        }
    }

    public void addItem(TodolistData.TodolistData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}
