package com.smartgrocery.smartgrocery;

/**
 * Created by floshaban on 12/8/16.
 */

public class FoodContainer {
    public String foodName;
    public double foodPrice;
    public double foodWeight;
    public double foodTemperature;
    public double foodHumidity;
    public double minWeight;
    int id;

    FoodContainer(String foodName, double foodPrice, double foodWeight, double foodTemperature, double foodHumidity, double minWeight, int id) {
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodWeight = foodWeight;
        this.foodTemperature = foodTemperature;
        this.foodHumidity = foodHumidity;
        this.minWeight = minWeight;
        this.id = id;
    }
}
