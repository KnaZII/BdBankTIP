package com.example;

public class Lot {
    private String id;
    private String userId;
    private int price;
    private String item;
    private boolean isSold;
    private boolean isDeleted;

    public Lot(String id, String userId, int price, String item) {
        this.id = id;
        this.userId = userId;
        this.price = price;
        this.item = item;
        this.isSold = false;
        this.isDeleted = false;
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public int getPrice() { return price; }
    public String getItem() { return item; }
    public boolean isSold() { return isSold; }
    public void setSold(boolean sold) { isSold = sold; }
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    @Override
    public String toString() {
        return "Лот #" + id + ": " + item + " за " + price + " руб.";
    }
}