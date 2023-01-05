package com.krafte.nebworks.util;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.krafte.nebworks.dataInterface.UserSelectInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitDBC {
    Context mContext;
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    RetrofitConnect rc = new RetrofitConnect();
    String jsonResponse = "";

    public RetrofitDBC(Context context) {
        this.mContext = context;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);
    }

    public String LoginCheck(String account) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserSelectInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        UserSelectInterface api = retrofit.create(UserSelectInterface.class);
        Call<String> call = api.getData(account);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dlog.i("에러1 = " + response.body());
                    jsonResponse = rc.getBase64decode(response.body());
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러1 = " + t.getMessage());
                jsonResponse = t.getMessage();
            }
        });
        return jsonResponse;
    }
}
