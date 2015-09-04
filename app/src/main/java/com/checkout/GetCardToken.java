package com.checkout;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Environment;
import android.widget.TimePicker;

import com.checkout.exceptions.CardException;
import com.checkout.exceptions.CheckoutException;
import com.checkout.httpconnector.Response;
import com.checkout.models.Card;
import com.checkout.models.CardToken;
import com.checkout.models.CardTokenResponse;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class GetCardToken extends ActionBarActivity {

    private String publicKey = "pk_test_6ff46046-30af-41d9-bf58-929022d2cd14";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_card_token);

        Spinner spinnerM = (Spinner) findViewById(R.id.spinnerMonth);
        Spinner spinnerY = (Spinner) findViewById(R.id.spinnerYear);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.months, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerM.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.years, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerY.setAdapter(adapter2);
    }

    public void getCardToken(View v) {
        try {
            new ConnectionTask().execute("");
        } catch (Exception e) {
            e.printStackTrace();
            goToError();
        }
    }

    private void goToError() {
        Intent intent = new Intent(this, Fail_result.class);
        startActivity(intent);
    }

    private void goToSuccess() {
        Intent intent = new Intent(this, Success_result.class);
        startActivity(intent);
    }


    class ConnectionTask extends AsyncTask<String, Void, String> {

        final EditText name = (EditText) findViewById(R.id.name);
        final EditText numberField = (EditText) findViewById(R.id.number);
        final EditText cvvField = (EditText) findViewById(R.id.cvv);
        final Spinner spinMonth = (Spinner) findViewById(R.id.spinnerMonth);
        final Spinner spinYear = (Spinner) findViewById(R.id.spinnerYear);
        final int errorColor = Color.rgb(204, 0, 51);

        private boolean validateCardFields(final String number, final String month, final String year, final String cvv) {
            boolean error = false;
            clearFieldsError();

            if (!CardValidator.validateCardNumber(number)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        numberField.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
                    }
                });
                error = true;
            }
            if (!CardValidator.validateExpiryDate(month, year)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spinMonth.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
                        spinYear.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
                    }
                });
                error = true;
            }
            if (cvv.equals("")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cvvField.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
                    }
                });
                error = true;
            }
            return !error;
        }

        private void clearFieldsError() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cvvField.getBackground().clearColorFilter();
                    spinMonth.getBackground().clearColorFilter();
                    spinYear.getBackground().clearColorFilter();
                    numberField.getBackground().clearColorFilter();
                }
            });
        }

        @Override
        protected String doInBackground(String... urls) {

            if (validateCardFields(numberField.getText().toString(), spinMonth.getSelectedItem().toString(), spinYear.getSelectedItem().toString(), cvvField.getText().toString())) {
                clearFieldsError();
                try {
                    Card card = new Card(numberField.getText().toString(), name.getText().toString(), spinMonth.getSelectedItem().toString(), spinYear.getSelectedItem().toString(), cvvField.getText().toString());
                    CheckoutKit ck = CheckoutKit.getInstance(publicKey);
                    final Response<CardTokenResponse> resp = ck.createCardToken(card);
                    if (resp.hasError) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                goToError();
                            }
                        });
                    } else {
                        CardToken ct = resp.model.getCard();
                        goToSuccess();
                        return resp.model.getCardToken();
                    }
                } catch (final CardException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (e.getType().equals(CardException.CardExceptionType.INVALID_CVV)) {
                                cvvField.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
                            } else if (e.getType().equals(CardException.CardExceptionType.INVALID_EXPIRY_DATE)) {
                                spinMonth.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
                                spinYear.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
                            } else if (e.getType().equals(CardException.CardExceptionType.INVALID_NUMBER)) {
                                numberField.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
                            }
                        }
                    });
                } catch (CheckoutException | IOException e2) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            goToError();
                        }
                    });
                }
            }
            return "";
        }

    }
}
