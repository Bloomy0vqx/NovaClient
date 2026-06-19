package com.novaclient.core.module.impl.server;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.BooleanSetting;
import com.novaclient.core.module.setting.ModeSetting;

public class Quickplay extends Module {
    private final ModeSetting gameMode = new ModeSetting("Game", "Quickplay game mode", "Bedwars", "Skywars", "Duels", "UHC");
    private final BooleanSetting autoQueue = new BooleanSetting("Auto Queue", "Auto queue next game", false);
    private final BooleanSetting showStats = new BooleanSetting("Stats", "Show game stats", true);

    public Quickplay() {
        super("Quickplay", "Quick queue into Hypixel games", ModuleCategory.SERVER, 0);
        addSetting(gameMode);
        addSetting(autoQueue);
        addSetting(showStats);
    }
}
