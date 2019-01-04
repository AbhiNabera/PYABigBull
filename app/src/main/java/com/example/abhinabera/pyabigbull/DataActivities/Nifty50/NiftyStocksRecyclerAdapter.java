package com.example.abhinabera.pyabigbull.DataActivities.Nifty50;

<<<<<<< Updated upstream
import android.content.Context;
=======
>>>>>>> Stashed changes
import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
<<<<<<< Updated upstream
import android.widget.LinearLayout;
=======
>>>>>>> Stashed changes
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.Api.Utility;
import com.example.abhinabera.pyabigbull.R;
import com.google.gson.JsonObject;

import java.util.List;

public class NiftyStocksRecyclerAdapter extends RecyclerView.Adapter<NiftyStocksRecyclerAdapter.ViewHolder> {
<<<<<<< Updated upstream
    public List<JsonObject> stockList;
    public Context context;
=======

    private NiftyStocksData[] itemsData;
>>>>>>> Stashed changes

    public NiftyStocksRecyclerAdapter(Context context, List<JsonObject> stockList) {
        this.stockList = stockList;
        this.context = context;
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
        viewHolder.companyName.setText(stockList.get(position).get("shortname").getAsString().trim()+"");
        viewHolder.price.setText(new Utility().getRoundoffData(stockList.get(position).get("lastvalue").getAsString()).trim()+"");
        viewHolder.volume.setText("Vol: " + stockList.get(position).get("volume").getAsString().trim()+"");
        viewHolder.boxPrice.setText(new Utility().getRoundoffData(stockList.get(position).get("change").getAsString()).trim()+"");

        String pchange = new Utility().getRoundoffData(stockList.get(position).get("percentchange").getAsString()).trim()+"";
        viewHolder.boxPercent.setText(pchange);

        if(Double.parseDouble(pchange)>=0) {
            viewHolder.stockBox.setBackgroundColor(context.getResources().getColor(R.color.greenText));
        }else {
            viewHolder.stockBox.setBackgroundColor(context.getResources().getColor(R.color.red));
        }
        viewHolder.boxPercent.setText(stockList.get(position).get("percentchange").getAsString().trim()+"");
    }

    // inner class to hold a reference to each item of RecyclerView 
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView companyName, price, volume, boxPrice, boxPercent;
<<<<<<< Updated upstream
        public LinearLayout stockBox;
=======
>>>>>>> Stashed changes
        public RelativeLayout niftyStocksRow;
        public Activity mContext;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            companyName = (TextView) itemLayoutView.findViewById(R.id.niftyStocksCompanyName);
            price = (TextView) itemLayoutView.findViewById(R.id.niftyStocksPrice);
            volume = (TextView) itemLayoutView.findViewById(R.id.niftyStocksVolume);
            boxPrice = (TextView) itemLayoutView.findViewById(R.id.niftyStocksBoxPrice);
            boxPercent = (TextView) itemLayoutView.findViewById(R.id.niftyStocksBoxPercent);
<<<<<<< Updated upstream
            stockBox = (LinearLayout) itemLayoutView.findViewById(R.id.niftyStocksBox);

            niftyStocksRow = (RelativeLayout) itemLayoutView.findViewById(R.id.nifty);
=======

            niftyStocksRow = (RelativeLayout) itemLayoutView.findViewById(R.id.niftyStocksRow);
>>>>>>> Stashed changes

            niftyStocksRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(itemLayoutView.getContext(), NiftyStocksIndividual.class);
                    i.putExtra("companyName", companyName.getText().toString());
<<<<<<< Updated upstream
                    i.putExtra("id", stockList.get(getAdapterPosition()).get("id").getAsString());
                    itemLayoutView.getContext().startActivity(i);
                    //mContext = (Activity) itemLayoutView.getContext();
                    //mContext.overridePendingTransition(R.anim.enter, R.anim.exit);
=======
                    itemLayoutView.getContext().startActivity(i);
                    mContext = (Activity) itemLayoutView.getContext();
                    mContext.overridePendingTransition(R.anim.enter, R.anim.exit);
>>>>>>> Stashed changes
                }
            });
        }
    }


    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return stockList.size();
    }
}