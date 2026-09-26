package com.example.warevista;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import android.widget.ProgressBar;
import android.widget.ScrollView;

public class SubscriptionActivity extends AppCompatActivity {

    // ============================================================
    // API
    // ============================================================

    private static final String BASE_URL =
            "https://script.google.com/macros/s/AKfycbxH_jxb8MUvSXeZQZAE9bbEGTojDKDoGD2-GMc3cWnn4EcI_fz42UCK4mTw2662HV14/exec";


    // ============================================================
    // UI
    // ============================================================

    private ImageButton btnBack;

    private MaterialCardView cardFree;
    private MaterialCardView cardGold;
    private MaterialCardView cardPlatinum;

    private MaterialCardView cardWarehouseCount;
    private FrameLayout layoutSubscriptionLoading;
    private LinearLayout layoutWarehouseContent;
    private ScrollView subscriptionScrollView;

    private TextView txtWarehouseInfo;
    private TextView txtWarehouseCount;

    private MaterialButton btnMinus;
    private MaterialButton btnPlus;

    private RadioGroup radioDuration;

    private TextView txtAmount;
    private TextView txtPriceInfo;

    private MaterialButton btnSubscribe;
    private ProgressBar progressSubscription;


    // ============================================================
    // SELECTED DATA
    // ============================================================

    private String selectedPlan = "";
    private String selectedDuration = "Monthly";

    /*
     * FIRST-TIME:
     *
     * warehouseCount = total selected warehouses
     *
     *
     * SECOND-TIME GOLD / PLATINUM:
     *
     * warehouseCount = NEW warehouses being added
     */
    private int warehouseCount = 1;


    // ============================================================
    // EXISTING SUBSCRIPTION DATA
    // ============================================================

    private boolean additionalPurchase = false;

    private int currentAppliedLimit = 0;

    private int existingWarehouseCount = 0;

    private int newAppliedLimit = 0;

    private String currentPlan = "";

    private boolean subscriptionLoaded = false;


    // ============================================================
    // GOLD MONTHLY PRICING
    // ============================================================

    private final int[] GOLD_MONTHLY_PRICES = {

            99,
            179,
            249,
            319,
            389,
            459,
            529

    };


    // ============================================================
    // GOLD YEARLY PRICING
    // ============================================================

    private final int[] GOLD_YEARLY_PRICES = {

            999,
            1799,
            2499,
            3199,
            3899,
            4599,
            5299

    };


    // ============================================================
    // PLATINUM PRICING
    //
    // Monthly:
    // ₹399 + (Warehouse Count × ₹80)
    //
    // Yearly:
    // (Monthly × 10) + 9
    //
    // IMPORTANT:
    //
    // First-time Platinum:
    // warehouseCount = total warehouses
    //
    // Additional Platinum:
    // warehouseCount = NEW warehouses only
    // ============================================================

    private static final int PLATINUM_BASE_PRICE = 399;

    private static final int PLATINUM_PRICE_PER_WAREHOUSE = 80;


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_subscription);


        // ========================================================
        // FIND VIEWS
        // ========================================================

        btnBack = findViewById(R.id.btnBack);

        cardFree = findViewById(R.id.cardFree);

        cardGold = findViewById(R.id.cardGold);

        cardPlatinum = findViewById(R.id.cardPlatinum);

        cardWarehouseCount =
                findViewById(R.id.cardWarehouseCount);
        subscriptionScrollView =
                findViewById(R.id.subscriptionScrollView);

        layoutSubscriptionLoading =
                findViewById(R.id.layoutSubscriptionLoading);

        layoutWarehouseContent =
                findViewById(R.id.layoutWarehouseContent);

        txtWarehouseInfo =
                findViewById(R.id.txtWarehouseInfo);

        txtWarehouseCount =
                findViewById(R.id.txtWarehouseCount);

        btnMinus =
                findViewById(R.id.btnMinus);

        btnPlus =
                findViewById(R.id.btnPlus);

        radioDuration =
                findViewById(R.id.radioDuration);

        txtAmount =
                findViewById(R.id.txtAmount);

        txtPriceInfo =
                findViewById(R.id.txtPriceInfo);

        btnSubscribe =
                findViewById(R.id.btnSubscribe);
        progressSubscription =
                findViewById(R.id.progressSubscription);


        // ========================================================
        // INITIAL VALUES
        // ========================================================

        warehouseCount = 1;

        selectedPlan = "";

        selectedDuration = "Monthly";

        additionalPurchase = false;

        currentAppliedLimit = 0;

        existingWarehouseCount = 0;

        newAppliedLimit = 0;


        txtWarehouseCount.setText(
                String.valueOf(warehouseCount)
        );


        // ========================================================
        // LOAD CURRENT SUBSCRIPTION
        // ========================================================
        loadCurrentSubscription();

        // ========================================================
        // BACK
        // ========================================================

        btnBack.setOnClickListener(v -> finish());


        // ========================================================
        // FREE PLAN
        // ========================================================

//        cardFree.setOnClickListener(v -> {
//
//            selectedPlan = "Free";
//
//            /*
//             * FREE is never an additional purchase.
//             */
//
//            additionalPurchase = false;
//
//            warehouseCount = 1;
//
//            newAppliedLimit = 1;
//
//            cardWarehouseCount.setVisibility(
//                    View.GONE
//            );
//
//            updatePlanSelection();
//
//            updatePrice();
//
//        });
//

        // ========================================================
        // GOLD PLAN
        // ========================================================
        cardGold.setOnClickListener(v -> {

            animatePlanCard(cardGold);

            selectedPlan = "Gold";

            // Immediate UI response
            additionalPurchase = false;
            warehouseCount = 1;
            newAppliedLimit = 1;

            updatePlanSelection();
            updateWarehouseUI();
            updatePrice();
            showWarehouseLoading();

            // If API is already loaded, apply actual subscription data
            if (subscriptionLoaded) {

                if (
                        currentPlan.equals("GOLD")
                                &&
                                currentAppliedLimit > 0
                ) {

                    additionalPurchase = true;
                    warehouseCount = 1;
                    newAppliedLimit =
                            currentAppliedLimit + warehouseCount;

                    updateWarehouseUI();
                    updatePrice();
                }
            }

            new android.os.Handler().postDelayed(() -> {

                updateWarehouseUI();
                showWarehouseContent();

            }, 800);


            // ====================================================
            // EXISTING GOLD
            // SECOND-TIME GOLD = ADDITIONAL PURCHASE
            // ====================================================

            if (
                    currentPlan.equals("GOLD")
                            &&
                            currentAppliedLimit > 0
            ) {

                additionalPurchase = true;


                /*
                 * IMPORTANT:
                 *
                 * warehouseCount = NEW warehouses only.
                 *
                 * Existing Gold = 3
                 * Select = 1
                 * Final = 4
                 */

                warehouseCount = 1;

                newAppliedLimit =
                        currentAppliedLimit
                                + warehouseCount;


                showWarehouseLoading();

                updatePlanSelection();

                updatePrice();

                new android.os.Handler().postDelayed(() -> {

                    updateWarehouseUI();

                    showWarehouseContent();

                }, 800);

                return;
            }


            // ====================================================
            // FIRST-TIME GOLD
            // ====================================================

            additionalPurchase = false;

            warehouseCount = 1;

            newAppliedLimit = 1;

            showWarehouseLoading();

            updatePlanSelection();

            updatePrice();

            new android.os.Handler().postDelayed(() -> {

                updateWarehouseUI();

                showWarehouseContent();

            }, 800);

        });


        // ========================================================
        // PLATINUM PLAN
        // ========================================================
        cardPlatinum.setOnClickListener(v -> {

            animatePlanCard(cardPlatinum);

            selectedPlan = "Platinum";

            // Immediate UI response
            additionalPurchase = false;

            warehouseCount =
                    Math.max(
                            1,
                            existingWarehouseCount
                    );

            newAppliedLimit = warehouseCount;

            updatePlanSelection();
            updateWarehouseUI();
            updatePrice();
            showWarehouseLoading();

            // If API is already loaded, apply actual subscription data
            if (subscriptionLoaded) {

                if (
                        currentPlan.equals("PLATINUM")
                                &&
                                currentAppliedLimit > 0
                ) {

                    additionalPurchase = true;

                    warehouseCount = 1;

                    newAppliedLimit =
                            currentAppliedLimit + warehouseCount;

                    updateWarehouseUI();
                    updatePrice();
                }
            }

            new android.os.Handler().postDelayed(() -> {

                updateWarehouseUI();
                showWarehouseContent();

            }, 800);

            // ====================================================
            // EXISTING PLATINUM
            // SECOND-TIME ADDITIONAL PLATINUM
            // ====================================================

            if (
                    currentPlan.equals("PLATINUM")
                            &&
                            currentAppliedLimit > 0
            ) {

                additionalPurchase = true;

                /*
                 * warehouseCount = NEW warehouses only.
                 */

                warehouseCount = 1;

                newAppliedLimit =
                        currentAppliedLimit
                                + warehouseCount;



                updatePlanSelection();

                updatePrice();

                new android.os.Handler().postDelayed(() -> {

                    updateWarehouseUI();

                    showWarehouseContent();

                }, 800);

                return;
            }


            // ========================================================
// FIRST-TIME PLATINUM
// ========================================================

            additionalPurchase = false;

            /*
             * First-time Platinum:
             *
             * Existing physical warehouses must be included.
             *
             * Example:
             *
             * Existing = 1 → minimum 1
             * Existing = 3 → minimum 3
             * Existing = 5 → minimum 5
             *
             * warehouseCount = TOTAL Platinum warehouse limit
             */

            warehouseCount =
                    Math.max(
                            1,
                            existingWarehouseCount
                    );

            newAppliedLimit =
                    warehouseCount;

            showWarehouseLoading();

            updatePlanSelection();

            updatePrice();

            new android.os.Handler().postDelayed(() -> {

                updateWarehouseUI();

                showWarehouseContent();

            }, 800);

        });


        btnMinus.setOnClickListener(v -> {

            int minimumWarehouseCount = 1;


            // ========================================================
            // FIRST-TIME PLATINUM
            // ========================================================

            if (
                    selectedPlan.equals("Platinum")
                            &&
                            !additionalPurchase
            ) {

                /*
                 * Existing warehouses must always be included.
                 */

                minimumWarehouseCount =
                        Math.max(
                                1,
                                existingWarehouseCount
                        );
            }


            // ========================================================
            // DECREASE
            // ========================================================

            if (warehouseCount > minimumWarehouseCount) {

                warehouseCount--;

                updateWarehouseUI();

                updatePrice();

            }

            else {

                if (
                        selectedPlan.equals("Platinum")
                                &&
                                !additionalPurchase
                ) {

                    Toast.makeText(
                            this,
                            "Platinum must include all existing warehouses",
                            Toast.LENGTH_SHORT
                    ).show();

                }

            }

        });


        // ========================================================
        // PLUS BUTTON
        // ========================================================

        btnPlus.setOnClickListener(v -> {


            // ====================================================
            // GOLD
            // ====================================================

            if (selectedPlan.equals("Gold")) {


                // ==================================================
                // ADDITIONAL GOLD
                // ==================================================

                if (additionalPurchase) {

                    /*
                     * Gold maximum TOTAL applied limit = 7.
                     *
                     * Example:
                     *
                     * Current Limit = 3
                     * New = 1 → Total = 4
                     *
                     * Current Limit = 3
                     * New = 4 → Total = 7
                     */

                    int maximumAdditional =
                            7 - currentAppliedLimit;


                    if (maximumAdditional <= 0) {

                        Toast.makeText(
                                this,
                                "Gold plan maximum limit of 7 warehouses is already reached",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;

                    }


                    if (
                            warehouseCount
                                    <
                                    maximumAdditional
                    ) {

                        warehouseCount++;

                        newAppliedLimit =
                                currentAppliedLimit
                                        + warehouseCount;

                        updateWarehouseUI();

                        updatePrice();

                    }

                    else {

                        Toast.makeText(
                                this,
                                "Gold plan maximum applied limit is 7 warehouses",
                                Toast.LENGTH_SHORT
                        ).show();

                    }

                    return;
                }


                // ==================================================
                // FIRST-TIME GOLD
                // ==================================================

                if (warehouseCount < 7) {

                    warehouseCount++;

                    newAppliedLimit =
                            warehouseCount;

                    updateWarehouseUI();

                    updatePrice();

                }

                else {

                    Toast.makeText(
                            this,
                            "Gold plan supports maximum 7 warehouses",
                            Toast.LENGTH_SHORT
                    ).show();

                }

                return;
            }


            // ====================================================
            // PLATINUM
            // ====================================================

            if (selectedPlan.equals("Platinum")) {

                /*
                 * Platinum has no fixed maximum here.
                 *
                 * First-time:
                 * warehouseCount = total warehouses
                 *
                 * Additional:
                 * warehouseCount = NEW warehouses
                 */

                warehouseCount++;

                if (additionalPurchase) {

                    newAppliedLimit =
                            currentAppliedLimit
                                    + warehouseCount;

                }

                else {

                    newAppliedLimit =
                            warehouseCount;

                }

                updateWarehouseUI();

                updatePrice();

            }

        });


        // ========================================================
        // DURATION
        // ========================================================

        radioDuration.setOnCheckedChangeListener(
                (group, checkedId) -> {

                    if (checkedId == R.id.rbMonthly) {

                        selectedDuration = "Monthly";

                    }

                    else if (checkedId == R.id.rbYearly) {

                        selectedDuration = "Yearly";

                    }

                    updatePrice();

                }
        );


        // ========================================================
        // CONTINUE
        // ========================================================

        btnSubscribe.setOnClickListener(v ->
                continueSubscription()
        );


        // ========================================================
        // INITIAL PRICE
        // ========================================================

        updatePrice();

    }


    private void showWarehouseLoading() {

        cardWarehouseCount.setVisibility(View.VISIBLE);

        layoutSubscriptionLoading.setVisibility(View.VISIBLE);

        layoutWarehouseContent.setVisibility(View.GONE);

        cardWarehouseCount.requestLayout();

        subscriptionScrollView.post(() -> {

            subscriptionScrollView.fullScroll(
                    View.FOCUS_DOWN
            );

        });
    }

    private void showWarehouseContent() {

        layoutSubscriptionLoading.setVisibility(View.GONE);

        layoutWarehouseContent.setVisibility(View.VISIBLE);

    }
    // ============================================================
    // UPDATE WAREHOUSE UI
    // ============================================================

    private void updateWarehouseUI() {

        txtWarehouseCount.setText(
                String.valueOf(warehouseCount)
        );


        // ========================================================
        // GOLD
        // ========================================================

        if (selectedPlan.equals("Gold")) {


            // ====================================================
            // ADDITIONAL GOLD
            // ====================================================

            if (additionalPurchase) {

                newAppliedLimit =
                        currentAppliedLimit
                                + warehouseCount;


                /*
                 * Show current subscription information.
                 *
                 * The counter itself represents NEW warehouses.
                 */

                txtWarehouseInfo.setText(

                        "Current Gold Subscription"
                                + "\nApplied Warehouse Limit: "
                                + currentAppliedLimit
                                + "\nExisting Warehouses: "
                                + existingWarehouseCount
                                + "\n\nSelect number of NEW warehouses"

                );

            }


            // ====================================================
            // FIRST-TIME GOLD
            // ====================================================

            else {

                newAppliedLimit =
                        warehouseCount;


                txtWarehouseInfo.setText(

                        "Selected: "
                                + warehouseCount
                                + " of maximum 7 warehouses"

                );

            }

            return;
        }


        // ========================================================
        // PLATINUM
        // ========================================================

        if (selectedPlan.equals("Platinum")) {


            // ====================================================
            // ADDITIONAL PLATINUM
            // ====================================================

            if (additionalPurchase) {

                newAppliedLimit =
                        currentAppliedLimit
                                + warehouseCount;


                txtWarehouseInfo.setText(

                        "Current Platinum Subscription"
                                + "\nApplied Warehouse Limit: "
                                + currentAppliedLimit
                                + "\nExisting Warehouses: "
                                + existingWarehouseCount
                                + "\n\nSelect number of NEW warehouses"

                );

            }


            // ====================================================
            // FIRST-TIME PLATINUM
            // ====================================================

            else {

                newAppliedLimit =
                        warehouseCount;


                txtWarehouseInfo.setText(

                        "Existing Warehouses: "
                                + existingWarehouseCount
                                + "\nMinimum Platinum Limit: "
                                + Math.max(
                                1,
                                existingWarehouseCount
                        )
                                + "\n\nSelected Platinum Limit: "
                                + warehouseCount
                                + " warehouse(s)"

                );

            }

        }

    }


    // ============================================================
    // GET SUBSCRIPTION AMOUNT
    // ============================================================

    private int getSubscriptionAmount() {


        // ========================================================
        // FREE
        // ========================================================

        if (selectedPlan.equals("Free")) {

            return 0;

        }


        // ========================================================
        // GOLD MONTHLY
        // ========================================================

        if (
                selectedPlan.equals("Gold")
                        &&
                        selectedDuration.equals("Monthly")
        ) {

            return GOLD_MONTHLY_PRICES[
                    warehouseCount - 1
                    ];

        }


        // ========================================================
        // GOLD YEARLY
        // ========================================================

        if (
                selectedPlan.equals("Gold")
                        &&
                        selectedDuration.equals("Yearly")
        ) {

            return GOLD_YEARLY_PRICES[
                    warehouseCount - 1
                    ];

        }


        // ========================================================
        // PLATINUM MONTHLY
        // ========================================================

        if (
                selectedPlan.equals("Platinum")
                        &&
                        selectedDuration.equals("Monthly")
        ) {

            return calculatePlatinumMonthlyPrice();

        }


        // ========================================================
        // PLATINUM YEARLY
        // ========================================================

        if (
                selectedPlan.equals("Platinum")
                        &&
                        selectedDuration.equals("Yearly")
        ) {

            return (
                    calculatePlatinumMonthlyPrice()
                            * 10
            ) + 9;

        }


        return 0;

    }


    // ============================================================
    // CALCULATE PLATINUM MONTHLY PRICE
    // ============================================================

    private int calculatePlatinumMonthlyPrice() {

        /*
         * First-time Platinum:
         *
         * warehouseCount = total selected warehouses
         *
         *
         * Additional Platinum:
         *
         * warehouseCount = NEW warehouses only
         *
         * Therefore payment is calculated only for
         * the NEW warehouses during additional purchase.
         */

        return PLATINUM_BASE_PRICE
                +
                (
                        (warehouseCount - 1)
                                *
                                PLATINUM_PRICE_PER_WAREHOUSE
                );

    }


    // ============================================================
    // UPDATE PRICE
    // ============================================================

    private void updatePrice() {


        // ========================================================
        // NO PLAN
        // ========================================================

        if (selectedPlan.isEmpty()) {

            txtAmount.setText("₹0");

            txtPriceInfo.setText(
                    "Select a subscription plan"
            );

            return;

        }


        // ========================================================
        // GET AMOUNT
        // ========================================================

        int amount =
                getSubscriptionAmount();


        // ========================================================
        // DISPLAY AMOUNT
        // ========================================================

        txtAmount.setText(
                "₹" + amount
        );


        // ========================================================
        // FREE
        // ========================================================

        if (selectedPlan.equals("Free")) {

            txtPriceInfo.setText(
                    "Free • 1 Warehouse"
            );

            return;

        }


        // ========================================================
        // ADDITIONAL GOLD
        // ========================================================

        if (
                selectedPlan.equals("Gold")
                        &&
                        additionalPurchase
        ) {

            txtPriceInfo.setText(

                    "Gold • "
                            + selectedDuration
                            + " • "
                            + warehouseCount
                            + " New Warehouse(s)"

            );

            return;

        }


        // ========================================================
        // ADDITIONAL PLATINUM
        // ========================================================

        if (
                selectedPlan.equals("Platinum")
                        &&
                        additionalPurchase
        ) {

            txtPriceInfo.setText(

                    "Platinum • "
                            + selectedDuration
                            + " • "
                            + warehouseCount
                            + " New Warehouse(s)"

            );

            return;

        }


        // ========================================================
        // NORMAL GOLD / PLATINUM
        // ========================================================

        txtPriceInfo.setText(

                selectedPlan
                        + " • "
                        + selectedDuration
                        + " • "
                        + warehouseCount
                        + " Warehouse(s)"

        );

    }


    // ============================================================
    // UPDATE PLAN SELECTION UI
    // ============================================================

    private void updatePlanSelection() {

        cardFree.setStrokeWidth(1);

        cardGold.setStrokeWidth(2);

        cardPlatinum.setStrokeWidth(2);


        // ========================================================
        // FREE
        // ========================================================

        if (selectedPlan.equals("Free")) {

            cardFree.setStrokeWidth(4);

        }


        // ========================================================
        // GOLD
        // ========================================================

        else if (selectedPlan.equals("Gold")) {

            cardGold.setStrokeWidth(4);

        }


        // ========================================================
        // PLATINUM
        // ========================================================

        else if (selectedPlan.equals("Platinum")) {

            cardPlatinum.setStrokeWidth(4);

        }

    }


    // ============================================================
    // CONTINUE SUBSCRIPTION
    // ============================================================

    private void continueSubscription() {


        // ========================================================
        // PLAN VALIDATION
        // ========================================================

        if (selectedPlan.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please select a subscription plan",
                    Toast.LENGTH_SHORT
            ).show();

            return;

        }


        // ========================================================
        // FREE PLAN
        // ========================================================

//        if (selectedPlan.equals("Free")) {
//
//            Toast.makeText(
//                    this,
//                    "Free plan selected",
//                    Toast.LENGTH_SHORT
//            ).show();
//
//
//            Intent intent =
//                    new Intent(
//                            SubscriptionActivity.this,
//                            SubscriptionPaymentActivity.class
//                    );
//
//
//            intent.putExtra(
//                    "plan",
//                    "Free"
//            );
//
//
//            intent.putExtra(
//                    "duration",
//                    "Free"
//            );
//
//
//            intent.putExtra(
//                    "warehouseCount",
//                    1
//            );
//
//
//            intent.putExtra(
//                    "amount",
//                    0
//            );
//
//
//            intent.putExtra(
//                    "additionalPurchase",
//                    false
//            );
//
//
//            intent.putExtra(
//                    "additionalWarehouses",
//                    0
//            );
//
//
//            intent.putExtra(
//                    "newAppliedLimit",
//                    1
//            );
//
//
//            startActivity(intent);
//
//            return;
//
//        }


        // ========================================================
        // PAID PLAN AMOUNT
        // ========================================================

        int totalAmount =
                getSubscriptionAmount();


        if (totalAmount <= 0) {

            Toast.makeText(
                    this,
                    "Invalid subscription amount",
                    Toast.LENGTH_SHORT
            ).show();

            return;

        }


        // ========================================================
        // ADDITIONAL GOLD VALIDATION
        // ========================================================

        if (
                selectedPlan.equals("Gold")
                        &&
                        additionalPurchase
        ) {

            if (currentAppliedLimit >= 7) {

                Toast.makeText(
                        this,
                        "Gold plan maximum limit of 7 warehouses is already reached",
                        Toast.LENGTH_SHORT
                ).show();

                return;

            }


            if (
                    warehouseCount < 1
                            ||
                            currentAppliedLimit + warehouseCount > 7
            ) {

                Toast.makeText(
                        this,
                        "Selected new warehouses exceed Gold maximum limit of 7",
                        Toast.LENGTH_SHORT
                ).show();

                return;

            }


            newAppliedLimit =
                    currentAppliedLimit
                            + warehouseCount;

        }


        // ========================================================
// FIRST-TIME PLATINUM VALIDATION
// ========================================================

        if (
                selectedPlan.equals("Platinum")
                        &&
                        !additionalPurchase
        ) {

            int minimumWarehouseCount =
                    Math.max(
                            1,
                            existingWarehouseCount
                    );


            if (
                    warehouseCount
                            <
                            minimumWarehouseCount
            ) {

                Toast.makeText(
                        this,
                        "Platinum must include all existing warehouses",
                        Toast.LENGTH_SHORT
                ).show();

                return;

            }


            newAppliedLimit =
                    warehouseCount;

        }

        // ========================================================
        // OPEN PAYMENT ACTIVITY
        // ========================================================

        Intent intent =
                new Intent(
                        SubscriptionActivity.this,
                        SubscriptionPaymentActivity.class
                );


        // ========================================================
        // BASIC SUBSCRIPTION DATA
        // ========================================================

        intent.putExtra(
                "plan",
                selectedPlan
        );


        intent.putExtra(
                "duration",
                selectedDuration
        );


        /*
         * IMPORTANT:
         *
         * First-time:
         * warehouseCount = TOTAL selected warehouses
         *
         * Additional:
         * warehouseCount = NEW warehouses only
         */

        intent.putExtra(
                "warehouseCount",
                warehouseCount
        );


        intent.putExtra(
                "amount",
                totalAmount
        );


        // ========================================================
        // ADDITIONAL PURCHASE
        // ========================================================

        intent.putExtra(
                "additionalPurchase",
                additionalPurchase
        );


        intent.putExtra(
                "additionalWarehouses",
                additionalPurchase
                        ? warehouseCount
                        : 0
        );


        intent.putExtra(
                "newAppliedLimit",
                additionalPurchase
                        ? newAppliedLimit
                        : warehouseCount
        );


        startActivity(intent);

    }


    // ============================================================
    // LOAD CURRENT SUBSCRIPTION
    // ============================================================

    private void loadCurrentSubscription() {

        SharedPreferences preferences =
                getSharedPreferences(
                        "WareVista",
                        MODE_PRIVATE
                );


        String userId =
                preferences.getString(
                        "userId",
                        ""
                );


        // ========================================================
        // USER ID VALIDATION
        // ========================================================

        if (userId.isEmpty()) {

            subscriptionLoaded = true;

            currentPlan = "";

            currentAppliedLimit = 0;

            existingWarehouseCount = 0;

            return;

        }


        // ========================================================
        // API REQUEST
        // ========================================================

        StringRequest request =
                new StringRequest(

                        Request.Method.POST,

                        BASE_URL,

                        response -> {

                            try {

                                JSONObject json =
                                        new JSONObject(
                                                response
                                        );


                                if (
                                        json.optBoolean(
                                                "success",
                                                false
                                        )
                                ) {

                                    // ==================================
                                    // CURRENT PLAN
                                    // ==================================

                                    currentPlan =
                                            json.optString(
                                                    "plan",
                                                    ""
                                            ).toUpperCase();


                                    // ==================================
                                    // CURRENT APPLIED LIMIT
                                    // ==================================

                                    currentAppliedLimit =
                                            json.optInt(
                                                    "warehouseLimit",
                                                    0
                                            );


                                    // ==================================
                                    // EXISTING WAREHOUSE COUNT
                                    // ==================================

                                    existingWarehouseCount =
                                            json.optInt(
                                                    "currentWarehouseCount",
                                                    0
                                            );

                                }

                                else {

                                    currentPlan = "";

                                    currentAppliedLimit = 0;

                                    existingWarehouseCount = 0;

                                }

                            }

                            catch (Exception e) {

                                currentPlan = "";

                                currentAppliedLimit = 0;

                                existingWarehouseCount = 0;

                            }


                            subscriptionLoaded = true;

                            cardFree.setEnabled(true);
                            cardGold.setEnabled(true);
                            cardPlatinum.setEnabled(true);

                        },


                        error -> {

                            currentPlan = "";

                            currentAppliedLimit = 0;

                            existingWarehouseCount = 0;

                            subscriptionLoaded = true;

                            cardFree.setEnabled(true);
                            cardGold.setEnabled(true);
                            cardPlatinum.setEnabled(true);

                            Toast.makeText(
                                    SubscriptionActivity.this,
                                    "Unable to load current subscription",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }

                ) {

                    @Override
                    protected Map<String, String> getParams() {

                        Map<String, String> params =
                                new HashMap<>();


                        params.put(
                                "module",
                                "getUserSubscriptionV2"
                        );


                        params.put(
                                "userId",
                                userId
                        );


                        return params;

                    }

                };


        // ========================================================
        // ADD REQUEST TO QUEUE
        // ========================================================

        RequestQueue queue =
                Volley.newRequestQueue(this);

        queue.add(request);

    }

    private void animatePlanCard(MaterialCardView card) {

        card.animate()
                .scaleX(0.96f)
                .scaleY(0.96f)
                .setDuration(100)
                .withEndAction(() -> {
                    card.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .start();
                })
                .start();
    }


    // ============================================================
    // BACK PRESSED
    // ============================================================

    @Override
    public void onBackPressed() {

        finish();

    }

}