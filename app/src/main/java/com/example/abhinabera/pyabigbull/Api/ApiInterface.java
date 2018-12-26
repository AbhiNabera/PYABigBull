package com.example.abhinabera.pyabigbull.Api;


import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("addPlayer")
    Call<JsonObject> addPlayer(@Field("phoneNumber") String phoneNumber,
                               @Field("userName") String userName,
                               @Field("prevUserName") String prevUserName);

    @GET("userName")
    Call<JsonObject> getUserName(@Query("phoneNumber") String phoneNumber);

    @GET("expiry")
    Call<JsonObject> getCommodityExpiry();

    @GET("jsonapi/market/indices&ind_id=9")
    Call<JsonObject> getNifty50();

    @GET("pricefeed/notapplicable/currencyspot/%24%24%3BUSDINR")
    Call<JsonObject> getUSDINR();

    @GET("pricefeed/notapplicable/currencyspot/%24%24%3BEURINR")
    Call<JsonObject> getEURINR();

    @GET("pricefeed/notapplicable/currencyspot/%24%24%3BGBPINR")
    Call<JsonObject> getGBPINR();
}
