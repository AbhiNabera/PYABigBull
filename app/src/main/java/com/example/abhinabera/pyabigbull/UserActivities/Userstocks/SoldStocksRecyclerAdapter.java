package com.example.abhinabera.pyabigbull.UserActivities.Userstocks;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.Api.Utility;
import com.example.abhinabera.pyabigbull.R;
import com.example.abhinabera.pyabigbull.SellActivity;
import com.google.gson.JsonObject;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SoldStocksRecyclerAdapter extends RecyclerView.Adapter<SoldStocksRecyclerAdapter.ViewHolder> {
    private List<JsonObject> stocks;
    public Activity context;

    public SoldStocksRecyclerAdapter(Activity context, List<JsonObject> stocks) {
        this.stocks = stocks;
        this.context = context;
        sdf = new SimpleDateFormat("dd/MMM/yyyy");
    }

    SimpleDateFormat sdf;

    // Create new views (invoked by the layout manager)
    @Override
    public SoldStocksRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        // create a new view
        View itemLayoutView = null;
        ViewHolder viewHolder = null;

        if(viewType == 0) {
            itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sold_stocks_row, null);
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

        if(stocks.get(position).get("TYPE")==null) {

            JsonObject transaction = stocks.get(position).getAsJsonObject();

            Log.d("position", position+"");

            viewHolder.buyprice.setText(new Utility().getRoundoffData(transaction.get("buy_price").getAsString()));
            viewHolder.sellquantity.setText(transaction.get("sell_qty").getAsString());
            viewHolder.txncharge.setText(new Utility().getRoundoffData(transaction.get("txn_amt").getAsString()));
            viewHolder.returntamount.setText(new Utility().getRoundoffData(
                    transaction.get("net_return").getAsString())+"");

            viewHolder.estimatedChange.setText("("+new Utility().getRoundoffData(
                    transaction.get("return_percentchange").getAsString())+"%)");

            viewHolder.companyName.setText(transaction.get("name").getAsString() + "");
            viewHolder.quantity.setText("Q: " + transaction.get("sell_qty").getAsString() + "");
            viewHolder.returns.setText(new Utility().getRoundoffData(transaction.get("net_return").getAsString()) + "");

            viewHolder.txn_id.setText(transaction.get("txn_id").getAsString() + "");

            try {
                viewHolder.txn_date.setText(sdf.format(new Date(transaction.get("timestamp").getAsLong())) + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

            viewHolder.sellprice.setText(new Utility().getRoundoffData(
                    transaction.get("sell_price").getAsString()));
            viewHolder.changeamt.setText(new Utility().getRoundoffData(
                    transaction.get("return_change").getAsString()));
            viewHolder.percentchangeamt.setText(new Utility().getRoundoffData(
                    transaction.get("return_percentchange").getAsString()) + "%");
            viewHolder.netchangeamt.setText(new Utility().getRoundoffData(
                    transaction.get("change").getAsString()));
            viewHolder.netpercentchangeamt.setText(new Utility().getRoundoffData(
                    transaction.get("percentchange").getAsString()) + "%");

            //viewHolder.buyOrSell.setVisibility(View.GONE);

            if(transaction.get("return_percentchange").getAsDouble()>=0) {
                viewHolder.curentStockPrice.setTextColor(context.getResources().getColor(R.color.greenText));
                viewHolder.estimatedChange.setTextColor(context.getResources().getColor(R.color.greenText));
                viewHolder.changeamt.setTextColor(context.getResources().getColor(R.color.greenText));
                viewHolder.percentchangeamt.setTextColor(context.getResources().getColor(R.color.greenText));
            }else {
                viewHolder.curentStockPrice.setTextColor(context.getResources().getColor(R.color.red));
                viewHolder.estimatedChange.setTextColor(context.getResources().getColor(R.color.red));
                viewHolder.changeamt.setTextColor(context.getResources().getColor(R.color.red));
                viewHolder.percentchangeamt.setTextColor(context.getResources().getColor(R.color.red));
            }

            if(transaction.get("percentchange").getAsDouble()>=0) {
                viewHolder.netchangeamt.setTextColor(context.getResources().getColor(R.color.greenText));
                viewHolder.netpercentchangeamt.setTextColor(context.getResources().getColor(R.color.greenText));
            }else {
                viewHolder.netchangeamt.setTextColor(context.getResources().getColor(R.color.red));
                viewHolder.netpercentchangeamt.setTextColor(context.getResources().getColor(R.color.red));
            }

        }else {
            viewHolder.stocks_divider_row.setText(stocks.get(position).get("TYPE").getAsString().toUpperCase()+"");
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView companyName, quantity, returns, buyOrSell, txn_date, txn_id, curentStockPrice, estimatedChange;
        public LinearLayout transactionsHistoryRow;
        public TextView stocks_divider_row;
        LinearLayout stockLayout;
        ImageView expand;
        ExpandableLayout expandableLayout;
        TextView buyprice, sellprice, sellquantity, txncharge, returntamount, changeamt, percentchangeamt,
                netchangeamt, netpercentchangeamt;

        public ViewHolder(View itemLayoutView, int viewType) {
            super(itemLayoutView);

            if(viewType == 0) {
                companyName = (TextView) itemLayoutView.findViewById(R.id.historyCompanyName);
                quantity = (TextView) itemLayoutView.findViewById(R.id.historyQuantity);
                returns = (TextView) itemLayoutView.findViewById(R.id.historyTotalReturn);
                buyOrSell = (TextView) itemLayoutView.findViewById(R.id.historyBuyOrSell);
                transactionsHistoryRow = (LinearLayout) itemLayoutView.findViewById(R.id.history);
                txn_date = (TextView) itemLayoutView.findViewById(R.id.txn_date);
                txn_id = (TextView) itemLayoutView.findViewById(R.id.txn_id);
                curentStockPrice = (TextView) itemLayoutView.findViewById(R.id.currentStockPrice);
                estimatedChange = (TextView) itemLayoutView.findViewById(R.id.estimatedChange);
                buyprice = (TextView) itemLayoutView.findViewById(R.id.buyprice);
                sellquantity = (TextView) itemLayoutView.findViewById(R.id.sellquantity);
                txncharge = (TextView) itemLayoutView.findViewById(R.id.txncharge);
                sellprice = (TextView) itemLayoutView.findViewById(R.id.sellprice);
                returntamount = (TextView) itemLayoutView.findViewById(R.id.returnamount);
                changeamt = (TextView) itemLayoutView.findViewById(R.id.changeamt);
                percentchangeamt = (TextView) itemLayoutView.findViewById(R.id.percentchangeamt);
                netchangeamt = (TextView) itemLayoutView.findViewById(R.id.netchangeamt);
                netpercentchangeamt = (TextView) itemLayoutView.findViewById(R.id.netpercentchangeamt);

                expand = (ImageView) itemLayoutView.findViewById(R.id.expand);

                expandableLayout = (ExpandableLayout) itemLayoutView.findViewById(R.id.expandableLayout);

                stockLayout = (LinearLayout) itemLayoutView.findViewById(R.id.stockLayout);

                stockLayout.setVisibility(View.VISIBLE);

                expand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(expandableLayout.isExpanded()) {
                            ObjectAnimator.ofFloat(expand,"rotation",180,
                                    0).start();
                        }else {
                            ObjectAnimator.ofFloat(expand, "rotation", 0,
                                    180).start();
                        }
                        expandableLayout.toggle();
                    }
                });

            }else {
                stocks_divider_row = (TextView) itemLayoutView.findViewById(R.id.dividerText);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {

        if(stocks.get(position).get("TYPE")!=null) {
            return 1;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return stocks.size();
    }
}