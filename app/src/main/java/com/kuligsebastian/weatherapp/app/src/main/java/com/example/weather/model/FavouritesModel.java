package com.example.weather.model;


/**
 * custom data type,
 * isSelected is used in selection of item in ListView
 */
public class FavouritesModel {
    private String cityName;
    private Boolean isSelected;

    public FavouritesModel(String cityName, Boolean isSelected) {
        this.cityName = cityName;
        this.isSelected = isSelected;
    }

    public String getCityName() {
        return this.cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}