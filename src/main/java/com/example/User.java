package com.example;

public class User {
    private String id;
    private String name;
    private int points;
    private String cardId;
    private long chatId;

    public User() {}

    public User(String id, String name, int points, String cardId, long chatId) {
        this.id = id;
        this.name = name;
        this.points = points;
        this.cardId = cardId;
        this.chatId = chatId;
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public String getName() { return name; }
    public int getPoints() { return points; }
    public void addPoints(int points) { this.points += points; }
    public void removePoints(int points) { this.points -= points; }
    public String getCardId() { return cardId; }
    public long getChatId() { return chatId; }
    public void setChatId(long chatId) { this.chatId = chatId; }

    @Override
    public String toString() {
        return "ID: " + id + ", Имя: " + name + ", Баллы: " + points;
    }
}