package edu.college.wms.model;

import java.math.BigDecimal;

public class Product {
    private int id;
    private String name;
    private String category;
    private BigDecimal price;
    private int quantity;
    private String description;
    private String type;
    private boolean active;
    private boolean lowStockTracked;
    private int lowStockThreshold;

    public Product(int id, String name, String category, BigDecimal price, int quantity,
                   String description, String type, boolean active,
                   boolean lowStockTracked, int lowStockThreshold) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
        this.type = type;
        this.active = active;
        this.lowStockTracked = lowStockTracked;
        this.lowStockThreshold = lowStockThreshold;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public BigDecimal getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public boolean isActive() { return active; }
    public boolean isLowStockTracked() { return lowStockTracked; }
    public int getLowStockThreshold() { return lowStockThreshold; }

    public void setId(int id) { this.id = id; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
