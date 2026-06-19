package com.novaclient.adapter;

import com.novaclient.core.NovaCore;
import com.novaclient.core.platform.Platform;
import com.novaclient.core.platform.PlatformAdapter;
import net.fabricmc.api.ClientModInitializer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Adapter26_1_2Fabric implements ClientModInitializer {

    private static Object mcInstance;
    private static Class<?> mcClass;
    private static Class<?> drawContextClass;
    private static Class<?> renderSystemClass;
    private static Class<?> glfwClass;

    private static Object currentDrawContext;

    public static void setCurrentDrawContext(Object ctx) {
        currentDrawContext = ctx;
    }

    public static Object getCurrentDrawContext() {
        return currentDrawContext;
    }

    @Override
    public void onInitializeClient() {
        initialize();
    }

    public static void initialize() {
        System.out.println("[Nova Client] Fabric 1.21.x adapter initializing...");

        try {
            mcClass = findClass("net.minecraft.client.MinecraftClient");
            drawContextClass = findClass("net.minecraft.client.gui.DrawContext");
            renderSystemClass = findClass("com.mojang.blaze3d.systems.RenderSystem");
            glfwClass = findClass("org.lwjgl.glfw.GLFW");

            if (mcClass != null) {
                Method getInstance = mcClass.getMethod("getInstance");
                mcInstance = getInstance.invoke(null);
            }

            Platform.setAdapter(new ReflectionAdapter());
            System.out.println("[Nova Client] Platform adapter registered (reflection)");

            NovaCore.getInstance().init(Platform.get());

            System.out.println("[Nova Client] Fabric 1.21.x adapter initialized!");
            System.out.println("[Nova Client] Right Shift = Mod Menu | Home = HUD Editor | Right Ctrl = Disable All");
        } catch (Exception e) {
            System.err.println("[Nova Client] Failed to initialize Fabric 1.21.x adapter: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Class<?> findClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static Object getMc() {
        if (mcInstance == null && mcClass != null) {
            try {
                Method getInstance = mcClass.getMethod("getInstance");
                mcInstance = getInstance.invoke(null);
            } catch (Exception e) { return null; }
        }
        return mcInstance;
    }

    private static Object getPlayer() {
        try {
            Object mc = getMc();
            if (mc == null) return null;
            Field f = mcClass.getDeclaredField("player");
            return f.get(mc);
        } catch (Exception e) { return null; }
    }

    private static Object getWorld() {
        try {
            Object mc = getMc();
            if (mc == null) return null;
            Field f = mcClass.getDeclaredField("world");
            return f.get(mc);
        } catch (Exception e) { return null; }
    }

    private static float getFloatField(Object obj, String name) {
        try {
            Field f = obj.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.getFloat(obj);
        } catch (Exception e) { return 0; }
    }

    private static double getDoubleField(Object obj, String name) {
        try {
            Field f = obj.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.getDouble(obj);
        } catch (Exception e) { return 0; }
    }

    private static boolean getBooleanField(Object obj, String name) {
        try {
            Field f = obj.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.getBoolean(obj);
        } catch (Exception e) { return false; }
        }

    private static boolean invokeBoolean(Object obj, String name) {
        try {
            Method m = obj.getClass().getDeclaredMethod(name);
            m.setAccessible(true);
            return (boolean) m.invoke(obj);
        } catch (Exception e) { return false; }
    }

    private static class ReflectionAdapter implements PlatformAdapter {

        @Override public String getPlayerName() {
            Object p = getPlayer();
            if (p == null) return "";
            try {
                Method getName = p.getClass().getDeclaredMethod("getName");
                Object text = getName.invoke(p);
                Method getString = text.getClass().getDeclaredMethod("getString");
                return (String) getString.invoke(text);
            } catch (Exception e) { return ""; }
        }

        @Override public java.util.UUID getPlayerUUID() {
            Object p = getPlayer();
            if (p == null) return java.util.UUID.randomUUID();
            try {
                Method m = p.getClass().getDeclaredMethod("getUuid");
                return (java.util.UUID) m.invoke(p);
            } catch (Exception e) { return java.util.UUID.randomUUID(); }
        }

        @Override public double getPlayerX() { Object p = getPlayer(); return p != null ? getDoubleField(p, "getX") : 0; }
        @Override public double getPlayerY() { Object p = getPlayer(); return p != null ? getDoubleField(p, "getY") : 0; }
        @Override public double getPlayerZ() { Object p = getPlayer(); return p != null ? getDoubleField(p, "getZ") : 0; }
        @Override public float getPlayerYaw() { Object p = getPlayer(); return p != null ? getFloatField(p, "yaw") : 0; }
        @Override public float getPlayerPitch() { Object p = getPlayer(); return p != null ? getFloatField(p, "pitch") : 0; }
        @Override public boolean isPlayerSprinting() { Object p = getPlayer(); return p != null && invokeBoolean(p, "isSprinting"); }
        @Override public boolean isPlayerSneaking() { Object p = getPlayer(); return p != null && invokeBoolean(p, "isSneaking"); }
        @Override public boolean isPlayerOnGround() { Object p = getPlayer(); return p != null && getBooleanField(p, "onGround"); }
        @Override public int getPlayerHealth() { Object p = getPlayer(); return p != null ? (int) getFloatField(p, "getHealth") : 0; }
        @Override public int getPlayerMaxHealth() { return 20; }
        @Override public int getPlayerFoodLevel() { return 20; }
        @Override public float getPlayerSaturation() { return 5.0f; }

        @Override public String getWorldName() {
            Object w = getWorld();
            if (w == null) return "";
            try {
                Method getRegistryKey = w.getClass().getDeclaredMethod("getRegistryKey");
                Object key = getRegistryKey.invoke(w);
                Method getValue = key.getClass().getDeclaredMethod("getValue");
                return getValue.invoke(key).toString();
            } catch (Exception e) { return "unknown"; }
        }
        @Override public long getWorldTime() { Object w = getWorld(); return w != null ? (long) getDoubleField(w, "getTimeOfDay") : 0; }
        @Override public int getWorldDay() { return 0; }
        @Override public boolean isRaining() { return false; }
        @Override public boolean isThundering() { return false; }
        @Override public float getAmbientLight() { return 0; }
        @Override public int getBrightness() { return 0; }

        @Override public boolean isOnMultiplayer() {
            try {
                Object mc = getMc();
                if (mc == null) return false;
                Method m = mcClass.getDeclaredMethod("getCurrentServerEntry");
                return m.invoke(mc) != null;
            } catch (Exception e) { return false; }
        }
        @Override public String getServerAddress() { return isOnMultiplayer() ? "Multiplayer" : "Singleplayer"; }
        @Override public String getServerName() { return isOnMultiplayer() ? "Multiplayer" : "Singleplayer"; }
        @Override public int getPing() { return 0; }
        @Override public int getFPS() {
            try {
                Object mc = getMc();
                if (mc == null) return 0;
                Method m = mcClass.getDeclaredMethod("getCurrentFps");
                return (int) m.invoke(mc);
            } catch (Exception e) { return 0; }
        }

        @Override
        public void drawRect(float left, float top, float right, float bottom, int color) {
            if (currentDrawContext == null) return;
            try {
                Method fill = drawContextClass.getMethod("fill", int.class, int.class, int.class, int.class, int.class);
                fill.invoke(currentDrawContext, (int) left, (int) top, (int) right, (int) bottom, color);
            } catch (Exception ignored) {}
        }

        @Override
        public void drawGradientRect(float left, float top, float right, float bottom, int colorTop, int colorBottom) {
            if (currentDrawContext == null) return;
            try {
                Method m = drawContextClass.getMethod("fillGradient", int.class, int.class, int.class, int.class, int.class, int.class, int.class);
                m.invoke(currentDrawContext, (int) left, (int) top, (int) right, (int) bottom, colorTop, colorBottom, 0);
            } catch (Exception ignored) {
                drawRect(left, top, right, bottom, colorTop);
            }
        }

        @Override
        public void drawGradientRectH(float left, float top, float right, float bottom, int colorLeft, int colorRight) {
            drawGradientRect(left, top, right, bottom, colorLeft, colorRight);
        }

        @Override
        public void drawCircle(float cx, float cy, float radius, int color, int segments) {
            drawRect(cx - radius, cy - radius, cx + radius, cy + radius, color);
        }

        @Override
        public void drawString(String text, float x, float y, int color, boolean shadow) {
            if (currentDrawContext == null) return;
            try {
                Object mc = getMc();
                if (mc == null) return;
                Field textRendererField = mcClass.getDeclaredField("textRenderer");
                Object textRenderer = textRendererField.get(mc);
                if (textRenderer == null) return;

                Class<?> textClass = findClass("net.minecraft.text.Text");
                if (textClass == null) return;
                Method literal = textClass.getMethod("literal", String.class);
                Object textObj = literal.invoke(null, text);

                if (shadow) {
                    Method m = drawContextClass.getMethod("drawTextWithShadow",
                        textRenderer.getClass(), textClass, int.class, int.class, int.class);
                    m.invoke(currentDrawContext, textRenderer, textObj, (int) x, (int) y, color);
                } else {
                    Method m = drawContextClass.getMethod("drawText",
                        textRenderer.getClass(), textClass, int.class, int.class, int.class, boolean.class);
                    m.invoke(currentDrawContext, textRenderer, textObj, (int) x, (int) y, color, false);
                }
            } catch (Exception ignored) {}
        }

        @Override
        public void drawString(String text, float x, float y, int color, float scale, boolean shadow) {
            drawString(text, x, y, color, shadow);
        }

        @Override
        public void drawCenteredString(String text, float x, float y, int color, boolean shadow) {
            int w = getStringWidth(text);
            drawString(text, x - w / 2.0f, y, color, shadow);
        }

        @Override public void drawTexture(String p, float x, float y, float w, float h) {}
        @Override public void drawTexture(String p, float x, float y, float u, float v, float uW, float vH, float w, float h, float tW, float tH) {}

        @Override
        public int getStringWidth(String text) {
            try {
                Object mc = getMc();
                if (mc == null) return text.length() * 6;
                Field f = mcClass.getDeclaredField("textRenderer");
                Object tr = f.get(mc);
                if (tr == null) return text.length() * 6;
                Method m = tr.getClass().getMethod("getWidth", String.class);
                return (int) m.invoke(tr, text);
            } catch (Exception e) { return text.length() * 6; }
        }

        @Override
        public int getStringHeight() {
            try {
                Object mc = getMc();
                if (mc == null) return 9;
                Field f = mcClass.getDeclaredField("textRenderer");
                Object tr = f.get(mc);
                if (tr == null) return 9;
                Field hField = tr.getClass().getField("fontHeight");
                return hField.getInt(tr);
            } catch (Exception e) { return 9; }
        }

        @Override public void enableGL2D() {}
        @Override public void disableGL2D() {}
        @Override public void enableScissor(int x, int y, int w, int h) {
            rsInvoke("enableScissor", new Class<?>[]{int.class, int.class, int.class, int.class}, new Object[]{x, y, w, h});
        }
        @Override public void disableScissor() {
            rsInvoke("disableScissor", new Class<?>[]{}, new Object[]{});
        }
        @Override public void pushMatrix() {}
        @Override public void popMatrix() {}
        @Override public void translate(float x, float y, float z) {}
        @Override public void scale(float x, float y, float z) {}
        @Override public void rotate(float a, float x, float y, float z) {}

        private void rsInvoke(String methodName, Class<?>[] paramTypes, Object[] args) {
            try {
                if (renderSystemClass == null) return;
                Method m = renderSystemClass.getDeclaredMethod(methodName, paramTypes);
                m.invoke(null, args);
            } catch (Exception ignored) {}
        }

        @Override
        public boolean isKeyDown(int key) {
            try {
                Object mc = getMc();
                if (mc == null) return false;
                Method getWindow = mcClass.getMethod("getWindow");
                Object window = getWindow.invoke(mc);
                Method getHandle = window.getClass().getMethod("getHandle");
                long handle = (long) getHandle.invoke(window);
                Method glfwGetKey = glfwClass.getMethod("glfwGetKey", long.class, int.class);
                return (int) glfwGetKey.invoke(null, handle, key) == 1;
            } catch (Exception e) { return false; }
        }

        @Override
        public boolean isMouseButtonDown(int button) {
            try {
                Object mc = getMc();
                if (mc == null) return false;
                Method getWindow = mcClass.getMethod("getWindow");
                Object window = getWindow.invoke(mc);
                Method getHandle = window.getClass().getMethod("getHandle");
                long handle = (long) getHandle.invoke(window);
                Method glfwGetMouseButton = glfwClass.getMethod("glfwGetMouseButton", long.class, int.class);
                return (int) glfwGetMouseButton.invoke(null, handle, button) == 1;
            } catch (Exception e) { return false; }
        }

        @Override public int getMouseX() { return 0; }
        @Override public int getMouseY() { return 0; }

        @Override
        public int getScreenWidth() {
            try {
                Object mc = getMc();
                if (mc == null) return 854;
                Method getWindow = mcClass.getMethod("getWindow");
                Object window = getWindow.invoke(mc);
                Method getScaledWidth = window.getClass().getMethod("getScaledWidth");
                return (int) getScaledWidth.invoke(window);
            } catch (Exception e) { return 854; }
        }

        @Override
        public int getScreenHeight() {
            try {
                Object mc = getMc();
                if (mc == null) return 480;
                Method getWindow = mcClass.getMethod("getWindow");
                Object window = getWindow.invoke(mc);
                Method getScaledHeight = window.getClass().getMethod("getScaledHeight");
                return (int) getScaledHeight.invoke(window);
            } catch (Exception e) { return 480; }
        }

        @Override
        public void sendChatMessage(String message) {
            try {
                Object mc = getMc();
                if (mc == null) return;
                Method getNetworkHandler = mcClass.getDeclaredMethod("getNetworkHandler");
                Object networkHandler = getNetworkHandler.invoke(mc);
                if (networkHandler != null) {
                    Method sendChat = networkHandler.getClass().getMethod("sendChatMessage", String.class);
                    sendChat.invoke(networkHandler, message);
                }
            } catch (Exception ignored) {}
        }

        @Override
        public void displayScreen(Object screen) {
            try {
                Object mc = getMc();
                if (mc == null) return;
                Class<?> screenClass = findClass("net.minecraft.client.gui.screen.Screen");
                if (screenClass != null && screenClass.isInstance(screen)) {
                    Method setScreen = mcClass.getMethod("setScreen", screenClass);
                    setScreen.invoke(mc, screen);
                }
            } catch (Exception ignored) {}
        }

        @Override public long getSystemTime() { return System.currentTimeMillis(); }
        @Override public int getTicksPerSecond() { return getFPS(); }
        @Override public Object getPlayerEntity() { return getPlayer(); }
        @Override public Object getWorldObj() { return getWorld(); }
        @Override public Object getMinecraftInstance() { return getMc(); }

        @Override
        public void setPlatformAdapter(PlatformAdapter adapter) {
            Platform.setAdapter(adapter);
        }
    }
}
