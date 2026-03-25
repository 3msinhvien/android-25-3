package com.example.bt_nhom_25_3.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "orders")
public class Order {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int userId;
    public String status; // "Pending", "Paid"
    public double totalAmount;
    public long orderDate;

    public Order(int userId, String status, double totalAmount, long orderDate) {
        this.userId = userId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
    }
}
