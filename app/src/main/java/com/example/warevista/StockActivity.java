package com.example.warevista;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

import android.content.SharedPreferences;


public class StockActivity extends AppCompatActivity {

    private static final String BASE_URL =
            "https://script.google.com/macros/s/AKfycbxH_jxb8MUvSXeZQZAE9bbEGTojDKDoGD2-GMc3cWnn4EcI_fz42UCK4mTw2662HV14/exec";


    TextView txtTotalPurchased;
    TextView txtTotalSold;
    TextView txtTotalAvailable;

    TextView txtHighlightIcon;
    TextView txtHighlightTitle;
    TextView txtHighlightValue;

    View skeletonPurchased;
    View skeletonSold;
    View skeletonAvailable;
    View skeletonHighlight;
    Spinner spCrop;
    View skeletonCrop;

    RecyclerView recyclerStock;

    ArrayList<StockModel> stockList;
    ArrayList<StockModel> originalList;

    TextView txtNoStock;

    MaterialCardView highlightCard;

    StockAdapter adapter;

    private Handler highlightHandler = new Handler();
    private Runnable highlightRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        LanguageManager.applySavedLanguage(this);

        setContentView(R.layout.activity_stock);


        // =====================================================
        // BACK BUTTON
        // =====================================================

        ImageView btnBack = findViewById(R.id.btnBack);

        if (btnBack != null) {

            btnBack.setOnClickListener(v -> finish());

        }


        // =====================================================
        // FIND VIEWS
        // =====================================================

        txtTotalPurchased =
                findViewById(R.id.txtTotalPurchased);

        txtTotalSold =
                findViewById(R.id.txtTotalSold);

        txtTotalAvailable =
                findViewById(R.id.txtTotalAvailable);


        txtHighlightIcon =
                findViewById(R.id.txtHighlightIcon);

        txtHighlightTitle =
                findViewById(R.id.txtHighlightTitle);

        txtHighlightValue =
                findViewById(R.id.txtHighlightValue);


        highlightCard =
                findViewById(R.id.highlightCard);

        txtNoStock =
                findViewById(R.id.txtNoStock);


        spCrop =
                findViewById(R.id.spCrop);


        recyclerStock =
                findViewById(R.id.recyclerStock);
        skeletonCrop = findViewById(R.id.skeletonCrop);


        // =====================================================
        // SKELETON VIEWS
        // =====================================================

        skeletonPurchased =
                findViewById(R.id.skeletonPurchased);

        skeletonSold =
                findViewById(R.id.skeletonSold);

        skeletonAvailable =
                findViewById(R.id.skeletonAvailable);

        skeletonHighlight =
                findViewById(R.id.skeletonHighlight);


        // =====================================================
        // RECYCLER VIEW
        // =====================================================

        recyclerStock.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerStock.setNestedScrollingEnabled(true);

        recyclerStock.setHasFixedSize(false);


        stockList = new ArrayList<>();

        originalList = new ArrayList<>();


        adapter = new StockAdapter(stockList);

        recyclerStock.setAdapter(adapter);


        // =====================================================
        // INITIAL LOADING
        // =====================================================

        showSummaryLoading();


        recyclerStock.setVisibility(View.GONE);

        txtNoStock.setVisibility(View.GONE);


        // =====================================================
        // LOAD FILTERS
        // =====================================================

        loadFilters();
    }


    // =========================================================
    // SHOW SUMMARY SKELETON
    // =========================================================

    private void showSummaryLoading() {

        // Show skeleton over complete card content
        skeletonPurchased.setVisibility(View.VISIBLE);
        skeletonSold.setVisibility(View.VISIBLE);
        skeletonAvailable.setVisibility(View.VISIBLE);


        // Hide actual values only
        txtTotalPurchased.setVisibility(View.INVISIBLE);
        txtTotalSold.setVisibility(View.INVISIBLE);
        txtTotalAvailable.setVisibility(View.INVISIBLE);
    }


    // =========================================================
    // HIDE SUMMARY SKELETON
    // =========================================================

    private void hideSummaryLoading() {

        skeletonPurchased.setVisibility(View.GONE);
        skeletonSold.setVisibility(View.GONE);
        skeletonAvailable.setVisibility(View.GONE);


        txtTotalPurchased.setVisibility(View.VISIBLE);
        txtTotalSold.setVisibility(View.VISIBLE);
        txtTotalAvailable.setVisibility(View.VISIBLE);
    }


    // =========================================================
    // CROP FILTER
    // =========================================================

    private void loadFilters() {

        // -----------------------------------------------------
        // SHOW LOADING TEXT IN SPINNER
        // -----------------------------------------------------

        showCropSkeleton();

        // -----------------------------------------------------
        // GET WAREHOUSE
        // -----------------------------------------------------

        SharedPreferences preferences =
                getSharedPreferences(
                        "WareVista",
                        MODE_PRIVATE
                );


        String warehouse =
                preferences.getString(
                        "warehouse",
                        ""
                );


        // -----------------------------------------------------
        // URL
        // -----------------------------------------------------

        String url;

        try {

            url = BASE_URL
                    + "?module=filters"
                    + "&warehouse="
                    + URLEncoder.encode(
                    warehouse,
                    "UTF-8"
            );

        } catch (Exception e) {

            e.printStackTrace();

            showCropLoadingError();

            return;
        }


        // -----------------------------------------------------
        // REQUEST
        // -----------------------------------------------------

        StringRequest request =
                new StringRequest(

                        Request.Method.GET,

                        url,

                        response -> {

                            try {

                                JSONObject object =
                                        new JSONObject(
                                                response.trim()
                                        );


                                JSONArray cropArray =
                                        object.getJSONArray(
                                                "crops"
                                        );


                                ArrayList<String> cropList =
                                        new ArrayList<>();


                                for (
                                        int i = 0;
                                        i < cropArray.length();
                                        i++
                                ) {

                                    String crop =
                                            cropArray
                                                    .getString(i)
                                                    .trim();


                                    if (!crop.isEmpty()) {

                                        cropList.add(crop);

                                    }
                                }


                                // -------------------------------------------------
                                // NO CROPS
                                // -------------------------------------------------

                                if (cropList.isEmpty()) {

                                    showCropLoadingError();

                                    return;
                                }


                                // -------------------------------------------------
                                // REAL SPINNER
                                // -------------------------------------------------

                                ArrayAdapter<String>
                                        adapterSpinner =
                                        new ArrayAdapter<>(
                                                StockActivity.this,
                                                R.layout.spinner_item,
                                                cropList
                                        );


                                adapterSpinner
                                        .setDropDownViewResource(
                                                R.layout.spinner_dropdown_item
                                        );


                                spCrop.setAdapter(adapterSpinner);

                                hideCropSkeleton();


                                // -------------------------------------------------
                                // CROP SELECT
                                // -------------------------------------------------

                                spCrop.setOnItemSelectedListener(

                                        new AdapterView
                                                .OnItemSelectedListener() {

                                            @Override
                                            public void onItemSelected(
                                                    AdapterView<?> parent,
                                                    View view,
                                                    int position,
                                                    long id) {

                                                loadStock();
                                            }


                                            @Override
                                            public void onNothingSelected(
                                                    AdapterView<?> parent) {

                                            }
                                        }
                                );


                                // -------------------------------------------------
                                // INITIAL STOCK
                                // -------------------------------------------------

                                loadStock();

                            }

                            catch (Exception e) {

                                e.printStackTrace();

                                showCropLoadingError();
                            }
                        },


                        error -> {

                            error.printStackTrace();

                            showCropLoadingError();

                            Toast.makeText(
                                    StockActivity.this,
                                    getString(R.string.unable_load_crop_list),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                );


        Volley
                .newRequestQueue(this)
                .add(request);
    }


    // =========================================================
    // CROP LOADING ERROR
    // =========================================================

    private void showCropLoadingError() {

        ArrayList<String> errorList =
                new ArrayList<>();


        errorList.add(
                getString(R.string.failed_load_crops)
        );


        ArrayAdapter<String> errorAdapter =
                new ArrayAdapter<>(
                        StockActivity.this,
                        R.layout.spinner_item,
                        errorList
                );


        errorAdapter.setDropDownViewResource(
                R.layout.spinner_dropdown_item
        );


        spCrop.setAdapter(errorAdapter);

        spCrop.setEnabled(false);
    }


    // =========================================================
    // LOAD STOCK
    // =========================================================

    private void loadStock() {

        // =====================================================
        // SHOW LOADING
        // =====================================================

        showSummaryLoading();
        showHighlightLoading();

        recyclerStock.setVisibility(View.VISIBLE);

        txtNoStock.setVisibility(View.GONE);

        adapter.setLoading(true);



        // =====================================================
        // GET WAREHOUSE
        // =====================================================

        SharedPreferences preferences =
                getSharedPreferences(
                        "WareVista",
                        MODE_PRIVATE
                );


        String warehouse =
                preferences.getString(
                        "warehouse",
                        ""
                );


        // =====================================================
        // SELECTED CROP
        // =====================================================

        String crop = "All";


        if (spCrop.getSelectedItem() != null) {

            crop =
                    spCrop
                            .getSelectedItem()
                            .toString();
        }


        // =====================================================
        // ENCODE
        // =====================================================

        try {

            warehouse =
                    URLEncoder.encode(
                            warehouse,
                            "UTF-8"
                    );


            crop =
                    URLEncoder.encode(
                            crop,
                            "UTF-8"
                    );

        }

        catch (Exception e) {

            e.printStackTrace();
        }


        // =====================================================
        // URL
        // =====================================================

        String url =
                BASE_URL
                        + "?module=stock"
                        + "&warehouse="
                        + warehouse
                        + "&crop="
                        + crop;


        // =====================================================
        // API REQUEST
        // =====================================================

        StringRequest request =
                new StringRequest(

                        Request.Method.GET,

                        url,

                        response -> {

                            try {

                                JSONObject responseObject =
                                        new JSONObject(
                                                response.trim()
                                        );


                                // =================================================
                                // TOTALS
                                // =================================================

                                int purchased =
                                        responseObject.getInt(
                                                "totalPurchased"
                                        );


                                int sold =
                                        responseObject.getInt(
                                                "totalSold"
                                        );


                                int available =
                                        responseObject.getInt(
                                                "totalAvailable"
                                        );


                                txtTotalPurchased.setText(
                                        purchased + " KG"
                                );


                                txtTotalSold.setText(
                                        sold + " KG"
                                );


                                txtTotalAvailable.setText(
                                        available + " KG"
                                );


                                // =================================================
                                // HIDE SUMMARY SKELETON
                                // =================================================

                                hideSummaryLoading();


                                // =================================================
                                // CLEAR OLD DATA
                                // =================================================

                                stockList.clear();

                                originalList.clear();


                                JSONArray array =
                                        responseObject
                                                .getJSONArray(
                                                        "stockList"
                                                );


                                // =================================================
                                // ADD STOCK DATA
                                // =================================================

                                for (
                                        int i = 0;
                                        i < array.length();
                                        i++
                                ) {

                                    JSONObject item =
                                            array.getJSONObject(i);


                                    StockModel model =
                                            new StockModel(

                                                    item.getString(
                                                            "crop"
                                                    ),

                                                    item.getInt(
                                                            "purchased"
                                                    ),

                                                    item.getInt(
                                                            "sold"
                                                    ),

                                                    item.getInt(
                                                            "available"
                                                    )
                                            );


                                    stockList.add(model);

                                    originalList.add(model);
                                }

                                hideHighlightLoading();
                                adapter.setLoading(false);
                                adapter.notifyDataSetChanged();


                                // =================================================
                                // NO DATA
                                // =================================================

                                if (stockList.isEmpty()) {

                                    txtNoStock.setText(
                                            getString(R.string.no_records_selected_crop)
                                    );


                                    txtNoStock.setVisibility(
                                            View.VISIBLE
                                    );


                                    recyclerStock.setVisibility(
                                            View.GONE
                                    );

                                }

                                else {

                                    txtNoStock.setVisibility(
                                            View.GONE
                                    );


                                    recyclerStock.setVisibility(
                                            View.VISIBLE
                                    );
                                }


                                // =================================================
                                // RECYCLER HEIGHT
                                // =================================================

                                recyclerStock.post(() -> {

                                    ViewGroup.LayoutParams params =
                                            recyclerStock
                                                    .getLayoutParams();


                                    if (stockList.size() <= 2) {

                                        params.height =
                                                ViewGroup.LayoutParams
                                                        .WRAP_CONTENT;

                                    }

                                    else {

                                        int height =
                                                (int) (
                                                        360
                                                                * getResources()
                                                                .getDisplayMetrics()
                                                                .density
                                                );

                                        params.height = height;
                                    }


                                    recyclerStock.setLayoutParams(
                                            params
                                    );

                                    recyclerStock.requestLayout();
                                });


                                // =================================================
                                // HIGHLIGHTS
                                // =================================================

                                JSONObject highest =
                                        responseObject
                                                .getJSONObject(
                                                        "highestStock"
                                                );


                                JSONObject lowest =
                                        responseObject
                                                .getJSONObject(
                                                        "lowestStock"
                                                );


                                JSONObject fast =
                                        responseObject
                                                .getJSONObject(
                                                        "fastMoving"
                                                );


                                JSONObject out = null;


                                if (
                                        !responseObject.isNull(
                                                "outOfStock"
                                        )
                                ) {

                                    out =
                                            responseObject
                                                    .getJSONObject(
                                                            "outOfStock"
                                                    );
                                }


                                String outCrop = "";

                                boolean outFound = false;


                                if (out != null) {

                                    outCrop =
                                            out.getString(
                                                    "crop"
                                            );


                                    outFound =
                                            out.getInt(
                                                    "available"
                                            ) == 0;
                                }


                                startHighlights(

                                        highest.getString(
                                                "crop"
                                        ),

                                        highest.getInt(
                                                "available"
                                        ),

                                        lowest.getString(
                                                "crop"
                                        ),

                                        lowest.getInt(
                                                "available"
                                        ),

                                        fast.getString(
                                                "crop"
                                        ),

                                        fast.getInt(
                                                "sold"
                                        ),

                                        "",

                                        -1,

                                        outCrop,

                                        outFound
                                );

                            }

                            catch (Exception e) {

                                e.printStackTrace();


                                hideSummaryLoading();


                                Toast.makeText(
                                        StockActivity.this,
                                        getString(R.string.failed_load_stock_data),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        },


                        error -> {

                            error.printStackTrace();


                            hideSummaryLoading();


                            Toast.makeText(
                                    StockActivity.this,
                                    getString(R.string.unable_load_stock_data),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                );


        Volley
                .newRequestQueue(this)
                .add(request);
    }


    // =========================================================
    // STOCK HIGHLIGHTS
    // =========================================================

    private void startHighlights(

            String highestCrop,
            int highestQty,

            String lowestCrop,
            int lowestQty,

            String soldCrop,
            int soldQty,

            String restockCrop,
            int restockQty,

            String outCrop,
            boolean outFound
    ) {

        if (highlightRunnable != null) {

            highlightHandler.removeCallbacks(
                    highlightRunnable
            );
        }


        highlightRunnable =
                new Runnable() {

                    int index = 0;


                    @Override
                    public void run() {

                        switch (index) {

                            case 0:

                                highlightCard
                                        .setCardBackgroundColor(
                                                Color.parseColor(
                                                        "#E8F5E9"
                                                )
                                        );


                                txtHighlightIcon.setText(
                                        "🏆"
                                );


                                txtHighlightTitle.setText(getString(R.string.highest_stock));


                                txtHighlightValue.setText(
                                        highestCrop
                                                + " ("
                                                + highestQty
                                                + " KG)"
                                );

                                break;


                            case 1:

                                highlightCard
                                        .setCardBackgroundColor(
                                                Color.parseColor(
                                                        "#FFF8E1"
                                                )
                                        );


                                txtHighlightIcon.setText(
                                        "📉"
                                );


                                txtHighlightTitle.setText(getString(R.string.lowest_stock));


                                txtHighlightValue.setText(
                                        lowestCrop
                                                + " ("
                                                + lowestQty
                                                + " KG)"
                                );

                                break;


                            case 2:

                                highlightCard
                                        .setCardBackgroundColor(
                                                Color.parseColor(
                                                        "#E3F2FD"
                                                )
                                        );


                                txtHighlightIcon.setText(
                                        "🚀"
                                );


                                txtHighlightTitle.setText(getString(R.string.fast_moving));


                                txtHighlightValue.setText(
                                        soldCrop + " (" + soldQty + " " + getString(R.string.kg_sold) + ")"
                                );

                                break;


                            case 3:

                                highlightCard
                                        .setCardBackgroundColor(
                                                Color.parseColor(
                                                        "#FFEBEE"
                                                )
                                        );


                                txtHighlightIcon.setText(
                                        "❌"
                                );


                                txtHighlightTitle.setText(getString(R.string.out_of_stock));


                                if (outFound) {

                                    txtHighlightValue.setText(
                                            outCrop + " " + getString(R.string.is_out_of_stock)
                                    );

                                }

                                else {

                                    txtHighlightValue.setText(
                                            getString(R.string.all_crops_available)
                                    );
                                }

                                break;
                        }


                        index++;


                        if (index > 3) {

                            index = 0;
                        }


                        highlightHandler.postDelayed(
                                this,
                                3000
                        );
                    }
                };


        highlightHandler.postDelayed(
                highlightRunnable,
                3000
        );
    }


    // =========================================================
    // DESTROY
    // =========================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();


        if (highlightRunnable != null) {

            highlightHandler.removeCallbacks(
                    highlightRunnable
            );
        }
    }

    private void showHighlightLoading() {

        skeletonHighlight.setVisibility(View.VISIBLE);

        txtHighlightIcon.setVisibility(View.INVISIBLE);
        txtHighlightTitle.setVisibility(View.INVISIBLE);
        txtHighlightValue.setVisibility(View.INVISIBLE);
    }
    private void hideHighlightLoading() {

        skeletonHighlight.setVisibility(View.GONE);

        txtHighlightIcon.setVisibility(View.VISIBLE);
        txtHighlightTitle.setVisibility(View.VISIBLE);
        txtHighlightValue.setVisibility(View.VISIBLE);
    }
    private void showCropSkeleton() {

        skeletonCrop.setVisibility(View.VISIBLE);

        spCrop.setVisibility(View.INVISIBLE);
        spCrop.setEnabled(false);
    }
    private void hideCropSkeleton() {

        skeletonCrop.setVisibility(View.GONE);

        spCrop.setVisibility(View.VISIBLE);
        spCrop.setEnabled(true);
    }
}