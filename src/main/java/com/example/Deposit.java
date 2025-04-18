package com.example;

import java.time.LocalDate;

public class Deposit {
    private String userId;
    private double amount;
    private LocalDate startDate;
    private LocalDate lastInterestDate;
    private boolean isActive;

    public Deposit(String userId, double amount) {
        this.userId = userId;
        this.amount = amount;
        this.startDate = LocalDate.now();
        this.lastInterestDate = LocalDate.now();
        this.isActive = true;
    }

    public String getUserId() {
        return userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getLastInterestDate() {
        return lastInterestDate;
    }

    public void setLastInterestDate(LocalDate lastInterestDate) {
        this.lastInterestDate = lastInterestDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void close() {
        this.isActive = false;
    }
}