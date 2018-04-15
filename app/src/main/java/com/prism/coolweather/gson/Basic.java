package com.prism.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Create by 棱镜  *
 **/
public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}
