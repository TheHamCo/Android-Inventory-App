package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.util.Patterns;

import com.example.android.inventoryapp.db.ProductContract;

import java.util.regex.Pattern;

import static com.example.android.inventoryapp.MainActivity.productUri;

/**
 * Created by mdd23 on 6/8/2017.
 */

public abstract class ProductValidation {

    /*VALIDATION STUFF*/
    /**
     * Validates if a price string is a properly-formatted decimal number
     * @param priceString
     * @return the string is a decimal number
     */
    public static boolean priceIsValid(String priceString){

        // Source: http://docs.oracle.com/javase/6/docs/api/java/lang/Double.html#valueOf%28java.lang.String%29
        // Given by: http://stackoverflow.com/a/3543749/5302182

        final String Digits     = "(\\p{Digit}+)";
        final String HexDigits  = "(\\p{XDigit}+)";
        // an exponent is 'e' or 'E' followed by an optionally
        // signed decimal integer.
        final String Exp        = "[eE][+-]?"+Digits;
        final String fpRegex    =
                ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
                        "[+-]?(" + // Optional sign character
                        "NaN|" +           // "NaN" string
                        "Infinity|" +      // "Infinity" string

                        // A decimal floating-point string representing a finite positive
                        // number without a leading sign has at most five basic pieces:
                        // Digits . Digits ExponentPart FloatTypeSuffix
                        //
                        // Since this method allows integer-only strings as input
                        // in addition to strings of floating-point literals, the
                        // two sub-patterns below are simplifications of the grammar
                        // productions from the Java Language Specification, 2nd
                        // edition, section 3.10.2.

                        // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                        "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

                        // . Digits ExponentPart_opt FloatTypeSuffix_opt
                        "(\\.("+Digits+")("+Exp+")?)|"+

                        // Hexadecimal strings
                        "((" +
                        // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                        "(0[xX]" + HexDigits + "(\\.)?)|" +

                        // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                        "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

                        ")[pP][+-]?" + Digits + "))" +
                        "[fFdD]?))" +
                        "[\\x00-\\x20]*");// Optional trailing "whitespace"

        return Pattern.matches(fpRegex, priceString);
    }

    /**
     * Validates all inputs at once
     * @param productName
     * @param price
     * @param qty
     * @param supplierName
     * @param supplierEmail
     * @param imageUrl
     * @return ProductValidation with isValid and failure toast message
     */
    // TODO: separation of concerns
    public static ProductValidationMessage validateAddProduct(Context context, String productName, String price, String qty, String supplierName, String supplierEmail, String imageUrl){
        boolean isValid = true;
        // Start with line break to keep toast padding even on all sides
        String toastMessage = "\n";

        //TODO: Refactor existence validation to an else if with duplicate validation?
        // Validate product name
        // Validate product name existence
        if (productName.length()==0){
            isValid = false;
            toastMessage += context.getString(R.string.please_enter_a_product_name) + "\n";
        }
        // Validate unique product name
        Cursor checkDuplicate = context.getContentResolver().query(
                productUri,null
                , ProductContract.ProductEntry.COLUMN_PRODUCT + "=?"
                , new String[] { productName }
                , null
        );
        if (checkDuplicate.getCount() > 0){
            isValid = false;
            toastMessage += context.getString(R.string.product_is_already_in_inventory) + "\n";
        }
        checkDuplicate.close();

        // Validate price
        // Validate decimal number format
        if (!priceIsValid(price)){
            isValid = false;
            toastMessage += context.getString(R.string.please_enter_a_valid_price) +"\n";
            // No negative prices
        } else if (Double.parseDouble(price)<0){
            isValid = false;
            toastMessage += context.getString(R.string.please_enter_a_valid_price) + "\n";
        }

        // Validate supplier name existence
        if (supplierName.length()==0){
            isValid = false;
            toastMessage += context.getString(R.string.please_enter_a_supplier_name) + "\n";
        }

        // Validate supplier email
        // Validate supplier email existence
        if (supplierEmail.length()==0){
            isValid = false;
            toastMessage += context.getString(R.string.please_enter_a_supplier_email) + "\n";
        }
        // Validate email proper format
        if (!Patterns.EMAIL_ADDRESS.matcher(supplierEmail).matches()){
            isValid = false;
            toastMessage += context.getString(R.string.please_enter_a_valid_email_address)  + "\n";
        }

        // Validate image URL
        // SOURCE: http://stackoverflow.com/q/15726665/5302182
        // Matches URL + image extension
        // ALSO, image is optional
        Pattern imageUrlRegex = Pattern.compile("(http(s?):/)(/[^/]+)+" + ".(?:jpg|gif|png)");
        if (imageUrl.length() != 0 && !imageUrlRegex.matcher(imageUrl).matches()){
            isValid = false;
            toastMessage += context.getString(R.string.please_enter_a_valid_image_url) + "\n";
        }

        return new ProductValidationMessage(isValid, toastMessage);
    }

    /**
     * Wrapper for all validation-related variables:
     * Add product isValid
     * Add product message
     */
    // Source: http://stackoverflow.com/questions/457629/how-to-return-multiple-objects-from-a-java-method
    public static class ProductValidationMessage {
        private boolean isValid;
        private String toastMessage;

        ProductValidationMessage(boolean isValid, String toastMessage) {
            this.isValid = isValid;
            this.toastMessage = toastMessage;
        }

        public boolean isValid() {
            return isValid;
        }

        public String getToastMessage() {
            return toastMessage;
        }
    }
}
