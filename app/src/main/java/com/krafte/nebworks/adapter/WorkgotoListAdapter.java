package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.WorkGotoListData;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WorkgotoListAdapter extends RecyclerView.Adapter<WorkgotoListAdapter.ViewHolder> {
    private static final String TAG = "WorkgotoListAdapter";
    private ArrayList<WorkGotoListData.WorkGotoListData_list> mData = null;
    Context mContext;
    PreferenceHelper shardpref;

    //Shared
    String USER_INFO_ID;
    String USER_INFO_NAME;
    Dlog dlog = new Dlog();

    Date to01 = null;
    Date to02 = null;

    int Tday = 0;
    int Thour = 0;
    int Tmin = 0;
    String TotalTime = "";

    public WorkgotoListAdapter(Context context, ArrayList<WorkGotoListData.WorkGotoListData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public WorkgotoListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.gotowork_list_item, parent, false);
        WorkgotoListAdapter.ViewHolder vh = new WorkgotoListAdapter.ViewHolder(view);
        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint({"SetTextI18n", "LongLogTag"})
    @Override
    public void onBindViewHolder(@NonNull WorkgotoListAdapter.ViewHolder holder, int position) {
        WorkGotoListData.WorkGotoListData_list item = mData.get(position);
        try{
            dlog.i("--------onBindViewHolder--------");
            dlog.i("item.getDay() : " + item.getDay());
            dlog.i("item.getIn_time() : " + item.getIn_time());
            dlog.i("item.getOut_time() : " + item.getOut_time());
            dlog.i("--------onBindViewHolder--------");

            if(item.getIn_time().equals("null") && item.getOut_time().equals("null")){
                holder.item_total.setVisibility(View.GONE);
            }else{
                holder.item_total.setVisibility(View.VISIBLE);
                holder.gotowork_itme00.setText(item.getDay() + "일");
                holder.gotowork_itme01_1.setText(item.getIn_time().equals("null")?"":item.getIn_time().isEmpty()?"":item.getIn_time().substring(0,5));
                holder.gotowork_itme03.setText(item.getOut_time().equals("null")?"":item.getOut_time().isEmpty()?"":item.getOut_time().substring(0,5));
                holder.gotowork_itme04.setText(item.getWorkdiff().equals("null")?"":item.getWorkdiff().isEmpty()?"":item.getWorkdiff());
            }
        }catch (Exception e){
            dlog.i("Exception : " + e);
        }

        try {
            @SuppressLint("SimpleDateFormat")
            Date format1 = new SimpleDateFormat("HH:mm").parse(item.getOut_time());
            @SuppressLint("SimpleDateFormat")
            Date format2 = new SimpleDateFormat("HH:mm").parse(item.getIn_time());

            long diffSec = (format1.getTime() - format2.getTime()) / 1000; //초 차이
            long diffMin = (format1.getTime() - format2.getTime()) / 60000; //분 차이
            long diffHuor = (format1.getTime() - format2.getTime()) / 3600000; //시 차이
            long diffDays = diffSec / (24 * 60 * 60); //일자수 차이

            dlog.i("(" + position + ") : " + diffDays + "day");
            dlog.i("(" + position + ") : " + diffHuor + "h");
            dlog.i("(" + position + ") : " + diffMin + "m");

            Tday += diffDays;
            Thour += diffHuor;
            Tmin += diffMin;

            if(Thour >= 24 ){
                Tday += (Thour/24);
            }
            if(Tmin >= 60){
                Thour += (Tmin/60);
            }
            TotalTime = Tday + " Day " + Thour + " H " + Tmin + "m";
            dlog.i("TotalTime : " + TotalTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item_total;
        TextView gotowork_itme00;
        TextView gotowork_itme01;
        TextView gotowork_itme01_1;
        TextView gotowork_itme02;
        TextView gotowork_itme03;
        TextView gotowork_itme04;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            item_total = itemView.findViewById(R.id.item_total);
            gotowork_itme00 = itemView.findViewById(R.id.gotowork_itme00);
            gotowork_itme01 = itemView.findViewById(R.id.gotowork_itme01);
            gotowork_itme01_1 = itemView.findViewById(R.id.gotowork_itme01_1);
            gotowork_itme02 = itemView.findViewById(R.id.gotowork_itme02);
            gotowork_itme03 = itemView.findViewById(R.id.gotowork_itme03);
            gotowork_itme04 = itemView.findViewById(R.id.gotowork_itme04);

            shardpref = new PreferenceHelper(mContext);
            dlog.DlogContext(mContext);

            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");



        }
    }

    public void addItem(WorkGotoListData.WorkGotoListData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
