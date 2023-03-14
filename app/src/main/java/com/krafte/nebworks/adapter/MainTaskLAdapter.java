package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.MainTaskData;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainTaskLAdapter extends RecyclerView.Adapter<MainTaskLAdapter.ViewHolder> {
    private static final String TAG = "MainTaskLAdapter";
    private ArrayList<MainTaskData.MainTaskData_list> mData = null;
    Context mContext;
    PageMoveClass pm = new PageMoveClass();
    PreferenceHelper shardpref;
    RetrofitConnect rc = new RetrofitConnect();
    DateCurrent dc = new DateCurrent();
    public static Activity activity;

    String place_id = "";
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";
    Dlog dlog = new Dlog();

    List<String> user_id = new ArrayList<>();
    List<String> user_name = new ArrayList<>();
    List<String> user_img_path = new ArrayList<>();
    List<String> user_img_jikgup = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private MainTaskLAdapter.OnItemClickListener mListener = null;

    public void setOnItemClickListener(MainTaskLAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public MainTaskLAdapter(Context context, ArrayList<MainTaskData.MainTaskData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public MainTaskLAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.main_task_item, parent, false);
        MainTaskLAdapter.ViewHolder vh = new MainTaskLAdapter.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MainTaskLAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MainTaskData.MainTaskData_list item = mData.get(position);
        try{
            dlog.i("mData item : " + mData.get(position));
            holder.title.setText(item.getTitle());
            String endhour = "";
            String endmin = "";

            if (item.getEnd_time().length() > 6) {
                if(item.getStart_time().length() <= 10){
                    holder.date.setText(item.getStart_time()+" 마감");
                }else{
                    String[] splitEndDate = item.getEnd_time().split(" ");
                    String[] endDate = splitEndDate[0].split("-");
                    endhour = splitEndDate[1].split(":")[0] + "시 ";
                    endmin = splitEndDate[1].split(":")[1] + "분";
                    holder.date.setText(endDate[0] + "년 " + endDate[1] + "월 " + endDate[2] + "일" + " | " + "마감 " + endhour + endmin + "까지");
                }
            } else {
                String date[] = item.getEnd_time().split(":");
                holder.date.setText("[반복할일]" + " 마감 " + date[0] + "시 " + date[1] + " 까지");
            }

            if(position == 0){
                holder.itemline.setVisibility(View.GONE);
            }else {
                holder.itemline.setVisibility(View.VISIBLE);
            }
            holder.report_btn.setOnClickListener(v -> {
                if (USER_INFO_AUTH.isEmpty()) {
                    isAuth();
                } else {
                    try {
                        JSONArray Response = new JSONArray(item.getUsers().toString().replace("[[", "[").replace("]]", "]"));
                        dlog.i("users : " + item.getUsers().toString().replace("[[", "[").replace("]]", "]"));
                        dlog.i("users Response : " + Response.length());
                        if (Response.length() == 0) {
                            Log.i(TAG, "GET SIZE : " + Response.length());
                        } else {
                            user_id.removeAll(user_id);
                            user_name.removeAll(user_name);
                            user_img_path.removeAll(user_img_path);
                            user_img_jikgup.removeAll(user_img_jikgup);
                            for (int i = 0; i < Response.length(); i++) {
                                JSONObject jsonObject = Response.getJSONObject(i);
                                if (!jsonObject.getString("user_name").equals("null")) {
                                    user_id.add(jsonObject.getString("user_id"));
                                    user_name.add(jsonObject.getString("user_name"));
                                    user_img_path.add(jsonObject.getString("img_path"));
                                    user_img_jikgup.add(jsonObject.getString("jikgup"));
                                }
                            }
                        }
//                        item.getApproval_state()
                        shardpref.putString("task_no", item.getId());
                        shardpref.putString("writer_id", item.getWriter_id());
                        shardpref.putString("kind", item.getKind());
                        shardpref.putString("title", item.getTitle());
                        shardpref.putString("contents", item.getContents());
                        shardpref.putString("complete_kind", item.getComplete_kind());
                        shardpref.putString("users", user_id.toString());
                        shardpref.putString("usersn", user_name.toString());
                        shardpref.putString("usersimg", user_img_path.toString());
                        shardpref.putString("usersjikgup", user_img_jikgup.toString());
                        shardpref.putString("task_date", item.getTask_date());
                        shardpref.putString("start_time", item.getStart_time());
                        shardpref.putString("end_time", item.getEnd_time());
                        shardpref.putString("sun", item.getSun());
                        shardpref.putString("mon", item.getMon());
                        shardpref.putString("tue", item.getTue());
                        shardpref.putString("wed", item.getWed());
                        shardpref.putString("thu", item.getThu());
                        shardpref.putString("fri", item.getFri());
                        shardpref.putString("sat", item.getSat());
                        shardpref.putString("img_path", item.getImg_path());
                        shardpref.putString("complete_yn", item.getComplete_yn());// y:완료, n:미완료
                        shardpref.putString("incomplete_reason", item.getIncomplete_reason()); // n: 미완료 사요
                        shardpref.putString("approval_state", item.getApproval_state());// 0: 결재대기, 1:승인, 2:반려, 3:결재요청 전
                        shardpref.putString("reject_reason", item.getReject_reason());
                        shardpref.putString("updated_at", item.getUpdated_at());
                        dlog.i("users : " + user_id.toString());
                        dlog.i("usersn : " + user_name.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    pm.TaskDetail(mContext);
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

        TextView title,date;
        LinearLayout itemline;
        CardView report_btn;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            title         = itemView.findViewById(R.id.title);
            date          = itemView.findViewById(R.id.date);
            itemline      = itemView.findViewById(R.id.itemline);
            report_btn    = itemView.findViewById(R.id.report_btn);

            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID","");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
            place_id = shardpref.getString("place_id","");

            dlog.DlogContext(mContext);

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    if (mListener != null) {
                        mListener.onItemClick(view,pos);
                    }
                }
            });
        }
    }

    public void addItem(MainTaskData.MainTaskData_list data) {
        mData.add(data);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<MainTaskData.MainTaskData_list> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void isAuth() {
        Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
        intent.putExtra("flag","더미");
        intent.putExtra("data","먼저 매장등록을 해주세요!");
        intent.putExtra("left_btn_txt", "닫기");
        intent.putExtra("right_btn_txt", "매장추가");
        mContext.startActivity(intent);
        activity.overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
}
