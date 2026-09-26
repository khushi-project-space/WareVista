//package com.example.warevista;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.airbnb.lottie.LottieAnimationView;
//import com.android.volley.Request;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//
//import org.json.JSONObject;
//
//import java.net.URLEncoder;
//
//public class LoginActivity extends AppCompatActivity {
//
//    // ============================================================
//    // GOOGLE APPS SCRIPT URL
//    // ============================================================
//
//    private static final String BASE_URL =
//            "https://script.google.com/macros/s/AKfycbxH_jxb8MUvSXeZQZAE9bbEGTojDKDoGD2-GMc3cWnn4EcI_fz42UCK4mTw2662HV14/exec";
//
//
//    // ============================================================
//    // UI
//    // ============================================================
//
//    private EditText etUserId;
//    private EditText etPassword;
//
//    private Button btnLogin;
//
//    private LottieAnimationView lottieTruck;
//
//
//    // ============================================================
//    // SHARED PREFERENCES
//    // ============================================================
//
//    private SharedPreferences preferences;
//
//
//    // ============================================================
//    // ON CREATE
//    // ============================================================
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//
//        // --------------------------------------------------------
//        // Shared Preferences
//        // --------------------------------------------------------
//
//        preferences =
//                getSharedPreferences(
//                        "WareVista",
//                        MODE_PRIVATE
//                );
//
//
//        // --------------------------------------------------------
//        // CHECK LOGIN
//        // --------------------------------------------------------
//
//        if (preferences.getBoolean("isLoggedIn", false)) {
//
//            openCorrectDashboard();
//
//            return;
//        }
//
//
//        // --------------------------------------------------------
//        // LOGIN UI
//        // --------------------------------------------------------
//
//        setContentView(R.layout.activity_login);
//
//
//        // --------------------------------------------------------
//        // LOGIN ANIMATION
//        // --------------------------------------------------------
//
//        LottieAnimationView lottieLogin =
//                findViewById(R.id.lottieLogin);
//
//        if (lottieLogin != null) {
//            lottieLogin.playAnimation();
//        }
//
//
//        // --------------------------------------------------------
//        // TRUCK
//        // --------------------------------------------------------
//
//        lottieTruck =
//                findViewById(R.id.lottieTruck);
//
//        if (lottieTruck != null) {
//            startTruckAnimation();
//        }
//
//
//        // --------------------------------------------------------
//        // FIND VIEWS
//        // IMPORTANT: XML ID = etUserId
//        // --------------------------------------------------------
//
//        etUserId =
//                findViewById(R.id.etUserId);
//
//        etPassword =
//                findViewById(R.id.etPassword);
//
//        btnLogin =
//                findViewById(R.id.btnLogin);
//
//
//        // --------------------------------------------------------
//        // LOGIN BUTTON
//        // --------------------------------------------------------
//
//        btnLogin.setOnClickListener(v -> login());
//
//    }
//
//
//    // ============================================================
//    // LOGIN
//    // ============================================================
//
//    private void login() {
//
//        String userId =
//                etUserId
//                        .getText()
//                        .toString()
//                        .trim();
//
//        String password =
//                etPassword
//                        .getText()
//                        .toString()
//                        .trim();
//
//
//        // ========================================================
//        // VALIDATION
//        // ========================================================
//
//        if (userId.isEmpty()) {
//
//            etUserId.setError("Enter User ID");
//
//            etUserId.requestFocus();
//
//            return;
//        }
//
//
//        if (password.isEmpty()) {
//
//            etPassword.setError("Enter Password");
//
//            etPassword.requestFocus();
//
//            return;
//        }
//
//
//        // ========================================================
//        // DISABLE BUTTON
//        // ========================================================
//
//        btnLogin.setEnabled(false);
//
//        btnLogin.setText("Signing In...");
//
//
//        // ========================================================
//        // CREATE URL
//        // ========================================================
//
//        try {
//
//            String url =
//                    BASE_URL
//                            + "?module=login"
//                            + "&userId="
//                            + URLEncoder.encode(
//                            userId,
//                            "UTF-8"
//                    )
//                            + "&password="
//                            + URLEncoder.encode(
//                            password,
//                            "UTF-8"
//                    );
//
//
//            // ====================================================
//            // REQUEST
//            // ====================================================
//
//            StringRequest request =
//                    new StringRequest(
//
//                            Request.Method.GET,
//
//                            url,
//
//
//                            // ====================================
//                            // SUCCESS
//                            // ====================================
//
//                            response -> {
//
//                                btnLogin.setEnabled(true);
//
//                                btnLogin.setText("Sign In");
//
//
//                                try {
//
//                                    JSONObject object =
//                                            new JSONObject(response);
//
//
//                                    boolean success =
//                                            object.optBoolean(
//                                                    "success",
//                                                    false
//                                            );
//
//
//                                    // ==========================================
//                                    // LOGIN SUCCESS
//                                    // ==========================================
//                                    if (success) {
//
//                                        // ========================================================
//                                        // GET LOGIN DATA
//                                        // ========================================================
//
//                                        String loginUserId =
//                                                object.optString("userId", "").trim();
//
//                                        String loginWarehouse =
//                                                object.optString("warehouse", "").trim();
//
//                                        String loginRole =
//                                                object.optString("role", "STAFF").trim().toUpperCase();
//
//                                        String loginMobile =
//                                                object.optString("mobile", "").trim();
//
//
//                                        // ========================================================
//                                        // SAVE LOGIN DATA
//                                        // ========================================================
//
//                                        preferences
//                                                .edit()
//                                                .putBoolean("isLoggedIn", false)
//                                                .putString("userId", loginUserId)
//                                                .putString("warehouse", loginWarehouse)
//                                                .putString("role", loginRole)
//                                                .putString("mobile", loginMobile)
//                                                .apply();
//
//
//                                        // ========================================================
//                                        // OPEN BIOMETRIC VERIFICATION
//                                        // ========================================================
//
//                                        Intent intent =
//                                                new Intent(
//                                                        LoginActivity.this,
//                                                        BiometricVerificationActivity.class
//                                                );
//
//
//                                        intent.putExtra(
//                                                "userId",
//                                                loginUserId
//                                        );
//
//                                        intent.putExtra(
//                                                "warehouse",
//                                                loginWarehouse
//                                        );
//
//                                        intent.putExtra(
//                                                "role",
//                                                loginRole
//                                        );
//
//                                        intent.putExtra(
//                                                "mobile",
//                                                loginMobile
//                                        );
//
//
//                                        startActivity(intent);
//
//                                        finish();
//                                    }
//
//                                    // ==========================================
//                                    // LOGIN FAILED
//                                    // ==========================================
//
//                                    else {
//
//                                        String message =
//                                                object.optString(
//                                                        "message",
//                                                        "Invalid User ID or Password"
//                                                );
//
//
//                                        Toast.makeText(
//                                                LoginActivity.this,
//                                                message,
//                                                Toast.LENGTH_SHORT
//                                        ).show();
//
//                                    }
//
//
//                                } catch (Exception e) {
//
//                                    Toast.makeText(
//                                            LoginActivity.this,
//                                            "Invalid Server Response",
//                                            Toast.LENGTH_SHORT
//                                    ).show();
//
//                                    e.printStackTrace();
//
//                                }
//
//                            },
//
//
//                            // ====================================
//                            // ERROR
//                            // ====================================
//
//                            error -> {
//
//                                btnLogin.setEnabled(true);
//
//                                btnLogin.setText("Sign In");
//
//
//                                Toast.makeText(
//                                        LoginActivity.this,
//                                        "Connection Error",
//                                        Toast.LENGTH_SHORT
//                                ).show();
//
//                                error.printStackTrace();
//
//                            }
//
//                    );
//
//
//            // ====================================================
//            // ADD REQUEST
//            // ====================================================
//
//            Volley
//                    .newRequestQueue(this)
//                    .add(request);
//
//
//        } catch (Exception e) {
//
//            btnLogin.setEnabled(true);
//
//            btnLogin.setText("Sign In");
//
//            e.printStackTrace();
//
//        }
//
//    }
//
//
//    // ============================================================
//    // OPEN CORRECT DASHBOARD
//    // ============================================================
//
//    private void openCorrectDashboard() {
//
//        String role =
//                preferences.getString(
//                                "role",
//                                "STAFF"
//                        )
//                        .trim()
//                        .toUpperCase();
//
//
//        // ========================================================
//        // ADMIN
//        // ========================================================
//
//        if (role.equals("ADMIN")) {
//
//            Intent intent =
//                    new Intent(
//                            LoginActivity.this,
//                            AdminDashboardActivity.class
//                    );
//
//
//            intent.setFlags(
//                    Intent.FLAG_ACTIVITY_NEW_TASK |
//                            Intent.FLAG_ACTIVITY_CLEAR_TASK
//            );
//
//
//            startActivity(intent);
//
//            finish();
//
//            return;
//        }
//
//
//        // ========================================================
//        // STAFF
//        // ========================================================
//
//        Intent intent =
//                new Intent(
//                        LoginActivity.this,
//                        LoadingActivity.class
//                );
//
//
//        intent.setFlags(
//                Intent.FLAG_ACTIVITY_NEW_TASK |
//                        Intent.FLAG_ACTIVITY_CLEAR_TASK
//        );
//
//
//        startActivity(intent);
//
//        finish();
//
//    }
//
//
//    // ============================================================
//    // TRUCK ANIMATION
//    // ============================================================
//
//    private void startTruckAnimation() {
//
//        if (lottieTruck == null) {
//            return;
//        }
//
//
//        lottieTruck.post(() -> {
//
//            float screenWidth =
//                    lottieTruck
//                            .getRootView()
//                            .getWidth();
//
//
//            float truckWidth =
//                    lottieTruck
//                            .getWidth();
//
//
//            lottieTruck.setTranslationX(
//                    -truckWidth
//            );
//
//
//            lottieTruck
//                    .animate()
//                    .translationX(screenWidth)
//                    .setDuration(5000)
//                    .withEndAction(() -> {
//
//                        startTruckAnimation();
//
//                    })
//                    .start();
//
//        });
//
//    }
//
//
//    // ============================================================
//    // BACK BUTTON
//    // ============================================================
//
//    @Override
//    public void onBackPressed() {
//
//        super.onBackPressed();
//
//    }
//
//}
package com.example.warevista;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.URLEncoder;
import android.widget.AdapterView;
import android.widget.Spinner;


public class LoginActivity extends AppCompatActivity {


    // ============================================================
    // GOOGLE APPS SCRIPT URL
    // ============================================================

    private static final String BASE_URL =
            "https://script.google.com/macros/s/AKfycbxH_jxb8MUvSXeZQZAE9bbEGTojDKDoGD2-GMc3cWnn4EcI_fz42UCK4mTw2662HV14/exec";


    // ============================================================
    // UI
    // ============================================================

    private EditText etUserId;
    private EditText etPassword;

    private Button btnLogin;

    private LottieAnimationView lottieTruck;


    // ============================================================
    // SHARED PREFERENCES
    // ============================================================

    private SharedPreferences preferences;


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        preferences =
                getSharedPreferences(
                        "WareVista",
                        MODE_PRIVATE
                );


        // ========================================================
        // ALREADY LOGGED IN
        // ========================================================

        if (preferences.getBoolean("isLoggedIn", false)) {

            openCorrectDashboard();

            return;

        }
//        hal
        LanguageManager.applySavedLanguage(this);


        setContentView(
                R.layout.activity_login
        );

//        hal
        Spinner spinnerLanguage = findViewById(R.id.spinnerLanguage);

        String currentLanguage = LanguageManager.getLanguage(this);

        if (currentLanguage.equals("gu")) {
            spinnerLanguage.setSelection(1);
        } else if (currentLanguage.equals("hi")) {
            spinnerLanguage.setSelection(2);
        } else {
            spinnerLanguage.setSelection(0);
        }

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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

                String savedLanguage = LanguageManager.getLanguage(LoginActivity.this);

                if (!selectedLanguage.equals(savedLanguage)) {

                    LanguageManager.setLanguage(
                            LoginActivity.this,
                            selectedLanguage
                    );

                    recreate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        // ========================================================
        // LOGIN ANIMATION
        // ========================================================

        LottieAnimationView lottieLogin =
                findViewById(
                        R.id.lottieLogin
                );


        if (lottieLogin != null) {

            lottieLogin.playAnimation();

        }


        // ========================================================
        // TRUCK ANIMATION
        // ========================================================

        lottieTruck =
                findViewById(
                        R.id.lottieTruck
                );


        if (lottieTruck != null) {

            startTruckAnimation();

        }


        // ========================================================
        // FIND VIEWS
        // ========================================================

        etUserId =
                findViewById(
                        R.id.etUserId
                );


        etPassword =
                findViewById(
                        R.id.etPassword
                );


        btnLogin =
                findViewById(
                        R.id.btnLogin
                );


        // ========================================================
        // LOGIN BUTTON
        // ========================================================

        btnLogin.setOnClickListener(
                v -> login()
        );

    }


    // ============================================================
    // LOGIN
    // ============================================================

    private void login() {


        String userId =

                etUserId
                        .getText()
                        .toString()
                        .trim();


        String password =

                etPassword
                        .getText()
                        .toString()
                        .trim();


        // ========================================================
        // VALIDATION
        // ========================================================

        if (userId.isEmpty()) {

            etUserId.setError(
                    getString(R.string.error_enter_user_id)
            );

            etUserId.requestFocus();

            return;

        }


        if (password.isEmpty()) {

            etPassword.setError(
                    getString(R.string.error_enter_password)
            );

            etPassword.requestFocus();

            return;

        }


        // ========================================================
        // BUTTON LOADING
        // ========================================================

        btnLogin.setEnabled(false);

        btnLogin.setText(
                getString(R.string.signing_in)
        );


        try {


            String url =

                    BASE_URL

                            + "?module=login"

                            + "&userId="

                            + URLEncoder.encode(
                            userId,
                            "UTF-8"
                    )

                            + "&password="

                            + URLEncoder.encode(
                            password,
                            "UTF-8"
                    );


            StringRequest request =

                    new StringRequest(

                            Request.Method.GET,

                            url,


                            // ====================================
                            // LOGIN SUCCESS
                            // ====================================

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


                                    // ==================================
                                    // INVALID LOGIN
                                    // ==================================

                                    if (!success) {


                                        resetLoginButton();


                                        Toast.makeText(

                                                LoginActivity.this,

                                                object.optString(

                                                        "message",

                                                        getString(R.string.invalid_user_password)

                                                ),

                                                Toast.LENGTH_SHORT

                                        ).show();


                                        return;

                                    }


                                    // ==================================
                                    // LOGIN DATA
                                    // ==================================

                                    String loginUserId =

                                            object.optString(
                                                            "userId",
                                                            ""
                                                    )
                                                    .trim();


                                    String loginWarehouse =

                                            object.optString(
                                                            "warehouse",
                                                            ""
                                                    )
                                                    .trim();


                                    String loginRole =

                                            object.optString(
                                                            "role",
                                                            "STAFF"
                                                    )
                                                    .trim()
                                                    .toUpperCase();


                                    String loginMobile =

                                            object.optString(
                                                            "mobile",
                                                            ""
                                                    )
                                                    .trim();


                                    // ==================================
                                    // LOAD USER-WISE SUBSCRIPTION
                                    // ==================================

                                    loadSubscriptionAndContinue(

                                            loginUserId,

                                            loginWarehouse,

                                            loginRole,

                                            loginMobile

                                    );


                                }

                                catch (Exception e) {


                                    resetLoginButton();


                                    Toast.makeText(

                                            LoginActivity.this,

                                            getString(R.string.invalid_server_response),

                                            Toast.LENGTH_SHORT

                                    ).show();


                                    e.printStackTrace();

                                }

                            },


                            // ====================================
                            // LOGIN ERROR
                            // ====================================

                            error -> {


                                resetLoginButton();


                                Toast.makeText(

                                        LoginActivity.this,

                                        getString(R.string.connection_error),

                                        Toast.LENGTH_SHORT

                                ).show();


                                error.printStackTrace();

                            }

                    );


            Volley
                    .newRequestQueue(this)
                    .add(request);


        }

        catch (Exception e) {


            resetLoginButton();

            e.printStackTrace();

        }

    }


    // ============================================================
    // LOAD SUBSCRIPTION
    // ============================================================

    private void loadSubscriptionAndContinue(

            String userId,

            String warehouse,

            String role,

            String mobile

    ) {


        SubscriptionManager.loadUserSubscription(

                LoginActivity.this,

                userId,

                warehouse,


                new SubscriptionManager.SubscriptionCallback() {


                    @Override
                    public void onResult(

                            boolean success,

                            String plan,

                            int warehouseLimit,

                            boolean purchaseAccess,

                            boolean salesAccess,

                            boolean historyAccess,

                            boolean liveStockAccess,

                            String message

                    ) {


                        // ========================================
                        // SUBSCRIPTION LOAD FAILED
                        // ========================================

                        if (!success) {


                            // IMPORTANT:
                            // Login should not continue with wrong
                            // subscription data.

                            resetLoginButton();


                            Toast.makeText(

                                    LoginActivity.this,

                                    message,

                                    Toast.LENGTH_LONG

                            ).show();


                            return;

                        }


                        // ========================================
                        // SAVE USER SESSION
                        // ========================================

                        preferences
                                .edit()


                                .putBoolean(
                                        "isLoggedIn",
                                        false
                                )


                                // USER

                                .putString(
                                        "userId",
                                        userId
                                )

                                .putString(
                                        "warehouse",
                                        warehouse
                                )

                                .putString(
                                        "role",
                                        role
                                )

                                .putString(
                                        "mobile",
                                        mobile
                                )


                                // SUBSCRIPTION

                                .putString(
                                        "subscriptionPlan",
                                        plan
                                )

                                .putString(
                                        "subscription",
                                        plan
                                )

                                .putInt(
                                        "warehouseLimit",
                                        warehouseLimit
                                )


                                // FEATURES

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
                        // OPEN BIOMETRIC
                        // ========================================

                        Intent intent =

                                new Intent(

                                        LoginActivity.this,

                                        BiometricVerificationActivity.class

                                );


                        // USER

                        intent.putExtra(
                                "userId",
                                userId
                        );

                        intent.putExtra(
                                "warehouse",
                                warehouse
                        );

                        intent.putExtra(
                                "role",
                                role
                        );

                        intent.putExtra(
                                "mobile",
                                mobile
                        );


                        // SUBSCRIPTION

                        intent.putExtra(
                                "subscriptionPlan",
                                plan
                        );

                        intent.putExtra(
                                "subscription",
                                plan
                        );

                        intent.putExtra(
                                "warehouseLimit",
                                warehouseLimit
                        );


                        // FEATURES

                        intent.putExtra(
                                "accessPurchase",
                                purchaseAccess
                        );

                        intent.putExtra(
                                "accessSales",
                                salesAccess
                        );

                        intent.putExtra(
                                "accessHistory",
                                historyAccess
                        );

                        intent.putExtra(
                                "accessLiveStock",
                                liveStockAccess
                        );


                        startActivity(intent);

                        finish();

                    }

                }

        );

    }


    // ============================================================
    // RESET LOGIN BUTTON
    // ============================================================

    private void resetLoginButton() {


        if (btnLogin != null) {

            btnLogin.setEnabled(true);

            btnLogin.setText(
                    getString(R.string.sign_in)
            );

        }

    }


    // ============================================================
    // OPEN CORRECT DASHBOARD
    // ============================================================

    private void openCorrectDashboard() {


        String role =

                preferences.getString(

                                "role",

                                "STAFF"

                        )

                        .trim()
                        .toUpperCase();


        // ========================================================
        // ADMIN
        // ========================================================

        if (role.equals("ADMIN")) {


            Intent intent =

                    new Intent(

                            LoginActivity.this,

                            AdminDashboardActivity.class

                    );


            intent.setFlags(

                    Intent.FLAG_ACTIVITY_NEW_TASK

                            |

                            Intent.FLAG_ACTIVITY_CLEAR_TASK

            );


            startActivity(intent);

            finish();

            return;

        }


        // ========================================================
        // STAFF
        // ========================================================

        Intent intent =

                new Intent(

                        LoginActivity.this,

                        LoadingActivity.class

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
    // TRUCK ANIMATION
    // ============================================================

    private void startTruckAnimation() {


        if (lottieTruck == null) {

            return;

        }


        lottieTruck.post(() -> {


            float screenWidth =

                    lottieTruck

                            .getRootView()

                            .getWidth();


            float truckWidth =

                    lottieTruck

                            .getWidth();


            lottieTruck.setTranslationX(
                    -truckWidth
            );


            lottieTruck

                    .animate()

                    .translationX(
                            screenWidth
                    )

                    .setDuration(
                            5000
                    )

                    .withEndAction(
                            this::startTruckAnimation
                    )

                    .start();

        });

    }


    // ============================================================
    // BACK
    // ============================================================

    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }

}