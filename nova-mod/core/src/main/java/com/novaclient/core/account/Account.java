package com.novaclient.core.account;

import java.util.UUID;

public class Account {
    private final String name;
    private final UUID uuid;
    private final AccountType type;
    private String accessToken;
    private String refreshToken;
    private long tokenExpiry;

    public Account(String name, UUID uuid, AccountType type) {
        this.name = name;
        this.uuid = uuid;
        this.type = type;
    }

    public String getName() { return name; }
    public UUID getUuid() { return uuid; }
    public AccountType getType() { return type; }
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String token) { this.accessToken = token; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String token) { this.refreshToken = token; }
    public long getTokenExpiry() { return tokenExpiry; }
    public void setTokenExpiry(long expiry) { this.tokenExpiry = expiry; }

    public boolean isExpired() {
        return System.currentTimeMillis() > tokenExpiry;
    }

    public boolean isMicrosoft() {
        return type == AccountType.MICROSOFT;
    }

    public enum AccountType {
        MICROSOFT,
        OFFLINE,
        MIGRATION
    }
}
