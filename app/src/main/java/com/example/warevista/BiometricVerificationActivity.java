//package com.example.warevista;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.biometric.BiometricManager;
//import androidx.biometric.BiometricPrompt;
//import androidx.core.content.ContextCompat;
//
//import com.google.android.material.button.MaterialButton;
//
//import java.util.concurrent.Executor;
//import android.content.SharedPreferences;
//public class BiometricVerificationActivity
//        extends AppCompatActivity {
//
//
//    // ============================================================
//    // UI
//    // ============================================================
//
//    private MaterialButton btnVerifyBiometric;
//
//    private TextView txtBiometricMessage;
//
//    private TextView txtCancel;
//
//
//    // ============================================================
//    // LOGIN DATA
//    // ============================================================
//
//    private String userId;
//
//    private String warehouse;
//
//    private String role;
//
//    private String mobile;
//
//
//    // ============================================================
//    // BIOMETRIC
//    // ============================================================
//
//    private Executor executor;
//
//    private BiometricPrompt biometricPrompt;
//
//    private BiometricPrompt.PromptInfo promptInfo;
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
//        setContentView(
//                R.layout.activity_biometric_verification
//        );
//
//
//        // ========================================================
//        // FIND VIEWS
//        // ========================================================
//
//        btnVerifyBiometric =
//                findViewById(
//                        R.id.btnVerifyBiometric
//                );
//
//        txtBiometricMessage =
//                findViewById(
//                        R.id.txtBiometricMessage
//                );
//
//        txtCancel =
//                findViewById(
//                        R.id.txtCancel
//                );
//
//
//        // ========================================================
//        // GET LOGIN DATA
//        // ========================================================
//
//        userId =
//                getIntent()
//                        .getStringExtra("userId");
//
//        warehouse =
//                getIntent()
//                        .getStringExtra("warehouse");
//
//        role =
//                getIntent()
//                        .getStringExtra("role");
//
//        mobile =
//                getIntent()
//                        .getStringExtra("mobile");
//
//
//        // ========================================================
//        // CHECK BIOMETRIC
//        // ========================================================
//
//        setupBiometric();
//
//
//        // ========================================================
//        // VERIFY BUTTON
//        // ========================================================
//
//        btnVerifyBiometric.setOnClickListener(
//                v -> authenticate()
//        );
//
//
//        // ========================================================
//        // CANCEL
//        // ========================================================
//
//        txtCancel.setOnClickListener(v -> {
//
//            getSharedPreferences(
//                    "WareVista",
//                    MODE_PRIVATE
//            )
//                    .edit()
//                    .clear()
//                    .apply();
//
//
//            Intent intent = new Intent(
//                    BiometricVerificationActivity.this,
//                    LoginActivity.class
//            );
//
//            intent.setFlags(
//                    Intent.FLAG_ACTIVITY_NEW_TASK |
//                            Intent.FLAG_ACTIVITY_CLEAR_TASK
//            );
//
//            startActivity(intent);
//
//            finish();
//
//        });
//
//    }
//
//
//    // ============================================================
//    // SETUP BIOMETRIC
//    // ============================================================
//
//    private void setupBiometric() {
//
//
//        BiometricManager biometricManager =
//                BiometricManager.from(this);
//
//
//        int result =
//                biometricManager.canAuthenticate(
//                        BiometricManager.Authenticators.BIOMETRIC_STRONG
//                                |
//                                BiometricManager.Authenticators.DEVICE_CREDENTIAL
//                );
//
//
//        // ========================================================
//        // AVAILABLE
//        // ========================================================
//
//        if (
//                result ==
//                        BiometricManager.BIOMETRIC_SUCCESS
//        ) {
//
//            txtBiometricMessage.setText(
//                    "Use Face Unlock, Fingerprint or device lock to continue"
//            );
//
//        }
//
//
//        // ========================================================
//        // NOT ENROLLED
//        // ========================================================
//
//        else if (
//                result ==
//                        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
//        ) {
//
//            txtBiometricMessage.setText(
//                    "No biometric is enrolled on this device"
//            );
//
//            btnVerifyBiometric.setText(
//                    "Open Security Settings"
//            );
//
//        }
//
//
//        // ========================================================
//        // NOT SUPPORTED
//        // ========================================================
//
//        else {
//
//            txtBiometricMessage.setText(
//                    "Biometric authentication is not available on this device"
//            );
//
//        }
//
//
//        // ========================================================
//        // EXECUTOR
//        // ========================================================
//
//        executor =
//                ContextCompat.getMainExecutor(this);
//
//
//        // ========================================================
//        // BIOMETRIC CALLBACK
//        // ========================================================
//
//        biometricPrompt =
//                new BiometricPrompt(
//                        this,
//                        executor,
//
//                        new BiometricPrompt.AuthenticationCallback() {
//
//
//                            @Override
//                            public void onAuthenticationSucceeded(
//                                    @NonNull BiometricPrompt.AuthenticationResult result
//                            ) {
//
//                                super
//                                        .onAuthenticationSucceeded(
//                                                result
//                                        );
//
//
//                                Toast.makeText(
//                                        BiometricVerificationActivity.this,
//                                        "Identity Verified",
//                                        Toast.LENGTH_SHORT
//                                ).show();
//
//
//// ========================================================
//// SAVE FINAL LOGIN SESSION
//// ========================================================
//
//                                SharedPreferences preferences =
//                                        getSharedPreferences(
//                                                "WareVista",
//                                                MODE_PRIVATE
//                                        );
//
//
//                                preferences.edit()
//
//                                        .putBoolean(
//                                                "isLoggedIn",
//                                                true
//                                        )
//
//                                        .putString(
//                                                "userId",
//                                                userId
//                                        )
//
//                                        .putString(
//                                                "warehouse",
//                                                warehouse
//                                        )
//
//                                        .putString(
//                                                "role",
//                                                role
//                                        )
//
//                                        .putString(
//                                                "mobile",
//                                                mobile
//                                        )
//
//                                        .apply();
//
//
//// ========================================================
//// OPEN CORRECT DASHBOARD
//// ========================================================
//
//                                openDashboard();
//
//                            }
//
//
//                            @Override
//                            public void onAuthenticationError(
//                                    int errorCode,
//                                    @NonNull CharSequence errString
//                            ) {
//
//                                super
//                                        .onAuthenticationError(
//                                                errorCode,
//                                                errString
//                                        );
//
//
//                                Toast.makeText(
//                                        BiometricVerificationActivity.this,
//                                        errString,
//                                        Toast.LENGTH_SHORT
//                                ).show();
//
//                            }
//
//
//                            @Override
//                            public void onAuthenticationFailed() {
//
//                                super
//                                        .onAuthenticationFailed();
//
//
//                                Toast.makeText(
//                                        BiometricVerificationActivity.this,
//                                        "Verification failed. Try again.",
//                                        Toast.LENGTH_SHORT
//                                ).show();
//
//                            }
//
//                        }
//                );
//
//
//        // ========================================================
//        // PROMPT
//        // ========================================================
//
//        promptInfo =
//                new BiometricPrompt.PromptInfo.Builder()
//
//                        .setTitle(
//                                "WareVista Identity Verification"
//                        )
//
//                        .setSubtitle(
//                                "Verify your identity to continue"
//                        )
//
//                        .setDescription(
//                                "Use your device biometric or screen lock"
//                        )
//
//                        .setAllowedAuthenticators(
//                                BiometricManager.Authenticators.BIOMETRIC_STRONG
//                                        |
//                                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
//                        )
//
//                        .build();
//
//    }
//
//
//    // ============================================================
//    // AUTHENTICATE
//    // ============================================================
//
//    private void authenticate() {
//
//        biometricPrompt.authenticate(
//                promptInfo
//        );
//
//    }
//
//
//    // ============================================================
//    // OPEN DASHBOARD
//    // ============================================================
//
//    private void openDashboard() {
//
//
//        Intent intent;
//
//
//        // ========================================================
//        // ADMIN
//        // ========================================================
//
//        if (
//                role != null &&
//                        role.equalsIgnoreCase("ADMIN")
//        ) {
//
//            intent =
//                    new Intent(
//                            this,
//                            AdminDashboardActivity.class
//                    );
//
//        }
//
//
//        // ========================================================
//        // STAFF
//        // ========================================================
//
//        else {
//
//            intent =
//                    new Intent(
//                            this,
//                            HomeActivity.class
//                    );
//
//        }
//
//
//        // ========================================================
//        // PASS USER DATA
//        // ========================================================
//
//        intent.putExtra(
//                "userId",
//                userId
//        );
//
//        intent.putExtra(
//                "warehouse",
//                warehouse
//        );
//
//        intent.putExtra(
//                "role",
//                role
//        );
//
//        intent.putExtra(
//                "mobile",
//                mobile
//        );
//
//
//        // ========================================================
//        // CLEAR LOGIN STACK
//        // ========================================================
//
//        intent.setFlags(
//                Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                        Intent.FLAG_ACTIVITY_NEW_TASK |
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
//    // BACK
//    // ============================================================
//
//    @Override
//    public void onBackPressed() {
//
//        finish();
//
//    }
//
//}
package com.example.warevista;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import java.util.concurrent.Executor;


public class BiometricVerificationActivity
        extends AppCompatActivity {


    // ============================================================
    // UI
    // ============================================================

    private MaterialButton btnVerifyBiometric;

    private TextView txtBiometricMessage;

    private TextView txtCancel;


    // ============================================================
    // SHARED PREFERENCES
    // ============================================================

    private SharedPreferences preferences;


    // ============================================================
    // LOGIN DATA
    // ============================================================

    private String userId;

    private String warehouse;

    private String role;

    private String mobile;


    // ============================================================
    // SUBSCRIPTION DATA
    // ============================================================

    private String subscription;

    private int warehouseLimit;

    private boolean accessPurchase;

    private boolean accessSales;

    private boolean accessHistory;

    private boolean accessLiveStock;


    // ============================================================
    // BIOMETRIC
    // ============================================================

    private Executor executor;

    private BiometricPrompt biometricPrompt;

    private BiometricPrompt.PromptInfo promptInfo;


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_biometric_verification
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

        btnVerifyBiometric =

                findViewById(
                        R.id.btnVerifyBiometric
                );


        txtBiometricMessage =

                findViewById(
                        R.id.txtBiometricMessage
                );


        txtCancel =

                findViewById(
                        R.id.txtCancel
                );


        // ========================================================
        // LOAD DATA
        // ========================================================

        loadSessionData();


        // ========================================================
        // SETUP BIOMETRIC
        // ========================================================

        setupBiometric();


        // ========================================================
        // VERIFY BUTTON
        // ========================================================

        btnVerifyBiometric.setOnClickListener(

                v -> authenticate()

        );


        // ========================================================
        // CANCEL
        // ========================================================

        txtCancel.setOnClickListener(

                v -> cancelLogin()

        );

    }


    // ============================================================
    // LOAD SESSION DATA
    // ============================================================

    private void loadSessionData() {


        // ========================================================
        // USER ID
        // ========================================================

        userId =

                getIntent().getStringExtra(
                        "userId"
                );


        if (userId == null ||
                userId.trim().isEmpty()) {

            userId =

                    preferences.getString(

                            "userId",

                            ""

                    );

        }


        // ========================================================
        // WAREHOUSE
        // ========================================================

        warehouse =

                getIntent().getStringExtra(
                        "warehouse"
                );


        if (warehouse == null ||
                warehouse.trim().isEmpty()) {

            warehouse =

                    preferences.getString(

                            "warehouse",

                            ""

                    );

        }


        // ========================================================
        // ROLE
        // ========================================================

        role =

                getIntent().getStringExtra(
                        "role"
                );


        if (role == null ||
                role.trim().isEmpty()) {

            role =

                    preferences.getString(

                            "role",

                            "STAFF"

                    );

        }


        // ========================================================
        // MOBILE
        // ========================================================

        mobile =

                getIntent().getStringExtra(
                        "mobile"
                );


        if (mobile == null ||
                mobile.trim().isEmpty()) {

            mobile =

                    preferences.getString(

                            "mobile",

                            ""

                    );

        }


        // ========================================================
        // PLAN
        //
        // STEP 12 sends:
        // subscriptionPlan
        // subscription
        // ========================================================

        subscription =

                getIntent().getStringExtra(
                        "subscriptionPlan"
                );


        if (subscription == null ||
                subscription.trim().isEmpty()) {

            subscription =

                    getIntent().getStringExtra(
                            "subscription"
                    );

        }


        if (subscription == null ||
                subscription.trim().isEmpty()) {

            subscription =

                    preferences.getString(

                            "subscriptionPlan",

                            "FREE"

                    );

        }


        subscription =
                subscription
                        .trim()
                        .toUpperCase();


        // ========================================================
        // WAREHOUSE LIMIT
        // ========================================================

        warehouseLimit =

                getIntent().getIntExtra(

                        "warehouseLimit",

                        preferences.getInt(

                                "warehouseLimit",

                                1

                        )

                );


        if (warehouseLimit < 1) {

            warehouseLimit = 1;

        }


        // ========================================================
        // PURCHASE
        // ========================================================

        accessPurchase =

                getIntent().getBooleanExtra(

                        "accessPurchase",

                        preferences.getBoolean(

                                "accessPurchase",

                                true

                        )

                );


        // ========================================================
        // SALES
        // ========================================================

        accessSales =

                getIntent().getBooleanExtra(

                        "accessSales",

                        preferences.getBoolean(

                                "accessSales",

                                true

                        )

                );


        // ========================================================
        // HISTORY
        // ========================================================

        accessHistory =

                getIntent().getBooleanExtra(

                        "accessHistory",

                        preferences.getBoolean(

                                "accessHistory",

                                false

                        )

                );


        // ========================================================
        // LIVE STOCK
        // ========================================================

        accessLiveStock =

                getIntent().getBooleanExtra(

                        "accessLiveStock",

                        preferences.getBoolean(

                                "accessLiveStock",

                                false

                        )

                );


        // ========================================================
        // NULL SAFETY
        // ========================================================

        if (userId == null) {

            userId = "";

        }


        if (warehouse == null) {

            warehouse = "";

        }


        if (role == null ||
                role.trim().isEmpty()) {

            role = "STAFF";

        }


        if (mobile == null) {

            mobile = "";

        }

    }


    // ============================================================
    // SETUP BIOMETRIC
    // ============================================================

    private void setupBiometric() {


        BiometricManager biometricManager =

                BiometricManager.from(
                        this
                );


        int result =

                biometricManager.canAuthenticate(

                        BiometricManager.Authenticators.BIOMETRIC_STRONG

                                |

                                BiometricManager.Authenticators.DEVICE_CREDENTIAL

                );


        // ========================================================
        // AVAILABLE
        // ========================================================

        if (

                result

                        ==

                        BiometricManager.BIOMETRIC_SUCCESS

        ) {


            txtBiometricMessage.setText(

                    "Use Face Unlock, Fingerprint or device lock to continue"

            );

        }


        // ========================================================
        // NOT ENROLLED
        // ========================================================

        else if (

                result

                        ==

                        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED

        ) {


            txtBiometricMessage.setText(

                    "No biometric or device lock is enrolled"

            );


            btnVerifyBiometric.setText(

                    "Authentication Unavailable"

            );


            btnVerifyBiometric.setEnabled(
                    false
            );

        }


        // ========================================================
        // NOT AVAILABLE
        // ========================================================

        else {


            txtBiometricMessage.setText(

                    "Biometric authentication is not available"

            );


            btnVerifyBiometric.setEnabled(
                    false
            );

        }


        // ========================================================
        // EXECUTOR
        // ========================================================

        executor =

                ContextCompat.getMainExecutor(
                        this
                );


        // ========================================================
        // BIOMETRIC PROMPT
        // ========================================================

        biometricPrompt =

                new BiometricPrompt(

                        this,

                        executor,


                        new BiometricPrompt.AuthenticationCallback() {


                            // ====================================
                            // SUCCESS
                            // ====================================

                            @Override
                            public void onAuthenticationSucceeded(

                                    @NonNull
                                    BiometricPrompt.AuthenticationResult result

                            ) {


                                super.onAuthenticationSucceeded(
                                        result
                                );


                                Toast.makeText(

                                        BiometricVerificationActivity.this,

                                        "Identity Verified",

                                        Toast.LENGTH_SHORT

                                ).show();


                                // ==================================
                                // SAVE FINAL SESSION
                                // ==================================

                                saveFinalSession();


                                // ==================================
                                // OPEN DASHBOARD
                                // ==================================

                                openDashboard();

                            }


                            // ====================================
                            // ERROR
                            // ====================================

                            @Override
                            public void onAuthenticationError(

                                    int errorCode,

                                    @NonNull
                                    CharSequence errString

                            ) {


                                super.onAuthenticationError(

                                        errorCode,

                                        errString

                                );


                                Toast.makeText(

                                        BiometricVerificationActivity.this,

                                        errString,

                                        Toast.LENGTH_SHORT

                                ).show();

                            }


                            // ====================================
                            // FAILED
                            // ====================================

                            @Override
                            public void onAuthenticationFailed() {


                                super.onAuthenticationFailed();


                                Toast.makeText(

                                        BiometricVerificationActivity.this,

                                        "Verification failed. Try again.",

                                        Toast.LENGTH_SHORT

                                ).show();

                            }

                        }

                );


        // ========================================================
        // PROMPT INFO
        // ========================================================

        promptInfo =

                new BiometricPrompt.PromptInfo.Builder()


                        .setTitle(

                                "WareVista Identity Verification"

                        )


                        .setSubtitle(

                                "Verify your identity to continue"

                        )


                        .setDescription(

                                "Use your device biometric or screen lock"

                        )


                        .setAllowedAuthenticators(

                                BiometricManager.Authenticators.BIOMETRIC_STRONG

                                        |

                                        BiometricManager.Authenticators.DEVICE_CREDENTIAL

                        )


                        .build();

    }


    // ============================================================
    // AUTHENTICATE
    // ============================================================

    private void authenticate() {


        if (biometricPrompt == null ||
                promptInfo == null) {


            Toast.makeText(

                    this,

                    "Authentication is not ready",

                    Toast.LENGTH_SHORT

            ).show();


            return;

        }


        biometricPrompt.authenticate(
                promptInfo
        );

    }


    // ============================================================
    // SAVE FINAL SESSION
    // ============================================================

    private void saveFinalSession() {


        preferences

                .edit()


                // =================================================
                // LOGIN
                // =================================================

                .putBoolean(

                        "isLoggedIn",

                        true

                )


                // =================================================
                // USER
                // =================================================

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

                        role.trim().toUpperCase()

                )


                .putString(

                        "mobile",

                        mobile

                )


                // =================================================
                // SUBSCRIPTION
                // =================================================

                .putString(

                        "subscriptionPlan",

                        subscription

                )


                .putString(

                        "subscription",

                        subscription

                )


                // =================================================
                // WAREHOUSE LIMIT
                // =================================================

                .putInt(

                        "warehouseLimit",

                        warehouseLimit

                )


                // =================================================
                // FEATURES
                // =================================================

                .putBoolean(

                        "accessPurchase",

                        accessPurchase

                )


                .putBoolean(

                        "accessSales",

                        accessSales

                )


                .putBoolean(

                        "accessHistory",

                        accessHistory

                )


                .putBoolean(

                        "accessLiveStock",

                        accessLiveStock

                )


                .apply();

    }


    // ============================================================
    // OPEN DASHBOARD
    // ============================================================

    private void openDashboard() {


        Intent intent;


        // ========================================================
        // ADMIN
        // ========================================================

        if (

                role.equalsIgnoreCase(
                        "ADMIN"
                )

        ) {


            intent =

                    new Intent(

                            BiometricVerificationActivity.this,

                            AdminDashboardActivity.class

                    );

        }


        // ========================================================
        // STAFF
        // ========================================================

        else {


            intent =

                    new Intent(

                            BiometricVerificationActivity.this,

                            LoadingActivity.class

                    );

        }


        // ========================================================
        // CLEAR BACK STACK
        // ========================================================

        intent.setFlags(

                Intent.FLAG_ACTIVITY_NEW_TASK

                        |

                        Intent.FLAG_ACTIVITY_CLEAR_TASK

        );


        startActivity(intent);

        finish();

    }


    // ============================================================
    // CANCEL LOGIN
    // ============================================================

    private void cancelLogin() {


        preferences

                .edit()

                .clear()

                .apply();


        Intent intent =

                new Intent(

                        BiometricVerificationActivity.this,

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
    // BACK
    // ============================================================

    @Override
    public void onBackPressed() {

        cancelLogin();

    }

}