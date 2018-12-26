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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.DataActivities.CrudeoilActivity;
import com.example.abhinabera.pyabigbull.DataActivities.DollarActivity;
import com.example.abhinabera.pyabigbull.DataActivities.EuroActivity;
import com.example.abhinabera.pyabigbull.DataActivities.GoldActivity;
import com.example.abhinabera.pyabigbull.DataActivities.NiftyActivity;
import com.example.abhinabera.pyabigbull.DataActivities.PoundActivity;
import com.example.abhinabera.pyabigbull.DataActivities.SilverActivity;
import com.example.abhinabera.pyabigbull.R;

public class DataFragment extends Fragment {

    CardView niftyCard, goldCard, silverCard, crudeoilCard, dollarCard, euroCard, poundCard;


    TextView niftyDate, goldDate, silverDate, crudeoilDate, dollarDate, euroDate, poundDate,
            nifty50Rate, goldRate, silverRate, crudeoilRate, dollarRate, euroRate, poundRate,
            nifty50BoxRate, goldBoxRate, silverBoxRate, crudeoilBoxRate, dollarBoxRate, euroBoxRate, poundBoxRate,
            nifty50BoxPercent, goldBoxPercent, silverBoxPercent, crudeoilBoxPercent, dollarBoxPercent, euroBoxPercent, poundBoxPercent;

    LinearLayout nifty50Box, goldBox, silverBox, crudeoilBox, dollarBox, euroBox, poundBox;
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_data, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        niftyCard = (CardView) view.findViewById(R.id.niftyCard);
        goldCard = (CardView) view.findViewById(R.id.goldCard);
        silverCard = (CardView) view.findViewById(R.id.silverCard);
        crudeoilCard = (CardView) view.findViewById(R.id.crudeoilCard);
        dollarCard = (CardView) view.findViewById(R.id.dollarCard);
        euroCard = (CardView) view.findViewById(R.id.euroCard);
        poundCard = (CardView) view.findViewById(R.id.poundCard);

        niftyDate = (TextView) view.findViewById(R.id.niftyDate);
        goldDate = (TextView) view.findViewById(R.id.goldDate);
        silverDate = (TextView) view.findViewById(R.id.silverDate);
        crudeoilDate = (TextView) view.findViewById(R.id.crudeoilDate);
        dollarDate = (TextView) view.findViewById(R.id.dollarDate);
        euroDate = (TextView) view.findViewById(R.id.euroDate);
        poundDate = (TextView) view.findViewById(R.id.poundDate);
        nifty50Rate = (TextView) view.findViewById(R.id.nifty50Rate);
        goldRate = (TextView) view.findViewById(R.id.goldRate);
        silverRate = (TextView) view.findViewById(R.id.silverRate);
        crudeoilRate = (TextView) view.findViewById(R.id.crudeoilRate);
        dollarRate = (TextView) view.findViewById(R.id.dollarRate);
        euroRate = (TextView) view.findViewById(R.id.euroRate);
        poundRate = (TextView) view.findViewById(R.id.poundRate);
        nifty50BoxRate = (TextView) view.findViewById(R.id.nifty50BoxRate);
        goldBoxRate = (TextView) view.findViewById(R.id.goldBoxRate);
        silverBoxRate = (TextView) view.findViewById(R.id.silverBoxRate);
        crudeoilBoxRate = (TextView) view.findViewById(R.id.crudeoilBoxRate);
        dollarBoxRate = (TextView) view.findViewById(R.id.dollarBoxRate);
        euroBoxRate = (TextView) view.findViewById(R.id.euroBoxRate);
        poundBoxRate = (TextView) view.findViewById(R.id.poundBoxRate);
        nifty50BoxPercent = (TextView) view.findViewById(R.id.nifty50BoxPercent);
        goldBoxPercent = (TextView) view.findViewById(R.id.goldBoxPercent);
        silverBoxPercent = (TextView) view.findViewById(R.id.silverBoxPercent);
        crudeoilBoxPercent = (TextView) view.findViewById(R.id.crudeoilBoxPercent);
        dollarBoxPercent = (TextView) view.findViewById(R.id.dollarBoxPercent);
        euroBoxPercent = (TextView) view.findViewById(R.id.euroBoxPercent);
        poundBoxPercent = (TextView) view.findViewById(R.id.poundBoxPercent);

        nifty50Box = (LinearLayout) view.findViewById(R.id.nifty50Box);
        goldBox = (LinearLayout) view.findViewById(R.id.goldBox);
        silverBox = (LinearLayout) view.findViewById(R.id.silverBox);
        crudeoilBox = (LinearLayout) view.findViewById(R.id.crudeoilBox);
        dollarBox = (LinearLayout) view.findViewById(R.id.dollarBox);
        euroBox = (LinearLayout) view.findViewById(R.id.euroBox);
        poundBox = (LinearLayout) view.findViewById(R.id.poundBox);

        niftyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), NiftyActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        goldCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), GoldActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        silverCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SilverActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        crudeoilCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CrudeoilActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        dollarCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), DollarActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        euroCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), EuroActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        poundCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), PoundActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

    }
}
