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
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Adapter12_2 {

    private static Object mcInstance;
    private static Class<?> mcClass;

    private static boolean wasRightShiftDown = false;
    private static boolean wasHomeDown = false;
    private static boolean wasMouseDown = false;

    static {
        initialize();
    }

    public static void initialize() {
        System.out.println("[Nova Client] Forge 1.12.2 adapter initializing...");
        try {
            mcClass = findClass("net.minecraft.client.Minecraft");
            Method getMinecraft = findMethod(mcClass, new String[]{"getMinecraft", "func_71410_x"});
            mcInstance = getMinecraft.invoke(null);
            Platform.setAdapter(new ReflectionAdapter());
            NovaCore.getInstance().init(Platform.get());
            MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());
            System.out.println("[Nova Client] Forge 1.12.2 adapter initialized!");
        } catch (Exception e) {
            System.err.println("[Nova Client] Failed to initialize Forge adapter: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Class<?> findClass(String name) {
        try { return Class.forName(name); } catch (ClassNotFoundException e) { return null; }
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
            if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
            try {
                Object mc = mcInstance;
                if (mc == null) return;
                Field dw = findField(mcClass, new String[]{"displayWidth", "field_71443_c"});
                Field dh = findField(mcClass, new String[]{"displayHeight", "field_71440_d"});
                float w = dw.getInt(mc);
                float h = dh.getInt(mc);
                float pt = event.getPartialTicks();
                EventBus.getInstance().fire(new Render2DEvent(pt));
                NovaCore.getInstance().getHudEditor().render(0, 0, pt);

                if (NovaCore.getInstance().isClickGuiOpen()) {
                    Class<?> mouse = findClass("org.lwjgl.input.Mouse");
                    int mouseX = (int) mouse.getMethod("getX").invoke(null) * (int)w / dw.getInt(mc);
                    int mouseY = (int)h - (int) mouse.getMethod("getY").invoke(null) * (int)h / dh.getInt(mc) - 1;
                    NovaCore.getInstance().getClickGui().render(mouseX, mouseY, pt);

                    boolean mouseDown = (boolean) mouse.getMethod("isButtonDown", int.class).invoke(null, 0);
                    if (mouseDown && !wasMouseDown) {
                        NovaCore.getInstance().getClickGui().mouseClicked(mouseX, mouseY, 0);
                    } else if (!mouseDown && wasMouseDown) {
                        NovaCore.getInstance().getClickGui().mouseReleased(mouseX, mouseY, 0);
                    }
                    wasMouseDown = mouseDown;
                }
            } catch (Exception e) { System.err.println("[Nova Client] Render error: " + e.getMessage()); }
        }

        @SubscribeEvent
        public void onClientTick(TickEvent.ClientTickEvent event) {
            if (!NovaCore.getInstance().isInitialized()) return;
            if (event.phase != TickEvent.Phase.END) return;
            try {
                Object mc = mcInstance;
                if (mc == null) return;
                boolean rs = false;
                try {
                    Class<?> kb = findClass("org.lwjgl.input.Keyboard");
                    rs = (boolean) kb.getMethod("isKeyDown", int.class).invoke(null, 348);
                } catch (Exception ignored) {}
                if (rs && !wasRightShiftDown && NovaCore.getInstance().getClickGui() != null) {
                    NovaCore.getInstance().setClickGuiOpen(!NovaCore.getInstance().isClickGuiOpen());
                    if (NovaCore.getInstance().isClickGuiOpen()) {
                        Field dw = findField(mcClass, new String[]{"displayWidth", "field_71443_c"});
                        Field dh = findField(mcClass, new String[]{"displayHeight", "field_71440_d"});
                        NovaCore.getInstance().getClickGui().init(dw.getInt(mc), dh.getInt(mc));
                        findClass("org.lwjgl.input.Mouse").getMethod("setGrabbed", boolean.class).invoke(null, false);
                    } else {
                        findClass("org.lwjgl.input.Mouse").getMethod("setGrabbed", boolean.class).invoke(null, true);
                    }
                }
                wasRightShiftDown = rs;
                boolean hd = false;
                try {
                    Class<?> kb = findClass("org.lwjgl.input.Keyboard");
                    hd = (boolean) kb.getMethod("isKeyDown", int.class).invoke(null, 347);
                } catch (Exception ignored) {}
                if (hd && !wasHomeDown && NovaCore.getInstance().getHudEditor() != null) {
                    NovaCore.getInstance().getHudEditor().toggle();
                }
                wasHomeDown = hd;
            } catch (Exception e) { System.err.println("[Nova Client] Tick error: " + e.getMessage()); }
        }

        @SubscribeEvent
        public void onAttackEntity(AttackEntityEvent event) {
            if (!NovaCore.getInstance().isInitialized()) return;
            try { EventBus.getInstance().fire(new com.novaclient.core.event.AttackEvent(event.getEntity())); } catch (Exception ignored) {}
        }
    }

    private static class ReflectionAdapter implements PlatformAdapter {
        private Object getMc() {
            if (mcInstance == null) try { mcInstance = mcClass.getMethod("getMinecraft").invoke(null); } catch (Exception e) { return null; }
            return mcInstance;
        }
        private Object getPlayer() { try { return mcClass.getDeclaredField("player").get(getMc()); } catch (Exception e) { return null; } }
        private Object getWorld() { try { return mcClass.getDeclaredField("world").get(getMc()); } catch (Exception e) { return null; } }
        private double getDoubleField(Object o, String n) { try { Field f = o.getClass().getDeclaredField(n); f.setAccessible(true); return f.getDouble(o); } catch (Exception e) { return 0; } }
        private float getFloatField(Object o, String n) { try { Field f = o.getClass().getDeclaredField(n); f.setAccessible(true); return f.getFloat(o); } catch (Exception e) { return 0; } }
        private boolean getBooleanField(Object o, String n) { try { Field f = o.getClass().getDeclaredField(n); f.setAccessible(true); return f.getBoolean(o); } catch (Exception e) { return false; } }
        private boolean invokeBoolean(Object o, String m) { try { Method mt = o.getClass().getDeclaredMethod(m); mt.setAccessible(true); return (boolean) mt.invoke(o); } catch (Exception e) { return false; } }
        private int getIntField(Object o, String n) { try { Field f = o.getClass().getDeclaredField(n); f.setAccessible(true); return f.getInt(o); } catch (Exception e) { return 0; } }

        @Override public String getPlayerName() { Object p = getPlayer(); if (p == null) return ""; try { return (String) p.getClass().getDeclaredMethod("getName").invoke(p); } catch (Exception e) { return ""; } }
        @Override public java.util.UUID getPlayerUUID() { Object p = getPlayer(); if (p == null) return java.util.UUID.randomUUID(); try { return (java.util.UUID) p.getClass().getDeclaredMethod("getUniqueID").invoke(p); } catch (Exception e) { return java.util.UUID.randomUUID(); } }
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
        @Override public boolean isOnMultiplayer() { try { Object mc = getMc(); return mc != null && mcClass.getDeclaredField("currentServerData").get(mc) != null; } catch (Exception e) { return false; } }
        @Override public String getServerAddress() { return isOnMultiplayer() ? "Multiplayer" : "Singleplayer"; }
        @Override public String getServerName() { return isOnMultiplayer() ? "Multiplayer" : "Singleplayer"; }
        @Override public int getPing() { return 0; }
        @Override public int getFPS() { try { Object mc = getMc(); return mc != null ? mcClass.getDeclaredField("debugFPS").getInt(mc) : 0; } catch (Exception e) { return 0; } }

        @Override public void drawRect(float l, float t, float r, float b, int c) { try { Class<?> g = findClass("net.minecraft.client.gui.Gui"); g.getDeclaredMethod("drawRect", int.class, int.class, int.class, int.class, int.class).invoke(null, (int)l, (int)t, (int)r, (int)b, c); } catch (Exception ignored) {} }
        @Override public void drawGradientRect(float l, float t, float r, float b, int ct, int cb) { try { Class<?> g = findClass("net.minecraft.client.gui.Gui"); g.getDeclaredMethod("drawGradientRect", int.class, int.class, int.class, int.class, int.class, int.class).invoke(null, (int)l, (int)t, (int)r, (int)b, ct, cb); } catch (Exception ignored) {} }
        @Override public void drawGradientRectH(float l, float t, float r, float b, int cl, int cr) { drawGradientRect(l, t, r, b, cl, cr); }
        @Override public void drawCircle(float cx, float cy, float rad, int c, int s) { drawRect(cx - rad, cy - rad, cx + rad, cy + rad, c); }
        @Override public void drawString(String text, float x, float y, int color, boolean shadow) { try { Object mc = getMc(); if (mc == null) return; Object fr = mcClass.getDeclaredField("fontRenderer").get(mc); fr.getClass().getMethod("drawStringWithShadow", String.class, float.class, float.class, int.class).invoke(fr, text, x, y, color); } catch (Exception ignored) {} }
        @Override public void drawString(String text, float x, float y, int color, float scale, boolean shadow) { drawString(text, x, y, color, shadow); }
        @Override public void drawCenteredString(String text, float x, float y, int color, boolean shadow) { int w = getStringWidth(text); drawString(text, x - w / 2.0f, y, color, shadow); }
        @Override public void drawTexture(String p, float x, float y, float w, float h) {}
        @Override public void drawTexture(String p, float x, float y, float u, float v, float uW, float vH, float w, float h, float tW, float tH) {}
        @Override public int getStringWidth(String text) { try { Object mc = getMc(); if (mc == null) return text.length() * 6; Object fr = mcClass.getDeclaredField("fontRenderer").get(mc); return (int) fr.getClass().getMethod("getStringWidth", String.class).invoke(fr, text); } catch (Exception e) { return text.length() * 6; } }
        @Override public int getStringHeight() { try { Object mc = getMc(); if (mc == null) return 9; Object fr = mcClass.getDeclaredField("fontRenderer").get(mc); return fr.getClass().getField("FONT_HEIGHT").getInt(fr); } catch (Exception e) { return 9; } }
        @Override public void enableGL2D() {} @Override public void disableGL2D() {} @Override public void enableScissor(int x, int y, int w, int h) {} @Override public void disableScissor() {} @Override public void pushMatrix() {} @Override public void popMatrix() {} @Override public void translate(float x, float y, float z) {} @Override public void scale(float x, float y, float z) {} @Override public void rotate(float a, float x, float y, float z) {}
        @Override public boolean isKeyDown(int key) { try { return (boolean) findClass("org.lwjgl.input.Keyboard").getMethod("isKeyDown", int.class).invoke(null, key); } catch (Exception e) { return false; } }
        @Override public boolean isMouseButtonDown(int button) { try { return (boolean) findClass("org.lwjgl.input.Mouse").getMethod("isButtonDown", int.class).invoke(null, button); } catch (Exception e) { return false; } }
        @Override public int getMouseX() { return 0; } @Override public int getMouseY() { return 0; }
        @Override public int getScreenWidth() { try { Object mc = getMc(); return mc != null ? mcClass.getDeclaredField("displayWidth").getInt(mc) : 854; } catch (Exception e) { return 854; } }
        @Override public int getScreenHeight() { try { Object mc = getMc(); return mc != null ? mcClass.getDeclaredField("displayHeight").getInt(mc) : 480; } catch (Exception e) { return 480; } }
        @Override public void sendChatMessage(String msg) { try { Object p = getPlayer(); if (p != null) p.getClass().getMethod("sendChatMessage", String.class).invoke(p, msg); } catch (Exception ignored) {} }
        @Override public void displayScreen(Object screen) {}
        @Override public long getSystemTime() { return System.currentTimeMillis(); } @Override public int getTicksPerSecond() { return getFPS(); } @Override public Object getPlayerEntity() { return getPlayer(); } @Override public Object getWorldObj() { return getWorld(); } @Override public Object getMinecraftInstance() { return getMc(); }
        @Override public void setPlatformAdapter(PlatformAdapter adapter) { Platform.setAdapter(adapter); }
    }
}
