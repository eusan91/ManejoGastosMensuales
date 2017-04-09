package com.santamaria.manejogastosmensuales.Domain;

/**
 * Created by Santamaria on 09/04/2017.
 */

public class Category {

    private String nombre;
    private Object picture;
    private float total;

    public Category(String nombre, Object picture, float total) {
        this.nombre = nombre;
        this.picture = picture;
        this.total = total;
    }

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
}
