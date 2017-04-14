package com.santamaria.manejogastosmensuales.Domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.santamaria.manejogastosmensuales.app.MyApplication;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Santamaria on 13/04/2017.
 */

public class CategoryDetail extends RealmObject implements Parcelable{

    @PrimaryKey
    private int id;
    @Required
    private Date date;
    @Required
    private String detail;
    private float amount;

    public CategoryDetail() {
    }

    public CategoryDetail(String detail, float amount) {
        this.id = MyApplication.CategoryDetailID.incrementAndGet();
        this.date = new Date();
        this.detail = detail;
        this.amount = amount;
    }

    protected CategoryDetail(Parcel in) {
        id = in.readInt();
        detail = in.readString();
        amount = in.readFloat();
    }

    public static final Creator<CategoryDetail> CREATOR = new Creator<CategoryDetail>() {
        @Override
        public CategoryDetail createFromParcel(Parcel in) {
            return new CategoryDetail(in);
        }

        @Override
        public CategoryDetail[] newArray(int size) {
            return new CategoryDetail[size];
        }
    };

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(detail);
        parcel.writeFloat(amount);
    }
}
