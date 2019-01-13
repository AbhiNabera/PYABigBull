package nabera.ranjan.abhinabera.pyabigbull.UserActivities.TransactionsHistory;

public class TransactionsHistoryData {


    private String companyName, quantity, investment, buyOrSell;

    public TransactionsHistoryData(String companyName, String buyOrSell, String quantity, String investment){

        this.companyName = companyName;
        this.quantity = quantity;
        this.investment = investment;
        this.buyOrSell = buyOrSell;
    }
    // getters & setters


    public String getCompanyName() {
        return companyName;
    }
    public String getQuantity(){
        return quantity;
    }
    public String getInvestment(){ return investment; }
    public String getBuyOrSell(){ return buyOrSell;}
}