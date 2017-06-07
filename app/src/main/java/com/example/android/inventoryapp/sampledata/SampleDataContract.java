package com.example.android.inventoryapp.sampledata;

import java.util.List;
import java.util.Map;

/**
 * Created by mdd23 on 6/5/2017.
 */
public interface SampleDataContract {
    public List<Map<String , String>> getSampleData();
    public void setSampleData(List<Map<String , String>> newSampleData);
}
