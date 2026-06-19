package com.novaclient.core.module.impl.server;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.BooleanSetting;
import com.novaclient.core.module.setting.ModeSetting;

public class HypixelMods extends Module {
    private final BooleanSetting autoTip = new BooleanSetting("Auto Tip", "Auto tip players", true);
    private final BooleanSetting antiAFK = new BooleanSetting("Anti AFK", "Prevents AFK kick", true);
    private final BooleanSetting gameTimer = new BooleanSetting("Game Timer", "Show game timer", true);
    private final ModeSetting lobbyScanner = new ModeSetting("Lobby Scanner", "Scan lobby players", "Off", "Bedwars", "Skywars", "Duels");
    private final BooleanSetting disconnectProtect = new BooleanSetting("Disconnect Protect", "Reconnect on disconnect", false);

    public HypixelMods() {
        super("Hypixel Mods", "Hypixel-specific features", ModuleCategory.SERVER, 0);
        addSetting(autoTip);
        addSetting(antiAFK);
        addSetting(gameTimer);
        addSetting(lobbyScanner);
        addSetting(disconnectProtect);
    }
}
