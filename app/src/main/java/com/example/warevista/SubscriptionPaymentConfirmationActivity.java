package com.example.warevista;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;


public class SubscriptionPaymentConfirmationActivity
        extends AppCompatActivity {


    // ============================================================
    // UI
    // ============================================================

    private TextView txtPlan;
    private TextView txtDuration;
    private TextView txtWarehouses;
    private TextView txtAmount;
    private TextView txtStatus;

    private MaterialButton btnContinue;


    // ============================================================
    // SESSION
    // ============================================================

    private SharedPreferences preferences;


    // ============================================================
    // SUBSCRIPTION DATA
    // ============================================================

    private String plan;
    private String duration;

    private int warehouseCount;
    private int amount;


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_subscription_payment_confirmation
        );


        // ========================================================
        // SESSION
        // ========================================================

        preferences = getSharedPreferences(
                "WareVista",
                MODE_PRIVATE
        );


        // ========================================================
        // FIND VIEWS
        // ========================================================

        txtPlan =
                findViewById(
                        R.id.txtPlan
                );


        txtDuration =
                findViewById(
                        R.id.txtDuration
                );


        txtWarehouses =
                findViewById(
                        R.id.txtWarehouses
                );


        txtAmount =
                findViewById(
                        R.id.txtAmount
                );


        txtStatus =
                findViewById(
                        R.id.txtStatus
                );


        btnContinue =
                findViewById(
                        R.id.btnContinue
                );


        // ========================================================
        // GET INTENT DATA
        // ========================================================

        getSubscriptionData();


        // ========================================================
        // SHOW DATA
        // ========================================================

        showSubscriptionData();


        // ========================================================
        // UPDATE SUBSCRIPTION
        // ========================================================

        updateSubscription();


        // ========================================================
        // CONTINUE
        // ========================================================

        btnContinue.setOnClickListener(
                v -> openAdminDashboard()
        );

    }


    // ============================================================
    // GET SUBSCRIPTION DATA
    // ============================================================

    private void getSubscriptionData() {


        Intent intent =
                getIntent();


        plan = intent.getStringExtra(
                "plan"
        );


        duration = intent.getStringExtra(
                "duration"
        );


        warehouseCount =

                intent.getIntExtra(
                        "warehouseCount",
                        1
                );


        amount =

                intent.getIntExtra(
                        "amount",
                        0
                );


        // ========================================================
        // DEFAULT PLAN
        // ========================================================

        if (plan == null ||
                plan.trim().isEmpty()) {

            plan = "FREE";

        }


        // ========================================================
        // DEFAULT DURATION
        // ========================================================

        if (duration == null ||
                duration.trim().isEmpty()) {

            duration = "FREE";

        }


        plan =
                plan.trim()
                        .toUpperCase();


        duration =
                duration.trim()
                        .toUpperCase();


        // ========================================================
        // WAREHOUSE VALIDATION
        // ========================================================

        if (warehouseCount < 1) {

            warehouseCount = 1;

        }

    }


    // ============================================================
    // SHOW SUBSCRIPTION DATA
    // ============================================================

    private void showSubscriptionData() {


        txtPlan.setText(
                plan
        );


        txtDuration.setText(
                duration
        );


        txtWarehouses.setText(

                warehouseCount
                        + " Warehouse(s)"

        );


        txtAmount.setText(

                "₹" + amount

        );


        txtStatus.setText(

                "Payment Successful 🎉"

        );

    }


    // ============================================================
    // UPDATE SUBSCRIPTION
    // ============================================================

    private void updateSubscription() {


        // ========================================================
        // DISABLE BUTTON
        //
        // Subscription update complete થાય ત્યાં સુધી
        // Admin Dashboard open નહીં થાય
        // ========================================================

        btnContinue.setEnabled(
                false
        );


        txtStatus.setText(
                "Updating your subscription..."
        );


        // ========================================================
        // GET USER ID
        // ========================================================

        String userId =

                preferences.getString(
                        "userId",
                        ""
                );


        // ========================================================
        // USER VALIDATION
        // ========================================================

        if (userId == null ||
                userId.trim().isEmpty()) {


            txtStatus.setText(
                    "User session not found"
            );


            Toast.makeText(

                    this,

                    "Please login again",

                    Toast.LENGTH_LONG

            ).show();


            return;

        }


        // ========================================================
        // TEMPORARY LOCAL UPDATE
        //
        // NEXT STEP માં Google Apps Script
        // updateSubscription API add કરીશું
        // ========================================================

        saveSubscriptionLocally();


        // ========================================================
        // SUCCESS
        // ========================================================

        txtStatus.setText(

                "Subscription Activated Successfully 🎉"

        );


        btnContinue.setEnabled(
                true
        );

    }


    // ============================================================
    // SAVE SUBSCRIPTION LOCALLY
    // ============================================================

    private void saveSubscriptionLocally() {


        // ========================================================
        // FEATURES
        // ========================================================

        boolean purchaseAccess = true;

        boolean salesAccess = true;

        boolean historyAccess = false;

        boolean liveStockAccess = false;


        // ========================================================
        // GOLD
        // ========================================================

        if (plan.equalsIgnoreCase("GOLD")) {


            historyAccess = true;

            liveStockAccess = false;

        }


        // ========================================================
        // PLATINUM
        // ========================================================

        else if (plan.equalsIgnoreCase("PLATINUM")) {


            historyAccess = true;

            liveStockAccess = true;

        }


        // ========================================================
        // SAVE USING SUBSCRIPTION MANAGER
        // ========================================================

        SubscriptionManager.saveSubscription(

                this,

                plan,

                warehouseCount,

                purchaseAccess,

                salesAccess,

                historyAccess,

                liveStockAccess

        );

    }


    // ============================================================
    // OPEN ADMIN DASHBOARD
    // ============================================================

    private void openAdminDashboard() {


        Intent intent =

                new Intent(

                        SubscriptionPaymentConfirmationActivity.this,

                        AdminDashboardActivity.class

                );


        intent.setFlags(

                Intent.FLAG_ACTIVITY_NEW_TASK

                        |

                        Intent.FLAG_ACTIVITY_CLEAR_TASK

        );


        startActivity(
                intent
        );


        finish();

    }


    // ============================================================
    // BACK
    //
    // PAYMENT SUCCESS પછી પાછા જવા નહીં દેવું
    // ============================================================

    @Override
    public void onBackPressed() {


        openAdminDashboard();

    }

}