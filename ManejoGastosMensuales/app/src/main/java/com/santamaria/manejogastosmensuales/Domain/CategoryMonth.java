package com.santamaria.manejogastosmensuales.Domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.santamaria.manejogastosmensuales.Enumerator.Months;
import com.santamaria.manejogastosmensuales.app.MyApplication;

import java.util.Calendar;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Santamaria on 13/04/2017.
 */

public class CategoryMonth extends RealmObject implements Parcelable {

    @PrimaryKey
    private int id;
    private int month;
    private int year;
    private boolean currentMonth;
    private RealmList<Category> categoryList;

    public CategoryMonth() {
        this.id = MyApplication.CategoryMonthID.incrementAndGet();
        this.month = Calendar.getInstance().get(Calendar.MONTH) - 1;
        this.year = Calendar.getInstance().get(Calendar.YEAR);
        this.categoryList = new RealmList<>();
        this.currentMonth = true;
    }

    public CategoryMonth(RealmList<Category> categoryList) {
        this.id = MyApplication.CategoryMonthID.incrementAndGet();
        this.month = Calendar.getInstance().get(Calendar.MONTH) - 1;
        this.year = Calendar.getInstance().get(Calendar.YEAR);
        this.categoryList = categoryList;
        this.currentMonth = true;
    }

    protected CategoryMonth(Parcel in) {
        id = in.readInt();
    }

    public static final Creator<CategoryMonth> CREATOR = new Creator<CategoryMonth>() {
        @Override
        public CategoryMonth createFromParcel(Parcel in) {
            return new CategoryMonth(in);
        }

        @Override
        public CategoryMonth[] newArray(int size) {
            return new CategoryMonth[size];
        }
    };

    public int getId() {
        return id;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean isCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(boolean currentMonth) {
        this.currentMonth = currentMonth;
    }

    public RealmList<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(RealmList<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
    }
}
