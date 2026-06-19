package com.novaclient.core.module.impl.server;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.BooleanSetting;
import com.novaclient.core.module.setting.NumberSetting;
import com.novaclient.core.module.setting.ColorSetting;

public class BedwarsUtilities extends Module {
    private final BooleanSetting bedDefense = new BooleanSetting("Bed Defense", "Show bed defense tips", true);
    private final NumberSetting bedDefenseRadius = new NumberSetting("Defense Radius", "Defense detection range", 10.0, 5.0, 30.0, 1.0);
    private final BooleanSetting npcPosition = new BooleanSetting("NPC Position", "Show NPC positions", true);
    private final ColorSetting bedColor = new ColorSetting("Bed Color", "Bed overlay color", 255, 0, 0, 100);
    private final BooleanSetting itemCount = new BooleanSetting("Item Count", "Show bedwars item count", true);
    private final BooleanSetting timerDisplay = new BooleanSetting("Timer", "Show bedwars timer", true);
    private final BooleanSetting playerTracer = new BooleanSetting("Tracers", "Show player tracers", false);

    public BedwarsUtilities() {
        super("Bedwars Utilities", "Hypixel Bedwars utilities", ModuleCategory.SERVER, 0);
        addSetting(bedDefense);
        addSetting(bedDefenseRadius);
        addSetting(npcPosition);
        addSetting(bedColor);
        addSetting(itemCount);
        addSetting(timerDisplay);
        addSetting(playerTracer);
    }
}
