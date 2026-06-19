package com.novaclient.core.platform;

import java.util.UUID;

/**
 * Platform adapter interface that each Minecraft version adapter must implement.
 * This is the bridge between version-agnostic core code and Minecraft-specific APIs.
 */
public interface PlatformAdapter {

    // --- Player ---
    String getPlayerName();
    UUID getPlayerUUID();
    double getPlayerX();
    double getPlayerY();
    double getPlayerZ();
    float getPlayerYaw();
    float getPlayerPitch();
    boolean isPlayerSprinting();
    boolean isPlayerSneaking();
    boolean isPlayerOnGround();
    int getPlayerHealth();
    int getPlayerMaxHealth();
    int getPlayerFoodLevel();
    float getPlayerSaturation();

    // --- World ---
    String getWorldName();
    long getWorldTime();
    int getWorldDay();
    boolean isRaining();
    boolean isThundering();
    float getAmbientLight();
    int getBrightness();

    // --- Server ---
    boolean isOnMultiplayer();
    String getServerAddress();
    String getServerName();
    int getPing();
    int getFPS();

    // --- Rendering ---
    void drawRect(float left, float top, float right, float bottom, int color);
    void drawGradientRect(float left, float top, float right, float bottom, int topColor, int bottomColor);
    void drawGradientRectH(float left, float top, float right, float bottom, int leftColor, int rightColor);
    void drawCircle(float cx, float cy, float radius, int color, int segments);
    void drawString(String text, float x, float y, int color, boolean shadow);
    void drawString(String text, float x, float y, int color, float scale, boolean shadow);
    void drawCenteredString(String text, float x, float y, int color, boolean shadow);
    void drawTexture(String texturePath, float x, float y, float width, float height);
    void drawTexture(String texturePath, float x, float y, float u, float v, float uWidth, float vHeight, float width, float height, float textureWidth, float textureHeight);
    int getStringWidth(String text);
    int getStringHeight();
    void enableGL2D();
    void disableGL2D();
    void enableScissor(int x, int y, int width, int height);
    void disableScissor();
    void pushMatrix();
    void popMatrix();
    void translate(float x, float y, float z);
    void scale(float x, float y, float z);
    void rotate(float angle, float x, float y, float z);

    // --- Input ---
    boolean isKeyDown(int key);
    boolean isMouseButtonDown(int button);
    int getMouseX();
    int getMouseY();
    int getScreenWidth();
    int getScreenHeight();
    void sendChatMessage(String message);
    void displayScreen(Object screen);

    // --- Game ---
    long getSystemTime();
    int getTicksPerSecond();
    Object getPlayerEntity();
    Object getWorldObj();
    Object getMinecraftInstance();

    // --- Lifecycle ---
    void setPlatformAdapter(PlatformAdapter adapter);
}
