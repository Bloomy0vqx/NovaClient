package com.novaclient.core.ui;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.ModuleManager;
import com.novaclient.core.module.setting.*;
import com.novaclient.core.platform.Platform;
import com.novaclient.core.NovaCore;
import com.novaclient.core.ui.component.*;
import com.novaclient.core.ui.render.ColorUtil;
import com.novaclient.core.ui.render.AnimationUtil;
import java.util.*;

public class ClickGuiScreen {

    private float x, y, width, height;

    private final List<ModuleCategory> categoryOrder = new ArrayList<>();
    private String selectedCategory = "All";
    private String searchQuery = "";
    private boolean searching = false;

    private double scrollAmount = 0;
    private double targetScroll = 0;
    private boolean isDraggingScrollbar = false;

    private float animationProgress = 0;
    private Module expandedSettingsModule = null;

    private DarkEditBox searchBar;
    private final List<DarkToggleSwitch> toggleSwitches = new ArrayList<>();

    private static final int COLUMNS = 3;
    private static final int CARD_W = 130;
    private static final int CARD_H = 92;
    private static final int CARD_GAP = 12;
    private static final int CARD_PITCH = CARD_H + CARD_GAP;
    private static final int GRID_W = COLUMNS * CARD_W + (COLUMNS - 1) * CARD_GAP;
    private static final int PANEL_PAD = 12;
    private static final int HEADER_H = 30;
    private static final int SEARCH_W = 100;
    private static final int CLOSE_BTN_SIZE = 16;
    private static final int CAT_BTN_W = 35;
    private static final int CAT_BTN_GAP = 5;

    // White/gray/black color scheme
    private static final int BG_BORDER = 0xFF000000;
    private static final int BG_FILL = 0xFFFFFFFF;
    private static final int DIVIDER = 0xFFE0E0E0;
    private static final int CARD_BORDER = 0xFFCCCCCC;
    private static final int CARD_FILL = 0xFFF5F5F5;
    private static final int CARD_HOVER_BORDER = 0xFF000000;
    private static final int TEXT_PRIMARY = 0xFF000000;
    private static final int TEXT_GREY = 0xFF666666;
    private static final int TEXT_MUTED = 0xFF999999;
    private static final int SETTINGS_BG = 0xFFEEEEEE;
    private static final int SCROLLBAR_TRACK = 0xFFE8E8E8;
    private static final int SCROLLBAR_THUMB = 0xFF000000;
    private static final int CAT_BTN_BG = 0xFFF0F0F0;
    private static final int CAT_BTN_SELECTED = 0xFF000000;
    private static final int SEARCH_BG = 0xFFFFFFFF;
    private static final int SEARCH_BORDER = 0xFFCCCCCC;

    public ClickGuiScreen() {
        this.width = GRID_W + PANEL_PAD * 2;
        this.height = 300;

        categoryOrder.add(ModuleCategory.PVP);
        categoryOrder.add(ModuleCategory.HUD);
        categoryOrder.add(ModuleCategory.VISUAL);
        categoryOrder.add(ModuleCategory.UTILITY);
        categoryOrder.add(ModuleCategory.SERVER);
    }

    public void init(float screenWidth, float screenHeight) {
        this.x = (screenWidth - this.width) / 2;
        this.y = (screenHeight - this.height) / 2;
        scrollAmount = 0;
        targetScroll = 0;
        animationProgress = 0;
        expandedSettingsModule = null;

        searchBar = new DarkEditBox(
            (int)(x + width - PANEL_PAD - SEARCH_W - CLOSE_BTN_SIZE - 4),
            (int)(y + 7),
            SEARCH_W, 16, "Search..."
        );

        rebuildToggles();
    }

    private void rebuildToggles() {
        toggleSwitches.clear();
        List<Module> mods = getDisplayModules();
        for (Module mod : mods) {
            DarkToggleSwitch toggle = new DarkToggleSwitch(
                0, 0, 16, mod.isEnabled(), "", false,
                () -> {
                    mod.toggle();
                    syncToggleState(mod);
                }
            );
            toggleSwitches.add(toggle);
        }
    }

    private void syncToggleState(Module mod) {
        List<Module> mods = getDisplayModules();
        for (int i = 0; i < mods.size() && i < toggleSwitches.size(); i++) {
            if (mods.get(i) == mod) {
                toggleSwitches.get(i).setEnabled(mod.isEnabled());
                break;
            }
        }
    }

    public void render(float mouseX, float mouseY, float partialTicks) {
        animationProgress = Math.min(1, animationProgress + 0.08f);
        updateScroll();

        int alpha = (int)(255 * AnimationUtil.easeOutCubic(animationProgress));

        int bgAlpha = ColorUtil.withAlpha(BG_FILL, (int)(0.8f * 255 * AnimationUtil.easeOutCubic(animationProgress)));
        int borderAlpha = ColorUtil.withAlpha(BG_BORDER, alpha);

        drawPixelatedRoundedRect((int) x - 1, (int) y - 1, (int) width + 2, (int) height + 2, borderAlpha);
        drawPixelatedRoundedRect((int) x, (int) y, (int) width, (int) height, bgAlpha);

        int divY = (int) (y + HEADER_H);
        fill((int) (x + 5), divY, (int) (x + width - 5), divY + 1, ColorUtil.withAlpha(DIVIDER, alpha));

        renderCategoryTabs(mouseX, mouseY, alpha);
        renderSearchBar(mouseX, mouseY, alpha);
        renderCloseButton(mouseX, mouseY, alpha);
        renderModCards(mouseX, mouseY, alpha);
        renderScrollbar(alpha);

        if (expandedSettingsModule != null) {
            renderSettingsPanel(mouseX, mouseY, alpha);
        }
    }

    private void renderCategoryTabs(float mouseX, float mouseY, int alpha) {
        float catX = x + PANEL_PAD;
        float catY = y + 7;

        List<String> catNames = new ArrayList<>();
        catNames.add("All");
        for (ModuleCategory cat : categoryOrder) {
            catNames.add(cat.getIcon() + " " + cat.getDisplayName());
        }

        for (int i = 0; i < catNames.size(); i++) {
            String catName = catNames.get(i);
            boolean selected = selectedCategory.equals(catName);
            boolean hover = mouseX >= catX && mouseX <= catX + CAT_BTN_W && mouseY >= catY && mouseY <= catY + 16;

            int bg = selected ? CAT_BTN_SELECTED : (hover ? 0xFFE0E0E0 : ColorUtil.withAlpha(CAT_BTN_BG, alpha));
            int textCol = selected ? 0xFFFFFFFF : ColorUtil.withAlpha(TEXT_PRIMARY, alpha);

            fill((int) catX, (int) catY, (int) (catX + CAT_BTN_W), (int) (catY + 16), bg);
            drawText(catName, catX + (CAT_BTN_W - getStringWidth(catName)) / 2, catY + 4, textCol);

            catX += CAT_BTN_W + CAT_BTN_GAP;
        }
    }

    private void renderSearchBar(float mouseX, float mouseY, int alpha) {
        searchBar.setX((int)(x + width - PANEL_PAD - SEARCH_W - CLOSE_BTN_SIZE - 4));
        searchBar.setY((int)(y + 7));
        searchBar.render(mouseX, mouseY, 0);
    }

    private void renderCloseButton(float mouseX, float mouseY, int alpha) {
        float cbX = x + width - PANEL_PAD - CLOSE_BTN_SIZE;
        float cbY = y + 7;
        boolean hover = mouseX >= cbX && mouseX <= cbX + CLOSE_BTN_SIZE && mouseY >= cbY && mouseY <= cbY + CLOSE_BTN_SIZE;

        int bg = hover ? 0xFFDDDDDD : ColorUtil.withAlpha(0xFFF0F0F0, alpha);
        fill((int) cbX, (int) cbY, (int) (cbX + CLOSE_BTN_SIZE), (int) (cbY + CLOSE_BTN_SIZE), bg);
        drawText("x", cbX + 5, cbY + 3, ColorUtil.withAlpha(TEXT_PRIMARY, alpha));
    }

    private void renderModCards(float mouseX, float mouseY, int alpha) {
        List<Module> mods = getDisplayModules();
        int scrollAreaTop = (int) (y + HEADER_H + 4);
        int scrollAreaBottom = (int) (y + height - 4);
        int visibleHeight = scrollAreaBottom - scrollAreaTop;

        enableScissor((int) (x + 1), scrollAreaTop, (int) (width - 2), visibleHeight);

        int startX = (int) x + PANEL_PAD;
        int listTop = scrollAreaTop + 4;

        int renderCount = Math.min(mods.size(), toggleSwitches.size());

        for (DarkToggleSwitch toggle : toggleSwitches) {
            toggle.setVisible(false);
        }

        for (int i = 0; i < renderCount; i++) {
            Module mod = mods.get(i);
            DarkToggleSwitch toggle = toggleSwitches.get(i);
            int col = i % COLUMNS;
            int row = i / COLUMNS;

            int cardX = startX + col * (CARD_W + CARD_GAP);
            int cardY = (int) (listTop + row * CARD_PITCH - scrollAmount);

            if (cardY + CARD_H < scrollAreaTop - 10 || cardY > scrollAreaBottom + 10) {
                toggle.setVisible(false);
                continue;
            }

            toggle.setVisible(true);
            toggle.setX(cardX + CARD_W - 46);
            toggle.setY(cardY + CARD_H - 22);

            renderModCard(cardX, cardY, CARD_W, CARD_H, mod, mouseX, mouseY, alpha);
            toggle.render(mouseX, mouseY, 0);
        }

        disableScissor();
    }

    private void renderModCard(int cx, int cy, int cw, int ch, Module mod, float mouseX, float mouseY, int alpha) {
        boolean hovered = mouseX >= cx && mouseX <= cx + cw && mouseY >= cy && mouseY <= cy + ch;

        int borderColor = hovered ? ColorUtil.withAlpha(CARD_HOVER_BORDER, alpha) : ColorUtil.withAlpha(CARD_BORDER, alpha);
        int fillColor = ColorUtil.withAlpha(CARD_FILL, alpha);

        drawPixelatedRoundedRect(cx, cy, cw, ch, borderColor);
        drawPixelatedRoundedRect(cx + 1, cy + 1, cw - 2, ch - 2, fillColor);

        // Settings gear background
        int gearBgAlpha = ColorUtil.withAlpha(SETTINGS_BG, alpha);
        fill(cx + 4, cy + ch - 18, cx + 19, cy + ch - 3, gearBgAlpha);
        drawText("[S]", cx + 7, cy + ch - 15, ColorUtil.withAlpha(TEXT_PRIMARY, alpha));

        // Module name
        drawText(mod.getName(), cx + 6, cy + 6, ColorUtil.withAlpha(TEXT_PRIMARY, alpha));

        // Description
        String desc = mod.getDescription();
        if (desc != null && desc.length() > 18) {
            desc = desc.substring(0, 16) + "..";
        }
        if (desc != null) {
            drawText(desc, cx + 6, cy + 20, ColorUtil.withAlpha(TEXT_GREY, alpha));
        }

        // Keybind
        if (mod.getKeyBind() != 0) {
            drawText("Key: " + mod.getKeyBind(), cx + 6, cy + ch - 30, ColorUtil.withAlpha(TEXT_MUTED, alpha));
        }
    }

    private void renderScrollbar(int alpha) {
        List<Module> mods = getDisplayModules();
        int scrollAreaTop = (int) (y + HEADER_H + 4);
        int scrollAreaBottom = (int) (y + height - 4);
        int visibleHeight = scrollAreaBottom - scrollAreaTop;

        int rows = (int) Math.ceil((double) mods.size() / COLUMNS);
        int totalContentHeight = rows * CARD_PITCH;

        if (totalContentHeight > visibleHeight) {
            int scrollBarX = (int) (x + width - PANEL_PAD - 4);
            int scrollBarY1 = scrollAreaTop + 4;
            int scrollBarY2 = scrollAreaBottom - 4;
            int scrollBarHeight = scrollBarY2 - scrollBarY1;

            fill(scrollBarX, scrollBarY1, scrollBarX + 4, scrollBarY2, ColorUtil.withAlpha(SCROLLBAR_TRACK, alpha));

            int thumbHeight = Math.max(20, (int) ((double) visibleHeight / totalContentHeight * scrollBarHeight));
            int maxScroll = totalContentHeight - visibleHeight;
            int thumbOffset = (int) (scrollAmount / maxScroll * (scrollBarHeight - thumbHeight));

            fill(scrollBarX, scrollBarY1 + thumbOffset, scrollBarX + 4, scrollBarY1 + thumbOffset + thumbHeight, ColorUtil.withAlpha(SCROLLBAR_THUMB, alpha));
        }
    }

    private void renderSettingsPanel(float mouseX, float mouseY, int alpha) {
        if (expandedSettingsModule == null) return;

        int panelW = 200;
        int panelH = 250;
        int panelX = (int)(x + width + 8);
        int panelY = (int) y;

        // Clamp to screen
        if (Platform.isInitialized()) {
            if (panelX + panelW > Platform.get().getScreenWidth()) {
                panelX = (int)(x - panelW - 8);
            }
            if (panelY + panelH > Platform.get().getScreenHeight()) {
                panelY = (int)(Platform.get().getScreenHeight() - panelH);
            }
        }

        int bgAlpha = ColorUtil.withAlpha(BG_FILL, (int)(0.95f * 255));
        drawPixelatedRoundedRect(panelX - 1, panelY - 1, panelW + 2, panelH + 2, BG_BORDER);
        drawPixelatedRoundedRect(panelX, panelY, panelW, panelH, bgAlpha);

        // Title
        drawText(expandedSettingsModule.getName() + " Settings", panelX + 8, panelY + 8, TEXT_PRIMARY);

        // Divider
        fill(panelX + 5, panelY + 22, panelX + panelW - 5, panelY + 23, DIVIDER);

        // Settings
        List<Setting> settings = expandedSettingsModule.getSettings();
        int settingY = panelY + 30;

        for (Setting setting : settings) {
            if (setting.isHidden()) continue;

            if (setting instanceof BooleanSetting) {
                BooleanSetting boolSetting = (BooleanSetting) setting;
                drawText(setting.getName(), panelX + 8, settingY + 4, TEXT_GREY);
                int toggleX = panelX + panelW - 48;
                int toggleBg = boolSetting.isEnabled() ? 0xFF000000 : 0xFFCCCCCC;
                fill(toggleX, settingY, toggleX + 40, settingY + 16, toggleBg);
                int knobX = boolSetting.isEnabled() ? toggleX + 40 - 16 + 3 : toggleX + 3;
                int knobColor = boolSetting.isEnabled() ? 0xFFFFFFFF : 0xFF666666;
                fill(knobX, settingY + 3, knobX + 13, settingY + 13, knobColor);
                settingY += 22;
            } else if (setting instanceof NumberSetting) {
                NumberSetting numSetting = (NumberSetting) setting;
                drawText(setting.getName() + ": " + String.format("%.1f", numSetting.getValue()), panelX + 8, settingY + 4, TEXT_GREY);

                int sliderX = panelX + 8;
                int sliderW = panelW - 16;
                int sliderY = settingY + 14;

                fill(sliderX, sliderY, sliderX + sliderW, sliderY + 4, 0xFFDDDDDD);

                double normalized = numSetting.getPercentage();
                int thumbX = sliderX + (int)(normalized * (sliderW - 8));
                drawPixelatedRoundedRect(thumbX, sliderY - 2, 8, 8, 0xFF000000);
                settingY += 30;
            } else if (setting instanceof ModeSetting) {
                ModeSetting modeSetting = (ModeSetting) setting;
                drawText(setting.getName(), panelX + 8, settingY + 4, TEXT_GREY);
                drawText(modeSetting.getMode(), panelX + 8, settingY + 16, TEXT_PRIMARY);

                int arrowSize = 12;
                int arrowX = panelX + panelW - 30;

                fill(arrowX, settingY + 12, arrowX + arrowSize, settingY + 12 + arrowSize, 0xFFEEEEEE);
                drawText("<", arrowX + 3, settingY + 14, TEXT_PRIMARY);

                fill(arrowX + arrowSize + 4, settingY + 12, arrowX + arrowSize * 2 + 4, settingY + 12 + arrowSize, 0xFFEEEEEE);
                drawText(">", arrowX + arrowSize + 7, settingY + 14, TEXT_PRIMARY);
                settingY += 32;
            } else if (setting instanceof ColorSetting) {
                ColorSetting colorSetting = (ColorSetting) setting;
                drawText(setting.getName(), panelX + 8, settingY + 4, TEXT_GREY);
                int previewSize = 12;
                fill(panelX + panelW - 20, settingY + 2, panelX + panelW - 20 + previewSize, settingY + 2 + previewSize, colorSetting.toInt());
                settingY += 20;
            }
        }
    }

    private boolean isClickConsumed = false;

    private List<Module> getDisplayModules() {
        if (!searchQuery.isEmpty()) {
            return ModuleManager.getInstance().search(searchQuery);
        }
        if (selectedCategory.equals("All")) {
            return ModuleManager.getInstance().getModules();
        }
        // Parse category from display name with icon
        for (ModuleCategory cat : categoryOrder) {
            String catDisplayName = cat.getIcon() + " " + cat.getDisplayName();
            if (selectedCategory.equals(catDisplayName)) {
                return ModuleManager.getInstance().getModulesByCategory(cat);
            }
        }
        return ModuleManager.getInstance().getModules();
    }

    public void mouseClicked(float mouseX, float mouseY, int button) {
        if (button != 0) return;
        isClickConsumed = true;

        // Settings panel click handling
        if (expandedSettingsModule != null) {
            int panelW = 200;
            int panelH = 250;
            int panelX = (int)(x + width + 8);
            int panelY = (int) y;
            if (panelX + panelW > Platform.get().getScreenWidth()) {
                panelX = (int)(x - panelW - 8);
            }

            if (mouseX >= panelX && mouseX <= panelX + panelW && mouseY >= panelY && mouseY <= panelY + panelH) {
                return;
            } else {
                expandedSettingsModule = null;
                isClickConsumed = false;
                return;
            }
        }

        // Category tabs
        float catY = y + 7;
        float catX = x + PANEL_PAD;
        List<String> catNames = new ArrayList<>();
        catNames.add("All");
        for (ModuleCategory cat : categoryOrder) {
            catNames.add(cat.getIcon() + " " + cat.getDisplayName());
        }
        for (String catName : catNames) {
            if (mouseX >= catX && mouseX <= catX + CAT_BTN_W && mouseY >= catY && mouseY <= catY + 16) {
                selectedCategory = catName;
                scrollAmount = 0;
                targetScroll = 0;
                rebuildToggles();
                isClickConsumed = false;
                return;
            }
            catX += CAT_BTN_W + CAT_BTN_GAP;
        }

        // Close button
        float cbX = x + width - PANEL_PAD - CLOSE_BTN_SIZE;
        float cbY = y + 7;
        if (mouseX >= cbX && mouseX <= cbX + CLOSE_BTN_SIZE && mouseY >= cbY && mouseY <= cbY + CLOSE_BTN_SIZE) {
            close();
            isClickConsumed = false;
            return;
        }

        // Search bar
        if (searchBar != null) {
            searchBar.mouseClicked(mouseX, mouseY, button);
            if (searchBar.isFocused()) {
                isClickConsumed = false;
                return;
            }
        }

        // Mod cards
        int scrollAreaTop = (int) (y + HEADER_H + 4);
        int scrollAreaBottom = (int) (y + height - 4);
        int startX = (int) x + PANEL_PAD;
        int listTop = scrollAreaTop + 4;

        List<Module> mods = getDisplayModules();
        int renderCount = Math.min(mods.size(), toggleSwitches.size());

        for (int i = 0; i < renderCount; i++) {
            Module mod = mods.get(i);
            DarkToggleSwitch toggle = toggleSwitches.get(i);
            int col = i % COLUMNS;
            int row = i / COLUMNS;
            int cardX = startX + col * (CARD_W + CARD_GAP);
            int cardY = (int) (listTop + row * CARD_PITCH - scrollAmount);

            if (cardY + CARD_H < scrollAreaTop || cardY > scrollAreaBottom) continue;

            if (mouseX >= cardX && mouseX <= cardX + CARD_W && mouseY >= cardY && mouseY <= cardY + CARD_H) {
                // Toggle switch click
                float toggleX = cardX + CARD_W - 46;
                float toggleY = cardY + CARD_H - 22;
                if (mouseX >= toggleX && mouseX <= toggleX + 40 && mouseY >= toggleY && mouseY <= toggleY + 16) {
                    toggle.mouseClicked(mouseX, mouseY, button);
                    isClickConsumed = false;
                    return;
                }

                // Settings gear click
                float settingsX = cardX + 4;
                float settingsY = cardY + CARD_H - 18;
                if (mouseX >= settingsX && mouseX <= settingsX + 15 && mouseY >= settingsY && mouseY <= settingsY + 15) {
                    expandedSettingsModule = (expandedSettingsModule == mod) ? null : mod;
                    isClickConsumed = false;
                    return;
                }

                // Card body click - toggle module
                mod.toggle();
                syncToggleState(mod);
                isClickConsumed = false;
                return;
            }
        }

        // Scrollbar
        int scrollBarX = (int) (x + width - PANEL_PAD - 4);
        if (mouseX >= scrollBarX && mouseX <= scrollBarX + 4) {
            isDraggingScrollbar = true;
            isClickConsumed = false;
        }

        isClickConsumed = false;
    }

    public void mouseReleased(float mouseX, float mouseY, int state) {
        isDraggingScrollbar = false;
        for (DarkToggleSwitch toggle : toggleSwitches) {
            toggle.mouseReleased(mouseX, mouseY, state);
        }
    }

    public void mouseScrolled(float mouseX, float mouseY, float delta) {
        List<Module> mods = getDisplayModules();
        int scrollAreaTop = (int) (y + HEADER_H + 4);
        int scrollAreaBottom = (int) (y + height - 4);
        int visibleHeight = scrollAreaBottom - scrollAreaTop;
        int rows = (int) Math.ceil((double) mods.size() / COLUMNS);
        int totalContentHeight = rows * CARD_PITCH;

        if (totalContentHeight > visibleHeight) {
            targetScroll = (double) AnimationUtil.clamp((float)(targetScroll - delta * 30), 0, (float)(totalContentHeight - visibleHeight));
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (searchBar != null && searchBar.isFocused()) {
            searchBar.keyTyped(typedChar, keyCode);

            // Update search results
            String newQuery = searchBar.getValue();
            if (!newQuery.equals(searchQuery)) {
                searchQuery = newQuery;
                scrollAmount = 0;
                targetScroll = 0;
                rebuildToggles();
            }

            if (keyCode == 28 || keyCode == 156) {
                searchBar.setFocused(false);
            }
            return;
        }

        if (keyCode == 1) {
            if (expandedSettingsModule != null) {
                expandedSettingsModule = null;
            } else {
                close();
            }
        }
    }

    public boolean isOpen() {
        return NovaCore.getInstance().isClickGuiOpen();
    }

    public void close() {
        NovaCore.getInstance().setClickGuiOpen(false);
        scrollAmount = 0;
        targetScroll = 0;
        searching = false;
        searchQuery = "";
        selectedCategory = "All";
        expandedSettingsModule = null;
        animationProgress = 0;
        if (searchBar != null) {
            searchBar.clear();
            searchBar.setFocused(false);
        }
    }

    private void updateScroll() {
        if (Math.abs(scrollAmount - targetScroll) > 0.1) {
            scrollAmount = AnimationUtil.lerp((float) scrollAmount, (float) targetScroll, 0.2f);
        } else {
            scrollAmount = targetScroll;
        }
    }

    private void drawPixelatedRoundedRect(int x, int y, int w, int h, int color) {
        fill(x + 1, y, x + w - 1, y + h, color);
        fill(x, y + 1, x + 1, y + h - 1, color);
        fill(x + w - 1, y + 1, x + w, y + h - 1, color);
    }

    private void fill(int left, int top, int right, int bottom, int color) {
        if (Platform.isInitialized()) {
            Platform.get().drawRect(left, top, right, bottom, color);
        }
    }

    private void drawText(String text, float x, float y, int color) {
        if (Platform.isInitialized()) {
            Platform.get().drawString(text, x, y, color, false);
        }
    }

    private int getStringWidth(String text) {
        if (Platform.isInitialized()) {
            return Platform.get().getStringWidth(text);
        }
        return text.length() * 6;
    }

    private void enableScissor(int x, int y, int w, int h) {
        if (Platform.isInitialized()) {
            Platform.get().enableScissor(x, y, w, h);
        }
    }

    private void disableScissor() {
        if (Platform.isInitialized()) {
            Platform.get().disableScissor();
        }
    }
}
