package com.example.fsc_diner.model;

public class IngredientSubItemInfo {

    private String ingredientSubItemName;
    private double extraPrice;
    private boolean hasExtraPrice;
    private boolean isPreSelected;

    public IngredientSubItemInfo(){}

    public IngredientSubItemInfo(String ingredientSubItemName, boolean hasExtraPrice, boolean isPreSelected) {
        this(ingredientSubItemName, 0.0, hasExtraPrice, isPreSelected);
    }

    public IngredientSubItemInfo(String ingredientSubItemName, double extraPrice, boolean hasExtraPrice, boolean isPreSelected) {
        this.ingredientSubItemName = ingredientSubItemName;
        this.extraPrice = extraPrice;
        this.hasExtraPrice = hasExtraPrice;
        this.isPreSelected = isPreSelected;
    }

    public String getIngredientSubItemName() {
        return ingredientSubItemName;
    }

    public void setIngredientSubItemName(String ingredientSubItemName) {
        this.ingredientSubItemName = ingredientSubItemName;
    }

    public double getExtraPrice() {
        return extraPrice;
    }

    public void setExtraPrice(double extraPrice) {
        this.extraPrice = extraPrice;
    }

    public boolean isHasExtraPrice() {
        return hasExtraPrice;
    }

    public void setHasExtraPrice(boolean hasExtraPrice) {
        this.hasExtraPrice = hasExtraPrice;
    }

    public boolean isPreSelected() {
        return isPreSelected;
    }

    public void setPreSelected(boolean preSelected) {
        isPreSelected = preSelected;
    }
}
