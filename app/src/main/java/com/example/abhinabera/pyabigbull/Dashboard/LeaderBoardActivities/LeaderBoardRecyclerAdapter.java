package com.example.abhinabera.pyabigbull.Dashboard.LeaderBoardActivities;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.Api.Utility;
import com.example.abhinabera.pyabigbull.R;
import com.google.gson.JsonObject;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderBoardRecyclerAdapter extends RecyclerView.Adapter<LeaderBoardRecyclerAdapter.ViewHolder> {

    ArrayList<JsonObject> boardlist;

    private Activity activity;

    public LeaderBoardRecyclerAdapter(Activity activity, ArrayList<JsonObject> boardlist) {
        this.boardlist = boardlist;
        this.activity = activity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LeaderBoardRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leader_board_recycler_row, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        try {
            if (boardlist.get(position).get("imageUrl") != null) {

                if (!boardlist.get(position).get("imageUrl").toString().equalsIgnoreCase("null")) {
                    Picasso.with(activity).
                            load(boardlist.get(position).get("imageUrl").getAsString().trim())
                            //.skipMemoryCache()
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .fit()
                            .into(viewHolder.profilePhoto);
                }
            }

            viewHolder.userName.setText(boardlist.get(position).get("userName").getAsString() + "");
            viewHolder.userName.setSelected(true);
            viewHolder.userRank.setText((position + 1) + "");
            viewHolder.userBoxPrice.setText(new Utility().getRoundoffData(boardlist.get(position)
                    .get("current_change").getAsString() + ""));
            viewHolder.userBoxPercent.setText(new Utility().getRoundoffData(boardlist.get(position)
                    .get("current_pchange").getAsString() + "") + "%");

            if (boardlist.get(position).get("current_pchange").getAsDouble() >= 0) {
                viewHolder.leaderBoardItemBox.setBackgroundColor(activity.getResources().getColor(R.color.greenText));
            } else {
                viewHolder.leaderBoardItemBox.setBackgroundColor(activity.getResources().getColor(R.color.red));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    // inner class to hold a reference to each item of RecyclerView 
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView userName, userRank, userBoxPrice, userBoxPercent;
        public CircleImageView profilePhoto;
        LinearLayout leaderBoardItemBox;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            userName = (TextView) itemLayoutView.findViewById(R.id.leaderBoardUserName);
            userRank = (TextView) itemLayoutView.findViewById(R.id.leaderBoardRank);
            userBoxPrice = (TextView) itemLayoutView.findViewById(R.id.leaderBoardItemBoxPrice);
            userBoxPercent = (TextView) itemLayoutView.findViewById(R.id.leaderBoardItemBoxPercent);
            profilePhoto = (CircleImageView) itemLayoutView.findViewById(R.id.profilePhoto);
            leaderBoardItemBox = (LinearLayout) itemLayoutView.findViewById(R.id.leaderBoardItemBox);
        }
    }

    @Override
    public int getItemCount() {
        return boardlist.size();
    }
}