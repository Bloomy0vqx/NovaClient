package com.novaclient.core;

import com.novaclient.core.event.EventBus;
import com.novaclient.core.module.ModuleManager;
import com.novaclient.core.config.ConfigManager;
import com.novaclient.core.account.AccountManager;
import com.novaclient.core.cosmetics.CosmeticEngine;
import com.novaclient.core.ui.hud.HudManager;
import com.novaclient.core.nametag.NametagRenderer;
import com.novaclient.core.platform.Platform;
import com.novaclient.core.platform.PlatformAdapter;
import com.novaclient.core.ui.ClickGuiScreen;
import com.novaclient.core.ui.NovaMenuScreen;
import com.novaclient.core.ui.HudEditor;

public class NovaCore {
    private static final NovaCore INSTANCE = new NovaCore();
    private static final String VERSION = "1.0.0";
    private static final String NAME = "Nova Client";

    private boolean initialized = false;
    private ClickGuiScreen clickGui;
    private NovaMenuScreen menuScreen;
    private HudEditor hudEditor;
    private boolean clickGuiOpen = false;

    public static NovaCore getInstance() {
        return INSTANCE;
    }

    public void init(PlatformAdapter adapter) {
        if (initialized) return;

        System.out.println("[Nova Client] Initializing " + NAME + " v" + VERSION);

        Platform.setAdapter(adapter);
        System.out.println("[Nova Client] Platform adapter registered: " + adapter.getClass().getSimpleName());

        EventBus.getInstance();
        System.out.println("[Nova Client] Event bus initialized");

        ConfigManager.getInstance().init();
        System.out.println("[Nova Client] Config manager initialized");

        ModuleManager.getInstance().init();
        System.out.println("[Nova Client] Module manager initialized - " + ModuleManager.getInstance().getModules().size() + " modules loaded");

        ConfigManager.getInstance().load();
        System.out.println("[Nova Client] Configuration loaded");

        HudManager.getInstance().init();
        System.out.println("[Nova Client] HUD manager initialized");

        AccountManager.getInstance().init();
        System.out.println("[Nova Client] Account manager initialized");

        CosmeticEngine.getInstance().init();
        System.out.println("[Nova Client] Cosmetic engine initialized");

        NametagRenderer.getInstance().init();
        System.out.println("[Nova Client] Nametag renderer initialized");

        float w = adapter.getScreenWidth();
        float h = adapter.getScreenHeight();
        clickGui = new ClickGuiScreen();
        clickGui.init(w, h);
        menuScreen = new NovaMenuScreen();
        menuScreen.init(w, h);
        hudEditor = new HudEditor();
        System.out.println("[Nova Client] UI screens initialized");

        initialized = true;
        clickGuiOpen = false;
        System.out.println("[Nova Client] " + NAME + " v" + VERSION + " initialized successfully!");
        System.out.println("[Nova Client] Press Right Shift to open Mod Menu");
        System.out.println("[Nova Client] Press Home to open HUD Editor");
    }

    public void init() {
        System.out.println("[Nova Client] WARNING: init() called without PlatformAdapter. Core-only mode.");
    }

    public void shutdown() {
        System.out.println("[Nova Client] Shutting down...");
        ConfigManager.getInstance().save();
        AccountManager.getInstance().save();
        System.out.println("[Nova Client] Shutdown complete.");
    }

    public void toggleClickGui() {
        if (clickGui == null) return;
        clickGuiOpen = !clickGuiOpen;
        if (!clickGuiOpen) {
            clickGui.close();
        }
    }

    public boolean isClickGuiOpen() {
        return clickGuiOpen;
    }

    public void setClickGuiOpen(boolean open) {
        this.clickGuiOpen = open;
    }

    public void onResize(int width, int height) {
        if (clickGui != null) clickGui.init(width, height);
        if (menuScreen != null) menuScreen.init(width, height);
    }

    public ClickGuiScreen getClickGui() { return clickGui; }
    public NovaMenuScreen getMenuScreen() { return menuScreen; }
    public HudEditor getHudEditor() { return hudEditor; }
    public String getVersion() { return VERSION; }
    public String getName() { return NAME; }
    public boolean isInitialized() { return initialized; }
}
