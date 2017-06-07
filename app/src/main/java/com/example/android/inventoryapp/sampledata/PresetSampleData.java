package com.example.android.inventoryapp.sampledata;

import com.example.android.inventoryapp.db.ProductContract.ProductEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mdd23 on 6/7/2017.
 */

public class PresetSampleData {
    private static SampleData presetData = new SampleData();

    public static SampleData getPresetData() {
        List<HashMap<String, String>> presetDataList = new ArrayList<>();

        HashMap<String, String> item = new HashMap<>();
        item.put(ProductEntry.COLUMN_PRODUCT, "Nougat");
        item.put(ProductEntry.COLUMN_PRICE, "7.00");
        item.put(ProductEntry.COLUMN_QTY, "7");
        item.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Android");
        item.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "nutella@android.com");
        item.put(ProductEntry.COLUMN_IMAGE_URL, "http://cdn03.androidauthority.net/wp-content/uploads/2016/03/android-n-preview-logo.jpg");
        presetDataList.add(item);

        item = new HashMap<>();
        item.put(ProductEntry.COLUMN_PRODUCT, "Nutella");
        item.put(ProductEntry.COLUMN_PRICE, "10.00");
        item.put(ProductEntry.COLUMN_QTY, "10");
        item.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Wishful Thinking");
        item.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "nutella@android.com");
        item.put(ProductEntry.COLUMN_IMAGE_URL, "http://www.nutella.com/nutella-gl-theme/images/custom/Nutella.png");
        presetDataList.add(item);

        item = new HashMap<>();
        item.put(ProductEntry.COLUMN_PRODUCT, "Tea");
        item.put(ProductEntry.COLUMN_PRICE, "0.99");
        item.put(ProductEntry.COLUMN_QTY, "3");
        item.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Dutch East India Company");
        item.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "1602@deic.com");
        item.put(ProductEntry.COLUMN_IMAGE_URL, "http://www.asiawelcome.com/Images/Spices/VOC_Holland_SpicesFleet_mw.jpg");
        presetDataList.add(item);

        item = new HashMap<>();
        item.put(ProductEntry.COLUMN_PRODUCT, "Espresso");
        item.put(ProductEntry.COLUMN_PRICE, "3.00");
        item.put(ProductEntry.COLUMN_QTY, "3");
        item.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Starbucks");
        item.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "starbucks@starbucks.com");
        item.put(ProductEntry.COLUMN_IMAGE_URL, "http://i.imgur.com/x48dEvw.jpg");
        presetDataList.add(item);

        item = new HashMap<>();
        item.put(ProductEntry.COLUMN_PRODUCT, "Latte");
        item.put(ProductEntry.COLUMN_PRICE, "4.00");
        item.put(ProductEntry.COLUMN_QTY, "3");
        item.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Starbucks");
        item.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "starbucks@starbucks.com");
        item.put(ProductEntry.COLUMN_IMAGE_URL, "http://i.imgur.com/YWInHSO.jpg");
        presetDataList.add(item);

        item = new HashMap<>();
        item.put(ProductEntry.COLUMN_PRODUCT, "Americano");
        item.put(ProductEntry.COLUMN_PRICE, "3.50");
        item.put(ProductEntry.COLUMN_QTY, "3");
        item.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Tim Hortons");
        item.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "canada@canada.ca");
        item.put(ProductEntry.COLUMN_IMAGE_URL, "http://www.themoscowtimes.com/upload/iblock/12e/1280px-A_small_cup_of_coffee.JPG");
        presetDataList.add(item);

        presetData.setSampleData(presetDataList);
        return presetData;
    }
}
