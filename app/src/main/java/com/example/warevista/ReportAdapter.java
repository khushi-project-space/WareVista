package com.example.warevista;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private final ArrayList<ReportModel> reportList;

    public ReportAdapter(ArrayList<ReportModel> reportList) {
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report, parent, false);

        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {

        ReportModel model = reportList.get(position);

        holder.txtCrop.setText("Crop : " + model.getCrop());

        holder.txtPurchaseQty.setText(
                "Purchase : " + model.getPurchaseQty() + " KG");

        holder.txtSalesQty.setText(
                "Sales : " + model.getSalesQty() + " KG");

        holder.txtPurchaseAmount.setText(
                "Purchase ₹ : " + model.getPurchaseAmount());

        holder.txtSalesAmount.setText(
                "Sales ₹ : " + model.getSalesAmount());

        if (model.getProfit() >= 0) {

            holder.txtProfit.setText(
                    "Profit : +₹" + model.getProfit() + " 🟢");

            holder.txtProfit.setTextColor(
                    Color.parseColor("#2E7D32"));

        } else {

            holder.txtProfit.setText(
                    "Loss : -₹" + Math.abs(model.getProfit()) + " 🔴");

            holder.txtProfit.setTextColor(
                    Color.parseColor("#D32F2F"));

        }
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {

        TextView txtCrop;
        TextView txtPurchaseQty;
        TextView txtSalesQty;
        TextView txtPurchaseAmount;
        TextView txtSalesAmount;
        TextView txtProfit;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCrop = itemView.findViewById(R.id.txtCrop);
            txtPurchaseQty = itemView.findViewById(R.id.txtPurchaseQty);
            txtSalesQty = itemView.findViewById(R.id.txtSalesQty);
            txtPurchaseAmount = itemView.findViewById(R.id.txtPurchaseAmount);
            txtSalesAmount = itemView.findViewById(R.id.txtSalesAmount);
            txtProfit = itemView.findViewById(R.id.txtProfit);
        }
    }
}