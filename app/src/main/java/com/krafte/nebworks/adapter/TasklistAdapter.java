package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.CalendarTaskData;
import com.krafte.nebworks.util.Dlog;

import java.util.ArrayList;

public class TasklistAdapter extends RecyclerView.Adapter<TasklistAdapter.ViewHolder> {
    private static final String TAG = "TasklistAdapter";
    private ArrayList<CalendarTaskData.CalendarTaskData_list> mData = null;
    Context mContext;
    Activity activity;
    Dlog dlog = new Dlog();

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public TasklistAdapter(Context context, ArrayList<CalendarTaskData.CalendarTaskData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public TasklistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.task_listitem, parent, false);
        TasklistAdapter.ViewHolder vh = new TasklistAdapter.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TasklistAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CalendarTaskData.CalendarTaskData_list item = mData.get(position);

//        dlog.i("mData size :" + mData.size());
//        dlog.i("mData title :" + item.getTitle());
//        dlog.i("mData contents :" + item.getContents());
        try{
            if(mData.size() == 1){
                holder.task_tv1.setText(mData.get(0).getTitle());
                holder.task_tv1.setTextColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#ffffff":"#6395EC"));
                holder.task_tv1.setBackgroundColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#6395EC":"#E0EAFB"));
            }else if(mData.size() == 2){
                holder.task_tv1.setText(mData.get(0).getTitle());
                holder.task_tv2.setText(mData.get(1).getTitle());
                holder.task_tv1.setTextColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#ffffff":"#6395EC"));
                holder.task_tv1.setBackgroundColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#6395EC":"#E0EAFB"));
                holder.task_tv2.setTextColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#ffffff":"#6395EC"));
                holder.task_tv2.setBackgroundColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#6395EC":"#E0EAFB"));
            }else if(mData.size() == 3){
                holder.task_tv1.setText(mData.get(0).getTitle());
                holder.task_tv2.setText(mData.get(1).getTitle());
                holder.task_tv3.setText(mData.get(2).getTitle());
                holder.task_tv1.setTextColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#ffffff":"#6395EC"));
                holder.task_tv1.setBackgroundColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#6395EC":"#E0EAFB"));
                holder.task_tv2.setTextColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#ffffff":"#6395EC"));
                holder.task_tv2.setBackgroundColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#6395EC":"#E0EAFB"));
                holder.task_tv3.setTextColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#ffffff":"#6395EC"));
                holder.task_tv3.setBackgroundColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#6395EC":"#E0EAFB"));
            }else if(mData.size() == 4){
                holder.task_tv1.setText(mData.get(0).getTitle());
                holder.task_tv2.setText(mData.get(1).getTitle());
                holder.task_tv3.setText(mData.get(2).getTitle());
                holder.task_tv4.setText(mData.get(3).getTitle());
                holder.task_tv1.setTextColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#ffffff":"#6395EC"));
                holder.task_tv1.setBackgroundColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#6395EC":"#E0EAFB"));
                holder.task_tv2.setTextColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#ffffff":"#6395EC"));
                holder.task_tv2.setBackgroundColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#6395EC":"#E0EAFB"));
                holder.task_tv3.setTextColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#ffffff":"#6395EC"));
                holder.task_tv3.setBackgroundColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#6395EC":"#E0EAFB"));
                holder.task_tv4.setTextColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#ffffff":"#6395EC"));
                holder.task_tv4.setBackgroundColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#6395EC":"#E0EAFB"));
            }else if(mData.size() == 5){
                holder.task_tv1.setText(mData.get(0).getTitle());
                holder.task_tv2.setText(mData.get(1).getTitle());
                holder.task_tv3.setText(mData.get(2).getTitle());
                holder.task_tv4.setText(mData.get(3).getTitle());
                holder.task_tv5.setText(mData.get(4).getTitle());
                holder.task_tv1.setTextColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#ffffff":"#6395EC"));
                holder.task_tv1.setBackgroundColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#6395EC":"#E0EAFB"));
                holder.task_tv2.setTextColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#ffffff":"#6395EC"));
                holder.task_tv2.setBackgroundColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#6395EC":"#E0EAFB"));
                holder.task_tv3.setTextColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#ffffff":"#6395EC"));
                holder.task_tv3.setBackgroundColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#6395EC":"#E0EAFB"));
                holder.task_tv4.setTextColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#ffffff":"#6395EC"));
                holder.task_tv4.setBackgroundColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#6395EC":"#E0EAFB"));
                holder.task_tv5.setTextColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#ffffff":"#6395EC"));
                holder.task_tv5.setBackgroundColor(Color.parseColor(mData.get(0).getKind().equals("0")?"#6395EC":"#E0EAFB"));
            }
        }catch (Exception e){
            dlog.i("Exception :" + e);
        }

    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView task_tv1,task_tv2,task_tv3,task_tv4,task_tv5;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조

            task_tv1 = itemView.findViewById(R.id.task_tv1);
            task_tv2 = itemView.findViewById(R.id.task_tv2);
            task_tv3 = itemView.findViewById(R.id.task_tv3);
            task_tv4 = itemView.findViewById(R.id.task_tv4);
            task_tv5 = itemView.findViewById(R.id.task_tv5);

            dlog.DlogContext(mContext);

        }
    }

    public void addItem(CalendarTaskData.CalendarTaskData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
