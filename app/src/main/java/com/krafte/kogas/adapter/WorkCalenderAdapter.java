package com.krafte.kogas.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.kogas.R;
import com.krafte.kogas.data.CalendarSetData;
import com.krafte.kogas.data.CalendarTaskData;
import com.krafte.kogas.data.WorkCalenderData;
import com.krafte.kogas.util.DBConnection;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PageMoveClass;
import com.krafte.kogas.util.PreferenceHelper;
import com.krafte.kogas.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class WorkCalenderAdapter extends RecyclerView.Adapter<WorkCalenderAdapter.ViewHolder> {
    private static final String TAG = "WorkCalenderAdapter";
    private ArrayList<WorkCalenderData.WorkCalenderData_list> mData = null;
    private ArrayList<CalendarSetData.CalendarSetData_list> mData2 = null;
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

    TasklistAdapter mAdapter1;
    TasklistAdapter mAdapter2;
    TasklistAdapter mAdapter3;
    TasklistAdapter mAdapter4;
    TasklistAdapter mAdapter5;
    TasklistAdapter mAdapter6;
    TasklistAdapter mAdapter7;
    ArrayList<CalendarTaskData.CalendarTaskData_list> caltaskdata1;
    ArrayList<CalendarTaskData.CalendarTaskData_list> caltaskdata2;
    ArrayList<CalendarTaskData.CalendarTaskData_list> caltaskdata3;
    ArrayList<CalendarTaskData.CalendarTaskData_list> caltaskdata4;
    ArrayList<CalendarTaskData.CalendarTaskData_list> caltaskdata5;
    ArrayList<CalendarTaskData.CalendarTaskData_list> caltaskdata6;
    ArrayList<CalendarTaskData.CalendarTaskData_list> caltaskdata7;

    int cnt = 0;
    int totallength = 0;
    String str = "";

    public WorkCalenderAdapter(Context context, ArrayList<WorkCalenderData.WorkCalenderData_list> data, ArrayList<CalendarSetData.CalendarSetData_list> data2
            , String place_id, String write_id, String selected_date) {
        this.mData = data;
        this.mData2 = data2;
        this.mContext = context;
        this.place_id = place_id;
        this.write_id = write_id;
        this.selected_date = selected_date;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.work_calender_item, parent, false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint({"LongLogTag", "SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

            // -- 일요일
            holder.sun.setText(item.getSun().equals("null") ? "" : item.getSun());
            try {
                for (int sun = 0; sun < mData2.size(); sun++) {
                    if (item.getSun().equals(mData2.get(sun).getDay().length() == 1?"0"+mData2.get(sun).getDay():mData2.get(sun).getDay())) {
                        dlog.i("mdata :" + mData2.get(sun).getTask().toString().replace("[[", "[").replace("]]", "]"));
                        JSONArray Response = new JSONArray(mData2.get(sun).getTask().toString().replace("[[", "[").replace("]]", "]"));
                        caltaskdata1 = new ArrayList<>();
                        mAdapter1 = new TasklistAdapter(mContext, caltaskdata1);
                        holder.sunlist.setAdapter(mAdapter1);
                        holder.sunlist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
                        for (int i3 = 0; i3 < Response.length(); i3++) {
                            JSONObject jsonObject = Response.getJSONObject(i3);
                            mAdapter1.addItem(new CalendarTaskData.CalendarTaskData_list(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("kind"),
                                    jsonObject.getString("title"),
                                    jsonObject.getString("contents"),
                                    jsonObject.getString("complete_kind"),
                                    Collections.singletonList(jsonObject.getString("users"))
                            ));
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
                            JSONArray Response = new JSONArray(mData2.get(m).getTask().toString().replace("[[", "[").replace("]]", "]"));
                            caltaskdata2 = new ArrayList<>();
                            mAdapter2 = new TasklistAdapter(mContext, caltaskdata2);
                            holder.monlist.setAdapter(mAdapter2);
                            holder.monlist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
                            for (int i3 = 0; i3 < Response.length(); i3++) {
                                JSONObject jsonObject = Response.getJSONObject(i3);
                                mAdapter2.addItem(new CalendarTaskData.CalendarTaskData_list(
                                        jsonObject.getString("id"),
                                        jsonObject.getString("kind"),
                                        jsonObject.getString("title"),
                                        jsonObject.getString("contents"),
                                        jsonObject.getString("complete_kind"),
                                        Collections.singletonList(jsonObject.getString("users"))
                                ));
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
                        JSONArray Response = new JSONArray(mData2.get(tue).getTask().toString().replace("[[", "[").replace("]]", "]"));
                        caltaskdata3 = new ArrayList<>();
                        mAdapter3 = new TasklistAdapter(mContext, caltaskdata3);
                        holder.tuelist.setAdapter(mAdapter3);
                        holder.tuelist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
                        for (int i3 = 0; i3 < Response.length(); i3++) {
                            JSONObject jsonObject = Response.getJSONObject(i3);
                            mAdapter3.addItem(new CalendarTaskData.CalendarTaskData_list(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("kind"),
                                    jsonObject.getString("title"),
                                    jsonObject.getString("contents"),
                                    jsonObject.getString("complete_kind"),
                                    Collections.singletonList(jsonObject.getString("users"))
                            ));
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
                        JSONArray Response = new JSONArray(mData2.get(wed).getTask().toString().replace("[[", "[").replace("]]", "]"));
                        caltaskdata4 = new ArrayList<>();
                        mAdapter4 = new TasklistAdapter(mContext, caltaskdata4);
                        holder.wedlist.setAdapter(mAdapter4);
                        holder.wedlist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
                        for (int i3 = 0; i3 < Response.length(); i3++) {
                            JSONObject jsonObject = Response.getJSONObject(i3);
                            mAdapter4.addItem(new CalendarTaskData.CalendarTaskData_list(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("kind"),
                                    jsonObject.getString("title"),
                                    jsonObject.getString("contents"),
                                    jsonObject.getString("complete_kind"),
                                    Collections.singletonList(jsonObject.getString("users"))
                            ));
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
                        JSONArray Response = new JSONArray(mData2.get(thu).getTask().toString().replace("[[", "[").replace("]]", "]"));
                        caltaskdata5 = new ArrayList<>();
                        mAdapter5 = new TasklistAdapter(mContext, caltaskdata5);
                        holder.thulist.setAdapter(mAdapter5);
                        holder.thulist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
                        for (int i3 = 0; i3 < Response.length(); i3++) {
                            JSONObject jsonObject = Response.getJSONObject(i3);
                            mAdapter5.addItem(new CalendarTaskData.CalendarTaskData_list(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("kind"),
                                    jsonObject.getString("title"),
                                    jsonObject.getString("contents"),
                                    jsonObject.getString("complete_kind"),
                                    Collections.singletonList(jsonObject.getString("users"))
                            ));
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
                        JSONArray Response = new JSONArray(mData2.get(fri).getTask().toString().replace("[[", "[").replace("]]", "]"));
                        caltaskdata6 = new ArrayList<>();
                        mAdapter6 = new TasklistAdapter(mContext, caltaskdata6);
                        holder.frilist.setAdapter(mAdapter6);
                        holder.frilist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
                        for (int i3 = 0; i3 < Response.length(); i3++) {
                            JSONObject jsonObject = Response.getJSONObject(i3);
                            mAdapter6.addItem(new CalendarTaskData.CalendarTaskData_list(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("kind"),
                                    jsonObject.getString("title"),
                                    jsonObject.getString("contents"),
                                    jsonObject.getString("complete_kind"),
                                    Collections.singletonList(jsonObject.getString("users"))
                            ));
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
                        JSONArray Response = new JSONArray(mData2.get(sat).getTask().toString().replace("[[", "[").replace("]]", "]"));
                        caltaskdata7 = new ArrayList<>();
                        mAdapter7 = new TasklistAdapter(mContext, caltaskdata7);
                        holder.satlist.setAdapter(mAdapter7);
                        holder.satlist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
                        for (int i3 = 0; i3 < Response.length(); i3++) {
                            JSONObject jsonObject = Response.getJSONObject(i3);
                            mAdapter7.addItem(new CalendarTaskData.CalendarTaskData_list(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("kind"),
                                    jsonObject.getString("title"),
                                    jsonObject.getString("contents"),
                                    jsonObject.getString("complete_kind"),
                                    Collections.singletonList(jsonObject.getString("users"))
                            ));
                        }
                    }
                }
            } catch (Exception e) {
                dlog.i("Exception sat :" + e);
            }

            //--기본세팅  END
            holder.sun_box.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(v, position, item.getSun(), "일", item.getYm() + "-" + item.getSun());
                }
            });

            holder.mon_box.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(v, position, item.getMon(), "월", item.getYm() + "-" + item.getMon());
                }
            });
            holder.tue_box.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(v, position, item.getTue(), "화", item.getYm() + "-" + item.getTue());
                }
            });
            holder.wed_box.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(v, position, item.getWed(), "수", item.getYm() + "-" + item.getWed());
                }
            });
            holder.thu_box.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(v, position, item.getThu(), "목", item.getYm() + "-" + item.getThu());
                }
            });
            holder.fri_box.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(v, position, item.getFri(), "금", item.getYm() + "-" + item.getFri());
                }
            });
            holder.sat_box.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(v, position, item.getSat(), "토", item.getYm() + "-" + item.getSat());
                }
            });
        } catch (Exception e) {
            dlog.i("Exception onBindViewHolder :" + e);
        }

    } // getItemCount : 전체 데이터의 개수를 리턴

    private void setYoilRound(ViewHolder holder, int i, String setNum) {
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
    private OnItemClickListener mListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position, String data, String yoil, String WorkDay);
    }


}

