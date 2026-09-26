package com.example.warevista;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<HistoryModel> historyList;

    public HistoryAdapter(ArrayList<HistoryModel> historyList) {
        this.historyList = historyList;
    }

    // Search/Filter માટે list update કરવા
    public void updateList(ArrayList<HistoryModel> newList) {
        historyList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        HistoryModel model = historyList.get(position);

        holder.txtName.setText("Supplier : " + model.getName());

        holder.txtCrop.setText("Crop:"+ model.getCrop());

        holder.txtQuantity.setText("⚖ Quantity : " + model.getQuantity() + " KG");

        holder.txtRate.setText("💰 Rate : ₹" + model.getRate() + " / KG");

        holder.txtTotal.setText("💵 Total : ₹" + model.getTotal());

        holder.txtDate.setText("📅 " + model.getDate());

        holder.txtPayment.setText(model.getPayment().toUpperCase());

        String payment = model.getPayment().toLowerCase();

        if(payment.contains("cash")){
            holder.txtPayment.setBackgroundResource(R.drawable.badge_green);
        }
        else if(payment.contains("upi")){
            holder.txtPayment.setBackgroundResource(R.drawable.badge_blue);
        }
        else if(payment.contains("bank")){
            holder.txtPayment.setBackgroundResource(R.drawable.badge_orange);
        }
        else{
            holder.txtPayment.setBackgroundResource(R.drawable.badge_gray);
        }
    }
    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtCrop, txtQuantity, txtRate,
                txtTotal, txtPayment, txtDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtCrop = itemView.findViewById(R.id.txtCrop);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtRate = itemView.findViewById(R.id.txtRate);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            txtPayment = itemView.findViewById(R.id.txtPayment);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }
}