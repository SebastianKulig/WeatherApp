package com.example.weather.api;

import com.example.weather.R;

public class WeatherImage {
    public static int switchImage(String imageFromApi){
        switch(imageFromApi){
            case "01d":
                return R.drawable.icon_01d;
            case "01n":
                return R.drawable.icon_01n;
            case "02d":
                return R.drawable.icon_02d;
            case "02n":
                return R.drawable.icon_02n;
            case "03n":
            case "03d":
                return R.drawable.icon_03dn;
            case "04n":
            case "04d":
                return R.drawable.icon_04dn;
            case "09n":
            case "09d":
                return R.drawable.icon_09dn;
            case "10d":
                return R.drawable.icon_10d;
            case "10n":
                return R.drawable.icon_10n;
            case "11d":
            case "11n":
                return R.drawable.icon_11dn;
            case "13d":
            case "13n":
                return R.drawable.icon_13dn;
            case "50d":
            case "50n":
                return R.drawable.icon_50dn;
            default:
                return R.drawable.icon_02d;
        }

    }
}
