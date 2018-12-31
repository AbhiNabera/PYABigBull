package com.example.abhinabera.pyabigbull.UserActivities.Userstocks;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.Api.Utility;
import com.example.abhinabera.pyabigbull.R;
import com.example.abhinabera.pyabigbull.UserActivities.TransactionsHistory.TransactionsHistoryIndi;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StocksRecyclerAdapter extends RecyclerView.Adapter<StocksRecyclerAdapter.ViewHolder> {
    private List<JsonObject> stocks;
    public Activity context;

    public StocksRecyclerAdapter(Activity context, List<JsonObject> stocks) {
        this.stocks = stocks;
        this.context = context;
        sdf = new SimpleDateFormat("dd MMM yyyy");
    }

    SimpleDateFormat sdf;

    // Create new views (invoked by the layout manager)
    @Override
    public StocksRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View itemLayoutView = null;
        ViewHolder viewHolder = null;

        if(viewType == 0) {
            itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.stocks_row, null);
            viewHolder = new ViewHolder(itemLayoutView, 0);
        }

        else if(viewType == 1) {
            itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.stocks_divider_row, null);
            viewHolder = new ViewHolder(itemLayoutView,1);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        if(stocks.get(position).get("type")==null) {

            JsonObject transaction = stocks.get(position).getAsJsonObject();

            viewHolder.curentStockPrice.setText("Current price: "+transaction.get("current_price").getAsString());
            viewHolder.estimatedChange.setText("("+new Utility().getRoundoffData(
                    transaction.get("pchange").getAsString())+"%)");

            viewHolder.companyName.setText(transaction.get("name").getAsString() + "");
            viewHolder.quantity.setText("Q: " + transaction.get("qty").getAsString() + "");
            viewHolder.investment.setText(new Utility().getRoundoffData(transaction.get("total_amount").getAsString()) + "");

            viewHolder.txn_id.setText(transaction.get("txn_id").getAsString() + "");

            try {
                viewHolder.txn_date.setText(sdf.format(new Date(transaction.get("timestamp").getAsLong())) + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

            viewHolder.buyOrSell.setVisibility(View.GONE);

            if(transaction.get("pchange").getAsDouble()>=0) {
                viewHolder.curentStockPrice.setTextColor(context.getResources().getColor(R.color.greenText));
                viewHolder.estimatedChange.setTextColor(context.getResources().getColor(R.color.greenText));
            }else {
                viewHolder.curentStockPrice.setTextColor(context.getResources().getColor(R.color.red));
                viewHolder.estimatedChange.setTextColor(context.getResources().getColor(R.color.red));
            }

        }else {
            viewHolder.stocks_divider_row.setText(stocks.get(position).get("type").getAsString().toUpperCase()+"");
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView companyName, quantity, investment, buyOrSell, txn_date, txn_id, curentStockPrice, estimatedChange;
        public RelativeLayout transactionsHistoryRow;
        public TextView stocks_divider_row;
        LinearLayout stockLayout;

        public ViewHolder(View itemLayoutView, int viewType) {
            super(itemLayoutView);

            if(viewType == 0) {
                companyName = (TextView) itemLayoutView.findViewById(R.id.historyCompanyName);
                quantity = (TextView) itemLayoutView.findViewById(R.id.historyQuantity);
                investment = (TextView) itemLayoutView.findViewById(R.id.historyTotalInvestment);
                buyOrSell = (TextView) itemLayoutView.findViewById(R.id.historyBuyOrSell);
                transactionsHistoryRow = (RelativeLayout) itemLayoutView.findViewById(R.id.history);
                txn_date = (TextView) itemLayoutView.findViewById(R.id.txn_date);
                txn_id = (TextView) itemLayoutView.findViewById(R.id.txn_id);
                curentStockPrice = (TextView) itemLayoutView.findViewById(R.id.currentStockPrice);
                estimatedChange = (TextView) itemLayoutView.findViewById(R.id.estimatedChange);

                stockLayout = (LinearLayout) itemLayoutView.findViewById(R.id.stockLayout);

                stockLayout.setVisibility(View.VISIBLE);

                transactionsHistoryRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(itemLayoutView.getContext(), TransactionsHistoryIndi.class);
                        itemLayoutView.getContext().startActivity(i);
                        context.overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                });

            }else {
                stocks_divider_row = (TextView) itemLayoutView.findViewById(R.id.dividerText);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {

        if(stocks.get(position).get("type")!=null) {
            return 1;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return stocks.size();
    }
}