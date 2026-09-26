package com.example.warevista;

import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.content.SharedPreferences;

public class ReportsActivity extends AppCompatActivity {

    private static final String BASE_URL =
            "https://script.google.com/macros/s/AKfycbxH_jxb8MUvSXeZQZAE9bbEGTojDKDoGD2-GMc3cWnn4EcI_fz42UCK4mTw2662HV14/exec";

    TextView txtPurchase, txtSales, txtProfit;
    TextView txtNoReport;
    TextView txtHighlightIcon;
    TextView txtHighlightTitle;
    TextView txtHighlightValue;

    Spinner spCrop;

    RecyclerView recyclerReport;

    ArrayList<ReportModel> reportList;
    ReportAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        txtPurchase = findViewById(R.id.txtPurchase);
        txtSales = findViewById(R.id.txtSales);
        txtProfit = findViewById(R.id.txtProfit);
        txtNoReport = findViewById(R.id.txtNoReport);


        txtHighlightIcon = findViewById(R.id.txtHighlightIcon);
        txtHighlightTitle = findViewById(R.id.txtHighlightTitle);
        txtHighlightValue = findViewById(R.id.txtHighlightValue);

        spCrop = findViewById(R.id.spCrop);



        recyclerReport = findViewById(R.id.recyclerReport);
        recyclerReport.setLayoutManager(new LinearLayoutManager(this));
        recyclerReport.setNestedScrollingEnabled(true);
        recyclerReport.setHasFixedSize(false);

        reportList = new ArrayList<>();
        adapter = new ReportAdapter(reportList);
        recyclerReport.setAdapter(adapter);

        loadSpinners();



       // loadReport();
    }

    private void loadSpinners() {

        String url = BASE_URL + "?module=filters";

        StringRequest request = new StringRequest(

                Request.Method.GET,

                url,

                response -> {

                    try {

                        JSONObject object = new JSONObject(response);

                        JSONArray cropArray = object.getJSONArray("crops");

                        ArrayList<String> cropList = new ArrayList<>();

                        for (int i = 0; i < cropArray.length(); i++) {

                            cropList.add(cropArray.getString(i));

                        }

                        ArrayAdapter<String> cropAdapter =
                                new ArrayAdapter<>(
                                        this,
                                        android.R.layout.simple_spinner_dropdown_item,
                                        cropList
                                );

                        spCrop.setAdapter(cropAdapter);
                        spCrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent,
                                                       android.view.View view,
                                                       int position,
                                                       long id) {

                                loadReport();

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        loadReport();

                    } catch (Exception e) {

                        e.printStackTrace();

                    }

                },

                error -> error.printStackTrace()

        );

        Volley.newRequestQueue(this).add(request);

    }


    private void loadReport() {

        String crop = "All";


        if (spCrop.getSelectedItem() != null) {
            crop = spCrop.getSelectedItem().toString();
        }

        SharedPreferences preferences =
                getSharedPreferences("WareVista", MODE_PRIVATE);

        String warehouse =
                preferences.getString("warehouse", "");

        String url = BASE_URL
                + "?module=reports"
                + "&warehouse=" + Uri.encode(warehouse)
                + "&crop=" + Uri.encode(crop);

        android.util.Log.d("REPORT_URL", url);

        StringRequest request = new StringRequest(

                Request.Method.GET,
                url,

                response -> {

                    try {

                        JSONObject object = new JSONObject(response);

                        txtPurchase.setText("₹" + object.getInt("totalPurchase"));
                        txtSales.setText("₹" + object.getInt("totalSales"));

                        int totalProfit = object.getInt("profit");

                        if (totalProfit >= 0) {

                            txtProfit.setText("+₹" + totalProfit);
                            txtProfit.setTextColor(Color.parseColor("#2E7D32"));

                        } else {

                            txtProfit.setText("-₹" + Math.abs(totalProfit));
                            txtProfit.setTextColor(Color.parseColor("#D32F2F"));

                        }

                        reportList.clear();

                        JSONArray array = object.getJSONArray("cropReport");
                        if (array.length() == 0) {

                            txtNoReport.setVisibility(View.VISIBLE);
                            recyclerReport.setVisibility(View.GONE);

                        } else {

                            txtNoReport.setVisibility(View.GONE);
                            recyclerReport.setVisibility(View.VISIBLE);

                        }

                        android.util.Log.d("REPORT", "JSON Count = " + array.length());

                        int highestPurchase = 0;
                        int highestSales = 0;

                        String highestPurchaseCrop = "";
                        String highestSalesCrop = "";

                        int maxProfit = Integer.MIN_VALUE;
                        int minProfit = Integer.MAX_VALUE;

                        String profitCrop = "";
                        String lossCrop = "";

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject item = array.getJSONObject(i);

                            android.util.Log.d("REPORT", item.getString("crop"));

                            ReportModel model = new ReportModel(

                                    item.getString("crop"),

                                    item.getInt("purchaseQty"),

                                    item.getInt("salesQty"),

                                    item.getInt("purchaseAmount"),

                                    item.getInt("salesAmount"),

                                    item.getInt("profit")

                            );

                            reportList.add(model);

                            if (model.getPurchaseQty() > highestPurchase) {

                                highestPurchase = model.getPurchaseQty();
                                highestPurchaseCrop = model.getCrop();

                            }

                            if (model.getSalesQty() > highestSales) {

                                highestSales = model.getSalesQty();
                                highestSalesCrop = model.getCrop();

                            }

                            if (model.getProfit() > maxProfit) {

                                maxProfit = model.getProfit();
                                profitCrop = model.getCrop();

                            }

                            if (model.getProfit() < minProfit) {

                                minProfit = model.getProfit();
                                lossCrop = model.getCrop();

                            }

                        }
                        android.util.Log.d("REPORT", "List Size = " + reportList.size());



                        adapter.notifyDataSetChanged();

                        recyclerReport.post(() -> {

                            ViewGroup.LayoutParams params = recyclerReport.getLayoutParams();

                            if (reportList.size() <= 2) {

                                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

                            } else {

                                int height = (int) (360 * getResources()
                                        .getDisplayMetrics()
                                        .density);

                                params.height = height;

                            }

                            recyclerReport.setLayoutParams(params);
                            recyclerReport.requestLayout();

                        });

                        startHighlights(

                                profitCrop,
                                maxProfit,

                                highestPurchaseCrop,
                                highestPurchase,

                                highestSalesCrop,
                                highestSales,

                                lossCrop,
                                minProfit

                        );



                    } catch (Exception e) {

                        e.printStackTrace();

                    }

                },

                error -> error.printStackTrace()

        );

        Volley.newRequestQueue(this).add(request);

    }
    private void startHighlights(
            String bestCrop,
            int bestProfit,
            String purchaseCrop,
            int purchaseQty,
            String salesCrop,
            int salesQty,
            String lossCrop,
            int lossAmount
    ) {

        Handler handler = new Handler();

        Runnable runnable = new Runnable() {

            int index = 0;

            @Override
            public void run() {

                Animation fadeOut = AnimationUtils.loadAnimation(
                        ReportsActivity.this,
                        android.R.anim.fade_out);

                Animation fadeIn = AnimationUtils.loadAnimation(
                        ReportsActivity.this,
                        android.R.anim.fade_in);

                txtHighlightIcon.startAnimation(fadeOut);
                txtHighlightTitle.startAnimation(fadeOut);
                txtHighlightValue.startAnimation(fadeOut);

                switch (index) {

                    case 0:

                        txtHighlightIcon.setText("🏆");
                        txtHighlightTitle.setText("Most Profit");
                        txtHighlightValue.setText(bestCrop + " (+₹" + bestProfit + ")");
                        break;

                    case 1:

                        txtHighlightIcon.setText("📦");
                        txtHighlightTitle.setText("Highest Purchase");
                        txtHighlightValue.setText(purchaseCrop + " (" + purchaseQty + " KG)");
                        break;

                    case 2:

                        txtHighlightIcon.setText("🛒");
                        txtHighlightTitle.setText("Highest Sales");
                        txtHighlightValue.setText(salesCrop + " (" + salesQty + " KG)");
                        break;

                    case 3:

                        txtHighlightIcon.setText("⚠");
                        txtHighlightTitle.setText("Needs Attention");

                        if (lossAmount < 0) {
                            txtHighlightValue.setText(lossCrop + " (-₹" + Math.abs(lossAmount) + ")");
                        } else {
                            txtHighlightValue.setText("No Loss");
                        }

                        break;
                }

                txtHighlightIcon.startAnimation(fadeIn);
                txtHighlightTitle.startAnimation(fadeIn);
                txtHighlightValue.startAnimation(fadeIn);

                index++;

                if (index > 3)
                    index = 0;

                handler.postDelayed(this, 2500);
            }
        };

        handler.post(runnable);
    }

}