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

    public CategoryDefined(String categoryName) {
        this.id = MyApplication.CategoryDetailID.incrementAndGet();
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
