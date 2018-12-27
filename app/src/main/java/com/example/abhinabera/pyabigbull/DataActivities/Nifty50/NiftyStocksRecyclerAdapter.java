package com.example.abhinabera.pyabigbull.DataActivities.Nifty50;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.R;

public class NiftyStocksRecyclerAdapter extends RecyclerView.Adapter<NiftyStocksRecyclerAdapter.ViewHolder> {
    private NiftyStocksData[] itemsData;

    public NiftyStocksRecyclerAdapter(NiftyStocksData[] itemsData) {
        this.itemsData = itemsData;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NiftyStocksRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nifty_stocks_recycler_row, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData
    }

    // inner class to hold a reference to each item of RecyclerView 
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView companyName, price, volume, boxPrice, boxPercent;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            companyName = (TextView) itemLayoutView.findViewById(R.id.niftyStocksCompanyName);
            price = (TextView) itemLayoutView.findViewById(R.id.niftyStocksPrice);
            volume = (TextView) itemLayoutView.findViewById(R.id.niftyStocksVolume);
            boxPrice = (TextView) itemLayoutView.findViewById(R.id.niftyStocksBoxPrice);
            boxPercent = (TextView) itemLayoutView.findViewById(R.id.niftyStocksBoxPercent);
        }
    }


    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemsData.length;
    }
}