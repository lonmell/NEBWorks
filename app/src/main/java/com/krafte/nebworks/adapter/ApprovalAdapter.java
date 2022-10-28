package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.TaskCheckData;
import com.krafte.nebworks.ui.fragment.approval.ApprovalFragment1;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApprovalAdapter extends RecyclerView.Adapter<ApprovalAdapter.ViewHolder> {
    private static final String TAG = "ApprovalAdapter";
    private ArrayList<TaskCheckData.TaskCheckData_list> mData = null;
    Context mContext;

    ApprovalFragment1 apf1 = new ApprovalFragment1();
    Dlog dlog = new Dlog();

    int user_kind;
    String stateKind = "";
    String TaskKind = "";
    int setKind = 0;
    int loadlist = 0;
    PreferenceHelper shardpref;
    String USER_INFO_AUTH = "";
    Handler mHandler = new Handler(Looper.getMainLooper());
    PageMoveClass pm = new PageMoveClass();
    boolean[] checkareatf;
    boolean allcheck = false;

    String[] checkworkno;
    List<String> ConductUser = new ArrayList<>();
    List<String> tasknoList = new ArrayList<>();
    List<String> taskdate = new ArrayList<>();

    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    public ApprovalAdapter(Context context, ArrayList<TaskCheckData.TaskCheckData_list> data, int user_kind, int setKind, boolean allcheck) {
        this.mData = data;
        this.user_kind = user_kind;
        this.setKind = setKind;
        this.allcheck = allcheck;
        mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public ApprovalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.task_checklist_item, parent, false);
        ApprovalAdapter.ViewHolder vh = new ApprovalAdapter.ViewHolder(view);
        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint({"LongLogTag", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ApprovalAdapter.ViewHolder holder, int position) {
        TaskCheckData.TaskCheckData_list item = mData.get(position);

        //체크박스 세팅
        /* getTask_kind
          1 = 일반업무
          2 = 개인업무
          3 = 휴가신청
        */
        /*getState
         * 결재 여부
          ( 승인 / 반려 / 거부 )
          0 - 처리전
          1 - 승인
          2 - 반려
          3 - 거부
         * */
        try{
            if(item.getComplete_kind().equals("1")){
                holder.imgwork.setVisibility(View.VISIBLE);
            }else{
                holder.imgwork.setVisibility(View.GONE);
            }

            holder.worknanager_info.setText(item.getRequester_department() + " " + item.getRequester_position());
            // 0:대기, 1:승인, 2:반려
            if (item.getState().equals("0")) {
                stateKind = "대기";
                holder.check_work_txt.setTextColor(Color.parseColor("#8EB3FC"));
            } else if (item.getState().equals("1")) {
                stateKind = "승인";
                holder.check_work_txt.setTextColor(Color.parseColor("#8EB3FC"));
            } else if (item.getState().equals("2")) {
                stateKind = "반려";
                holder.check_work_txt.setTextColor(Color.parseColor("#FF3636"));
            }
            if(item.getRequest_date().isEmpty()){
                holder.check_state.setVisibility(View.VISIBLE);
            }else{
                holder.check_state.setVisibility(View.GONE);
            }

            holder.work_title.setText(item.getTitle());

            try{
                holder.work_date.setText((item.getEnd_time().isEmpty()?"":"마감 "+item.getEnd_time()) + (item.getComplete_time().isEmpty()?"":"| 완료"+item.getComplete_time()));
            }catch(Exception e){
                Log.i(TAG,"E : " + e);
            }


            if(setKind == 1){
                holder.list_setting.setBackgroundColor(Color.parseColor("#3A368A"));
            }else if(setKind == 2){
                holder.list_setting.setBackgroundColor(Color.parseColor("#2D8F88"));
            }else if(setKind == 3){
                holder.list_setting.setBackgroundColor(Color.parseColor("#D43D3D"));
            }
            holder.list_setting.setOnClickListener(v -> {
                if(!checkareatf[position]){
                    checkworkno[position] = item.getId();
                    checkareatf[position] = true;
                    ConductUser.add(item.getRequester_id());
                    tasknoList.add(item.getRequest_task_no());
                    taskdate.add(item.getRequest_date());
                    holder.checkarea.setBackgroundResource(R.drawable.checkbox_on);
                }else{
                    checkworkno[position] = "";
                    checkareatf[position] = false;
                    ConductUser.remove(item.getRequester_id());
                    tasknoList.remove(item.getRequest_task_no());
                    taskdate.remove(item.getRequest_date());
                    holder.checkarea.setBackgroundResource(R.drawable.checkbox_off);
                }

                shardpref.putString("checkworkno", Arrays.toString(checkworkno));
                shardpref.putString("ConductUser", String.valueOf(ConductUser));
                shardpref.putString("tasknoList", String.valueOf(tasknoList));
                shardpref.putString("taskdate", String.valueOf(taskdate));

                int Tcnt = 0;
                int Fcnt = 0;

                for(int a = 0; a < mData.size();a++){
                    if(checkareatf[a]){
                        ++Tcnt;
                    }
                    if(!checkareatf[a]){
                        ++Fcnt;
                    }
                }
                Log.i(TAG,"Tcnt : " + Tcnt + " / Fcnt : " + Fcnt);
                if (mListener != null) {
                    mListener.onItemClick(v,position,Tcnt,Fcnt);
                }
            });

            holder.check_work_txt.setText(stateKind);
            try{
                Glide.with(mContext).load(item.getRequester_img_path())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(holder.workimg);
            }catch (Exception e){
                Log.i(TAG,"ApprovalAdapter Exception : " + e);
            }


            holder.area01.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener2.onItemClick(v,position);
                }
            });
        }catch (Exception e){
            dlog.i("Exception : " + e);
        }
    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item_total;
        TextView work_title, check_work_txt, work_date, worknanager_info, check_state;
        ImageView workimg;
        CardView workimg_url,area01;
        ImageView checkarea,imgwork;
        RelativeLayout list_setting;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
//            work_state = itemView.findViewById(R.id.work_state);
            work_title = itemView.findViewById(R.id.work_title);
            workimg = itemView.findViewById(R.id.workimg);
            worknanager_info = itemView.findViewById(R.id.worknanager_info);
            check_work_txt = itemView.findViewById(R.id.check_work_txt);
            workimg_url = itemView.findViewById(R.id.workimg_url);
            work_date = itemView.findViewById(R.id.work_date);
            list_setting = itemView.findViewById(R.id.list_setting);
            check_state = itemView.findViewById(R.id.check_state);
            checkarea = itemView.findViewById(R.id.checkarea);
            item_total = itemView.findViewById(R.id.item_total);
            imgwork = itemView.findViewById(R.id.imgwork);
            area01 = itemView.findViewById(R.id.area01);

            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
            checkarea.setBackgroundResource(R.drawable.checkbox_off);
            checkareatf = new boolean[mData.size()];
            checkworkno = new String[mData.size()];


            if(USER_INFO_AUTH.equals("0")){//0-관리자 / 1- 근로자
                list_setting.setVisibility(View.VISIBLE);
            }else{
                list_setting.setVisibility(View.GONE);
            }

            if(allcheck){
                checkarea.setBackgroundResource(R.drawable.checkbox_on);
                Log.i(TAG,"mData.size() : " + mData.size());
                for(int i = 0;i < mData.size(); i++){
                    checkareatf[i] = true;
                    checkworkno[i] = mData.get(i).getId();
                    ConductUser.add(mData.get(i).getRequester_id());
                    tasknoList.add(mData.get(i).getRequest_task_no());
                    taskdate.add(mData.get(i).getRequest_date());
                }
                Log.i(TAG,"checkworkno : " + Arrays.toString(checkworkno));
                shardpref.putString("checkworkno", Arrays.toString(checkworkno));
                shardpref.putString("ConductUser", String.valueOf(ConductUser));
                shardpref.putString("tasknoList", String.valueOf(tasknoList));
                shardpref.putString("taskdate", String.valueOf(taskdate));
            }else{
                checkarea.setBackgroundResource(R.drawable.checkbox_off);
                Log.i(TAG,"mData.size() : " + mData.size());
                for(int i = 0;i < mData.size(); i++){
                    checkareatf[i] = false;
                    checkworkno[i] = "";
                    ConductUser.remove(mData.get(i).getRequester_id());
                    tasknoList.remove(mData.get(i).getRequest_task_no());
                    taskdate.remove(mData.get(i).getRequest_date());
                }
                Log.i(TAG,"checkworkno : " + Arrays.toString(checkworkno));
                shardpref.remove("checkworkno");
                shardpref.remove("ConductUser");
                shardpref.remove("tasknoList");
                shardpref.remove("taskdate");
            }


            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    TaskCheckData.TaskCheckData_list item = mData.get(pos);

                }
            });
        }
    }

    public void addItem(TaskCheckData.TaskCheckData_list data) {
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
        void onItemClick(View v, int position, int Tcnt, int Fcnt);
    }
    //--
    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener2 mListener2 = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener2(OnItemClickListener2 listener2) {
        this.mListener2 = listener2;
    }

    public interface OnItemClickListener2 {
        void onItemClick(View v, int position);
    }
}
