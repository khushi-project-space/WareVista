package com.example.warevista;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.StaffViewHolder> {

    private final ArrayList<ManageStaffModel> staffList = new ArrayList<>();
    private final OnStaffActionListener listener;

    public interface OnStaffActionListener {
        void onEdit(ManageStaffModel staff);
        void onDelete(ManageStaffModel staff);
    }

    public StaffAdapter(
            List<ManageStaffModel> initialList,
            OnStaffActionListener listener) {

        this.listener = listener;

        if (initialList != null) {
            staffList.addAll(initialList);
        }
    }

    public void updateList(List<ManageStaffModel> newList) {

        staffList.clear();

        if (newList != null) {
            staffList.addAll(newList);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StaffViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.staff_item,
                        parent,
                        false
                );

        return new StaffViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull StaffViewHolder holder,
            int position) {

        ManageStaffModel staff = staffList.get(position);

        holder.txtStaffName.setText(
                safe(staff.getStaffName())
        );

        holder.txtStaffId.setText(
                "ID: " + safe(staff.getUserId())
        );

        holder.txtWarehouse.setText(
                "Warehouse: " + safe(staff.getWarehouse())
        );

        holder.txtMobile.setText(
                "Mobile: " + safe(staff.getMobile())
        );

        holder.txtCreateDate.setText(
                "Created: " + safe(staff.getCreateDate())
        );

        String role = safe(staff.getRole());

        if (role.isEmpty()) {
            role = "STAFF";
        }

        holder.txtRole.setText(
                role.toUpperCase()
        );

        holder.btnEditStaff.setOnClickListener(v -> {

            if (listener != null) {
                listener.onEdit(staff);
            }
        });

        holder.btnDeleteStaff.setOnClickListener(v -> {

            if (listener != null) {
                listener.onDelete(staff);
            }
        });
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    static class StaffViewHolder extends RecyclerView.ViewHolder {

        TextView txtStaffName;
        TextView txtStaffId;
        TextView txtWarehouse;
        TextView txtMobile;
        TextView txtCreateDate;
        TextView txtRole;

        ImageButton btnEditStaff;
        ImageButton btnDeleteStaff;

        StaffViewHolder(@NonNull View itemView) {

            super(itemView);

            txtStaffName = itemView.findViewById(
                    R.id.txtStaffName
            );

            txtStaffId = itemView.findViewById(
                    R.id.txtStaffId
            );

            txtWarehouse = itemView.findViewById(
                    R.id.txtWarehouse
            );

            txtMobile = itemView.findViewById(
                    R.id.txtMobile
            );

            txtCreateDate = itemView.findViewById(
                    R.id.txtCreateDate
            );

            txtRole = itemView.findViewById(
                    R.id.txtRole
            );

            btnEditStaff = itemView.findViewById(
                    R.id.btnEditStaff
            );

            btnDeleteStaff = itemView.findViewById(
                    R.id.btnDeleteStaff
            );
        }
    }
}