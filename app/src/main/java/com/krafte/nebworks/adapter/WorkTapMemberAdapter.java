package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.WorkStatusTapData;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorkTapMemberAdapter extends RecyclerView.Adapter<WorkTapMemberAdapter.ViewHolder> {
    private static final String TAG = "WorkTapMemberAdapter";

    private ArrayList<WorkStatusTapData.WorkStatusTapData_list> mData = null;
    Context mContext;
    PreferenceHelper shardpref;
    FragmentManager fragmentManager;
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    Dlog dlog = new Dlog();

    String place_id = "";
    String place_owner_id = "";
    String state = "";
    String USER_INFO_ID = "";
    String USER_INFO_EMAIL = "";
    String USER_INFO_AUTH = "";
    Activity activity;
    PageMoveClass pm = new PageMoveClass();
    int lastpos = -99;
    List<String> yoil = new ArrayList<>();
    String Tap = "";
    int loadlist = 0;

    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private WorkTapMemberAdapter.OnItemClickListener mListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(WorkTapMemberAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener2 {
        void onItemClick(View v, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener2 = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener2(OnItemClickListener listener2) {
        this.mListener2 = listener2;
    }

    public WorkTapMemberAdapter(Context context, ArrayList<WorkStatusTapData.WorkStatusTapData_list> data, String Tap, FragmentManager fragmentManager) {
        this.mData = data;
        this.mContext = context;
        this.Tap = Tap;
        this.fragmentManager = fragmentManager;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public WorkTapMemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.workpwork_member_item, parent, false);
        WorkTapMemberAdapter.ViewHolder vh = new WorkTapMemberAdapter.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WorkTapMemberAdapter.ViewHolder holder, int position) {
        WorkStatusTapData.WorkStatusTapData_list item = mData.get(position);
        try {
            dlog.DlogContext(mContext);
            holder.name.setText(item.getName());

            Glide.with(mContext).load(item.getImg_path())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(holder.user_thumnail);


            holder.worktime.setText(item.getWorktime().equals("null")?"":item.getWorktime());
            holder.vacationtv.setText(" " + (item.getVaca_accept().equals("휴가승인")?"휴가":""));
            yoil.addAll(Arrays.asList(item.getYoil().split(",")));

            if(item.getVaca_accept().equals("휴가승인")){
                holder.linear02.setVisibility(View.GONE);
            }else {
                if (item.getKind().equals("0")) {
                    state = "근무 중";
                } else if (item.getKind().equals("1")) {
                    state = "퇴근";
                } else if (item.getKind().equals("2") && item.getCommuting().equals("미출근")) {
                    //출근시간 지남
                    state = item.getCommuting();
                    holder.warnning.setVisibility(View.VISIBLE);
                    holder.worktime_title.setTextColor(Color.parseColor("#DD6540"));
                    holder.worktime.setTextColor(Color.parseColor("#DD6540"));
                    holder.worktime.setText(state);
//                holder.worktime.setText(state + "[" + (item.getWorktime().equals("null")?"":item.getWorktime()) + "]");
                }
            }

//            else if (item.getKind().equals("2") && item.getCommuting().equals("")) {
//                //아직 출근시간 아님
////                state = "출근시간 아님";
////                holder.worktime.setText(state + "[" + (item.getWorktime().equals("null")?"":item.getWorktime()) + "]");
//            }
            holder.inTime.setText(editTimeText(item.getIn_time()));
            holder.outTime.setText(editTimeText(item.getOut_time()));

            if(USER_INFO_AUTH.equals("0")){
                holder.list_setting.setVisibility(View.VISIBLE);
                holder.list_setting.setClickable(true);
                holder.list_setting.setEnabled(true);
            }else{
                holder.list_setting.setVisibility(View.INVISIBLE);
                holder.list_setting.setClickable(false);
                holder.list_setting.setEnabled(false);
            }

            holder.list_setting.setOnClickListener(v -> {
                if (mListener != null) {
                    shardpref.putString("item_user_id", item.getUser_id());
                    shardpref.putString("item_user_name", item.getName());
                    mListener.onItemClick(v, position);
                }
            });

//            holder.linear02.setOnClickListener(v -> {
//                if (USER_INFO_AUTH.equals("0")) {
//                    if (mListener2 != null) {
//                        mListener2.onItemClick(v, position);
//                    }
//                }
//            });

//            if (loadlist == 0) {
//                //--아이템에 나타나기 애니메이션 줌
//                holder.item_total.setTranslationY(150);
//                holder.item_total.setAlpha(0.f);
//                holder.item_total.animate().translationY(0).alpha(1.f)
//                        .setStartDelay(delayEnterAnimation ? 20 * (position) : 0) // position 마다 시간차를 조금 주고..
//                        .setInterpolator(new DecelerateInterpolator(2.f))
//                        .setDuration(300)
//                        .setListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                animationsLocked = true; // 진입시에만 animation 하도록 하기 위함
//                            }
//                        });
//                loadlist++;
//            }
        } catch (Exception e) {
            dlog.i("Exception : " + e);
        }

    } // getItemCount : 전체 데이터의 개수를 리턴

    private String editTimeText(String time) {
        if (time.equals("null")) {
            return "";
        } else {
            String[] splitTime = time.split(":");
            return splitTime[0] + ":" + splitTime[1];
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, worktime, inTime, outTime,  worktime_title, vacationtv;
        CardView add_detail, warnning;
        RelativeLayout list_setting, item_total;
        LinearLayout linear01, linear02;
        ImageView user_thumnail;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            item_total      = itemView.findViewById(R.id.item_total);
            name            = itemView.findViewById(R.id.name);
            worktime        = itemView.findViewById(R.id.worktime);
            inTime          = itemView.findViewById(R.id.inTime);
            outTime         = itemView.findViewById(R.id.outTime);
            add_detail      = itemView.findViewById(R.id.add_detail);
            list_setting    = itemView.findViewById(R.id.list_setting);
            linear01        = itemView.findViewById(R.id.linear01);
            linear02        = itemView.findViewById(R.id.linear02);
            user_thumnail   = itemView.findViewById(R.id.user_thumnail);
            worktime_title  = itemView.findViewById(R.id.worktime_title);
            warnning        = itemView.findViewById(R.id.warnning);
            vacationtv      = itemView.findViewById(R.id.vacationtv);

            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            place_id = shardpref.getString("place_id", "");
            place_owner_id  = shardpref.getString("place_owner_id", "");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "");
            USER_INFO_AUTH  = shardpref.getString("USER_INFO_AUTH", "");

            itemView.setOnClickListener(view -> {
                if(USER_INFO_AUTH.isEmpty()) {
                    isAuth();
                } else {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    WorkStatusTapData.WorkStatusTapData_list item = mData.get(pos);
                    if (USER_INFO_AUTH.equals("0")) {
                        if (mListener2 != null) {
                            mListener2.onItemClick(view, pos);
                        }
                    }
//                    shardpref.putString("stub_place_id", item.getPlace_id());
//                    shardpref.putString("stub_user_id", item.getUser_id());
//                    shardpref.putString("stub_user_account", item.getAccount());
//                    shardpref.putString("change_place_name", item.getPlace_name());
//                    pm.MemberDetail(mContext);
                }
            }
            });

        }
    }

    public void addItem(WorkStatusTapData.WorkStatusTapData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void isAuth() {
        Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
        intent.putExtra("flag","더미");
        intent.putExtra("data","먼저 매장등록을 해주세요!");
        intent.putExtra("left_btn_txt", "닫기");
        intent.putExtra("right_btn_txt", "매장추가");
        mContext.startActivity(intent);
        activity.overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
}
