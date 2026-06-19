package com.novaclient.core.account;

import com.google.gson.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MicrosoftAuth {
    private static final String CLIENT_ID = "d47971a7-c000-45f0-a43f-75b2644e4b63";
    private static final String AUTH_URL = "https://login.live.com/oauth20_authorize.srf";
    private static final String TOKEN_URL = "https://login.live.com/oauth20_token.srf";
    private static final String XBOX_AUTH_URL = "https://user.auth.xboxlive.com/user/authenticate";
    private static final String XBOX_XSTS_URL = "https://xsts.auth.xboxlive.com/xsts/authorize";
    private static final String MC_SESSION_URL = "https://api.minecraftservices.com/authentication/login_with_xbox";
    private static final String MC_PROFILE_URL = "https://api.minecraftservices.com/minecraft/profile";
    private static final String SCOPE = "service::user.auth.xboxlive.com::MBI_SSL";

    private String deviceCode;
    private String userCode;
    private long expiryTime;

    public CompletableFuture<AuthResult> startDeviceCodeFlow() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Step 1: Get device code
                String deviceCodeBody = "client_id=" + CLIENT_ID + "&scope=" + URLEncoder.encode(SCOPE, StandardCharsets.UTF_8.name());
                String deviceCodeResponse = httpPost("https://login.live.com/oauth20_connect.srf", deviceCodeBody);
                JsonObject deviceCodeJson = JsonParser.parseString(deviceCodeResponse).getAsJsonObject();
                this.deviceCode = deviceCodeJson.get("device_code").getAsString();
                this.userCode = deviceCodeJson.get("user_code").getAsString();
                this.expiryTime = System.currentTimeMillis() + (deviceCodeJson.get("expires_in").getAsLong() * 1000);

                return new AuthResult(true, "Please go to: " + userCode, null);
            } catch (Exception e) {
                return new AuthResult(false, "Failed to start auth: " + e.getMessage(), null);
            }
        });
    }

    public CompletableFuture<AuthResult> pollForToken() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String body = "client_id=" + CLIENT_ID
                    + "&grant_type=urn:ietf:params:oauth:grant-type:device_code"
                    + "&device_code=" + deviceCode;
                String response = httpPost(TOKEN_URL, body);
                JsonObject json = JsonParser.parseString(response).getAsJsonObject();

                if (json.has("error")) {
                    String error = json.get("error").getAsString();
                    if ("authorization_pending".equals(error)) {
                        return new AuthResult(true, "Waiting for authorization...", null);
                    }
                    return new AuthResult(false, "Auth error: " + error, null);
                }

                String accessToken = json.get("access_token").getAsString();
                String refreshToken = json.has("refresh_token") ? json.get("refresh_token").getAsString() : null;

                // Step 2: Xbox Live auth
                String xboxToken = authenticateXbox(accessToken);
                if (xboxToken == null) return new AuthResult(false, "Xbox auth failed", null);

                // Step 3: XSTS auth
                String xstsToken = authenticateXSTS(xboxToken);
                if (xstsToken == null) return new AuthResult(false, "XSTS auth failed", null);

                // Step 4: Minecraft session
                String mcAccessToken = authenticateMinecraft(xstsToken);
                if (mcAccessToken == null) return new AuthResult(false, "MC auth failed", null);

                // Step 5: Get profile
                MinecraftProfile profile = getProfile(mcAccessToken);
                if (profile == null) return new AuthResult(false, "Failed to get profile", null);

                return new AuthResult(false, "Success", new AuthData(profile.name, profile.uuid, mcAccessToken, refreshToken));

            } catch (Exception e) {
                return new AuthResult(false, "Error: " + e.getMessage(), null);
            }
        });
    }

    private String authenticateXbox(String accessToken) throws Exception {
        JsonObject body = new JsonObject();
        JsonObject properties = new JsonObject();
        properties.addProperty("AuthMethod", "RPS");
        properties.addProperty("SiteName", "user.auth.xboxlive.com");
        properties.addProperty("RpsTicket", accessToken);
        body.add("Properties", properties);
        body.addProperty("RelyingParty", "http://auth.xboxlive.com");
        body.addProperty("TokenType", "JWT");

        String response = httpPostJson(XBOX_AUTH_URL, body.toString());
        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
        return json.getAsJsonArray("TokenIssues").get(0).getAsJsonObject().get("Token").getAsString();
    }

    private String authenticateXSTS(String xboxToken) throws Exception {
        JsonObject body = new JsonObject();
        JsonArray tokens = new JsonArray();
        tokens.add(xboxToken);
        body.add("TokenIds", tokens);
        body.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
        body.addProperty("TokenType", "JWT");

        String response = httpPostJson(XBOX_XSTS_URL, body.toString());
        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
        if (json.has("XErr")) {
            return null;
        }
        return json.get("Token").getAsString();
    }

    private String authenticateMinecraft(String xstsToken) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("identityToken", "XBL3.0 x=" + xstsToken);

        String response = httpPostJson(MC_SESSION_URL, body.toString());
        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
        return json.get("access_token").getAsString();
    }

    private MinecraftProfile getProfile(String mcAccessToken) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(MC_PROFILE_URL).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + mcAccessToken);

        if (conn.getResponseCode() != 200) return null;

        String body;
        try (Scanner s = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {
            body = s.useDelimiter("\\A").hasNext() ? s.next() : "";
        }

        JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        return new MinecraftProfile(
            json.get("name").getAsString(),
            java.util.UUID.fromString(json.get("id").getAsString())
        );
    }

    private String httpPost(String urlString, String body) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }
        try (Scanner s = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {
            return s.useDelimiter("\\A").hasNext() ? s.next() : "";
        }
    }

    private String httpPostJson(String urlString, String json) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
        try (Scanner s = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {
            return s.useDelimiter("\\A").hasNext() ? s.next() : "";
        }
    }

    public String getUserCode() { return userCode; }
    public boolean isExpired() { return System.currentTimeMillis() > expiryTime; }

    public static class AuthResult {
        public final boolean pending;
        public final String message;
        public final AuthData data;

        public AuthResult(boolean pending, String message, AuthData data) {
            this.pending = pending;
            this.message = message;
            this.data = data;
        }
    }

    public static class AuthData {
        public final String name;
        public final java.util.UUID uuid;
        public final String accessToken;
        public final String refreshToken;

        public AuthData(String name, java.util.UUID uuid, String accessToken, String refreshToken) {
            this.name = name;
            this.uuid = uuid;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }

    private static class MinecraftProfile {
        final String name;
        final java.util.UUID uuid;

        MinecraftProfile(String name, java.util.UUID uuid) {
            this.name = name;
            this.uuid = uuid;
        }
    }
}
