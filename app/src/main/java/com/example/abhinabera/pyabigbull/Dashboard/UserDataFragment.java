package com.example.abhinabera.pyabigbull.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.R;
import com.example.abhinabera.pyabigbull.UserActivities.About;
import com.example.abhinabera.pyabigbull.UserActivities.TermsAndConditions;
import com.example.abhinabera.pyabigbull.UserActivities.TransactionsHistory;
import com.example.abhinabera.pyabigbull.UserActivities.UserStocks;

public class UserDataFragment extends Fragment {

    CardView myStocks, transactionsHistory, termsAndConditions, about, logout;
    TextView userName, phoneNumber, balance, rank;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_user_data, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myStocks = (CardView) view.findViewById(R.id.myStocksCard);
        transactionsHistory = (CardView) view.findViewById(R.id.transactionsHistoryCard);
        termsAndConditions = (CardView) view.findViewById(R.id.termsAndCondtionsCard);
        about = (CardView) view.findViewById(R.id.aboutCard);
        logout = (CardView) view.findViewById(R.id.logoutCard);

        userName = (TextView) view.findViewById(R.id.userNameText);
        phoneNumber = (TextView) view.findViewById(R.id.phoneNumberText);
        balance = (TextView) view.findViewById(R.id.balanceText);
        rank = (TextView) view.findViewById(R.id.rankText);

        myStocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), UserStocks.class);
                i.putExtra("name", "MY STOCKS");
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        transactionsHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), TransactionsHistory.class);
                i.putExtra("name", "HISTORY");
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        termsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), TermsAndConditions.class);
                i.putExtra("name", "TERMS AND CONDITIONS");
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), About.class);
                i.putExtra("name", "ABOUT");
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}
