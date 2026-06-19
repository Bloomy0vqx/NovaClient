package com.novaclient.core.module.impl.pvp;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.BooleanSetting;

public class PvPInfo extends Module {
    private final BooleanSetting showOpponentHealth = new BooleanSetting("Opponent Health", "Show opponent health", true);
    private final BooleanSetting showCombo = new BooleanSetting("Combo", "Show combo counter", true);
    private final BooleanSetting showPing = new BooleanSetting("Ping", "Show opponent ping", true);
    private final BooleanSetting showArmor = new BooleanSetting("Armor", "Show opponent armor", true);

    public PvPInfo() {
        super("PvP Info", "Displays useful PvP information", ModuleCategory.PVP, 0);
        addSetting(showOpponentHealth);
        addSetting(showCombo);
        addSetting(showPing);
        addSetting(showArmor);
    }
}
