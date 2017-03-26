package com.smartgrocery.smartgrocery;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by floshaban on 12/8/16.
 */

public class FoodRecyclerViewAdapter extends RecyclerView.Adapter<FoodViewHolder> {

    private Context mContext;
    List<FoodContainer> mFoodContainerList;
    private final RecyclerClickListener mClickListener;
    boolean isManager = false;

    FoodRecyclerViewAdapter(Context context, List<FoodContainer> foodContainerList, RecyclerClickListener clickListener) {
        this.mContext = context;
        this.mFoodContainerList = foodContainerList;
        this.mClickListener = clickListener;
    }

    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_grocery, parent, false);
        return new FoodViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FoodViewHolder holder, final int position) {
        FoodContainer foodItem = mFoodContainerList.get(position);

        holder.itemNameView.setText(foodItem.foodName);
        if(foodItem.foodName.equalsIgnoreCase("Bananas")) {
            holder.itemImageView.setImageResource(R.drawable.bananas);
        }

        holder.itemStockWeightView.setText(String.format("%.2f g",foodItem.foodWeight));

        if(foodItem.minWeight > foodItem.foodWeight) {
            holder.isItemStockedView.setText("Out of Stock");
           // holder.itemPriceView.setText("$ N/A");
            holder.itemStockWeightView.setTextColor(Color.RED);

        } else {
            holder.isItemStockedView.setText("In Stock");
            holder.itemStockWeightView.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        }

        holder.itemPriceView.setText(String.format("$%.2f /lb",foodItem.foodPrice));
        holder.itemTemperatureView.setText(String.format("%.2f\u00B0 C",foodItem.foodTemperature));
        holder.itemHumidityView.setText(String.format("%.2f%%",foodItem.foodHumidity));

        if(isManager) {
            //set items visible
            holder.itemTemperatureView.setVisibility(View.VISIBLE);
            holder.itemHumidityView.setVisibility(View.VISIBLE);
            holder.weightLabelView.setVisibility(View.VISIBLE);
            holder.temperatureLabelView.setVisibility(View.VISIBLE);
            holder.humidityLabelView.setVisibility(View.VISIBLE);

            holder.isItemStockedView.setVisibility(View.INVISIBLE);
            holder.itemStockWeightView.setVisibility(View.VISIBLE);
        } else {
            holder.itemTemperatureView.setVisibility(View.GONE);
            holder.itemHumidityView.setVisibility(View.GONE);
            holder.weightLabelView.setVisibility(View.GONE);
            holder.temperatureLabelView.setVisibility(View.GONE);
            holder.humidityLabelView.setVisibility(View.GONE);


            holder.isItemStockedView.setVisibility(View.VISIBLE);
            holder.itemStockWeightView.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.itemClicked(v, position);
            }
        });

    }

    public void setManager(boolean manager) {
        isManager = manager;
    }

    @Override
    public int getItemCount() {
        return mFoodContainerList.size();
    }
}
