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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.CalendarSetStatusData;
import com.krafte.nebworks.data.CalendarTaskStatusData;
import com.krafte.nebworks.data.WorkCalenderData;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class WorkStatusCalenderAdapter extends RecyclerView.Adapter<WorkStatusCalenderAdapter.ViewHolder> {
    private static final String TAG = "WorkStatusCalenderAdapter";
    private ArrayList<WorkCalenderData.WorkCalenderData_list> mData = null;
    private ArrayList<CalendarSetStatusData.CalendarSetStatusData_list> mData2 = null;
    Context mContext;
    private String month;
    private final DateCurrent dc = new DateCurrent();

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

    TasklistAdapter2 mAdapter1;
    TasklistAdapter2 mAdapter2;
    TasklistAdapter2 mAdapter3;
    TasklistAdapter2 mAdapter4;
    TasklistAdapter2 mAdapter5;
    TasklistAdapter2 mAdapter6;
    TasklistAdapter2 mAdapter7;
    ArrayList<CalendarTaskStatusData.CalendarTaskStatusData_list> caltaskdata1;
    ArrayList<CalendarTaskStatusData.CalendarTaskStatusData_list> caltaskdata2;
    ArrayList<CalendarTaskStatusData.CalendarTaskStatusData_list> caltaskdata3;
    ArrayList<CalendarTaskStatusData.CalendarTaskStatusData_list> caltaskdata4;
    ArrayList<CalendarTaskStatusData.CalendarTaskStatusData_list> caltaskdata5;
    ArrayList<CalendarTaskStatusData.CalendarTaskStatusData_list> caltaskdata6;
    ArrayList<CalendarTaskStatusData.CalendarTaskStatusData_list> caltaskdata7;

    int cnt = 0;
    int totallength = 0;
    String str = "";

    public WorkStatusCalenderAdapter(Context context, ArrayList<WorkCalenderData.WorkCalenderData_list> data, ArrayList<CalendarSetStatusData.CalendarSetStatusData_list> data2
            , String place_id, String write_id, String selected_date, String month) {
        this.mData = data;
        this.mData2 = data2;
        this.mContext = context;
        this.place_id = place_id;
        this.write_id = write_id;
        this.selected_date = selected_date;
        this.month = month;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public WorkStatusCalenderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.work_calender_item, parent, false);
        WorkStatusCalenderAdapter.ViewHolder vh = new WorkStatusCalenderAdapter.ViewHolder(view);

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint({"LongLogTag", "SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull WorkStatusCalenderAdapter.ViewHolder holder, int position) {
        WorkCalenderData.WorkCalenderData_list item = mData.get(position);
        try {
            dlog.i("mData2 size :" + mData2.size());

            //--기본세팅 START
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

            if (Objects.equals(month, dc.GET_MONTH)) {
                if (Objects.equals(item.getSun(), dc.GET_DAY)) {
                    holder.sun_layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
                    holder.sun.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                }
                if (Objects.equals(item.getMon(), dc.GET_DAY)) {
                    holder.mon_layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
                    holder.mon.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                }
                if (Objects.equals(item.getTue(), dc.GET_DAY)) {
                    holder.tue_layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
                    holder.tue.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                }
                if (Objects.equals(item.getWed(), dc.GET_DAY)) {
                    holder.wed_layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
                    holder.wed.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                }
                if (Objects.equals(item.getThu(), dc.GET_DAY)) {
                    holder.thu_layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
                    holder.thu.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                }
                if (Objects.equals(item.getFri(), dc.GET_DAY)) {
                    holder.fri_layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
                    holder.fri.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                }
                if (Objects.equals(item.getSat(), dc.GET_DAY)) {
                    holder.sat_layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
                    holder.sat.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                }
            }


            // -- 일요일
            holder.sun.setText(item.getSun().equals("null") ? "" : item.getSun());
            try {
                for (int sun = 0; sun < mData2.size(); sun++) {
                    if (item.getSun().equals(mData2.get(sun).getDay().length() == 1?"0"+mData2.get(sun).getDay():mData2.get(sun).getDay())) {
                        JSONArray Response = new JSONArray(mData2.get(sun).getUsers().toString().replace("[[", "[").replace("]]", "]"));
                        caltaskdata1 = new ArrayList<>();
                        mAdapter1 = new TasklistAdapter2(mContext, caltaskdata1);
                        holder.sunlist.setAdapter(mAdapter1);
                        holder.sunlist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
                        for (int i3 = 0; i3 < Response.length(); i3++) {
                            JSONObject jsonObject = Response.getJSONObject(i3);
                            mAdapter1.addItem(new CalendarTaskStatusData.CalendarTaskStatusData_list(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("user_id"),
                                    jsonObject.getString("user_name"),
                                    jsonObject.getString("img_path"),
                                    jsonObject.getString("jikgup"),
                                    jsonObject.getString("setdate"),
                                    jsonObject.getString("worktime"),
                                    jsonObject.getString("workyoil")
                            ));
                            if(jsonObject.getString("worktime").equals("holiday")){
                                holder.sun.setTextColor(Color.parseColor("#FF687A"));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                dlog.i("Exception sun :" + e);
            }

            // -- 월요일
            holder.mon.setText(item.getMon().equals("null") ? "" : item.getMon());
            try {
                for (int m = 0; m < mData2.size(); m++) {
                    if (item.getMon().equals(mData2.get(m).getDay().length() == 1?"0"+mData2.get(m).getDay():mData2.get(m).getDay())) {
                        JSONArray Response = new JSONArray(mData2.get(m).getUsers().toString().replace("[[", "[").replace("]]", "]"));
                        caltaskdata2 = new ArrayList<>();
                        mAdapter2 = new TasklistAdapter2(mContext, caltaskdata2);
                        holder.monlist.setAdapter(mAdapter2);
                        holder.monlist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
                        for (int i3 = 0; i3 < Response.length(); i3++) {
                            JSONObject jsonObject = Response.getJSONObject(i3);
                            mAdapter2.addItem(new CalendarTaskStatusData.CalendarTaskStatusData_list(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("user_id"),
                                    jsonObject.getString("user_name"),
                                    jsonObject.getString("img_path"),
                                    jsonObject.getString("jikgup"),
                                    jsonObject.getString("setdate"),
                                    jsonObject.getString("worktime"),
                                    jsonObject.getString("workyoil")
                            ));
                            if(jsonObject.getString("worktime").equals("holiday")){
                                holder.mon.setTextColor(Color.parseColor("#FF687A"));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                dlog.i("Exception mon :" + e);
            }


            // -- 화요일
            holder.tue.setText(item.getTue().equals("null") ? "" : item.getTue());
            try {
                for (int tue = 0; tue < mData2.size(); tue++) {
                    if (item.getTue().equals(mData2.get(tue).getDay().length() == 1?"0"+mData2.get(tue).getDay():mData2.get(tue).getDay())) {
                        JSONArray Response = new JSONArray(mData2.get(tue).getUsers().toString().replace("[[", "[").replace("]]", "]"));
                        caltaskdata3 = new ArrayList<>();
                        mAdapter3 = new TasklistAdapter2(mContext, caltaskdata3);
                        holder.tuelist.setAdapter(mAdapter3);
                        holder.tuelist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
                        for (int i3 = 0; i3 < Response.length(); i3++) {
                            JSONObject jsonObject = Response.getJSONObject(i3);
                            mAdapter3.addItem(new CalendarTaskStatusData.CalendarTaskStatusData_list(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("user_id"),
                                    jsonObject.getString("user_name"),
                                    jsonObject.getString("img_path"),
                                    jsonObject.getString("jikgup"),
                                    jsonObject.getString("setdate"),
                                    jsonObject.getString("worktime"),
                                    jsonObject.getString("workyoil")
                            ));
                            if(jsonObject.getString("worktime").equals("holiday")){
                                holder.tue.setTextColor(Color.parseColor("#FF687A"));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                dlog.i("Exception tue :" + e);
            }

            // -- 수요일
            holder.wed.setText(item.getWed().equals("null") ? "" : item.getWed());
            try {
                for (int wed = 0; wed < mData2.size(); wed++) {
                    if (item.getWed().equals(mData2.get(wed).getDay().length() == 1?"0"+mData2.get(wed).getDay():mData2.get(wed).getDay())) {
                        JSONArray Response = new JSONArray(mData2.get(wed).getUsers().toString().replace("[[", "[").replace("]]", "]"));
                        caltaskdata4 = new ArrayList<>();
                        mAdapter4 = new TasklistAdapter2(mContext, caltaskdata4);
                        holder.wedlist.setAdapter(mAdapter4);
                        holder.wedlist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
                        for (int i3 = 0; i3 < Response.length(); i3++) {
                            JSONObject jsonObject = Response.getJSONObject(i3);
                            mAdapter4.addItem(new CalendarTaskStatusData.CalendarTaskStatusData_list(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("user_id"),
                                    jsonObject.getString("user_name"),
                                    jsonObject.getString("img_path"),
                                    jsonObject.getString("jikgup"),
                                    jsonObject.getString("setdate"),
                                    jsonObject.getString("worktime"),
                                    jsonObject.getString("workyoil")
                            ));
                            if(jsonObject.getString("worktime").equals("holiday")){
                                holder.wed.setTextColor(Color.parseColor("#FF687A"));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                dlog.i("Exception wed :" + e);
            }

            // -- 목요일
            holder.thu.setText(item.getThu().equals("null") ? "" : item.getThu());
            try {
                for (int thu = 0; thu < mData2.size(); thu++) {
                    if (item.getThu().equals(mData2.get(thu).getDay().length() == 1?"0"+mData2.get(thu).getDay():mData2.get(thu).getDay())) {
                        JSONArray Response = new JSONArray(mData2.get(thu).getUsers().toString().replace("[[", "[").replace("]]", "]"));
                        caltaskdata5 = new ArrayList<>();
                        mAdapter5 = new TasklistAdapter2(mContext, caltaskdata5);
                        holder.thulist.setAdapter(mAdapter5);
                        holder.thulist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
                        for (int i3 = 0; i3 < Response.length(); i3++) {
                            JSONObject jsonObject = Response.getJSONObject(i3);
                            mAdapter5.addItem(new CalendarTaskStatusData.CalendarTaskStatusData_list(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("user_id"),
                                    jsonObject.getString("user_name"),
                                    jsonObject.getString("img_path"),
                                    jsonObject.getString("jikgup"),
                                    jsonObject.getString("setdate"),
                                    jsonObject.getString("worktime"),
                                    jsonObject.getString("workyoil")
                            ));
                            if(jsonObject.getString("worktime").equals("holiday")){
                                holder.thu.setTextColor(Color.parseColor("#FF687A"));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                dlog.i("Exception thu :" + e);
            }

            // --금요일
            holder.fri.setText(item.getFri().equals("null") ? "" : item.getFri());
            try {
                for (int fri = 0; fri < mData2.size(); fri++) {
                    if (item.getFri().equals(mData2.get(fri).getDay().length() == 1?"0"+mData2.get(fri).getDay():mData2.get(fri).getDay())) {
                        JSONArray Response = new JSONArray(mData2.get(fri).getUsers().toString().replace("[[", "[").replace("]]", "]"));
                        caltaskdata6 = new ArrayList<>();
                        mAdapter6 = new TasklistAdapter2(mContext, caltaskdata6);
                        holder.frilist.setAdapter(mAdapter6);
                        holder.frilist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
                        for (int i3 = 0; i3 < Response.length(); i3++) {
                            JSONObject jsonObject = Response.getJSONObject(i3);
                            mAdapter6.addItem(new CalendarTaskStatusData.CalendarTaskStatusData_list(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("user_id"),
                                    jsonObject.getString("user_name"),
                                    jsonObject.getString("img_path"),
                                    jsonObject.getString("jikgup"),
                                    jsonObject.getString("setdate"),
                                    jsonObject.getString("worktime"),
                                    jsonObject.getString("workyoil")
                            ));
                            if(jsonObject.getString("worktime").equals("holiday")){
                                holder.fri.setTextColor(Color.parseColor("#FF687A"));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                dlog.i("Exception fri:" + e);
            }

            // --토요일
            holder.sat.setText(item.getSat().equals("null") ? "" : item.getSat());
            try {
                for (int sat = 0; sat < mData2.size(); sat++) {
                    if (item.getSat().equals(mData2.get(sat).getDay().length() == 1?"0"+mData2.get(sat).getDay():mData2.get(sat).getDay())) {
                        JSONArray Response = new JSONArray(mData2.get(sat).getUsers().toString().replace("[[", "[").replace("]]", "]"));
                        caltaskdata7 = new ArrayList<>();
                        mAdapter7 = new TasklistAdapter2(mContext, caltaskdata7);
                        holder.satlist.setAdapter(mAdapter7);
                        holder.satlist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
                        for (int i3 = 0; i3 < Response.length(); i3++) {
                            JSONObject jsonObject = Response.getJSONObject(i3);
                            mAdapter7.addItem(new CalendarTaskStatusData.CalendarTaskStatusData_list(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("user_id"),
                                    jsonObject.getString("user_name"),
                                    jsonObject.getString("img_path"),
                                    jsonObject.getString("jikgup"),
                                    jsonObject.getString("setdate"),
                                    jsonObject.getString("worktime"),
                                    jsonObject.getString("workyoil")
                            ));
                            if(jsonObject.getString("worktime").equals("holiday")){
                                holder.sat.setTextColor(Color.parseColor("#FF687A"));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                dlog.i("Exception sat :" + e);
            }

            //--기본세팅  END
            holder.sun_box.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(v, position, item.getSun(), "일", item.getYm().substring(0,4) + "-" + item.getYm().substring(4,6) + "-" + item.getSun());
                }
            });

            holder.mon_box.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(v, position, item.getMon(), "월", item.getYm().substring(0,4) + "-" + item.getYm().substring(4,6) + "-" + item.getMon());
                }
            });
            holder.tue_box.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(v, position, item.getTue(), "화", item.getYm().substring(0,4) + "-" + item.getYm().substring(4,6) + "-" + item.getTue());
                }
            });
            holder.wed_box.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(v, position, item.getWed(), "수", item.getYm().substring(0,4) + "-" + item.getYm().substring(4,6) + "-" + item.getWed());
                }
            });
            holder.thu_box.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(v, position, item.getThu(), "목", item.getYm().substring(0,4) + "-" + item.getYm().substring(4,6) + "-" + item.getThu());
                }
            });
            holder.fri_box.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(v, position, item.getFri(), "금", item.getYm().substring(0,4) + "-" + item.getYm().substring(4,6) + "-" + item.getFri());
                }
            });
            holder.sat_box.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(v, position, item.getSat(), "토", item.getYm().substring(0,4) + "-" + item.getYm().substring(4,6) + "-" + item.getSat());
                }
            });
        } catch (Exception e) {
            dlog.i("Exception onBindViewHolder :" + e);
        }

    } // getItemCount : 전체 데이터의 개수를 리턴

    private void setYoilRound(WorkStatusCalenderAdapter.ViewHolder holder, int i, String setNum) {
        if (i == 1) {
            holder.sun_box.setBackgroundResource(R.drawable.loginbox_round);
        } else if (i == 2) {
            holder.mon_box.setBackgroundResource(R.drawable.default_gray_round);
        } else if (i == 3) {
            holder.tue_box.setBackgroundResource(R.drawable.default_gray_round);
        } else if (i == 4) {
            holder.wed_box.setBackgroundResource(R.drawable.default_gray_round);
        } else if (i == 5) {
            holder.thu_box.setBackgroundResource(R.drawable.default_gray_round);
        } else if (i == 6) {
            holder.fri_box.setBackgroundResource(R.drawable.default_gray_round);
        } else if (i == 7) {
            holder.sat_box.setBackgroundResource(R.drawable.default_gray_round);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item_total;
        TextView sun, mon, tue, wed, thu, fri, sat;
        LinearLayout sun_box, mon_box, tue_box, wed_box, thu_box, fri_box, sat_box, yoil_area;
        RelativeLayout sun_layout, mon_layout, tue_layout, wed_layout, thu_layout, fri_layout, sat_layout;
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

            sun_layout = itemView.findViewById(R.id.sun_layout);
            mon_layout = itemView.findViewById(R.id.mon_layout);
            tue_layout = itemView.findViewById(R.id.tue_layout);
            wed_layout = itemView.findViewById(R.id.wed_layout);
            thu_layout = itemView.findViewById(R.id.thu_layout);
            fri_layout = itemView.findViewById(R.id.fri_layout);
            sat_layout = itemView.findViewById(R.id.sat_layout);

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
    private WorkStatusCalenderAdapter.OnItemClickListener mListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(WorkStatusCalenderAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position, String data, String yoil, String WorkDay);
    }


}
