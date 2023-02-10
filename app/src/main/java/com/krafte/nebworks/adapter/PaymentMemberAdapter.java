package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.PaymentData;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.PushLogInputInterface;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class PaymentMemberAdapter extends RecyclerView.Adapter<PaymentMemberAdapter.ViewHolder> {
    private static final String TAG = "PaymentMemberAdapter";
    private ArrayList<PaymentData.PaymentData_list> mData = null;
    Context mContext;
    PreferenceHelper shardpref;
    int select_flag = -1;
    int before_pos = 0;
    String selectdate = "0000.00";
    String place_id = "";
    String place_name = "";
    String place_img = "";
    Dlog dlog = new Dlog();
    PageMoveClass pm = new PageMoveClass();
    int AllPayment = 0;
    String Tap = "";
    String USER_INFO_AUTH = "";

    String insurance1 = "";
    String insurance2 = "";
    String insurance3 = "";
    String insurance4 = "";

    float insurance01p = 0;//국민연금 퍼센트
    float insurance02p = 0;//건강보험 퍼센트
    float insurance03p = 0;//고용보험 퍼센트
    float insurance04p = 0;//장기요양보험료 퍼센트

    RetrofitConnect rc = new RetrofitConnect();
    DecimalFormat myFormatter = new DecimalFormat("###,###");
    public PaymentMemberAdapter(Context context, ArrayList<PaymentData.PaymentData_list> data, String selectdate,float insurance01p,float insurance02p,float insurance03p,float insurance04p, String Tap) {
        this.mData = data;
        this.mContext = context;
        this.selectdate = selectdate;
        this.insurance01p = insurance01p;
        this.insurance02p = insurance02p;
        this.insurance03p = insurance03p;
        this.insurance04p = insurance04p;
        this.Tap = Tap;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.paymenager_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PaymentData.PaymentData_list item = mData.get(position);
        try{
            AllPayment = Integer.parseInt(item.getTotal_payment().equals("null")?"0":item.getTotal_payment()) + Integer.parseInt(item.getSecond_pay().equals("null")?"0":item.getSecond_pay()) + Integer.parseInt(item.getOverwork_pay().equals("null")?"0":item.getOverwork_pay());

            if(USER_INFO_AUTH.equals("1")){
                holder.total_pay.setVisibility(View.GONE);
                holder.name.setText(place_name);
                Glide.with(mContext).load(place_img)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(holder.profile_setimg);
                if(Tap.equals("0")){
                    if(item.getGongjeynpay().equals("null")){
                        holder.gongje_box.setVisibility(View.GONE);
                        holder.send_user_state.setVisibility(View.GONE);
                        holder.write_payment.setVisibility(View.GONE);
                        holder.weekly_worktime_progress.setVisibility(View.VISIBLE);
                        holder.progress_tvarea.setVisibility(View.VISIBLE);
                    }else{
                        holder.gongje_box.setVisibility(View.VISIBLE);
                        holder.send_user_state.setVisibility(View.GONE);
                        holder.write_payment.setVisibility(View.GONE);
                        holder.weekly_worktime_progress.setVisibility(View.VISIBLE);
                        holder.progress_tvarea.setVisibility(View.VISIBLE);
                    }
                }else if(Tap.equals("1")){
                    holder.gongje_box.setVisibility(View.VISIBLE);
                    holder.send_user_state.setVisibility(View.GONE);
                    holder.write_payment.setVisibility(View.GONE);
                    holder.weekly_worktime_progress.setVisibility(View.GONE);
                    holder.progress_tvarea.setVisibility(View.GONE);
                }
            }else if(USER_INFO_AUTH.equals("0")){
                holder.total_pay.setVisibility(View.VISIBLE);
                holder.name.setText(item.getUser_name());
                Glide.with(mContext).load(item.getImg_path())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(holder.profile_setimg);

                if(Tap.equals("0")){
                    if(item.getGongjeynpay().equals("null")){
                        holder.gongje_box.setVisibility(View.GONE);
                        holder.send_user_state.setVisibility(View.GONE);
                        holder.write_payment.setVisibility(View.VISIBLE);
                        holder.weekly_worktime_progress.setVisibility(View.VISIBLE);
                        holder.progress_tvarea.setVisibility(View.VISIBLE);
                    }else{
                        holder.gongje_box.setVisibility(View.VISIBLE);
                        holder.send_user_state.setVisibility(View.GONE);
                        holder.write_payment.setVisibility(View.GONE);
                        holder.weekly_worktime_progress.setVisibility(View.VISIBLE);
                        holder.progress_tvarea.setVisibility(View.GONE);
                    }
                }else if(Tap.equals("1")){
                        holder.gongje_box.setVisibility(View.VISIBLE);
                        holder.send_user_state.setVisibility(View.VISIBLE);
                        holder.write_payment.setVisibility(View.GONE);
                        holder.weekly_worktime_progress.setVisibility(View.GONE);
                        holder.progress_tvarea.setVisibility(View.GONE);
                }
            }

            insurance1 = String.valueOf(Math.round((AllPayment * insurance01p)/100));
            insurance2 = String.valueOf(Math.round((AllPayment * insurance02p)/100));
            insurance3 = String.valueOf(Math.round((AllPayment * insurance03p)/100));
            insurance4 = String.valueOf(Math.round((Math.round((AllPayment * insurance02p)/100) * insurance04p)/100));

            int result_gongje_int = Integer.parseInt(insurance1)+Integer.parseInt(insurance2)+Integer.parseInt(insurance3)+Integer.parseInt(insurance4);
            String resultGonjeTv = myFormatter.format(result_gongje_int);
            holder.result_pay.setText(myFormatter.format(Integer.parseInt(item.getTotal_payment())) + "원");
            holder.result_gongje.setText(resultGonjeTv + "원");

            String pay = myFormatter.format(Integer.parseInt(item.getGongjeynpay().equals("null")?"0":item.getGongjeynpay()));//공제처리된 실급여

            if(pay.equals("0")){
                holder.total_pay.setVisibility(View.GONE);
            }else{
                holder.total_pay.setText(pay + "원");
            }

            holder.work_day.setText(item.getTotal_workday() + "일");

            List<String> workhour = new ArrayList<>(Arrays.asList(item.getWorkhour().split("\\.")));
            holder.work_time.setText(workhour.get(0) + "시간");
            holder.weekly_worktime_progress.setProgress(Integer.parseInt(workhour.get(0)));

            holder.nowpay.setText("예상 급여 " + myFormatter.format(Integer.parseInt(item.getTotal_payment())) + "원");
            holder.mypay.setText("계약 급여 " + item.getPayment() + "원");


            holder.write_payment.setOnClickListener(v -> {
                dlog.i("write_payment Click");
                if (mListener != null) {
                    mListener.onItemClick(v, position);
                }
            });

            holder.paystub_resend.setOnClickListener(v -> {
                String message = "["+item.getUser_name()+"] 님의 " + item.getSet_month() + "월 급여명세서가 도착했습니다.";
                getUserToken(item.getUser_id(),"1",message);
                AddPush("급여명세서",message,item.getUser_id());
            });
        }catch (Exception e){
            e.printStackTrace();
        }


    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView total_pay,work_day,work_time,name;
        ProgressBar weekly_worktime_progress;
        TextView nowpay,mypay,result_pay,result_gongje;
        LinearLayout gongje_box,send_user_state;
        RelativeLayout progress_tvarea;
        ImageView profile_setimg;
        CardView paystub_resend,send_money,write_payment;


        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);

            name                     = itemView.findViewById(R.id.name);
            total_pay                = itemView.findViewById(R.id.total_pay);
            work_day                 = itemView.findViewById(R.id.work_day);
            work_time                = itemView.findViewById(R.id.work_time);
            weekly_worktime_progress = itemView.findViewById(R.id.weekly_worktime_progress);
            nowpay                   = itemView.findViewById(R.id.nowpay);
            mypay                    = itemView.findViewById(R.id.mypay);
            gongje_box               = itemView.findViewById(R.id.gongje_box);
            write_payment            = itemView.findViewById(R.id.write_payment);
            send_user_state          = itemView.findViewById(R.id.send_user_state);
            result_pay               = itemView.findViewById(R.id.result_pay);
            result_gongje            = itemView.findViewById(R.id.result_gongje);
            progress_tvarea          = itemView.findViewById(R.id.progress_tvarea);
            profile_setimg           = itemView.findViewById(R.id.profile_setimg);
            paystub_resend           = itemView.findViewById(R.id.paystub_resend);
            send_money               = itemView.findViewById(R.id.send_money);

            USER_INFO_AUTH  = shardpref.getString("USER_INFO_AUTH","-1");
            place_id        = shardpref.getString("place_id","-1");
            place_name      = PlaceCheckData.getInstance().getPlace_name();
            place_img       = PlaceCheckData.getInstance().getPlace_img_path();

            dlog.i("------ViewHolder------");
            dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
            dlog.i("place_id : " + place_id);
            dlog.i("place_name : " + place_name);
            dlog.i("place_img : " + place_img);
            dlog.i("------ViewHolder------");

            itemView.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    // 리스너 객체의 메서드 호출.
                    PaymentData.PaymentData_list item = mData.get(pos);
                    if(USER_INFO_AUTH.equals("1")){
                        if(!item.getBasic_pay().equals("null")
                                || !item.getSecond_pay().equals("null")
                                || !item.getOverwork_pay().equals("null")
                                || !item.getMeal_allowance_yn().equals("null")
                                || !item.getStore_insurance_yn().equals("null")
                                || !item.getMeal_pay().equals("null")){
                            shardpref.putString("select_month",selectdate.substring(5,7));
                            shardpref.putString("stub_place_id",item.getStore_no());
                            shardpref.putString("stub_user_id",item.getUser_id());
                            shardpref.putString("stub_user_name",item.getUser_name());
                            shardpref.putString("stub_jikgup",item.getJikgup());
                            shardpref.putString("stub_basic_pay",item.getBasic_pay());
                            shardpref.putString("stub_second_pay",item.getSecond_pay());
                            shardpref.putString("stub_overwork_pay",item.getOverwork_pay());
                            shardpref.putString("stub_meal_allowance_yn",item.getMeal_allowance_yn());
                            shardpref.putString("stub_store_insurance_yn",item.getStore_insurance_yn());
                            shardpref.putString("stub_gongjeynpay",item.getGongjeynpay());
                            shardpref.putString("stub_total_payment",item.getTotal_payment());
                            shardpref.putString("stub_workday",item.getWorkday());
                            shardpref.putString("stub_total_workday",item.getTotal_workday());
                            shardpref.putString("stub_total_workhour", item.getWorkhour());
                            shardpref.putString("stub_payment",item.getPayment());
                            shardpref.putString("stub_selectdate",selectdate);
                            shardpref.putString("stub_meal_pay",item.getMeal_pay());
//                        shardpref.putString("returnPage","");
                            pm.PaystubAll(mContext);
                        }else{
                            Toast.makeText(mContext,"작성된 급여명세서 데이터가 없습니다.",Toast.LENGTH_SHORT).show();
                        }
                    }else if(USER_INFO_AUTH.equals("0")){
                        if(Tap.equals("0")){
                            shardpref.putString("stub_place_id",item.getStore_no());
                            shardpref.putString("stub_user_id",item.getUser_id());
                            shardpref.putString("stub_user_account",item.getAccount());
                            pm.MemberDetail(mContext);
                        }else if(Tap.equals("1")){
                            if(!item.getBasic_pay().equals("null")
                                    || !item.getSecond_pay().equals("null")
                                    || !item.getOverwork_pay().equals("null")
                                    || !item.getMeal_allowance_yn().equals("null")
                                    || !item.getStore_insurance_yn().equals("null")
                                    || !item.getMeal_pay().equals("null")){
                                shardpref.putString("select_month",selectdate.substring(5,7));
                                shardpref.putString("stub_place_id",item.getStore_no());
                                shardpref.putString("stub_user_id",item.getUser_id());
                                shardpref.putString("stub_user_name",item.getUser_name());
                                shardpref.putString("stub_jikgup",item.getJikgup());
                                shardpref.putString("stub_basic_pay",item.getBasic_pay());
                                shardpref.putString("stub_second_pay",item.getSecond_pay());
                                shardpref.putString("stub_overwork_pay",item.getOverwork_pay());
                                shardpref.putString("stub_meal_allowance_yn",item.getMeal_allowance_yn());
                                shardpref.putString("stub_store_insurance_yn",item.getStore_insurance_yn());
                                shardpref.putString("stub_gongjeynpay",item.getGongjeynpay());
                                shardpref.putString("stub_total_payment",item.getTotal_payment());
                                shardpref.putString("stub_workday",item.getWorkday());
                                shardpref.putString("stub_total_workday",item.getTotal_workday());
                                shardpref.putString("stub_total_workhour", item.getWorkhour());
                                shardpref.putString("stub_payment",item.getPayment());
                                shardpref.putString("stub_selectdate",selectdate);
                                shardpref.putString("stub_meal_pay",item.getMeal_pay());
//                        shardpref.putString("returnPage","");
                                pm.PaystubAll(mContext);
                            }
                        }
                    }
                }
            });
        }
    }

    public void addItem(PaymentData.PaymentData_list data) {
        mData.add(data);
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    String place_owner_id = "";
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
            click_action = "Payment1";
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
