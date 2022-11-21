package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.WorkGotoListData;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.util.ArrayList;

public class MemberInoutAdapter extends RecyclerView.Adapter<MemberInoutAdapter.ViewHolder> {
    private static final String TAG = "MemberInoutAdapter";
    private ArrayList<WorkGotoListData.WorkGotoListData_list> mData = null;
    Context mContext;
    PreferenceHelper shardpref;
    Dlog dlog = new Dlog();
    PageMoveClass pm = new PageMoveClass();
    RetrofitConnect rc = new RetrofitConnect();
    String month = "";

    public MemberInoutAdapter(Context context, ArrayList<WorkGotoListData.WorkGotoListData_list> data,String month) {
        this.mData = data;
        this.mContext = context;
        this.month = month;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public MemberInoutAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.memberinout_item, parent, false);
        MemberInoutAdapter.ViewHolder vh = new MemberInoutAdapter.ViewHolder(view);
        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MemberInoutAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WorkGotoListData.WorkGotoListData_list item = mData.get(position);
        try{
            if(position == 0){
                holder.first_pos.setVisibility(View.VISIBLE);
            }else{
                holder.first_pos.setVisibility(View.GONE);
            }
            holder.date.setText(month + "월 " + item.getDay() + "일");
            holder.time.setText(item.getWorking_time().substring(0,2) + "시간" + item.getWorking_time().substring(3,5) + " 분");
            holder.in_time.setText(item.getIn_time().substring(0,5));
            holder.out_time.setText(item.getOut_time().substring(0,5));
        }catch (Exception e){
            e.printStackTrace();
        }


    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date,time,in_time,out_time;
        RelativeLayout list_setting;
        LinearLayout first_pos;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);

            date            = itemView.findViewById(R.id.date);
            time            = itemView.findViewById(R.id.time);
            in_time         = itemView.findViewById(R.id.in_time);
            out_time        = itemView.findViewById(R.id.out_time);
            list_setting    = itemView.findViewById(R.id.list_setting);
            first_pos       = itemView.findViewById(R.id.first_pos);

            dlog.i("mData size : " + mData.size());
            itemView.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    // 리스너 객체의 메서드 호출.
                    WorkGotoListData.WorkGotoListData_list item = mData.get(pos);

                }
            });
        }
    }

    public void addItem(WorkGotoListData.WorkGotoListData_list data) {
        mData.add(data);
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
}

