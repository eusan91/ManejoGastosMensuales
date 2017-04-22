package com.santamaria.manejogastosmensuales.Domain;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Santamaria on 22/04/2017.
 */

public class SettingsData extends RealmObject {

    @PrimaryKey
    private int id;
    private int startMonth;
    private RealmList<CategoryDefined> categoryDefinedList;

    public SettingsData() {

    }

    public SettingsData(int startMonth, RealmList<CategoryDefined> categoryDefinedList) {
        this.startMonth = startMonth;
        this.categoryDefinedList = categoryDefinedList;
    }

    public SettingsData(int startMonth) {
        this.startMonth = startMonth;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public RealmList<CategoryDefined> getCategoryDefinedList() {
        return categoryDefinedList;
    }

    public void setCategoryDefinedList(RealmList<CategoryDefined> categoryDefinedList) {
        this.categoryDefinedList = categoryDefinedList;
    }
}
