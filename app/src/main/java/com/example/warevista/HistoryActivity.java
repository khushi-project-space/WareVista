package com.example.warevista;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class HistoryActivity extends AppCompatActivity {

    private static final String BASE_URL =
            "https://script.google.com/macros/s/AKfycbxH_jxb8MUvSXeZQZAE9bbEGTojDKDoGD2-GMc3cWnn4EcI_fz42UCK4mTw2662HV14/exec";

    CardView cardPurchaseHistory;
    CardView cardSalesHistory;

    TextView txtTotalRecords;
    TextView txtTodayRecords;

    TextView txtPurchaseRecords;
    TextView txtPurchaseToday;

    TextView txtSalesRecords;
    TextView txtSalesToday;

    ImageView btnBack;

    // =========================================================
    // SKELETON VIEWS
    // =========================================================

    View skeletonTotalRecords;
    View skeletonTodayRecords;
    View skeletonPurchase;
    View skeletonSales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);

        // =====================================================
        // BACK BUTTON
        // =====================================================

        btnBack = findViewById(R.id.btnBack);

        if (btnBack != null) {

            btnBack.setOnClickListener(v -> finish());

        }

        // =====================================================
        // TEXT VIEWS
        // =====================================================

        txtTotalRecords =
                findViewById(R.id.txtTotalRecords);

        txtTodayRecords =
                findViewById(R.id.txtTodayRecords);

        txtPurchaseRecords =
                findViewById(R.id.txtPurchaseRecords);

        txtPurchaseToday =
                findViewById(R.id.txtPurchaseToday);

        txtSalesRecords =
                findViewById(R.id.txtSalesRecords);

        txtSalesToday =
                findViewById(R.id.txtSalesToday);

        // =====================================================
        // HISTORY BUTTON CARDS
        // =====================================================

        cardPurchaseHistory =
                findViewById(R.id.cardPurchaseHistory);

        cardSalesHistory =
                findViewById(R.id.cardSalesHistory);

        // =====================================================
        // SKELETON
        // =====================================================

        skeletonTotalRecords =
                findViewById(R.id.skeletonTotalRecords);

        skeletonTodayRecords =
                findViewById(R.id.skeletonTodayRecords);

        skeletonPurchase =
                findViewById(R.id.skeletonPurchase);

        skeletonSales =
                findViewById(R.id.skeletonSales);

        // =====================================================
        // SHOW SKELETON FIRST
        // =====================================================

        showSummarySkeleton();

        // =====================================================
        // LOAD DATA
        // =====================================================

        loadSummary();

        // =====================================================
        // PURCHASE HISTORY
        // =====================================================

        cardPurchaseHistory.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            HistoryActivity.this,
                            PurchaseHistoryActivity.class
                    );

            startActivity(intent);

        });

        // =====================================================
        // SALES HISTORY
        // =====================================================

        cardSalesHistory.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            HistoryActivity.this,
                            SalesHistoryActivity.class
                    );

            startActivity(intent);

        });

    }


    // =========================================================
    // SHOW SUMMARY SKELETON
    // =========================================================

    private void showSummarySkeleton() {

        if (skeletonTotalRecords != null) {

            skeletonTotalRecords.setVisibility(
                    View.VISIBLE
            );

        }

        if (skeletonTodayRecords != null) {

            skeletonTodayRecords.setVisibility(
                    View.VISIBLE
            );

        }

        if (skeletonPurchase != null) {

            skeletonPurchase.setVisibility(
                    View.VISIBLE
            );

        }

        if (skeletonSales != null) {

            skeletonSales.setVisibility(
                    View.VISIBLE
            );

        }

        // -----------------------------------------------------
        // Hide actual data
        // -----------------------------------------------------

        txtTotalRecords.setVisibility(
                View.INVISIBLE
        );

        txtTodayRecords.setVisibility(
                View.INVISIBLE
        );

        txtPurchaseRecords.setVisibility(
                View.INVISIBLE
        );

        txtPurchaseToday.setVisibility(
                View.INVISIBLE
        );

        txtSalesRecords.setVisibility(
                View.INVISIBLE
        );

        txtSalesToday.setVisibility(
                View.INVISIBLE
        );

    }


    // =========================================================
    // HIDE SUMMARY SKELETON
    // =========================================================

    private void hideSummarySkeleton() {

        if (skeletonTotalRecords != null) {

            skeletonTotalRecords.setVisibility(
                    View.GONE
            );

        }

        if (skeletonTodayRecords != null) {

            skeletonTodayRecords.setVisibility(
                    View.GONE
            );

        }

        if (skeletonPurchase != null) {

            skeletonPurchase.setVisibility(
                    View.GONE
            );

        }

        if (skeletonSales != null) {

            skeletonSales.setVisibility(
                    View.GONE
            );

        }

        // -----------------------------------------------------
        // Show actual data
        // -----------------------------------------------------

        txtTotalRecords.setVisibility(
                View.VISIBLE
        );

        txtTodayRecords.setVisibility(
                View.VISIBLE
        );

        txtPurchaseRecords.setVisibility(
                View.VISIBLE
        );

        txtPurchaseToday.setVisibility(
                View.VISIBLE
        );

        txtSalesRecords.setVisibility(
                View.VISIBLE
        );

        txtSalesToday.setVisibility(
                View.VISIBLE
        );

    }


    // =========================================================
    // LOAD SUMMARY
    // =========================================================

    private void loadSummary() {

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

        String url =
                BASE_URL +
                        "?module=historySummary&warehouse=" +
                        warehouse;

        StringRequest request =
                new StringRequest(

                        Request.Method.GET,

                        url,

                        response -> {

                            try {

                                JSONObject obj =
                                        new JSONObject(
                                                response
                                        );

                                // =================================================
                                // SET DATA
                                // =================================================

                                txtTotalRecords.setText(
                                        obj.getString(
                                                "totalRecords"
                                        )
                                );

                                txtTodayRecords.setText(
                                        obj.getString(
                                                "todayRecords"
                                        )
                                );

                                txtPurchaseRecords.setText(
                                        getString(R.string.total) + " " +
                                                obj.getString(
                                                        "purchaseTotal"
                                                )
                                );

                                txtPurchaseToday.setText(
                                        getString(R.string.today) + " " +
                                                obj.getString(
                                                        "purchaseToday"
                                                )
                                );

                                txtSalesRecords.setText(
                                        getString(R.string.total) + " " +
                                                obj.getString(
                                                        "salesTotal"
                                                )
                                );

                                txtSalesToday.setText(
                                        getString(R.string.today) + " " +
                                                obj.getString(
                                                        "salesToday"
                                                )
                                );

                                // =================================================
                                // DATA LOADED
                                // =================================================

                                hideSummarySkeleton();

                            }

                            catch (Exception e) {

                                e.printStackTrace();

                                // Error hoy to skeleton hide kari ne
                                // blank text na badle normal UI show karishu

                                hideSummarySkeleton();

                            }

                        },

                        error -> {

                            error.printStackTrace();

                            hideSummarySkeleton();

                        }

                );

        Volley
                .newRequestQueue(this)
                .add(request);

    }

}