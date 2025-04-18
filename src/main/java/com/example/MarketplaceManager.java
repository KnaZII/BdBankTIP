package com.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class MarketplaceManager {
    private final Map<String, Lot> lots;
    private static final String DATA_FILE = "marketplace.json";

    public MarketplaceManager() {
        lots = new HashMap<>();
        loadLots();
    }

    public String addLot(String userId, int price, String item) {
        String lotId = generateRandomId(6);
        Lot newLot = new Lot(lotId, userId, price, item);
        lots.put(lotId, newLot);
        saveLots();
        return lotId;
    }

    public Lot buyLot(String lotId) {
        Lot lot = lots.get(lotId);
        if (lot != null && !lot.isSold() && !lot.isDeleted()) {
            lot.setSold(true);
            saveLots();
            return lot;
        }
        return null;
    }

    public boolean deleteLot(String userId, String lotId) {
        Lot lot = lots.get(lotId);
        if (lot != null && lot.getUserId().equals(userId)) {
            lot.setDeleted(true);
            saveLots();
            return true;
        }
        return false;
    }

    public boolean restoreLot(String userId, String lotId) {
        Lot lot = lots.get(lotId);
        if (lot != null && lot.getUserId().equals(userId)) {
            lot.setDeleted(false);
            saveLots();
            return true;
        }
        return false;
    }

    public List<Lot> getActiveLots() {
        List<Lot> activeLots = new ArrayList<>();
        for (Lot lot : lots.values()) {
            if (!lot.isSold() && !lot.isDeleted()) {
                activeLots.add(lot);
            }
        }
        return activeLots;
    }

    public Lot getLotByIndex(int index) {
        List<Lot> activeLots = getActiveLots();
        if (index >= 0 && index < activeLots.size()) {
            return activeLots.get(index);
        }
        return null;
    }

    public List<Lot> getUserLots(String userId) {
        List<Lot> userLots = new ArrayList<>();
        for (Lot lot : lots.values()) {
            if (lot.getUserId().equals(userId)) {
                userLots.add(lot);
            }
        }
        return userLots;
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

    private void saveLots() {
        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(lots, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadLots() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (FileReader reader = new FileReader(DATA_FILE)) {
                Gson gson = new Gson();
                Type type = new TypeToken<HashMap<String, Lot>>() {}.getType();
                Map<String, Lot> loadedLots = gson.fromJson(reader, type);
                lots.putAll(loadedLots);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}