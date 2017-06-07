package com.example.android.inventoryapp.sampledata;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mdd23 on 6/5/2017.
 */
public interface SampleDataContract {
    public List<HashMap<String , String>> getSampleData();
    public void setSampleData(List<HashMap<String, String>> newSampleData);
}
