package nabera.ranjan.abhinabera.pyabigbull.UserActivities.Userstocks;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import nabera.ranjan.abhinabera.pyabigbull.Api.Utility;
import nabera.ranjan.abhinabera.pyabigbull.R;
import com.google.gson.JsonObject;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StocksRecyclerAdapter extends RecyclerView.Adapter<StocksRecyclerAdapter.ViewHolder> {
    private List<JsonObject> stocks;
    public Activity context;

    public StocksRecyclerAdapter(Activity context, List<JsonObject> stocks, ClickListener clickListener) {
        this.stocks = stocks;
        this.context = context;
        sdf = new SimpleDateFormat("dd/MMM/yyyy");
        this.clickListener = clickListener;
    }

    ClickListener clickListener;

    public interface ClickListener {

        public void onItemClick(List<JsonObject> stocks, int position);
    }

    SimpleDateFormat sdf;

    // Create new views (invoked by the layout manager)
    @Override
    public StocksRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType ) {
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

        Log.d("position", position+"");

        if(stocks.get(position).get("TYPE")==null) {

            JsonObject transaction = stocks.get(position).getAsJsonObject();

            viewHolder.currentvalue.setText(new Utility().getRoundoffData(transaction.get("current_value").getAsString()));
            viewHolder.changeamount.setText(new Utility().getRoundoffData(transaction.get("changeamount").getAsString()));

            viewHolder.buyprice.setText(new Utility().getRoundoffData(transaction.get("buy_price").getAsString()));
            viewHolder.buyquantity.setText(transaction.get("qty").getAsString());
            viewHolder.txncharge.setText(new Utility().getRoundoffData(transaction.get("txn_amt").getAsString()));
            viewHolder.investmentamount.setText(new Utility().getRoundoffData(
                    transaction.get("total_amount").getAsString()));

            viewHolder.curentStockPrice.setText("Current price: "+new Utility().getRoundoffData
                    (transaction.get("current_price").getAsString()));
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
                viewHolder.changeamount.setTextColor(context.getResources().getColor(R.color.greenText));
            }else {
                viewHolder.curentStockPrice.setTextColor(context.getResources().getColor(R.color.red));
                viewHolder.estimatedChange.setTextColor(context.getResources().getColor(R.color.red));
                viewHolder.changeamount.setTextColor(context.getResources().getColor(R.color.red));
            }

            if(stocks.get(position).get("id").getAsString().equalsIgnoreCase("FD")) {
                //viewHolder.stockLayout.setVisibility(View.GONE);
                viewHolder.curentStockPrice.setText("Fixed deposit");
                viewHolder.estimatedChange.setText("");
                viewHolder.curentStockPrice.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }

        }else {
            viewHolder.stocks_divider_row.setText(stocks.get(position).get("TYPE").getAsString().toUpperCase()+"");
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView companyName, quantity, investment, buyOrSell, txn_date, txn_id, curentStockPrice, estimatedChange;
        public LinearLayout transactionsHistoryRow;
        public TextView stocks_divider_row;
        TextView sellRecyclerBtn;
        LinearLayout stockLayout;
        ImageView expand;
        ExpandableLayout expandableLayout;
        RelativeLayout nextact;
        TextView buyprice, buyquantity, txncharge, investmentamount, changeamount, currentvalue;

        public ViewHolder(View itemLayoutView, int viewType) {
            super(itemLayoutView);

            if(viewType == 0) {
                companyName = (TextView) itemLayoutView.findViewById(R.id.historyCompanyName);
                quantity = (TextView) itemLayoutView.findViewById(R.id.historyQuantity);
                investment = (TextView) itemLayoutView.findViewById(R.id.historyTotalInvestment);
                buyOrSell = (TextView) itemLayoutView.findViewById(R.id.historyBuyOrSell);
                transactionsHistoryRow = (LinearLayout) itemLayoutView.findViewById(R.id.history);
                sellRecyclerBtn = (TextView) itemLayoutView.findViewById(R.id.sellRecyclerBtn);
                txn_date = (TextView) itemLayoutView.findViewById(R.id.txn_date);
                txn_id = (TextView) itemLayoutView.findViewById(R.id.txn_id);
                curentStockPrice = (TextView) itemLayoutView.findViewById(R.id.currentStockPrice);
                estimatedChange = (TextView) itemLayoutView.findViewById(R.id.estimatedChange);
                buyprice = (TextView) itemLayoutView.findViewById(R.id.buyprice);
                buyquantity = (TextView) itemLayoutView.findViewById(R.id.buyquantity);
                txncharge = (TextView) itemLayoutView.findViewById(R.id.txncharge);
                investmentamount = (TextView) itemLayoutView.findViewById(R.id.investmentamount);
                changeamount = (TextView) itemLayoutView.findViewById(R.id.historyProfitOrLoss);
                currentvalue = (TextView) itemLayoutView.findViewById(R.id.historyCurrentValue);

                expand = (ImageView) itemLayoutView.findViewById(R.id.expand);

                expandableLayout = (ExpandableLayout) itemLayoutView.findViewById(R.id.expandableLayout);

                nextact = (RelativeLayout) itemLayoutView.findViewById(R.id.nextact);

                stockLayout = (LinearLayout) itemLayoutView.findViewById(R.id.stockLayout);

                stockLayout.setVisibility(View.VISIBLE);

                transactionsHistoryRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickListener.onItemClick(stocks, getAdapterPosition());
                    }
                });

                nextact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickListener.onItemClick(stocks, getAdapterPosition());
                    }
                });

                sellRecyclerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickListener.onItemClick(stocks, getAdapterPosition());
                    }
                });

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