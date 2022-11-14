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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.WorkCalenderData;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.util.ArrayList;

public class DatePickerAdapter extends RecyclerView.Adapter<DatePickerAdapter.ViewHolder> {
    private static final String TAG = "DatePickerAdapter";
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
    int lastPos = 0;
    String GetLastDate = "0";

    String yoil = "";
    String date = "";
    String ym = "";
    public DatePickerAdapter(Context context, ArrayList<WorkCalenderData.WorkCalenderData_list> data) {
        this.mContext = context;
        this.mData = data;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public DatePickerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.datepicker_coustom_item, parent, false);
        DatePickerAdapter.ViewHolder vh = new DatePickerAdapter.ViewHolder(view);

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint({"LongLogTag", "SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull DatePickerAdapter.ViewHolder holder, int position) {
        WorkCalenderData.WorkCalenderData_list item = mData.get(position);
        try {
            //--기본세팅 START
            // -- 날짜가 없는 영역은 아무것도 표시 안함
            dlog.DlogContext(mContext);

            if (position == 0) {
                holder.yoil_area.setVisibility(View.VISIBLE);
            } else {
                holder.yoil_area.setVisibility(View.GONE);
            }

            if(GetLastDate.equals(item.getSun())){
                holder.sun_round.setCardBackgroundColor(Color.parseColor("#6395EC"));
                holder.sun.setTextColor(Color.parseColor("#ffffff"));
                yoil = "일";
                date = item.getSun();
                ym = item.getYm() + "-" + item.getSun();
            }else if(GetLastDate.equals(item.getMon())){
                holder.mon_round.setCardBackgroundColor(Color.parseColor("#6395EC"));
                holder.mon.setTextColor(Color.parseColor("#ffffff"));
                yoil = "월";
                date = item.getMon();
                ym = item.getYm() + "-" + item.getMon();
            }else if(GetLastDate.equals(item.getTue())){
                holder.tue_round.setCardBackgroundColor(Color.parseColor("#6395EC"));
                holder.tue.setTextColor(Color.parseColor("#ffffff"));
                yoil = "화";
                date = item.getTue();
                ym = item.getYm() + "-" + item.getTue();
            }else if(GetLastDate.equals(item.getWed())){
                holder.wed_round.setCardBackgroundColor(Color.parseColor("#6395EC"));
                holder.wed.setTextColor(Color.parseColor("#ffffff"));
                yoil = "수";
                date = item.getWed();
                ym = item.getYm() + "-" + item.getWed();
            }else if(GetLastDate.equals(item.getThu())){
                holder.thu_round.setCardBackgroundColor(Color.parseColor("#6395EC"));
                holder.thu.setTextColor(Color.parseColor("#ffffff"));
                yoil = "목";
                date = item.getThu();
                ym = item.getYm() + "-" + item.getThu();
            }else if(GetLastDate.equals(item.getFri())){
                holder.fri_round.setCardBackgroundColor(Color.parseColor("#6395EC"));
                holder.fri.setTextColor(Color.parseColor("#ffffff"));
                yoil = "금";
                date = item.getFri();
                ym = item.getYm() + "-" + item.getFri();
            }else if(GetLastDate.equals(item.getSat())){
                holder.sat_round.setCardBackgroundColor(Color.parseColor("#6395EC"));
                holder.sat.setTextColor(Color.parseColor("#ffffff"));
                yoil = "토";
                date = item.getSat();
                ym = item.getYm() + "-" + item.getSat();
            } else{
                holder.sun_round.setCardBackgroundColor(Color.parseColor("#ffffff"));
                holder.sun.setTextColor(R.color.red);
                holder.mon_round.setCardBackgroundColor(Color.parseColor("#ffffff"));
                holder.mon.setTextColor(R.color.black);
                holder.tue_round.setCardBackgroundColor(Color.parseColor("#ffffff"));
                holder.tue.setTextColor(R.color.black);
                holder.wed_round.setCardBackgroundColor(Color.parseColor("#ffffff"));
                holder.wed.setTextColor(R.color.black);
                holder.thu_round.setCardBackgroundColor(Color.parseColor("#ffffff"));
                holder.thu.setTextColor(R.color.black);
                holder.fri_round.setCardBackgroundColor(Color.parseColor("#ffffff"));
                holder.fri.setTextColor(R.color.black);
                holder.sat_round.setCardBackgroundColor(Color.parseColor("#ffffff"));
                holder.sat.setTextColor(R.color.blue);
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

            holder.sun_box.setOnClickListener(v -> {
                if(!item.getSun().isEmpty()){
//                    setYoilRound(holder,1);
                    if (mListener != null) {
                        mListener.onItemClick(v, position, item.getSun(), "일", item.getYm() );
                    }
                    GetLastDate = item.getSun();
                }
            });

            holder.mon_box.setOnClickListener(v -> {
                if(!item.getMon().isEmpty()){
//                    setYoilRound(holder,2);
                    if (mListener != null) {
                        mListener.onItemClick(v, position, item.getMon(), "월", item.getYm());
                    }
                    GetLastDate = item.getMon();
                }
            });
            holder.tue_box.setOnClickListener(v -> {
                if(!item.getTue().isEmpty()){
//                    setYoilRound(holder,3);
                    if (mListener != null) {
                        mListener.onItemClick(v, position, item.getTue(), "화", item.getYm());
                    }
                    GetLastDate = item.getTue();
                }
            });
            holder.wed_box.setOnClickListener(v -> {
                if(!item.getWed().isEmpty()){
//                    setYoilRound(holder,4);
                    if (mListener != null) {
                        mListener.onItemClick(v, position, item.getWed(), "수", item.getYm());
                    }
                    GetLastDate = item.getWed();
                }
            });
            holder.thu_box.setOnClickListener(v -> {
                if(!item.getThu().isEmpty()){
//                    setYoilRound(holder,5);
                    if (mListener != null) {
                        mListener.onItemClick(v, position, item.getThu(), "목", item.getYm());
                    }
                    GetLastDate = item.getThu();
                }
            });
            holder.fri_box.setOnClickListener(v -> {
                if(!item.getFri().isEmpty()){
//                    setYoilRound(holder,6);
                    if (mListener != null) {
                        mListener.onItemClick(v, position, item.getFri(), "금", item.getYm());
                    }
                    GetLastDate = item.getFri();
                }
            });
            holder.sat_box.setOnClickListener(v -> {
                if(!item.getSat().isEmpty()){
//                    setYoilRound(holder,7);
                    if (mListener != null) {
                        mListener.onItemClick(v, position, item.getSat(), "토", item.getYm());
                    }
                    GetLastDate = item.getSat();
                }
            });
        } catch (Exception e) {
            dlog.i("Exception onBindViewHolder :" + e);
        }

    } // getItemCount : 전체 데이터의 개수를 리턴

    private void setYoilRound(DatePickerAdapter.ViewHolder holder, int i) {
        if (i == 1) {
            holder.sun_round.setCardBackgroundColor(Color.parseColor("#6395EC"));
            holder.sun.setTextColor(Color.parseColor("#ffffff"));
        } else if (i == 2) {
            holder.mon_round.setCardBackgroundColor(Color.parseColor("#6395EC"));
            holder.mon.setTextColor(Color.parseColor("#ffffff"));
        } else if (i == 3) {
            holder.tue_round.setCardBackgroundColor(Color.parseColor("#6395EC"));
            holder.tue.setTextColor(Color.parseColor("#ffffff"));
        } else if (i == 4) {
            holder.wed_round.setCardBackgroundColor(Color.parseColor("#6395EC"));
            holder.wed.setTextColor(Color.parseColor("#ffffff"));
        } else if (i == 5) {
            holder.thu_round.setCardBackgroundColor(Color.parseColor("#6395EC"));
            holder.thu.setTextColor(Color.parseColor("#ffffff"));
        } else if (i == 6) {
            holder.fri_round.setCardBackgroundColor(Color.parseColor("#6395EC"));
            holder.fri.setTextColor(Color.parseColor("#ffffff"));
        } else if (i == 7) {
            holder.sat_round.setCardBackgroundColor(Color.parseColor("#6395EC"));
            holder.sat.setTextColor(Color.parseColor("#ffffff"));
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
        CardView sun_round,mon_round,tue_round,wed_round,thu_round,fri_round,sat_round;
        //업무표시할 텍스트들

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

            sun_round = itemView.findViewById(R.id.sun_round);
            mon_round = itemView.findViewById(R.id.mon_round);
            tue_round = itemView.findViewById(R.id.tue_round);
            wed_round = itemView.findViewById(R.id.wed_round);
            thu_round = itemView.findViewById(R.id.thu_round);
            fri_round = itemView.findViewById(R.id.fri_round);
            sat_round = itemView.findViewById(R.id.sat_round);

            sun_round.setCardBackgroundColor(Color.parseColor("#ffffff"));
            sun.setTextColor(R.color.red);
            mon_round.setCardBackgroundColor(Color.parseColor("#ffffff"));
            mon.setTextColor(R.color.black);
            tue_round.setCardBackgroundColor(Color.parseColor("#ffffff"));
            tue.setTextColor(R.color.black);
            wed_round.setCardBackgroundColor(Color.parseColor("#ffffff"));
            wed.setTextColor(R.color.black);
            thu_round.setCardBackgroundColor(Color.parseColor("#ffffff"));
            thu.setTextColor(R.color.black);
            fri_round.setCardBackgroundColor(Color.parseColor("#ffffff"));
            fri.setTextColor(R.color.black);
            sat_round.setCardBackgroundColor(Color.parseColor("#ffffff"));
            sat.setTextColor(R.color.blue);

            shardpref = new PreferenceHelper(mContext);
            dlog.DlogContext(mContext);
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    WorkCalenderData.WorkCalenderData_list item = mData.get(pos);
                    lastPos = pos;
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
    private DatePickerAdapter.OnItemClickListener mListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(DatePickerAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position, String data, String yoil, String WorkDay);
    }
}