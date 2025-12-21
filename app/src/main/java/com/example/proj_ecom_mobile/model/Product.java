package com.example.proj_ecom_mobile.model;

import java.io.Serializable;

public class Product implements Serializable {
    private String id;
    private String name;
    private double price;
    private String description;
    private String imageUrl;
    private String category;
    private int stockS;
    private int stockM;
    private int stockL;
    private int stockXL;

    public Product() {
    }

    public Product(String id, String name, double price, String description, String imageUrl, String category, int stockS, int stockM, int stockL, int stockXL) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.stockS = stockS;
        this.stockM = stockM;
        this.stockL = stockL;
        this.stockXL = stockXL;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getStockS() { return stockS; }
    public void setStockS(int stockS) { this.stockS = stockS; }

    public int getStockM() { return stockM; }
    public void setStockM(int stockM) { this.stockM = stockM; }

    public int getStockL() { return stockL; }
    public void setStockL(int stockL) { this.stockL = stockL; }

    public int getStockXL() { return stockXL; }
    public void setStockXL(int stockXL) { this.stockXL = stockXL; }

    public int getTotalStock() {
        return stockS + stockM + stockL + stockXL;
    }
}