package com.example.warevista;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.URLEncoder;


// =========================================================
// SUBSCRIPTION MANAGER
//
// USER-WISE SUBSCRIPTION MANAGEMENT
//
// FREE
// Purchase      = YES
// Sales         = YES
// History       = NO
// Live Stock    = NO
// Warehouse     = 1
//
// GOLD
// Purchase      = YES
// Sales         = YES
// History       = YES
// Live Stock    = NO
// Warehouse     = Purchased Limit
//
// PLATINUM
// Purchase      = YES
// Sales         = YES
// History       = YES
// Live Stock    = YES
// Warehouse     = Purchased Limit
// =========================================================

public class SubscriptionManager {


    // =========================================================
    // API URL
    // =========================================================

    public static final String BASE_URL =

            "https://script.google.com/macros/s/AKfycbxH_jxb8MUvSXeZQZAE9bbEGTojDKDoGD2-GMc3cWnn4EcI_fz42UCK4mTw2662HV14/exec";


    // =========================================================
    // SHARED PREFERENCES
    // =========================================================

    private static final String PREF_NAME =

            "WareVista";


    // =========================================================
    // PREFERENCE KEYS
    // =========================================================

    private static final String KEY_PLAN =

            "subscriptionPlan";


    private static final String KEY_SUBSCRIPTION =

            "subscription";


    private static final String KEY_WAREHOUSE_LIMIT =

            "warehouseLimit";


    private static final String KEY_PURCHASE_ACCESS =

            "accessPurchase";


    private static final String KEY_SALES_ACCESS =

            "accessSales";


    private static final String KEY_HISTORY_ACCESS =

            "accessHistory";


    private static final String KEY_LIVE_STOCK_ACCESS =

            "accessLiveStock";


    // =========================================================
    // CALLBACK
    // =========================================================

    public interface SubscriptionCallback {


        void onResult(

                boolean success,

                String plan,

                int warehouseLimit,

                boolean purchaseAccess,

                boolean salesAccess,

                boolean historyAccess,

                boolean liveStockAccess,

                String message

        );

    }


    // =========================================================
    // LOAD USER SUBSCRIPTION
    //
    // BACKEND FLOW
    //
    // userId
    //    ↓
    //
    // getUserSubscription
    //    ↓
    //
    // Google Sheet
    //    ↓
    //
    // Subscriptions
    //    ↓
    //
    // Load Plan + Features
    //    ↓
    //
    // Save SharedPreferences
    // =========================================================
    // =========================================================
// LOAD USER SUBSCRIPTION
//
// USER ID + WAREHOUSE
//
// IMPORTANT:
//
// ADMIN:
// Subscription belongs directly to ADMIN.
//
// STAFF:
// Subscription owner is found from Warehouse.
//
// This ensures all warehouses under the same ADMIN
// receive the same subscription features.
// =========================================================

    public static void loadUserSubscription(

            Context context,

            String userId,

            String warehouse,

            SubscriptionCallback callback

    ) {


        // =====================================================
        // USER ID VALIDATION
        // =====================================================

        if (

                userId == null

                        ||

                        userId.trim().isEmpty()

        ) {


            callback.onResult(

                    false,

                    "FREE",

                    1,

                    true,

                    true,

                    false,

                    false,

                    "User ID not found"

            );


            return;

        }


        try {


            // =================================================
            // ENCODE USER ID
            // =================================================

            String encodedUserId =

                    URLEncoder.encode(

                            userId.trim(),

                            "UTF-8"

                    );


            // =================================================
            // ENCODE WAREHOUSE
            // =================================================

            String encodedWarehouse =

                    URLEncoder.encode(

                            warehouse == null

                                    ? ""

                                    : warehouse.trim(),

                            "UTF-8"

                    );


            // =================================================
            // API URL
            //
            // userId
            // warehouse
            // =================================================

            String url =

                    BASE_URL

                            + "?module=getUserSubscription"

                            + "&userId="

                            + encodedUserId

                            + "&warehouse="

                            + encodedWarehouse;


            // =================================================
            // VOLLEY REQUEST
            // =================================================

            StringRequest request =

                    new StringRequest(

                            Request.Method.GET,

                            url,


                            // =====================================
                            // SUCCESS
                            // =====================================

                            response -> {


                                try {


                                    JSONObject object =

                                            new JSONObject(

                                                    response

                                            );


                                    // =================================
                                    // SUCCESS
                                    // =================================

                                    boolean success =

                                            object.optBoolean(

                                                    "success",

                                                    false

                                            );


                                    // =================================
                                    // BACKEND FAILED
                                    // =================================

                                    if (!success) {


                                        callback.onResult(

                                                false,

                                                getPlan(

                                                        context

                                                ),

                                                getWarehouseLimit(

                                                        context

                                                ),

                                                canAccessPurchase(

                                                        context

                                                ),

                                                canAccessSales(

                                                        context

                                                ),

                                                canAccessHistory(

                                                        context

                                                ),

                                                canAccessLiveStock(

                                                        context

                                                ),

                                                object.optString(

                                                        "message",

                                                        "Unable to load subscription"

                                                )

                                        );


                                        return;

                                    }


                                    // =================================
                                    // GET PLAN
                                    // =================================

                                    String plan =

                                            object.optString(

                                                            "plan",

                                                            "FREE"

                                                    )

                                                    .trim()

                                                    .toUpperCase();


                                    // =================================
                                    // PLAN VALIDATION
                                    // =================================

                                    if (

                                            !plan.equals("FREE")

                                                    &&

                                                    !plan.equals("GOLD")

                                                    &&

                                                    !plan.equals("PLATINUM")

                                    ) {


                                        plan = "FREE";

                                    }


                                    // =================================
                                    // GET WAREHOUSE LIMIT
                                    // =================================

                                    int warehouseLimit =

                                            object.optInt(

                                                    "warehouseLimit",

                                                    1

                                            );


                                    // =================================
                                    // FREE RULE
                                    // =================================

                                    if (plan.equals("FREE")) {

                                        warehouseLimit = 1;

                                    }


                                    // =================================
                                    // MINIMUM LIMIT
                                    // =================================

                                    if (warehouseLimit < 1) {

                                        warehouseLimit = 1;

                                    }


                                    // =================================
                                    // DEFAULT FEATURES
                                    // =================================

                                    boolean purchaseAccess = true;

                                    boolean salesAccess = true;

                                    boolean historyAccess = false;

                                    boolean liveStockAccess = false;


                                    // =================================
                                    // GET FEATURES OBJECT
                                    // =================================

                                    JSONObject features =

                                            object.optJSONObject(

                                                    "features"

                                            );


                                    // =================================
                                    // LOAD FEATURES FROM BACKEND
                                    // =================================

                                    if (features != null) {


                                        purchaseAccess =

                                                features.optBoolean(

                                                        "purchase",

                                                        true

                                                );


                                        salesAccess =

                                                features.optBoolean(

                                                        "sales",

                                                        true

                                                );


                                        historyAccess =

                                                features.optBoolean(

                                                        "history",

                                                        false

                                                );


                                        liveStockAccess =

                                                features.optBoolean(

                                                        "liveStock",

                                                        false

                                                );

                                    }


                                    // =================================
                                    // PLAN SAFETY RULES
                                    // =================================

                                    if (plan.equals("FREE")) {


                                        purchaseAccess = true;

                                        salesAccess = true;

                                        historyAccess = false;

                                        liveStockAccess = false;

                                        warehouseLimit = 1;

                                    }


                                    // =================================
                                    // GOLD
                                    // =================================

                                    else if (plan.equals("GOLD")) {


                                        purchaseAccess = true;

                                        salesAccess = true;

                                        historyAccess = true;

                                        liveStockAccess = false;

                                    }


                                    // =================================
                                    // PLATINUM
                                    // =================================

                                    else if (plan.equals("PLATINUM")) {


                                        purchaseAccess = true;

                                        salesAccess = true;

                                        historyAccess = true;

                                        liveStockAccess = true;

                                    }


                                    // =================================
                                    // SAVE LOCALLY
                                    // =================================

                                    saveSubscription(

                                            context,

                                            plan,

                                            warehouseLimit,

                                            purchaseAccess,

                                            salesAccess,

                                            historyAccess,

                                            liveStockAccess

                                    );


                                    // =================================
                                    // CALLBACK SUCCESS
                                    // =================================

                                    callback.onResult(

                                            true,

                                            plan,

                                            warehouseLimit,

                                            purchaseAccess,

                                            salesAccess,

                                            historyAccess,

                                            liveStockAccess,

                                            object.optString(

                                                    "message",

                                                    "Subscription loaded successfully"

                                            )

                                    );


                                }

                                catch (Exception e) {


                                    callback.onResult(

                                            false,

                                            getPlan(context),

                                            getWarehouseLimit(context),

                                            canAccessPurchase(context),

                                            canAccessSales(context),

                                            canAccessHistory(context),

                                            canAccessLiveStock(context),

                                            "Invalid subscription response"

                                    );

                                }

                            },


                            // =====================================
                            // ERROR
                            // =====================================

                            error -> {


                                callback.onResult(

                                        false,

                                        getPlan(context),

                                        getWarehouseLimit(context),

                                        canAccessPurchase(context),

                                        canAccessSales(context),

                                        canAccessHistory(context),

                                        canAccessLiveStock(context),

                                        "Unable to connect to subscription server"

                                );

                            }

                    );


            // =================================================
            // ADD REQUEST
            // =================================================

            Volley

                    .newRequestQueue(

                            context.getApplicationContext()

                    )

                    .add(

                            request

                    );


        }

        catch (Exception e) {


            callback.onResult(

                    false,

                    getPlan(context),

                    getWarehouseLimit(context),

                    canAccessPurchase(context),

                    canAccessSales(context),

                    canAccessHistory(context),

                    canAccessLiveStock(context),

                    "Unable to load subscription"

            );

        }

    }


    // =========================================================
    // SAVE SUBSCRIPTION
    //
    // Called after:
    //
    // 1. FREE Plan activation
    // 2. Razorpay payment success
    // 3. Backend subscription loading
    // =========================================================

    public static void saveSubscription(

            Context context,

            String plan,

            int warehouseLimit,

            boolean purchaseAccess,

            boolean salesAccess,

            boolean historyAccess,

            boolean liveStockAccess

    ) {


        // =====================================================
        // PLAN VALIDATION
        // =====================================================

        if (

                plan == null

                        ||

                        plan.trim().isEmpty()

        ) {


            plan = "FREE";

        }


        plan =

                plan

                        .trim()

                        .toUpperCase();


        // =====================================================
        // VALID PLAN
        // =====================================================

        if (

                !plan.equals("FREE")

                        &&

                        !plan.equals("GOLD")

                        &&

                        !plan.equals("PLATINUM")

        ) {


            plan = "FREE";

        }


        // =====================================================
        // WAREHOUSE LIMIT
        // =====================================================

        if (warehouseLimit < 1) {

            warehouseLimit = 1;

        }


        // =====================================================
        // FREE PLAN FORCE RULES
        // =====================================================

        if (plan.equals("FREE")) {


            warehouseLimit = 1;

            purchaseAccess = true;

            salesAccess = true;

            historyAccess = false;

            liveStockAccess = false;

        }


        // =====================================================
        // GOLD PLAN FORCE RULES
        // =====================================================

        else if (plan.equals("GOLD")) {


            purchaseAccess = true;

            salesAccess = true;

            historyAccess = true;

            liveStockAccess = false;

        }


        // =====================================================
        // PLATINUM PLAN FORCE RULES
        // =====================================================

        else if (plan.equals("PLATINUM")) {


            purchaseAccess = true;

            salesAccess = true;

            historyAccess = true;

            liveStockAccess = true;

        }


        // =====================================================
        // GET PREFERENCES
        // =====================================================

        SharedPreferences preferences =

                context

                        .getSharedPreferences(

                                PREF_NAME,

                                Context.MODE_PRIVATE

                        );


        // =====================================================
        // SAVE DATA
        // =====================================================

        preferences

                .edit()


                .putString(

                        KEY_PLAN,

                        plan

                )


                .putString(

                        KEY_SUBSCRIPTION,

                        plan

                )


                .putInt(

                        KEY_WAREHOUSE_LIMIT,

                        warehouseLimit

                )


                .putBoolean(

                        KEY_PURCHASE_ACCESS,

                        purchaseAccess

                )


                .putBoolean(

                        KEY_SALES_ACCESS,

                        salesAccess

                )


                .putBoolean(

                        KEY_HISTORY_ACCESS,

                        historyAccess

                )


                .putBoolean(

                        KEY_LIVE_STOCK_ACCESS,

                        liveStockAccess

                )


                .apply();

    }


    // =========================================================
    // GET PLAN
    // =========================================================

    public static String getPlan(

            Context context

    ) {


        String plan =

                context

                        .getSharedPreferences(

                                PREF_NAME,

                                Context.MODE_PRIVATE

                        )

                        .getString(

                                KEY_PLAN,

                                "FREE"

                        );


        if (plan == null) {

            return "FREE";

        }


        return

                plan

                        .trim()

                        .toUpperCase();

    }


    // =========================================================
    // GET WAREHOUSE LIMIT
    // =========================================================

    public static int getWarehouseLimit(

            Context context

    ) {


        int limit =

                context

                        .getSharedPreferences(

                                PREF_NAME,

                                Context.MODE_PRIVATE

                        )

                        .getInt(

                                KEY_WAREHOUSE_LIMIT,

                                1

                        );


        if (limit < 1) {

            limit = 1;

        }


        return limit;

    }


    // =========================================================
    // PURCHASE ACCESS
    // =========================================================

    public static boolean canAccessPurchase(

            Context context

    ) {


        return context

                .getSharedPreferences(

                        PREF_NAME,

                        Context.MODE_PRIVATE

                )

                .getBoolean(

                        KEY_PURCHASE_ACCESS,

                        true

                );

    }


    // =========================================================
    // SALES ACCESS
    // =========================================================

    public static boolean canAccessSales(

            Context context

    ) {


        return context

                .getSharedPreferences(

                        PREF_NAME,

                        Context.MODE_PRIVATE

                )

                .getBoolean(

                        KEY_SALES_ACCESS,

                        true

                );

    }


    // =========================================================
    // HISTORY ACCESS
    // =========================================================

    public static boolean canAccessHistory(

            Context context

    ) {


        return context

                .getSharedPreferences(

                        PREF_NAME,

                        Context.MODE_PRIVATE

                )

                .getBoolean(

                        KEY_HISTORY_ACCESS,

                        false

                );

    }


    // =========================================================
    // LIVE STOCK ACCESS
    // =========================================================

    public static boolean canAccessLiveStock(

            Context context

    ) {


        return context

                .getSharedPreferences(

                        PREF_NAME,

                        Context.MODE_PRIVATE

                )

                .getBoolean(

                        KEY_LIVE_STOCK_ACCESS,

                        false

                );

    }


    // =========================================================
    // CHECK IF PAID PLAN
    // =========================================================

    public static boolean isPaidPlan(

            Context context

    ) {


        String plan =

                getPlan(context);


        return

                plan.equals("GOLD")

                        ||

                        plan.equals("PLATINUM");

    }


    // =========================================================
    // CLEAR SUBSCRIPTION
    //
    // Logout વખતે ઉપયોગી
    // =========================================================

    public static void clearSubscription(

            Context context

    ) {


        context

                .getSharedPreferences(

                        PREF_NAME,

                        Context.MODE_PRIVATE

                )

                .edit()


                .remove(

                        KEY_PLAN

                )


                .remove(

                        KEY_SUBSCRIPTION

                )


                .remove(

                        KEY_WAREHOUSE_LIMIT

                )


                .remove(

                        KEY_PURCHASE_ACCESS

                )


                .remove(

                        KEY_SALES_ACCESS

                )


                .remove(

                        KEY_HISTORY_ACCESS

                )


                .remove(

                        KEY_LIVE_STOCK_ACCESS

                )


                .apply();

    }


}