package com.novaclient.adapter;

import com.novaclient.core.NovaCore;
import com.novaclient.core.event.*;
import com.novaclient.core.platform.PlatformAdapter;
import com.novaclient.core.platform.Platform;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Adapter7_10 {

    private static Object mcInstance;
    private static Class<?> mcClass;

    private static boolean wasRightShiftDown = false;
    private static boolean wasHomeDown = false;
    private static boolean wasMouseDown = false;

    static {
        initialize();
    }

    public static void initialize() {
        System.out.println("[Nova Client] Forge 1.7.10 adapter initializing...");

        try {
            mcClass = findClass("net.minecraft.client.Minecraft");
            Method getMinecraft = findMethod(mcClass, new String[]{"getMinecraft", "func_71410_x"});
            mcInstance = getMinecraft.invoke(null);

            Platform.setAdapter(new ReflectionAdapter());
            System.out.println("[Nova Client] Platform adapter registered via reflection");

            NovaCore.getInstance().init(Platform.get());

            MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());

            System.out.println("[Nova Client] Forge 1.7.10 adapter initialized!");
        } catch (Exception e) {
            System.err.println("[Nova Client] Failed to initialize Forge adapter: " + e.getMessage());
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

    private static Method findMethod(Class<?> clazz, String[] names, Class<?>... parameterTypes) {
        for (String name : names) {
            try {
                Method m = clazz.getDeclaredMethod(name, parameterTypes);
                m.setAccessible(true);
                return m;
            } catch (Exception ignored) {}
        }
        return null;
    }

    private static Field findField(Class<?> clazz, String[] names) {
        for (String name : names) {
            try {
                Field f = clazz.getDeclaredField(name);
                f.setAccessible(true);
                return f;
            } catch (Exception ignored) {}
        }
        return null;
    }

    public static class ForgeEventHandler {

        @SubscribeEvent
        public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
            if (!NovaCore.getInstance().isInitialized()) return;
            if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;

            try {
                Object mc = mcInstance;
                if (mc == null) return;

                Field displayWidthField = findField(mcClass, new String[]{"displayWidth", "field_71443_c"});
                Field displayHeightField = findField(mcClass, new String[]{"displayHeight", "field_71440_d"});
                float w = displayWidthField.getInt(mc);
                float h = displayHeightField.getInt(mc);

                float partialTicks = event.partialTicks;

                Render2DEvent renderEvent = new Render2DEvent(partialTicks);
                EventBus.getInstance().fire(renderEvent);

                NovaCore.getInstance().getHudEditor().render(0, 0, partialTicks);

                if (NovaCore.getInstance().isClickGuiOpen()) {
                    Class<?> mouse = findClass("org.lwjgl.input.Mouse");
                    int mouseX = (int) mouse.getMethod("getX").invoke(null) * (int)w / displayWidthField.getInt(mc);
                    int mouseY = (int)h - (int) mouse.getMethod("getY").invoke(null) * (int)h / displayHeightField.getInt(mc) - 1;
                    NovaCore.getInstance().getClickGui().render(mouseX, mouseY, partialTicks);

                    boolean mouseDown = (boolean) mouse.getMethod("isButtonDown", int.class).invoke(null, 0);
                    if (mouseDown && !wasMouseDown) {
                        NovaCore.getInstance().getClickGui().mouseClicked(mouseX, mouseY, 0);
                    } else if (!mouseDown && wasMouseDown) {
                        NovaCore.getInstance().getClickGui().mouseReleased(mouseX, mouseY, 0);
                    }
                    wasMouseDown = mouseDown;
                }
            } catch (Exception e) {
                System.err.println("[Nova Client] Render error: " + e.getMessage());
            }
        }

        @SubscribeEvent
        public void onClientTick(TickEvent.ClientTickEvent event) {
            if (!NovaCore.getInstance().isInitialized()) return;
            if (event.phase != TickEvent.Phase.END) return;

            try {
                Object mc = mcInstance;
                if (mc == null) return;

                boolean rightShiftDown = false;
                try {
                    Class<?> keyboardClass = findClass("org.lwjgl.input.Keyboard");
                    Method isKeyDown = keyboardClass.getMethod("isKeyDown", int.class);
                    rightShiftDown = (boolean) isKeyDown.invoke(null, 100);
                } catch (Exception ignored) {}

                if (rightShiftDown && !wasRightShiftDown) {
                    NovaCore.getInstance().setClickGuiOpen(!NovaCore.getInstance().isClickGuiOpen());
                    if (NovaCore.getInstance().isClickGuiOpen()) {
                        Field displayWidthField = findField(mcClass, new String[]{"displayWidth", "field_71443_c"});
                        Field displayHeightField = findField(mcClass, new String[]{"displayHeight", "field_71440_d"});
                        NovaCore.getInstance().getClickGui().init(displayWidthField.getInt(mc), displayHeightField.getInt(mc));
                        findClass("org.lwjgl.input.Mouse").getMethod("setGrabbed", boolean.class).invoke(null, false);
                    } else {
                        findClass("org.lwjgl.input.Mouse").getMethod("setGrabbed", boolean.class).invoke(null, true);
                    }
                }
                wasRightShiftDown = rightShiftDown;

                boolean homeDown = false;
                try {
                    Class<?> keyboardClass = findClass("org.lwjgl.input.Keyboard");
                    Method isKeyDown = keyboardClass.getMethod("isKeyDown", int.class);
                    homeDown = (boolean) isKeyDown.invoke(null, 327);
                } catch (Exception ignored) {}

                if (homeDown && !wasHomeDown) {
                    if (NovaCore.getInstance().getHudEditor() != null) {
                        NovaCore.getInstance().getHudEditor().toggle();
                    }
                }
                wasHomeDown = homeDown;
            } catch (Exception e) {
                System.err.println("[Nova Client] Tick error: " + e.getMessage());
            }
        }
    }

    private static class ReflectionAdapter implements PlatformAdapter {

        private Object getMc() {
            if (mcInstance == null) {
                try {
                    Method getMinecraft = mcClass.getMethod("getMinecraft");
                    mcInstance = getMinecraft.invoke(null);
                } catch (Exception e) { return null; }
            }
            return mcInstance;
        }

        private Object getPlayer() {
            try {
                Object mc = getMc();
                if (mc == null) return null;
                Field playerField = mcClass.getDeclaredField("thePlayer");
                return playerField.get(mc);
            } catch (Exception e) { return null; }
        }

        private Object getWorld() {
            try {
                Object mc = getMc();
                if (mc == null) return null;
                Field worldField = mcClass.getDeclaredField("theWorld");
                return worldField.get(mc);
            } catch (Exception e) { return null; }
        }

        private double getDoubleField(Object obj, String fieldName) {
            try {
                Field f = obj.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                return f.getDouble(obj);
            } catch (Exception e) { return 0; }
        }

        private float getFloatField(Object obj, String fieldName) {
            try {
                Field f = obj.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                return f.getFloat(obj);
            } catch (Exception e) { return 0; }
        }

        private boolean getBooleanField(Object obj, String fieldName) {
            try {
                Field f = obj.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                return f.getBoolean(obj);
            } catch (Exception e) { return false; }
        }

        private boolean invokeBoolean(Object obj, String methodName) {
            try {
                Method m = obj.getClass().getDeclaredMethod(methodName);
                m.setAccessible(true);
                return (boolean) m.invoke(obj);
            } catch (Exception e) { return false; }
        }

        @Override public String getPlayerName() {
            Object p = getPlayer();
            if (p == null) return "";
            try {
                Method m = p.getClass().getDeclaredMethod("getCommandSenderName");
                return (String) m.invoke(p);
            } catch (Exception e) { return ""; }
        }

        @Override public java.util.UUID getPlayerUUID() {
            Object p = getPlayer();
            if (p == null) return java.util.UUID.randomUUID();
            try {
                Method m = p.getClass().getDeclaredMethod("getUniqueID");
                return (java.util.UUID) m.invoke(p);
            } catch (Exception e) { return java.util.UUID.randomUUID(); }
        }

        @Override public double getPlayerX() { Object p = getPlayer(); return p != null ? getDoubleField(p, "posX") : 0; }
        @Override public double getPlayerY() { Object p = getPlayer(); return p != null ? getDoubleField(p, "posY") : 0; }
        @Override public double getPlayerZ() { Object p = getPlayer(); return p != null ? getDoubleField(p, "posZ") : 0; }
        @Override public float getPlayerYaw() { Object p = getPlayer(); return p != null ? getFloatField(p, "rotationYaw") : 0; }
        @Override public float getPlayerPitch() { Object p = getPlayer(); return p != null ? getFloatField(p, "rotationPitch") : 0; }
        @Override public boolean isPlayerSprinting() { Object p = getPlayer(); return p != null && invokeBoolean(p, "isSprinting"); }
        @Override public boolean isPlayerSneaking() { Object p = getPlayer(); return p != null && invokeBoolean(p, "isSneaking"); }
        @Override public boolean isPlayerOnGround() { Object p = getPlayer(); return p != null && getBooleanField(p, "onGround"); }
        @Override public int getPlayerHealth() { Object p = getPlayer(); return p != null ? (int) getFloatField(p, "getHealth") : 0; }
        @Override public int getPlayerMaxHealth() { return 20; }
        @Override public int getPlayerFoodLevel() { return 20; }
        @Override public float getPlayerSaturation() { return 5.0f; }
        @Override public String getWorldName() { Object w = getWorld(); return w != null ? "Overworld" : ""; }
        @Override public long getWorldTime() { Object w = getWorld(); return w != null ? getIntField(w, "worldTime") : 0; }
        @Override public int getWorldDay() { return 0; }
        @Override public boolean isRaining() { return false; }
        @Override public boolean isThundering() { return false; }
        @Override public float getAmbientLight() { return 0; }
        @Override public int getBrightness() { return 0; }

        private int getIntField(Object obj, String fieldName) {
            try {
                Field f = obj.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                return f.getInt(obj);
            } catch (Exception e) { return 0; }
        }

        @Override public boolean isOnMultiplayer() {
            try {
                Object mc = getMc();
                if (mc == null) return false;
                Field f = mcClass.getDeclaredField("currentServerData");
                return f.get(mc) != null;
            } catch (Exception e) { return false; }
        }
        @Override public String getServerAddress() { return isOnMultiplayer() ? "Multiplayer" : "Singleplayer"; }
        @Override public String getServerName() { return isOnMultiplayer() ? "Multiplayer" : "Singleplayer"; }
        @Override public int getPing() { return 0; }
        @Override public int getFPS() {
            try {
                Object mc = getMc();
                if (mc == null) return 0;
                Field f = mcClass.getDeclaredField("debugFPS");
                return f.getInt(mc);
            } catch (Exception e) { return 0; }
        }

        @Override public void drawRect(float left, float top, float right, float bottom, int color) {
            try {
                Class<?> guiClass = findClass("net.minecraft.client.gui.Gui");
                Method drawRect = guiClass.getDeclaredMethod("drawRect", int.class, int.class, int.class, int.class, int.class);
                drawRect.invoke(null, (int) left, (int) top, (int) right, (int) bottom, color);
            } catch (Exception ignored) {}
        }
        @Override public void drawGradientRect(float left, float top, float right, float bottom, int colorTop, int colorBottom) {
            try {
                Class<?> guiClass = findClass("net.minecraft.client.gui.Gui");
                Method drawGradientRect = guiClass.getDeclaredMethod("drawGradientRect", int.class, int.class, int.class, int.class, int.class, int.class);
                drawGradientRect.invoke(null, (int) left, (int) top, (int) right, (int) bottom, colorTop, colorBottom);
            } catch (Exception ignored) {}
        }
        @Override public void drawGradientRectH(float left, float top, float right, float bottom, int colorLeft, int colorRight) { drawGradientRect(left, top, right, bottom, colorLeft, colorRight); }
        @Override public void drawCircle(float cx, float cy, float radius, int color, int segments) { drawRect(cx - radius, cy - radius, cx + radius, cy + radius, color); }
        @Override public void drawString(String text, float x, float y, int color, boolean shadow) {
            try {
                Object mc = getMc();
                if (mc == null) return;
                Field frField = mcClass.getDeclaredField("fontRendererObj");
                Object fontRenderer = frField.get(mc);
                if (fontRenderer == null) return;
                Method drawStringWithShadow = fontRenderer.getClass().getMethod("drawStringWithShadow", String.class, float.class, float.class, int.class);
                drawStringWithShadow.invoke(fontRenderer, text, x, y, color);
            } catch (Exception ignored) {}
        }
        @Override public void drawString(String text, float x, float y, int color, float scale, boolean shadow) { drawString(text, x, y, color, shadow); }
        @Override public void drawCenteredString(String text, float x, float y, int color, boolean shadow) { int w = getStringWidth(text); drawString(text, x - w / 2.0f, y, color, shadow); }
        @Override public void drawTexture(String p, float x, float y, float w, float h) {}
        @Override public void drawTexture(String p, float x, float y, float u, float v, float uW, float vH, float w, float h, float tW, float tH) {}
        @Override public int getStringWidth(String text) {
            try {
                Object mc = getMc();
                if (mc == null) return text.length() * 6;
                Field f = mcClass.getDeclaredField("fontRendererObj");
                Object fr = f.get(mc);
                if (fr == null) return text.length() * 6;
                Method m = fr.getClass().getMethod("getStringWidth", String.class);
                return (int) m.invoke(fr, text);
            } catch (Exception e) { return text.length() * 6; }
        }
        @Override public int getStringHeight() {
            try {
                Object mc = getMc();
                if (mc == null) return 9;
                Field f = mcClass.getDeclaredField("fontRendererObj");
                Object fr = f.get(mc);
                if (fr == null) return 9;
                Field hField = fr.getClass().getField("FONT_HEIGHT");
                return hField.getInt(fr);
            } catch (Exception e) { return 9; }
        }

        @Override public void enableGL2D() {}
        @Override public void disableGL2D() {}
        @Override public void enableScissor(int x, int y, int w, int h) {}
        @Override public void disableScissor() {}
        @Override public void pushMatrix() {}
        @Override public void popMatrix() {}
        @Override public void translate(float x, float y, float z) {}
        @Override public void scale(float x, float y, float z) {}
        @Override public void rotate(float a, float x, float y, float z) {}
        @Override public boolean isKeyDown(int key) {
            try {
                Class<?> keyboardClass = findClass("org.lwjgl.input.Keyboard");
                Method isKeyDown = keyboardClass.getMethod("isKeyDown", int.class);
                return (boolean) isKeyDown.invoke(null, key);
            } catch (Exception e) { return false; }
        }
        @Override public boolean isMouseButtonDown(int button) {
            try {
                Class<?> mouseClass = findClass("org.lwjgl.input.Mouse");
                Method isButtonDown = mouseClass.getMethod("isButtonDown", int.class);
                return (boolean) isButtonDown.invoke(null, button);
            } catch (Exception e) { return false; }
        }
        @Override public int getMouseX() { return 0; }
        @Override public int getMouseY() { return 0; }
        @Override public int getScreenWidth() {
            try {
                Object mc = getMc();
                if (mc == null) return 854;
                Field f = mcClass.getDeclaredField("displayWidth");
                return f.getInt(mc);
            } catch (Exception e) { return 854; }
        }
        @Override public int getScreenHeight() {
            try {
                Object mc = getMc();
                if (mc == null) return 480;
                Field f = mcClass.getDeclaredField("displayHeight");
                return f.getInt(mc);
            } catch (Exception e) { return 480; }
        }
        @Override public void sendChatMessage(String message) {
            try {
                Object p = getPlayer();
                if (p == null) return;
                Method sendChat = p.getClass().getMethod("sendChatMessage", String.class);
                sendChat.invoke(p, message);
            } catch (Exception ignored) {}
        }
        @Override public void displayScreen(Object screen) {}
        @Override public long getSystemTime() { return System.currentTimeMillis(); }
        @Override public int getTicksPerSecond() { return getFPS(); }
        @Override public Object getPlayerEntity() { return getPlayer(); }
        @Override public Object getWorldObj() { return getWorld(); }
        @Override public Object getMinecraftInstance() { return getMc(); }
        @Override public void setPlatformAdapter(PlatformAdapter adapter) { Platform.setAdapter(adapter); }
    }
}
