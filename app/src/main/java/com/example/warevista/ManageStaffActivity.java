package com.example.warevista;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ManageStaffActivity extends AppCompatActivity
        implements ManageStaffAdapter.OnStaffActionListener {


    // ============================================================
    // API URL
    // ============================================================

    private static final String BASE_URL =
            "https://script.google.com/macros/s/AKfycbxH_jxb8MUvSXeZQZAE9bbEGTojDKDoGD2-GMc3cWnn4EcI_fz42UCK4mTw2662HV14/exec";


    // ============================================================
    // VIEWS
    // ============================================================

    private RecyclerView recyclerManageStaff;

    private SearchView searchStaff;

    private Spinner spWarehouseFilter;

    private ProgressBar progressStaff;



    private TextView txtResultCount;

    private TextView txtEmptyStaff;

    private ImageButton btnBack;


    // ============================================================
    // ADAPTER
    // ============================================================

    private ManageStaffAdapter adapter;


    // ============================================================
    // VOLLEY
    // ============================================================

    private RequestQueue requestQueue;


    // ============================================================
    // LISTS
    // ============================================================

    private final ArrayList<ManageStaffModel> allStaff =
            new ArrayList<>();

    private final ArrayList<ManageStaffModel> filteredStaff =
            new ArrayList<>();

    private final ArrayList<String> warehouseList =
            new ArrayList<>();


    private String selectedWarehouse =
            "All Warehouses";


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_manage_staff
        );


        initializeViews();

        requestQueue =
                Volley.newRequestQueue(this);

        setupRecyclerView();

        setupBackButton();

        setupSearch();

        setupWarehouseFilter();

        loadStaff();
    }


    // ============================================================
    // INITIALIZE
    // ============================================================

    private void initializeViews() {

        recyclerManageStaff =
                findViewById(
                        R.id.recyclerManageStaff
                );

        searchStaff =
                findViewById(
                        R.id.searchStaff
                );

        spWarehouseFilter =
                findViewById(
                        R.id.spWarehouseFilter
                );

        progressStaff =
                findViewById(
                        R.id.progressStaff
                );



        txtResultCount =
                findViewById(
                        R.id.txtResultCount
                );

        txtEmptyStaff =
                findViewById(
                        R.id.txtEmptyStaff
                );

        btnBack =
                findViewById(
                        R.id.btnBack
                );
    }


    // ============================================================
    // RECYCLER
    // ============================================================

    private void setupRecyclerView() {

        recyclerManageStaff.setLayoutManager(
                new LinearLayoutManager(this)
        );


        adapter =
                new ManageStaffAdapter(
                        new ArrayList<>(),
                        this
                );


        recyclerManageStaff.setAdapter(
                adapter
        );
    }


    // ============================================================
    // BACK
    // ============================================================

    private void setupBackButton() {

        btnBack.setOnClickListener(
                v -> finish()
        );


        getOnBackPressedDispatcher()
                .addCallback(
                        this,
                        new OnBackPressedCallback(true) {

                            @Override
                            public void handleOnBackPressed() {

                                finish();

                            }
                        }
                );
    }


    // ============================================================
    // SEARCH
    // ============================================================

    private void setupSearch() {

        searchStaff.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(
                            String query) {

                        filterStaff(query);

                        return true;
                    }


                    @Override
                    public boolean onQueryTextChange(
                            String newText) {

                        filterStaff(newText);

                        return true;
                    }

                }
        );
    }


    // ============================================================
    // WAREHOUSE FILTER
    // ============================================================

    private void setupWarehouseFilter() {

        spWarehouseFilter.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        if (
                                position >= 0 &&
                                        position <
                                                warehouseList.size()
                        ) {

                            selectedWarehouse =
                                    warehouseList.get(position);

                            filterStaff(
                                    searchStaff
                                            .getQuery()
                                            .toString()
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


    // ============================================================
    // LOAD STAFF
    // ============================================================

    private void loadStaff() {

        showLoading(true);


        String url =
                BASE_URL +
                        "?module=manageStaff&_=" +
                        System.currentTimeMillis();


        StringRequest request =
                new StringRequest(
                        Request.Method.GET,
                        url,

                        response -> {

                            showLoading(false);

                            parseStaffResponse(
                                    response
                            );
                        },

                        error -> {

                            showLoading(false);

                            Toast.makeText(
                                    this,
                                    "Unable to load staff",
                                    Toast.LENGTH_LONG
                            ).show();

                        }
                );


        request.setTag(
                "manage_staff"
        );


        requestQueue.add(request);
    }


    // ============================================================
    // PARSE RESPONSE
    // ============================================================

    private void parseStaffResponse(
            String response) {

        try {

            String cleanResponse =
                    response.trim();


            JSONObject json =
                    new JSONObject(
                            cleanResponse
                    );


            boolean success =
                    json.optBoolean(
                            "success",
                            false
                    );


            if (!success) {

                Toast.makeText(
                        this,
                        json.optString(
                                "message",
                                "Unable to load staff"
                        ),
                        Toast.LENGTH_LONG
                ).show();

                showNoStaff();

                return;
            }


            JSONArray staffArray =
                    json.optJSONArray(
                            "staffList"
                    );


            allStaff.clear();


            if (
                    staffArray != null &&
                            staffArray.length() > 0
            ) {

                for (
                        int i = 0;
                        i < staffArray.length();
                        i++
                ) {

                    JSONObject item =
                            staffArray
                                    .getJSONObject(i);


                    ManageStaffModel staff =
                            new ManageStaffModel();


                    staff.setUserId(
                            item.optString(
                                    "userId",
                                    ""
                            )
                    );


                    staff.setPassword(
                            item.optString(
                                    "password",
                                    ""
                            )
                    );


                    staff.setWarehouse(
                            item.optString(
                                    "warehouse",
                                    ""
                            )
                    );


                    staff.setRole(
                            item.optString(
                                    "role",
                                    "STAFF"
                            )
                    );


                    staff.setMobile(
                            item.optString(
                                    "mobile",
                                    ""
                            )
                    );


                    staff.setCreateDate(
                            item.optString(
                                    "createDate",
                                    ""
                            )
                    );


                    staff.setStaffName(
                            item.optString(
                                    "staffName",
                                    ""
                            )
                    );


                    staff.setRowNumber(
                            item.optInt(
                                    "rowNumber",
                                    0
                            )
                    );


                    allStaff.add(
                            staff
                    );
                }
            }


            createWarehouseFilter();


            filterStaff(
                    searchStaff
                            .getQuery()
                            .toString()
            );


        } catch (Exception e) {

            showNoStaff();


            Toast.makeText(
                    this,
                    "Response parsing error: " +
                            e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }


    // ============================================================
    // WAREHOUSE FILTER
    // ============================================================

    private void createWarehouseFilter() {

        Set<String> unique =
                new LinkedHashSet<>();


        unique.add(
                "All Warehouses"
        );


        for (
                ManageStaffModel staff :
                allStaff
        ) {

            String warehouse =
                    staff.getWarehouse();


            if (
                    warehouse != null &&
                            !warehouse.trim().isEmpty()
            ) {

                unique.add(
                        warehouse.trim()
                );
            }
        }


        warehouseList.clear();

        warehouseList.addAll(
                unique
        );


        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        warehouseList
                );


        spinnerAdapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item
        );


        spWarehouseFilter.setAdapter(
                spinnerAdapter
        );


        int position =
                warehouseList.indexOf(
                        selectedWarehouse
                );


        if (position < 0) {

            position = 0;

            selectedWarehouse =
                    "All Warehouses";
        }


        spWarehouseFilter.setSelection(
                position
        );
    }


    // ============================================================
    // FILTER
    // ============================================================

    private void filterStaff(
            String query) {

        String search =
                query == null
                        ? ""
                        : query
                        .trim()
                        .toLowerCase();


        filteredStaff.clear();


        for (
                ManageStaffModel staff :
                allStaff
        ) {

            String warehouse =
                    staff.getWarehouse() == null
                            ? ""
                            : staff
                            .getWarehouse()
                            .trim();


            boolean warehouseMatch =
                    selectedWarehouse
                            .equals(
                                    "All Warehouses"
                            )
                            ||
                            warehouse.equalsIgnoreCase(
                                    selectedWarehouse
                            );


            if (!warehouseMatch) {
                continue;
            }


            String name =
                    safeLower(
                            staff.getStaffName()
                    );

            String userId =
                    safeLower(
                            staff.getUserId()
                    );

            String mobile =
                    safeLower(
                            staff.getMobile()
                    );


            boolean searchMatch =
                    search.isEmpty()
                            ||
                            name.contains(search)
                            ||
                            userId.contains(search)
                            ||
                            mobile.contains(search);


            if (searchMatch) {

                filteredStaff.add(
                        staff
                );
            }
        }


        // IMPORTANT:
        // Adapter has its own list.
        adapter.updateList(
                filteredStaff
        );


        updateEmptyState();
    }


    // ============================================================
    // SAFE LOWER
    // ============================================================

    private String safeLower(
            String value) {

        if (value == null) {
            return "";
        }

        return value
                .trim()
                .toLowerCase();
    }


    // ============================================================
    // EMPTY STATE
    // ============================================================

    private void updateEmptyState() {

        int count =
                filteredStaff.size();


        txtResultCount.setText(
                "Showing " +
                        count +
                        " staff"
        );


        if (count == 0) {

            showNoStaff();

        } else {

            txtEmptyStaff.setVisibility(
                    View.GONE
            );

            recyclerManageStaff
                    .setVisibility(
                            View.VISIBLE
                    );
        }
    }


    private void showNoStaff() {

        txtEmptyStaff.setVisibility(
                View.VISIBLE
        );

        recyclerManageStaff.setVisibility(
                View.GONE
        );

        txtResultCount.setText(
                "Showing 0 staff"
        );
    }


    // ============================================================
    // EDIT
    // ============================================================

    @Override
    public void onEdit(
            ManageStaffModel staff) {

        showEditDialog(
                staff
        );
    }


    // ============================================================
    // DELETE
    // ============================================================

    @Override
    public void onDelete(
            ManageStaffModel staff) {

        new AlertDialog.Builder(this)

                .setTitle(
                        "Delete Staff"
                )

                .setMessage(
                        "Are you sure you want to delete " +
                                staff.getStaffName() +
                                "?"
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .setPositiveButton(
                        "Delete",
                        (dialog, which) ->
                                deleteStaff(
                                        staff
                                )
                )

                .show();
    }


    // ============================================================
    // EDIT DIALOG
    // ============================================================

    private void showEditDialog(
            ManageStaffModel staff) {

        View view =
                LayoutInflater.from(this)
                        .inflate(
                                R.layout.dialog_edit_staff,
                                null
                        );


        EditText etStaffName =
                view.findViewById(
                        R.id.etEditStaffName
                );

        EditText etStaffId =
                view.findViewById(
                        R.id.etEditStaffId
                );

        EditText etPassword =
                view.findViewById(
                        R.id.etEditPassword
                );

        EditText etWarehouse =
                view.findViewById(
                        R.id.etEditWarehouse
                );

        EditText etMobile =
                view.findViewById(
                        R.id.etEditMobile
                );

        EditText etCreateDate =
                view.findViewById(
                        R.id.etEditCreateDate
                );


        etStaffName.setText(
                staff.getStaffName()
        );

        etStaffId.setText(
                staff.getUserId()
        );

        etPassword.setText("");

        etWarehouse.setText(
                staff.getWarehouse()
        );

        etMobile.setText(
                staff.getMobile()
        );

        etCreateDate.setText(
                staff.getCreateDate()
        );


        AlertDialog dialog =
                new AlertDialog.Builder(this)

                        .setTitle(
                                "Edit Staff"
                        )

                        .setView(view)

                        .setNegativeButton(
                                "Cancel",
                                null
                        )

                        .setPositiveButton(
                                "Update",
                                null
                        )

                        .create();


        dialog.setOnShowListener(
                d -> {

                    Button update =
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE
                            );


                    update.setOnClickListener(
                            v -> {

                                String name =
                                        etStaffName
                                                .getText()
                                                .toString()
                                                .trim();

                                String id =
                                        etStaffId
                                                .getText()
                                                .toString()
                                                .trim();

                                String password =
                                        etPassword
                                                .getText()
                                                .toString()
                                                .trim();

                                String warehouse =
                                        etWarehouse
                                                .getText()
                                                .toString()
                                                .trim();

                                String mobile =
                                        etMobile
                                                .getText()
                                                .toString()
                                                .trim();

                                String date =
                                        etCreateDate
                                                .getText()
                                                .toString()
                                                .trim();


                                if (name.isEmpty()) {

                                    etStaffName.setError(
                                            "Enter staff name"
                                    );

                                    return;
                                }


                                if (
                                        !name.matches(
                                                "[A-Za-z ]+"
                                        )
                                ) {

                                    etStaffName.setError(
                                            "Letters only"
                                    );

                                    return;
                                }


                                if (id.isEmpty()) {

                                    etStaffId.setError(
                                            "Enter staff ID"
                                    );

                                    return;
                                }


                                if (
                                        !id.matches(
                                                "[A-Za-z0-9_]+"
                                        )
                                ) {

                                    etStaffId.setError(
                                            "Invalid Staff ID"
                                    );

                                    return;
                                }


                                if (warehouse.isEmpty()) {

                                    etWarehouse.setError(
                                            "Enter warehouse"
                                    );

                                    return;
                                }


                                if (
                                        !warehouse.matches(
                                                "[A-Za-z ]+"
                                        )
                                ) {

                                    etWarehouse.setError(
                                            "Letters only"
                                    );

                                    return;
                                }


                                if (
                                        !mobile.matches(
                                                "[0-9]{10}"
                                        )
                                ) {

                                    etMobile.setError(
                                            "Enter 10 digit mobile"
                                    );

                                    return;
                                }


                                if (
                                        !password.isEmpty() &&
                                                password.length() < 6
                                ) {

                                    etPassword.setError(
                                            "Minimum 6 characters"
                                    );

                                    return;
                                }


                                updateStaff(
                                        staff,
                                        name,
                                        id,
                                        password,
                                        warehouse,
                                        mobile,
                                        date,
                                        dialog
                                );
                            }
                    );
                }
        );


        dialog.show();
    }


    // ============================================================
    // UPDATE API
    // ============================================================

    private void updateStaff(
            ManageStaffModel oldStaff,
            String name,
            String id,
            String password,
            String warehouse,
            String mobile,
            String date,
            AlertDialog dialog) {


        StringRequest request =
                new StringRequest(
                        Request.Method.POST,
                        BASE_URL,

                        response -> {

                            try {

                                JSONObject json =
                                        new JSONObject(
                                                response
                                        );


                                boolean success =
                                        json.optBoolean(
                                                "success",
                                                false
                                        );


                                String message =
                                        json.optString(
                                                "message",
                                                "Update completed"
                                        );


                                Toast.makeText(
                                        this,
                                        message,
                                        Toast.LENGTH_SHORT
                                ).show();


                                if (success) {

                                    dialog.dismiss();

                                    loadStaff();
                                }

                            } catch (Exception e) {

                                Toast.makeText(
                                        this,
                                        "Invalid update response",
                                        Toast.LENGTH_LONG
                                ).show();
                            }

                        },

                        error -> {

                            Toast.makeText(
                                    this,
                                    "Update failed",
                                    Toast.LENGTH_LONG
                            ).show();

                        }
                ) {

                    @Override
                    protected Map<String, String>
                    getParams() {

                        Map<String, String> params =
                                new HashMap<>();


                        params.put(
                                "module",
                                "updateStaff"
                        );

                        params.put(
                                "oldUserId",
                                oldStaff.getUserId()
                        );

                        params.put(
                                "userId",
                                id
                        );

                        params.put(
                                "password",
                                password
                        );

                        params.put(
                                "warehouse",
                                warehouse
                        );

                        params.put(
                                "mobile",
                                mobile
                        );

                        params.put(
                                "createDate",
                                date
                        );

                        params.put(
                                "staffName",
                                name
                        );


                        return params;
                    }
                };


        requestQueue.add(
                request
        );
    }


    // ============================================================
    // DELETE API
    // ============================================================

    private void deleteStaff(
            ManageStaffModel staff) {


        StringRequest request =
                new StringRequest(
                        Request.Method.POST,
                        BASE_URL,

                        response -> {

                            try {

                                JSONObject json =
                                        new JSONObject(
                                                response
                                        );


                                boolean success =
                                        json.optBoolean(
                                                "success",
                                                false
                                        );


                                String message =
                                        json.optString(
                                                "message",
                                                "Delete completed"
                                        );


                                Toast.makeText(
                                        this,
                                        message,
                                        Toast.LENGTH_SHORT
                                ).show();


                                if (success) {

                                    loadStaff();
                                }

                            } catch (Exception e) {

                                Toast.makeText(
                                        this,
                                        "Invalid delete response",
                                        Toast.LENGTH_LONG
                                ).show();
                            }

                        },

                        error -> {

                            Toast.makeText(
                                    this,
                                    "Delete failed",
                                    Toast.LENGTH_LONG
                            ).show();

                        }
                ) {

                    @Override
                    protected Map<String, String>
                    getParams() {

                        Map<String, String> params =
                                new HashMap<>();


                        params.put(
                                "module",
                                "deleteStaff"
                        );

                        params.put(
                                "userId",
                                staff.getUserId()
                        );


                        return params;
                    }
                };


        requestQueue.add(
                request
        );
    }


    // ============================================================
    // LOADING
    // ============================================================

    private void showLoading(
            boolean loading) {

        progressStaff.setVisibility(
                loading
                        ? View.VISIBLE
                        : View.GONE
        );


        if (loading) {

            txtEmptyStaff.setVisibility(
                    View.GONE
            );

            recyclerManageStaff.setVisibility(
                    View.GONE
            );
        }
    }


    // ============================================================
    // DESTROY
    // ============================================================

    @Override
    protected void onDestroy() {

        if (requestQueue != null) {

            requestQueue.cancelAll(
                    "manage_staff"
            );
        }

        super.onDestroy();
    }
}