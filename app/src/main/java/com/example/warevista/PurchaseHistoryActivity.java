package com.example.warevista;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class PurchaseHistoryActivity extends AppCompatActivity {

    private static final String BASE_URL =
            "https://script.google.com/macros/s/AKfycbxH_jxb8MUvSXeZQZAE9bbEGTojDKDoGD2-GMc3cWnn4EcI_fz42UCK4mTw2662HV14/exec";

    private RecyclerView recyclerHistory;

    private TextInputEditText etSearchHistory;
    private TextView txtSearchError;

    private Spinner spCropFilter;
    private Spinner spPaymentFilter;

    private EditText etDateFilter;
    private Button btnClearDate;

    private ArrayList<HistoryModel> historyList;
    private ArrayList<HistoryModel> filteredList;

    private HistoryAdapter adapter;

    private ImageView btnBack;

    // Skeleton + Actual Content
    private View skeletonLayout;
    private View contentLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_purchase_history);


        // =====================================================
        // SKELETON / CONTENT
        // =====================================================

        skeletonLayout = findViewById(R.id.skeletonLayout);
        contentLayout = findViewById(R.id.contentLayout);

        showSkeleton();


        // =====================================================
        // FIND VIEWS
        // =====================================================

        btnBack = findViewById(R.id.btnBack);

        recyclerHistory = findViewById(R.id.recyclerHistory);


        etSearchHistory = findViewById(R.id.etSearchHistory);

        txtSearchError = findViewById(R.id.txtSearchError);

        spCropFilter = findViewById(R.id.spCropFilter);

        spPaymentFilter = findViewById(R.id.spPaymentFilter);

        etDateFilter = findViewById(R.id.etDateFilter);

        btnClearDate = findViewById(R.id.btnClearDate);


        // =====================================================
        // BACK BUTTON
        // =====================================================

        btnBack.setOnClickListener(v -> finish());


        // =====================================================
        // RECYCLER VIEW
        // =====================================================

        recyclerHistory.setLayoutManager(
                new LinearLayoutManager(this)
        );


        historyList = new ArrayList<>();

        filteredList = new ArrayList<>();


        adapter = new HistoryAdapter(filteredList);

        recyclerHistory.setAdapter(adapter);


        // =====================================================
        // SEARCH
        // =====================================================

        setupSearch();


        // =====================================================
        // FILTER
        // =====================================================

        setupFilters();


        // =====================================================
        // DATE PICKER
        // =====================================================

        setupDatePicker();


        // =====================================================
        // CLEAR DATE
        // =====================================================

        btnClearDate.setOnClickListener(v -> {

            etDateFilter.setText("");

            applyFilter();

        });


        // =====================================================
        // LOAD DATA
        // =====================================================

        loadHistory();

    }


    // =========================================================
    // SHOW SKELETON
    // =========================================================

    private void showSkeleton() {

        if (skeletonLayout != null) {
            skeletonLayout.setVisibility(View.VISIBLE);
        }

        if (contentLayout != null) {
            contentLayout.setVisibility(View.GONE);
        }

    }


    // =========================================================
    // HIDE SKELETON
    // =========================================================

    private void hideSkeleton() {

        if (skeletonLayout != null) {
            skeletonLayout.setVisibility(View.GONE);
        }

        if (contentLayout != null) {
            contentLayout.setVisibility(View.VISIBLE);
        }

    }


    // =========================================================
    // LOAD PURCHASE HISTORY
    // =========================================================

    private void loadHistory() {

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


        String url = "";


        try {

            url = BASE_URL
                    + "?module=purchaseHistory"
                    + "&warehouse="
                    + URLEncoder.encode(
                    warehouse,
                    "UTF-8"
            );

        } catch (Exception e) {

            e.printStackTrace();

            hideSkeleton();

            return;
        }


        StringRequest request =
                new StringRequest(

                        Request.Method.GET,

                        url,

                        response -> {

                            try {

                                historyList.clear();

                                filteredList.clear();


                                JSONArray array =
                                        new JSONArray(response);


                                for (
                                        int i = 0;
                                        i < array.length();
                                        i++
                                ) {

                                    JSONObject object =
                                            array.getJSONObject(i);


                                    String apiDate =
                                            object.getString("date");


                                    String formattedDate;


                                    try {

                                        SimpleDateFormat input =
                                                new SimpleDateFormat(
                                                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                                        Locale.getDefault()
                                                );


                                        input.setTimeZone(
                                                TimeZone.getTimeZone("UTC")
                                        );


                                        SimpleDateFormat output =
                                                new SimpleDateFormat(
                                                        "d/M/yyyy",
                                                        Locale.getDefault()
                                                );


                                        formattedDate =
                                                output.format(
                                                        input.parse(apiDate)
                                                );


                                    } catch (Exception e) {

                                        formattedDate = apiDate;

                                    }


                                    HistoryModel model =
                                            new HistoryModel(

                                                    object.getString(
                                                            "purchaseId"
                                                    ),

                                                    object.getString(
                                                            "farmer"
                                                    ),

                                                    object.getString(
                                                            "crop"
                                                    ),

                                                    object.getString(
                                                            "quantity"
                                                    ),

                                                    object.getString(
                                                            "rate"
                                                    ),

                                                    object.getString(
                                                            "total"
                                                    ),

                                                    object.getString(
                                                            "payment"
                                                    ),

                                                    formattedDate

                                            );


                                    historyList.add(model);

                                }


                                filteredList.addAll(
                                        historyList
                                );


                                adapter.notifyDataSetChanged();


                                // Dropdown data
                                loadSpinnerData();


                                // IMPORTANT:
                                // Data + dropdown ready
                                hideSkeleton();


                            } catch (Exception e) {

                                e.printStackTrace();

                                hideSkeleton();

                            }

                        },


                        error -> {

                            error.printStackTrace();

                            hideSkeleton();

                        }

                );


        Volley
                .newRequestQueue(this)
                .add(request);

    }


    // =========================================================
    // LOAD DROPDOWN DATA
    // =========================================================

    private void loadSpinnerData() {

        ArrayList<String> cropList =
                new ArrayList<>();

        ArrayList<String> paymentList =
                new ArrayList<>();


        cropList.add("All Crop");

        paymentList.add("All Payment");


        for (HistoryModel model : historyList) {

            if (!cropList.contains(
                    model.getCrop()
            )) {

                cropList.add(
                        model.getCrop()
                );

            }


            if (!paymentList.contains(
                    model.getPayment()
            )) {

                paymentList.add(
                        model.getPayment()
                );

            }

        }


        // Crop Spinner

        ArrayAdapter<String> cropAdapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.spinner_item,
                        cropList
                );


        cropAdapter.setDropDownViewResource(
                R.layout.spinner_dropdown_item
        );


        spCropFilter.setAdapter(
                cropAdapter
        );


        // Payment Spinner

        ArrayAdapter<String> paymentAdapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.spinner_item,
                        paymentList
                );


        paymentAdapter.setDropDownViewResource(
                R.layout.spinner_dropdown_item
        );


        spPaymentFilter.setAdapter(
                paymentAdapter
        );

    }


    // =========================================================
    // SEARCH
    // =========================================================

    private void setupSearch() {

        etSearchHistory.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {

                    }


                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        applyFilter();

                    }


                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {

                    }

                }
        );

    }


    // =========================================================
    // FILTER LISTENER
    // =========================================================

    private void setupFilters() {

        android.widget.AdapterView
                .OnItemSelectedListener listener =

                new android.widget.AdapterView
                        .OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        applyFilter();

                    }


                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent
                    ) {

                    }

                };


        spCropFilter.setOnItemSelectedListener(
                listener
        );


        spPaymentFilter.setOnItemSelectedListener(
                listener
        );

    }


    // =========================================================
    // APPLY FILTER
    // =========================================================

    private void applyFilter() {

        if (historyList == null ||
                filteredList == null ||
                adapter == null) {

            return;

        }


        filteredList.clear();


        String seller =
                etSearchHistory
                        .getText()
                        .toString()
                        .trim()
                        .toLowerCase();


        String crop = "All Crop";


        if (spCropFilter.getSelectedItem() != null) {

            crop =
                    spCropFilter
                            .getSelectedItem()
                            .toString();

        }


        String payment = "All Payment";


        if (spPaymentFilter.getSelectedItem() != null) {

            payment =
                    spPaymentFilter
                            .getSelectedItem()
                            .toString();

        }


        String date =
                etDateFilter
                        .getText()
                        .toString()
                        .trim();


        boolean found = false;

        boolean invalidSearch = false;


        for (HistoryModel model : historyList) {


            // Check invalid search

            if (!seller.isEmpty()) {

                if (
                        model.getCrop()
                                .toLowerCase()
                                .contains(seller)

                                ||

                                model.getPayment()
                                        .toLowerCase()
                                        .contains(seller)

                                ||

                                model.getDate()
                                        .toLowerCase()
                                        .contains(seller)
                ) {

                    invalidSearch = true;

                }

            }


            boolean sellerMatch =
                    seller.isEmpty()
                            ||
                            model.getName()
                                    .toLowerCase()
                                    .contains(seller);


            boolean cropMatch =
                    crop.equals("All Crop")
                            ||
                            model.getCrop()
                                    .equals(crop);


            boolean paymentMatch =
                    payment.equals("All Payment")
                            ||
                            model.getPayment()
                                    .equals(payment);


            boolean dateMatch =
                    date.isEmpty()
                            ||
                            model.getDate()
                                    .trim()
                                    .equalsIgnoreCase(
                                            date.trim()
                                    );


            if (
                    sellerMatch
                            &&
                            cropMatch
                            &&
                            paymentMatch
                            &&
                            dateMatch
            ) {

                filteredList.add(model);

                found = true;

            }

        }


        // =====================================================
        // SEARCH ERROR
        // =====================================================

        if (seller.isEmpty()) {

            txtSearchError.setVisibility(
                    View.GONE
            );

        } else {

            if (found) {

                txtSearchError.setVisibility(
                        View.GONE
                );

            } else {

                if (invalidSearch) {

                    txtSearchError.setText(
                            "Search only by Supplier Name"
                    );

                } else {

                    txtSearchError.setText(
                            "No Supplier Found"
                    );

                }


                txtSearchError.setVisibility(
                        View.VISIBLE
                );

            }

        }


        adapter.notifyDataSetChanged();

    }


    // =========================================================
    // DATE PICKER
    // =========================================================

    private void setupDatePicker() {

        etDateFilter.setOnClickListener(
                v -> {

                    Calendar calendar =
                            Calendar.getInstance();


                    DatePickerDialog dialog =
                            new DatePickerDialog(

                                    PurchaseHistoryActivity.this,

                                    (view, year, month, day) -> {

                                        String selectedDate =
                                                day
                                                        + "/"
                                                        + (month + 1)
                                                        + "/"
                                                        + year;


                                        etDateFilter.setText(
                                                selectedDate
                                        );


                                        applyFilter();

                                    },

                                    calendar.get(
                                            Calendar.YEAR
                                    ),

                                    calendar.get(
                                            Calendar.MONTH
                                    ),

                                    calendar.get(
                                            Calendar.DAY_OF_MONTH
                                    )

                            );


                    dialog.show();

                }
        );

    }

}