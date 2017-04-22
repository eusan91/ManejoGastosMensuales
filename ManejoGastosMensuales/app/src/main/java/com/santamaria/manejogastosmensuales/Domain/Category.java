package com.santamaria.manejogastosmensuales.Domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.santamaria.manejogastosmensuales.app.MyApplication;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Santamaria on 09/04/2017.
 */

public class Category extends RealmObject implements Parcelable {

    @PrimaryKey
    private int id;
    @Required
    private String nombre;
    private String picture;
    private float total;
    private RealmList<CategoryDetail> categoryDetailList;

    public Category() {
    }

    public Category(String nombre) {
        this.id = MyApplication.CategoryID.incrementAndGet();
        this.nombre = nombre;
        this.picture = "";
        this.total = 0;
        categoryDetailList = new RealmList<>();
    }

    public Category(String nombre, String picture) {
        this.id = MyApplication.CategoryID.incrementAndGet();
        this.nombre = nombre;
        this.picture = picture;
        this.total = 0;
        categoryDetailList = new RealmList<>();
    }

    public Category(String nombre, String picture, float total) {
        this.id = MyApplication.CategoryID.incrementAndGet();
        this.nombre = nombre;
        this.picture = picture;
        this.total = total;
        categoryDetailList = new RealmList<>();
    }

    public Category(String nombre, String picture, float total, RealmList<CategoryDetail> categoryDetailList) {
        this.id = MyApplication.CategoryID.incrementAndGet();
        this.nombre = nombre;
        this.picture = picture;
        this.total = total;
        this.categoryDetailList = categoryDetailList;
    }

    protected Category(Parcel in) {
        id = in.readInt();
        nombre = in.readString();
        total = in.readFloat();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public RealmList<CategoryDetail> getCategoryDetailList() {
        return categoryDetailList;
    }

    public void setCategoryDetailList(RealmList<CategoryDetail> categoryDetailList) {
        this.categoryDetailList = categoryDetailList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(nombre);
        parcel.writeFloat(total);
    }
}
