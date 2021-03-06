package com.example.openweatherapp;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WeatherService {
    @POST("data/2.5/weather?")
    Call<WeatherResponse> getCurrentWeatherData(@Query("q") String cidadePais, @Query("units") String unit, @Query("appid") String app_id);

    @POST("data/2.5/onecall?")
    Call<PrevisaoResponse> getPrevisaoWeatherData(@Query("lat") double lat, @Query("lon") double lon,
                                                  @Query("exclude") String exclude, @Query("appid") String app_id);
}
