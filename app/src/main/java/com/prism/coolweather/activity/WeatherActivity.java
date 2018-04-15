package com.prism.coolweather.activity;

import android.app.Activity;
import android.os.Bundle;

import com.prism.coolweather.R;

public class WeatherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
    }
}
