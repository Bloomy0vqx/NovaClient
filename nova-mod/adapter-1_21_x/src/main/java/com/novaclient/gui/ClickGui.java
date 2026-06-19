package com.novaclient.gui;

import com.novaclient.NovaClient;
import com.novaclient.module.Module;
import com.novaclient.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ClickGui {

    private static final int PANEL_WIDTH = 140;
    private static final int PANEL_HEADER_H = 22;
    private static final int MODULE_H = 18;
    private static final int PADDING = 8;

    // Colors (ARGB)
    private static final int COL_BG         = 0xE0080810;
    private static final int COL_PANEL_BG   = 0xF0111118;
    private static final int COL_PANEL_HDR  = 0xFF1a1a28;
    private static final int COL_ACCENT     = 0xFF7c3aed;
    private static final int COL_ACCENT_DIM = 0x557c3aed;
    private static final int COL_MOD_ON     = 0xFF1e1a2e;
    private static final int COL_MOD_OFF    = 0x000000;
    private static final int COL_MOD_HOVER  = 0xFF1a1a28;
    private static final int COL_TEXT       = 0xFFe2e8f0;
    private static final int COL_TEXT_DIM   = 0xFF8888aa;
    private static final int COL_TEXT_ON    = 0xFFa78bfa;
    private static final int COL_BORDER     = 0xFF2a2a40;
    private static final int COL_SEP        = 0xFF1e1e30;

    private final ModuleManager moduleManager;
    private final List<Panel> panels = new ArrayList<>();

    public ClickGui(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
        buildPanels();
    }

    private void buildPanels() {
        ModuleManager.Category[] cats = ModuleManager.Category.values();
        int x = PADDING;
        for (ModuleManager.Category cat : cats) {
            List<Module> mods = moduleManager.getByCategory(cat);
            panels.add(new Panel(cat, mods, x, PADDING));
            x += PANEL_WIDTH + PADDING;
        }
    }

    public Screen createScreen() {
        return new NovaScreen(Text.literal("Nova Client"), this);
    }

    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int sw = ctx.getScaledWindowWidth();
        int sh = ctx.getScaledWindowHeight();

        // Full dim overlay
        ctx.fill(0, 0, sw, sh, COL_BG);

        // Header bar
        ctx.fill(0, 0, sw, 28, 0xFF0a0a14);
        ctx.fill(0, 27, sw, 28, COL_ACCENT);
        RenderHelper.drawText(ctx,
            net.minecraft.client.MinecraftClient.getInstance().textRenderer,
            Text.literal("§5§lNOVA §7§lCLIENT"),
            sw / 2, 8, COL_TEXT, true
        );

        // Render each panel
        for (Panel panel : panels) {
            panel.render(ctx, mouseX, mouseY);
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        for (Panel panel : panels) {
            panel.mouseClicked((int) mouseX, (int) mouseY, button);
        }
    }

    // ── Panel ────────────────────────────────────────────────

    static class Panel {
        final ModuleManager.Category category;
        final List<Module> modules;
        int x, y;
        boolean collapsed = false;
        int dragOffX, dragOffY;
        boolean dragging = false;

        Panel(ModuleManager.Category category, List<Module> modules, int x, int y) {
            this.category = category;
            this.modules = modules;
            this.x = x;
            this.y = y + 32; // below header bar
        }

        int height() {
            if (collapsed) return PANEL_HEADER_H;
            return PANEL_HEADER_H + modules.size() * MODULE_H + 2;
        }

        void render(DrawContext ctx, int mx, int my) {
            boolean hoverPanel = mx >= x && mx <= x + PANEL_WIDTH && my >= y && my <= y + height();

            // Panel shadow
            ctx.fill(x + 2, y + 2, x + PANEL_WIDTH + 2, y + height() + 2, 0x44000000);

            // Panel bg
            ctx.fill(x, y, x + PANEL_WIDTH, y + height(), COL_PANEL_BG);

            // Border
            drawBorder(ctx, x, y, PANEL_WIDTH, height(), COL_BORDER);

            // Header bg
            ctx.fill(x, y, x + PANEL_WIDTH, y + PANEL_HEADER_H, COL_PANEL_HDR);

            // Accent left stripe on header
            ctx.fill(x, y, x + 2, y + PANEL_HEADER_H, COL_ACCENT);

            // Bottom border of header
            ctx.fill(x, y + PANEL_HEADER_H - 1, x + PANEL_WIDTH, y + PANEL_HEADER_H, COL_ACCENT_DIM);

            // Category name
            var tr = net.minecraft.client.MinecraftClient.getInstance().textRenderer;
            String catName = category.name();
            catName = catName.charAt(0) + catName.substring(1).toLowerCase();
            RenderHelper.drawText(ctx, tr, Text.literal("§5" + catName), x + 7, y + 7, COL_TEXT, true);

            // Module count badge
            String badge = String.valueOf(modules.size());
            int badgeW = tr.getWidth(badge) + 6;
            int badgeX = x + PANEL_WIDTH - badgeW - 4;
            ctx.fill(badgeX, y + 5, badgeX + badgeW, y + PANEL_HEADER_H - 5, COL_ACCENT_DIM);
            RenderHelper.drawText(ctx, tr, Text.literal(badge), badgeX + 3, y + 7, COL_TEXT_ON, true);

            // Collapse arrow
            String arrow = collapsed ? "▶" : "▼";
            RenderHelper.drawText(ctx, tr, Text.literal("§8" + arrow), x + PANEL_WIDTH - 18, y + 7, COL_TEXT_DIM, true);

            if (!collapsed) {
                renderModules(ctx, mx, my, tr);
            }
        }

        void renderModules(DrawContext ctx, int mx, int my, net.minecraft.client.font.TextRenderer tr) {
            for (int i = 0; i < modules.size(); i++) {
                Module mod = modules.get(i);
                int my2 = y + PANEL_HEADER_H + i * MODULE_H;
                boolean hov = mx >= x && mx <= x + PANEL_WIDTH && my >= my2 && my < my2 + MODULE_H;

                // Row bg
                if (mod.isEnabled()) {
                    ctx.fill(x, my2, x + PANEL_WIDTH, my2 + MODULE_H, COL_MOD_ON);
                } else if (hov) {
                    ctx.fill(x, my2, x + PANEL_WIDTH, my2 + MODULE_H, COL_MOD_HOVER);
                }

                // Enabled indicator — left stripe
                if (mod.isEnabled()) {
                    ctx.fill(x, my2, x + 2, my2 + MODULE_H, COL_ACCENT);
                }

                // Module name
                int textColor = mod.isEnabled() ? COL_TEXT_ON : (hov ? COL_TEXT : COL_TEXT_DIM);
                RenderHelper.drawText(ctx, tr, Text.literal(mod.getName()), x + 7, my2 + 5, textColor, true);

                // Separator line
                if (i < modules.size() - 1) {
                    ctx.fill(x + 2, my2 + MODULE_H - 1, x + PANEL_WIDTH - 2, my2 + MODULE_H, COL_SEP);
                }
            }
        }

        void mouseClicked(int mx, int my, int button) {
            if (button != 0) return;

            // Header click = toggle collapse
            if (mx >= x && mx <= x + PANEL_WIDTH && my >= y && my < y + PANEL_HEADER_H) {
                collapsed = !collapsed;
                return;
            }

            if (collapsed) return;

            // Module click = toggle
            for (int i = 0; i < modules.size(); i++) {
                int my2 = y + PANEL_HEADER_H + i * MODULE_H;
                if (mx >= x && mx <= x + PANEL_WIDTH && my >= my2 && my < my2 + MODULE_H) {
                    modules.get(i).toggle();
                    return;
                }
            }
        }
    }

    private static void drawBorder(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x, y, x + w, y + 1, color);         // top
        ctx.fill(x, y + h - 1, x + w, y + h, color); // bottom
        ctx.fill(x, y, x + 1, y + h, color);          // left
        ctx.fill(x + w - 1, y, x + w, y + h, color);  // right
    }
}
