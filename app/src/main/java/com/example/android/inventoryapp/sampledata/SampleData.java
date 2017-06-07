package com.example.android.inventoryapp.sampledata;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mdd23 on 6/5/2017.
 */
public class SampleData implements SampleDataContract{

    private List<HashMap<String , String>>  sampleData;

    public SampleData(List<HashMap<String , String>> newSampleData) {
        sampleData = newSampleData;
    }

    public SampleData() {

    }

    @Override
    public List<HashMap<String , String>> getSampleData() {
        return sampleData;
    }

    @Override
    public void setSampleData(List<HashMap<String, String>> newSampleData) {
        sampleData = newSampleData;
    }
}
