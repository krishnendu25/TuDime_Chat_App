package com.TuDime.Internet_Calling;

/**
 * Created by mahak on 5/5/2017.
 */

public class PAyment_model {
    String amount;

    public String getTotal_credit() {
        return total_credit;
    }

    public void setTotal_credit(String total_credit) {
        this.total_credit = total_credit;
    }

    String total_credit;

    public String getReceipt() {
        return Receipt;
    }

    public void setReceipt(String receipt) {
        Receipt = receipt;
    }

    String Receipt;

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    String credit;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    String date;
    String time;
}
