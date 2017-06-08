package com.example.android.inventoryapp.sampledata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mdd23 on 6/7/2017.
 */

public class PresetSampleData {
    public static List<HashMap<String, String>> getPresetData() {
        List<HashMap<String, String>> presetDataList = new ArrayList<>();

        presetDataList.add(new SampleDataHashMap()
                .addProductName("Nougat")
                .addPrice(7.00)
                .addQuantity(7)
                .addSupplierName("Android")
                .addSupplierEmail("nutella@android.com")
                .addImageUrl("http://cdn03.androidauthority.net/wp-content/uploads/2016/03/android-n-preview-logo.jpg")
        );

        presetDataList.add(new SampleDataHashMap()
                .addProductName("Ice Cream")
                .addPrice(2.00)
                .addQuantity(25)
                .addSupplierName("Ben and Jerry's")
                .addSupplierEmail("ice@cream.com")
                .addImageUrl("http://i.imgur.com/F4k3seo.jpg")
        );

        presetDataList.add(new SampleDataHashMap()
                .addProductName("Nutella")
                .addPrice(10.00)
                .addQuantity(10)
                .addSupplierName("Wishful Thinking")
                .addSupplierEmail("nutella@android.com")
                .addImageUrl("http://www.nutella.com/nutella-gl-theme/images/custom/Nutella.png")
         );

        presetDataList.add(new SampleDataHashMap()
                .addProductName("Tea")
                .addPrice(0.99)
                .addQuantity(3)
                .addSupplierName("Dutch East India Company")
                .addSupplierEmail("1602@deic.com")
                .addImageUrl("http://www.asiawelcome.com/Images/Spices/VOC_Holland_SpicesFleet_mw.jpg")
        );

        presetDataList.add(new SampleDataHashMap()
                .addProductName("Espresso")
                .addPrice(3.00)
                .addQuantity(3)
                .addSupplierName("Starbucks")
                .addSupplierEmail("starbucks@starbucks.com")
                .addImageUrl("http://i.imgur.com/x48dEvw.jpg")
       );

        presetDataList.add(new SampleDataHashMap()
                .addProductName("Latte")
                .addPrice(4.00)
                .addQuantity(3)
                .addSupplierName("Starbucks")
                .addSupplierEmail("starbucks@starbucks.com")
                .addImageUrl("http://i.imgur.com/YWInHSO.jpg")
        );

        presetDataList.add(new SampleDataHashMap()
                .addProductName("Americano")
                .addPrice(3.50)
                .addQuantity(3)
                .addSupplierName("Tim Hortons")
                .addSupplierEmail("canada@canada.ca")
                .addImageUrl("http://www.themoscowtimes.com/upload/iblock/12e/1280px-A_small_cup_of_coffee.JPG")
        );

        return presetDataList;
    }
}
