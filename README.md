### Requirements

JDK 1.8

### How to use the library

Download ```checkoutkit.jar``` and use it as an external library in your IDE.

Add ```compile 'com.google.code.gson:gson:2.2.4'``` to your application's ```build.gradle``` file.

### Example

A demo application is available in the folder *app*

Import the **CheckoutKit.java** in your code as below:
```
import com.checkout.CheckoutKit;
```

You will need to specify at least the public key when creating an instance of ***CheckoutKit***. We offer a wide range of constructors, the ***CheckoutKit*** instance is entirely customizable:

```html
CheckoutKit.getInstance(String publicKey) throws CheckoutException
CheckoutKit.getInstance(String publicKey, Environment baseUrl) throws CheckoutException
CheckoutKit.getInstance(String publicKey, boolean debug) throws CheckoutException
CheckoutKit.getInstance(String publicKey, Environment baseUrl, boolean debug) throws CheckoutException
CheckoutKit.getInstance(String publicKey, boolean debug, Log logger) throws CheckoutException
CheckoutKit.getInstance(String publicKey, Environment baseUrl, boolean debug, Log logger) throws CheckoutException
CheckoutKit.getInstance(String publicKey, Environment baseUrl, Log logger) throws CheckoutException
CheckoutKit getInstance(String publicKey, Log logger) throws CheckoutException
CheckoutKit getInstance() throws CheckoutException // to use only once the CheckoutKit object has been instantiated, otherwise throws a CheckoutException
```

These functions can throw CheckoutException (when the public key specified is invalid).
Here are more details about the parameters :
- publicKey : String containing the public key. It is tested against the following regular expression : ```^pk_(?:test_)?(?:\w{8})-(?:\w{4})-(?:\w{4})-(?:\w{4})-(?:\w{12})$```. Mandatory otherwise the ***CheckoutKit*** object cannot be instantiated.
- baseUrl : Environment object containing the information of the merchant's environment, default is SANDBOX. Optional.
- debug : boolean, if the debug mode is activated or not, default is true. Optional.
- logger : Log object printing information, warnings and errors to the console (for now). Optional.

If **debug** is set to true, many actions will be traced to the logging system.

The available functions of ***CheckoutKit*** can be found [here](http://docs.checkout.com/mobile/android-kit/reference/checkoutkit)

Another class is available for utility functions: ***CardValidator***. It provides static functions for validating all the card related information before processing them. The list of the available functions is available in [here](http://docs.checkout.com/mobile/android-kit/reference/cardvalidator)


**Create card token**

```
import com.checkout.*;

public class Example {

    private String publicKey = "your_public_key";

    public void main(String[] args) {

        try {

            /* Take the card information where ever you want (form, fields in the application page...) */
            Card card = new Card(number.getText().toString(), name.getText().toString(), expMonth.getText().toString(), expYear.getText().toString(), cvv.getText().toString());

            /* Instantiates the CheckoutKit object with the minimum parameters, default configuration for the other ones */
            CheckoutKit ck = CheckoutKit.getInstance(publicKey);

            /* Makes the call to the server (specified in environment variable) */
            Response<CardTokenResponse> resp = ck.createCardToken(card);

            if (resp.hasError) {
                /* Print error message or handle it */
            } else {
                /* The card object */
                CardToken ct = resp.model.getCard();
                /* The field containing the card token to make a charge */
                String cardToken = resp.model.getCardToken();
            }

        } catch (CardException e1) {
            /* Happens when the card informations entered by the user are not correct. */
            /* Type of the exception in the enum CardExceptionType. Different types are INVALID_CVV (if the CVV does not have the correct format), INVALID_EXPIRY_DATE (if the card is expired), INVALID_NUMBER (if the card's number is wrong). */
        } catch (CheckoutException e2) {
            /* Happens when the public key is not valid, raised during the instanciation of the CheckoutKit object */
            /* Type of the exception in the enum CKExceptionType. Different types are NO_PUBLIC_KEY (if getInstance is called with no parameters and no public key has been provided before) and INVALID_PUBLIC_KEY (if the public key is invalid) */
        }
    }
}
```

**Usage within an Android app**

Calls to getCardProviders and createCardToken need to be done through a ConnectionTask as shown below:

```
public class GetCardToken extends ActionBarActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* ... */

        /* Clicking on the button starts the create card token process */
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

    /* We need to create a ConnectionTask class because networking requests cannot be handled in the main thread */
    class ConnectionTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            /* All the fields that need to be filled with the card information */
            final EditText name = (EditText) findViewById(R.id.name);
            final EditText number = (EditText) findViewById(R.id.number);
            final EditText expMonth = (EditText) findViewById(R.id.expMonth);
            final EditText expYear = (EditText) findViewById(R.id.expYear);
            final EditText cvv = (EditText) findViewById(R.id.cvv);

            try {
                /* Create the card object */
                Card card = new Card(number.getText().toString(), name.getText().toString(), expMonth.getText().toString(), expYear.getText().toString(), cvv.getText().toString());
                /* Create the CheckoutKit instance */
                CheckoutKit ck = CheckoutKit.getInstance(publicKey);

                final Response<CardTokenResponse> resp = ck.createCardToken(card);
                if (resp.hasError) {
                    /* Handle errors */
                } else {
                    /* The card token */
                    String cardToken = resp.model.getCardToken();
                }
            } catch (final CardException e1) {
                /* Handle validation errors */
            } catch (final CheckoutException e2) {
                /* Handle errors related to the CheckoutKit creation */
            }
            return "";
        }
    }
}
```

### Logging

Most of the activity of the **CheckoutKit** is logged either as information, warning or error. All the logs are made to the console for now. Logging occurs only if the debug mode is activated (true as default, but can be explicitely set to false). The printing format is ```date (yyyy/MM/dd HH:mm:ss)  **Subject/Class name**  logged message```. The logger can be modified via the functions setLogger or getInstance. The log entries are then added to the logger specified.

### Unit Tests

All the unit test written with JUnit (v4) and resides in the test package.
