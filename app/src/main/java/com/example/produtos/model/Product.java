package com.example.produtos.model;

import android.graphics.Bitmap;

public class Product {
    String id;
    String name;
    String price;
    String description;
    Bitmap photo;

    public Product(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Product(String id, String name, String price, String description, Bitmap photo) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public Bitmap getPhoto() {
        return photo;
    }
}
