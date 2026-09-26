package com.example.warevista;

public class HistoryModel {

    private String id;
    private String name;
    private String crop;
    private String quantity;
    private String rate;
    private String total;
    private String payment;
    private String date;

    public HistoryModel(String id,
                        String name,
                        String crop,
                        String quantity,
                        String rate,
                        String total,
                        String payment,
                        String date) {

        this.id = id;
        this.name = name;
        this.crop = crop;
        this.quantity = quantity;
        this.rate = rate;
        this.total = total;
        this.payment = payment;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
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