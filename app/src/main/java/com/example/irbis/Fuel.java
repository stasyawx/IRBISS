package com.example.irbis;

public class Fuel {
    private String id;
    private String name;
    private double price;
    private String description;
    private boolean isActive;

    public Fuel() {}

    public Fuel(String id, String name, double price, String description, boolean isActive) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.isActive = isActive;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}