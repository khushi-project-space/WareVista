package com.example.warevista;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

public class AskWareVistaActivity extends AppCompatActivity {
    private TextToSpeech textToSpeech;

    private EditText edtQuestion;
    private TextView txtAnswer;
    private static final int VOICE_REQUEST_CODE = 1001;
    private static final String API_URL =
            "https://script.google.com/macros/s/AKfycbxH_jxb8MUvSXeZQZAE9bbEGTojDKDoGD2-GMc3cWnn4EcI_fz42UCK4mTw2662HV14/exec";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ask_ware_vista);
        textToSpeech = new TextToSpeech(
                this,
                status -> {

                    if (status == TextToSpeech.SUCCESS) {

                        textToSpeech.setLanguage(
                                Locale.ENGLISH
                        );

                    }

                }
        );

        edtQuestion = findViewById(R.id.edtQuestion);
        txtAnswer = findViewById(R.id.txtAnswer);

        findViewById(R.id.btnAsk).setOnClickListener(v -> {

            String question =
                    edtQuestion.getText().toString().trim();

            if (question.isEmpty()) {

                Toast.makeText(
                        AskWareVistaActivity.this,
                        "Please enter your question",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            askWareVista(question);
        });
        findViewById(R.id.btnVoice).setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
                    );

            intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            );
            intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    "gu-IN"
            );


            intent.putExtra(
                    RecognizerIntent.EXTRA_PROMPT,
                    "Ask WareVista"
            );

            try {

                startActivityForResult(
                        intent,
                        VOICE_REQUEST_CODE
                );

            } catch (Exception e) {

                Toast.makeText(
                        AskWareVistaActivity.this,
                        "Voice input is not available",
                        Toast.LENGTH_SHORT
                ).show();

            }

        });
    }
    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {
        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (
                requestCode == VOICE_REQUEST_CODE &&
                        resultCode == RESULT_OK &&
                        data != null
        ) {

            java.util.ArrayList<String> results =
                    data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS
                    );

            if (
                    results != null &&
                            !results.isEmpty()
            ) {

                String voiceText =
                        results.get(0);

                String convertedText =
                        convertGujaratiToWareVista(
                                voiceText
                        );

                edtQuestion.setText(
                        convertedText
                );

                edtQuestion.setSelection(
                        edtQuestion.getText().length()
                );
                askWareVista(convertedText);
            }
        }
    }
    private String convertGujaratiToWareVista(String text) {

        String result = text;

        result = result

                // =========================
                // WAREHOUSES
                // =========================

                .replace("અમદાવાદ", "Ahmedabad")
                .replace("અમદવાદ", "Ahmedabad")

                .replace("સુરત", "Surat")

                .replace("વડોદરા", "Vadodara")
                .replace("બરોડા", "Vadodara")

                .replace("રાજકોટ", "Rajkot")

                .replace("ભાવનગર", "Bhavnagar")

                .replace("જામનગર", "Jamnagar")

                .replace("નડિયાદ", "Nadiad")


                // =========================
                // CROPS
                // =========================
                // Potato
                .replace("બટાકા", "Potato")
                .replace("બટાકો", "Potato")
                .replace("પોટેટો", "Potato")

                // Wheat
                .replace("ઘઉં", "Wheat")
                .replace("ઘઉ", "Wheat")
                .replace("વ્હીટ", "Wheat")
                .replace("વીટ", "Wheat")
                .replace("વિટ", "Wheat")

                // Rice
                .replace("ચોખા", "Rice")
                .replace("ચોખો", "Rice")
                .replace("રાઈસ", "Rice")
                .replace("રાઇસ", "Rice")

                // Corn
                .replace("મકાઈ", "Corn")
                .replace("મકાઇ", "Corn")
                .replace("કોર્ન", "Corn")

                // Millet
                .replace("બાજરી", "Millet")
                .replace("મિલેટ", "Millet")

                // Paddy
                .replace("ધાન", "Paddy")
                .replace("પેડી", "Paddy")
                .replace("પેડ્ડી", "Paddy")

                // Jowar
                .replace("જુવાર", "Jowar")
                .replace("જવાર", "Jowar")
                .replace("જોઅર", "Jowar")

                // Cotton
                .replace("કપાસ", "Cotton")
                .replace("કોટન", "Cotton")


                // =========================
                // PURCHASE
                // =========================

                .replace("ખરીદી", "purchase")
                .replace("ખરીદ", "purchase")
                .replace("ખરીદેલ", "purchased")
                .replace("ખરીદેલું", "purchased")
                .replace("ખરીદ્યું", "purchased")
                .replace("ખરીદ્યુ", "purchased")
                .replace("ખરીદવામાં", "purchase")
                .replace("ખરીદવા", "purchase")
                .replace("ખરીદવાની", "purchase")
                .replace("ખરીદવાનો", "purchase")
                .replace("ખરીદેલી", "purchased")
                .replace("ખરીદેલા", "purchased")

                // English pronunciation in Gujarati
                .replace("પરચેઝ", "purchase")
                .replace("પરચેસ", "purchase")
                .replace("પરચેજ", "purchase")


                // =========================
                // SALES
                // =========================

                .replace("વેચાણ", "sales")
                .replace("વેચાણનું", "sales")
                .replace("વેચાણની", "sales")
                .replace("વેચાણનો", "sales")
                .replace("વેચાણના", "sales")

                .replace("વેચ્યું", "sold")
                .replace("વેચ્યુ", "sold")
                .replace("વેચેલા", "sold")
                .replace("વેચેલું", "sold")

                // English pronunciation in Gujarati
                .replace("સેલ્સ", "sales")
                .replace("સેલ", "sales")


                // =========================
                // STOCK
                // =========================

                .replace("સ્ટોક", "stock")
                .replace("સ્ટૉક", "stock")

                .replace("જથ્થો", "stock")
                .replace("માલનો જથ્થો", "stock")

                .replace("ઉપલબ્ધ સ્ટોક", "available stock")
                .replace("કુલ સ્ટોક", "total stock")
                .replace("વર્તમાન સ્ટોક", "current stock")


                // =========================
                // TOTAL
                // =========================

                .replace("કુલ", "total")
                .replace("ટોટલ", "total")


                // =========================
                // TODAY
                // =========================

                .replace("આજે", "today")
                .replace("આજ", "today")
                .replace("આજનું", "today")
                .replace("આજની", "today")
                .replace("આજનો", "today")
                .replace("આજના", "today")
                .replace("ટુડે", "today")
                .replace("ટુડેએ", "today")


                // =========================
                // CURRENT / AVAILABLE
                // =========================

                .replace("વર્તમાન", "current")
                .replace("હાલનો", "current")
                .replace("હાલનું", "current")
                .replace("હાલની", "current")
                .replace("હાલમાં", "currently")
                .replace("હાલ", "current")
                .replace("હમણાં", "currently")

                .replace("ઉપલબ્ધ", "available")
                .replace("બાકી", "remaining")


                // =========================
                // COMPARISON
                // =========================

                .replace("સરખામણી કરો", "compare")
                .replace("સરખામણી", "compare")
                .replace("સરખાવો", "compare")
                .replace("સરખાવવું", "compare")

                .replace("તુલના કરો", "compare")
                .replace("તુલના", "compare")

                .replace("કમ્પેર કરો", "compare")
                .replace("કમ્પેર", "compare")
                .replace("કોમ્પેર", "compare")


                // =========================
                // QUESTION WORDS
                // =========================

                .replace("કેટલો છે", "ketlo che")
                .replace("કેટલું છે", "ketlu che")
                .replace("કેટલા છે", "ketla che")
                .replace("કેટલી છે", "ketli che")

                .replace("કેટલો", "ketlo")
                .replace("કેટલું", "ketlu")
                .replace("કેટલા", "ketla")
                .replace("કેટલી", "ketli")

                .replace("કેટલું થયું", "ketlu thayu")
                .replace("કેટલો થયો", "ketlo thayo")
                .replace("કેટલી થઈ", "ketli thai")
                .replace("કેટલી થઇ", "ketli thai")


                // =========================
                // QUANTITY / AMOUNT
                // =========================

                .replace("જથ્થો કેટલો", "quantity ketlo")
                .replace("કેટલો જથ્થો", "quantity ketlo")

                .replace("રકમ કેટલી", "amount ketli")
                .replace("કેટલી રકમ", "amount ketli")

                .replace("પૈસા કેટલા", "amount ketla")

                .replace("કુલ રકમ", "total amount")


                // =========================
                // RELATION WORDS
                // =========================
                .replace("અને", "ane")
                .replace("એન્ડ", "ane")
                .replace("and", "ane")

                .replace("માં", "ma")
                .replace("માટે", "mate")

                .replace("નો", "no")
                .replace("ની", "ni")
                .replace("નું", "nu")
                .replace("ના", "na")

                .replace("અને", "ane")
                .replace("and", "ane")
                .replace("અથવા", "athva")
                .replace("થી", "thi")
                .replace("વચ્ચે", "between")


                // =========================
                // COMMON WORDS
                // =========================

                .replace("છે", "che")

                .replace("હતું", "hatu")
                .replace("હતી", "hati")
                .replace("હતા", "hata")

                .replace("થયું", "thayu")
                .replace("થયુ", "thayu")
                .replace("થઈ", "thai")
                .replace("થઇ", "thai")
                .replace("થયો", "thayo")
                .replace("થયા", "thaya")

                .replace("મને", "mane")
                .replace("મારો", "maro")
                .replace("મારી", "mari")
                .replace("મારું", "maru")
                .replace("મારા", "mara")

                .replace("કહો", "kaho")
                .replace("બતાવો", "batavo")

                .replace("કરો", "karo")
                .replace("કરવું", "karvu")
                .replace("કર્યું", "karyu")
                .replace("કર્યુ", "karyu");

        return result;
    }
    private void askWareVista(String question) {

        txtAnswer.setText("Thinking...");


        StringRequest request = new StringRequest(
                Request.Method.POST,
                API_URL,

                response -> {

                    try {

                        JSONObject json =
                                new JSONObject(response);

                        boolean success =
                                json.optBoolean("success", false);

                        if (success) {

                            String answer =
                                    json.optString(
                                            "answer",
                                            "No answer"
                                    );
                            speakAnswer(answer);
                            txtAnswer.setText(answer);

                        } else {

                            txtAnswer.setText(
                                    json.optString(
                                            "message",
                                            "Something went wrong"
                                    )
                            );
                        }

                    } catch (Exception e) {

                        txtAnswer.setText(
                                "Invalid response from server"
                        );
                    }

                },

                error -> {

                    txtAnswer.setText(
                            "Server Error: " +
                                    (error.getMessage() != null
                                            ? error.getMessage()
                                            : error.toString())
                    );

                }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params =
                        new HashMap<>();

                params.put("module", "askWareVista");
                params.put("question", question);

                return params;
            }
        };


        RequestQueue queue =
                Volley.newRequestQueue(this);

        request.setRetryPolicy(
                new com.android.volley.DefaultRetryPolicy(
                        30000,
                        1,
                        1.0f
                )
        );

        queue.add(request);
    }
    private void speakAnswer(String answer) {

        if (textToSpeech != null) {

            textToSpeech.speak(
                    answer,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "WareVistaAnswer"
            );

        }
    }
    @Override
    protected void onDestroy() {

        if (textToSpeech != null) {

            textToSpeech.stop();
            textToSpeech.shutdown();

        }

        super.onDestroy();
    }
}