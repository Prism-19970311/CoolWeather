package com.prism.coolweather.gson;

/**
 * Create by 棱镜  *
 **/
public class AQI {
    public AQICity city;

    public class AQICity {
        public String aqi;
        public String pm25;
    }
}
