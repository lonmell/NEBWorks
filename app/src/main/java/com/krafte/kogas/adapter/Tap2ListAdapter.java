package com.krafte.kogas.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.kogas.R;
import com.krafte.kogas.data.TodolistData;
import com.krafte.kogas.data.UsersData;
import com.krafte.kogas.pop.Tap2OptionActivity;
import com.krafte.kogas.util.DateCurrent;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PageMoveClass;
import com.krafte.kogas.util.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
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
    boolean[] checkareatf;
    String[] checkworkno;
    boolean allcheck;


    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    public interface OnItemClickListener {
        void onItemClick(View v, int position, int Tcnt, int Fcnt);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public Tap2ListAdapter(Context context, ArrayList<TodolistData.TodolistData_list> data, FragmentManager fragmentManager, boolean allcheck) {
        this.mData = data;
        this.mContext = context;
        this.allcheck = allcheck;
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
            if (item.getWriter_id().equals(USER_INFO_ID)) {
                holder.list_setting.setVisibility(View.VISIBLE);
            } else {
                holder.list_setting.setVisibility(View.GONE);
            }

            if (item.getComplete_kind().equals("0")) {
                holder.no_img_icon.setVisibility(View.GONE);
            } else {
                holder.no_img_icon.setVisibility(View.VISIBLE);
            }

            holder.check_box.setOnClickListener(v -> {
                if (!checkareatf[position]) {
                    checkworkno[position] = item.getId();
                    checkareatf[position] = true;
                    holder.checkarea.setBackgroundResource(R.drawable.checkbox_on);
                } else {
                    checkworkno[position] = "";
                    checkareatf[position] = false;
                    holder.checkarea.setBackgroundResource(R.drawable.checkbox_off);
                }
//            shardpref.putInt("checkcnt", checkworkno.length);
                shardpref.putString("checkworkno", Arrays.toString(checkworkno));

                int Tcnt = 0;
                int Fcnt = 0;

                for (int a = 0; a < mData.size(); a++) {
                    if (checkareatf[a]) {
                        ++Tcnt;
                    }
                    if (!checkareatf[a]) {
                        ++Fcnt;
                    }
                }
                Log.i(TAG, "Tcnt : " + Tcnt + " / Fcnt : " + Fcnt);
                if (mListener != null) {
                    mListener.onItemClick(v, position, Tcnt, Fcnt);
                }
            });

            if (item.getSun().equals("1")) {
                yoil[0] = "일";
            } else {
                yoil[0] = "";
            }
            if (item.getMon().equals("1")) {
                yoil[1] = "월";
            } else {
                yoil[1] = "";
            }
            if (item.getTue().equals("1")) {
                yoil[2] = "화";
            } else {
                yoil[2] = "";
            }
            if (item.getWed().equals("1")) {
                yoil[3] = "수";
            } else {
                yoil[3] = "";
            }
            if (item.getThu().equals("1")) {
                yoil[4] = "목";
            } else {
                yoil[4] = "";
            }
            if (item.getFri().equals("1")) {
                yoil[5] = "금";
            } else {
                yoil[5] = "";
            }
            if (item.getSat().equals("1")) {
                yoil[6] = "토";
            } else {
                yoil[6] = "";
            }
            int cnt = 0;
            dlog.i("yoil size 배열길이 : " + yoil.length);
            for (int i = 0; i < yoil.length; i++) {
                if (!yoil[i].isEmpty()) {
                    cnt++;
                    dlog.i("yoil[i] : " + yoil[i]);
                    setYoil += yoil[i] + (i < 6 ? "," : "");
                }
            }
            dlog.i("cnt : " + cnt);
            dlog.i("setYoil12 : " + setYoil.replace(",", " ").trim().replace(" ", ","));
            holder.work_yoil.setText(setYoil.replace(",", " ").trim().replace(" ", ","));
            setYoil = "";


            try {
                JSONArray Response = new JSONArray(item.getUsers().toString().replace("[[", "[").replace("]]", "]"));
                dlog.i("users : " + item.getUsers());
                dlog.i("users Response : " + Response.length());

                mList = new ArrayList<>();
                mAdapter = new AdaperInMemberAdapter(mContext, mList);
                holder.member_list.setAdapter(mAdapter);
                holder.member_list.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
                if (Response.length() == 0) {
                    Log.i(TAG, "GET SIZE : " + Response.length());
                } else {
                    if (Response.length() > 3) {
                        for (int i = 0; i < 3; i++) {
                            JSONObject jsonObject = Response.getJSONObject(i);
                            mAdapter.addItem(new UsersData.UsersData_list(
                                    jsonObject.getString("user_id"),
                                    jsonObject.getString("user_name"),
                                    jsonObject.getString("img_path")
                            ));
                        }
                    } else {
                        for (int i = 0; i < Response.length(); i++) {
                            JSONObject jsonObject = Response.getJSONObject(i);
                            mAdapter.addItem(new UsersData.UsersData_list(
                                    jsonObject.getString("user_id"),
                                    jsonObject.getString("user_name"),
                                    jsonObject.getString("img_path")
                            ));
                        }
                    }
                    user_id.removeAll(user_id);
                    for (int i = 0; i < Response.length(); i++) {
                        JSONObject jsonObject = Response.getJSONObject(i);
                        if (!jsonObject.getString("user_name").equals("null")) {
                            user_id.add(jsonObject.getString("user_id"));
                        }
                    }

                    if (user_id.contains(USER_INFO_ID)) {
                        holder.check_box.setVisibility(View.VISIBLE);
                    } else {
                        holder.check_box.setVisibility(View.GONE);
                    }

                    if (Response.length() > 3) {
                        int MemberCnt = Response.length() - 3;
                        holder.plus_add_membercnt.setText("+" + MemberCnt);
                    } else {
                        holder.plus_add_membercnt.setVisibility(View.GONE);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            holder.work_title.setText(item.getTitle());

            int subHourEnd = Integer.parseInt(item.getEnd_time().substring(0, 2));

            if (subHourEnd < 12) {
                AMPM = "AM";
            } else {
                AMPM = "PM";
            }
            holder.work_date.setText("마감시간 : " + item.getEnd_time() + " " + AMPM);

            String tv_complete = "";
            if (item.getComplete_yn().equals("null")) {
                tv_complete = "완료";
            } else {
                tv_complete = item.getComplete_yn().equals("y") ? "완료" : "미완료";
            }
            holder.work_confirm.setText(tv_complete);
            dlog.i("complete_yn : " + tv_complete);

            holder.list_setting.setOnClickListener(v -> {
                shardpref.putString("task_no", item.getId());
                shardpref.putString("writer_id", item.getWriter_id());
                shardpref.putString("kind", item.getKind());            // 0:할일, 1:일정
                shardpref.putString("title", item.getTitle());
                shardpref.putString("contents", item.getContents());
                shardpref.putString("complete_kind", item.getComplete_kind());               // 0:체크, 1:사진
                shardpref.putString("users", user_id.toString());
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
                shardpref.putInt("make_kind", Integer.parseInt(item.getKind()));
                Intent intent = new Intent(mContext, Tap2OptionActivity.class);
                intent.putExtra("left_btn_txt", "닫기");
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            });
//
//        holder.item_total.setOnClickListener(v -> {
//            Intent intent = new Intent(mContext, WorkAssigmentContentsPop.class);
//            intent.putExtra("data", item.getTask_contents());
//            intent.putExtra("task_no", item.getTask_no());
//            intent.putExtra("flag", "tap2");
//            intent.putExtra("name", item.getTask_conduct_name());
//            intent.putExtra("profileimg", item.getUser_thumnail_url());
//            mContext.startActivity(intent);
//            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        });
//
//        holder.work_name.setText(item.getTask_conduct_name());

            //--아이템에 나타나기 애니메이션 줌
//        holder.item_total.setTranslationY(150);
//        holder.item_total.setAlpha(0.f);
//        holder.item_total.animate().translationY(0).alpha(1.f)
//                .setStartDelay(delayEnterAnimation ? 20 * (position) : 0) // position 마다 시간차를 조금 주고..
//                .setInterpolator(new DecelerateInterpolator(2.f))
//                .setDuration(300)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        animationsLocked = true; // 진입시에만 animation 하도록 하기 위함
//                    }
//                });
        } catch (Exception e) {
            dlog.i("Exception : " + e);
        }
    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView work_title, work_date, work_confirm, work_yoil;
        RelativeLayout list_setting, check_box;
        CardView visiable_area;
        RecyclerView member_list;
        TextView plus_add_membercnt;
        LinearLayout item_total;
        ImageView checkarea, no_img_icon;


        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            work_title = itemView.findViewById(R.id.work_title);
            work_date = itemView.findViewById(R.id.work_date);
            list_setting = itemView.findViewById(R.id.list_setting);
            item_total = itemView.findViewById(R.id.item_total);
            work_confirm = itemView.findViewById(R.id.work_confirm);
            work_yoil = itemView.findViewById(R.id.work_yoil);
            member_list = itemView.findViewById(R.id.member_list);
            plus_add_membercnt = itemView.findViewById(R.id.plus_add_membercnt);
            check_box = itemView.findViewById(R.id.check_box);
            checkarea = itemView.findViewById(R.id.checkarea);
            no_img_icon = itemView.findViewById(R.id.no_img_icon);

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

            dlog.DlogContext(mContext);
            checkareatf = new boolean[mData.size()];
            checkworkno = new String[mData.size()];

            Log.i(TAG, "allcheck : " + allcheck);
            if (allcheck) {
                checkarea.setBackgroundResource(R.drawable.checkbox_on);
                Log.i(TAG, "mData.size() : " + mData.size());
                for (int i = 0; i < mData.size(); i++) {
//                    if(mData.get(i).getComplete_yn().equals("y")){
                    checkareatf[i] = true;
                    checkworkno[i] = mData.get(i).getId();
//                    }
                }
                Log.i(TAG, "checkworkno : " + Arrays.toString(checkworkno));
                shardpref.putString("checkworkno", Arrays.toString(checkworkno));
            } else {
                checkarea.setBackgroundResource(R.drawable.checkbox_off);
                Log.i(TAG, "mData.size() : " + mData.size());
                for (int i = 0; i < mData.size(); i++) {
                    checkareatf[i] = false;
                    checkworkno[i] = "";
                }
                Log.i(TAG, "checkworkno : " + Arrays.toString(checkworkno));
            }

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    TodolistData.TodolistData_list item = mData.get(pos);
                    shardpref.putString("task_no", item.getId());
                    shardpref.putString("writer_id", item.getWriter_id());
                    shardpref.putString("kind", item.getKind());            // 0:할일, 1:일정
                    shardpref.putString("title", item.getTitle());
                    shardpref.putString("contents", item.getContents());
                    shardpref.putString("complete_kind", item.getComplete_kind());               // 0:체크, 1:사진
                    shardpref.putString("users", user_id.toString());
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
                    shardpref.putInt("make_kind", Integer.parseInt(item.getKind()));
                    pm.workDetailGo(mContext);
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
