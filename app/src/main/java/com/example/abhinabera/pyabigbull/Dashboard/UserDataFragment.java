package com.example.abhinabera.pyabigbull.Dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.example.abhinabera.pyabigbull.Api.Utility;
import com.example.abhinabera.pyabigbull.Login.RegistrationActivity;
import com.example.abhinabera.pyabigbull.R;
import com.example.abhinabera.pyabigbull.UserActivities.About;
import com.example.abhinabera.pyabigbull.UserActivities.Disclaimer;
import com.example.abhinabera.pyabigbull.UserActivities.TermsAndConditions;
import com.example.abhinabera.pyabigbull.UserActivities.TransactionsHistory.TransactionsHistory;
import com.example.abhinabera.pyabigbull.UserActivities.Userstocks.UserStocks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDataFragment extends Fragment {

    TextView myStocks, transactionsHistory, termsAndConditions, disclaimer, about, logout;
    TextView userName, phoneNumber, balance, investmentText, changeText, percentchangeText;

    SwipeRefreshLayout refreshLayout;
    LinearLayout accountLayout;
    ExpandableLayout expandableLayout;

    JsonObject player;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_user_data_c, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        myStocks = (TextView) view.findViewById(R.id.myStocksText);
        transactionsHistory = (TextView) view.findViewById(R.id.transactionsHistoryText);
        termsAndConditions = (TextView) view.findViewById(R.id.termsAndConditionsText);
        disclaimer = (TextView) view.findViewById(R.id.disclaimerText);
        about = (TextView) view.findViewById(R.id.aboutText);
        logout = (TextView) view.findViewById(R.id.logoutText);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        expandableLayout = (ExpandableLayout) view.findViewById(R.id.expandableLayout);
        accountLayout = (LinearLayout) view.findViewById(R.id.accountLayout);

        userName = (TextView) view.findViewById(R.id.userNameText);
        phoneNumber = (TextView) view.findViewById(R.id.phoneNumberText);
        balance = (TextView) view.findViewById(R.id.balanceText);
        investmentText = (TextView) view.findViewById(R.id.investmentText);
        changeText = (TextView) view.findViewById(R.id.changeText);
        percentchangeText = (TextView) view.findViewById(R.id.percentchangeText);

        myStocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), UserStocks.class);
                i.putExtra("name", "MY PORTFOLIO");
                i.putExtra("acc_bal", player.get("data").getAsJsonObject().get("Account").getAsJsonObject()
                        .get("avail_balance").getAsString());
                startActivity(i);
                //getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        transactionsHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), TransactionsHistory.class);
                i.putExtra("name", "HISTORY");
                startActivity(i);
                //getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        termsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), TermsAndConditions.class);
                i.putExtra("name", "PLAYING CONDITIONS");
                startActivity(i);
                //getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        disclaimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Disclaimer.class);
                i.putExtra("name", "DISCLAIMER");
                startActivity(i);
                //getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), About.class);
                i.putExtra("name", "ABOUT");
                startActivity(i);
                //getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                removeDataFromSP();
                startActivity(new Intent(getActivity(), RegistrationActivity.class));
                getActivity().finish();
            }
        });

        accountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandableLayout.toggle();
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserInfo();
            }
        });

        getUserInfo();
    }

    public void setUserCard() {

        phoneNumber.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+"");
        userName.setText(player.get("data").getAsJsonObject().get("userName").getAsString());

        balance.setText(new Utility().getRoundoffData(player.get("data").getAsJsonObject().get("Account").getAsJsonObject()
                .get("avail_balance").getAsString()));
        investmentText.setText(new Utility().getRoundoffData(player.get("data").getAsJsonObject().get("Account").getAsJsonObject()
                .get("investment").getAsString()));

        String change = new Utility().getRoundoffData(player.get("data").getAsJsonObject().get("Account").getAsJsonObject()
                .get("change").getAsString());
        String percentchange = new Utility().getRoundoffData(player.get("data").getAsJsonObject().get("Account").getAsJsonObject()
                .get("percentchange").getAsString());

        changeText.setText(change+"");
        percentchangeText.setText(percentchange+"%");

        if(Double.parseDouble(percentchange)>=0) {
            changeText.setTextColor(getResources().getColor(R.color.greenText));
            percentchangeText.setTextColor(getResources().getColor(R.color.greenText));
        }else {
            changeText.setTextColor(getResources().getColor(R.color.red));
            percentchangeText.setTextColor(getResources().getColor(R.color.red));
        }

        myStocks.setText("My Portfolio ("+player.get("data").getAsJsonObject().get("Account").getAsJsonObject()
        .get("stocks_count").getAsString()+")");

        transactionsHistory.setText("Transaction History ("+player.get("data").getAsJsonObject().get("Account").getAsJsonObject()
        .get("txn_history").getAsJsonArray().size()+")");

        //expandableLayout.toggle();
    }

    public void getUserInfo() {

        refreshLayout.setRefreshing(true);

        new RetrofitClient().getInterface().getPlayerinfo(FirebaseAuth.getInstance().getCurrentUser().
                getPhoneNumber().substring(3)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                refreshLayout.setRefreshing(false);

                if(response.isSuccessful()) {
                    if(response.body().getAsJsonObject("data")!=null) {
                        player = response.body();
                        setUserCard();
                    }else {

                        Toast.makeText(getActivity(), "error occured while fetching account", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    try {
                        Log.d("error", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    public void removeDataFromSP() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Utility.MyPREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("phoneNumber");
        editor.remove("userName");
        editor.apply();
        editor.commit();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
