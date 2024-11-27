package com.example.smd_assignment_4;

public class ShoppingItem {
    private String name;
    private int quantity;
    private double price;
    private int id; // Field for sequential ID

    // Default constructor
    public ShoppingItem() {
    }

    // Constructor with ID
    public ShoppingItem(String name, int quantity, double price, int id) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.id = id;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
