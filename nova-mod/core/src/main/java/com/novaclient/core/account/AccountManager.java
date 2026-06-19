package com.novaclient.core.account;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountManager {
    private static final AccountManager INSTANCE = new AccountManager();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final List<Account> accounts = new ArrayList<>();
    private Account activeAccount;
    private Path accountsFile;

    public static AccountManager getInstance() {
        return INSTANCE;
    }

    public void init() {
        accountsFile = Paths.get("novaclient", "accounts.json");
        try {
            Files.createDirectories(accountsFile.getParent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        load();
    }

    public Account addMicrosoftAccount(String name, UUID uuid, String accessToken, String refreshToken, long expiry) {
        Account account = new Account(name, uuid, Account.AccountType.MICROSOFT);
        account.setAccessToken(accessToken);
        account.setRefreshToken(refreshToken);
        account.setTokenExpiry(expiry);
        accounts.add(account);
        save();
        return account;
    }

    public Account addOfflineAccount(String name) {
        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes());
        Account account = new Account(name, uuid, Account.AccountType.OFFLINE);
        accounts.add(account);
        save();
        return account;
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
        if (activeAccount == account) {
            activeAccount = accounts.isEmpty() ? null : accounts.get(0);
        }
        save();
    }

    public void setActiveAccount(Account account) {
        this.activeAccount = account;
        save();
    }

    public Account getActiveAccount() {
        return activeAccount;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public Optional<Account> getAccountByName(String name) {
        return accounts.stream()
            .filter(a -> a.getName().equalsIgnoreCase(name))
            .findFirst();
    }

    public void switchToNext() {
        if (accounts.isEmpty()) return;
        int idx = accounts.indexOf(activeAccount);
        idx = (idx + 1) % accounts.size();
        setActiveAccount(accounts.get(idx));
    }

    public boolean hasMicrosoftAccount() {
        return accounts.stream().anyMatch(Account::isMicrosoft);
    }

    public void save() {
        JsonArray array = new JsonArray();
        for (Account account : accounts) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", account.getName());
            obj.addProperty("uuid", account.getUuid().toString());
            obj.addProperty("type", account.getType().name());
            if (account.getAccessToken() != null) {
                obj.addProperty("accessToken", account.getAccessToken());
            }
            if (account.getRefreshToken() != null) {
                obj.addProperty("refreshToken", account.getRefreshToken());
            }
            obj.addProperty("tokenExpiry", account.getTokenExpiry());
            array.add(obj);
        }

        JsonObject root = new JsonObject();
        root.add("accounts", array);
        if (activeAccount != null) {
            root.addProperty("activeAccount", activeAccount.getName());
        }

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(accountsFile.toFile()), StandardCharsets.UTF_8)) {
            gson.toJson(root, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        if (!Files.exists(accountsFile)) return;

        try (Reader reader = new InputStreamReader(new FileInputStream(accountsFile.toFile()), StandardCharsets.UTF_8)) {
            JsonObject root = gson.fromJson(reader, JsonObject.class);
            if (root == null || !root.has("accounts")) return;

            accounts.clear();
            JsonArray array = root.getAsJsonArray("accounts");
            for (JsonElement element : array) {
                JsonObject obj = element.getAsJsonObject();
                String name = obj.get("name").getAsString();
                UUID uuid = UUID.fromString(obj.get("uuid").getAsString());
                Account.AccountType type = Account.AccountType.valueOf(obj.get("type").getAsString());

                Account account = new Account(name, uuid, type);
                if (obj.has("accessToken")) {
                    account.setAccessToken(obj.get("accessToken").getAsString());
                }
                if (obj.has("refreshToken")) {
                    account.setRefreshToken(obj.get("refreshToken").getAsString());
                }
                if (obj.has("tokenExpiry")) {
                    account.setTokenExpiry(obj.get("tokenExpiry").getAsLong());
                }
                accounts.add(account);
            }

            if (root.has("activeAccount")) {
                String activeName = root.get("activeAccount").getAsString();
                getAccountByName(activeName).ifPresent(a -> activeAccount = a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
