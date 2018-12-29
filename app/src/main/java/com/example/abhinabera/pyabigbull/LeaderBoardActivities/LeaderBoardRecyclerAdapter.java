package com.example.abhinabera.pyabigbull.LeaderBoardActivities;

import android.graphics.Movie;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.R;

import java.util.List;

public class LeaderBoardRecyclerAdapter extends RecyclerView.Adapter<LeaderBoardRecyclerAdapter.ViewHolder> {
    private LeaderBoardData[] itemsData;

    public LeaderBoardRecyclerAdapter(LeaderBoardData[] itemsData) {
        this.itemsData = itemsData;
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

        viewHolder.userName.setText(itemsData[position].getUserName());
        viewHolder.userRank.setText(itemsData[position].getUserRank());
        viewHolder.imageId.setImageResource(itemsData[position].getImageId());
    }

    // inner class to hold a reference to each item of RecyclerView 
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView userName, userRank, userBoxPrice, userBoxPercent;
        public ImageView imageId;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            userName = (TextView) itemLayoutView.findViewById(R.id.leaderBoardUserName);
            userRank = (TextView) itemLayoutView.findViewById(R.id.leaderBoardRank);
            userBoxPrice = (TextView) itemLayoutView.findViewById(R.id.leaderBoardItemBoxPrice);
            userBoxPercent = (TextView) itemLayoutView.findViewById(R.id.leaderBoardItemBoxPrice);
            imageId = (ImageView) itemLayoutView.findViewById(R.id.medal);
        }
    }

    @Override
    public int getItemCount() {
        return itemsData.length;
    }
}