package com.example.warevista;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.widget.AdapterView;
import android.widget.Spinner;


public class HomeActivity extends AppCompatActivity {


    // ============================================================
    // UI
    // ============================================================

    private CardView cardPurchase;
    private CardView cardSales;
    private CardView cardStock;
    private CardView cardHistory;
    private CardView cardLogout;

    private LottieAnimationView lottieWarehouse;

    private TextView txtWarehouse;
    private TextView txtDate;


    // ============================================================
    // SESSION
    // ============================================================

    private SharedPreferences preferences;

    private String warehouse = "";
    private String plan = "FREE";

    private String userId = "";


    // ============================================================
    // FEATURE ACCESS
    // ============================================================

    private boolean accessPurchase = true;
    private boolean accessSales = true;
    private boolean accessHistory = false;
    private boolean accessLiveStock = false;


    // ============================================================
    // ANIMATION
    // ============================================================

    private Animation clickAnim;


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        LanguageManager.applySavedLanguage(this);

        setContentView(
                R.layout.activity_home
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
                                        HomeActivity.this
                                );

                        if (!selectedLanguage.equals(savedLanguage)) {

                            LanguageManager.setLanguage(
                                    HomeActivity.this,
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


        // ========================================================
        // SHARED PREFERENCES
        // ========================================================

        preferences =
                getSharedPreferences(
                        "WareVista",
                        MODE_PRIVATE
                );


        // ========================================================
        // FIND VIEWS
        // ========================================================

        lottieWarehouse =
                findViewById(
                        R.id.lottieWarehouse
                );


        txtWarehouse =
                findViewById(
                        R.id.txtWarehouse
                );


        txtDate =
                findViewById(
                        R.id.txtDate
                );


        cardPurchase =
                findViewById(
                        R.id.cardPurchase
                );


        cardSales =
                findViewById(
                        R.id.cardSales
                );


        cardStock =
                findViewById(
                        R.id.cardStock
                );


        cardHistory =
                findViewById(
                        R.id.cardHistory
                );


        cardLogout =
                findViewById(
                        R.id.cardLogout
                );


        // ========================================================
        // CLICK ANIMATION
        // ========================================================

        clickAnim =
                AnimationUtils.loadAnimation(
                        this,
                        R.anim.card_click
                );


        // ========================================================
        // HEADER ANIMATION
        // ========================================================

        if (lottieWarehouse != null) {

            lottieWarehouse.playAnimation();

        }


        // ========================================================
        // LOAD LOCAL SESSION FIRST
        // ========================================================

        loadSession();


        // ========================================================
        // HEADER
        // ========================================================

        setupHeader();


        // ========================================================
        // SETUP CARDS
        // ========================================================

        setupPurchase();

        setupSales();

        setupHistory();

        setupStock();

        setupLogout();


        // ========================================================
        // APPLY LOCAL SUBSCRIPTION FIRST
        // FAST UI
        // ========================================================

        applySubscriptionFeatures();


        // ========================================================
        // LOAD FRESH SUBSCRIPTION FROM SERVER
        // ========================================================

        loadFreshSubscription();

    }


    // ============================================================
    // ON RESUME
    // ============================================================

//    @Override
//    protected void onResume() {
//
//        super.onResume();
//
//        loadSession();
//
//        applySubscriptionFeatures();
//
//    }

    @Override
    protected void onResume() {

        super.onResume();

        // ========================================================
        // LOAD LOCAL SESSION
        // ========================================================

        loadSession();

        applySubscriptionFeatures();


        // ========================================================
        // REFRESH SUBSCRIPTION FROM SERVER
        // ========================================================

        loadFreshSubscription();

    }
    // ============================================================
    // LOAD SESSION
    // ============================================================

    private void loadSession() {


        warehouse =
                preferences.getString(
                        "warehouse",
                        ""
                );


        userId =
                preferences.getString(
                        "userId",
                        ""
                );


        plan =
                preferences.getString(
                        "subscriptionPlan",
                        preferences.getString(
                                "subscription",
                                "FREE"
                        )
                );


        accessPurchase =
                preferences.getBoolean(
                        "accessPurchase",
                        true
                );


        accessSales =
                preferences.getBoolean(
                        "accessSales",
                        true
                );


        accessHistory =
                preferences.getBoolean(
                        "accessHistory",
                        false
                );


        accessLiveStock =
                preferences.getBoolean(
                        "accessLiveStock",
                        false
                );


        // ========================================================
        // NULL SAFETY
        // ========================================================

        if (warehouse == null) {

            warehouse = "";

        }


        if (userId == null) {

            userId = "";

        }


        if (plan == null ||
                plan.trim().isEmpty()) {

            plan = "FREE";

        }


        plan = plan.trim().toUpperCase();

        userId = userId.trim();

    }


    // ============================================================
    // LOAD FRESH SUBSCRIPTION
    // ============================================================

    private void loadFreshSubscription() {


        // ========================================================
        // USER ID REQUIRED
        // ========================================================

        if (userId.isEmpty()) {

            return;

        }
        SubscriptionManager.loadUserSubscription(

                this,

                userId,

                warehouse,

                (

                        success,

                        serverPlan,

                        warehouseLimit,

                        purchaseAccess,

                        salesAccess,

                        historyAccess,

                        liveStockAccess,

                        message

                ) -> {

                    // ====================================================
                    // SUCCESS
                    // ====================================================

                    if (success) {


                        // ================================================
                        // UPDATE LOCAL VARIABLES
                        // ================================================

                        plan =
                                serverPlan;

                        accessPurchase =
                                purchaseAccess;

                        accessSales =
                                salesAccess;

                        accessHistory =
                                historyAccess;

                        accessLiveStock =
                                liveStockAccess;


                        // ================================================
                        // UPDATE UI
                        // ================================================

                        applySubscriptionFeatures();

                    }


                    // ====================================================
                    // FAILED
                    //
                    // LOCAL CACHE CONTINUES
                    // ====================================================

                    else {


                        // Do not reset subscription.
                        // Existing local data will continue.

                    }

                }

        );

    }


    // ============================================================
    // HEADER
    // ============================================================

    private void setupHeader() {


        txtWarehouse.setText(
                getString(R.string.warehouse_label)
                        + warehouse
        );


        SimpleDateFormat sdf =
                new SimpleDateFormat(

                        "dd MMMM yyyy",

                        Locale.getDefault()

                );


        txtDate.setText(
                getString(R.string.date_label)
                        + sdf.format(
                        new Date()
                )
        );

    }


    // ============================================================
    // APPLY SUBSCRIPTION FEATURES
    // ============================================================

    private void applySubscriptionFeatures() {


        // ========================================================
        // PURCHASE
        // ========================================================

        if (accessPurchase) {

            unlockCard(cardPurchase);

        }

        else {

            lockCard(cardPurchase);

        }


        // ========================================================
        // SALES
        // ========================================================

        if (accessSales) {

            unlockCard(cardSales);

        }

        else {

            lockCard(cardSales);

        }


        // ========================================================
        // HISTORY
        // ========================================================

        if (accessHistory) {

            unlockCard(cardHistory);

        }

        else {

            lockCard(cardHistory);

        }


        // ========================================================
        // LIVE STOCK
        // ========================================================

        if (accessLiveStock) {

            unlockCard(cardStock);

        }

        else {

            lockCard(cardStock);

        }

    }


    // ============================================================
    // SETUP PURCHASE
    // ============================================================

    private void setupPurchase() {

        cardPurchase.setOnClickListener(v -> {


            if (!accessPurchase) {

                showLockedMessage(
                        "Purchase"
                );

                return;

            }


            v.startAnimation(
                    clickAnim
            );


            startActivity(

                    new Intent(

                            HomeActivity.this,

                            PurchaseActivity.class

                    )

            );

        });

    }


    // ============================================================
    // SETUP SALES
    // ============================================================

    private void setupSales() {

        cardSales.setOnClickListener(v -> {


            if (!accessSales) {

                showLockedMessage(
                        "Sales"
                );

                return;

            }


            v.startAnimation(
                    clickAnim
            );


            startActivity(

                    new Intent(

                            HomeActivity.this,

                            SalesActivity.class

                    )

            );

        });

    }


    // ============================================================
    // SETUP HISTORY
    // ============================================================

    private void setupHistory() {

        cardHistory.setOnClickListener(v -> {


            if (!accessHistory) {

                showLockedMessage(
                        "History"
                );

                return;

            }


            v.startAnimation(
                    clickAnim
            );


            startActivity(

                    new Intent(

                            HomeActivity.this,

                            HistoryActivity.class

                    )

            );

        });

    }


    // ============================================================
    // SETUP LIVE STOCK
    // ============================================================

    private void setupStock() {

        cardStock.setOnClickListener(v -> {


            if (!accessLiveStock) {

                showLockedMessage(
                        "Live Stock"
                );

                return;

            }


            v.startAnimation(
                    clickAnim
            );


            startActivity(

                    new Intent(

                            HomeActivity.this,

                            StockActivity.class

                    )

            );

        });

    }


    // ============================================================
    // UNLOCK CARD
    // ============================================================

    private void unlockCard(
            CardView card
    ) {

        if (card == null) {

            return;

        }


        card.setAlpha(1f);

        card.setEnabled(true);

        card.setClickable(true);

    }


    // ============================================================
    // LOCK CARD
    // ============================================================

    private void lockCard(
            CardView card
    ) {

        if (card == null) {

            return;

        }


        // ========================================================
        // VISUALLY LOCKED
        // ========================================================

        card.setAlpha(0.50f);


        // ========================================================
        // KEEP CLICKABLE
        // ========================================================

        card.setEnabled(true);

        card.setClickable(true);

    }


    // ============================================================
    // LOCKED MESSAGE
    // ============================================================
//
//    private void showLockedMessage(
//            String feature
//    ) {
//
//
//        String message;
//
//
//        // ========================================================
//        // FREE
//        // ========================================================
//
//        if (plan.equalsIgnoreCase("FREE")) {
//
//
//            if (feature.equalsIgnoreCase("History")) {
//
//                message =
//                        "🔒 History is available in GOLD or PLATINUM plan.";
//
//            }
//
//            else if (
//                    feature.equalsIgnoreCase(
//                            "Live Stock"
//                    )
//            ) {
//
//                message =
//                        "🔒 Live Stock is available in PLATINUM plan.";
//
//            }
//
//            else {
//
//                message =
//                        "🔒 " +
//                                feature +
//                                " is not available for your plan.";
//
//            }
//
//        }
//
//
//        // ========================================================
//        // GOLD
//        // ========================================================
//
//        else if (plan.equalsIgnoreCase("GOLD")) {
//
//
//            if (
//                    feature.equalsIgnoreCase(
//                            "Live Stock"
//                    )
//            ) {
//
//                message =
//                        "🔒 Live Stock is available in PLATINUM plan.";
//
//            }
//
//            else {
//
//                message =
//                        "🔒 This feature is not available for your plan.";
//
//            }
//
//        }
//
//
//        // ========================================================
//        // OTHER
//        // ========================================================
//
//        else {
//
//            message =
//                    "🔒 This feature is not available for your plan.";
//
//        }
//
//
//        Toast.makeText(
//
//                this,
//
//                message,
//
//                Toast.LENGTH_LONG
//
//        ).show();
//
//    }

    private void showLockedMessage(
            String feature
    ) {

        String message;

        if (plan.equalsIgnoreCase("FREE")) {

            if (feature.equalsIgnoreCase("History")) {

                message =
                        getString(
                                R.string.locked_history_message
                        );

            } else if (
                    feature.equalsIgnoreCase("Live Stock")
            ) {

                message =
                        getString(
                                R.string.locked_live_stock_message
                        );

            } else {

                message =
                        getString(
                                R.string.locked_plan_feature_message,
                                feature
                        );
            }

        } else if (plan.equalsIgnoreCase("GOLD")) {

            if (feature.equalsIgnoreCase("Live Stock")) {

                message =
                        getString(
                                R.string.locked_live_stock_message
                        );

            } else {

                message =
                        getString(
                                R.string.locked_feature_message
                        );
            }

        } else {

            message =
                    getString(
                            R.string.locked_feature_message
                    );
        }

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }
    // ============================================================
    // LOGOUT
    // ============================================================

    private void setupLogout() {


        cardLogout.setOnClickListener(v -> {


            v.startAnimation(
                    clickAnim
            );


            logout();

        });

    }


    // ============================================================
    // LOGOUT
    // ============================================================

    private void logout() {


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


        Intent intent =

                new Intent(

                        HomeActivity.this,

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

}