package nabera.ranjan.abhinabera.pyabigbull.DataActivities.Nifty50;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import nabera.ranjan.abhinabera.pyabigbull.Api.Utility;
import nabera.ranjan.abhinabera.pyabigbull.R;
import com.google.gson.JsonObject;

import java.util.List;

public class NiftyStocksRecyclerAdapter extends RecyclerView.Adapter<NiftyStocksRecyclerAdapter.ViewHolder> {
    public List<JsonObject> stockList;
    public Activity context;

    Clicklistener clicklistener;

    public interface Clicklistener{
        public void onClick(int pos, JsonObject stock);
    }

    public NiftyStocksRecyclerAdapter(Activity context, List<JsonObject> stockList,  Clicklistener clicklistener) {
        this.stockList = stockList;
        this.context = context;
        this.clicklistener = clicklistener;
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
        viewHolder.boxPercent.setText(pchange + "%");

        if(Double.parseDouble(pchange)>=0) {
            viewHolder.stockBox.setBackgroundColor(context.getResources().getColor(R.color.greenText));
        }else {
            viewHolder.stockBox.setBackgroundColor(context.getResources().getColor(R.color.red));
        }
        viewHolder.boxPercent.setText(stockList.get(position).get("percentchange").getAsString().trim()+"%");
    }

    // inner class to hold a reference to each item of RecyclerView 
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView companyName, price, volume, boxPrice, boxPercent;
        public LinearLayout stockBox;
        public RelativeLayout niftyStocksRow;
        public Activity mContext;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            companyName = (TextView) itemLayoutView.findViewById(R.id.niftyStocksCompanyName);
            price = (TextView) itemLayoutView.findViewById(R.id.niftyStocksPrice);
            volume = (TextView) itemLayoutView.findViewById(R.id.niftyStocksVolume);
            boxPrice = (TextView) itemLayoutView.findViewById(R.id.niftyStocksBoxPrice);
            boxPercent = (TextView) itemLayoutView.findViewById(R.id.niftyStocksBoxPercent);
            stockBox = (LinearLayout) itemLayoutView.findViewById(R.id.niftyStocksBox);

            niftyStocksRow = (RelativeLayout) itemLayoutView.findViewById(R.id.nifty);

            niftyStocksRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clicklistener.onClick(getAdapterPosition(), stockList.get(getAdapterPosition()));
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