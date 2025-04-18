package com.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class UserManager {
    private final Map<String, User> users;
    private final Map<Long, String> loggedInUsers;
    private static final String USERS_FILE = "users.json";
    private static final String LOGGED_IN_FILE = "logged_in.json";

    public UserManager() {
        users = new HashMap<>();
        loggedInUsers = new HashMap<>();
        loadUsers();
        loadLoggedInUsers();
    }

    public String registerUser(String name, long chatId) {
        String userId = generateRandomId(6);
        String cardId = generateRandomId(10);
        User user = new User(userId, name, 0, cardId, chatId);
        users.put(userId, user);
        loggedInUsers.put(chatId, userId);
        saveUsers();
        saveLoggedInUsers();
        return userId;
    }

    public void loginUser(long chatId, String userId) {
        if (users.containsKey(userId)) {
            loggedInUsers.put(chatId, userId);
            saveLoggedInUsers();
        }
    }

    public boolean isLoggedIn(long chatId) {
        return loggedInUsers.containsKey(chatId);
    }

    public String getLoggedInUserId(long chatId) {
        return loggedInUsers.get(chatId);
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public boolean containsUser(String userId) {
        return users.containsKey(userId);
    }

    public void addPoints(String userId, int points) {
        User user = users.get(userId);
        if (user != null) {
            user.addPoints(points);
            saveUsers();
        }
    }

    public Map<String, User> getAllUsers() {
        return users;
    }

    private String generateRandomId(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private void saveUsers() {
        try (FileWriter writer = new FileWriter(USERS_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (file.exists()) {
            try (FileReader reader = new FileReader(USERS_FILE)) {
                Gson gson = new Gson();
                Type type = new TypeToken<HashMap<String, User>>() {}.getType();
                Map<String, User> loadedUsers = gson.fromJson(reader, type);
                users.putAll(loadedUsers);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveLoggedInUsers() {
        try (FileWriter writer = new FileWriter(LOGGED_IN_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(loggedInUsers, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadLoggedInUsers() {
        File file = new File(LOGGED_IN_FILE);
        if (file.exists()) {
            try (FileReader reader = new FileReader(LOGGED_IN_FILE)) {
                Gson gson = new Gson();
                Type type = new TypeToken<HashMap<Long, String>>() {}.getType();
                Map<Long, String> loaded = gson.fromJson(reader, type);
                loggedInUsers.putAll(loaded);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}