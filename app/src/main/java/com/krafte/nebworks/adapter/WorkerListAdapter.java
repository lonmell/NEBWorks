package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.WorkerlistData;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;

public class WorkerListAdapter extends RecyclerView.Adapter<WorkerListAdapter.ViewHolder> {

    private static final String TAG = "WorkerListAdapter";
    private ArrayList<WorkerlistData.WorkerlistData_list> mData = null;
    Context mContext;
    PreferenceHelper shardpref;
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();

    public WorkerListAdapter(Context context, ArrayList<WorkerlistData.WorkerlistData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴



    @NonNull
    @Override
    public WorkerListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.worker_item, parent, false);
        WorkerListAdapter.ViewHolder vh = new WorkerListAdapter.ViewHolder(view);
        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WorkerListAdapter.ViewHolder holder, int position) {
        WorkerlistData.WorkerlistData_list item = mData.get(position);

        try{
            holder.name.setText(item.getUser_name());
            holder.jikgup.setText(item.getJikgup().equals("null")?"미정":item.getJikgup());
            holder.workyoil.setText(item.getWorkyoil().equals("null")?"미정":item.getWorkyoil());
        }catch (Exception e){
            dlog.i("Exception : " + e);
        }


    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView user_img;
        TextView name,jikgup,workyoil,state_tv;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            user_img = itemView.findViewById(R.id.user_img);
            name     = itemView.findViewById(R.id.name);
            jikgup   = itemView.findViewById(R.id.jikgup);
            workyoil = itemView.findViewById(R.id.workyoil);
            state_tv = itemView.findViewById(R.id.state_tv);

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    WorkerlistData.WorkerlistData_list item = mData.get(pos);
                    Log.i("WorkerListAdapter", "pos : " + pos);

//                    pm.EmployerStoreSetting(mContext);
                }
            });

        }
    }

    public void addItem(WorkerlistData.WorkerlistData_list workPlaceListData_list) {
        mData.add(workPlaceListData_list);
    }


}
