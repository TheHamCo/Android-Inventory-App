package com.example.android.inventoryapp.sampledata;

import java.util.List;
import java.util.Map;

/**
 * Created by mdd23 on 6/5/2017.
 */
public class SampleData implements SampleDataContract{

    private List<Map<String , String>>  sampleData;

    public SampleData(List<Map<String , String>> newSampleData) {
        sampleData = newSampleData;
    }

    public SampleData() {

    }

    @Override
    public List<Map<String , String>> getSampleData() {
        return sampleData;
    }

    @Override
    public void setSampleData(List<Map<String , String>> newSampleData) {
        sampleData = newSampleData;
    }
}
