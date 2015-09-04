package com.checkout;

import com.checkout.exceptions.CardException;
import com.checkout.exceptions.CheckoutException;
import com.checkout.httpconnector.Response;
import com.checkout.httpconnector.ResponseError;
import com.checkout.models.Card;
import com.checkout.models.CardProvider;
import com.checkout.models.CardProviderResponse;
import com.checkout.models.CardToken;
import com.checkout.models.CardTokenResponse;
import com.checkout.models.CustDetails;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.Gson;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by manonh on 03/08/2015.
 */
public class CheckoutKitTest {

    private CheckoutKit ck;
    private CustDetails billingDetails = new CustDetails("100 test street", "", "E1", "UK", "London", "", "44", "00000000");
    private Card testCard;
    private CardToken testCardToken;
    private Gson gson = new Gson();

    public static List<CardProvider> cp = Arrays.asList(new CardProvider("cp_1", "VISA", true),
            new CardProvider("cp_2", "MASTERCARD", true),
            new CardProvider("cp_3", "AMEX", true),
            new CardProvider("cp_4", "DISCOVER", true),
            new CardProvider("cp_5", "DINERSCLUB", true));


    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080); // No-args constructor defaults to port 8080

    public void initCard() {
        try {
            testCard = new Card("4242424242424242", "test", "06", "18", "100", billingDetails);
            testCardToken = new CardToken("06", "2018", billingDetails, "card_789E87FC-A6BF-4B74-BDB1-80BCC4DD968C", "4242", "Visa", "C96C9E67-DF9A-442A-93C5-CEEE7955314B", "test");
        } catch (CardException e) {
            fail(e.getMessage());
        }
    }

    public void init() {
        initCard();
        CheckoutKit.destroy();
        try {
            ck = CheckoutKit.getInstance("pk_test_6ff46046-30af-41d9-bf58-929022d2cd14");
        } catch (CheckoutException e) {
            fail(e.getMessage());
        }
        ck.baseUrlOverride = "http://localhost:8080/";
    }

    @Test
    public void wrongCardNumberTest() {
        try {
            testCard = new Card("4242424242524243", "test", "06", "18", "100", billingDetails);
            fail("Expected a CardException to be thrown");
        } catch (CardException e) {
            assertEquals(e.getMessage(), "INVALID_NUMBER");
        }
    }

    @Test
    public void wrongExpDateTest() {
        try {
            testCard = new Card("4242424242424242", "test", "06", "13", "100", billingDetails);
            fail("Expected a CardException to be thrown");
        } catch (CardException e) {
            assertEquals(e.getMessage(), "INVALID_EXPIRY_DATE");
        }
    }

    @Test
    public void wrongCVVTest() {
        try {
            testCard = new Card("4242424242424242", "test", "06", "18", "99999", billingDetails);
            fail("Expected a CardException to be thrown");
        } catch (CardException e) {
            assertEquals(e.getMessage(), "INVALID_CVV");
        }
    }

    @Test
    public void wrongPKTest() {
        try {
            ck = CheckoutKit.getInstance("pk_test_6ff46046-30af-419-bf58-929022d2cd");
            fail("Expected a CheckoutException to be thrown");
        } catch (CheckoutException e) {
            assertEquals(e.getMessage(), "INVALID_PUBLIC_KEY");
        }
    }

    @Test
    public void getCardProvidersTest() {
        init();
        stubFor(get(urlEqualTo("/providers/cards"))
                .withHeader("AUTHORIZATION", equalTo("pk_test_6ff46046-30af-41d9-bf58-929022d2cd14"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(gson.toJson(new CardProviderResponse("list", cp.size(), cp)))));

        try {
            Response<CardProviderResponse> resp = ck.getCardProviders();
            assertEquals(resp.hasError, false);
            assertEquals(resp.model.getData(), cp);
        } catch (CheckoutException|IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void getCardProvidersWrongPKTest() {
        CheckoutKit.destroy();
        stubFor(get(urlEqualTo("/providers/cards"))
                .withHeader("AUTHORIZATION", equalTo("pk_test_6ff46046-30af-41d9-bf58-929022d200c4"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody(gson.toJson(new ResponseError("401", "Your API key is invalid.", null, null)))));

        try {
            ck = CheckoutKit.getInstance("pk_test_6ff46046-30af-41d9-bf58-929022d200c4");
            Response<CardProviderResponse> resp = ck.getCardProviders();
            assertEquals(resp.hasError, true);
            assertEquals(resp.httpStatus, 401);
            assertEquals(resp.error.message, "Your API key is invalid.");
            assertEquals(resp.error.errorCode, "401");
        } catch (CheckoutException|IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void createCardTokenTest() {
        init();
        stubFor(post(urlEqualTo("/tokens/card"))
                .withHeader("AUTHORIZATION", equalTo("pk_test_6ff46046-30af-41d9-bf58-929022d2cd14"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(gson.toJson(new CardTokenResponse("card_tok_BE4BEDC5-E804-402C-8770-10F1E8415F50", false, "2015-08-03T14:56:14Z", false, testCardToken)))));
        try {
            Response<CardTokenResponse> resp = ck.createCardToken(testCard);
            assertEquals(resp.hasError, false);
            assertEquals(testCardToken, resp.model.getCard());
        } catch (CheckoutException|IOException e) {
            fail(e.getMessage());
        }

        // {"id":"card_tok_BE4BEDC5-E804-402C-8770-10F1E8415F50","liveMode":false,"created":"2015-08-03T14:56:14Z","used":false,"card":{"expiryMonth":"6","expiryYear":"2018",
        // "billingDetails":{"addressLine1":null,"addressLine2":null,"postcode":null,"country":null,"city":null,"state":null,"phone":{"number":null}},
        // "id":"card_789E87FC-A6BF-4B74-BDB1-80BCC4DD968C","last4":"4242","paymentMethod":"Visa","fingerprint":"C96C9E67-DF9A-442A-93C5-CEEE7955314B","name":"test",
        // "cvvCheck":null,"avsCheck":null,"responseCode":null}}
    }

    @Test
    public void createCardTokenWrongPKTest() {
        CheckoutKit.destroy();
        stubFor(get(urlEqualTo("/tokens/card"))
                .withHeader("AUTHORIZATION", equalTo("pk_test_6ff46046-30af-41d9-bf58-929022d200c4"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody(gson.toJson(new ResponseError("401", "Your API key is invalid.", null, null)))));

        try {
            initCard();
            ck = CheckoutKit.getInstance("pk_test_6ff46046-30af-41d9-bf58-929022d200c4");
            Response<CardTokenResponse> resp = ck.createCardToken(testCard);
            assertEquals(resp.hasError, true);
            assertEquals(resp.httpStatus, 401);
            assertEquals(resp.error.message, "Your API key is invalid.");
            assertEquals(resp.error.errorCode, "401");
        } catch (CheckoutException|IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void cardLiveTest() {
        CheckoutKit.destroy();
        com.checkout.models.Card c = null;
        try {
            c = new Card("4242424242424242", "", "06", "18", "100");
            ck = CheckoutKit.getInstance("pk_1ADBEB2D-2BEA-4F82-8ABC-EDE3A1201C8D");
            System.out.println(ck.getPublicKey());
            ck.setEnvironment(CheckoutKit.Environment.LIVE);
            Response<CardTokenResponse> resp = ck.createCardToken(c);
            assertEquals(resp.hasError, false);

            System.out.println(resp.model.getCardToken());
        } catch (CheckoutException|IOException|CardException e) {
            fail(e.getMessage());
        }
    }

//    @Test
//    public void defaultDebugModeTest() {
//        try {
//            init();
//            ck = CheckoutKit.getInstance("pk_1ADBEB2D-2BEA-4F82-8ABC-EDE3A1201C8D");
//            assertEquals(true, ck.getDebugMode());
//            assertEquals(CheckoutKit.Environment.SANDBOX, ck.getEnvironment());
//            assertEquals(System.out, ck.getLogger());
//        } catch (CheckoutException|IOException e) {
//            fail(e.getMessage());
//        }
//    }
//

    @Test
    public void consoleLogTest() {
        try {
            init();
            ck = CheckoutKit.getInstance("pk_1ADBEB2D-2BEA-4F82-8ABC-EDE3A1201C8D");

            ck.getLogger().info("Test console log message");
        } catch (CheckoutException e) {
            fail(e.getMessage());
        }
    }

//    @Test
//    public void fileLogTest() {
//        CheckoutKit.destroy();
//        try {
//            ck = CheckoutKit.getInstance("pk_1ADBEB2D-2BEA-4F82-8ABC-EDE3A1201C8D", testLogFile);
//            ck.log("Test file log message", CheckoutKit.LoggerLevel.MEDIUM);
//            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(testLogFile))));
//            System.out.println("-----READ FROM FILE " + testLogFile + "-----");
//            String content = "";
//            while ((content = br.readLine()) != null) {
//                System.out.println(content);
//            }
//            System.out.println("-----EOF-----");
//        } catch (com.checkout.exceptions.CheckoutException e) {
//            fail(e.getMessage());
//        } catch (IOException e) {
//            fail(e.getMessage());
//        }
//    }

}
