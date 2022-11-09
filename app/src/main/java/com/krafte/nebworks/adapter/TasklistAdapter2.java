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
import com.krafte.nebworks.data.CalendarTaskStatusData;
import com.krafte.nebworks.util.Dlog;

import java.util.ArrayList;

public class TasklistAdapter2 extends RecyclerView.Adapter<TasklistAdapter2.ViewHolder> {
    private static final String TAG = "TasklistAdapter2";
    private ArrayList<CalendarTaskStatusData.CalendarTaskStatusData_list> mData = null;
    Context mContext;
    Activity activity;
    Dlog dlog = new Dlog();
    String Color01 = "";
    String Color02 = "";
    String Color03 = "";
    String Color04 = "";
    String Color05 = "";

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private TasklistAdapter2.OnItemClickListener mListener = null;

    public void setOnItemClickListener(TasklistAdapter2.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public TasklistAdapter2(Context context, ArrayList<CalendarTaskStatusData.CalendarTaskStatusData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public TasklistAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.task_listitem2, parent, false);
        TasklistAdapter2.ViewHolder vh = new TasklistAdapter2.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TasklistAdapter2.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CalendarTaskStatusData.CalendarTaskStatusData_list item = mData.get(position);

//        dlog.i("mData size :" + mData.size());
//        dlog.i("mData title :" + item.getTitle());
//        dlog.i("mData contents :" + item.getContents());
        try{

            dlog.i("mData getKind 0:" + mData.get(0).getUser_name());
            holder.task_tv1.setText(mData.get(0).getUser_name());
            switch (mData.get(0).getWorktime()) {
                case "오전":
                    Color01 = "#68B0FF";
                    break;
                case "주간":
                    Color01 = "#44F905";
                    break;
                case "야간":
                    Color01 = "#1D1D1D";
                    break;
                case "주말":
                    Color01 = "#FF687A";
                    break;
                default:
                    Color01 = "#696969";
                    break;
            }
            holder.task_tv1.setBackgroundColor(Color.parseColor(Color01));

            dlog.i("mData getKind 1:" + mData.get(1).getUser_name());
            holder.task_tv2.setText(mData.get(1).getUser_name());
            switch (mData.get(1).getWorktime()) {
                case "오전":
                    Color02 = "#68B0FF";
                    break;
                case "주간":
                    Color02 = "#44F905";
                    break;
                case "야간":
                    Color02 = "#1D1D1D";
                    break;
                case "주말":
                    Color02 = "#FF687A";
                    break;
                default:
                    Color02 = "#696969";
                    break;
            }
            holder.task_tv2.setBackgroundColor(Color.parseColor(Color02));

            dlog.i("mData getKind 2:" + mData.get(2).getUser_name());
            holder.task_tv3.setText(mData.get(2).getUser_name());
            switch (mData.get(2).getWorktime()) {
                case "오전":
                    Color03 = "#68B0FF";
                    break;
                case "주간":
                    Color03 = "#44F905";
                    break;
                case "야간":
                    Color03 = "#1D1D1D";
                    break;
                case "주말":
                    Color03 = "#FF687A";
                    break;
                default:
                    Color03 = "#696969";
                    break;
            }
            holder.task_tv3.setBackgroundColor(Color.parseColor(Color03));

            dlog.i("mData getKind 3:" + mData.get(3).getUser_name());
            holder.task_tv4.setText(mData.get(3).getUser_name());
            switch (mData.get(3).getWorktime()) {
                case "오전":
                    Color04 = "#68B0FF";
                    break;
                case "주간":
                    Color04 = "#44F905";
                    break;
                case "야간":
                    Color04 = "#1D1D1D";
                    break;
                case "주말":
                    Color04 = "#FF687A";
                    break;
                default:
                    Color04 = "#696969";
                    break;
            }
            holder.task_tv4.setBackgroundColor(Color.parseColor(Color04));

            dlog.i("mData getKind 4:" + mData.get(4).getUser_name());
            holder.task_tv5.setText(mData.get(4).getUser_name());
            switch (mData.get(4).getWorktime()) {
                case "오전":
                    Color05 = "#68B0FF";
                    break;
                case "주간":
                    Color05 = "#44F905";
                    break;
                case "야간":
                    Color05 = "#1D1D1D";
                    break;
                case "주말":
                    Color05 = "#FF687A";
                    break;
                default:
                    Color05 = "#696969";
                    break;
            }
            holder.task_tv5.setBackgroundColor(Color.parseColor(Color05));

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

    public void addItem(CalendarTaskStatusData.CalendarTaskStatusData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
