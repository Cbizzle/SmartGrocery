package com.smartgrocery.smartgrocery;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by floshaban on 12/8/16.
 */

public class FoodViewHolder extends RecyclerView.ViewHolder {
    public ImageView itemImageView = null;
    public TextView itemNameView = null;
    public TextView itemPriceView = null;
    public TextView isItemStockedView = null;


    //Manager only items
    public TextView itemStockWeightView = null;
    public TextView itemTemperatureView = null;
    public TextView itemHumidityView = null;
    public TextView weightLabelView = null;
    public TextView temperatureLabelView = null;
    public TextView humidityLabelView = null;



    public FoodViewHolder(View itemView) {
        super(itemView);
        itemImageView = (ImageView)itemView.findViewById(R.id.itemImage);
        itemNameView = (TextView)itemView.findViewById(R.id.itemName);
        itemPriceView = (TextView)itemView.findViewById(R.id.itemPrice);
        isItemStockedView = (TextView)itemView.findViewById(R.id.isItemStocked);

        itemStockWeightView = (TextView)itemView.findViewById(R.id.itemStockWeight);
        itemTemperatureView = (TextView)itemView.findViewById(R.id.itemTemperature);
        itemHumidityView = (TextView)itemView.findViewById(R.id.itemHumidity);

        weightLabelView = (TextView)itemView.findViewById(R.id.weightLabel);
        temperatureLabelView = (TextView)itemView.findViewById(R.id.temperatureLabel);
        humidityLabelView = (TextView)itemView.findViewById(R.id.humidityLabel);

    }
}
