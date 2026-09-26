package com.example.warevista;

public class SalesHistoryModel {

    private String salesId;
    private String buyer;
    private String crop;
    private String quantity;
    private String rate;
    private String total;
    private String payment;
    private String date;


    public SalesHistoryModel(
            String salesId,
            String buyer,
            String crop,
            String quantity,
            String rate,
            String total,
            String payment,
            String date
    ) {

        this.salesId = salesId;
        this.buyer = buyer;
        this.crop = crop;
        this.quantity = quantity;
        this.rate = rate;
        this.total = total;
        this.payment = payment;
        this.date = date;

    }


    public String getSalesId() {
        return salesId;
    }

    public String getBuyer() {
        return buyer;
    }

    public String getCrop() {
        return crop;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getRate() {
        return rate;
    }

    public String getTotal() {
        return total;
    }

    public String getPayment() {
        return payment;
    }

    public String getDate() {
        return date;
    }
}