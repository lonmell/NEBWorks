package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.WorkGetallData;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CalendarDayAdaper extends RecyclerView.Adapter<CalendarDayAdaper.ViewHolder> {
    private static final String TAG = "CalendarDayAdaper";
    private List<String> mData = null;
    private ArrayList<WorkGetallData.WorkGetallData_list> mList = new ArrayList<>();
    private ArrayList<WorkGetallData.WorkGetallData_list> mList2 = new ArrayList<>();
    private CalendarDayAdaper2 mAdapter;

    public static Activity activity;

    Context mContext;
    PreferenceHelper shardpref;
    Dlog dlog = new Dlog();
    String month = "";
    String year = "";
    int kind = 0;
    DateCurrent dc = new DateCurrent();

    List<String> mTask_month = new ArrayList<>();
    List<String> mDay = new ArrayList<>();
    List<String> mId = new ArrayList<>();
    List<String> mPlace_id = new ArrayList<>();
    List<String> mKind = new ArrayList<>();
    List<String> mTitle = new ArrayList<>();
    List<String> mTask_date = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(View v, int position, String data, String yoil, String WorkDay);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    //-- kind : 1 - 요일별 데이터 배치 / 2 - 날짜별 데이터 배치
    public CalendarDayAdaper(Context context, List<String> data, String month, String year, ArrayList<WorkGetallData.WorkGetallData_list> mList, int kind) {
        this.mData = data;
        this.mContext = context;
        this.month = month;
        this.year = year;
        this.mList = mList;
        this.kind = kind;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public CalendarDayAdaper.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_calendar, parent, false);
        CalendarDayAdaper.ViewHolder vh = new CalendarDayAdaper.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CalendarDayAdaper.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String item = mData.get(position);
        try {
            holder.tv_date.setText(item);
            if(dc.GET_YEAR.equals(year)
                    && dc.GET_MONTH.equals(month)
                    && dc.GET_DAY.equals((mData.get(position).length()==1?"0"+mData.get(position):mData.get(position)))){
//                holder.tv_date.setBackgroundColor(Color.parseColor("#EFF4FD"));
                holder.item_total.setBackgroundColor(Color.parseColor("#EFF4FD"));
            }else{
//                holder.tv_date.setBackgroundColor(Color.parseColor("#FFFFFF"));
                holder.item_total.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 실행할 함수 코드 작성
                    // 근무현황
//                        dlog.i("----------PARAMTER----------");
//                        dlog.i("YEAR :: " + year);
//                        dlog.i("MONTH :: " + month);
//                        dlog.i("----------PARAMTER----------");

                    if (mList.size() != 0) {
                        mTask_month.clear();
                        mDay.clear();
                        mId.clear();
                        mPlace_id.clear();
                        mKind.clear();
                        mTitle.clear();
                        mTask_date.clear();
                        for (int i = 0; i < mList.size(); i++) {
                            if (mList.get(i).getTask_month().equals(month) && (!mData.get(position).equals(""))) {
                                String task_year = mList.get(i).getTask_date().substring(0,4);
                                String sumDate = year + "-" + month + "-" + item;
//                                    if ((task_year.equals(year)) && mList.get(i).getTask_month().equals(month) && (!mData.get(position).equals(""))
//                                        && mList.get(i).getDay().equals(getYoil(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(mData.get(position))))) {
                                if(mList.get(i).getTask_date().equals(sumDate)){
                                    mTask_month.add(mList.get(i).getTask_month());
                                    mDay.add(mList.get(i).getDay());
                                    mId.add(mList.get(i).getId());
                                    mPlace_id.add(mList.get(i).getPlace_id());
                                    mKind.add(mList.get(i).getKind());
                                    mTitle.add(mList.get(i).getTitle());
                                    mTask_date.add(mList.get(i).getTask_date());
                                }
                            }
                        }
                        dlog.i("------------mList Result--------------");
                        dlog.i(month + "월 " + item + "일");
                        dlog.i("mTask_month : " + mTask_month.toString());
                        dlog.i("mKind : " + mKind.toString());
                        dlog.i("mTitle : " + mTitle.toString());
                        dlog.i("mTask_date : " + mTask_date.toString());
                        dlog.i("------------mList Result--------------");
//                            dlog.i("------------mList---------------");
                        List<String> mTitlefac = new ArrayList<>(Arrays.asList(mTitle.toString().replace("[", "").replace("]", "").split(",")));
                        mList2 = new ArrayList<>();
                        mAdapter = new CalendarDayAdaper2(mContext, mList2,item);
                        holder.task_list.setAdapter(mAdapter);
                        holder.task_list.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
//                            dlog.i("mTask_month.size() : " + mTask_month.size());
                        if(mTitle.size() != 0){
                            dlog.i("mTitle.size() : " + mTitle.size());
                            for (int i2 = 0; i2 < mTitle.size(); i2++) {
                                mAdapter.addItem(new WorkGetallData.WorkGetallData_list(
                                        mTask_month.get(i2),
                                        mDay.get(i2),
                                        mId.get(i2),
                                        mPlace_id.get(i2),
                                        mKind.get(i2),
                                        mTitle.get(i2),
                                        mTask_date.get(i2)
                                ));
                                dlog.i("------------addItem--------------");
                                dlog.i("mTask_month : " + mTask_month.get(i2) + " // mDay : " + mDay.get(i2));
                                dlog.i(month + "월 " + item + "일");
                                dlog.i("mKind : " + mKind.get(i2));
                                dlog.i("mTitle : " + mTitlefac.get(i2));
                                dlog.i("mTask_date : " + mTask_date.get(i2));
                                dlog.i("------------addItem--------------");
                                if (mList.get(i2).getKind().equals("holiday") && mList.get(i2).getTask_month().equals(month)
                                        && (mList.get(i2).getDay().equals(getYoil(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(mData.get(position)))))) {
                                    holder.tv_date.setTextColor(Color.parseColor("#FF687A"));
                                }
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                }
            }, 300); // 0.3초 뒤에 실행됨 (3000ms = 3 seconds)

        } catch (Exception e) {
            dlog.i("Exception : " + e);
        }

    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_date;
        RecyclerView task_list;
        RelativeLayout rl_date, item_total;
        List<String> mDay = new ArrayList<>();//-- String으로 받은 List날짜를 하나씩 분리

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            tv_date     = itemView.findViewById(R.id.tv_date);
            task_list   = itemView.findViewById(R.id.task_list);
            rl_date     = itemView.findViewById(R.id.rl_date);
            item_total  = itemView.findViewById(R.id.item_total);

            shardpref = new PreferenceHelper(mContext);

            dlog.DlogContext(mContext);

            rl_date.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    if (mListener != null) {
                        String yoil = getYoil(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(mData.get(pos)));
                        dlog.i("onItemClick yoil : " + yoil);
                        mListener.onItemClick(v, pos, mData.get(pos), yoil, year + "-" + month + "-" + mData.get(pos));
                    }
                    dlog.i("선택한 날짜1 : " + mData.get(pos).toString());
                    dlog.i("선택한 pos : " + pos);
                }
            });
            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                }
            });
        }
    }

    public void addItem(String data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private String getYoil(int year, int month, int day) {
        // 1. LocalDate 생성
        LocalDate date = LocalDate.of(year, month, day);
//        System.out.println(date); // 2021-12-25

        // 2. DayOfWeek 객체 구하기
        DayOfWeek dayOfWeek = date.getDayOfWeek();


        // 4. 텍스트 요일 구하기 (한글)
        return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN);
    }
}

