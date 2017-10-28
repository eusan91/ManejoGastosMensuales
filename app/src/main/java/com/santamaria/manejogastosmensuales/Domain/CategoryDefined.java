package com.santamaria.manejogastosmensuales.Domain;

import com.santamaria.manejogastosmensuales.app.MyApplication;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Santamaria on 22/04/2017.
 */

public class CategoryDefined  extends RealmObject {

    @PrimaryKey
    private int id;
    private String categoryName;
    private int color;

    public CategoryDefined(String categoryName, int color) {
        this.id = MyApplication.CategoryDefinedID.incrementAndGet();
        this.categoryName = categoryName;
        this.color = color;
    }

    public CategoryDefined() {
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
