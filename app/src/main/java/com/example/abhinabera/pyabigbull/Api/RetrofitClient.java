package com.example.abhinabera.pyabigbull.Api;

import com.example.abhinabera.pyabigbull.Utility;
import com.google.android.gms.common.api.Api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public ApiInterface getInterface() {

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utility.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                //.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(ApiInterface.class);
    }

    public ApiInterface getNifty50Interface() {

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utility.MONEY_CONTROL_NIFTY50_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                //.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(ApiInterface.class);
    }

    public ApiInterface getCurrencyInterface() {

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utility.MONEY_CONTROL_CURRENCY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                //.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(ApiInterface.class);
    }
}
