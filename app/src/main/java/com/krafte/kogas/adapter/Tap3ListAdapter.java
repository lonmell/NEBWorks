package com.krafte.kogas.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.kogas.R;
import com.krafte.kogas.data.TodoReuseData;
import com.krafte.kogas.data.UsersData;
import com.krafte.kogas.pop.Tap3OptionActivity;
import com.krafte.kogas.pop.WorkAssigmentContentsPop;
import com.krafte.kogas.util.DateCurrent;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PageMoveClass;
import com.krafte.kogas.util.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

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

        try{
            shardpref = new PreferenceHelper(mContext);

            holder.work_title.setText(item.getTitle());
            holder.work_date.setText("마감시간 : " + item.getEnd_time());
            holder.work_kind.setText(item.getComplete_kind().equals("0")?"체크":"현장사진");

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
                Intent intent = new Intent(mContext, Tap3OptionActivity.class);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            });

            holder.item_total.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, WorkAssigmentContentsPop.class);
                intent.putExtra("data", item.getContents());
                intent.putExtra("task_no",item.getId());
                intent.putExtra("title",item.getTitle());
                intent.putExtra("flag", "tap3");
                intent.putExtra("name", "");
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        RelativeLayout list_setting;
        CardView item_total;
        TextView work_kind;


        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            work_title = itemView.findViewById(R.id.work_title);
            work_date = itemView.findViewById(R.id.work_date);
            list_setting = itemView.findViewById(R.id.list_setting);
            item_total = itemView.findViewById(R.id.item_total);
            work_kind = itemView.findViewById(R.id.work_kind);

            shardpref = new PreferenceHelper(mContext);
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH","0");
            USER_INFO_ID = shardpref.getString("USER_INFO_ID","0");

            dlog.DlogContext(mContext);

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

