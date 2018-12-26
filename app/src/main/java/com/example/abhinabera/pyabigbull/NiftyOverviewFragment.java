package com.example.abhinabera.pyabigbull;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.R;

public class NiftyOverviewFragment extends Fragment {

    TextView openPrice, prevClose, todaysLow, todaysHigh, wkLow, wkHigh;

    public NiftyOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nifty_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        openPrice = (TextView) view.findViewById(R.id.openPriceTextView);
        prevClose = (TextView) view.findViewById(R.id.prevCloseTextView);
        todaysLow = (TextView) view.findViewById(R.id.todaysLowTextView);
        todaysHigh = (TextView) view.findViewById(R.id.todaysHighTextView);
        wkLow = (TextView) view.findViewById(R.id.wkLowTextView);
        wkHigh = (TextView) view.findViewById(R.id.wkHighTextView);
    }
}
