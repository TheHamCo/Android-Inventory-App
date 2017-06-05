package com.example.android.inventoryapp.sampledata;

/**
 * Created by mdd23 on 6/5/2017.
 */
public class SampleData implements SampleDataContract{

    String[][] sampleData;

    public SampleData(String[][] newSampleData) {
        sampleData = newSampleData;
    }

    public SampleData() {
        sampleData = new String[1][6];
    }

    @Override
    public String[][] getSampleData() {
        return sampleData;
    }

    @Override
    public void setSampleData(String[][] newSampleData) {
        sampleData = newSampleData;
    }
}
