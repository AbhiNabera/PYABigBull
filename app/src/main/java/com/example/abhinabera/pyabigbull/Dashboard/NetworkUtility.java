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

    public void getGold(String expiry, final NetworkCallback networkCallback) {

        new RetrofitClient().getCurrencyInterface().getGold(expiry).enqueue(new Callback<JsonObject>() {
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

    public void getSilver(String expiry, final NetworkCallback networkCallback) {
        new RetrofitClient().getCurrencyInterface().getSilver(expiry).enqueue(new Callback<JsonObject>() {
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

    public void getCrudeoil(String expiry, final NetworkCallback networkCallback) {
        new RetrofitClient().getCurrencyInterface().getCrudeoil(expiry).enqueue(new Callback<JsonObject>() {
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
