package com.example.abhinabera.pyabigbull.LeaderBoardActivities;

public class LeaderBoardData {


    private String userName, userRank;
    private int imageId;

    public LeaderBoardData(String userName, String userRank, int imageId){

        this.userName = userName;
        this.userRank = userRank;
        this.imageId = imageId;
    }
    // getters & setters


    public String getUserName() {
        return userName;
    }
    public String getUserRank(){
        return userRank;
    }
    public int getImageId(){
        return imageId;
    }
}