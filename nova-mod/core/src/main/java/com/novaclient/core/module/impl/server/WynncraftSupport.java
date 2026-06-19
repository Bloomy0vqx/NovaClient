package com.novaclient.core.module.impl.server;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.BooleanSetting;
import com.novaclient.core.module.setting.ModeSetting;

public class WynncraftSupport extends Module {
    private final BooleanSetting questHelper = new BooleanSetting("Quest Helper", "Show quest objectives", true);
    private final BooleanSetting itemInfo = new BooleanSetting("Item Info", "Show detailed item info", true);
    private final ModeSetting classDisplay = new ModeSetting("Class", "Class display mode", "Top Right", "Below Name", "Disabled");
    private final BooleanSetting guildInfo = new BooleanSetting("Guild", "Show guild info", true);
    private final BooleanSetting territoryTracker = new BooleanSetting("Territory", "Track territory ownership", false);

    public WynncraftSupport() {
        super("Wynncraft Support", "Wynncraft-specific features", ModuleCategory.SERVER, 0);
        addSetting(questHelper);
        addSetting(itemInfo);
        addSetting(classDisplay);
        addSetting(guildInfo);
        addSetting(territoryTracker);
    }
}
