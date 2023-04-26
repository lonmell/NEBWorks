package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.TodoReuseData;
import com.krafte.nebworks.data.UsersData;
import com.krafte.nebworks.pop.Tap3OptionActivity;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Tap3ListAdapter extends RecyclerView.Adapter<Tap3ListAdapter.ViewHolder> {
    private static final String TAG = "Tap3ListAdapter";
    private ArrayList<TodoReuseData.TodoReuseData_list> mData = null;
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


    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public Tap3ListAdapter(Context context, ArrayList<TodoReuseData.TodoReuseData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.tap3listitem, parent, false);
        ViewHolder vh = new ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TodoReuseData.TodoReuseData_list item = mData.get(position);
        SimpleDateFormat f = new SimpleDateFormat("HH:mm", Locale.KOREA);
        try{
            String diff_time_get = "";
            holder.work_title.setText(item.getTitle());
            dlog.i("휴게시간 계산");
            //휴게시간 계산
            Date d3 = f.parse(item.getEnd_time());
            Date d4 = f.parse(item.getStart_time());

            long diff2 = d3.getTime() - d4.getTime();
            dlog.i("diff : " + diff2);
            long min2 = diff2 / 60000;

            long getH2 = min2 / 60;
            long getM2 = min2 % 60;
            if (getM2 != 0) {
                diff_time_get = getH2 + "시간 " + getM2 + "분";
            } else if (getH2 == 0) {
                diff_time_get = getM2 + "분";
            } else {
                diff_time_get = getH2 + "시간";
            }

            holder.diff_time.setText(diff_time_get);
            holder.task_kind_state.setText(item.getComplete_kind().equals("0")?"체크":"인증사진");

           holder.list_setting.setOnClickListener(v -> {
                shardpref.putString("task_no",item.getId());
                shardpref.putString("writer_id" ,item.getWriter_id());
                shardpref.putString("title" ,item.getTitle());
                shardpref.putString("contents" ,item.getContents());
                shardpref.putString("complete_kind" ,item.getComplete_kind());
                shardpref.putString("start_time" ,item.getStart_time());
                shardpref.putString("end_time" ,item.getEnd_time());
                shardpref.putString("sun" ,item.getSun());
                shardpref.putString("mon" ,item.getMon());
                shardpref.putString("tue" ,item.getTue());
                shardpref.putString("wed" ,item.getWed());
                shardpref.putString("thu" ,item.getThu());
                shardpref.putString("fri" ,item.getFri());
                shardpref.putString("sat" ,item.getSat());
                shardpref.putString("overdate" ,item.getTask_overdate());
                Intent intent = new Intent(mContext, Tap3OptionActivity.class);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            });

//            holder.item_total.setOnClickListener(v -> {
//                Intent intent = new Intent(mContext, WorkAssigmentContentsPop.class);
//                intent.putExtra("data", item.getContents());
//                intent.putExtra("task_no",item.getId());
//                intent.putExtra("title",item.getTitle());
//                intent.putExtra("flag", "tap3");
//                intent.putExtra("name", "");
//                mContext.startActivity(intent);
//                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            });
            holder.addtask_btn.setOnClickListener(v -> {
                shardpref.putString("writer_id" ,item.getWriter_id());
                shardpref.putString("title" ,item.getTitle());
                shardpref.putString("contents" ,item.getContents());
                shardpref.putString("complete_kind" ,item.getComplete_kind());
                shardpref.putString("start_time" ,item.getStart_time());
                shardpref.putString("end_time" ,item.getEnd_time());
                shardpref.putString("sun" ,item.getSun());
                shardpref.putString("mon" ,item.getMon());
                shardpref.putString("tue" ,item.getTue());
                shardpref.putString("wed" ,item.getWed());
                shardpref.putString("thu" ,item.getThu());
                shardpref.putString("fri" ,item.getFri());
                shardpref.putString("sat" ,item.getSat());
                shardpref.putString("overdate" ,item.getTask_overdate());
                shardpref.putString("return_page","task_reuse");
                shardpref.putInt("make_kind",1);
                pm.addWorkGo(mContext);
            });

//            //--아이템에 나타나기 애니메이션 줌
//            holder.item_total.setTranslationY(150);
//            holder.item_total.setAlpha(0.f);
//            holder.item_total.animate().translationY(0).alpha(1.f)
//                    .setStartDelay(delayEnterAnimation ? 20 * (position) : 0) // position 마다 시간차를 조금 주고..
//                    .setInterpolator(new DecelerateInterpolator(2.f))
//                    .setDuration(300)
//                    .setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            animationsLocked = true; // 진입시에만 animation 하도록 하기 위함
//                        }
//                    });
        }catch (Exception e){
            dlog.i("Exception : " + e);
        }
    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView work_title, work_date;
        RelativeLayout list_setting,addtask_btn;
        LinearLayout item_total;
        TextView diff_time,task_kind_state;


        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            work_title      = itemView.findViewById(R.id.work_title);
            work_date       = itemView.findViewById(R.id.work_date);
            list_setting    = itemView.findViewById(R.id.list_setting);
            item_total      = itemView.findViewById(R.id.item_total);
            diff_time       = itemView.findViewById(R.id.diff_time);
            task_kind_state = itemView.findViewById(R.id.task_kind_state);
            addtask_btn     = itemView.findViewById(R.id.addtask_btn);

            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
            USER_INFO_ID = shardpref.getString("USER_INFO_ID","0");



            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    TodoReuseData.TodoReuseData_list item = mData.get(pos);
                }
            });
        }
    }

    public void addItem(TodoReuseData.TodoReuseData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}

