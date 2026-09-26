package com.example.warevista;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SubscriptionPaymentActivity
        extends AppCompatActivity
        implements PaymentResultListener {


    // ============================================================
    // API
    // ============================================================

    private static final String BASE_URL =
            "https://script.google.com/macros/s/AKfycbxH_jxb8MUvSXeZQZAE9bbEGTojDKDoGD2-GMc3cWnn4EcI_fz42UCK4mTw2662HV14/exec";


    // ============================================================
    // UI
    // ============================================================

    private ImageButton btnBack;

    private TextView txtPlan;
    private TextView txtDuration;
    private TextView txtWarehouses;
    private TextView txtAmount;

    private MaterialButton btnPay;


    // ============================================================
    // SESSION
    // ============================================================

    private SharedPreferences preferences;

    private String userId = "";


    // ============================================================
    // SUBSCRIPTION DATA
    // ============================================================

    private String plan = "";

    private String duration = "";

    /*
     * FIRST-TIME PURCHASE:
     *
     * warehouseCount = TOTAL selected warehouses
     *
     *
     * SECOND-TIME GOLD / PLATINUM:
     *
     * warehouseCount = NEW warehouses only
     */

    private int warehouseCount = 1;

    private int amount = 0;


    // ============================================================
    // ADDITIONAL PURCHASE
    // ============================================================

    private boolean additionalPurchase = false;

    private int additionalWarehouses = 0;

    private int newAppliedLimit = 0;


    // ============================================================
    // PAYMENT STATUS
    // ============================================================

    private boolean isProcessing = false;


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_subscription_payment
        );


        // ========================================================
        // SHARED PREFERENCES
        // ========================================================

        preferences =
                getSharedPreferences(
                        "WareVista",
                        MODE_PRIVATE
                );


        // ========================================================
        // USER ID
        // ========================================================

        userId =
                preferences.getString(
                        "userId",
                        ""
                );


        if (userId == null) {

            userId = "";

        }


        userId = userId.trim();


        // ========================================================
        // FIND VIEWS
        // ========================================================

        btnBack =
                findViewById(
                        R.id.btnBack
                );

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

        btnPay =
                findViewById(
                        R.id.btnPay
                );


        // ========================================================
        // GET INTENT DATA
        // ========================================================

        getSubscriptionData();


        // ========================================================
        // VALIDATE
        // ========================================================

        if (!validateSubscriptionData()) {

            return;

        }


        // ========================================================
        // DISPLAY
        // ========================================================

        displaySubscriptionData();


        // ========================================================
        // BACK
        // ========================================================

        btnBack.setOnClickListener(
                v -> {

                    if (!isProcessing) {


                        finish();

                    }

                }
        );


        // ========================================================
        // PAY
        // ========================================================

        btnPay.setOnClickListener(
                v -> processPayment()
        );

    }


    // ============================================================
    // GET SUBSCRIPTION DATA
    // ============================================================

    private void getSubscriptionData() {

        Intent intent =
                getIntent();


        plan =
                intent.getStringExtra(
                        "plan"
                );


        duration =
                intent.getStringExtra(
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
        // ADDITIONAL PURCHASE DATA
        // ========================================================

        additionalPurchase =
                intent.getBooleanExtra(
                        "additionalPurchase",
                        false
                );


        additionalWarehouses =
                intent.getIntExtra(
                        "additionalWarehouses",
                        0
                );


        newAppliedLimit =
                intent.getIntExtra(
                        "newAppliedLimit",
                        warehouseCount
                );


        // ========================================================
        // NULL SAFETY
        // ========================================================

        if (plan == null) {

            plan = "";

        }


        if (duration == null) {

            duration = "";

        }


        // ========================================================
        // FORMAT
        // ========================================================

        plan =
                plan.trim()
                        .toUpperCase();


        duration =
                duration.trim()
                        .toUpperCase();

    }


    // ============================================================
    // VALIDATE SUBSCRIPTION DATA
    // ============================================================

    private boolean validateSubscriptionData() {


        // ========================================================
        // USER ID
        // ========================================================

        if (userId.isEmpty()) {

            Toast.makeText(
                    this,
                    "User session not found. Please login again.",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return false;

        }


        // ========================================================
        // PLAN
        // ========================================================

        if (
                !plan.equals("FREE")
                        &&
                        !plan.equals("GOLD")
                        &&
                        !plan.equals("PLATINUM")
        ) {

            Toast.makeText(
                    this,
                    "Invalid subscription plan",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return false;

        }


        // ========================================================
        // FREE
        // ========================================================

        if (plan.equals("FREE")) {

            warehouseCount = 1;

            amount = 0;

            duration = "FREE";

            additionalPurchase = false;

            additionalWarehouses = 0;

            newAppliedLimit = 1;

        }


        // ========================================================
        // GOLD / PLATINUM
        // ========================================================

        else {

            if (warehouseCount < 1) {

                warehouseCount = 1;

            }


            if (amount <= 0) {

                Toast.makeText(
                        this,
                        "Invalid payment amount",
                        Toast.LENGTH_LONG
                ).show();

                finish();

                return false;

            }


            if (
                    !duration.equals("MONTHLY")
                            &&
                            !duration.equals("YEARLY")
            ) {

                Toast.makeText(
                        this,
                        "Invalid subscription duration",
                        Toast.LENGTH_LONG
                ).show();

                finish();

                return false;

            }


            // ====================================================
            // ADDITIONAL PURCHASE
            // GOLD + PLATINUM
            // ====================================================

            if (additionalPurchase) {


                // ================================================
                // ONLY GOLD / PLATINUM ALLOWED
                // ================================================

                if (
                        !plan.equals("GOLD")
                                &&
                                !plan.equals("PLATINUM")
                ) {

                    Toast.makeText(
                            this,
                            "Invalid additional subscription purchase",
                            Toast.LENGTH_LONG
                    ).show();

                    finish();

                    return false;

                }


                // ================================================
                // NEW WAREHOUSES
                // ================================================

                if (additionalWarehouses < 1) {

                    Toast.makeText(
                            this,
                            "Invalid additional warehouse count",
                            Toast.LENGTH_LONG
                    ).show();

                    finish();

                    return false;

                }


                /*
                 * warehouseCount must represent the same
                 * NEW warehouse count.
                 */

                if (
                        warehouseCount
                                !=
                                additionalWarehouses
                ) {

                    warehouseCount =
                            additionalWarehouses;

                }


                // ================================================
                // NEW APPLIED LIMIT
                // ================================================

//                if (newAppliedLimit < 1) {
//
//                    Toast.makeText(
//                            this,
//                            "Invalid new warehouse limit",
//                            Toast.LENGTH_LONG
//                    ).show();
//
//                    finish();
//
//                    return false;
//
//                }

                if (newAppliedLimit < 1) {

                    newAppliedLimit = warehouseCount;

                }
                // ================================================
                // GOLD MAXIMUM
                // ================================================

                if (
                        plan.equals("GOLD")
                                &&
                                newAppliedLimit > 7
                ) {

                    Toast.makeText(
                            this,
                            "Gold plan maximum limit is 7 warehouses",
                            Toast.LENGTH_LONG
                    ).show();

                    finish();

                    return false;

                }

            }

        }


        return true;

    }


    // ============================================================
    // DISPLAY SUBSCRIPTION DATA
    // ============================================================

    private void displaySubscriptionData() {


        // ========================================================
        // PLAN
        // ========================================================

        txtPlan.setText(
                plan
        );


        // ========================================================
        // DURATION
        // ========================================================

        if (plan.equals("FREE")) {

            txtDuration.setText(
                    "Free Plan"
            );

        }

        else {

            txtDuration.setText(
                    formatDuration(duration)
            );

        }


        // ========================================================
        // WAREHOUSE DISPLAY
        // ========================================================

        if (additionalPurchase) {

            /*
             * Payment page shows only NEW warehouses being added.
             *
             * Existing warehouses are NOT charged again.
             */

            txtWarehouses.setText(

                    "Adding "
                            + additionalWarehouses
                            + " New Warehouse(s)"

            );

        }

        else {

            if (warehouseCount == 1) {

                txtWarehouses.setText(
                        "1 Warehouse"
                );

            }

            else {

                txtWarehouses.setText(
                        warehouseCount
                                + " Warehouses"
                );

            }

        }


        // ========================================================
        // AMOUNT
        // ========================================================

        txtAmount.setText(
                "₹" + amount
        );


        // ========================================================
        // BUTTON
        // ========================================================

        if (plan.equals("FREE")) {

            btnPay.setText(
                    "Activate Free Plan"
            );

        }

        else {

            btnPay.setText(
                    "Pay ₹" + amount
            );

        }

    }


    // ============================================================
    // FORMAT DURATION
    // ============================================================

    private String formatDuration(
            String value
    ) {

        if (value.equals("MONTHLY")) {

            return "Monthly Subscription";

        }


        if (value.equals("YEARLY")) {

            return "Yearly Subscription";

        }


        return value;

    }


    // ============================================================
    // PROCESS PAYMENT
    // ============================================================

    private void processPayment() {


        if (isProcessing) {

            return;

        }


        // ========================================================
        // FREE
        // ========================================================

        if (plan.equals("FREE")) {

            activateFreePlan();

            return;

        }


        // ========================================================
        // PAID
        // ========================================================

        startRazorpayPayment();

    }


    // ============================================================
    // ACTIVATE FREE PLAN
    // ============================================================

    private void activateFreePlan() {

        isProcessing = true;

        btnPay.setEnabled(false);

        btnPay.setText(
                "Activating..."
        );


        saveSubscription(
                "FREE_PLAN"
        );

    }


    // ============================================================
    // START RAZORPAY
    // ============================================================

    private void startRazorpayPayment() {

        try {

            Checkout checkout =
                    new Checkout();


            // ====================================================
            // TEST KEY
            // ====================================================

            checkout.setKeyID(
                    "rzp_test_TPC8S4Ag2Vxrd0"
            );


            JSONObject options =
                    new JSONObject();


            // ====================================================
            // NAME
            // ====================================================

            options.put(
                    "name",
                    "WareVista"
            );


            // ====================================================
            // DESCRIPTION
            // ====================================================

            if (additionalPurchase) {

                options.put(
                        "description",
                        plan
                                + " Additional Warehouse Subscription"
                );

            }

            else {

                options.put(
                        "description",
                        plan
                                + " "
                                + duration
                                + " Subscription"
                );

            }


            // ====================================================
            // AMOUNT
            // ====================================================

            options.put(
                    "amount",
                    amount * 100
            );


            // ====================================================
            // CURRENCY
            // ====================================================

            options.put(
                    "currency",
                    "INR"
            );


            // ====================================================
            // PREFILL
            // ====================================================

            JSONObject prefill =
                    new JSONObject();

            options.put(
                    "prefill",
                    prefill
            );


            // ====================================================
            // THEME
            // ====================================================

            JSONObject theme =
                    new JSONObject();

            theme.put(
                    "color",
                    "#2E7D32"
            );

            options.put(
                    "theme",
                    theme
            );


            // ====================================================
            // OPEN
            // ====================================================

            checkout.open(
                    this,
                    options
            );

        }

        catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to open payment: "
                            + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            resetButton();

        }

    }


    // ============================================================
    // PAYMENT SUCCESS
    // ============================================================

    @Override
    public void onPaymentSuccess(
            String razorpayPaymentId
    ) {

        if (isProcessing) {

            return;

        }


        isProcessing = true;

        btnPay.setEnabled(false);

        btnPay.setText(
                "Saving Subscription..."
        );


        Toast.makeText(
                this,
                "Payment successful. Activating subscription...",
                Toast.LENGTH_SHORT
        ).show();


        saveSubscription(
                razorpayPaymentId
        );

    }


    // ============================================================
    // PAYMENT ERROR
    // ============================================================

    @Override
    public void onPaymentError(
            int code,
            String response
    ) {

        isProcessing = false;

        resetButton();


        Toast.makeText(
                this,
                "Payment Cancelled or Failed",
                Toast.LENGTH_LONG
        ).show();

    }


    // ============================================================
    // SAVE SUBSCRIPTION
    // ============================================================

    private void saveSubscription(
            String paymentId
    ) {

        StringRequest request =
                new StringRequest(

                        Request.Method.POST,

                        BASE_URL,


                        // ====================================================
                        // SUCCESS
                        // ====================================================

                        response -> {

                            try {

                                JSONObject object =
                                        new JSONObject(
                                                response
                                        );


                                boolean success =
                                        object.optBoolean(
                                                "success",
                                                false
                                        );


                                if (!success) {

                                    String message =
                                            object.optString(
                                                    "message",
                                                    "Unable to activate subscription"
                                            );


                                    Toast.makeText(
                                            SubscriptionPaymentActivity.this,
                                            message,
                                            Toast.LENGTH_LONG
                                    ).show();


                                    resetButton();

                                    return;

                                }


                                // ============================================
                                // SERVER VALUES
                                // ============================================

                                String subscriptionId =
                                        object.optString(
                                                "subscriptionId",
                                                ""
                                        );


                                String expiryDate =
                                        object.optString(
                                                "expiryDate",
                                                ""
                                        );


                                int serverWarehouseLimit =
                                        object.optInt(
                                                "warehouseLimit",
                                                newAppliedLimit > 0
                                                        ? newAppliedLimit
                                                        : warehouseCount
                                        );


                                /*
                                 * Server is the final authority.
                                 */

                                warehouseCount =
                                        serverWarehouseLimit;


                                newAppliedLimit =
                                        object.optInt(
                                                "newAppliedLimit",
                                                serverWarehouseLimit
                                        );


                                // ============================================
                                // SAVE LOCAL SUBSCRIPTION
                                // ============================================

                                saveSubscriptionLocally();


                                // ============================================
                                // SHOW SUCCESS
                                // ============================================

                                showSuccessDialog(
                                        subscriptionId,
                                        expiryDate
                                );

                            }

                            catch (Exception e) {

                                Toast.makeText(
                                        SubscriptionPaymentActivity.this,
                                        "Invalid server response",
                                        Toast.LENGTH_LONG
                                ).show();

                                resetButton();

                            }

                        },


                        // ====================================================
                        // ERROR
                        // ====================================================

                        error -> {

                            Toast.makeText(
                                    SubscriptionPaymentActivity.this,
                                    "Subscription activation failed",
                                    Toast.LENGTH_LONG
                            ).show();

                            resetButton();

                        }

                ) {


                    // ====================================================
                    // PARAMETERS
                    // ====================================================

                    @Override
                    protected Map<String, String> getParams() {

                        Map<String, String> params =
                                new HashMap<>();


                        // =================================================
                        // MODULE
                        // =================================================

                        params.put(
                                "module",
                                "createSubscription"
                        );


                        // =================================================
                        // USER ID
                        // =================================================

                        params.put(
                                "userId",
                                userId
                        );


                        // =================================================
                        // PLAN
                        // =================================================

                        params.put(
                                "plan",
                                plan
                        );


                        // =================================================
                        // DURATION
                        // =================================================

                        params.put(
                                "duration",
                                duration
                        );


                        // =================================================
                        // WAREHOUSE LIMIT
                        // =================================================

                        /*
                         * FIRST-TIME:
                         *
                         * warehouseLimit = selected total warehouses
                         *
                         *
                         * ADDITIONAL:
                         *
                         * warehouseLimit = FINAL APPLIED LIMIT
                         *
                         * Example:
                         *
                         * Existing Limit = 3
                         * New = 2
                         * Final Limit = 5
                         */

                        int requestWarehouseLimit;


                        if (additionalPurchase) {

                            requestWarehouseLimit =
                                    newAppliedLimit;

                        }

                        else {

                            requestWarehouseLimit =
                                    warehouseCount;

                        }


                        params.put(
                                "warehouseLimit",
                                String.valueOf(
                                        requestWarehouseLimit
                                )
                        );


                        // =================================================
                        // AMOUNT
                        // =================================================

                        /*
                         * IMPORTANT:
                         *
                         * For additional purchase,
                         * amount is already calculated only for
                         * NEW warehouses in SubscriptionActivity.
                         */

                        params.put(
                                "amount",
                                String.valueOf(
                                        amount
                                )
                        );


                        // =================================================
                        // PAYMENT ID
                        // =================================================

                        params.put(
                                "paymentId",
                                paymentId
                        );


                        // =================================================
                        // ADDITIONAL PURCHASE
                        // =================================================

                        params.put(
                                "additionalPurchase",
                                String.valueOf(
                                        additionalPurchase
                                )
                        );


                        // =================================================
                        // ADDITIONAL WAREHOUSES
                        // =================================================

                        params.put(
                                "additionalWarehouses",
                                String.valueOf(
                                        additionalPurchase
                                                ? additionalWarehouses
                                                : 0
                                )
                        );


                        // =================================================
                        // NEW APPLIED LIMIT
                        // =================================================

                        params.put(
                                "newAppliedLimit",
                                String.valueOf(
                                        additionalPurchase
                                                ? newAppliedLimit
                                                : warehouseCount
                                )
                        );


                        return params;

                    }

                };


        // ========================================================
        // REQUEST QUEUE
        // ========================================================

        RequestQueue queue =
                Volley.newRequestQueue(
                        this
                );


        queue.add(
                request
        );

    }


    // ============================================================
    // SAVE LOCAL SUBSCRIPTION
    // ============================================================

    private void saveSubscriptionLocally() {

        // ========================================================
        // DEFAULT ACCESS
        // ========================================================

        boolean purchaseAccess = true;

        boolean salesAccess = true;

        boolean historyAccess = false;

        boolean liveStockAccess = false;


        // ========================================================
        // GOLD
        // ========================================================

        if (plan.equals("GOLD")) {

            purchaseAccess = true;

            salesAccess = true;

            historyAccess = true;

            liveStockAccess = false;

        }


        // ========================================================
        // PLATINUM
        // ========================================================

        else if (plan.equals("PLATINUM")) {

            purchaseAccess = true;

            salesAccess = true;

            historyAccess = true;

            liveStockAccess = true;

        }


        // ========================================================
        // FREE
        // ========================================================

        else {

            purchaseAccess = true;

            salesAccess = true;

            historyAccess = false;

            liveStockAccess = false;

        }


        // ========================================================
        // SAVE
        // ========================================================
        SubscriptionManager.saveSubscription(

                this,

                plan,

                newAppliedLimit,

                purchaseAccess,

                salesAccess,

                historyAccess,

                liveStockAccess

        );

    }


    // ============================================================
    // RESET BUTTON
    // ============================================================

    private void resetButton() {

        isProcessing = false;

        btnPay.setEnabled(true);


        if (plan.equals("FREE")) {

            btnPay.setText(
                    "Activate Free Plan"
            );

        }

        else {

            btnPay.setText(
                    "Pay ₹" + amount
            );

        }

    }


    // ============================================================
    // SUCCESS DIALOG
    // ============================================================

    private void showSuccessDialog(

            String subscriptionId,

            String expiryDate

    ) {

        String message;


        // ========================================================
        // FREE
        // ========================================================

        if (plan.equals("FREE")) {

            message =

                    "Plan: FREE"

                            + "\n\nPurchase: Available"

                            + "\nSales: Available"

                            + "\nHistory: Locked"

                            + "\nLive Stock: Locked"

                            + "\nWarehouse Limit: 1"

                            + "\n\nSubscription activated successfully.";

        }


        // ========================================================
        // GOLD
        // ========================================================

        else if (plan.equals("GOLD")) {


            if (additionalPurchase) {

                message =

                        "Plan: GOLD"

                                + "\n\nPurchase: Available"

                                + "\nSales: Available"

                                + "\nHistory: Available"

                                + "\nLive Stock: Locked"

                                + "\n\nAdded Warehouses: "
                                + additionalWarehouses

                                + "\nNew Applied Limit: "
                                + newAppliedLimit

                                + "\nExpiry: "
                                + expiryDate;

            }

            else {

                message =

                        "Plan: GOLD"

                                + "\n\nPurchase: Available"

                                + "\nSales: Available"

                                + "\nHistory: Available"

                                + "\nLive Stock: Locked"

                                + "\nWarehouse Limit: "
                                + warehouseCount

                                + "\nExpiry: "
                                + expiryDate;

            }

        }


        // ========================================================
        // PLATINUM
        // ========================================================

        else {

            if (additionalPurchase) {

                message =

                        "Plan: PLATINUM"

                                + "\n\nPurchase: Available"

                                + "\nSales: Available"

                                + "\nHistory: Available"

                                + "\nLive Stock: Available"

                                + "\n\nAdded Warehouses: "
                                + additionalWarehouses

                                + "\nNew Applied Limit: "
                                + newAppliedLimit

                                + "\nExpiry: "
                                + expiryDate;

            }

            else {

                message =

                        "Plan: PLATINUM"

                                + "\n\nPurchase: Available"

                                + "\nSales: Available"

                                + "\nHistory: Available"

                                + "\nLive Stock: Available"

                                + "\nWarehouse Limit: "
                                + warehouseCount

                                + "\nExpiry: "
                                + expiryDate;

            }

        }


        // ========================================================
        // DIALOG
        // ========================================================

        new android.app.AlertDialog.Builder(
                this
        )

                .setTitle(
                        "🎉 Subscription Activated"
                )

                .setMessage(
                        message
                )

                .setCancelable(
                        false
                )

                .setPositiveButton(

                        "Go to Dashboard",

                        (dialog, which) ->
                                openAdminDashboard()

                )

                .show();

    }


    // ============================================================
    // OPEN ADMIN DASHBOARD
    // ============================================================

    private void openAdminDashboard() {

        Intent intent =
                new Intent(
                        SubscriptionPaymentActivity.this,
                        AdminDashboardActivity.class
                );


        intent.setFlags(

                Intent.FLAG_ACTIVITY_NEW_TASK
                        |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

        );


        startActivity(intent);

        finish();

    }


    // ============================================================
    // BACK
    // ============================================================

    @Override
    public void onBackPressed() {

        if (!isProcessing) {

            finish();

        }

    }

}