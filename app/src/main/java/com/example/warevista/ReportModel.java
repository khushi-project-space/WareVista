package com.example.warevista;

public class ReportModel {

    private String crop;

    private int purchaseQty;
    private int salesQty;

    private int purchaseAmount;
    private int salesAmount;

    private int profit;

    public ReportModel(String crop,
                       int purchaseQty,
                       int salesQty,
                       int purchaseAmount,
                       int salesAmount,
                       int profit) {

        this.crop = crop;
        this.purchaseQty = purchaseQty;
        this.salesQty = salesQty;
        this.purchaseAmount = purchaseAmount;
        this.salesAmount = salesAmount;
        this.profit = profit;
    }

    public String getCrop() {
        return crop;
    }

    public void setCrop(String crop) {
        this.crop = crop;
    }

    public int getPurchaseQty() {
        return purchaseQty;
    }

    public void setPurchaseQty(int purchaseQty) {
        this.purchaseQty = purchaseQty;
    }

    public int getSalesQty() {
        return salesQty;
    }

    public void setSalesQty(int salesQty) {
        this.salesQty = salesQty;
    }

    public int getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(int purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public int getSalesAmount() {
        return salesAmount;
    }

    public void setSalesAmount(int salesAmount) {
        this.salesAmount = salesAmount;
    }

    public int getProfit() {
        return profit;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }
}