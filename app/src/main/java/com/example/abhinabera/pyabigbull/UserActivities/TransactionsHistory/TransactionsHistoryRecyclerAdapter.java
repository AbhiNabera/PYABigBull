package com.example.abhinabera.pyabigbull.UserActivities.TransactionsHistory;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.DataActivities.Nifty50.NiftyStocksIndividual;
import com.example.abhinabera.pyabigbull.R;

public class TransactionsHistoryRecyclerAdapter extends RecyclerView.Adapter<TransactionsHistoryRecyclerAdapter.ViewHolder> {
    private TransactionsHistoryData[] itemsData;

    public TransactionsHistoryRecyclerAdapter(TransactionsHistoryData[] itemsData) {
        this.itemsData = itemsData;
    }

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

        viewHolder.companyName.setText(itemsData[position].getCompanyName());
        viewHolder.quantity.setText(itemsData[position].getQuantity());
        viewHolder.buyOrSell.setText(itemsData[position].getBuyOrSell());
        viewHolder.investment.setText(itemsData[position].getInvestment());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView companyName, quantity, investment, buyOrSell;
        public RelativeLayout transactionsHistoryRow;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            companyName = (TextView) itemLayoutView.findViewById(R.id.historyCompanyName);
            quantity = (TextView) itemLayoutView.findViewById(R.id.historyQuantity);
            investment = (TextView) itemLayoutView.findViewById(R.id.historyTotalInvestment);
            buyOrSell = (TextView) itemLayoutView.findViewById(R.id.historyBuyOrSell);
            transactionsHistoryRow = (RelativeLayout) itemLayoutView.findViewById(R.id.history);

            transactionsHistoryRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(itemLayoutView.getContext(), TransactionsHistoryIndi.class);
                    itemLayoutView.getContext().startActivity(i);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemsData.length;
    }
}