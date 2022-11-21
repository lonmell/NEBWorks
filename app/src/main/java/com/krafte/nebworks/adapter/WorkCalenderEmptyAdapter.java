package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.WorkCalenderData;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.util.ArrayList;

public class WorkCalenderEmptyAdapter extends RecyclerView.Adapter<WorkCalenderEmptyAdapter.ViewHolder> {
    private static final String TAG = "WorkCalenderEmptyAdapter";
    private ArrayList<WorkCalenderData.WorkCalenderData_list> mData = null;
    Context mContext;

    int user_kind;
    String stateKind = "";
    String TaskKind = "";
    int setKind;

    //Shared
    PreferenceHelper shardpref;
    String USER_INFO_AUTH = "";
    String store_no = "";
    String USER_INFO_ID = "";

    //Other
    Handler mHandler = new Handler(Looper.getMainLooper());
    PageMoveClass pm = new PageMoveClass();
    RetrofitConnect rc = new RetrofitConnect();
    DBConnection dbConnection = new DBConnection();

    Dlog dlog = new Dlog();

    //요일 전역변수
    String setNum = "";
    String getNum = "";
    String select_flag = "-99";
    String place_id = "";
    String write_id = "";
    String selected_date = "";

    int cnt = 0;
    int totallength = 0;
    String str = "";

    String before_num = "0";

    public WorkCalenderEmptyAdapter(Context context, ArrayList<WorkCalenderData.WorkCalenderData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public WorkCalenderEmptyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.work_calenderempty_item, parent, false);
        WorkCalenderEmptyAdapter.ViewHolder vh = new WorkCalenderEmptyAdapter.ViewHolder(view);

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint({"LongLogTag", "SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull WorkCalenderEmptyAdapter.ViewHolder holder, int position) {
        WorkCalenderData.WorkCalenderData_list item = mData.get(position);
        try {

            // -- 날짜가 없는 영역은 아무것도 표시 안함
            dlog.DlogContext(mContext);
            /*
             * 표시하는 종류
             * PW = Private Work - 개인일정
             * SW = Store Work - 매장 업무 일정
             * */
            if (position == 0) {
                holder.yoil_area.setVisibility(View.VISIBLE);
            } else {
                holder.yoil_area.setVisibility(View.GONE);
            }
            // -- 일요일
            holder.sun.setText(item.getSun().equals("null") ? "" : item.getSun());
            // -- 월요일
            holder.mon.setText(item.getMon().equals("null") ? "" : item.getMon());
            // -- 화요일
            holder.tue.setText(item.getTue().equals("null") ? "" : item.getTue());
            // -- 수요일
            holder.wed.setText(item.getWed().equals("null") ? "" : item.getWed());
            // -- 목요일
            holder.thu.setText(item.getThu().equals("null") ? "" : item.getThu());
            // --금요일
            holder.fri.setText(item.getFri().equals("null") ? "" : item.getFri());
            // --토요일
            holder.sat.setText(item.getSat().equals("null") ? "" : item.getSat());

            if(!before_num.isEmpty()){
                holder.sun_box.setBackgroundColor(Color.parseColor(before_num.equals(item.getSun()) ? "#E0EAFB" : "#ffffff"));
                holder.mon_box.setBackgroundColor(Color.parseColor(before_num.equals(item.getMon()) ? "#E0EAFB" : "#ffffff"));
                holder.tue_box.setBackgroundColor(Color.parseColor(before_num.equals(item.getTue()) ? "#E0EAFB" : "#ffffff"));
                holder.wed_box.setBackgroundColor(Color.parseColor(before_num.equals(item.getWed()) ? "#E0EAFB" : "#ffffff"));
                holder.thu_box.setBackgroundColor(Color.parseColor(before_num.equals(item.getThu()) ? "#E0EAFB" : "#ffffff"));
                holder.fri_box.setBackgroundColor(Color.parseColor(before_num.equals(item.getFri()) ? "#E0EAFB" : "#ffffff"));
                holder.sat_box.setBackgroundColor(Color.parseColor(before_num.equals(item.getSat()) ? "#E0EAFB" : "#ffffff"));
            }
            //--기본세팅  END
            holder.sun_box.setOnClickListener(v -> {
                before_num = item.getSun();
                if (mListener != null) {
                    mListener.onItemClick(v, position, item.getSun(), "일", item.getYm() + "-" + item.getSun());
                }
            });

            holder.mon_box.setOnClickListener(v -> {
                before_num = item.getMon();
                if (mListener != null) {
                    mListener.onItemClick(v, position, item.getMon(), "월", item.getYm() + "-" + item.getMon());
                }
            });
            holder.tue_box.setOnClickListener(v -> {
                before_num = item.getTue();
                if (mListener != null) {
                    mListener.onItemClick(v, position, item.getTue(), "화", item.getYm() + "-" + item.getTue());
                }
            });
            holder.wed_box.setOnClickListener(v -> {
                before_num = item.getWed();
                if (mListener != null) {
                    mListener.onItemClick(v, position, item.getWed(), "수", item.getYm() + "-" + item.getWed());
                }
            });
            holder.thu_box.setOnClickListener(v -> {
                before_num = item.getThu();
                if (mListener != null) {
                    mListener.onItemClick(v, position, item.getThu(), "목", item.getYm() + "-" + item.getThu());
                }
            });
            holder.fri_box.setOnClickListener(v -> {
                before_num = item.getFri();
                if (mListener != null) {
                    mListener.onItemClick(v, position, item.getFri(), "금", item.getYm() + "-" + item.getFri());
                }
            });
            holder.sat_box.setOnClickListener(v -> {
                before_num = item.getSat();
                if (mListener != null) {
                    mListener.onItemClick(v, position, item.getSat(), "토", item.getYm() + "-" + item.getSat());
                }
            });
        } catch (Exception e) {
            dlog.i("Exception onBindViewHolder :" + e);
        }

    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item_total;
        TextView sun, mon, tue, wed, thu, fri, sat;
        LinearLayout sun_box, mon_box, tue_box, wed_box, thu_box, fri_box, sat_box, yoil_area;
        //업무표시할 텍스트들
        RecyclerView sunlist, monlist, tuelist, wedlist, thulist, frilist, satlist;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
//            work_state = itemView.findViewById(R.id.work_state);
            sun = itemView.findViewById(R.id.sun);
            mon = itemView.findViewById(R.id.mon);
            tue = itemView.findViewById(R.id.tue);
            wed = itemView.findViewById(R.id.wed);
            thu = itemView.findViewById(R.id.thu);
            fri = itemView.findViewById(R.id.fri);
            sat = itemView.findViewById(R.id.sat);
            yoil_area = itemView.findViewById(R.id.yoil_area);

            sun_box = itemView.findViewById(R.id.sun_box);
            mon_box = itemView.findViewById(R.id.mon_box);
            tue_box = itemView.findViewById(R.id.tue_box);
            wed_box = itemView.findViewById(R.id.wed_box);
            thu_box = itemView.findViewById(R.id.thu_box);
            fri_box = itemView.findViewById(R.id.fri_box);
            sat_box = itemView.findViewById(R.id.sat_box);

            sunlist = itemView.findViewById(R.id.sunlist);
            monlist = itemView.findViewById(R.id.monlist);
            tuelist = itemView.findViewById(R.id.tuelist);
            wedlist = itemView.findViewById(R.id.wedlist);
            thulist = itemView.findViewById(R.id.thulist);
            frilist = itemView.findViewById(R.id.frilist);
            satlist = itemView.findViewById(R.id.satlist);

            shardpref = new PreferenceHelper(mContext);
            dlog.DlogContext(mContext);
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
            store_no = shardpref.getString("store_no", "");
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");


            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    WorkCalenderData.WorkCalenderData_list item = mData.get(pos);
                    dlog.i("POS : " + pos);
                }
            });
        }
    }

    public void addItem(WorkCalenderData.WorkCalenderData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // 리스너 객체 참조를 저장하는 변수
    private WorkCalenderEmptyAdapter.OnItemClickListener mListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(WorkCalenderEmptyAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position, String data, String yoil, String WorkDay);
    }


}
