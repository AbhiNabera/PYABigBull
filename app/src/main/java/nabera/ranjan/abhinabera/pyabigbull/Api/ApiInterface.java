package nabera.ranjan.abhinabera.pyabigbull.Api;


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

    @GET("userNameData")
    Call<JsonObject> getUserNameData(@Query("phoneNumber") String phoneNumber);

    @GET("expiry")
    Call<JsonObject> getCommodityExpiry();

    @GET("userAccount")
    Call<JsonObject> getUserAccount(@Query("phoneNumber") String phoneNumber, @Query("item_type") String item_type);

    @GET("userTxnAccount")
    Call<JsonObject> getUserTxnData(@Query("phoneNumber") String phoneNumber, @Query("item_type") String item_type);

    @GET("adminSettings")
    Call<JsonObject> getAdminSettings();

    @POST("txn")
    Call<JsonObject> performTransaction(@Body JsonObject object);

    @GET("userinfo")
    Call<JsonObject> getPlayerinfo(@Query("phoneNumber") String phoneNumber);

    @GET("txnHistory")
    Call<JsonObject> getTxnHistory(@Query("phoneNumber") String phoneNumber);

    @GET("boughtfragmentinfo")
    Call<JsonObject> getBoughtFragmentData(@Query("phoneNumber") String phoneNumber);

    @GET("sellList")
    Call<JsonObject> getsellList(@Query("phoneNumber") String phoneNumber);

    @GET("leaderboard")
    Call<JsonObject> getLeaderboard();

    @GET("standings")
    Call<JsonObject> getStandings();

    @FormUrlEncoded
    @POST("imageUrl")
    Call<JsonObject> updateUrl(@Field("phoneNumber") String phoneNumber, @Field("imageUrl") String imageUrl);

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