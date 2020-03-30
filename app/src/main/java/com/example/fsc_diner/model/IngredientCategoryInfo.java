package com.example.fsc_diner.model;

public class IngredientCategoryInfo {

    private String ingredientCategoryName;
    private String ingredientCategorySelectionType;

    public IngredientCategoryInfo(){}

    public IngredientCategoryInfo(String ingredientCategoryName, String ingredientCategorySelectionType) {
        this.ingredientCategoryName = ingredientCategoryName;
        this.ingredientCategorySelectionType = ingredientCategorySelectionType;
    }

    public String getIngredientCategoryName() {
        return ingredientCategoryName;
    }

    public void setIngredientCategoryName(String ingredientCategoryName) {
        this.ingredientCategoryName = ingredientCategoryName;
    }

    public String getIngredientCategorySelectionType() {
        return ingredientCategorySelectionType;
    }

    public void setIngredientCategorySelectionType(String ingredientCategorySelectionType) {
        this.ingredientCategorySelectionType = ingredientCategorySelectionType;
    }
}
