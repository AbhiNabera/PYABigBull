package com.ranjan.abhinabera.pyabigbull.Api;

import com.google.gson.JsonObject;

import retrofit2.Response;


public interface NetworkCallback {

    public void onSuccess(Response<JsonObject> response);

    public void onError(Throwable t);
}
