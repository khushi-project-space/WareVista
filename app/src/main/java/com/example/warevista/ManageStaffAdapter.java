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

public class ManageStaffAdapter
        extends RecyclerView.Adapter<ManageStaffAdapter.StaffViewHolder> {

    private final ArrayList<ManageStaffModel> staffList = new ArrayList<>();

    private final OnStaffActionListener listener;

    public interface OnStaffActionListener {

        void onEdit(ManageStaffModel staff);

        void onDelete(ManageStaffModel staff);
    }

    public ManageStaffAdapter(
            List<ManageStaffModel> initialList,
            OnStaffActionListener listener) {

        this.listener = listener;

        if (initialList != null) {
            staffList.addAll(initialList);
        }
    }

    // ============================================================
    // UPDATE LIST
    // ============================================================

    public void updateList(List<ManageStaffModel> newList) {

        staffList.clear();

        if (newList != null) {
            staffList.addAll(newList);
        }

        notifyDataSetChanged();
    }

    // ============================================================
    // CREATE VIEW HOLDER
    // ============================================================

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

    // ============================================================
    // BIND VIEW HOLDER
    // ============================================================

    @Override
    public void onBindViewHolder(
            @NonNull StaffViewHolder holder,
            int position) {

        ManageStaffModel staff =
                staffList.get(position);

        // --------------------------------------------------------
        // STAFF NAME
        // --------------------------------------------------------

        holder.txtStaffName.setText(
                safe(staff.getStaffName())
        );

        // --------------------------------------------------------
        // STAFF ID
        // --------------------------------------------------------

        holder.txtStaffId.setText(
                "ID: " + safe(staff.getUserId())
        );

        // --------------------------------------------------------
        // ROLE
        // --------------------------------------------------------

        holder.txtRole.setText(
                safe(staff.getRole())
        );

        // --------------------------------------------------------
        // WAREHOUSE
        // --------------------------------------------------------

        holder.txtWarehouse.setText(
                safe(staff.getWarehouse())
        );

        // --------------------------------------------------------
        // MOBILE
        // --------------------------------------------------------

        holder.txtMobile.setText(
                safe(staff.getMobile())
        );

        // --------------------------------------------------------
        // CREATED DATE
        // --------------------------------------------------------

        holder.txtCreateDate.setText(
                safe(staff.getCreateDate())
        );

        // ========================================================
        // EDIT BUTTON
        // ========================================================

        holder.btnEditStaff.setOnClickListener(
                v -> {

                    int adapterPosition =
                            holder.getBindingAdapterPosition();

                    if (adapterPosition == RecyclerView.NO_POSITION) {
                        return;
                    }

                    ManageStaffModel selectedStaff =
                            staffList.get(adapterPosition);

                    if (listener != null) {

                        listener.onEdit(
                                selectedStaff
                        );
                    }
                }
        );

        // ========================================================
        // DELETE BUTTON
        // ========================================================

        holder.btnDeleteStaff.setOnClickListener(
                v -> {

                    int adapterPosition =
                            holder.getBindingAdapterPosition();

                    if (adapterPosition == RecyclerView.NO_POSITION) {
                        return;
                    }

                    ManageStaffModel selectedStaff =
                            staffList.get(adapterPosition);

                    if (listener != null) {

                        listener.onDelete(
                                selectedStaff
                        );
                    }
                }
        );
    }

    // ============================================================
    // ITEM COUNT
    // ============================================================

    @Override
    public int getItemCount() {

        return staffList.size();
    }

    // ============================================================
    // SAFE STRING
    // ============================================================

    private String safe(String value) {

        return value == null
                ? ""
                : value.trim();
    }

    // ============================================================
    // VIEW HOLDER
    // ============================================================

    static class StaffViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtStaffName;
        TextView txtStaffId;
        TextView txtWarehouse;
        TextView txtMobile;
        TextView txtCreateDate;
        TextView txtRole;

        ImageButton btnEditStaff;
        ImageButton btnDeleteStaff;

        StaffViewHolder(
                @NonNull View itemView) {

            super(itemView);

            // ----------------------------------------------------
            // TEXT VIEWS
            // ----------------------------------------------------

            txtStaffName =
                    itemView.findViewById(
                            R.id.txtStaffName
                    );

            txtStaffId =
                    itemView.findViewById(
                            R.id.txtStaffId
                    );

            txtWarehouse =
                    itemView.findViewById(
                            R.id.txtWarehouse
                    );

            txtMobile =
                    itemView.findViewById(
                            R.id.txtMobile
                    );

            txtCreateDate =
                    itemView.findViewById(
                            R.id.txtCreateDate
                    );

            txtRole =
                    itemView.findViewById(
                            R.id.txtRole
                    );

            // ----------------------------------------------------
            // ACTION BUTTONS
            // ----------------------------------------------------

            btnEditStaff =
                    itemView.findViewById(
                            R.id.btnEditStaff
                    );

            btnDeleteStaff =
                    itemView.findViewById(
                            R.id.btnDeleteStaff
                    );
        }
    }
}