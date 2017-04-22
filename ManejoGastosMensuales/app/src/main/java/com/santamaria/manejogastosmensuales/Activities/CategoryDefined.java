package com.santamaria.manejogastosmensuales.Activities;

import io.realm.RealmObject;

/**
 * Created by Santamaria on 22/04/2017.
 */

public class CategoryDefined  extends RealmObject {

    private String categoryName;

    public CategoryDefined(String categoryName) {
        this.categoryName = categoryName;
    }

    public CategoryDefined() {
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
