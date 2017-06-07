package com.example.android.inventoryapp.sampledata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.android.inventoryapp.db.ProductContract.ProductEntry.COLUMN_IMAGE_URL;
import static com.example.android.inventoryapp.db.ProductContract.ProductEntry.COLUMN_PRICE;
import static com.example.android.inventoryapp.db.ProductContract.ProductEntry.COLUMN_PRODUCT;
import static com.example.android.inventoryapp.db.ProductContract.ProductEntry.COLUMN_QTY;
import static com.example.android.inventoryapp.db.ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL;
import static com.example.android.inventoryapp.db.ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME;

/**
 * Created by mdd23 on 6/7/2017.
 */

public class PresetSampleData {
    private static SampleData presetData = new SampleData();

    public static SampleData getPresetData() {
        List<HashMap<String, String>> presetDataList = new ArrayList<>();

        presetDataList.add(createDataHashMap(
                  "Nougat"
                , "7.00"
                , "7"
                , "Android"
                , "nutella@android.com"
                , "http://cdn03.androidauthority.net/wp-content/uploads/2016/03/android-n-preview-logo.jpg"
        ));

        presetDataList.add(createDataHashMap(
                  "Ice Cream"
                , "$2.00"
                , "25"
                , "Ben and Jerry's"
                , "ice@cream.com"
                , "http://i.imgur.com/F4k3seo.jpg"
        ));

        presetDataList.add(createDataHashMap(
                  "Nutella"
                , "10.00"
                , "10"
                , "Wishful Thinking"
                , "nutella@android.com"
                , "http://www.nutella.com/nutella-gl-theme/images/custom/Nutella.png"
        ));

        presetDataList.add(createDataHashMap(
                  "Tea"
                , "0.99"
                , "3"
                , "Dutch East India Company"
                , "1602@deic.com"
                , "http://www.asiawelcome.com/Images/Spices/VOC_Holland_SpicesFleet_mw.jpg"
        ));

        presetDataList.add(createDataHashMap(
                  "Espresso"
                , "3.00"
                , "3"
                , "Starbucks"
                , "starbucks@starbucks.com"
                , "http://i.imgur.com/x48dEvw.jpg"
        ));

        presetDataList.add(createDataHashMap(
                  "Latte"
                , "4.00"
                , "3"
                , "Starbucks"
                , "starbucks@starbucks.com"
                , "http://i.imgur.com/YWInHSO.jpg"
        ));

        presetDataList.add(createDataHashMap(
                  "Americano"
                , "3.50"
                , "3"
                , "Tim Hortons"
                , "canada@canada.ca"
                , "http://www.themoscowtimes.com/upload/iblock/12e/1280px-A_small_cup_of_coffee.JPG"
        ));

        presetData.setSampleData(presetDataList);
        return presetData;
    }

    private static HashMap<String, String> createDataHashMap(
              final String product
            , final String price
            , final String qty
            , final String supplierName
            , final String supplierEmail
            , final String imageUrl
    ) {
        return new HashMap<String, String>() {{
            put(COLUMN_PRODUCT,product);
            put(COLUMN_PRICE,price);
            put(COLUMN_QTY,qty);
            put(COLUMN_SUPPLIER_NAME,supplierName);
            put(COLUMN_SUPPLIER_EMAIL,supplierEmail);
            put(COLUMN_IMAGE_URL,imageUrl);
        }};
    }
}
