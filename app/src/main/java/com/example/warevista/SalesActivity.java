package com.example.warevista;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SalesActivity extends AppCompatActivity {

    // =========================================================
    // API
    // =========================================================

    private static final String API_URL =
            "https://script.google.com/macros/s/AKfycbxH_jxb8MUvSXeZQZAE9bbEGTojDKDoGD2-GMc3cWnn4EcI_fz42UCK4mTw2662HV14/exec";

    // =========================================================
    // VIEWS
    // =========================================================

    private Spinner spCrop;
    private Spinner spPaymentMethod;

    private TextView txtAvailableStock;

    private EditText etBuyerName;
    private EditText etMobile;
    private EditText etQuantity;
    private EditText etRate;
    private EditText etTotal;
    private EditText etDate;
    private EditText etRemarks;

    private Button btnSaveSales;

    // =========================================================
    // VARIABLES
    // =========================================================

    private int availableStock = 0;

    private boolean isSaving = false;
    private boolean isPaymentProcessing = false;

    // Demo payment ID
    private String demoPaymentId = "";

    private AlertDialog qrDialog;

    private Handler paymentHandler;
    private Runnable paymentChecker;

    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LanguageManager.applySavedLanguage(this);

        setContentView(R.layout.activity_sales);

        initializeViews();

        setupBackButton();

        loadCropSpinner();

        loadPaymentSpinner();

        showCurrentDate();

        setupCropSelection();

        setupValidation();

        setupTotalCalculation();

        // =====================================================
        // SAVE SALES BUTTON
        // =====================================================

        btnSaveSales.setOnClickListener(v -> {

            if (isSaving || isPaymentProcessing) {
                return;
            }

            if (!validateForm()) {
                return;
            }

            String paymentMethod =
                    spPaymentMethod.getSelectedItem()
                            .toString()
                            .trim();

            // =================================================
            // CASH
            // =================================================

            if (paymentMethod.equalsIgnoreCase("Cash")) {

                isSaving = true;

                btnSaveSales.setEnabled(false);

                btnSaveSales.setText(getString(R.string.saving));

                demoPaymentId = "";

                saveSales();

            }

            // =================================================
            // ONLINE / DEMO PAYMENT
            // =================================================

            else if (paymentMethod.equalsIgnoreCase("Online")) {

                isPaymentProcessing = true;

                btnSaveSales.setEnabled(false);

                btnSaveSales.setText(getString(R.string.creating_qr));

                createDemoQR();

            }

            else {

                Toast.makeText(
                        SalesActivity.this,
                        getString(R.string.please_select_payment_sales) ,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        spCrop =
                findViewById(R.id.spCrop);

        spPaymentMethod =
                findViewById(R.id.spPaymentMethod);

        txtAvailableStock =
                findViewById(R.id.txtAvailableStock);

        etBuyerName =
                findViewById(R.id.etBuyerName);

        etMobile =
                findViewById(R.id.etMobile);

        etQuantity =
                findViewById(R.id.etQuantity);

        etRate =
                findViewById(R.id.etRate);

        etTotal =
                findViewById(R.id.etTotal);

        etDate =
                findViewById(R.id.etDate);

        etRemarks =
                findViewById(R.id.etRemarks);

        btnSaveSales =
                findViewById(R.id.btnSaveSales);
    }

    // =========================================================
    // BACK BUTTON
    // =========================================================

    private void setupBackButton() {

        ImageView btnBack =
                findViewById(R.id.btnBack);

        if (btnBack != null) {

            btnBack.setOnClickListener(v -> finish());
        }
    }

    // =========================================================
    // LOAD CROP SPINNER
    // =========================================================

    private void loadCropSpinner() {

        // =========================================================
        // SHOW LOADING STATE
        // =========================================================

        ArrayList<String> loadingList = new ArrayList<>();

        loadingList.add(getString(R.string.loading_crops));

        ArrayAdapter<String> loadingAdapter =
                new ArrayAdapter<>(
                        SalesActivity.this,
                        R.layout.spinner_item,
                        loadingList
                );

        loadingAdapter.setDropDownViewResource(
                R.layout.spinner_dropdown_item
        );

        spCrop.setAdapter(loadingAdapter);

        // Disable spinner while crop data is loading
        spCrop.setEnabled(false);


        // =========================================================
        // GET WAREHOUSE
        // =========================================================

        SharedPreferences pref =
                getSharedPreferences(
                        "WareVista",
                        MODE_PRIVATE
                );

        String warehouse =
                pref.getString(
                        "warehouse",
                        ""
                );


        // =========================================================
        // API URL
        // =========================================================

        try {

            String url =
                    API_URL
                            + "?module=crops"
                            + "&warehouse="
                            + URLEncoder.encode(
                            warehouse,
                            "UTF-8"
                    );


            StringRequest request =
                    new StringRequest(
                            Request.Method.GET,
                            url,

                            // =================================================
                            // SUCCESS
                            // =================================================

                            response -> {

                                try {

                                    JSONArray array =
                                            new JSONArray(
                                                    response.trim()
                                            );

                                    ArrayList<String> cropList =
                                            new ArrayList<>();

                                    // Default item
                                    cropList.add(getString(R.string.select_crop));


                                    // =================================================
                                    // ADD CROPS
                                    // =================================================

                                    for (
                                            int i = 0;
                                            i < array.length();
                                            i++
                                    ) {

                                        String crop =
                                                array.getString(i)
                                                        .trim();

                                        if (!crop.isEmpty()) {

                                            cropList.add(crop);
                                        }
                                    }


                                    // =================================================
                                    // SET ACTUAL ADAPTER
                                    // =================================================

                                    ArrayAdapter<String> adapter =
                                            new ArrayAdapter<>(
                                                    SalesActivity.this,
                                                    R.layout.spinner_item,
                                                    cropList
                                            );

                                    adapter.setDropDownViewResource(
                                            R.layout.spinner_dropdown_item
                                    );

                                    spCrop.setAdapter(adapter);


                                    // Enable after loading
                                    spCrop.setEnabled(true);


                                } catch (Exception e) {

                                    showCropLoadingError();
                                }
                            },


                            // =================================================
                            // API ERROR
                            // =================================================

                            error -> {

                                showCropLoadingError();
                            }
                    );


            Volley.newRequestQueue(this)
                    .add(request);


        } catch (Exception e) {

            showCropLoadingError();
        }
    }
    private void showCropLoadingError() {

        ArrayList<String> errorList =
                new ArrayList<>();

        errorList.add(getString(R.string.failed_load_crops));


        ArrayAdapter<String> errorAdapter =
                new ArrayAdapter<>(
                        SalesActivity.this,
                        R.layout.spinner_item,
                        errorList
                );

        errorAdapter.setDropDownViewResource(
                R.layout.spinner_dropdown_item
        );

        spCrop.setAdapter(errorAdapter);

        // Keep disabled if loading failed
        spCrop.setEnabled(false);


        Toast.makeText(
                this,
                getString(R.string.unable_load_crop_list),
                Toast.LENGTH_SHORT
        ).show();
    }

    // =========================================================
    // CROP SELECTION
    // =========================================================

    private void setupCropSelection() {

        spCrop.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        if (position > 0) {

                            String crop =
                                    spCrop.getSelectedItem()
                                            .toString()
                                            .trim();

                            loadAvailableStock(crop);

                        } else {

                            availableStock = 0;

                            txtAvailableStock.setText(
                                    "📦 Available Stock : 0 KG"
                            );
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                }
        );
    }

    // =========================================================
    // PAYMENT SPINNER
    // =========================================================

    private void loadPaymentSpinner() {

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.payment_method_list,
                        R.layout.spinner_item
                );

        adapter.setDropDownViewResource(
                R.layout.spinner_dropdown_item
        );

        spPaymentMethod.setAdapter(adapter);
    }

    // =========================================================
    // VALIDATION
    // =========================================================

    private void setupValidation() {

        // BUYER NAME

        etBuyerName.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        String value =
                                s.toString().trim();

                        if (value.isEmpty()) {

                            etBuyerName.setError(
                                    getString(R.string.enter_buyer_name)
                            );

                        } else if (!value.matches(
                                "[a-zA-Z ]+"
                        )) {

                            etBuyerName.setError(
                                    getString(R.string.only_alphabets_allowed)
                            );

                        } else {

                            etBuyerName.setError(null);
                        }
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );

        // MOBILE

        etMobile.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        String mobile =
                                s.toString().trim();

                        if (mobile.isEmpty()) {

                            etMobile.setError(
                                    getString(R.string.enter_mobile_number)
                            );

                        } else if (!mobile.matches(
                                "[0-9]{10}"
                        )) {

                            etMobile.setError(
                                    getString(R.string.valid_mobile_sales)
                            );

                        } else {

                            etMobile.setError(null);
                        }
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );

        // QUANTITY

        etQuantity.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        validateQuantity(
                                s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );

        // RATE

        etRate.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        validateRate(
                                s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );
    }

    // =========================================================
    // QUANTITY VALIDATION
    // =========================================================

    private void validateQuantity(String value) {

        if (value.trim().isEmpty()) {
            return;
        }

        try {

            int quantity =
                    Integer.parseInt(
                            value.trim()
                    );

            if (quantity <= 0) {

                etQuantity.setError(
                        getString(R.string.quantity_greater_zero)
                );

            } else if (quantity > 500) {

                etQuantity.setError(
                        getString(R.string.maximum_500_kg)
                );

            } else if (quantity > availableStock) {

                etQuantity.setError(
                        getString(R.string.stock_not_available)
                );

            } else {

                etQuantity.setError(null);
            }

        } catch (Exception e) {

            etQuantity.setError(
                    getString(R.string.invalid_quantity)
            );
        }
    }

    // =========================================================
    // RATE VALIDATION
    // =========================================================

    private void validateRate(String value) {

        if (value.trim().isEmpty()) {
            return;
        }

        try {

            int rate =
                    Integer.parseInt(
                            value.trim()
                    );

            if (rate <= 0) {

                etRate.setError(
                        getString(R.string.rate_greater_zero)
                );

            } else if (rate > 1000) {

                etRate.setError(
                        getString(R.string.maximum_1000_sales)
                );

            } else {

                etRate.setError(null);
            }

        } catch (Exception e) {

            etRate.setError(
                    getString(R.string.invalid_rate)
            );
        }
    }

    // =========================================================
    // TOTAL CALCULATION
    // =========================================================

    private void setupTotalCalculation() {

        TextWatcher watcher =
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        calculateTotal();
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                };

        etQuantity.addTextChangedListener(watcher);

        etRate.addTextChangedListener(watcher);
    }

    private void calculateTotal() {

        try {

            String qtyText =
                    etQuantity.getText()
                            .toString()
                            .trim();

            String rateText =
                    etRate.getText()
                            .toString()
                            .trim();

            if (qtyText.isEmpty() ||
                    rateText.isEmpty()) {

                etTotal.setText("");

                return;
            }

            double qty =
                    Double.parseDouble(qtyText);

            double rate =
                    Double.parseDouble(rateText);

            double total =
                    qty * rate;

            if (total == (long) total) {

                etTotal.setText(
                        String.valueOf(
                                (long) total
                        )
                );

            } else {

                etTotal.setText(
                        String.valueOf(total)
                );
            }

        } catch (Exception e) {

            etTotal.setText("");
        }
    }

    // =========================================================
    // DATE
    // =========================================================

    private void showCurrentDate() {

        String date =
                new SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                ).format(new Date());

        etDate.setText(date);
    }

    // =========================================================
    // FORM VALIDATION
    // =========================================================

    private boolean validateForm() {

        // BUYER

        String buyer =
                etBuyerName.getText()
                        .toString()
                        .trim();

        if (buyer.isEmpty()) {

            etBuyerName.setError(
                    getString(R.string.enter_buyer_name)
            );

            etBuyerName.requestFocus();

            return false;
        }

        if (!buyer.matches(
                "[a-zA-Z ]+"
        )) {

            etBuyerName.setError(
                    getString(R.string.only_alphabets_allowed)
            );

            etBuyerName.requestFocus();

            return false;
        }

        // MOBILE

        String mobile =
                etMobile.getText()
                        .toString()
                        .trim();

        if (!mobile.matches(
                "[0-9]{10}"
        )) {

            etMobile.setError(
                    getString(R.string.valid_mobile_sales)
            );

            etMobile.requestFocus();

            return false;
        }

        // CROP

        if (spCrop.getSelectedItemPosition() == 0) {

            Toast.makeText(
                    this,
                    getString(R.string.please_select_crop),
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        // QUANTITY

        String qtyText =
                etQuantity.getText()
                        .toString()
                        .trim();

        if (qtyText.isEmpty()) {

            etQuantity.setError(
                    getString(R.string.enter_quantity_sales)
            );

            return false;
        }

        int quantity;

        try {

            quantity =
                    Integer.parseInt(qtyText);

        } catch (Exception e) {

            etQuantity.setError(
                    getString(R.string.invalid_quantity)
            );

            return false;
        }

        if (quantity <= 0) {

            etQuantity.setError(
                    getString(R.string.quantity_greater_zero)
            );

            return false;
        }

        if (quantity > 500) {

            etQuantity.setError(
                    getString(R.string.maximum_500_kg)
            );

            return false;
        }

        if (quantity > availableStock) {

            etQuantity.setError(
                    getString(R.string.stock_not_available)
            );

            return false;
        }

        // RATE

        String rateText =
                etRate.getText()
                        .toString()
                        .trim();

        if (rateText.isEmpty()) {

            etRate.setError(
                    getString(R.string.enter_rate)
            );

            return false;
        }

        try {

            int rate =
                    Integer.parseInt(rateText);

            if (rate <= 0 || rate > 1000) {

                etRate.setError(
                        getString(R.string.invalid_rate)
                );

                return false;
            }

        } catch (Exception e) {

            etRate.setError(
                    getString(R.string.invalid_rate)
            );

            return false;
        }

        // PAYMENT

        if (spPaymentMethod.getSelectedItemPosition() == 0) {

            Toast.makeText(
                    this,
                    getString(R.string.please_select_payment_sales),
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        // TOTAL

        String total =
                etTotal.getText()
                        .toString()
                        .trim();

        if (total.isEmpty()) {

            Toast.makeText(
                    this,
                    getString(R.string.invalid_total_amount),
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        return true;
    }

    // =========================================================
    // CREATE DEMO QR
    // =========================================================

    private void createDemoQR() {

        String totalText =
                etTotal.getText()
                        .toString()
                        .trim();

        if (totalText.isEmpty()) {

            paymentReset();

            Toast.makeText(
                    this,
                    getString(R.string.invalid_total_amount),
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String url;

        try {

            url =
                    API_URL
                            + "?module=createDemoQR"
                            + "&amount="
                            + URLEncoder.encode(
                            totalText,
                            "UTF-8"
                    );

        } catch (Exception e) {

            paymentReset();

            Toast.makeText(
                    this,
                    getString(R.string.qr_url_error),
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        StringRequest request =
                new StringRequest(
                        Request.Method.GET,
                        url,

                        response -> {

                            try {

                                JSONObject json =
                                        new JSONObject(
                                                response.trim()
                                        );

                                boolean success =
                                        json.optBoolean(
                                                "success",
                                                false
                                        );

                                if (!success) {

                                    paymentReset();

                                    Toast.makeText(
                                            this,
                                            json.optString(
                                                    "message",
                                                    getString(R.string.demo_qr_creation_failed)
                                            ),
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }

                                // =====================================
                                // GET DEMO PAYMENT ID
                                // =====================================

                                demoPaymentId =
                                        json.optString(
                                                "paymentId",
                                                ""
                                        );

                                // =====================================
                                // GET PAYMENT URL
                                // =====================================

                                String paymentUrl =
                                        json.optString(
                                                "paymentUrl",
                                                ""
                                        );

                                if (demoPaymentId.isEmpty()) {

                                    paymentReset();

                                    Toast.makeText(
                                            this,
                                            getString(R.string.payment_id_not_received),
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }

                                if (paymentUrl.isEmpty()) {

                                    paymentReset();

                                    Toast.makeText(
                                            this,
                                            getString(R.string.payment_id_not_received),
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }

                                // =====================================
                                // SHOW QR
                                // =====================================

                                showDemoQR(paymentUrl);

                            } catch (Exception e) {

                                paymentReset();

                                Toast.makeText(
                                        this,
                                        getString(R.string.invalid_demo_qr_response),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        },

                        error -> {

                            paymentReset();

                            Toast.makeText(
                                    this,
                                    getString(R.string.demo_qr_connection_failed),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );

        Volley.newRequestQueue(this)
                .add(request);
    }

    // =========================================================
    // GENERATE AND SHOW QR
    // =========================================================

    private void showDemoQR(String paymentUrl) {

        try {

            QRCodeWriter writer =
                    new QRCodeWriter();

            BitMatrix bitMatrix =
                    writer.encode(
                            paymentUrl,
                            BarcodeFormat.QR_CODE,
                            800,
                            800
                    );

            Bitmap bitmap =
                    Bitmap.createBitmap(
                            800,
                            800,
                            Bitmap.Config.RGB_565
                    );

            for (int x = 0; x < 800; x++) {

                for (int y = 0; y < 800; y++) {

                    bitmap.setPixel(
                            x,
                            y,
                            bitMatrix.get(x, y)
                                    ? Color.BLACK
                                    : Color.WHITE
                    );
                }
            }

            // =====================================================
            // MAIN LAYOUT
            // =====================================================

            LinearLayout layout =
                    new LinearLayout(this);

            layout.setOrientation(
                    LinearLayout.VERTICAL
            );

            layout.setGravity(
                    Gravity.CENTER
            );

            layout.setPadding(
                    30,
                    25,
                    30,
                    25
            );

            // =====================================================
            // TITLE
            // =====================================================

            TextView title =
                    new TextView(this);

            title.setText(
                    getString(R.string.demo_payment_title)
            );

            title.setTextSize(20);

            title.setGravity(
                    Gravity.CENTER
            );

            title.setPadding(
                    0,
                    0,
                    0,
                    15
            );

            // =====================================================
            // AMOUNT
            // =====================================================

            TextView amount =
                    new TextView(this);

            amount.setText(
                    getString(R.string.scan_qr_to_pay)
                            + etTotal.getText()
                            .toString()
            );

            amount.setTextSize(18);

            amount.setGravity(
                    Gravity.CENTER
            );

            amount.setPadding(
                    0,
                    0,
                    0,
                    15
            );

            // =====================================================
            // QR IMAGE
            // =====================================================

            ImageView qrImage =
                    new ImageView(this);

            qrImage.setImageBitmap(bitmap);

            qrImage.setAdjustViewBounds(true);

            // =====================================================
            // PAYMENT ID
            // =====================================================

            TextView paymentIdText =
                    new TextView(this);

            paymentIdText.setText(
                    getString(R.string.demo_payment_id)
                            + demoPaymentId
            );

            paymentIdText.setTextSize(12);

            paymentIdText.setGravity(
                    Gravity.CENTER
            );

            paymentIdText.setPadding(
                    0,
                    10,
                    0,
                    0
            );

            // =====================================================
            // ADD VIEWS
            // =====================================================

            layout.addView(title);

            layout.addView(amount);

            layout.addView(
                    qrImage,
                    new LinearLayout.LayoutParams(
                            -1,
                            650
                    )
            );

            layout.addView(paymentIdText);

            // =====================================================
            // DIALOG
            // =====================================================

            qrDialog =
                    new AlertDialog.Builder(this)
                            .setView(layout)
                            .setNegativeButton(
                                    getString(R.string.cancel),
                                    (dialog, which) -> {

                                        paymentReset();
                                    }
                            )
                            .create();

            qrDialog.setCanceledOnTouchOutside(
                    false
            );

            qrDialog.show();

            // =====================================================
            // START PAYMENT CHECKING
            // =====================================================

            isPaymentProcessing = true;

            btnSaveSales.setEnabled(false);

            btnSaveSales.setText(
                    getString(R.string.waiting_demo_payment)
            );

            startDemoPaymentChecking();

        } catch (WriterException e) {

            paymentReset();

            Toast.makeText(
                    this,
                    getString(R.string.qr_generation_failed),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // =========================================================
    // START DEMO PAYMENT CHECKING
    // =========================================================

    private void startDemoPaymentChecking() {

        stopPaymentChecking();

        paymentHandler =
                new Handler(
                        Looper.getMainLooper()
                );

        paymentChecker =
                new Runnable() {

                    @Override
                    public void run() {

                        checkDemoPayment();

                        if (isPaymentProcessing) {

                            paymentHandler.postDelayed(
                                    this,
                                    3000
                            );
                        }
                    }
                };

        paymentHandler.postDelayed(
                paymentChecker,
                3000
        );
    }

    // =========================================================
    // CHECK DEMO PAYMENT
    // =========================================================

    private void checkDemoPayment() {

        if (demoPaymentId == null ||
                demoPaymentId.isEmpty()) {

            return;
        }

        String url;

        try {

            url =
                    API_URL
                            + "?module=checkDemoPayment"
                            + "&paymentId="
                            + URLEncoder.encode(
                            demoPaymentId,
                            "UTF-8"
                    );

        } catch (Exception e) {

            return;
        }

        StringRequest request =
                new StringRequest(
                        Request.Method.GET,
                        url,

                        response -> {

                            try {

                                JSONObject json =
                                        new JSONObject(
                                                response.trim()
                                        );

                                boolean paid =
                                        json.optBoolean(
                                                "paid",
                                                false
                                        );

                                // =================================================
                                // PAYMENT SUCCESS
                                // =================================================

                                if (paid) {

                                    stopPaymentChecking();

                                    if (qrDialog != null &&
                                            qrDialog.isShowing()) {

                                        qrDialog.dismiss();
                                    }

                                    isPaymentProcessing =
                                            false;

                                    isSaving = true;

                                    btnSaveSales.setEnabled(
                                            false
                                    );

                                    btnSaveSales.setText(
                                            getString(R.string.saving_sale)
                                    );

                                    saveSales();
                                }

                            } catch (Exception ignored) {
                                // next check will retry
                            }
                        },

                        error -> {
                            // retry after 3 seconds
                        }
                );

        Volley.newRequestQueue(this)
                .add(request);
    }

    // =========================================================
    // STOP PAYMENT CHECKING
    // =========================================================

    private void stopPaymentChecking() {

        if (paymentHandler != null &&
                paymentChecker != null) {

            paymentHandler.removeCallbacks(
                    paymentChecker
            );
        }
    }

    // =========================================================
    // PAYMENT RESET
    // =========================================================

    private void paymentReset() {

        stopPaymentChecking();

        isPaymentProcessing = false;

        isSaving = false;

        demoPaymentId = "";

        btnSaveSales.setEnabled(true);

        btnSaveSales.setText(
                getString(R.string.save_sales)
        );
    }

    // =========================================================
    // SAVE SALES
    // =========================================================

    private void saveSales() {

        SharedPreferences pref =
                getSharedPreferences(
                        "WareVista",
                        MODE_PRIVATE
                );

        String warehouse =
                pref.getString(
                        "warehouse",
                        ""
                );

        StringRequest request =
                new StringRequest(

                        Request.Method.POST,

                        API_URL,

                        response -> {

                            isSaving = false;

                            isPaymentProcessing = false;

                            btnSaveSales.setEnabled(true);

                            btnSaveSales.setText(
                                    getString(R.string.save_sales)
                            );

                            try {

                                JSONObject json =
                                        new JSONObject(
                                                response.trim()
                                        );

                                boolean success =
                                        json.optBoolean(
                                                "success",
                                                true
                                        );

                                if (success) {

                                    showBillDownloadDialog();

                                } else {

                                    Toast.makeText(
                                            this,
                                            json.optString(
                                                    "message",
                                                    "Sales save failed"
                                            ),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }

                            } catch (Exception e) {

                                // Existing backend may return
                                // non-JSON success response.

                                showBillDownloadDialog();
                            }
                        },

                        error -> {

                            isSaving = false;

                            isPaymentProcessing = false;

                            btnSaveSales.setEnabled(true);

                            btnSaveSales.setText(
                                    getString(R.string.save_sales)
                            );

                            Toast.makeText(
                                    this,
                                    "Server Error",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                ) {

                    @Override
                    protected Map<String, String>
                    getParams() {

                        Map<String, String> params =
                                new HashMap<>();

                        // =================================================
                        // BASIC SALES DATA
                        // =================================================

                        params.put(
                                "module",
                                "sales"
                        );

                        params.put(
                                "warehouse",
                                warehouse
                        );

                        params.put(
                                "buyer",
                                etBuyerName.getText()
                                        .toString()
                                        .trim()
                        );

                        params.put(
                                "mobile",
                                etMobile.getText()
                                        .toString()
                                        .trim()
                        );

                        params.put(
                                "crop",
                                spCrop.getSelectedItem()
                                        .toString()
                                        .trim()
                        );

                        params.put(
                                "quantity",
                                etQuantity.getText()
                                        .toString()
                                        .trim()
                        );

                        params.put(
                                "rate",
                                etRate.getText()
                                        .toString()
                                        .trim()
                        );

                        params.put(
                                "total",
                                etTotal.getText()
                                        .toString()
                                        .trim()
                        );

                        params.put(
                                "payment",
                                spPaymentMethod
                                        .getSelectedItem()
                                        .toString()
                                        .trim()
                        );

                        params.put(
                                "date",
                                etDate.getText()
                                        .toString()
                                        .trim()
                        );

                        params.put(
                                "remarks",
                                etRemarks.getText()
                                        .toString()
                                        .trim()
                        );

                        // =================================================
                        // PAYMENT STATUS
                        // =================================================

                        String paymentMethod =
                                spPaymentMethod
                                        .getSelectedItem()
                                        .toString()
                                        .trim();

                        if (paymentMethod.equalsIgnoreCase(
                                "Online"
                        )) {

                            params.put(
                                    "paymentStatus",
                                    "SUCCESS"
                            );

                        } else {

                            params.put(
                                    "paymentStatus",
                                    "CASH"
                            );
                        }

                        // =================================================
                        // DEMO PAYMENT ID
                        // =================================================

                        params.put(
                                "demoPaymentId",
                                demoPaymentId
                        );

                        return params;
                    }
                };

        Volley.newRequestQueue(this)
                .add(request);
    }

    // =========================================================
    // LOAD AVAILABLE STOCK
    // =========================================================

    private void loadAvailableStock(
            String cropName) {

        SharedPreferences pref =
                getSharedPreferences(
                        "WareVista",
                        MODE_PRIVATE
                );

        String warehouse =
                pref.getString(
                        "warehouse",
                        ""
                );

        try {

            String url =
                    API_URL
                            + "?module=stock"
                            + "&warehouse="
                            + URLEncoder.encode(
                            warehouse,
                            "UTF-8"
                    )
                            + "&crop="
                            + URLEncoder.encode(
                            cropName,
                            "UTF-8"
                    );

            StringRequest request =
                    new StringRequest(
                            Request.Method.GET,
                            url,

                            response -> {

                                try {

                                    JSONObject json =
                                            new JSONObject(
                                                    response.trim()
                                            );

                                    JSONArray array =
                                            json.optJSONArray(
                                                    "stockList"
                                            );

                                    availableStock = 0;

                                    if (array != null) {

                                        for (
                                                int i = 0;
                                                i < array.length();
                                                i++
                                        ) {

                                            JSONObject obj =
                                                    array.getJSONObject(
                                                            i
                                                    );

                                            String crop =
                                                    obj.optString(
                                                            "crop",
                                                            ""
                                                    ).trim();

                                            if (crop.equalsIgnoreCase(
                                                    cropName.trim()
                                            )) {

                                                availableStock =
                                                        obj.optInt(
                                                                "available",
                                                                0
                                                        );

                                                break;
                                            }
                                        }
                                    }

                                    if (availableStock <= 0) {

                                        txtAvailableStock.setText(
                                                getString(R.string.stock_not_available_display)
                                        );

                                    } else {

                                        txtAvailableStock.setText(
                                                getString(
                                                        R.string.available_stock,
                                                        availableStock
                                                )
                                        );
                                    }

                                } catch (Exception e) {

                                    availableStock = 0;

                                    txtAvailableStock.setText(
                                            getString(R.string.stock_loading_error)
                                    );
                                }
                            },

                            error -> {

                                availableStock = 0;

                                txtAvailableStock.setText(
                                        getString(R.string.stock_loading_error)
                                );
                            }
                    );

            Volley.newRequestQueue(this)
                    .add(request);

        } catch (Exception e) {

            availableStock = 0;

            txtAvailableStock.setText(
                    getString(R.string.stock_loading_error)
            );
        }
    }

    // =========================================================
    // BILL DOWNLOAD DIALOG
    // =========================================================

    private void showBillDownloadDialog() {

        new androidx.appcompat.app.AlertDialog.Builder(this)

                .setTitle(
                        getString(R.string.download_bill)
                )

                .setMessage(
                        getString(R.string.download_bill_question)
                )

                .setPositiveButton(
                        getString(R.string.yes),
                        (dialog, which) ->
                                generateBillPDF()
                )

                .setNegativeButton(
                        getString(R.string.no),
                        (dialog, which) ->
                                openSuccessScreen()
                )

                .show();
    }

    // =========================================================
    // SUCCESS SCREEN
    // =========================================================

    private void openSuccessScreen() {

        Intent intent =
                new Intent(
                        SalesActivity.this,
                        SuccessActivity.class
                );

        intent.putExtra(
                "type",
                "sales"
        );

        intent.putExtra(
                "message",
                getString(R.string.sales_saved_successfully)
        );

        startActivity(intent);

        clearForm();
    }

    // =========================================================
    // CLEAR FORM
    // =========================================================

    private void clearForm() {

        etBuyerName.setText("");

        etMobile.setText("");

        spCrop.setSelection(0);

        etQuantity.setText("");

        etRate.setText("");

        etTotal.setText("");

        spPaymentMethod.setSelection(0);

        etRemarks.setText("");

        demoPaymentId = "";

        availableStock = 0;

        txtAvailableStock.setText(
                getString(
                        R.string.available_stock,
                        0
                )
        );

        showCurrentDate();
    }

    // =========================================================
    // PDF
    // =========================================================

    private void generateBillPDF() {

        /*
         * Tamaro existing PDF generation code
         * ahi muki shako.
         */

        Toast.makeText(
                this,
                getString(R.string.bill_pdf_generation),
                Toast.LENGTH_SHORT
        ).show();

        openSuccessScreen();
    }

    // =========================================================
    // ACTIVITY DESTROY
    // =========================================================

    @Override
    protected void onDestroy() {

        stopPaymentChecking();

        if (qrDialog != null &&
                qrDialog.isShowing()) {

            qrDialog.dismiss();
        }

        super.onDestroy();
    }
}