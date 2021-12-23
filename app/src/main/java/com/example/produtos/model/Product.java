package com.example.produtos.model;

import android.graphics.Bitmap;

// Classe que guarda as informações dos produtos.
public class Product {
    String id;
    String name;
    String price;
    String description;

    public Product(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Product(String id, String name, String price, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
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
}
