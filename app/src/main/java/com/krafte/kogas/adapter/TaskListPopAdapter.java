package com.krafte.kogas.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.kogas.R;
import com.krafte.kogas.data.TaskPointPopData;
import com.krafte.kogas.util.DateCurrent;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PageMoveClass;
import com.krafte.kogas.util.PreferenceHelper;
import com.krafte.kogas.util.RetrofitConnect;

import java.util.ArrayList;

public class TaskListPopAdapter extends RecyclerView.Adapter<TaskListPopAdapter.ViewHolder> {
    private static final String TAG = "TaskListPopAdapter";
    private ArrayList<TaskPointPopData.TaskPointPopData_list> mData = null;
    Context mContext;
    PageMoveClass pm = new PageMoveClass();
    PreferenceHelper shardpref;
    RetrofitConnect rc = new RetrofitConnect();
    DateCurrent dc = new DateCurrent();
    Activity activity;

    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    int loadlist = 0;
    Dlog dlog = new Dlog();

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private TaskListPopAdapter.OnItemClickListener mListener = null;

    public void setOnItemClickListener(TaskListPopAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public TaskListPopAdapter(Context context, ArrayList<TaskPointPopData.TaskPointPopData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public TaskListPopAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.tasklist_popitem, parent, false);
        TaskListPopAdapter.ViewHolder vh = new TaskListPopAdapter.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TaskListPopAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TaskPointPopData.TaskPointPopData_list item = mData.get(position);

        try{
            if(item.getKind().equals("0")){
                holder.kind_color.setCardBackgroundColor(Color.parseColor("#5B93FF"));
            } else if(item.getKind().equals("1")){
                holder.kind_color.setCardBackgroundColor(Color.parseColor("#FF0000"));
            }

            holder.title_tv.setText(item.getTitle());


            if (loadlist == 0) {
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
                loadlist++;
            }
        }catch (Exception e){
            dlog.i("Exception : " + e);
        }

    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item_total;
        CardView kind_color;
        TextView title_tv;


        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조

            item_total = itemView.findViewById(R.id.item_total);
            kind_color = itemView.findViewById(R.id.kind_color);
            title_tv = itemView.findViewById(R.id.title_tv);

            shardpref = new PreferenceHelper(mContext);

            dlog.DlogContext(mContext);
            dlog.i("mData : " + mData.size());

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    TaskPointPopData.TaskPointPopData_list item = mData.get(pos);


                }
            });
        }
    }

    public void addItem(TaskPointPopData.TaskPointPopData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}
