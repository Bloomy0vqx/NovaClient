package com.novaclient.core.module.impl.pvp;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.BooleanSetting;

public class ToggleSneak extends Module {
    private final BooleanSetting autoDisable = new BooleanSetting("Auto Disable", "Disable when key released", true);
    private boolean isSneaking = false;

    public ToggleSneak() {
        super("Toggle Sneak", "Toggles sneaking instead of holding", ModuleCategory.PVP, 0);
        addSetting(autoDisable);
    }

    public boolean isSneaking() {
        return isSneaking;
    }

    public void setSneaking(boolean sneaking) {
        this.isSneaking = sneaking;
    }
}
