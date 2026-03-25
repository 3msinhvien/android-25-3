package com.example.bt_nhom_25_3.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class Product {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public double price;
    public int categoryId;
    public String imageUrl;

    public Product(String name, double price, int categoryId, String imageUrl) {
        this.name = name;
        this.price = price;
        this.categoryId = categoryId;
        this.imageUrl = imageUrl;
    }
}
