package com.krafte.kogas.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.kogas.R;
import com.krafte.kogas.data.UsersData;
import com.krafte.kogas.util.DateCurrent;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PageMoveClass;
import com.krafte.kogas.util.PreferenceHelper;
import com.krafte.kogas.util.RetrofitConnect;

import java.util.ArrayList;

public class AdaperInMemberAdapter extends RecyclerView.Adapter<AdaperInMemberAdapter.ViewHolder> {
    private static final String TAG = "AdaperInMemberAdapter";
    private ArrayList<UsersData.UsersData_list> mData = null;
    Context mContext;
    PageMoveClass pm = new PageMoveClass();
    PreferenceHelper shardpref;
    RetrofitConnect rc = new RetrofitConnect();
    DateCurrent dc = new DateCurrent();
    public static Activity activity;

    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    int TouchItemPos = -1;
    int loadlist = 0;
    String TodayTxt = "";
    String topic = "";

    String place_id = "";
    String USER_INFO_ID = "";
    String token = "";
    Dlog dlog = new Dlog();

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public AdaperInMemberAdapter(Context context, ArrayList<UsersData.UsersData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public AdaperInMemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_member_item, parent, false);
        AdaperInMemberAdapter.ViewHolder vh = new AdaperInMemberAdapter.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdaperInMemberAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try{

        }catch (Exception e){
            dlog.i("Exception : " + e);
        }
        UsersData.UsersData_list item = mData.get(position);
        Glide.with(mContext).load(item.getImg_path())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.workimg);
        if(item.getUser_name().equals("null")){
            holder.work_name.setText("퇴사한 사원");
        }else{
            holder.work_name.setText(item.getUser_name());
        }

    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView workimg;
        TextView work_name;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            workimg = itemView.findViewById(R.id.workimg);

            work_name = itemView.findViewById(R.id.work_name);

            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID","");
            place_id = shardpref.getString("place_id","");
            token = shardpref.getString("token", "");

            dlog.DlogContext(mContext);

        }
    }

    public void addItem(UsersData.UsersData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    String click_action = "";
/* -- fcm완료되면 Retrofit으로 작성
    @SuppressLint("LongLogTag")
    private void FcmTestFunctionCall(String token_get,String title, String store_name) {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            click_action = "EmployeeNotifyListActivity";
            String SendTitle = "(재발송)새로운 매장 공지가 작성되었습니다.";
            dbConnection.FcmTestFunctionResend(topic, store_name, title, token_get, click_action, "5", topic);
            activity.runOnUiThread(() -> {

            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
*/

}
