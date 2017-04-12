package com.santamaria.manejogastosmensuales.Domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Santamaria on 09/04/2017.
 */

public class Category implements Parcelable {

    private String nombre;
    private Object picture;
    private float total;

    public Category(String nombre, Object picture, float total) {
        this.nombre = nombre;
        this.picture = picture;
        this.total = total;
    }

    protected Category(Parcel in) {
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Object getPicture() {
        return picture;
    }

    public void setPicture(Object picture) {
        this.picture = picture;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nombre);
        parcel.writeFloat(total);
    }
}
