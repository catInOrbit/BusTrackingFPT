package com.bustracking.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.os.Bundle;
import android.os.Handler;

import com.bustracking.R;
import com.bustracking.model.busmodels.BusStop;

import java.util.ArrayList;

public class BusStopsViewModel extends ViewModel
{
    private final MutableLiveData<ArrayList<BusStop>> busStopsLiveData = new MutableLiveData<ArrayList<BusStop>>();

    public MutableLiveData<ArrayList<BusStop>> getBusStops()
    {
        return busStopsLiveData;
    }

    public void setBusStopsLiveData(ArrayList<BusStop> parcelData)
    {
        Handler handler = new Handler();
        handler.postDelayed(() ->
        {
            busStopsLiveData.setValue(parcelData);
        }, 100);
    }
}
