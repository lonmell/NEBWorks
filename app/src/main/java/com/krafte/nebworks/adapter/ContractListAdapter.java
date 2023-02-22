package com.krafte.nebworks.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.ContractData;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.PushLogInputInterface;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ContractListAdapter extends RecyclerView.Adapter<ContractListAdapter.ViewHolder> {
    private static final String TAG = "ContractListAdapter";
    private ArrayList<ContractData.ContractData_list> mData = null;
    Context mContext;
    PageMoveClass pm = new PageMoveClass();
    PreferenceHelper shardpref;
    RetrofitConnect rc = new RetrofitConnect();
    DateCurrent dc = new DateCurrent();
    Activity activity;
    String USER_INFO_AUTH = "";
    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    int loadlist = 0;
    Dlog dlog = new Dlog();

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private ContractListAdapter.OnItemClickListener mListener = null;

    public void setOnItemClickListener(ContractListAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public ContractListAdapter(Context context, ArrayList<ContractData.ContractData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public ContractListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.contractlist_item, parent, false);
        ContractListAdapter.ViewHolder vh = new ContractListAdapter.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ContractListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ContractData.ContractData_list item = mData.get(position);

        try {
            holder.name.setText(item.getName());
            holder.phone.setText(item.getJoin_date());

            holder.profile_tv.setVisibility(View.VISIBLE);
            holder.profile_tv2.setVisibility(View.GONE);

            if(item.getContract_yn().equals("0")){
                holder.profile_img.setCardBackgroundColor(Color.parseColor("#DD6540"));
                holder.profile_tv.setText("미작성");
                holder.address.setText("근로계약서 작성");
                holder.address.setTextColor(Color.parseColor("#DD6540"));
            }else if(item.getContract_yn().equals("1")){
                if(!(item.getOwner_sign_id().equals("null")||item.getOwner_sign_id().isEmpty()) && (item.getWorker_sign_id().equals("null")||item.getWorker_sign_id().isEmpty())) {
                    holder.profile_img.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
                    holder.profile_tv.setText("서명\n대기중");
                    holder.address.setText("근로계약서 작성");
                    holder.address.setTextColor(ContextCompat.getColor(mContext, R.color.new_blue));
                } else if(!(item.getOwner_sign_id().equals("null")||item.getOwner_sign_id().isEmpty()) && !(item.getWorker_sign_id().equals("null")||item.getWorker_sign_id().isEmpty())){
                    holder.profile_img.setCardBackgroundColor(Color.parseColor("#DBDBDB"));
                    holder.profile_tv.setText("완료");
                    holder.address.setText("근로계약서 작성");
                    holder.address.setTextColor(Color.parseColor("#DBDBDB"));
                }else if((item.getOwner_sign_id().equals("null")||item.getOwner_sign_id().isEmpty()) && (item.getWorker_sign_id().equals("null")||item.getWorker_sign_id().isEmpty())){
                    holder.profile_img.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
                    holder.profile_tv.setText("작성중");
                    holder.address.setText("근로계약서 작성중");
                    holder.address.setTextColor(ContextCompat.getColor(mContext, R.color.new_blue));
                }
            }
            holder.item_total.setOnClickListener(v -> {
                shardpref.putString("worker_id",item.getUser_id());
                shardpref.putString("worker_name",item.getName());
                shardpref.putString("contract_place_id",item.getPlace_id());
                shardpref.putString("contract_user_id",item.getUser_id());
                if(item.getContract_yn().equals("0")){
                    if(USER_INFO_AUTH.equals("0")){
                        pm.AddContractPage01(mContext);
                    }else{
                        Toast.makeText(mContext,"작성된 근로계약서가 없습니다. ", Toast.LENGTH_SHORT).show();
                    }
                }else if(item.getContract_yn().equals("1")){
//                    if(item.getWorker_sign_id().isEmpty() || item.getWorker_sign_id().equals("null")){
//                       dlog.i("서명대기중");
//                    }else if(!item.getWorker_sign_id().isEmpty() && !item.getWorker_sign_id().isEmpty()){
//                        dlog.i("완료");
//                    }
                    shardpref.putString("contract_id",item.getContract_id());
                    shardpref.putString("progress_pos",item.getProgress_pos());
                    /* item.getContract_id()
                    *   현재 진행중인 페이지
                        0 or null - 작성안됨
                        1 - 사업장 기본사항
                        2 - 근무 기본사항
                        3 - 급여 기본사항
                        4 - 특약
                        5 - 근로자 인적사항
                        6 - 서명
                        7 - 완료
                    * */
                    if(USER_INFO_AUTH.equals("0")){
                        if(item.getProgress_pos().equals("0")){
                            pm.AddContractPage03(mContext);
                        }else if(item.getProgress_pos().equals("1")){
                            //근무 기본사항 부터
                            pm.AddContractPage04(mContext);
                        }else if(item.getProgress_pos().equals("2")){
                            //급여 기본사항 부터
                            pm.AddContractPage05(mContext);
                        }else if(item.getProgress_pos().equals("3")){
                            //특약 부터
                            pm.AddContractPage06(mContext);
                        }else if(item.getProgress_pos().equals("4")){
                            //근로자 인적사항 부터
                            pm.AddContractPage07(mContext);
                        }else if(item.getProgress_pos().equals("5")){
                            //서명 부터
                            pm.AddContractPage08(mContext);
                        }else if(item.getProgress_pos().equals("7")){
                            //해당 근로계약서 전체 상세 페이지로
                            shardpref.putString("contract_id", item.getContract_id());
                            pm.ContractAll(mContext);
                        }
                    }else{
                        //근로자일경우
                        if(item.getProgress_pos().equals("6")){
                            pm.ContractWorkerAccept(mContext);
                        }else if(item.getProgress_pos().equals("7")){
                            //해당 근로계약서 전체 상세 페이지로
                            shardpref.putString("contract_id", item.getContract_id());
                            pm.ContractAll(mContext);
                        }
                    }
                }
            });

            holder.call_icon.setOnClickListener(v -> {
                String place_name = PlaceCheckData.getInstance().getPlace_name();
                String message = "[" + place_name + "]에서 " + "[" + item.getName() + "]의 근로계약서 작성 요청이 도착했습니다.";
                getUserToken(item.getUser_id(), "5", message);
                AddPush("근로계약서 작성요청",message,item.getUser_id());
            });
            if (loadlist == 0) {
                //--아이템에 나타나기 애니메이션 줌
                holder.item_total.setTranslationY(150);
                holder.item_total.setAlpha(0.f);
                holder.item_total.animate().translationY(0).alpha(1.f)
                        .setStartDelay(delayEnterAnimation ? 20 * (position) : 0) // position 마다 시간차를 조금 주고..
                        .setInterpolator(new DecelerateInterpolator(2.f))
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                animationsLocked = true; // 진입시에만 animation 하도록 하기 위함
                            }
                        });
                loadlist++;
            }
        } catch (Exception e) {
            dlog.i("Exception : " + e);
        }

    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<ContractData.ContractData_list> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, address, phone, profile_tv;
        CardView item_total,profile_img;
        LinearLayout call_icon;
        ImageView profile_tv2;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            name        = itemView.findViewById(R.id.name);
            address     = itemView.findViewById(R.id.address);
            phone       = itemView.findViewById(R.id.phone);
            item_total  = itemView.findViewById(R.id.item_total);
            call_icon   = itemView.findViewById(R.id.call_icon);
            profile_img = itemView.findViewById(R.id.profile_img);
            profile_tv  = itemView.findViewById(R.id.profile_tv);
            profile_tv2  = itemView.findViewById(R.id.profile_tv2);

            shardpref = new PreferenceHelper(mContext);
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH","");
            dlog.DlogContext(mContext);
            if(USER_INFO_AUTH.equals("1")){
                call_icon.setVisibility(View.VISIBLE);
                call_icon.setClickable(true);
            }
            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    ContractData.ContractData_list item = mData.get(pos);
                    if(!item.getOwner_sign_id().equals("null") && !item.getWorker_sign_id().equals("null")){
                        shardpref.putString("contract_id", item.getContract_id());
                        pm.ContractAll(mContext);
                    }else{
//                        Toast.makeText(mContext,"근로계약서 작성이 완료되지 않았습니다.",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void addItem(ContractData.ContractData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    String place_owner_id = "";
    String place_id = "";
    public void getUserToken(String user_id, String type, String message) {
        dlog.i("-----getManagerToken-----");
        dlog.i("user_id : " + user_id);
        dlog.i("type : " + type);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FCMSelectInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FCMSelectInterface api = retrofit.create(FCMSelectInterface.class);
        Call<String> call = api.getData(user_id, type);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String jsonResponse = rc.getBase64decode(response.body());
                dlog.i("jsonResponse length : " + jsonResponse.length());
                dlog.i("jsonResponse : " + jsonResponse);
                try {
                    JSONArray Response = new JSONArray(jsonResponse);
                    if (Response.length() > 0) {
                        dlog.i("-----getManagerToken-----");
                        dlog.i("user_id : " + Response.getJSONObject(0).getString("user_id"));
                        dlog.i("token : " + Response.getJSONObject(0).getString("token"));
                        String id = Response.getJSONObject(0).getString("id");
                        String token = Response.getJSONObject(0).getString("token");
                        dlog.i("-----getManagerToken-----");
                        place_id = PlaceCheckData.getInstance().getPlace_id();
                        place_owner_id = shardpref.getString("place_owner_id","");
                        boolean channelId1 = Response.getJSONObject(0).getString("channel2").equals("1");
                        if (!token.isEmpty() && channelId1) {
                            PushFcmSend(id, "", message, token, "2", place_id);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러 = " + t.getMessage());
            }
        });
    }

    public void AddPush(String title, String content, String user_id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PushLogInputInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PushLogInputInterface api = retrofit.create(PushLogInputInterface.class);
        Call<String> call = api.getData(place_id, "", title, content, place_owner_id, user_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String jsonResponse = rc.getBase64decode(response.body());
                dlog.i("jsonResponse length : " + jsonResponse.length());
                dlog.i("jsonResponse : " + jsonResponse);
                if (response.isSuccessful() && response.body() != null) {

                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러1 = " + t.getMessage());
            }
        });
    }

    DBConnection dbConnection = new DBConnection();
    String click_action = "";
    //점주 > 근로자
    private void PushFcmSend(String topic, String title, String message, String token, String tag, String place_id) {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            click_action = "TaskList1";
            dlog.i("-----PushFcmSend-----");
            dlog.i("topic : " + topic);
            dlog.i("title : " + title);
            dlog.i("message : " + message);
            dlog.i("token : " + token);
            dlog.i("click_action : " + click_action);
            dlog.i("tag : " + tag);
            dlog.i("place_id : " + place_id);
            dlog.i("-----PushFcmSend-----");
            dbConnection.FcmTestFunction(topic, title, message, token, click_action, tag, place_id);
//            activity.runOnUiThread(() -> {
//            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
