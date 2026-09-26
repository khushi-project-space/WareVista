package com.example.warevista;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import android.text.Editable;
import android.text.TextWatcher;

public class SalesHistoryActivity extends AppCompatActivity {

    private static final String BASE_URL =
            "https://script.google.com/macros/s/AKfycbxH_jxb8MUvSXeZQZAE9bbEGTojDKDoGD2-GMc3cWnn4EcI_fz42UCK4mTw2662HV14/exec";

    private RecyclerView recyclerHistory;

    private TextInputEditText etSearchHistory;
    private TextView txtSearchError;

    private Spinner spCropFilter;
    private Spinner spPaymentFilter;

    private EditText etDateFilter;
    private Button btnClearDate;

    private ArrayList<SalesHistoryModel> historyList;
    private ArrayList<SalesHistoryModel> filteredList;

    private SalesHistoryAdapter adapter;

    private ImageView btnBack;

    // =========================
    // SKELETON VIEWS
    // =========================

    private View skeletonSearch;
    private View skeletonCrop;
    private View skeletonPayment;
    private View skeletonDate;
    private View skeletonClearDate;

    private LinearLayout skeletonHistoryContainer;

    // =========================
    // ACTUAL CONTENT CONTAINER
    // =========================

    private View searchCard;
    private View filterCard;
    private View dateCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sales_history);

        // =========================
        // NORMAL VIEWS
        // =========================

        btnBack = findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        recyclerHistory = findViewById(R.id.recyclerHistory);

        etSearchHistory = findViewById(R.id.etSearchHistory);

        txtSearchError = findViewById(R.id.txtSearchError);

        spCropFilter = findViewById(R.id.spCropFilter);

        spPaymentFilter = findViewById(R.id.spPaymentFilter);

        etDateFilter = findViewById(R.id.etDateFilter);

        btnClearDate = findViewById(R.id.btnClearDate);

        // =========================
        // SKELETON VIEWS
        // =========================

        skeletonSearch = findViewById(R.id.skeletonSearch);

        skeletonCrop = findViewById(R.id.skeletonCrop);

        skeletonPayment = findViewById(R.id.skeletonPayment);

        skeletonDate = findViewById(R.id.skeletonDate);

        skeletonClearDate = findViewById(R.id.skeletonClearDate);

        skeletonHistoryContainer =
                findViewById(R.id.skeletonHistoryContainer);

        // =========================
        // ACTUAL CARDS
        // =========================

        searchCard = findViewById(R.id.searchCard);

        filterCard = findViewById(R.id.filterCard);

        dateCard = findViewById(R.id.dateCard);

        // =========================
        // RECYCLER VIEW
        // =========================

        recyclerHistory.setLayoutManager(
                new LinearLayoutManager(this)
        );

        historyList = new ArrayList<>();

        filteredList = new ArrayList<>();

        adapter = new SalesHistoryAdapter(filteredList);

        recyclerHistory.setAdapter(adapter);

        // =========================
        // START SKELETON
        // =========================

        showSkeleton();

        // =========================
        // LOAD DATA
        // =========================

        loadHistory();

        setupSearch();

        setupFilters();

        setupDatePicker();

        btnClearDate.setOnClickListener(v -> {

            etDateFilter.setText("");

            applyFilter();

        });
    }

    // =========================================================
    // SHOW SKELETON
    // =========================================================

    private void showSkeleton() {

        if (searchCard != null)
            searchCard.setVisibility(View.GONE);

        if (filterCard != null)
            filterCard.setVisibility(View.GONE);

        if (dateCard != null)
            dateCard.setVisibility(View.GONE);

        recyclerHistory.setVisibility(View.GONE);

        if (txtSearchError != null)
            txtSearchError.setVisibility(View.GONE);


        if (skeletonSearch != null)
            skeletonSearch.setVisibility(View.VISIBLE);

        if (skeletonCrop != null)
            skeletonCrop.setVisibility(View.VISIBLE);

        if (skeletonPayment != null)
            skeletonPayment.setVisibility(View.VISIBLE);

        if (skeletonDate != null)
            skeletonDate.setVisibility(View.VISIBLE);

        if (skeletonClearDate != null)
            skeletonClearDate.setVisibility(View.VISIBLE);

        if (skeletonHistoryContainer != null)
            skeletonHistoryContainer.setVisibility(View.VISIBLE);
    }

    // =========================================================
    // HIDE SKELETON
    // =========================================================

    private void hideSkeleton() {

        if (skeletonSearch != null)
            skeletonSearch.setVisibility(View.GONE);

        if (skeletonCrop != null)
            skeletonCrop.setVisibility(View.GONE);

        if (skeletonPayment != null)
            skeletonPayment.setVisibility(View.GONE);

        if (skeletonDate != null)
            skeletonDate.setVisibility(View.GONE);

        if (skeletonClearDate != null)
            skeletonClearDate.setVisibility(View.GONE);

        if (skeletonHistoryContainer != null)
            skeletonHistoryContainer.setVisibility(View.GONE);


        if (searchCard != null)
            searchCard.setVisibility(View.VISIBLE);

        if (filterCard != null)
            filterCard.setVisibility(View.VISIBLE);

        if (dateCard != null)
            dateCard.setVisibility(View.VISIBLE);

        recyclerHistory.setVisibility(View.VISIBLE);
    }

    // =========================================================
    // LOAD HISTORY
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
                    + "?module=salesHistory"
                    + "&warehouse="
                    + URLEncoder.encode(
                    warehouse,
                    "UTF-8"
            );

        } catch (Exception e) {

            e.printStackTrace();

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

                                    SalesHistoryModel model =
                                            new SalesHistoryModel(

                                                    object.getString(
                                                            "salesId"
                                                    ),

                                                    object.getString(
                                                            "buyer"
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

                                loadSpinnerData();

                                // =========================
                                // DATA LOADED
                                // =========================

                                hideSkeleton();

                            } catch (Exception e) {

                                e.printStackTrace();

                                hideSkeleton();
                            }
                        },

                        error -> {

                            error.printStackTrace();

                            // Error આવે તો પણ skeleton હંમેશા
                            // screen પર અટકી ન રહે

                            hideSkeleton();
                        }
                );

        Volley
                .newRequestQueue(this)
                .add(request);
    }

    // =========================================================
    // SPINNER DATA
    // =========================================================

    private void loadSpinnerData() {

        ArrayList<String> cropList =
                new ArrayList<>();

        ArrayList<String> paymentList =
                new ArrayList<>();

        cropList.add("All Crop");

        paymentList.add("All Payment");

        for (SalesHistoryModel model : historyList) {

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
    // FILTER
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

        filteredList.clear();

        String buyer =
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

        for (SalesHistoryModel model :
                historyList) {

            if (!buyer.isEmpty()) {

                if (
                        model.getCrop()
                                .toLowerCase()
                                .contains(buyer)

                                ||

                                model.getPayment()
                                        .toLowerCase()
                                        .contains(buyer)

                                ||

                                model.getDate()
                                        .toLowerCase()
                                        .contains(buyer)
                ) {

                    invalidSearch = true;
                }
            }

            boolean buyerMatch =
                    buyer.isEmpty()
                            ||
                            model.getBuyer()
                                    .toLowerCase()
                                    .contains(buyer);

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
                    buyerMatch
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

        // =========================
        // SEARCH ERROR
        // =========================

        if (buyer.isEmpty()) {

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
                            "Search only by Buyer Name"
                    );

                } else {

                    txtSearchError.setText(
                            "No Buyer Found"
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

                                    SalesHistoryActivity.this,

                                    (view,
                                     year,
                                     month,
                                     day) -> {

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