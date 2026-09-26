package com.example.warevista;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;

public class QrPaymentActivity extends AppCompatActivity {

    private ImageView imgQR;
    private TextView txtAmount;
    private Button btnPaymentDone;
    private Button btnCancel;

    private String paymentId = "";
    private String amount = "";

    // =========================================================
    // YOUR GOOGLE APPS SCRIPT URL
    // =========================================================

    private static final String API_URL =
            "https://script.google.com/macros/s/AKfycbxH_jxb8MUvSXeZQZAE9bbEGTojDKDoGD2-GMc3cWnn4EcI_fz42UCK4mTw2662HV14/exec";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qr_payment);

        imgQR = findViewById(R.id.imgQR);

        txtAmount = findViewById(R.id.txtQRAmount);

        btnPaymentDone =
                findViewById(R.id.btnPaymentDone);

        btnCancel =
                findViewById(R.id.btnCancelQR);


        // =====================================================
        // GET PAYMENT DATA
        // =====================================================

        paymentId =
                getIntent().getStringExtra("payment_id");

        if (paymentId == null) {
            paymentId =
                    getIntent().getStringExtra("qr_id");
        }

        amount =
                getIntent().getStringExtra("amount");


        if (paymentId == null) {
            paymentId = "";
        }

        if (amount == null) {
            amount = "0";
        }


        // =====================================================
        // SHOW AMOUNT
        // =====================================================

        txtAmount.setText(
                "Amount to Pay : ₹" + amount
        );


        // =====================================================
        // GET QR IMAGE
        // =====================================================

        String imageContent =
                getIntent().getStringExtra(
                        "image_content"
                );

        String imageUrl =
                getIntent().getStringExtra(
                        "image_url"
                );


        boolean imageLoaded = false;


        // =====================================================
        // LOAD BASE64 QR
        // =====================================================

        if (imageContent != null &&
                !imageContent.trim().isEmpty()) {

            try {

                String base64 = imageContent.trim();

                // Remove data:image/...;base64,
                if (base64.contains(",")) {

                    base64 =
                            base64.substring(
                                    base64.indexOf(",") + 1
                            );
                }


                byte[] decoded =
                        android.util.Base64.decode(
                                base64,
                                android.util.Base64.DEFAULT
                        );


                Bitmap bitmap =
                        BitmapFactory.decodeStream(
                                new ByteArrayInputStream(
                                        decoded
                                )
                        );


                if (bitmap != null) {

                    imgQR.setImageBitmap(bitmap);

                    imageLoaded = true;
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }


        // =====================================================
        // LOAD QR FROM URL
        // =====================================================

        if (!imageLoaded &&
                imageUrl != null &&
                !imageUrl.trim().isEmpty()) {

            loadImageFromUrl(imageUrl);
        }


        // =====================================================
        // PAYMENT DONE BUTTON
        // =====================================================

        btnPaymentDone.setOnClickListener(
                v -> checkPayment()
        );


        // =====================================================
        // CANCEL
        // =====================================================

        btnCancel.setOnClickListener(
                v -> {

                    setResult(
                            RESULT_CANCELED
                    );

                    finish();
                }
        );
    }


    // =========================================================
    // LOAD IMAGE FROM URL
    // =========================================================

    private void loadImageFromUrl(String url) {

        ImageRequest imageRequest =
                new ImageRequest(

                        url,

                        bitmap -> {

                            if (bitmap != null) {

                                imgQR.setImageBitmap(bitmap);
                            }
                        },

                        0,
                        0,

                        ImageView.ScaleType.FIT_CENTER,

                        Bitmap.Config.RGB_565,

                        error -> {

                            Toast.makeText(
                                    this,
                                    "Unable to load QR image",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );


        Volley
                .newRequestQueue(this)
                .add(imageRequest);
    }


    // =========================================================
    // CHECK DEMO PAYMENT
    // =========================================================

    private void checkPayment() {

        if (paymentId == null ||
                paymentId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Payment ID missing",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        btnPaymentDone.setEnabled(false);

        btnPaymentDone.setText(
                "Checking Payment..."
        );


        try {

            String encodedPaymentId =
                    URLEncoder.encode(
                            paymentId,
                            "UTF-8"
                    );


            String api =
                    API_URL
                            + "?module=checkDemoPaymentStatus"
                            + "&paymentId="
                            + encodedPaymentId;


            StringRequest request =
                    new StringRequest(

                            Request.Method.GET,

                            api,

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


                                    boolean paid =
                                            json.optBoolean(
                                                    "paid",
                                                    false
                                            );


                                    // =================================
                                    // PAYMENT SUCCESS
                                    // =================================

                                    if (success && paid) {

                                        String returnedPaymentId =
                                                json.optString(
                                                        "paymentId",
                                                        paymentId
                                                );


                                        android.content.Intent result =
                                                new android.content.Intent();


                                        result.putExtra(
                                                "payment_id",
                                                returnedPaymentId
                                        );


                                        result.putExtra(
                                                "paymentId",
                                                returnedPaymentId
                                        );


                                        result.putExtra(
                                                "amount",
                                                amount
                                        );


                                        setResult(
                                                RESULT_OK,
                                                result
                                        );


                                        Toast.makeText(
                                                this,
                                                "Payment Successful",
                                                Toast.LENGTH_SHORT
                                        ).show();


                                        finish();

                                    }

                                    // =================================
                                    // PAYMENT NOT SUCCESSFUL
                                    // =================================

                                    else {

                                        btnPaymentDone
                                                .setEnabled(true);

                                        btnPaymentDone
                                                .setText(
                                                        "I HAVE PAID"
                                                );


                                        Toast.makeText(
                                                this,
                                                "Payment not received yet. Please complete the demo payment and try again.",
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }

                                } catch (Exception e) {

                                    e.printStackTrace();

                                    btnPaymentDone
                                            .setEnabled(true);

                                    btnPaymentDone
                                            .setText(
                                                    "I HAVE PAID"
                                            );


                                    Toast.makeText(
                                            this,
                                            "Invalid payment response",
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            },

                            error -> {

                                btnPaymentDone
                                        .setEnabled(true);

                                btnPaymentDone
                                        .setText(
                                                "I HAVE PAID"
                                        );


                                Toast.makeText(
                                        this,
                                        "Unable to check payment",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                    );


            Volley
                    .newRequestQueue(this)
                    .add(request);


        } catch (Exception e) {

            e.printStackTrace();

            btnPaymentDone
                    .setEnabled(true);

            btnPaymentDone
                    .setText(
                            "I HAVE PAID"
                    );


            Toast.makeText(
                    this,
                    "Payment checking error",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}