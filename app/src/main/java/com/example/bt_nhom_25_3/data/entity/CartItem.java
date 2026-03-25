package com.example.bt_nhom_25_3.data.entity;

public class CartItem {
    public String productName;
    public int quantity;
    public double price;
    public double subTotal;

    public CartItem(String productName, int quantity, double price) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.subTotal = quantity * price;
    }
}
