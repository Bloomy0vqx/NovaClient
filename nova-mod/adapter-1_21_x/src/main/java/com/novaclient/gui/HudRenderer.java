package com.novaclient.gui;

import com.novaclient.module.Module;
import com.novaclient.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class HudRenderer {

    private static final int COL_TEXT     = 0xFFe2e8f0;
    private static final int COL_ACCENT   = 0xFFa78bfa;
    private static final int COL_SHADOW   = 0xFF0a0a14;
    private static final int COL_BG       = 0x88080810;
    private static final int COL_ERROR    = 0xFFff6b6b;
    private static final int COL_WARN     = 0xFFffd93d;

    private final ModuleManager moduleManager;

    public HudRenderer(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    public void render(DrawContext ctx, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.player == null || mc.world == null) return;
        if (mc.options.hudHidden) return;

        TextRenderer tr = mc.textRenderer;
        ClientPlayerEntity player = mc.player;

        int x = 4;
        int y = 4;

        // FPS
        if (isEnabled("FPS")) {
            String fps = "§7FPS: §5" + MinecraftClient.getInstance().getCurrentFps();
            renderHudLine(ctx, tr, fps, x, y);
            y += 12;
        }

        // Coordinates
        if (isEnabled("Coordinates")) {
            String coords = String.format("§7XYZ: §5%.1f §7/ §5%.1f §7/ §5%.1f",
                player.getX(), player.getY(), player.getZ());
            renderHudLine(ctx, tr, coords, x, y);
            y += 12;
        }

        // Direction
        if (isEnabled("Direction")) {
            float yaw = player.getYaw() % 360;
            if (yaw < 0) yaw += 360;
            String dir = getDirection(yaw);
            String dirStr = String.format("§7Facing: §5%s §7(%.1f°)", dir, yaw);
            renderHudLine(ctx, tr, dirStr, x, y);
            y += 12;
        }

        // Ping
        if (isEnabled("Ping") && mc.getNetworkHandler() != null && mc.getCurrentServerEntry() != null) {
            int ping = mc.getNetworkHandler().getPlayerListEntry(player.getUuid()) != null
                ? mc.getNetworkHandler().getPlayerListEntry(player.getUuid()).getLatency() : 0;
            String col = ping < 80 ? "§a" : ping < 150 ? "§e" : "§c";
            renderHudLine(ctx, tr, "§7Ping: " + col + ping + "ms", x, y);
            y += 12;
        }

        // Armor
        if (isEnabled("Armor")) {
            int armor = player.getArmor();
            renderHudLine(ctx, tr, "§7Armor: §5" + armor, x, y);
            y += 12;
        }

        // Clock
        if (isEnabled("Clock")) {
            java.time.LocalTime time = java.time.LocalTime.now();
            renderHudLine(ctx, tr, String.format("§7%02d:%02d", time.getHour(), time.getMinute()), x, y);
            y += 12;
        }

        // ModList — right side
        if (isEnabled("ModList")) {
            renderModList(ctx, tr, mc);
        }
    }

    private void renderHudLine(DrawContext ctx, TextRenderer tr, String text, int x, int y) {
        int w = tr.getWidth(net.minecraft.text.Text.literal(text).getString());
        ctx.fill(x - 2, y - 1, x + w + 2, y + tr.fontHeight + 1, COL_BG);
        RenderHelper.drawText(ctx, tr, net.minecraft.text.Text.literal(text), x, y, COL_TEXT, true);
    }

    private void renderModList(DrawContext ctx, TextRenderer tr, MinecraftClient mc) {
        List<Module> active = new ArrayList<>();
        for (Module m : moduleManager.getModules()) {
            if (m.isEnabled() && m.getCategory() != ModuleManager.Category.HUD) {
                active.add(m);
            }
        }
        if (active.isEmpty()) return;

        int sw = ctx.getScaledWindowWidth();
        int y = 4;

        for (Module m : active) {
            String name = m.getName();
            int tw = tr.getWidth(name);
            int x = sw - tw - 6;
            ctx.fill(x - 2, y - 1, sw - 2, y + tr.fontHeight + 1, 0x88080810);
            ctx.fill(x - 3, y - 1, x - 2, y + tr.fontHeight + 1, 0xFF7c3aed);
            RenderHelper.drawText(ctx, tr, net.minecraft.text.Text.literal("§5" + name), x, y, COL_ACCENT, true);
            y += 12;
        }
    }

    private String getDirection(float yaw) {
        if (yaw >= 337.5 || yaw < 22.5) return "S";
        if (yaw < 67.5) return "SW";
        if (yaw < 112.5) return "W";
        if (yaw < 157.5) return "NW";
        if (yaw < 202.5) return "N";
        if (yaw < 247.5) return "NE";
        if (yaw < 292.5) return "E";
        return "SE";
    }

    private boolean isEnabled(String name) {
        for (Module m : moduleManager.getModules()) {
            if (m.getName().equals(name)) return m.isEnabled();
        }
        return false;
    }
}
