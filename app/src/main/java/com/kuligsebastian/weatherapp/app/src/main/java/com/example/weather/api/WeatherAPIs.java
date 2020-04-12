package com.example.weather.api;

import com.example.weather.model.WeatherResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPIs {

    /*
    Get request to fetch city weather.Takes in four parameter: - latitude of city
                                                                -longitude of city
                                                                - units
                                                                - API key.
    */
    @GET("/data/2.5/forecast/") //endpointlat={lat}&lon={lon
    Call<WeatherResult> getWeatherByCoordinates(@Query("lat") double lat, @Query("lon") double lon, @Query("units")  String units, @Query("appid") String apiKey);

    /*
   Get request to fetch city weather.Takes in three parameter: - city name
                                                               - units
                                                               - API key.
   */
    @GET("/data/2.5/forecast/")
    Call<WeatherResult> getWeatherByCity(@Query("q") String city, @Query("units")  String units, @Query("appid") String apiKey);
}