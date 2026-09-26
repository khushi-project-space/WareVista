package com.example.warevista;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import android.widget.ImageView;

public class PurchaseActivity extends AppCompatActivity {


    private static final String API_URL =
            "https://script.google.com/macros/s/AKfycbxH_jxb8MUvSXeZQZAE9bbEGTojDKDoGD2-GMc3cWnn4EcI_fz42UCK4mTw2662HV14/exec";


    Spinner spCrop, spPaymentMethod;


    TextView txtOtherCrop;


    EditText etFarmerName,
            etMobile,
            etOtherCrop,
            etQuantity,
            etRate,
            etTotal,
            etDate,
            etRemarks;


    Button btnSavePurchase;


    boolean isSaving = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        LanguageManager.applySavedLanguage(this);

        setContentView(R.layout.activity_purchase);



        // FIND VIEWS


        spCrop = findViewById(R.id.spCrop);

        spPaymentMethod = findViewById(R.id.spPaymentMethod);


        txtOtherCrop = findViewById(R.id.txtOtherCrop);



        etFarmerName = findViewById(R.id.etFarmerName);

        etMobile = findViewById(R.id.etMobile);

        etOtherCrop = findViewById(R.id.etOtherCrop);


        etQuantity = findViewById(R.id.etQuantity);

        etRate = findViewById(R.id.etRate);

        etTotal = findViewById(R.id.etTotal);



        etDate = findViewById(R.id.etDate);

        etRemarks = findViewById(R.id.etRemarks);



        btnSavePurchase =
                findViewById(R.id.btnSavePurchase);



        loadCropSpinner();


        loadPaymentSpinner();


        showCurrentDate();


        otherCropLogic();


        liveValidation();


        calculateTotalLogic();



        btnSavePurchase.setOnClickListener(v -> {

            if (isSaving) {
                return;
            }

            if (!validateForm()) {
                return;
            }

            isSaving = true;

            // Disable button immediately

            btnSavePurchase.setClickable(false);


            savePurchase();

        });

        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            finish();
        });



    }
    // ==========================
    // Crop Spinner
    // ==========================

    private void loadCropSpinner(){

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.crop_list,
                        R.layout.spinner_item
                );

        adapter.setDropDownViewResource(
                R.layout.spinner_dropdown_item
        );
        spCrop.setAdapter(adapter);
    }



    // ==========================
    // Payment Spinner
    // ==========================


    private void loadPaymentSpinner(){


        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.payment_method_list,
                        R.layout.spinner_item
                );


        adapter.setDropDownViewResource(
                R.layout.spinner_item
        );

        spPaymentMethod.setAdapter(adapter);


    }





    // ==========================
    // Other Crop Logic
    // ==========================

    private void otherCropLogic(){


        spCrop.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {


                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view,
                                               int position,
                                               long id) {


                        String crop =
                                parent.getItemAtPosition(position).toString();



                        if(crop.equals("Other (अन्य)")){


                            txtOtherCrop.setVisibility(View.VISIBLE);

                            etOtherCrop.setVisibility(View.VISIBLE);


                        }
                        else{


                            txtOtherCrop.setVisibility(View.GONE);

                            etOtherCrop.setVisibility(View.GONE);


                        }


                    }



                    @Override
                    public void onNothingSelected(AdapterView<?> parent){

                    }


                });


    }






    // ==========================
    // LIVE VALIDATION
    // ==========================


    private void liveValidation(){



        // Farmer Name


        etFarmerName.addTextChangedListener(new TextWatcher() {


            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after){}



            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count){



                String value =
                        s.toString().trim();



                if(value.isEmpty()){


                    etFarmerName.setError(
                            getString(R.string.enter_farmer_name)
                    );


                }
                else if(!value.matches("[a-zA-Z ]+")){


                    etFarmerName.setError(
                            getString(R.string.only_alphabets_allowed)
                    );


                }
                else{


                    etFarmerName.setError(null);


                }


            }



            public void afterTextChanged(Editable s){}


        });






        // Mobile Number


        etMobile.addTextChangedListener(new TextWatcher() {


            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after){}




            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count){



                String mobile =
                        s.toString().trim();



                if(mobile.isEmpty()){


                    etMobile.setError(
                            getString(R.string.enter_mobile)
                    );


                }

                else if(!mobile.matches("[0-9]{10}")){


                    etMobile.setError(
                            getString(R.string.valid_10_digit_number)
                    );


                }

                else{


                    etMobile.setError(null);


                }


            }



            public void afterTextChanged(Editable s){}



        });







        // Other Crop


        etOtherCrop.addTextChangedListener(new TextWatcher() {


            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after){}



            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count){



                if(etOtherCrop.getVisibility()!=View.VISIBLE){

                    return;

                }



                String value =
                        s.toString().trim();



                if(value.isEmpty()){


                    etOtherCrop.setError(
                            getString(R.string.enter_crop_name)
                    );


                }

                else if(!value.matches("[a-zA-Z ]+")){


                    etOtherCrop.setError(
                            getString(R.string.only_alphabets_allowed)
                    );


                }

                else{


                    etOtherCrop.setError(null);


                }


            }



            public void afterTextChanged(Editable s){}



        });








        // Quantity


        etQuantity.addTextChangedListener(new TextWatcher() {


            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after){}



            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count){



                String qty =
                        s.toString();



                if(qty.isEmpty()){


                    etQuantity.setError(
                            getString(R.string.quantity_required)
                    );


                }

                else if(Integer.parseInt(qty)>500){


                    etQuantity.setError(
                            getString(R.string.maximum_500_kg)
                    );


                }

                else{


                    etQuantity.setError(null);


                }


            }



            public void afterTextChanged(Editable s){}


        });







        // Rate


        etRate.addTextChangedListener(new TextWatcher() {


            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after){}



            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count){



                String rate =
                        s.toString();



                if(rate.isEmpty()){


                    etRate.setError(
                            getString(R.string.enter_rate)
                    );


                }

                else if(Integer.parseInt(rate)>1000){


                    etRate.setError(
                            getString(R.string.maximum_1000)
                    );


                }

                else{


                    etRate.setError(null);


                }


            }



            public void afterTextChanged(Editable s){}


        });



    }






    // ==========================
    // TOTAL CALCULATION
    // ==========================


    private void calculateTotalLogic(){


        TextWatcher watcher =
                new TextWatcher() {


                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after){}



                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count){


                        calculateTotal();


                    }



                    public void afterTextChanged(Editable s){}


                };



        etQuantity.addTextChangedListener(watcher);

        etRate.addTextChangedListener(watcher);



    }



    private void calculateTotal(){


        String qty =
                etQuantity.getText().toString();



        String rate =
                etRate.getText().toString();




        if(!qty.isEmpty() && !rate.isEmpty()){


            try{


                int total =
                        Integer.parseInt(qty)
                                *
                                Integer.parseInt(rate);



                etTotal.setText(
                        String.valueOf(total)
                );


            }
            catch(Exception e){


                etTotal.setText("");

            }


        }
        else{


            etTotal.setText("");


        }


    }
    // ==========================
    // DATE
    // ==========================

    private void showCurrentDate(){


        String date =
                new SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                ).format(new Date());


        etDate.setText(date);


    }






    // ==========================
    // FORM VALIDATION
    // ==========================


    private boolean validateForm(){



        String name =
                etFarmerName.getText()
                        .toString()
                        .trim();



        if(name.isEmpty()){


            etFarmerName.setError(
                    getString(R.string.enter_farmer_name)
            );

            etFarmerName.requestFocus();

            return false;


        }



        if(!name.matches("[a-zA-Z ]+")){


            etFarmerName.setError(
                    getString(R.string.only_alphabets_allowed)
            );

            etFarmerName.requestFocus();

            return false;


        }







        String mobile =
                etMobile.getText()
                        .toString()
                        .trim();



        if(!mobile.matches("[0-9]{10}")){


            etMobile.setError(
                    getString(R.string.valid_10_digit_number)
            );


            etMobile.requestFocus();


            return false;


        }








        // Crop Check


        if(spCrop.getSelectedItemPosition()==0){



            Toast.makeText(
                    this,
                    getString(R.string.please_select_crop),
                    Toast.LENGTH_SHORT
            ).show();



            return false;


        }








        // Other Crop Check


        if(txtOtherCrop.getVisibility()==View.VISIBLE){



            String other =
                    etOtherCrop.getText()
                            .toString()
                            .trim();




            if(other.isEmpty()){


                etOtherCrop.setError(
                        getString(R.string.enter_crop_name)
                );


                etOtherCrop.requestFocus();


                return false;


            }





            if(!other.matches("[a-zA-Z ]+")){


                etOtherCrop.setError(
                        getString(R.string.only_alphabets_allowed)
                );


                etOtherCrop.requestFocus();


                return false;


            }



        }









        // Quantity


        String qty =
                etQuantity.getText()
                        .toString()
                        .trim();




        if(qty.isEmpty()){


            etQuantity.setError(
                    "Enter Quantity"
            );


            etQuantity.requestFocus();


            return false;


        }




        int quantity =
                Integer.parseInt(qty);




        if(quantity < 1 || quantity > 500){


            etQuantity.setError(
                    "Quantity 1-500 KG allowed"
            );


            etQuantity.requestFocus();


            return false;


        }










        // Rate


        String rate =
                etRate.getText()
                        .toString()
                        .trim();




        if(rate.isEmpty()){


            etRate.setError(
                    "Enter Rate"
            );


            etRate.requestFocus();


            return false;


        }





        int price =
                Integer.parseInt(rate);




        if(price < 1 || price > 1000){



            etRate.setError(
                    getString(R.string.rate_1_1000)
            );


            etRate.requestFocus();


            return false;


        }









        // Payment


        if(spPaymentMethod.getSelectedItemPosition()==0){



            Toast.makeText(
                    this,
                    getString(R.string.please_select_payment),
                    Toast.LENGTH_SHORT
            ).show();



            return false;


        }







        return true;



    }
    // ==========================
    // SAVE PURCHASE TO SHEET
    // ==========================


    private void savePurchase(){


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



        btnSavePurchase.setEnabled(false);


        btnSavePurchase.setText(
                getString(R.string.saving)
        );





        StringRequest request =
                new StringRequest(


                        Request.Method.POST,


                        API_URL,



                        response -> {

                            isSaving = false;

                            btnSavePurchase.setEnabled(true);
                            btnSavePurchase.setClickable(true);
                            btnSavePurchase.setText(
                                    getString(R.string.save_purchase)
                            );

                            Intent intent =
                                    new Intent(
                                            PurchaseActivity.this,
                                            SuccessActivity.class
                                    );

                            intent.putExtra(
                                    "message",
                                    getString(R.string.purchase_saved_successfully)
                            );

                            startActivity(intent);

                            finish();

                        },


                        error -> {

                            isSaving = false;

                            btnSavePurchase.setEnabled(true);
                            btnSavePurchase.setClickable(true);
                            btnSavePurchase.setText(
                                    getString(R.string.save_purchase)
                            );

                            Toast.makeText(
                                    PurchaseActivity.this,
                                    getString(R.string.server_error),
                                    Toast.LENGTH_LONG
                            ).show();

                        }



                ){



                    @Override
                    protected Map<String,String> getParams(){


                        Map<String,String> params =
                                new HashMap<>();



                        params.put(
                                "module",
                                "purchase"
                        );



                        params.put(
                                "warehouse",
                                warehouse
                        );



                        params.put(
                                "farmer",
                                etFarmerName.getText()
                                        .toString()
                                        .trim()
                        );



                        params.put(
                                "mobile",
                                etMobile.getText()
                                        .toString()
                                        .trim()
                        );





                        String crop =
                                spCrop.getSelectedItem()
                                        .toString();



                        if(crop.equals("Other (अन्य)")){


                            crop =
                                    etOtherCrop.getText()
                                            .toString()
                                            .trim();


                        }





                        params.put(
                                "crop",
                                crop
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
                        );



                        params.put(
                                "date",
                                etDate.getText()
                                        .toString()
                        );



                        params.put(
                                "remarks",
                                etRemarks.getText()
                                        .toString()
                                        .trim()
                        );



                        return params;


                    }



                };




        RequestQueue queue =
                Volley.newRequestQueue(this);



        queue.add(request);



    }






    // ==========================
    // CLEAR FORM
    // ==========================


    private void clearForm(){


        etFarmerName.setText("");

        etMobile.setText("");



        spCrop.setSelection(0);



        etOtherCrop.setText("");

        etOtherCrop.setVisibility(View.GONE);

        txtOtherCrop.setVisibility(View.GONE);




        etQuantity.setText("");

        etRate.setText("");

        etTotal.setText("");



        spPaymentMethod.setSelection(0);



        etRemarks.setText("");



        showCurrentDate();



    }



}