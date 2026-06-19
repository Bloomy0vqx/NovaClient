package com.novaclient.core.module.impl.pvp;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.BooleanSetting;
import com.novaclient.core.module.setting.ModeSetting;

public class ArmorStatus extends Module {
    private final ModeSetting displayMode = new ModeSetting("Display", "Armor display mode", "Horizontal", "Vertical", "Percentage");
    private final BooleanSetting showDurability = new BooleanSetting("Durability", "Show item durability", true);
    private final BooleanSetting showArmor = new BooleanSetting("Helmet", "Show helmet", true);
    private final BooleanSetting showChest = new BooleanSetting("Chestplate", "Show chestplate", true);
    private final BooleanSetting showLegs = new BooleanSetting("Leggings", "Show leggings", true);
    private final BooleanSetting showBoots = new BooleanSetting("Boots", "Show boots", true);

    public ArmorStatus() {
        super("Armor Status", "Shows your current armor status on screen", ModuleCategory.PVP, 0);
        addSetting(displayMode);
        addSetting(showDurability);
        addSetting(showArmor);
        addSetting(showChest);
        addSetting(showLegs);
        addSetting(showBoots);
    }
}
