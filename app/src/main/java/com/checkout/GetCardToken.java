package com.checkout;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Environment;

import com.checkout.httpconnector.Response;
import com.checkout.models.Card;
import com.checkout.models.CardToken;
import com.checkout.models.CardTokenResponse;

import java.io.File;


public class GetCardToken extends ActionBarActivity {

    private String publicKey = "pk_test_6ff46046-30af-41d9-bf58-929022d2cd14";
    private TextView cardToken;
    private TextView error;
    private boolean excep = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_card_token);
        cardToken = (TextView) findViewById(R.id.cardToken);
        error = (TextView) findViewById(R.id.error);

        final Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    new ConnectionTask().execute("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void write(String error_msg, String info) {
        error.setText(error_msg);
        cardToken.setText(info);
    }

    class ConnectionTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            final EditText name = (EditText) findViewById(R.id.name);
            final EditText number = (EditText) findViewById(R.id.number);
            final EditText expMonth = (EditText) findViewById(R.id.expMonth);
            final EditText expYear = (EditText) findViewById(R.id.expYear);
            final EditText cvv = (EditText) findViewById(R.id.cvv);


            try {
                Card card = new Card(number.getText().toString(), name.getText().toString(), expMonth.getText().toString(), expYear.getText().toString(), cvv.getText().toString());
                CheckoutKit ck = CheckoutKit.getInstance(publicKey);
                //ck.setFileLogger("/dev/log.log");
                final Response<CardTokenResponse> resp = ck.createCardToken(card);
                if (resp.hasError) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            write(resp.error + "  " + resp.httpStatus, "");
                            excep = true;
                        }
                    });
                } else {
                    CardToken ct = resp.model.getCard();
                    excep = false;
                    return ct.getCardToken();
                }
            } catch (final Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        write(e.getMessage(), "");
                        excep = true;
                    }
                });
            }
            return "";
        }

        @Override
        protected void onPostExecute(final String result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!excep) write("", result);
                }
            });
        }
    }
}
