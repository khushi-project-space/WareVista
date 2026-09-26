package com.example.warevista;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.net.Uri;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONObject;
import androidx.appcompat.app.AlertDialog;
import android.widget.AdapterView;
import android.widget.Spinner;

public class AdminDashboardActivity
        extends AppCompatActivity {

// ============================================================
// GOOGLE APPS SCRIPT URL
// ============================================================

    private static final String BASE_URL =
            "https://script.google.com/macros/s/AKfycbxH_jxb8MUvSXeZQZAE9bbEGTojDKDoGD2-GMc3cWnn4EcI_fz42UCK4mTw2662HV14/exec";


// ============================================================
// UI
// ============================================================

    private MaterialCardView cardCreateWarehouse;
    private MaterialCardView cardManageStaff;
    private android.widget.TextView txtCreateWarehouseIcon;
    private android.widget.TextView txtCreateWarehouseLock;

    private android.widget.TextView txtCreateWarehouseTitle;
    private MaterialCardView cardSubscription;
    private MaterialCardView cardAdminLogout;
    private int currentWarehouseCount = 0;

    private MaterialCardView cardCurrentSubscription;

    private android.widget.TextView txtSubscriptionIcon;
    private android.widget.TextView txtSubscriptionPlan;
    private android.widget.TextView txtSubscriptionStatus;
    private android.widget.TextView txtSubscriptionDuration;
    private android.widget.TextView txtSubscriptionWarehouse;



// ============================================================
// SESSION
// ============================================================

    private SharedPreferences preferences;

    private String userId = "";
    private String subscriptionPlan = "FREE";

    private int warehouseLimit = 1;


// ============================================================
// ON CREATE
// ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        LanguageManager.applySavedLanguage(this);

        setContentView(
                R.layout.activity_admin_dashboard
        );

        Spinner spinnerLanguage =
                findViewById(R.id.spinnerLanguage);

        String currentLanguage =
                LanguageManager.getLanguage(this);

        if (currentLanguage.equals("gu")) {

            spinnerLanguage.setSelection(1);

        } else if (currentLanguage.equals("hi")) {

            spinnerLanguage.setSelection(2);

        } else {

            spinnerLanguage.setSelection(0);
        }

        spinnerLanguage.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            android.view.View view,
                            int position,
                            long id) {

                        String selectedLanguage;

                        if (position == 1) {

                            selectedLanguage = "gu";

                        } else if (position == 2) {

                            selectedLanguage = "hi";

                        } else {

                            selectedLanguage = "en";
                        }

                        String savedLanguage =
                                LanguageManager.getLanguage(
                                        AdminDashboardActivity.this
                                );

                        if (!selectedLanguage.equals(savedLanguage)) {

                            LanguageManager.setLanguage(
                                    AdminDashboardActivity.this,
                                    selectedLanguage
                            );

                            recreate();
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                }
        );
        findViewById(R.id.btnAskWareVista).setOnClickListener(v -> {

            Intent intent = new Intent(AdminDashboardActivity.this, AskWareVistaActivity.class);
            startActivity(intent);

        });


        // ========================================================
        // SESSION
        // ========================================================

        preferences =
                getSharedPreferences(
                        "WareVista",
                        MODE_PRIVATE
                );


        // ========================================================
        // LOAD LOCAL SESSION
        // ========================================================

        userId =
                preferences.getString(
                        "userId",
                        ""
                );


        subscriptionPlan =
                preferences.getString(

                        "subscriptionPlan",

                        preferences.getString(
                                "subscription",
                                "FREE"
                        )

                );


        warehouseLimit =
                preferences.getInt(
                        "warehouseLimit",
                        1
                );


        // ========================================================
        // SAFETY
        // ========================================================

        if (userId == null) {

            userId = "";

        }


        if (

                subscriptionPlan == null

                        ||

                        subscriptionPlan.trim().isEmpty()

        ) {

            subscriptionPlan = "FREE";

        }


        subscriptionPlan =

                subscriptionPlan
                        .trim()
                        .toUpperCase();


        if (warehouseLimit < 1) {

            warehouseLimit = 1;

        }



        // ========================================================
        // FIND VIEWS
        // ========================================================

        cardCreateWarehouse =
                findViewById(
                        R.id.cardCreateWarehouse
                );

        txtCreateWarehouseIcon =
                findViewById(
                        R.id.txtCreateWarehouseIcon
                );

        txtCreateWarehouseLock =
                findViewById(
                        R.id.txtCreateWarehouseLock
                );


        cardManageStaff =
                findViewById(
                        R.id.cardManageStaff
                );


        cardSubscription =
                findViewById(
                        R.id.cardSubscription
                );


        cardAdminLogout =
                findViewById(
                        R.id.cardAdminLogout
                );

        cardCurrentSubscription =
                findViewById(
                        R.id.cardCurrentSubscription
                );

        txtSubscriptionIcon =
                findViewById(
                        R.id.txtSubscriptionIcon
                );

        txtSubscriptionPlan =
                findViewById(
                        R.id.txtSubscriptionPlan
                );

        txtSubscriptionStatus =
                findViewById(
                        R.id.txtSubscriptionStatus
                );

        txtSubscriptionDuration =
                findViewById(
                        R.id.txtSubscriptionDuration
                );

        txtSubscriptionWarehouse =
                findViewById(
                        R.id.txtSubscriptionWarehouse
                );
        txtCreateWarehouseTitle =
                findViewById(
                        R.id.txtCreateWarehouseTitle
                );


        // ========================================================
        // CREATE WAREHOUSE
        // ========================================================

        cardCreateWarehouse.setOnClickListener(v -> {

            openCreateWarehouse();

        });


        // ========================================================
        // MANAGE STAFF
        // ========================================================

        cardManageStaff.setOnClickListener(v -> {


            Intent intent =

                    new Intent(

                            AdminDashboardActivity.this,

                            ManageStaffActivity.class

                    );


            startActivity(intent);

        });


        // ========================================================
        // SUBSCRIPTION
        // ========================================================

        cardSubscription.setOnClickListener(v -> {


            Intent intent =

                    new Intent(

                            AdminDashboardActivity.this,

                            SubscriptionActivity.class

                    );


            startActivity(intent);

        });

        cardCurrentSubscription.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AdminDashboardActivity.this,
                            SubscriptionActivity.class
                    );

            startActivity(intent);

        });


        // ========================================================
        // LOGOUT
        // ========================================================

        cardAdminLogout.setOnClickListener(v -> {

            logout();

        });


        // ========================================================
        // LOAD FRESH SUBSCRIPTION
        // ========================================================

        loadFreshSubscription();

    }


// ============================================================
// ON RESUME
//
// Subscription બદલ્યા પછી fresh data મળશે
// ============================================================

    @Override
    protected void onResume() {

        super.onResume();

        loadFreshSubscription();

        checkCreateWarehouseStatus();

    }


// ============================================================
// LOAD FRESH SUBSCRIPTION
// ============================================================

    private void loadFreshSubscription() {


        // ========================================================
        // USER ID REQUIRED
        // ========================================================

        if (

                userId == null

                        ||

                        userId.trim().isEmpty()

        ) {

            return;

        }


        // ========================================================
        // LOAD FROM SERVER
        // ========================================================

        SubscriptionManager.loadUserSubscription(

                this,

                userId,

                "",


                new SubscriptionManager.SubscriptionCallback() {


                    @Override
                    public void onResult(

                            boolean success,

                            String plan,

                            int limit,

                            boolean purchaseAccess,

                            boolean salesAccess,

                            boolean historyAccess,

                            boolean liveStockAccess,

                            String message

                    ) {


                        // ========================================
                        // SUCCESS
                        // ========================================

                        if (!success) {

                            return;

                        }


                        // ========================================
                        // PLAN
                        // ========================================

                        subscriptionPlan = plan;


                        if (

                                subscriptionPlan == null

                                        ||

                                        subscriptionPlan.trim().isEmpty()

                        ) {

                            subscriptionPlan = "FREE";

                        }


                        subscriptionPlan =

                                subscriptionPlan
                                        .trim()
                                        .toUpperCase();


                        // ========================================
                        // WAREHOUSE LIMIT
                        // ========================================

                        warehouseLimit = limit;


                        if (warehouseLimit < 1) {

                            warehouseLimit = 1;

                        }



                        // ========================================
                        // SAVE SESSION
                        // ========================================

                        preferences
                                .edit()


                                .putString(

                                        "subscriptionPlan",

                                        subscriptionPlan

                                )


                                .putString(

                                        "subscription",

                                        subscriptionPlan

                                )


                                .putInt(

                                        "warehouseLimit",

                                        warehouseLimit

                                )


                                .putBoolean(

                                        "accessPurchase",

                                        purchaseAccess

                                )


                                .putBoolean(

                                        "accessSales",

                                        salesAccess

                                )


                                .putBoolean(

                                        "accessHistory",

                                        historyAccess

                                )


                                .putBoolean(

                                        "accessLiveStock",

                                        liveStockAccess

                                )


                                .apply();

                        // ========================================
                        // UPDATE SUBSCRIPTION BAR
                        // ========================================

                        updateSubscriptionBar();

                    }

                }

        );

    }


    // ============================================================
// UPDATE CURRENT SUBSCRIPTION BAR
// ============================================================

    private void updateSubscriptionBar() {

        if (txtSubscriptionPlan == null) {
            return;
        }


        // ========================================================
        // PLAN
        // ========================================================

        String plan =
                subscriptionPlan == null
                        ? "FREE"
                        : subscriptionPlan
                        .trim()
                        .toUpperCase();


        // ========================================================
        // DEFAULT
        // ========================================================

        txtSubscriptionStatus.setVisibility(
                android.view.View.VISIBLE
        );

        txtSubscriptionStatus.setText(
                getString(R.string.active)
        );

        txtSubscriptionStatus.setTextColor(
                android.graphics.Color.parseColor(
                        "#81C784"
                )
        );

        txtSubscriptionStatus.setTextSize(
                11
        );


        // ========================================================
        // FREE
        // ========================================================

        if (plan.equals("FREE")) {

            txtSubscriptionIcon.setText(
                    "🆓"
            );

            txtSubscriptionPlan.setText(
                    "FREE"
            );
//            txtSubscriptionDuration.setText(
//                    "Free • Basic Access"
//            );

            txtSubscriptionWarehouse.setText(
                    "🏢 1 / 1"
            );

            return;
        }


        // ========================================================
        // GOLD
        // ========================================================

        if (plan.equals("GOLD")) {

            txtSubscriptionIcon.setText(
                    "🥇"
            );

            txtSubscriptionPlan.setText(
                    "GOLD"
            );

            txtSubscriptionDuration.setText(
                    getString(R.string.monthly)
            );



            return;
        }


        // ========================================================
        // PLATINUM
        // ========================================================
        if (plan.equals("PLATINUM")) {

            txtSubscriptionIcon.setText(
                    "💎"
            );

            txtSubscriptionPlan.setText(
                    "PLATINUM"
            );

            txtSubscriptionDuration.setText(
                    getString(R.string.current_plan)
            );

            updateSubscriptionWarehouseCount();

            return;
        }


        // ========================================================
        // SAFETY
        // ========================================================

        txtSubscriptionIcon.setText(
                "📦"
        );

        txtSubscriptionPlan.setText(
                plan
        );

        txtSubscriptionDuration.setText(
                getString(R.string.current_plan)
        );

        txtSubscriptionWarehouse.setText(
                "🏢 "
                        + warehouseLimit
                        + " Warehouses"
        );

    }


    // ============================================================
// CREATE WAREHOUSE CLICK FEEDBACK
// ============================================================

    private void showCreateWarehouseChecking() {

        cardCreateWarehouse.setEnabled(false);

        // Small press animation
        cardCreateWarehouse.animate()
                .scaleX(0.97f)
                .scaleY(0.97f)
                .setDuration(100)
                .withEndAction(() -> {

                    cardCreateWarehouse.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .start();

                })
                .start();

        // Change text immediately
        TextView title =
                (TextView) ((android.view.ViewGroup)
                        cardCreateWarehouse.getChildAt(0))
                        .findViewWithTag("createWarehouseTitle");

    }
// ============================================================
// CREATE WAREHOUSE
//
// FIRST CHECK LIMIT FROM BACKEND
// ============================================================

    private void openCreateWarehouse() {


        // ========================================================
        // USER VALIDATION
        // ========================================================

        if (

                userId == null

                        ||

                        userId.trim().isEmpty()

        ) {


            Toast.makeText(

                    this,

                    getString(R.string.user_session_not_found),

                    Toast.LENGTH_LONG

            ).show();


            logout();

            return;

        }


        // ========================================================
        // DISABLE CARD
        //
        // Prevent multiple clicks
        // ========================================================

        cardCreateWarehouse.setEnabled(false);
        // ========================================================
// IMMEDIATE CLICK FEEDBACK
// ========================================================

        txtCreateWarehouseTitle.setText(
                getString(R.string.checking)
        );

// Small press animation
        cardCreateWarehouse.animate()
                .scaleX(0.97f)
                .scaleY(0.97f)
                .setDuration(100)
                .withEndAction(() -> {

                    cardCreateWarehouse.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .start();

                })
                .start();


        // ========================================================
        // CREATE URL
        // ========================================================

        String url =

                BASE_URL

                        + "?module=checkWarehouseLimit"

                        + "&userId="

                        + Uri.encode(
                        userId
                );


        // ========================================================
        // API REQUEST
        // ========================================================

        StringRequest request =

                new StringRequest(

                        Request.Method.GET,

                        url,


                        // ============================================
                        // SUCCESS
                        // ============================================

                        response -> {


                            // ========================================
                            // ENABLE CARD
                            // ========================================

                            cardCreateWarehouse.setEnabled(true);

                            txtCreateWarehouseTitle.setText(
                                    getString(R.string.create_warehouse)
                            );

                            cardCreateWarehouse.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(100)
                                    .start();


                            try {


                                // ====================================
                                // PARSE JSON
                                // ====================================

                                JSONObject object =

                                        new JSONObject(
                                                response
                                        );


                                // ====================================
                                // SUCCESS
                                // ====================================

                                boolean success =

                                        object.optBoolean(

                                                "success",

                                                false

                                        );


                                if (!success) {


                                    Toast.makeText(

                                            AdminDashboardActivity.this,

                                            object.optString(

                                                    "message",

                                                    "Unable to check warehouse limit"

                                            ),

                                            Toast.LENGTH_LONG

                                    ).show();


                                    return;

                                }


                                // ====================================
                                // ALLOWED
                                // ====================================

                                boolean allowed =

                                        object.optBoolean(

                                                "allowed",

                                                false

                                        );
                                updateCreateWarehouseUI(allowed);


                                // ====================================
                                // FRESH PLAN
                                // ====================================

                                String freshPlan =

                                        object.optString(

                                                        "plan",

                                                        subscriptionPlan

                                                )

                                                .trim()
                                                .toUpperCase();


                                // ====================================
                                // SAFETY
                                // ====================================

                                if (

                                        freshPlan.isEmpty()

                                ) {

                                    freshPlan = "FREE";

                                }


                                // ====================================
                                // FRESH LIMIT
                                // ====================================

                                int freshLimit =

                                        object.optInt(

                                                "warehouseLimit",

                                                warehouseLimit

                                        );


                                if (freshLimit < 1) {

                                    freshLimit = 1;

                                }


                                // ====================================
                                // UPDATE VARIABLES
                                // ====================================

                                subscriptionPlan =
                                        freshPlan;


                                warehouseLimit =
                                        freshLimit;


                                // ====================================
                                // SAVE UPDATED SUBSCRIPTION
                                // ====================================

                                preferences
                                        .edit()


                                        .putString(

                                                "subscriptionPlan",

                                                subscriptionPlan

                                        )


                                        .putString(

                                                "subscription",

                                                subscriptionPlan

                                        )


                                        .putInt(

                                                "warehouseLimit",

                                                warehouseLimit

                                        )


                                        .apply();


                                // ====================================
// LIMIT REACHED
// ====================================

                                if (!allowed) {


                                    String message =

                                            object.optString(

                                                    "message",

                                                    "Warehouse limit reached"

                                            );


                                    showWarehouseLimitDialog(
                                            message
                                    );


                                    return;

                                }

                                // ====================================
                                // ALLOWED
                                //
                                // OPEN CREATE WAREHOUSE PAGE
                                // ====================================

                                Intent intent =

                                        new Intent(

                                                AdminDashboardActivity.this,

                                                CreateWarehouseActivity.class

                                        );


                                // ====================================
                                // USER
                                // ====================================

                                intent.putExtra(

                                        "userId",

                                        userId

                                );


                                // ====================================
                                // SUBSCRIPTION
                                // ====================================

                                intent.putExtra(

                                        "subscriptionPlan",

                                        subscriptionPlan

                                );


                                intent.putExtra(

                                        "warehouseLimit",

                                        warehouseLimit

                                );


                                startActivity(
                                        intent
                                );


                            }

                            // ========================================
                            // JSON ERROR
                            // ========================================

                            catch (Exception e) {


                                Toast.makeText(

                                        AdminDashboardActivity.this,

                                        "Server Response: " + response,

                                        Toast.LENGTH_LONG

                                ).show();


                                e.printStackTrace();

                            }

                        },


                        // ============================================
                        // CONNECTION ERROR
                        // ============================================

                        error -> {


                            cardCreateWarehouse.setEnabled(true);

                            txtCreateWarehouseTitle.setText(
                                    getString(R.string.create_warehouse)
                            );

                            cardCreateWarehouse.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(100)
                                    .start();


                            Toast.makeText(

                                    AdminDashboardActivity.this,

                                    getString(R.string.connection_error_retry),

                                    Toast.LENGTH_LONG

                            ).show();


                            error.printStackTrace();

                        }

                );


        // ========================================================
        // SEND REQUEST
        // ========================================================

        Volley

                .newRequestQueue(
                        getApplicationContext()
                )

                .add(
                        request
                );

    }


    // ============================================================
// SHOW WAREHOUSE LIMIT DIALOG
// ============================================================

    private void showWarehouseLimitDialog(
            String message
    ) {

        new AlertDialog.Builder(this)

                .setTitle(
                        getString(R.string.warehouse_limit_reached)
                )

                .setMessage(
                        message
                )

                // ================================================
                // CANCEL
                // ================================================

                .setNegativeButton(
                        getString(R.string.cancel),
                        null
                )

                // ================================================
                // VIEW SUBSCRIPTION
                // ================================================

                .setPositiveButton(
                        getString(R.string.view_subscription),

                        (dialog, which) -> {

                            Intent intent =

                                    new Intent(

                                            AdminDashboardActivity.this,

                                            SubscriptionActivity.class

                                    );


                            startActivity(
                                    intent
                            );

                        }

                )

                .show();

    }

// ============================================================
// LOGOUT
// ============================================================

    private void logout() {


        // ========================================================
        // CLEAR LOCAL SESSION
        // ========================================================

        preferences
                .edit()
                .clear()
                .apply();


        // ========================================================
        // CLEAR SUBSCRIPTION
        // ========================================================

        SubscriptionManager.clearSubscription(
                this
        );


        // ========================================================
        // OPEN LOGIN
        // ========================================================

        Intent intent =

                new Intent(

                        this,

                        LoginActivity.class

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
// UPDATE CREATE WAREHOUSE UI
// ============================================================

    // ============================================================
// UPDATE CREATE WAREHOUSE UI
// ============================================================

    private void updateCreateWarehouseUI(boolean allowed) {

        if (allowed) {

            // ====================================================
            // UNLOCKED
            // ====================================================

            cardCreateWarehouse.setEnabled(true);

            cardCreateWarehouse.setAlpha(1.0f);

            txtCreateWarehouseIcon.setText("🏢");

            txtCreateWarehouseLock.setVisibility(
                    android.view.View.GONE
            );

        } else {

            // ====================================================
            // LOCKED UI
            // ====================================================

            // IMPORTANT:
            // Keep enabled so user can click the card
            // and see View Subscription dialog.

            cardCreateWarehouse.setEnabled(true);

            cardCreateWarehouse.setAlpha(0.55f);

            txtCreateWarehouseIcon.setText("🏢");

            txtCreateWarehouseLock.setVisibility(
                    android.view.View.VISIBLE
            );

        }

    }

    // ============================================================
// CHECK CREATE WAREHOUSE UI STATUS
// ============================================================

    private void checkCreateWarehouseStatus() {

        if (userId == null || userId.trim().isEmpty()) {
            return;
        }

        String url =
                BASE_URL
                        + "?module=checkWarehouseLimit"
                        + "&userId="
                        + Uri.encode(userId);

        StringRequest request =
                new StringRequest(

                        Request.Method.GET,

                        url,

                        response -> {

                            try {

                                JSONObject object =
                                        new JSONObject(response);

                                boolean success =
                                        object.optBoolean(
                                                "success",
                                                false
                                        );

                                if (!success) {
                                    return;
                                }

                                boolean allowed =
                                        object.optBoolean(
                                                "allowed",
                                                false
                                        );

                                currentWarehouseCount =
                                        object.optInt(
                                                "currentWarehouseCount",
                                                0
                                        );

                                warehouseLimit =
                                        object.optInt(
                                                "warehouseLimit",
                                                warehouseLimit
                                        );

                                updateCreateWarehouseUI(
                                        allowed
                                );

                                updateSubscriptionWarehouseCount();

                                updateSubscriptionBar();


                            } catch (Exception e) {

                                e.printStackTrace();

                            }

                        },

                        error -> {

                            error.printStackTrace();

                        }

                );

        Volley
                .newRequestQueue(
                        getApplicationContext()
                )
                .add(request);
    }
    private void updateSubscriptionWarehouseCount() {

        if (txtSubscriptionWarehouse == null) {
            return;
        }

        txtSubscriptionWarehouse.setText(
                "🏢 "
                        + currentWarehouseCount
                        + " / "
                        + warehouseLimit
        );
    }

// ============================================================
// BACK
// ============================================================

    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }

}

