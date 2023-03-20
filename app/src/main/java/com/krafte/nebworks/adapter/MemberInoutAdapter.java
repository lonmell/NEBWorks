package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.WorkGotoListData;
import com.krafte.nebworks.util.DateCurrent;
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
    DateCurrent dc = new DateCurrent();
    String vaca_state = "";
    String hdd_state = "";

    public MemberInoutAdapter(Context context, ArrayList<WorkGotoListData.WorkGotoListData_list> data, String month) {
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
        try {
            if (position == 0) {
                holder.first_pos.setVisibility(View.VISIBLE);
            } else {
                holder.first_pos.setVisibility(View.GONE);
            }
            String today = dc.GET_MONTH + "월 " + dc.GET_DAY + "일";
            String toItemday = month + "월 " + item.getDay() + "일";

            if(today.equals(toItemday)){
                holder.today_area.setCardBackgroundColor(Color.parseColor("#E0EAFB"));
            }
            holder.date.setText(toItemday);

            dlog.i("item.getWorkdiff() : " + item.getWorkdiff());
            dlog.i("item.getState() : " + item.getState());
            vaca_state = item.getVaca_accept().equals("휴가")?"휴가":"";

            if (!item.getWorkdiff().equals("null")) {
                if(vaca_state.equals("")){
                    holder.time.setText(item.getWorkdiff());
                }else{
                    holder.time.setText(vaca_state + "\n" + item.getWorkdiff());
                }
            } else {
                if (item.getState().equals("휴무") || item.getState().equals("공휴일")) {
                    holder.time.setTextColor(Color.parseColor("#6395EC"));
                    holder.time.setText(item.getState());
                } else if (item.getState().equals("결근")) {
                    if(vaca_state.equals("")){
                        holder.time.setTextColor(Color.parseColor("#DD6540"));
                        holder.time.setText(item.getState());
                    }else{
                        holder.time.setTextColor(Color.parseColor("#6395EC"));
                        holder.time.setText(vaca_state);
                    }
                } else {
                    if(item.getSieob1().equals("지각")){
                        if(vaca_state.equals("")){
                            holder.time.setTextColor(Color.parseColor("#DD6540"));
                            holder.time.setText(item.getSieob1());
                        }else{
                            holder.time.setTextColor(Color.parseColor("#6395EC"));
                            holder.time.setText(vaca_state);
                        }
                    }else if(item.getSieob1().equals("조기퇴근")){
                        if(vaca_state.equals("")){
                            holder.time.setTextColor(Color.parseColor("#DD6540"));
                            holder.time.setText(item.getSieob1());
                        }else{
                            holder.time.setTextColor(Color.parseColor("#6395EC"));
                            holder.time.setText(vaca_state);
                        }
                    }else if(item.getSieob1().equals("추가근무/퇴근")){
                        if(vaca_state.equals("")){
                            holder.time.setTextColor(Color.parseColor("#DD6540"));
                            holder.time.setText(item.getSieob1());
                        }else{
                            holder.time.setTextColor(Color.parseColor("#6395EC"));
                            holder.time.setText(vaca_state);
                        }
                    }else{
                        if(vaca_state.equals("휴가")){
                            holder.time.setTextColor(Color.parseColor("#6395EC"));
                        }
                        holder.time.setText(vaca_state);
                    }
                }
            }


            if (!item.getSieob1().equals("null")) {
                holder.in_time.setTextColor(Color.parseColor("#DD6540"));
            } else {
                holder.in_time.setTextColor(Color.parseColor("#6395EC"));
            }
            if (!item.getJongeob1().equals("null")) {
                holder.out_time.setTextColor(Color.parseColor("#DD6540"));
            } else {
                holder.out_time.setTextColor(Color.parseColor("#6395EC"));
            }
            if (!item.getIn_time().equals("null")) {
                holder.in_time.setText(item.getIn_time());
            } else {
                holder.in_time.setText("");
            }
            if (!item.getOut_time().equals("null")) {
                holder.out_time.setText(item.getOut_time());
            } else {
                holder.out_time.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date, time, in_time, out_time;
        RelativeLayout list_setting;
        LinearLayout first_pos;
        CardView today_area;

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
            today_area      = itemView.findViewById(R.id.today_area);

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

