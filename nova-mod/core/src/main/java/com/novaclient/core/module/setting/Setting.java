package com.novaclient.core.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class Setting {
    protected final String name;
    protected final String description;
    protected boolean hidden;

    public Setting(String name, String description) {
        this.name = name;
        this.description = description;
        this.hidden = false;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isHidden() { return hidden; }
    public void setHidden(boolean hidden) { this.hidden = hidden; }

    public abstract JsonElement toJson();
    public abstract void fromJson(JsonElement element);
}
