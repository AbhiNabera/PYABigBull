package com.example.abhinabera.pyabigbull.Api;


import com.example.abhinabera.pyabigbull.Utility;
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

    @FormUrlEncoded
    @POST("addPlayer")
    Call<JsonObject> addPlayer(@Field("phoneNumber") String phoneNumber,
                               @Field("userName") String userName,
                               @Field("prevUserName") String prevUserName);

    @GET("userName")
    Call<JsonObject> getUserName(@Query("phoneNumber") String phoneNumber);

    @GET("expiry")
    Call<JsonObject> getCommodityExpiry();

    //appfeeds
    @GET("jsonapi/market/indices&ind_id=9")
    Call<JsonObject> getNifty50();

    @GET
    Call<JsonObject> getNift50Graph(@Url String url);

    /*//appfeeds: to fetch any specific stck(depricated)
    @GET("jsonapi/stocks/overview&format=json&sc_id=MPS")//for adani port
    Call<JsonObject> getNift50company();
    */

    //appfeeds: Get graph of specific stock
    @GET
    Call<JsonObject> getStockGraph(@Url String url);

    //appfeeds: json array of all the stocks
    @GET("jsonapi/ticker/index&type=nifty&format=json")
    Call<JsonObject> getNifty50stocks();

    //priceapi
    @GET("pricefeed/mcx/commodityfuture/GOLD")
    Call<JsonObject> getGold(@Query("expiry") String expiry);

    //priceapi
    @GET("pricefeed/mcx/commodityfuture/SILVER")
    Call<JsonObject> getSilver(@Query("expiry") String expiry);

    //priceapi
    @GET("pricefeed/mcx/commodityfuture/CRUDEOIL")
    Call<JsonObject> getCrudeoil(@Query("expiry") String expiry);

    //priceapi
    @GET("pricefeed/notapplicable/currencyspot/%24%24%3BUSDINR")
    Call<JsonObject> getUSDINR();

    //priceapi
    @GET("pricefeed/notapplicable/currencyspot/%24%24%3BEURINR")
    Call<JsonObject> getEURINR();

    //priceapi
    @GET("pricefeed/notapplicable/currencyspot/%24%24%3BGBPINR")
    Call<JsonObject> getGBPINR();
}
