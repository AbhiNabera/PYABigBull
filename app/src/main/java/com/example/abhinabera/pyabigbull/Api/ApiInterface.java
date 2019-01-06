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
import retrofit2.http.Url;

public interface ApiInterface {

    @GET("userStatus")
    Call<JsonObject> checkifActive(@Query("phoneNumber") String phoneNumber);

    @POST("updateuser")
    Call<JsonObject> addPlayer(@Body JsonObject object);

    @GET("userName")
    Call<JsonObject> getUserName(@Query("phoneNumber") String phoneNumber);

    @GET("userNameData")
    Call<JsonObject> getUserNameData(@Query("phoneNumber") String phoneNumber);

    @GET("expiry")
    Call<JsonObject> getCommodityExpiry();

    @GET("userAccount")
    Call<JsonObject> getUserAccount(@Query("phoneNumber") String phoneNumber);

    @GET("adminSettings")
    Call<JsonObject> getAdminSettings();

    @POST("transaction")
    Call<JsonObject> performTransaction(@Body JsonObject object);

    @POST("buytransaction")
    Call<JsonObject> executeTransaction(@Body JsonObject object);

    @POST("selltransaction")
    Call<JsonObject> executesellTransaction(@Body JsonObject object);

    @GET("userinfo")
    Call<JsonObject> getPlayerinfo(@Query("phoneNumber") String phoneNumber);

    @GET("txnHistory")
    Call<JsonObject> getTxnHistory(@Query("phoneNumber") String phoneNumber);

    @GET("buyList")
    Call<JsonObject> getbuyList(@Query("phoneNumber") String phoneNumber);

    @GET("sellList")
    Call<JsonObject> getsellList(@Query("phoneNumber") String phoneNumber);

    @GET("leaderboard")
    Call<JsonObject> getLeaderboard();

    /*
    appfeeds
     */
    @GET("jsonapi/market/indices&ind_id=9")
    Call<JsonObject> getNifty50();

    @GET("jsonapi/ticker/index&type=nifty&format=json")
    Call<List<JsonObject>> getNify50StockList();

    @GET
    Call<JsonObject> getData(@Url String url);

    @GET("jsonapi/commodity/top_commodity&ex=MCX&format=json")
    Call<JsonObject> getTopCommodity();


    /*
    priceapi
     */
    @GET("pricefeed/mcx/commodityfuture/GOLD")
    Call<JsonObject> getGold(@Query("expiry") String expiry);

    @GET("pricefeed/mcx/commodityfuture/SILVER")
    Call<JsonObject> getSilver(@Query("expiry") String expiry);

    @GET("pricefeed/mcx/commodityfuture/CRUDEOIL")
    Call<JsonObject> getCrudeoil(@Query("expiry") String expiry);

    @GET("pricefeed/notapplicable/currencyspot/%24%24%3BUSDINR")
    Call<JsonObject> getUSDINR();

    @GET("pricefeed/notapplicable/currencyspot/%24%24%3BEURINR")
    Call<JsonObject> getEURINR();

    @GET("pricefeed/notapplicable/currencyspot/%24%24%3BGBPINR")
    Call<JsonObject> getGBPINR();
}
