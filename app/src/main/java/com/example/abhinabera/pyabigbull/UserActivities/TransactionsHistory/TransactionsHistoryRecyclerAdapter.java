package com.example.abhinabera.pyabigbull.UserActivities.TransactionsHistory;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.Api.Utility;
import com.example.abhinabera.pyabigbull.R;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TransactionsHistoryRecyclerAdapter extends RecyclerView.Adapter<TransactionsHistoryRecyclerAdapter.ViewHolder> {
    private List<JsonObject> transactions;
    public Activity context;

    public TransactionsHistoryRecyclerAdapter(Activity context, List<JsonObject> transactions) {
        this.transactions = transactions;
        this.context = context;
        sdf = new SimpleDateFormat("dd/MMM/yyyy");
    }

    SimpleDateFormat sdf;

    // Create new views (invoked by the layout manager)
    @Override
    public TransactionsHistoryRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                            int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transactions_history_row, null);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        JsonObject transaction = transactions.get(position).getAsJsonObject().get("txn_summary").getAsJsonObject();

        viewHolder.companyName.setText(transaction.get("name").getAsString()+"");
        viewHolder.buyOrSell.setText(transactions.get(position).getAsJsonObject().get("txn_type").getAsString().toUpperCase()+"");

        viewHolder.txn_id.setText(transaction.get("txn_id").getAsString()+"");
        try {
            viewHolder.txn_date.setText(sdf.format(new Date(transaction.get("timestamp").getAsLong()))+"");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(viewHolder.buyOrSell.getText().toString().trim().equalsIgnoreCase("SELL")) {
            viewHolder.quantity.setText("Q: "+transaction.get("sell_qty").getAsString()+"");
            viewHolder.investment.setText(new Utility().getRoundoffData(transaction.get("net_return").getAsString())+"");
            viewHolder.buyOrSell.setTextColor(context.getResources().getColor(R.color.greenText));
        }else {
            viewHolder.quantity.setText("Q: "+transaction.get("qty").getAsString()+"");
            viewHolder.investment.setText(new Utility().getRoundoffData(transaction.get("total_amount").getAsString())+"");
            viewHolder.buyOrSell.setTextColor(context.getResources().getColor(R.color.red));
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView companyName, quantity, investment, buyOrSell, txn_date, txn_id;
        public RelativeLayout transactionsHistoryRow;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            companyName = (TextView) itemLayoutView.findViewById(R.id.historyCompanyName);
            quantity = (TextView) itemLayoutView.findViewById(R.id.historyQuantity);
            investment = (TextView) itemLayoutView.findViewById(R.id.historyTotalInvestment);
            buyOrSell = (TextView) itemLayoutView.findViewById(R.id.historyBuyOrSell);
            transactionsHistoryRow = (RelativeLayout) itemLayoutView.findViewById(R.id.history);
            txn_date = (TextView) itemLayoutView.findViewById(R.id.txn_date);
            txn_id = (TextView) itemLayoutView.findViewById(R.id.txn_id);

            transactionsHistoryRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(transactions.get(getAdapterPosition()).getAsJsonObject().get("txn_type").getAsString().equals("buy")) {
                        Intent i = new Intent(itemLayoutView.getContext(), TransactionsHistoryIndiPurchase.class);
                        i.putExtra("transactionHistory", transactions.get(getAdapterPosition()).toString());
                        Log.d("transactionHistory", transactions.get(getAdapterPosition()).getAsJsonObject().get("txn_type").getAsString());
                        itemLayoutView.getContext().startActivity(i);
                        context.overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                    else{
                        Intent i = new Intent(itemLayoutView.getContext(), TransactionsHistoryIndiSale.class);
                        i.putExtra("transactionHistory", transactions.get(getAdapterPosition()).toString());
                        itemLayoutView.getContext().startActivity(i);
                        context.overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }
}