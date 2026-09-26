package com.example.warevista;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {

    private final ArrayList<StockModel> stockList;

    // Loading state
    private boolean isLoading = false;

    public StockAdapter(ArrayList<StockModel> stockList) {
        this.stockList = stockList;
    }

    // =========================================================
    // SET LOADING
    // =========================================================

    public void setLoading(boolean loading) {

        isLoading = loading;

        notifyDataSetChanged();
    }

    // =========================================================
    // VIEW TYPE
    // =========================================================

    @Override
    public int getItemViewType(int position) {

        if (isLoading) {
            return 1;
        }

        return 0;
    }

    // =========================================================
    // CREATE VIEW HOLDER
    // =========================================================

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view;

        if (viewType == 1) {

            // Skeleton card
            view = LayoutInflater.from(parent.getContext())
                    .inflate(
                            R.layout.item_stock_skeleton,
                            parent,
                            false
                    );

        } else {

            // Actual stock card
            view = LayoutInflater.from(parent.getContext())
                    .inflate(
                            R.layout.item_stock,
                            parent,
                            false
                    );
        }

        return new StockViewHolder(view, viewType);
    }

    // =========================================================
    // BIND VIEW HOLDER
    // =========================================================

    @Override
    public void onBindViewHolder(
            @NonNull StockViewHolder holder,
            int position) {

        // Skeleton has nothing to bind
        if (isLoading) {
            return;
        }

        StockModel model = stockList.get(position);

        holder.txtCrop.setText(
                "🌾 " + model.getCrop()
        );

        holder.txtPurchased.setText(
                "📥 Purchased : "
                        + model.getPurchased()
                        + " KG"
        );

        holder.txtSold.setText(
                "📤 Sold : "
                        + model.getSold()
                        + " KG"
        );

        if (model.getAvailable() > 0) {

            holder.txtAvailable.setText(
                    "📦 Available Stock : "
                            + model.getAvailable()
                            + " KG"
            );

        } else {

            holder.txtAvailable.setText(
                    "❌ Stock Not Available"
            );
        }

        // =====================================================
        // STOCK PERCENTAGE
        // =====================================================

        int percent = 0;

        if (model.getPurchased() > 0) {

            percent =
                    (model.getAvailable() * 100)
                            / model.getPurchased();
        }

        if (percent < 0) {
            percent = 0;
        }

        if (percent > 100) {
            percent = 100;
        }

        holder.progressStock.setProgress(percent);

        holder.txtPercent.setText(
                percent + "% Available"
        );

        // =====================================================
        // STOCK STATUS
        // =====================================================

        if (model.getAvailable() == 0) {

            holder.txtStatus.setText(
                    "🔴 Out Of Stock"
            );

            holder.txtStatus.setBackgroundColor(
                    Color.parseColor("#D32F2F")
            );

        } else if (percent < 20) {

            holder.txtStatus.setText(
                    "🟠 Restock Soon"
            );

            holder.txtStatus.setBackgroundColor(
                    Color.parseColor("#F57C00")
            );

        } else if (percent < 50) {

            holder.txtStatus.setText(
                    "🟡 Medium"
            );

            holder.txtStatus.setBackgroundColor(
                    Color.parseColor("#FBC02D")
            );

        } else {

            holder.txtStatus.setText(
                    "🟢 Healthy"
            );

            holder.txtStatus.setBackgroundColor(
                    Color.parseColor("#388E3C")
            );
        }
    }

    // =========================================================
    // ITEM COUNT
    // =========================================================

    @Override
    public int getItemCount() {

        // Show 3 skeleton cards
        if (isLoading) {
            return 3;
        }

        return stockList.size();
    }

    // =========================================================
    // VIEW HOLDER
    // =========================================================

    public static class StockViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtCrop;
        TextView txtPurchased;
        TextView txtSold;
        TextView txtAvailable;

        TextView txtPercent;
        TextView txtStatus;

        ProgressBar progressStock;

        public StockViewHolder(
                @NonNull View itemView,
                int viewType) {

            super(itemView);

            // Only actual stock card has these views
            if (viewType == 0) {

                txtCrop =
                        itemView.findViewById(
                                R.id.txtCrop
                        );

                txtPurchased =
                        itemView.findViewById(
                                R.id.txtPurchased
                        );

                txtSold =
                        itemView.findViewById(
                                R.id.txtSold
                        );

                txtAvailable =
                        itemView.findViewById(
                                R.id.txtAvailable
                        );

                progressStock =
                        itemView.findViewById(
                                R.id.progressStock
                        );

                txtPercent =
                        itemView.findViewById(
                                R.id.txtPercent
                        );

                txtStatus =
                        itemView.findViewById(
                                R.id.txtStatus
                        );
            }
        }
    }
}