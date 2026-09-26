package com.example.warevista;

public class StockModel {

    private String crop;
    private int purchased;
    private int sold;
    private int available;

    public StockModel(String crop, int purchased, int sold, int available) {
        this.crop = crop;
        this.purchased = purchased;
        this.sold = sold;
        this.available = available;
    }

    public String getCrop() {
        return crop;
    }

    public int getPurchased() {
        return purchased;
    }

    public int getSold() {
        return sold;
    }

    public int getAvailable() {
        return available;
    }
}