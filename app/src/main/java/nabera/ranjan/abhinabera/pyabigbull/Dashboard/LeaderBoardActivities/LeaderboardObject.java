package nabera.ranjan.abhinabera.pyabigbull.Dashboard.LeaderBoardActivities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by AVINASH on 1/3/2019.
 */

public class LeaderboardObject {

    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("percentchange")
    @Expose
    private Double percentchange;
    @SerializedName("change")
    @Expose
    private String change;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Double getPercentchange() {
        return percentchange;
    }

    public void setPercentchange(Double percentchange) {
        this.percentchange = percentchange;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }
}
