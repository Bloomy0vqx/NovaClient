package com.novaclient;

import com.novaclient.gui.ClickGui;
import com.novaclient.gui.HudRenderer;
import com.novaclient.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NovaClient implements ClientModInitializer {

    public static final String MOD_ID = "novaclient";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static NovaClient INSTANCE;

    private ClickGui clickGui;
    private HudRenderer hudRenderer;
    private ModuleManager moduleManager;
    private boolean guiOpen = false;
    private boolean hudEditorOpen = false;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        LOGGER.info("[Nova Client] Initializing...");

        moduleManager = new ModuleManager();
        moduleManager.init();

        clickGui = new ClickGui(moduleManager);
        hudRenderer = new HudRenderer(moduleManager);

        LOGGER.info("[Nova Client] Ready! Right Shift = Menu | Home = HUD Editor");
    }

    public void toggleGui() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;

        if (!guiOpen) {
            guiOpen = true;
            client.setScreen(clickGui.createScreen());
        } else {
            guiOpen = false;
            if (client.currentScreen instanceof com.novaclient.gui.NovaScreen) {
                client.setScreen(null);
            }
        }
    }

    public static NovaClient getInstance() { return INSTANCE; }
    public ClickGui getClickGui() { return clickGui; }
    public HudRenderer getHudRenderer() { return hudRenderer; }
    public ModuleManager getModuleManager() { return moduleManager; }
    public boolean isGuiOpen() { return guiOpen; }
    public void setGuiOpen(boolean v) { guiOpen = v; }
    public boolean isHudEditorOpen() { return hudEditorOpen; }
    public void setHudEditorOpen(boolean v) { hudEditorOpen = v; }
}
