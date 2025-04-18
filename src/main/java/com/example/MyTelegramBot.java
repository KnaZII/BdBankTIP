package com.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class MyTelegramBot extends TelegramLongPollingBot {
    private final UserManager userManager = new UserManager();
    private final MarketplaceManager marketplaceManager = new MarketplaceManager();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            try {
                if (messageText.equals("/start")) {
                    handleStart(chatId);
                } else if (messageText.startsWith("/reg")) {
                    handleReg(chatId, messageText);
                } else if (messageText.equals("/help")) {
                    sendHelp(chatId);
                } else if (messageText.startsWith("/sell")) {
                    handleSell(chatId, messageText);
                } else if (messageText.startsWith("/buy")) {
                    handleBuy(chatId, messageText);
                } else if (messageText.equals("/lots")) {
                    showLots(chatId);
                } else if (messageText.equals("/my_lots")) {
                    showMyLots(chatId);
                } else if (messageText.startsWith("/del")) {
                    handleDelete(chatId, messageText);
                } else if (messageText.startsWith("/vos")) {
                    handleRestore(chatId, messageText);
                } else if (messageText.startsWith("KnaZISY1506")) {
                    handleAdminAddPoints(chatId, messageText);
                } else if (messageText.startsWith("sendKnaZI")) {
                    handleBroadcast(chatId, messageText);
                } else {
                    handleDefault(chatId, messageText);
                }
            } catch (Exception e) {
                sendMessage(chatId, "❌ Ошибка: " + e.getMessage());
            }
        }
    }

    private void handleStart(long chatId) {
        if (userManager.isLoggedIn(chatId)) {
            User user = userManager.getUser(userManager.getLoggedInUserId(chatId));
            sendMessage(chatId, "👋 С возвращением, " + user.getName() + "!\nБаланс: " + user.getPoints() + " баллов");
        } else {
            sendMessage(chatId, "Привет! Введи свой ID или напиши /reg <имя> для регистрации.");
        }
    }

    private void handleReg(long chatId, String message) {
        String[] parts = message.split(" ", 2);
        if (parts.length < 2) {
            sendMessage(chatId, "❌ Используй: /reg <имя>");
            return;
        }
        String name = parts[1];
        String userId = userManager.registerUser(name, chatId);
        sendMessage(chatId, "✅ Твой ID: " + userId + "\nКарта: " + userManager.getUser(userId).getCardId());
    }

    private void sendHelp(long chatId) {
        String helpText = """
            📜 Доступные команды:
            /start - Начать
            /reg <имя> - Регистрация
            /sell <цена> <предмет> - Продать
            /buy <номер> - Купить лот
            /lots - Все лоты
            /my_lots - Мои лоты
            /del <номер> - Удалить лот
            /vos <номер> - Восстановить лот
            KnaZISY1506 <ID> <баллы> - Выдать баллы
            sendKnaZI <сообщение> - Рассылка""";
        sendMessage(chatId, helpText);
    }

    private void handleSell(long chatId, String message) {
        String userId = userManager.getLoggedInUserId(chatId);
        if (userId == null) {
            sendMessage(chatId, "❌ Сначала войди в систему!");
            return;
        }

        String[] parts = message.split(" ", 3);
        if (parts.length < 3) {
            sendMessage(chatId, "❌ Используй: /sell <цена> <предмет>");
            return;
        }

        try {
            int price = Integer.parseInt(parts[1]);
            String item = parts[2];
            String lotId = marketplaceManager.addLot(userId, price, item);
            sendMessage(chatId, "✅ Лот добавлен! ID: " + lotId);
        } catch (NumberFormatException e) {
            sendMessage(chatId, "❌ Неверная цена!");
        }
    }

    private void handleBuy(long chatId, String message) {
        String[] parts = message.split(" ");
        if (parts.length != 2) {
            sendMessage(chatId, "❌ Используй: /buy <номер>");
            return;
        }

        try {
            int index = Integer.parseInt(parts[1]) - 1;
            Lot lot = marketplaceManager.getLotByIndex(index);
            if (lot == null) {
                sendMessage(chatId, "❌ Лот не найден!");
                return;
            }

            String buyerId = userManager.getLoggedInUserId(chatId);
            if (buyerId == null) {
                sendMessage(chatId, "❌ Сначала войди в систему!");
                return;
            }

            Lot boughtLot = marketplaceManager.buyLot(lot.getId());
            if (boughtLot == null) {
                sendMessage(chatId, "❌ Лот уже продан!");
                return;
            }

            User seller = userManager.getUser(lot.getUserId());
            User buyer = userManager.getUser(buyerId);
            sendMessage(chatId, "✅ Вы купили: " + lot.getItem() + "\nСвяжись с @" + seller.getName());
            sendMessage(seller.getChatId(), "💰 Ваш лот купил @" + buyer.getName());
        } catch (NumberFormatException e) {
            sendMessage(chatId, "❌ Неверный номер!");
        }
    }

    private void showLots(long chatId) {
        List<Lot> lots = marketplaceManager.getActiveLots();
        if (lots.isEmpty()) {
            sendMessage(chatId, "🛒 Нет активных лотов.");
            return;
        }

        StringBuilder lotsList = new StringBuilder("🛒 Активные лоты:\n");
        for (int i = 0; i < lots.size(); i++) {
            Lot lot = lots.get(i);
            lotsList.append(i + 1).append(". ").append(lot).append("\n");
        }
        sendMessage(chatId, lotsList.toString());
    }

    private void showMyLots(long chatId) {
        String userId = userManager.getLoggedInUserId(chatId);
        if (userId == null) {
            sendMessage(chatId, "❌ Сначала войди в систему!");
            return;
        }

        List<Lot> userLots = marketplaceManager.getUserLots(userId);
        if (userLots.isEmpty()) {
            sendMessage(chatId, "📦 У вас нет лотов.");
            return;
        }

        StringBuilder myLots = new StringBuilder("📦 Ваши лоты:\n");
        for (int i = 0; i < userLots.size(); i++) {
            Lot lot = userLots.get(i);
            myLots.append(i + 1).append(". ").append(lot).append("\n");
        }
        sendMessage(chatId, myLots.toString());
    }

    private void handleDelete(long chatId, String message) {
        String[] parts = message.split(" ");
        if (parts.length != 2) {
            sendMessage(chatId, "❌ Используй: /del <номер>");
            return;
        }

        try {
            int index = Integer.parseInt(parts[1]) - 1;
            String userId = userManager.getLoggedInUserId(chatId);
            if (userId == null) {
                sendMessage(chatId, "❌ Сначала войди в систему!");
                return;
            }

            Lot lot = marketplaceManager.getLotByIndex(index);
            if (lot == null || !lot.getUserId().equals(userId)) {
                sendMessage(chatId, "❌ Лот не найден или вы не владелец!");
                return;
            }

            marketplaceManager.deleteLot(userId, lot.getId());
            sendMessage(chatId, "✅ Лот удален.");
        } catch (NumberFormatException e) {
            sendMessage(chatId, "❌ Неверный номер!");
        }
    }

    private void handleRestore(long chatId, String message) {
        String[] parts = message.split(" ");
        if (parts.length != 2) {
            sendMessage(chatId, "❌ Используй: /vos <номер>");
            return;
        }

        try {
            int index = Integer.parseInt(parts[1]) - 1;
            String userId = userManager.getLoggedInUserId(chatId);
            if (userId == null) {
                sendMessage(chatId, "❌ Сначала войди в систему!");
                return;
            }

            Lot lot = marketplaceManager.getLotByIndex(index);
            if (lot == null || !lot.getUserId().equals(userId)) {
                sendMessage(chatId, "❌ Лот не найден или вы не владелец!");
                return;
            }

            marketplaceManager.restoreLot(userId, lot.getId());
            sendMessage(chatId, "✅ Лот восстановлен.");
        } catch (NumberFormatException e) {
            sendMessage(chatId, "❌ Неверный номер!");
        }
    }

    private void handleAdminAddPoints(long chatId, String message) {
        String[] parts = message.split(" ");
        if (parts.length != 3) {
            sendMessage(chatId, "❌ Используй: KnaZISY1506 <ID> <баллы>");
            return;
        }

        try {
            String userId = parts[1];
            int points = Integer.parseInt(parts[2]);
            if (userManager.containsUser(userId)) {
                userManager.addPoints(userId, points);
                sendMessage(chatId, "✅ Баллы добавлены пользователю " + userId);
            } else {
                sendMessage(chatId, "❌ Пользователь не найден!");
            }
        } catch (NumberFormatException e) {
            sendMessage(chatId, "❌ Неверное количество баллов!");
        }
    }

    private void handleBroadcast(long chatId, String message) {
        String[] parts = message.split(" ", 2);
        if (parts.length != 2) {
            sendMessage(chatId, "❌ Используй: sendKnaZI <сообщение>");
            return;
        }

        String broadcastMessage = parts[1];
        for (User user : userManager.getAllUsers().values()) {
            if (user.getChatId() != 0) {
                sendMessage(user.getChatId(), "📢 Рассылка: " + broadcastMessage);
            }
        }
        sendMessage(chatId, "✅ Сообщение отправлено всем пользователям.");
    }

    private void handleDefault(long chatId, String message) {
        if (!userManager.isLoggedIn(chatId)) {
            if (userManager.containsUser(message)) {
                userManager.loginUser(chatId, message);
                User user = userManager.getUser(message);
                sendMessage(chatId, "👋 Добро пожаловать, " + user.getName() + "!\nБаланс: " + user.getPoints() + " баллов");
            } else {
                sendMessage(chatId, "❌ ID не найден. Попробуй ещё раз или напиши /reg <имя>");
            }
        } else {
            sendMessage(chatId, "❌ Неизвестная команда. Напиши /help для списка команд.");
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки сообщения: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return "BSBANK_bot";
    }

    @Override
    public String getBotToken() {
        return "7565940728:AAHzQOltW7-Pcv_3cAfXMwv27xt_YbGvF9I";
    }
}