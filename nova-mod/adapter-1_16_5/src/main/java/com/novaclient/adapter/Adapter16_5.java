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
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Adapter16_5 {

    private static Object mcInstance;
    private static Class<?> mcClass;
    private static boolean wasRightShiftDown = false;
    private static boolean wasHomeDown = false;
    private static boolean wasMouseDown = false;

    static { initialize(); }

    public static void initialize() {
        System.out.println("[Nova Client] Forge 1.16.5 adapter initializing...");
        try {
            mcClass = findClass("net.minecraft.client.Minecraft");
            Method getInstance = findMethod(mcClass, new String[]{"getInstance", "func_71410_x"});
            mcInstance = getInstance.invoke(null);
            Platform.setAdapter(new ReflectionAdapter());
            NovaCore.getInstance().init(Platform.get());
            MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());
            System.out.println("[Nova Client] Forge 1.16.5 adapter initialized!");
        } catch (Exception e) {
            System.err.println("[Nova Client] Failed to initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Class<?> findClass(String n) { try { return Class.forName(n); } catch (Exception e) { return null; } }

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
                Object mc = mcInstance; if (mc == null) return;
                Object w = findMethod(mcClass, new String[]{"getWindow", "func_228018_at_"}).invoke(mc);
                float sw = (int) findMethod(w.getClass(), new String[]{"getScaledWidth", "func_228026_p_"}).invoke(w);
                float sh = (int) findMethod(w.getClass(), new String[]{"getScaledHeight", "func_228027_q_"}).invoke(w);
                float pt = event.getPartialTicks();
                EventBus.getInstance().fire(new Render2DEvent(pt));
                NovaCore.getInstance().getHudEditor().render(0, 0, pt);

                if (NovaCore.getInstance().isClickGuiOpen()) {
                    long h = (long) findMethod(w.getClass(), new String[]{"getHandle", "func_228032_x_"}).invoke(w);
                    double[] mx = new double[1];
                    double[] my = new double[1];
                    findClass("org.lwjgl.glfw.GLFW").getMethod("glfwGetCursorPos", long.class, double[].class, double[].class).invoke(null, h, mx, my);
                    
                    int mouseX = (int) mx[0];
                    int mouseY = (int) my[0];
                    
                    // Basic scaling
                    float scale = (int) findMethod(w.getClass(), new String[]{"getScaleFactor", "func_228028_r_"}).invoke(w);
                    mouseX = (int)(mouseX / scale);
                    mouseY = (int)(mouseY / scale);
                    
                    NovaCore.getInstance().getClickGui().render(mouseX, mouseY, pt);

                    boolean mouseDown = (int) findClass("org.lwjgl.glfw.GLFW").getMethod("glfwGetMouseButton", long.class, int.class).invoke(null, h, 0) == 1; // GLFW_PRESS
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
                Object mc = mcInstance; if (mc == null) return;
                Object w = findMethod(mcClass, new String[]{"getWindow", "func_228018_at_"}).invoke(mc);
                long h = (long) findMethod(w.getClass(), new String[]{"getHandle", "func_228032_x_"}).invoke(w);
                boolean rs = (int) findClass("org.lwjgl.glfw.GLFW").getMethod("glfwGetKey", long.class, int.class).invoke(null, h, 344) == 1;
                if (rs && !wasRightShiftDown) {
                    NovaCore.getInstance().setClickGuiOpen(!NovaCore.getInstance().isClickGuiOpen());
                    if (NovaCore.getInstance().isClickGuiOpen()) {
                        float sw = (int) findMethod(w.getClass(), new String[]{"getScaledWidth", "func_228026_p_"}).invoke(w);
                        float sh = (int) findMethod(w.getClass(), new String[]{"getScaledHeight", "func_228027_q_"}).invoke(w);
                        NovaCore.getInstance().getClickGui().init(sw, sh);
                        findClass("org.lwjgl.glfw.GLFW").getMethod("glfwSetInputMode", long.class, int.class, int.class).invoke(null, h, 0x00033001, 0x00034001); // GLFW_CURSOR_NORMAL
                    } else {
                        findClass("org.lwjgl.glfw.GLFW").getMethod("glfwSetInputMode", long.class, int.class, int.class).invoke(null, h, 0x00033001, 0x00034003); // GLFW_CURSOR_DISABLED
                    }
                }
                wasRightShiftDown = rs;
                boolean hd = (int) findClass("org.lwjgl.glfw.GLFW").getMethod("glfwGetKey", long.class, int.class).invoke(null, h, 268) == 1;
                if (hd && !wasHomeDown && NovaCore.getInstance().getHudEditor() != null) NovaCore.getInstance().getHudEditor().toggle();
                wasHomeDown = hd;
            } catch (Exception e) { System.err.println("[Nova Client] Tick error: " + e.getMessage()); }
        }

        @SubscribeEvent
        public void onAttackEntity(AttackEntityEvent event) {
            if (!NovaCore.getInstance().isInitialized()) return;
            try { EventBus.getInstance().fire(new AttackEvent(event.getEntity())); } catch (Exception ignored) {}
        }
    }

    private static class ReflectionAdapter implements PlatformAdapter {
        private Object getMc() { if (mcInstance == null) try { mcInstance = mcClass.getMethod("getInstance").invoke(null); } catch (Exception e) { return null; } return mcInstance; }
        private Object getPlayer() { try { return mcClass.getDeclaredField("player").get(getMc()); } catch (Exception e) { return null; } }
        private Object getWorld() { try { return mcClass.getDeclaredField("world").get(getMc()); } catch (Exception e) { return null; } }
        private double getDoubleField(Object o, String n) { try { Field f = o.getClass().getDeclaredField(n); f.setAccessible(true); return f.getDouble(o); } catch (Exception e) { return 0; } }
        private float getFloatField(Object o, String n) { try { Field f = o.getClass().getDeclaredField(n); f.setAccessible(true); return f.getFloat(o); } catch (Exception e) { return 0; } }
        private boolean getBooleanField(Object o, String n) { try { Field f = o.getClass().getDeclaredField(n); f.setAccessible(true); return f.getBoolean(o); } catch (Exception e) { return false; } }
        private boolean invokeBoolean(Object o, String m) { try { Method mt = o.getClass().getDeclaredMethod(m); mt.setAccessible(true); return (boolean) mt.invoke(o); } catch (Exception e) { return false; } }

        @Override public String getPlayerName() { Object p = getPlayer(); if (p == null) return ""; try { Object text = p.getClass().getDeclaredMethod("getName").invoke(p); Object str = text.getClass().getDeclaredMethod("getString").invoke(text); return str instanceof String s ? s : str.toString(); } catch (Exception e) { return ""; } }
        @Override public java.util.UUID getPlayerUUID() { Object p = getPlayer(); if (p == null) return java.util.UUID.randomUUID(); try { return (java.util.UUID) p.getClass().getDeclaredMethod("getUniqueID").invoke(p); } catch (Exception e) { return java.util.UUID.randomUUID(); } }
        @Override public double getPlayerX() { Object p = getPlayer(); return p != null ? getDoubleField(p, "getPosX") : 0; }
        @Override public double getPlayerY() { Object p = getPlayer(); return p != null ? getDoubleField(p, "getPosY") : 0; }
        @Override public double getPlayerZ() { Object p = getPlayer(); return p != null ? getDoubleField(p, "getPosZ") : 0; }
        @Override public float getPlayerYaw() { Object p = getPlayer(); return p != null ? getFloatField(p, "rotationYaw") : 0; }
        @Override public float getPlayerPitch() { Object p = getPlayer(); return p != null ? getFloatField(p, "rotationPitch") : 0; }
        @Override public boolean isPlayerSprinting() { Object p = getPlayer(); return p != null && invokeBoolean(p, "isSprinting"); }
        @Override public boolean isPlayerSneaking() { Object p = getPlayer(); return p != null && invokeBoolean(p, "isCrouching"); }
        @Override public boolean isPlayerOnGround() { Object p = getPlayer(); return p != null && invokeBoolean(p, "isOnGround"); }
        @Override public int getPlayerHealth() { Object p = getPlayer(); return p != null ? (int) getFloatField(p, "getHealth") : 0; }
        @Override public int getPlayerMaxHealth() { return 20; } @Override public int getPlayerFoodLevel() { return 20; } @Override public float getPlayerSaturation() { return 5.0f; }
        @Override public String getWorldName() { Object w = getWorld(); return w != null ? "Overworld" : ""; }
        @Override public long getWorldTime() { Object w = getWorld(); return w != null ? (long) getDoubleField(w, "getGameTime") : 0; }
        @Override public int getWorldDay() { return 0; } @Override public boolean isRaining() { return false; } @Override public boolean isThundering() { return false; } @Override public float getAmbientLight() { return 0; } @Override public int getBrightness() { return 0; }
        @Override public boolean isOnMultiplayer() { try { Object mc = getMc(); return mc != null && mcClass.getDeclaredField("getCurrentServerData").get(mc) != null; } catch (Exception e) { return false; } }
        @Override public String getServerAddress() { return isOnMultiplayer() ? "Multiplayer" : "Singleplayer"; }
        @Override public String getServerName() { return isOnMultiplayer() ? "Multiplayer" : "Singleplayer"; }
        @Override public int getPing() { return 0; }
        @Override public int getFPS() { try { Object mc = getMc(); return mc != null ? (int) mcClass.getDeclaredMethod("getFPS").invoke(mc) : 0; } catch (Exception e) { return 0; } }

        @Override public void drawRect(float l, float t, float r, float b, int c) { try { Class<?> g = findClass("net.minecraft.client.gui.AbstractGui"); g.getDeclaredMethod("fill", int.class, int.class, int.class, int.class, int.class).invoke(null, (int)l, (int)t, (int)r, (int)b, c); } catch (Exception ignored) {} }
        @Override public void drawGradientRect(float l, float t, float r, float b, int ct, int cb) { try { Class<?> g = findClass("net.minecraft.client.gui.AbstractGui"); g.getDeclaredMethod("fillGradient", int.class, int.class, int.class, int.class, int.class, int.class).invoke(null, (int)l, (int)t, (int)r, (int)b, ct, cb); } catch (Exception ignored) { drawRect(l, t, r, b, ct); } }
        @Override public void drawGradientRectH(float l, float t, float r, float b, int cl, int cr) { drawGradientRect(l, t, r, b, cl, cr); }
        @Override public void drawCircle(float cx, float cy, float rad, int c, int s) { drawRect(cx - rad, cy - rad, cx + rad, cy + rad, c); }
        @Override public void drawString(String text, float x, float y, int color, boolean shadow) { try { Object mc = getMc(); if (mc == null) return; Object fr = mcClass.getDeclaredField("fontRenderer").get(mc); Object ms = findClass("com.mojang.blaze3d.matrix.MatrixStack").getConstructor().newInstance(); fr.getClass().getMethod("drawStringWithShadow", findClass("com.mojang.blaze3d.matrix.MatrixStack"), CharSequence.class, float.class, float.class, int.class).invoke(fr, ms, text, x, y, color); } catch (Exception ignored) {} }
        @Override public void drawString(String text, float x, float y, int color, float scale, boolean shadow) { drawString(text, x, y, color, shadow); }
        @Override public void drawCenteredString(String text, float x, float y, int color, boolean shadow) { drawString(text, x - getStringWidth(text) / 2.0f, y, color, shadow); }
        @Override public void drawTexture(String p, float x, float y, float w, float h) {}
        @Override public void drawTexture(String p, float x, float y, float u, float v, float uW, float vH, float w, float h, float tW, float tH) {}
        @Override public int getStringWidth(String text) { try { Object mc = getMc(); if (mc == null) return text.length() * 6; Object fr = mcClass.getDeclaredField("fontRenderer").get(mc); return (int) fr.getClass().getMethod("getStringWidth", String.class).invoke(fr, text); } catch (Exception e) { return text.length() * 6; } }
        @Override public int getStringHeight() { try { Object mc = getMc(); if (mc == null) return 9; Object fr = mcClass.getDeclaredField("fontRenderer").get(mc); return fr.getClass().getField("FONT_HEIGHT").getInt(fr); } catch (Exception e) { return 9; } }
        @Override public void enableGL2D() {} @Override public void disableGL2D() {}
        @Override public void enableScissor(int x, int y, int w, int h) { try { Class<?> rs = findClass("com.mojang.blaze3d.systems.RenderSystem"); rs.getDeclaredMethod("enableScissor", int.class, int.class, int.class, int.class).invoke(null, x, y, w, h); } catch (Exception ignored) {} }
        @Override public void disableScissor() { try { findClass("com.mojang.blaze3d.systems.RenderSystem").getDeclaredMethod("disableScissor").invoke(null); } catch (Exception ignored) {} }
        @Override public void pushMatrix() {} @Override public void popMatrix() {} @Override public void translate(float x, float y, float z) {} @Override public void scale(float x, float y, float z) {} @Override public void rotate(float a, float x, float y, float z) {}
        @Override public boolean isKeyDown(int key) { try { Object mc = getMc(); if (mc == null) return false; Object w = mcClass.getMethod("getWindow").invoke(mc); long h = (long) w.getClass().getMethod("getHandle").invoke(w); return (int) findClass("net.minecraft.client.util.InputUtil").getMethod("isKeyDown", long.class, int.class).invoke(null, h, key) != 0; } catch (Exception e) { return false; } }
        @Override public boolean isMouseButtonDown(int button) { try { Object mc = getMc(); if (mc == null) return false; Object w = mcClass.getMethod("getWindow").invoke(mc); long h = (long) w.getClass().getMethod("getHandle").invoke(w); return (int) findClass("org.lwjgl.glfw.GLFW").getMethod("glfwGetMouseButton", long.class, int.class).invoke(null, h, button) == 1; } catch (Exception e) { return false; } }
        @Override public int getMouseX() { return 0; } @Override public int getMouseY() { return 0; }
        @Override public int getScreenWidth() { try { Object mc = getMc(); if (mc == null) return 854; Object w = mcClass.getMethod("getWindow").invoke(mc); return (int) w.getClass().getMethod("getScaledWidth").invoke(w); } catch (Exception e) { return 854; } }
        @Override public int getScreenHeight() { try { Object mc = getMc(); if (mc == null) return 480; Object w = mcClass.getMethod("getWindow").invoke(mc); return (int) w.getClass().getMethod("getScaledHeight").invoke(w); } catch (Exception e) { return 480; } }
        @Override public void sendChatMessage(String msg) { try { Object mc = getMc(); if (mc == null) return; Object nh = mcClass.getDeclaredMethod("getNetworkHandler").invoke(mc); if (nh != null) nh.getClass().getMethod("sendChatMessage", String.class).invoke(nh, msg); } catch (Exception ignored) {} }
        @Override public void displayScreen(Object screen) {}
        @Override public long getSystemTime() { return System.currentTimeMillis(); } @Override public int getTicksPerSecond() { return getFPS(); } @Override public Object getPlayerEntity() { return getPlayer(); } @Override public Object getWorldObj() { return getWorld(); } @Override public Object getMinecraftInstance() { return getMc(); }
        @Override public void setPlatformAdapter(PlatformAdapter adapter) { Platform.setAdapter(adapter); }
    }
}
