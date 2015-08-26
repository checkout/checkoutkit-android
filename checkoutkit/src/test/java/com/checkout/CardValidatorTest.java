package com.checkout;

import com.checkout.CardValidator.Cards;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;


/**
 * Created by manonh on 30/07/2015.
 */
public class CardValidatorTest {

    @Test
    public void isDigitTest() {
        assertEquals(true, boolFunction("isDigit", new Class[]{String.class}, new Object[]{"123"}));
        assertEquals(false, boolFunction("isDigit", new Class[]{String.class}, new Object[]{"1abc"}));
    }

    @Test
    public void sanitizeNameTest() {
        assertEquals("JOHN LENNON", strFunction("sanitizeName", new Class[]{String.class}, new Object[]{"john lennon123"}));
        assertEquals("JOHNLENNON", strFunction("sanitizeName", new Class[]{String.class}, new Object[]{"johnlennon"}));
    }

    @Test
    public void sanitizeEntryTest() {
        assertEquals("123123123", strFunction("sanitizeEntry", new Class[]{String.class, boolean.class}, new Object[]{"123-123-123aa", true}));
        assertEquals("123123123aa", strFunction("sanitizeEntry", new Class[]{String.class, boolean.class}, new Object[]{"123-123-123aa", false}));
    }

    @Test
    public void validateCardNumberTest() {
        assertEquals(true, CardValidator.validateCardNumber("4242424242424242"));
        assertEquals(true, CardValidator.validateCardNumber("4242-42424242-4242"));
        assertEquals(true, CardValidator.validateCardNumber("4242 4242 4242 4242"));
        assertEquals(true, CardValidator.validateCardNumber("378282246310005"));
        assertEquals(true, CardValidator.validateCardNumber("5555 5555 5555 4444"));
        assertEquals(false, CardValidator.validateCardNumber("4242-1111-1111-1111"));
        assertEquals(false, CardValidator.validateCardNumber("1234asd"));
    }

    @Test
    public void getCardTypeTest() {
        assertEquals(Cards.VISA, CardValidator.getCardType("4242-4242-4242-4242"));
        assertEquals(Cards.VISA, CardValidator.getCardType("4543474002249996"));
        assertEquals(Cards.AMEX, CardValidator.getCardType("378282246310005"));
        assertEquals(Cards.AMEX, CardValidator.getCardType("345678901234564"));
        assertEquals(Cards.MASTERCARD, CardValidator.getCardType("5555 5555 5555 4444"));
        assertEquals(Cards.MASTERCARD, CardValidator.getCardType("5436031030606378"));
        assertEquals(Cards.DINERSCLUB, CardValidator.getCardType("30123456789019"));
        assertEquals(Cards.JCB, CardValidator.getCardType("3530111333300000"));
        assertEquals(Cards.DISCOVER, CardValidator.getCardType("6011111111111117"));
        assertEquals(null, CardValidator.getCardType("1234"));
    }

    @Test
    public void validateLuhnNumberTest() {
        assertEquals(true, boolFunction("validateCardNumber", new Class[]{String.class}, new Object[]{"4242424242424242"}));
        assertEquals(true, boolFunction("validateCardNumber", new Class[]{String.class}, new Object[]{"4242-42424242-4242"}));
        assertEquals(true, boolFunction("validateCardNumber", new Class[]{String.class}, new Object[]{"4242 4242 4242 4242"}));
        assertEquals(false, boolFunction("validateCardNumber", new Class[]{String.class}, new Object[]{"4242-1111-1111-1111"}));
        assertEquals(false, boolFunction("validateCardNumber", new Class[]{String.class}, new Object[]{"1234asd"}));
    }

    @Test
    public void validateExpiryDateTest() {
        assertEquals(true, CardValidator.validateExpiryDate("02", "2018"));
        assertEquals(true, CardValidator.validateExpiryDate("02", "35"));
        assertEquals(false, CardValidator.validateExpiryDate("02", "15"));
        assertEquals(false, CardValidator.validateExpiryDate("12", "1999"));
        assertEquals(true, CardValidator.validateExpiryDate(02, 2018));
        assertEquals(true, CardValidator.validateExpiryDate(02, 35));
        assertEquals(false, CardValidator.validateExpiryDate(02, 15));
        assertEquals(false, CardValidator.validateExpiryDate(12, 1999));
        Calendar cal = Calendar.getInstance();
        int curMonth = cal.get(Calendar.MONTH) + 1;
        int curYear = cal.get(Calendar.YEAR) + 1;
        assertEquals(true, CardValidator.validateExpiryDate(String.valueOf(curMonth), String.valueOf(curYear)));
    }

    @Test
    public void validateCVVTest() {
        assertEquals(true, CardValidator.validateCVV("123", Cards.VISA));
        assertEquals(true, CardValidator.validateCVV("1234", Cards.AMEX));
        assertEquals(true, CardValidator.validateCVV(123, Cards.VISA));
        assertEquals(true, CardValidator.validateCVV(1234, Cards.AMEX));
        assertEquals(false, CardValidator.validateCVV(123, null));
        assertEquals(false, CardValidator.validateCVV("", Cards.MAESTRO));
    }

    /*
     * Method used to test a private function of CardValidator that returns a boolean
     */
    private boolean boolFunction(String fName, Class[] argsC, Object[] argsO) {
        boolean b = false;
        Class c = CardValidator.class;
        try {
            Method method = c.getDeclaredMethod(fName, argsC);
            method.setAccessible(true);
            b = (Boolean)method.invoke(c, argsO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    /*
     * Method used to test a private function of CardValidator that returns a String
     */
    private String strFunction(String fName, Class[] argsC, Object[] argsO) {
        String s = null;
        Class c = CardValidator.class;
        try {
            Method method = c.getDeclaredMethod(fName, argsC);
            method.setAccessible(true);
            s = (String)method.invoke(c, argsO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

}