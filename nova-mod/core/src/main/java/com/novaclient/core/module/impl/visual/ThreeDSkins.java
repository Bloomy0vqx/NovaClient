package com.novaclient.core.module.impl.visual;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.BooleanSetting;

public class ThreeDSkins extends Module {
    private final BooleanSetting showInInventory = new BooleanSetting("Inventory", "Show in inventory", true);
    private final BooleanSetting showOnScoreboard = new BooleanSetting("Scoreboard", "Show on scoreboard", true);

    public ThreeDSkins() {
        super("3D Skins", "Renders player skins in 3D", ModuleCategory.VISUAL, 0);
        addSetting(showInInventory);
        addSetting(showOnScoreboard);
    }
}
