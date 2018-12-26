package com.example.abhinabera.pyabigbull.Dashboard;

import com.example.abhinabera.pyabigbull.Api.NetworkCallback;
import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkUtility {

    public void getNifty50(final NetworkCallback networkCallback) {

        new RetrofitClient().getNifty50Interface().getNifty50().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                networkCallback.onSuccess(response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                networkCallback.onError(t);
            }
        });
    }

    public void getUSDINR(final NetworkCallback networkCallback) {

        new RetrofitClient().getCurrencyInterface().getUSDINR().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                networkCallback.onSuccess(response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                networkCallback.onError(t);
            }
        });
    }

    public void getEURINR(final NetworkCallback networkCallback) {

        new RetrofitClient().getCurrencyInterface().getEURINR().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                networkCallback.onSuccess(response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                networkCallback.onError(t);
            }
        });
    }

    public void getGBPINR(final NetworkCallback networkCallback) {

        new RetrofitClient().getCurrencyInterface().getGBPINR().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                networkCallback.onSuccess(response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                networkCallback.onError(t);
            }
        });
    }
}
