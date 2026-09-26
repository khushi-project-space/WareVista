package com.example.warevista;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class SuccessActivity extends AppCompatActivity {

    private LottieAnimationView lottieSuccess;
    private TextView txtMessage;
    private TextView txtSubMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_success);

        // ============================================================
        // FIND VIEWS
        // ============================================================

        lottieSuccess =
                findViewById(R.id.lottieSuccess);

        txtMessage =
                findViewById(R.id.txtMessage);

        txtSubMessage =
                findViewById(R.id.txtSubMessage);


        // ============================================================
        // GET TYPE
        // ============================================================

        String type =
                getIntent().getStringExtra("type");


        // ============================================================
        // SALES
        // ============================================================

        if ("sales".equals(type)) {

            lottieSuccess.setAnimation(
                    R.raw.sales_success
            );

            txtSubMessage.setText(
                    "Stock Reduced Successfully"
            );

        }


        // ============================================================
        // WAREHOUSE
        // ============================================================

        else if ("warehouse".equals(type)) {

            lottieSuccess.setAnimation(
                    R.raw.warehouse_created
            );

            txtSubMessage.setText(
                    "Staff Account Created Successfully"
            );

        }


        // ============================================================
        // PURCHASE
        // ============================================================

        else {

            lottieSuccess.setAnimation(
                    R.raw.warehouse_success
            );

            txtSubMessage.setText(
                    "Stock Added Successfully"
            );

        }


        // ============================================================
        // MAIN MESSAGE
        // ============================================================

        String message =
                getIntent().getStringExtra("message");

        if (message != null &&
                !message.trim().isEmpty()) {

            txtMessage.setText(message);

        }


        // ============================================================
        // PLAY ANIMATION
        // ============================================================

        lottieSuccess.playAnimation();


        // ========================================================
// GO TO CORRECT DASHBOARD
// ========================================================

        new Handler().postDelayed(() -> {

            Intent intent;


            // ====================================================
            // WAREHOUSE
            // ALWAYS GO TO ADMIN DASHBOARD
            // ====================================================

            if ("warehouse".equals(type)) {

                intent = new Intent(
                        SuccessActivity.this,
                        AdminDashboardActivity.class
                );

            }

            else {

                SharedPreferences preferences =
                        getSharedPreferences(
                                "WareVista",
                                MODE_PRIVATE
                        );


                String role =
                        preferences.getString(
                                "role",
                                "STAFF"
                        );


                if (role == null) {

                    role = "STAFF";

                }


                role = role.trim().toUpperCase();


                // ====================================================
                // ADMIN
                // ====================================================

                if ("ADMIN".equals(role)) {

                    intent = new Intent(
                            SuccessActivity.this,
                            AdminDashboardActivity.class
                    );

                }


                // ====================================================
                // STAFF
                // ====================================================

                else {

                    intent = new Intent(
                            SuccessActivity.this,
                            HomeActivity.class
                    );

                }

            }


            // ====================================================
            // CLEAR BACK STACK
            // ====================================================

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK
            );


            startActivity(intent);

            finish();


        }, 3000);
    }
}