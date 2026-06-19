package com.novaclient.core.module.impl.server;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.BooleanSetting;
import com.novaclient.core.module.setting.NumberSetting;

public class SkyblockAddons extends Module {
    private final BooleanSetting itemPriceChecker = new BooleanSetting("Item Prices", "Show item prices", true);
    private final BooleanSetting dungeonTimer = new BooleanSetting("Dungeon Timer", "Show dungeon timer", true);
    private final BooleanSetting highlightChests = new BooleanSetting("Chests", "Highlight dungeon chests", true);
    private final NumberSetting notificationVolume = new NumberSetting("Volume", "Notification volume", 0.8, 0.0, 1.0, 0.1);
    private final BooleanSetting autoFish = new BooleanSetting("Auto Fish", "Auto fish macro", false);
    private final BooleanSetting skillTracker = new BooleanSetting("Skills", "Track skill progression", true);
    private final BooleanSetting gemstoneESP = new BooleanSetting("Gemstones", "Highlight gemstones", false);

    public SkyblockAddons() {
        super("Skyblock Addons", "Hypixel Skyblock features", ModuleCategory.SERVER, 0);
        addSetting(itemPriceChecker);
        addSetting(dungeonTimer);
        addSetting(highlightChests);
        addSetting(notificationVolume);
        addSetting(autoFish);
        addSetting(skillTracker);
        addSetting(gemstoneESP);
    }
}
