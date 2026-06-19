package com.novaclient.core.cosmetics;

import com.google.gson.JsonObject;

public class CustomCape {
    private String id;
    private String name;
    private String filePath;
    private long createdAt;

    public CustomCape(String id, String name, String filePath) {
        this.id = id;
        this.name = name;
        this.filePath = filePath;
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getFilePath() { return filePath; }
    public long getCreatedAt() { return createdAt; }

    public void setName(String name) { this.name = name; }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", id);
        obj.addProperty("name", name);
        obj.addProperty("filePath", filePath);
        obj.addProperty("createdAt", createdAt);
        return obj;
    }

    public static CustomCape fromJson(JsonObject obj) {
        return new CustomCape(
            obj.get("id").getAsString(),
            obj.get("name").getAsString(),
            obj.get("filePath").getAsString()
        );
    }
}
