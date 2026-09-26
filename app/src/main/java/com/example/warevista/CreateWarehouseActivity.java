package com.example.warevista;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class CreateWarehouseActivity extends AppCompatActivity {


    // ============================================================
    // GOOGLE APPS SCRIPT URL
    // ============================================================

    private static final String BASE_URL =
            "https://script.google.com/macros/s/AKfycbxH_jxb8MUvSXeZQZAE9bbEGTojDKDoGD2-GMc3cWnn4EcI_fz42UCK4mTw2662HV14/exec";

    // ============================================================
    // UI
    // ============================================================

    private EditText etLocation;
    private EditText etDate;
    private EditText etStaffName;
    private EditText etStaffId;
    private EditText etPassword;
    private EditText etMobile;

    private Button btnCreate;

    private ImageButton btnBack;


    // ============================================================
    // SESSION
    // ============================================================

    private SharedPreferences preferences;

    private String userId;

    private String subscriptionPlan;

    private int warehouseLimit;


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_create_warehouse
        );


        // ========================================================
        // SESSION
        // ========================================================

        preferences = getSharedPreferences(
                "WareVista",
                MODE_PRIVATE
        );


        userId = getIntent().getStringExtra(
                "userId"
        );


        subscriptionPlan = getIntent().getStringExtra(
                "subscriptionPlan"
        );


        warehouseLimit = getIntent().getIntExtra(
                "warehouseLimit",
                1
        );


        // ========================================================
        // FALLBACK FROM SHARED PREFERENCES
        // ========================================================

        if (userId == null ||
                userId.trim().isEmpty()) {

            userId = preferences.getString(
                    "userId",
                    ""
            );

        }


        if (subscriptionPlan == null ||
                subscriptionPlan.trim().isEmpty()) {

            subscriptionPlan = preferences.getString(
                    "subscriptionPlan",
                    "FREE"
            );

        }


        if (warehouseLimit <= 0) {

            warehouseLimit = preferences.getInt(
                    "warehouseLimit",
                    1
            );

        }


        // ========================================================
        // FIND VIEWS
        // ========================================================

        btnBack = findViewById(
                R.id.btnBack
        );


        etLocation = findViewById(
                R.id.etLocation
        );


        etDate = findViewById(
                R.id.etDate
        );


        etStaffName = findViewById(
                R.id.etStaffName
        );


        etStaffId = findViewById(
                R.id.etStaffId
        );


        etPassword = findViewById(
                R.id.etPassword
        );


        etMobile = findViewById(
                R.id.etMobile
        );


        btnCreate = findViewById(
                R.id.btnCreateWarehouse
        );


        // ========================================================
        // BACK
        // ========================================================

        btnBack.setOnClickListener(
                v -> finish()
        );


        // ========================================================
        // TODAY DATE
        // ========================================================

        setTodayDate();


        // ========================================================
        // STAFF NAME FILTER
        // ========================================================

        etStaffName.setFilters(

                new InputFilter[]{

                        new InputFilter() {

                            @Override
                            public CharSequence filter(

                                    CharSequence source,

                                    int start,

                                    int end,

                                    android.text.Spanned dest,

                                    int dstart,

                                    int dend

                            ) {


                                StringBuilder result =
                                        new StringBuilder();


                                for (
                                        int i = start;
                                        i < end;
                                        i++
                                ) {


                                    char c =
                                            source.charAt(i);


                                    if (

                                            Character.isLetter(c)

                                                    ||

                                                    Character.isWhitespace(c)

                                    ) {


                                        result.append(c);

                                    }

                                }


                                return result.toString();

                            }

                        }

                }

        );


        // ========================================================
        // STAFF ID
        // ========================================================

        etStaffId.setFilters(

                new InputFilter[]{

                        new InputFilter.LengthFilter(20)

                }

        );


        // ========================================================
        // PASSWORD
        // ========================================================

        etPassword.setFilters(

                new InputFilter[]{

                        new InputFilter.LengthFilter(30)

                }

        );


        // ========================================================
        // MOBILE
        // ========================================================

        etMobile.setFilters(

                new InputFilter[]{

                        new InputFilter.LengthFilter(10)

                }

        );


        // ========================================================
        // CREATE
        // ========================================================

        btnCreate.setOnClickListener(
                v -> validateAndCreate()
        );

    }


    // ============================================================
    // SET TODAY DATE
    // ============================================================

    private void setTodayDate() {


        SimpleDateFormat sdf =

                new SimpleDateFormat(

                        "dd/MM/yyyy",

                        Locale.getDefault()

                );


        etDate.setText(

                sdf.format(
                        new Date()
                )

        );

    }


    // ============================================================
    // VALIDATE
    // ============================================================

    private void validateAndCreate() {


        String location =

                etLocation
                        .getText()
                        .toString()
                        .trim();


        String createDate =

                etDate
                        .getText()
                        .toString()
                        .trim();


        String staffName =

                etStaffName
                        .getText()
                        .toString()
                        .trim();


        String staffId =

                etStaffId
                        .getText()
                        .toString()
                        .trim();


        String password =

                etPassword
                        .getText()
                        .toString()
                        .trim();


        String mobile =

                etMobile
                        .getText()
                        .toString()
                        .trim();


        // ========================================================
        // USER SESSION
        // ========================================================

        if (userId == null ||
                userId.trim().isEmpty()) {


            Toast.makeText(

                    this,

                    "User session not found. Please login again.",

                    Toast.LENGTH_LONG

            ).show();


            return;

        }


        // ========================================================
        // LOCATION
        // ========================================================

        if (TextUtils.isEmpty(location)) {


            etLocation.setError(
                    "Enter warehouse location"
            );


            etLocation.requestFocus();

            return;

        }


        if (!location.matches("[a-zA-Z ]+")) {


            etLocation.setError(
                    "Location should contain letters and spaces only"
            );


            etLocation.requestFocus();

            return;

        }


        // ========================================================
        // DATE
        // ========================================================

        if (TextUtils.isEmpty(createDate)) {


            etDate.setError(
                    "Select create date"
            );


            etDate.requestFocus();

            return;

        }


        // ========================================================
        // STAFF NAME
        // ========================================================

        if (TextUtils.isEmpty(staffName)) {


            etStaffName.setError(
                    "Enter staff name"
            );


            etStaffName.requestFocus();

            return;

        }


        if (!staffName.matches("[a-zA-Z ]+")) {


            etStaffName.setError(
                    "Name should contain letters and spaces only"
            );


            etStaffName.requestFocus();

            return;

        }


        // ========================================================
        // STAFF ID
        // ========================================================

        if (TextUtils.isEmpty(staffId)) {


            etStaffId.setError(
                    "Enter staff ID"
            );


            etStaffId.requestFocus();

            return;

        }


        if (!staffId.matches("[a-zA-Z0-9_]+")) {


            etStaffId.setError(
                    "Use letters, numbers and underscore only"
            );


            etStaffId.requestFocus();

            return;

        }


        // ========================================================
        // PASSWORD
        // ========================================================

        if (TextUtils.isEmpty(password)) {


            etPassword.setError(
                    "Enter password"
            );


            etPassword.requestFocus();

            return;

        }


        if (password.length() < 6) {


            etPassword.setError(
                    "Password must be at least 6 characters"
            );


            etPassword.requestFocus();

            return;

        }


        // ========================================================
        // MOBILE
        // ========================================================

        if (TextUtils.isEmpty(mobile)) {


            etMobile.setError(
                    "Enter mobile number"
            );


            etMobile.requestFocus();

            return;

        }


        if (!mobile.matches("[0-9]{10}")) {


            etMobile.setError(
                    "Enter valid 10 digit mobile number"
            );


            etMobile.requestFocus();

            return;

        }


        // ========================================================
        // CREATE WAREHOUSE
        // ========================================================

        createWarehouse(

                location,

                createDate,

                staffName,

                staffId,

                password,

                mobile

        );

    }


    // ============================================================
    // CREATE WAREHOUSE
    // ============================================================

    private void createWarehouse(

            String location,

            String createDate,

            String staffName,

            String staffId,

            String password,

            String mobile

    ) {


        // ========================================================
        // BUTTON LOADING
        // ========================================================

        btnCreate.setEnabled(false);

        btnCreate.setText(
                "Creating..."
        );


        StringRequest request =

                new StringRequest(

                        Request.Method.POST,

                        BASE_URL,


                        // ============================================
                        // SUCCESS
                        // ============================================

                        response -> {
                            btnCreate.setEnabled(true);
                            btnCreate.setText("Create Warehouse");

                            try {

                                JSONObject object =
                                        new JSONObject(response);


                                // ========================================================
                                // SERVER SUCCESS
                                // ========================================================

                                Intent intent = new Intent(
                                        CreateWarehouseActivity.this,
                                        SuccessActivity.class
                                );

                                intent.putExtra(
                                        "type",
                                        "warehouse"
                                );

                                intent.putExtra(
                                        "message",
                                        "Warehouse Created Successfully"
                                );

                                startActivity(intent);
                                finish();
                            }
                            catch (Exception e) {

                                Toast.makeText(
                                        CreateWarehouseActivity.this,
                                        "Invalid server response",
                                        Toast.LENGTH_SHORT
                                ).show();

                                e.printStackTrace();

                            }
                        },


                        // ============================================
                        // ERROR
                        // ============================================

                        error -> {


                            btnCreate.setEnabled(true);

                            btnCreate.setText(
                                    "Create Warehouse"
                            );


                            Toast.makeText(

                                    CreateWarehouseActivity.this,

                                    "Connection Error",

                                    Toast.LENGTH_SHORT

                            ).show();


                            error.printStackTrace();

                        }

                ) {


                    @Override
                    protected Map<String, String> getParams() {


                        Map<String, String> params =
                                new HashMap<>();


                        // ============================================
                        // MODULE
                        // ============================================

                        params.put(

                                "module",

                                "createWarehouse"

                        );


                        // ============================================
                        // SUBSCRIPTION OWNER
                        // ============================================

                        params.put(

                                "userId",

                                userId

                        );

                        // ============================================
                        // WAREHOUSE LOCATION
                        // IMPORTANT
                        // ============================================

                        params.put(
                                "location",
                                location
                        );


                        params.put(

                                "createDate",

                                createDate

                        );


                        // ============================================
                        // STAFF
                        // ============================================

                        params.put(

                                "staffName",

                                staffName

                        );


                        params.put(

                                "staffId",

                                staffId

                        );


                        params.put(

                                "password",

                                password

                        );


                        params.put(

                                "mobile",

                                mobile

                        );


                        return params;

                    }

                };


        Volley

                .newRequestQueue(this)

                .add(request);

    }


    // ============================================================
    // BACK
    // ============================================================

    @Override
    public void onBackPressed() {

        finish();

    }

}
